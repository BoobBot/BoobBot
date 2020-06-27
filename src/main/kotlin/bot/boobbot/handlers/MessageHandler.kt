package bot.boobbot.handlers

import bot.boobbot.BoobBot
import bot.boobbot.entities.db.Guild
import bot.boobbot.entities.internals.Config
import bot.boobbot.utils.Formats
import bot.boobbot.utils.Utils
import bot.boobbot.utils.json
import de.mxro.metrics.jre.Metrics
import net.dv8tion.jda.api.Permission
import net.dv8tion.jda.api.entities.Member
import net.dv8tion.jda.api.entities.TextChannel
import net.dv8tion.jda.api.events.message.MessageReceivedEvent
import net.dv8tion.jda.api.hooks.ListenerAdapter
import java.util.concurrent.Executors
import java.util.concurrent.atomic.AtomicInteger

class MessageHandler : ListenerAdapter() {
    private val threadCounter = AtomicInteger()
    private val commandExecutorPool = Executors.newCachedThreadPool {
        Thread(it, "Command-Executor-${threadCounter.getAndIncrement()}")
    }

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
        val guildData: Guild by lazy { BoobBot.database.getGuild(event.guild.id) }

        if (event.channelType.isGuild) {
            if (event.message.mentionsEveryone()) {
                BoobBot.metrics.record(Metrics.happened("atEveryoneSeen"))
            }

            if (!event.textChannel.canTalk()) {
                return
            }

            if (guildData.ignoredChannels.contains(event.channel.id) && !event.member!!.hasPermission(Permission.MESSAGE_MANAGE)) {
                return
            }

            if (guildData.modMute.contains(event.author.id)) {
                return event.message.delete().reason("mod mute").queue()
            }
        }

        val messageContent = event.message.contentRaw
        val standardTrigger =
            if (event.isFromGuild) guildData.prefix ?: BoobBot.defaultPrefix else BoobBot.defaultPrefix
        val acceptablePrefixes = mutableListOf(
            standardTrigger,
            "<@${event.jda.selfUser.id}> ",
            "<@!${event.jda.selfUser.id}> "
        )

        val trigger = acceptablePrefixes.firstOrNull { messageContent.toLowerCase().startsWith(it) }
            ?: return

        val args = messageContent.substring(trigger.length).split(" +".toRegex()).toMutableList()
        val commandString = args.removeAt(0)

        val command = BoobBot.commands.findCommand(commandString)

        if (command == null) {
            if (!event.channelType.isGuild) {
                return
            }

            val customCommand = guildData.customCommands.firstOrNull { it.name == commandString }
                ?: return

            return event.channel.sendMessage(customCommand.content).queue()
        }

        if (
            event.isFromGuild &&
            guildData.disabled.contains(command.name) ||
            guildData.channelDisabled.any { it.name == command.name && it.channelId == event.channel.id }
        ) {
            return
        }

        if (!command.properties.enabled) {
            return
        }

        if (command.properties.developerOnly && !Config.OWNERS.contains(event.author.idLong)) {
            return
        }

        if (command.properties.guildOnly && !event.channelType.isGuild) {
            return event.channel.sendMessage("No, whore you can only use this in a guild").queue()
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

        if (
            event.channelType.isGuild && !event.guild.selfMember.hasPermission(
                event.textChannel,
                Permission.MESSAGE_EMBED_LINKS
            )
        ) {
            return event.channel.sendMessage("I do not have permission to use embeds, da fuck?").queue()
        }

        if (command.properties.donorOnly && !Utils.checkDonor(event.message)) {
            return event.channel.sendMessage(
                Formats.error(
                    " Sorry this command is only available to our Patrons.\n<:p_:475801484282429450> "
                            + "Stop being a cheap fuck and join today!\nhttps://www.patreon.com/OfficialBoobBot"
                )
            ).queue()
        }

        if (event.isFromGuild && command.properties.userPermissions.isNotEmpty()) {
            val missing = checkMissingPermissions(event.member!!, event.textChannel, command.properties.userPermissions)

            if (missing.isNotEmpty()) {
                val fmt = missing.joinToString("`\n `", prefix = "`", postfix = "`", transform = Permission::getName)
                return event.channel.sendMessage("You need these permissions, whore:\n$fmt").queue()
            }
        }

        if (event.isFromGuild && command.properties.botPermissions.isNotEmpty()) {
            val missing =
                checkMissingPermissions(event.guild.selfMember, event.textChannel, command.properties.botPermissions)

            if (missing.isNotEmpty()) {
                val fmt = missing.joinToString("`\n `", prefix = "`", postfix = "`", transform = Permission::getName)
                return event.channel.sendMessage("I need these permissions, whore:\n$fmt").queue()
            }
        }

        val userData = BoobBot.database.getUser(event.author.id)

        if (event.channelType.isGuild && userData.anonymity
            && event.guild.selfMember.hasPermission(event.textChannel, Permission.MESSAGE_MANAGE)
        ) {
            event.message.delete().queue(null, {})
        }

        try {
            Utils.logCommand(event.message)
            BoobBot.metrics.record(Metrics.happened("command"))
            BoobBot.metrics.record(Metrics.happened(command.name))

            if (command.properties.nsfw) {
                userData.nsfwCommandsUsed++
            } else {
                userData.commandsUsed++
            }

            userData.save()
            command.execute(trigger, event.message, args)
        } catch (e: Exception) {
            BoobBot.log.error("Command `${command.name}` encountered an error during execution", e)
            event.message.addReaction("\uD83D\uDEAB").queue()
        }
    }

    private fun checkMissingPermissions(
        target: Member,
        channel: TextChannel,
        permissions: Array<Permission>
    ): List<Permission> {
        return permissions.filter { !target.hasPermission(channel, it) }
    }
}
