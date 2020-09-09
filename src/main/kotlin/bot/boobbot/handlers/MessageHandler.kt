package bot.boobbot.handlers

import bot.boobbot.BoobBot
import bot.boobbot.entities.db.Guild
import bot.boobbot.entities.db.User
import bot.boobbot.entities.framework.BootyDropper
import bot.boobbot.entities.internals.Config
import bot.boobbot.utils.Formats
import bot.boobbot.utils.Utils
import bot.boobbot.utils.json
import de.mxro.metrics.jre.Metrics
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import net.dv8tion.jda.api.Permission
import net.dv8tion.jda.api.entities.Member
import net.dv8tion.jda.api.entities.TextChannel
import net.dv8tion.jda.api.events.message.MessageReceivedEvent
import net.dv8tion.jda.api.hooks.ListenerAdapter
import java.util.*
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

        if (event.author.isBot) { // Basic check to reduce thread usage
            return
        }

        commandExecutorPool.execute {
            processMessageEvent(event)
        }

    }

    private fun processMessageEvent(event: MessageReceivedEvent) {

        val guildData: Guild by lazy { BoobBot.database.getGuild(event.guild.id) }
        val user: User by lazy { BoobBot.database.getUser(event.author.id) }

        if (event.channelType.isGuild) {

            if (guildData.dropEnabled && event.textChannel.isNSFW) {
                GlobalScope.launch { BootyDropper().processDrop(event) }
            }

            if (event.message.mentionsEveryone()) {
                BoobBot.metrics.record(Metrics.happened("atEveryoneSeen"))
            }

            if (!event.textChannel.canTalk()) {
                return
            }

            if (guildData.ignoredChannels.contains(event.channel.id)
                && !event.member!!.hasPermission(Permission.MESSAGE_MANAGE)
            ) {
                return
            }

            if (guildData.modMute.contains(event.author.id)) {
                return event.message.delete().reason("mod mute").queue()
            }
        }

        processCommand(event, user, guildData)
        processUser(event, user)
    }


    private fun processCommand(event: MessageReceivedEvent, user: User, guild: Guild) {
        val messageContent = event.message.contentRaw
        val standardTrigger =
            if (event.isFromGuild) guild.prefix ?: BoobBot.defaultPrefix else BoobBot.defaultPrefix
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


        if (event.channelType.isGuild && user.anonymity
            && event.guild.selfMember.hasPermission(event.textChannel, Permission.MESSAGE_MANAGE)
        ) {
            event.message.delete().queue(null, {})
        }

        try {
            Utils.logCommand(event.message)
            command.execute(trigger, event.message, args)
            BoobBot.metrics.record(Metrics.happened("command"))
            BoobBot.metrics.record(Metrics.happened(command.name))
            if (command.properties.nsfw) {
                user.nsfwCommandsUsed++
            } else {
                user.commandsUsed++
            }
            user.save()
        } catch (e: Exception) {
            BoobBot.log.error("Command `${command.name}` encountered an error during execution", e)
            BoobBot.log.info(user.toString())
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

    private fun processUser(event: MessageReceivedEvent, user: User) {
        if (!event.isFromGuild) {
            return
        }
        user.messagesSent++
        if (!user.blacklisted) {
            if (user.inJail) {
                user.jailRemaining = min(user.jailRemaining - 1, 0)
                user.inJail = user.jailRemaining > 0
            }

            if (event.message.textChannel.isNSFW) {
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

    private fun calculateLewdLevel(user: User): Int {
        val calculateLewdPoints =
            (user.experience / 100) * .1 +
                    (user.nsfwCommandsUsed / 100) * .3 -
                    (user.commandsUsed / 100) * .3 +
                    (user.lewdPoints / 100) * 20
        // lewd level up
        return floor(0.1 * sqrt(calculateLewdPoints)).toInt()
    }

    private val random = Random()
    private fun random(lower: Int, upper: Int): Int {
        return random.nextInt(upper - lower) + lower
    }


}
