package bot.boobbot.handlers

import bot.boobbot.BoobBot
import bot.boobbot.entities.db.Guild
import bot.boobbot.entities.db.User
import bot.boobbot.entities.framework.BootyDropper
import bot.boobbot.entities.framework.MessageContext
import bot.boobbot.entities.internals.Config
import bot.boobbot.utils.Constants
import bot.boobbot.utils.Formats
import bot.boobbot.utils.Utils
import bot.boobbot.utils.Utils.calculateLewdLevel
import bot.boobbot.utils.Utils.checkMissingPermissions
import bot.boobbot.utils.Utils.random
import bot.boobbot.utils.json
import com.google.common.primitives.Ints.max
import de.mxro.metrics.jre.Metrics
import kotlinx.coroutines.*
import net.dv8tion.jda.api.Permission
import net.dv8tion.jda.api.entities.channel.ChannelType
import net.dv8tion.jda.api.entities.channel.concrete.ThreadChannel
import net.dv8tion.jda.api.entities.emoji.Emoji
import net.dv8tion.jda.api.events.GenericEvent
import net.dv8tion.jda.api.events.message.MessageReceivedEvent
import net.dv8tion.jda.api.hooks.EventListener
import java.util.concurrent.Executors
import java.util.concurrent.atomic.AtomicInteger
import kotlin.math.floor
import kotlin.math.min
import kotlin.math.sqrt

class MessageHandler : EventListener {
    private val threadCounter = AtomicInteger()
    private val commandExecutorPool = Executors.newCachedThreadPool {
        Thread(it, "Command-Executor-${threadCounter.getAndIncrement()}")
    }

    override fun onEvent(event: GenericEvent) {
        when (event) {
            is MessageReceivedEvent -> onMessageReceived(event)
        }
    }

    private fun onMessageReceived(event: MessageReceivedEvent) {
        BoobBot.metrics.record(Metrics.happened("MessageReceived"))

        if (event.author.isBot) { // Basic check to reduce usage
            return
        }

        commandExecutorPool.execute {
            processMessageEvent(event)
        }

        processUser(event)
    }

    private fun processMessageEvent(event: MessageReceivedEvent) {
        if (event.channelType.isGuild) {
            if (event.message.mentions.mentionsEveryone()) {
                BoobBot.metrics.record(Metrics.happened("atEveryoneSeen"))
            }

            val channel = event.channel
            // IntelliJ insists this is useless because parentChannel can't be null, however this version of JDA
            // doesn't handle a certain type of Thread properly, in which case, parentChannel *can* be null.
            @Suppress("SENSELESS_COMPARISON")
            if (channel is ThreadChannel && channel.parentChannel == null) {
                return
            }

            if (!event.channel.canTalk()) {
                return
            }

            if (BoobBot.database.isIgnoredChannel(event.guild.idLong, event.channel.idLong) && !event.member!!.hasPermission(Permission.MESSAGE_MANAGE)) {
                return
            }

            if (BoobBot.database.isModMuted(event.guild.idLong, event.author.idLong)) {
                return event.message.delete().reason("mod mute").queue()
            }
        }

        val messageContent = event.message.contentRaw.trim()
        //val prefixTrigger = event.jda.selfUser.asMention
        val acceptablePrefixes = MessageContext.BOT_MENTIONS// + prefixTrigger
        val trigger = acceptablePrefixes.firstOrNull { messageContent.lowercase().startsWith(it) }
            ?: return
        val args = messageContent.substring(trigger.length).trim().split("[ \\t]+".toRegex()).dropLastWhile { it.isEmpty() }.toMutableList()

        if (trigger in MessageContext.BOT_MENTIONS && args.isEmpty()) {
            val prefix = event.jda.selfUser.asMention
            return event.channel.sendMessage("My prefix is $prefix whore.\nUse ${prefix}help for a list of commands.").queue()
        }

        val commandString = args.removeAt(0)
        val command = BoobBot.commands.findCommand(commandString.lowercase())

        if (command == null) {
            if (!event.channelType.isGuild) {
                return
            }

            val customCommand = BoobBot.database.getCustomCommands(event.guild.idLong)[commandString]
                ?: return

            return event.channel.sendMessage(customCommand).queue()
        }

        if (event.isFromGuild && (BoobBot.database.isCommandDisabled(event.guild.idLong, command.name) || BoobBot.database.isCommandDisabledInChannel(event.guild.idLong, event.channel.idLong, command.name))) {
            return
        }

        if (!command.properties.enabled) {
            return
        }

        if (command.properties.developerOnly && !BoobBot.owners.contains(event.author.idLong)) {
            return
        }

        if (command.properties.guildOnly && !event.channelType.isGuild) {
            return event.channel.sendMessage("No, whore you can only use this in a guild").queue()
        }

        if (command.properties.nsfw && event.isFromGuild && (event.channelType != ChannelType.TEXT || !event.channel.asTextChannel().isNSFW)) {
            BoobBot.requestUtil.get("https://nekos.life/api/v2/img/meow").queue {
                val j = it?.json()
                    ?: return@queue event.channel.sendMessage("This channel isn't NSFW, whore.").queue()

                event.channel.sendMessage(
                    "This isn't an NSFW channel whore, so have some SFW pussy.\n" +
                            "Confused? Try `/nsfwtoggle` or join the support server ${Constants.SUPPORT_SERVER_URL}\n" +
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

        if (event.channelType.isGuild && BoobBot.database.getUserAnonymity(event.author.idLong) && event.guild.selfMember.hasPermission(event.guildChannel, Permission.MESSAGE_MANAGE)) {
            event.message.delete().queue()
        }

        try {
            Utils.logCommand(event.message)
            command.execute(MessageContext(event.message, args))
            BoobBot.metrics.record(Metrics.happened("command"))
            BoobBot.metrics.record(Metrics.happened(command.name))

            BoobBot.database.getUser(event.author.idLong).let {
                if (command.properties.nsfw) it.nsfwCommandsUsed++
                else it.commandsUsed++
                it.save()
            }
        } catch (e: Exception) {
            BoobBot.log.error("Command `${command.name}` encountered an error during execution", e)

            if (event.isFromGuild && event.guild.selfMember.hasPermission(event.guildChannel, Permission.MESSAGE_HISTORY)) {
                event.message.addReaction(Emoji.fromUnicode("\uD83D\uDEAB")).queue()
            }
        }
    }

    private fun processUser(event: MessageReceivedEvent) {
        if (!event.isFromGuild) {
            return
        }

        val user = BoobBot.database.getUser(event.author.idLong)
        user.messagesSent++

        if (user.blacklisted) {
            return
        }

        if (user.inJail) {
            user.jailRemaining = max(user.jailRemaining - 1, 0)
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
