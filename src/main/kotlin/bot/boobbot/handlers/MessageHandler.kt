package bot.boobbot.handlers

import bot.boobbot.BoobBot
import bot.boobbot.entities.db.Guild
import bot.boobbot.entities.db.User
import bot.boobbot.entities.framework.BootyDropper
import bot.boobbot.entities.framework.Context
import bot.boobbot.entities.internals.Config
import bot.boobbot.utils.Formats
import bot.boobbot.utils.Utils
import bot.boobbot.utils.Utils.calculateLewdLevel
import bot.boobbot.utils.Utils.checkMissingPermissions
import bot.boobbot.utils.Utils.random
import bot.boobbot.utils.json
import de.mxro.metrics.jre.Metrics
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import net.dv8tion.jda.api.Permission
import net.dv8tion.jda.api.entities.ChannelType
import net.dv8tion.jda.api.entities.emoji.Emoji
import net.dv8tion.jda.api.events.message.MessageReceivedEvent
import net.dv8tion.jda.api.hooks.ListenerAdapter
import org.jetbrains.kotlin.utils.addToStdlib.ifTrue
import java.util.concurrent.Executors
import java.util.concurrent.atomic.AtomicInteger
import kotlin.math.floor
import kotlin.math.min
import kotlin.math.sqrt

class MessageHandler : ListenerAdapter() {
    private val threadCounter = AtomicInteger()
    private val commandExecutorPool = Executors.newCachedThreadPool {
        Thread(it, "Command-Executor-${threadCounter.getAndIncrement()}")
    }

    override fun onMessageReceived(event: MessageReceivedEvent) {
        BoobBot.metrics.record(Metrics.happened("MessageReceived"))

        if (event.author.isBot) { // Basic check to reduce usage
            return
        }

        commandExecutorPool.execute {
            processMessageEvent(event)
        }

        processUser(event)
    }

    @Suppress("EXPERIMENTAL_API_USAGE")
    private fun processMessageEvent(event: MessageReceivedEvent) {
        val guild: Guild by lazy { BoobBot.database.getGuild(event.guild.id) }

        if (event.channelType.isGuild) {
            if (guild.dropEnabled && event.channelType == ChannelType.TEXT && event.channel.asTextChannel().isNSFW) {
                GlobalScope.launch { BootyDropper().processDrop(event) }
            }

            if (event.message.mentions.mentionsEveryone()) {
                BoobBot.metrics.record(Metrics.happened("atEveryoneSeen"))
            }

            if (!event.channel.canTalk()) {
                return
            }

            if (guild.ignoredChannels.contains(event.channel.id)
                && !event.member!!.hasPermission(Permission.MESSAGE_MANAGE)
            ) {
                return
            }

            if (guild.modMute.contains(event.author.id)) {
                return event.message.delete().reason("mod mute").queue()
            }
        }
        val messageContent = event.message.contentRaw
        val standardTrigger = event.isFromGuild.ifTrue { guild.prefix } ?: BoobBot.defaultPrefix
        val acceptablePrefixes = Context.BOT_MENTIONS + standardTrigger

        val trigger = acceptablePrefixes.firstOrNull { messageContent.lowercase().startsWith(it) }
            ?: return

        val args = messageContent.substring(trigger.length).split(" +".toRegex()).toMutableList()
        val commandString = args.removeAt(0)

        val command = BoobBot.commands.findCommand(commandString)

        if (command == null) {
            if (!event.channelType.isGuild) {
                return
            }

            val customCommand = guild.customCommands.firstOrNull { it.name == commandString }
                ?: return

            return event.channel.sendMessage(customCommand.content).queue()
        }

        if (
            event.isFromGuild && (guild.disabled.contains(command.name) ||
                    guild.channelDisabled.any { it.name == command.name && it.channelId == event.channel.id })
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

        // TODO test this logic
        if (command.properties.nsfw && event.isFromGuild && (event.channelType != ChannelType.TEXT || !event.channel.asTextChannel().isNSFW)) {
            BoobBot.requestUtil.get("https://nekos.life/api/v2/img/meow").queue {
                val j = it?.json()
                    ?: return@queue event.channel.sendMessage("This channel isn't NSFW, whore.").queue()

                event.channel.sendMessage(
                    "This isn't an NSFW channel whore, so have some SFW pussy.\n" +
                            "Confused? Try `bbhuh` or join the support server https://discord.gg/wFfFRb3Qbr\n" +
                            j.getString("url")
                ).queue()
            }
            return
        }

        if (event.channelType.isGuild && !event.guild.selfMember.hasPermission(event.guildChannel, Permission.MESSAGE_EMBED_LINKS)) {
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
            val missing = checkMissingPermissions(event.member!!, event.guildChannel, command.properties.userPermissions)

            if (missing.isNotEmpty()) {
                val fmt = missing.joinToString("`\n `", prefix = "`", postfix = "`", transform = Permission::getName)
                return event.channel.sendMessage("You need these permissions, whore:\n$fmt").queue()
            }
        }

        if (event.isFromGuild && command.properties.botPermissions.isNotEmpty()) {
            val missing = checkMissingPermissions(event.guild.selfMember, event.guildChannel, command.properties.botPermissions)

            if (missing.isNotEmpty()) {
                val fmt = missing.joinToString("`\n `", prefix = "`", postfix = "`", transform = Permission::getName)
                return event.channel.sendMessage("I need these permissions, whore:\n$fmt").queue()
            }
        }

        if (event.channelType.isGuild && BoobBot.database.getUserAnonymity(event.author.id)
            && event.guild.selfMember.hasPermission(event.guildChannel, Permission.MESSAGE_MANAGE)
        ) {
            event.message.delete().queue()
        }

        try {
            Utils.logCommand(event.message)
            command.execute(trigger, event.message, args)
            BoobBot.metrics.record(Metrics.happened("command"))
            BoobBot.metrics.record(Metrics.happened(command.name))
            val user: User by lazy { BoobBot.database.getUser(event.author.id) }
            if (command.properties.nsfw) {
                user.nsfwCommandsUsed++
            } else {
                user.commandsUsed++
            }
            user.save()
        } catch (e: Exception) {
            BoobBot.log.error("Command `${command.name}` encountered an error during execution", e)
            event.message.addReaction(Emoji.fromUnicode("\uD83D\uDEAB")).queue()
        }
    }

    private fun processUser(event: MessageReceivedEvent) {
        if (!event.isFromGuild) {
            return
        }
        val user: User by lazy { BoobBot.database.getUser(event.author.id) }
        user.messagesSent++
        if (user.blacklisted) {
            return
        }

        if (user.inJail) {
            user.jailRemaining = min(user.jailRemaining - 1, 0)
            user.inJail = user.jailRemaining > 0
            user.save()
            return
        }

        if (event.channelType == ChannelType.TEXT && event.message.channel.asTextChannel().isNSFW) {
            val tagSize = Formats.tag.count { event.message.contentDisplay.contains(it) }
            user.lewdPoints += min(tagSize, 5)
            user.nsfwMessagesSent++
        }
        if (user.coolDownCount >= random(0, 10)) {
            user.coolDownCount = random(0, 10)

            user.experience++
            if (user.bonusXp > 0) {
                user.experience++ // extra XP
                user.bonusXp = user.bonusXp - 1
            }
        }
        user.level = floor(0.1 * sqrt(user.experience.toDouble())).toInt()
        user.lewdLevel = calculateLewdLevel(user)
        user.save()
    }

}