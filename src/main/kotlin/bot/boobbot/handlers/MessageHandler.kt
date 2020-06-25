package bot.boobbot.handlers

import bot.boobbot.BoobBot
import bot.boobbot.utils.Formats
import bot.boobbot.utils.Utils
import bot.boobbot.utils.json
import bot.boobbot.entities.internals.Config
import de.mxro.metrics.jre.Metrics
import net.dv8tion.jda.api.Permission
import net.dv8tion.jda.api.events.message.MessageReceivedEvent
import net.dv8tion.jda.api.hooks.ListenerAdapter
import java.util.concurrent.Executors
import java.util.concurrent.atomic.AtomicInteger

class MessageHandler : ListenerAdapter() {
    private val threadCounter = AtomicInteger()
    private val commandExecutorPool = Executors.newCachedThreadPool { Thread("Command-Executor-${threadCounter.getAndIncrement()}") }

    override fun onMessageReceived(event: MessageReceivedEvent) {
        BoobBot.metrics.record(Metrics.happened("MessageReceived"))

        if (event.author.isBot) { // Basic check to reduce thread usage
            return
        }

        commandExecutorPool.execute {
            processMessageEvent(event)
        }
    }

    private fun processMessageEvent(event: MessageReceivedEvent) {
        if (event.channelType.isGuild) {
            if (event.message.mentionsEveryone()) {
                BoobBot.metrics.record(Metrics.happened("atEveryoneSeen"))
            }

            if (!event.textChannel.canTalk()) {
                return
            }

            val g = BoobBot.database.getGuild(event.guild.id)

            if (g.ignoredChannels.contains(event.channel.id) && !event.member!!.hasPermission(Permission.MESSAGE_MANAGE)) {
                return
            }

            if (g.modMute.contains(event.author.id)) {
                return event.message.delete().reason("mod mute").queue()
            }
        }

        val messageContent = event.message.contentRaw
        val acceptablePrefixes = mutableListOf(
            BoobBot.defaultPrefix,
            "<@${event.jda.selfUser.id}> ",
            "<@!${event.jda.selfUser.id}> "
        )

        if (event.channelType.isGuild) {
            val custom = BoobBot.database.getPrefix(event.guild.id)

            if (custom != null) {
                acceptablePrefixes.add(custom)
            }
        }

        val trigger = acceptablePrefixes.firstOrNull { messageContent.toLowerCase().startsWith(it) }
            ?: return

        val args = messageContent.substring(trigger.length).split(" +".toRegex()).toMutableList()
        val commandString = args.removeAt(0)

        val command = BoobBot.commands.findCommand(commandString)

        if (command == null) {
            if (!event.channelType.isGuild) {
                return
            }

            val customCommand = BoobBot.database.findCustomCommand(event.guild.id, commandString)
                ?: return

            return event.channel.sendMessage(customCommand).queue()
        }

        if (event.isFromGuild) {
            val disabledCommands = BoobBot.database.getDisabledCommands(event.guild.id)
            val disabledForChannel = BoobBot.database.getDisabledForChannel(event.guild.id, event.channel.id)

            if (disabledCommands.contains(command.name) || disabledForChannel.contains(command.name)) {
                return
            }
        }

        if (!command.properties.enabled) {
            return
        }

        if (command.properties.developerOnly && !Config.OWNERS.contains(event.author.idLong)) {
            return
        }

        if (command.properties.guildOnly && !event.channelType.isGuild) {
            event.channel.sendMessage("No, whore you can only use this in a guild").queue()
            return
        }

        if (command.properties.nsfw && event.channelType.isGuild && !event.textChannel.isNSFW) {
            BoobBot.requestUtil.get("https://nekos.life/api/v2/img/meow").queue {
                val j = it?.json()
                    ?: return@queue event.channel.sendMessage("This channel isn't NSFW, whore.").queue()

                event.channel.sendMessage(
                    "This isn't an NSFW channel whore, so have some SFW pussy.\n" +
                            "Confused? Try `bbhuh` or join the support server https://discord.gg/boobbot\n" +
                            j.getString("url")
                ).queue()
            }
            return
        }

        if (event.channelType.isGuild && !event.guild.selfMember.hasPermission(
                event.textChannel,
                Permission.MESSAGE_EMBED_LINKS
            )
        ) {
            event.channel.sendMessage("I do not have permission to use embeds, da fuck?").queue()
            return
        }

        if (command.properties.donorOnly && !Utils.checkDonor(event.message)) {
            event.channel.sendMessage(
                Formats.error(
                    " Sorry this command is only available to our Patrons.\n<:p_:475801484282429450> "
                            + "Stop being a cheap fuck and join today!\nhttps://www.patreon.com/OfficialBoobBot"
                )
            ).queue()
            return
        }

        if (event.isFromGuild && command.properties.userPermissions.isNotEmpty()) {
            val missing = command.properties.userPermissions.filter { !event.member!!.hasPermission(event.textChannel, it) }

            if (missing.isNotEmpty()) {
                val fmt = missing.joinToString("`\n `", prefix = "`", postfix = "`", transform = Permission::getName)
                return event.channel.sendMessage("You need these permissions, whore:\n$fmt").queue()
            }
        }

        if (event.isFromGuild && command.properties.botPermissions.isNotEmpty()) {
            val missing = command.properties.botPermissions.filter { !event.guild.selfMember.hasPermission(event.textChannel, it) }

            if (missing.isNotEmpty()) {
                val fmt = missing.joinToString("`\n `", prefix = "`", postfix = "`", transform = Permission::getName)
                return event.channel.sendMessage("I need these permissions, whore:\n$fmt").queue()
            }
        }

        if (event.channelType.isGuild
            && event.guild.selfMember.hasPermission(event.textChannel, Permission.MESSAGE_MANAGE)
            && BoobBot.database.getUserAnonymity(event.author.id)
        ) {
            event.message.delete().queue(null, {})
        }

        try {
            if (BoobBot.logCom) {
                Utils.logCommand(event.message)
            }
            BoobBot.metrics.record(Metrics.happened("command"))
            BoobBot.metrics.record(Metrics.happened(command.name))
            val user = BoobBot.database.getUser(event.author.id)
            if (command.properties.nsfw) user.nsfwCommandsUsed += 1 else user.commandsUsed += 1
            BoobBot.database.setUser(user)
            command.execute(trigger, event.message, args)
        } catch (e: Exception) {
            BoobBot.log.error("Command `${command.name}` encountered an error during execution", e)
            event.message.addReaction("\uD83D\uDEAB").queue()
        }
    }
}
