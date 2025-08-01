package bot.boobbot.handlers

import bot.boobbot.BoobBot
import bot.boobbot.entities.framework.SlashContext
import bot.boobbot.utils.Constants
import bot.boobbot.utils.Formats
import bot.boobbot.utils.Utils
import bot.boobbot.utils.Utils.checkMissingPermissions
import bot.boobbot.utils.json
import de.mxro.metrics.jre.Metrics
import io.sentry.Sentry
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.plus
import net.dv8tion.jda.api.Permission
import net.dv8tion.jda.api.entities.channel.ChannelType
import net.dv8tion.jda.api.entities.channel.concrete.ThreadChannel
import net.dv8tion.jda.api.events.GenericEvent
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent
import net.dv8tion.jda.api.hooks.EventListener
import java.util.concurrent.Executors
import java.util.concurrent.atomic.AtomicInteger

class SlashHandler : EventListener {
    private val threadCounter = AtomicInteger()
    private val commandExecutorPool = Executors.newCachedThreadPool {
        Thread(it, "Slash-Executor-${threadCounter.getAndIncrement()}")
    }
    private val asyncScope = CoroutineScope(Dispatchers.Default) + SupervisorJob()

    override fun onEvent(event: GenericEvent) {
        when (event) {
            is SlashCommandInteractionEvent -> onSlashInteraction(event)
        }
    }

    private fun onSlashInteraction(event: SlashCommandInteractionEvent) {
        BoobBot.metrics.record(Metrics.happened("SlashReceived"))

        if (event.user.isBot) { // Basic check to reduce usage
            return
        }

        commandExecutorPool.execute {
            processSlashEvent(event)
        }
    }

    private fun processSlashEvent(event: SlashCommandInteractionEvent) {
        if (event.channelType.isGuild) {
            (event.channel as? ThreadChannel)?.let {
                @Suppress("USELESS_ELVIS")
                it.parentChannel ?: return
            }

            if (!event.channel.canTalk()) {
                return
            }

            if (BoobBot.database.isIgnoredChannel(event.guild!!.idLong, event.channel.idLong) && !event.member!!.hasPermission(Permission.MESSAGE_MANAGE)) {
                return
            }
        }

        val commandString = event.name
        val commandGroup = event.subcommandGroup
        val commandGroupName = event.subcommandName
        val command = commandGroup?.let { BoobBot.commands.findCommand(commandString, commandGroup) }
            ?: BoobBot.commands.findCommand(commandString) ?: BoobBot.commands.findCommand(commandGroupName.toString())
            ?: return event.reply("Command not found").setEphemeral(true).queue()

        if (event.isFromGuild && (BoobBot.database.isCommandDisabled(event.guild!!.idLong, command.name) || BoobBot.database.isCommandDisabledInChannel(event.guild!!.idLong, event.channel.idLong, command.name))) {
            return event.reply("Command is disabled").setEphemeral(true).queue()
        }

        if (!command.properties.enabled) {
            return
        }

        if (command.properties.developerOnly && !BoobBot.owners.contains(event.user.idLong)) {
            return
        }

        if (command.properties.guildOnly && !event.isFromGuild) {
            return event.reply("No, whore you can only use this in a guild").queue()
        }

        if (command.properties.nsfw && event.isFromGuild && (event.channelType != ChannelType.TEXT || !event.channel.asTextChannel().isNSFW)) {
            BoobBot.requestUtil.get("https://nekos.life/api/v2/img/meow").queue {
                val j = it?.json()
                    ?: return@queue event.reply("This channel isn't NSFW, whore.").queue()

                event.reply(
                    "This isn't an NSFW channel whore, so have some SFW pussy.\n" +
                            "Confused? Try `/nsfwtoggle` or join the support server ${Constants.SUPPORT_SERVER_URL}\n" +
                            j.getString("url")
                ).queue()
            }
            return
        }

        if (event.channelType.isGuild && !event.guild!!.selfMember.hasPermission(event.guildChannel, Permission.MESSAGE_EMBED_LINKS)) {
            return event.reply("I do not have permission to use embeds, da fuck?").queue()
        }

        if (command.properties.donorOnly && !Utils.checkDonor(event.user, event.guild)) {
            return event.reply(
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
                return event.reply("You need these permissions, whore:\n$fmt").queue()
            }
        }

        if (event.isFromGuild && command.properties.botPermissions.isNotEmpty()) {
            val missing = checkMissingPermissions(event.guild!!.selfMember, event.guildChannel, command.properties.botPermissions)

            if (missing.isNotEmpty()) {
                val fmt = missing.joinToString("`\n `", prefix = "`", postfix = "`", transform = Permission::getName)
                return event.reply("I need these permissions, whore:\n$fmt").queue()
            }
        }

        if (event.channelType.isGuild && BoobBot.database.getUserAnonymity(event.user.idLong) && event.guild!!.selfMember.hasPermission(event.guildChannel, Permission.MESSAGE_MANAGE)) {
            //event.message.delete().queue()
            // TODO maybe set ephemeral reply?
        }

        try {
            //Utils.logCommand(event.message)
            command.execute(SlashContext(event))
            BoobBot.metrics.record(Metrics.happened("command"))
            BoobBot.metrics.record(Metrics.happened(command.name))

            val key = if (command.properties.nsfw) "nsfwCommandsUsed" else "commandsUsed"
            BoobBot.database.execute("INSERT INTO users_v2 (userId, $key) VALUES (?, ?) ON DUPLICATE KEY UPDATE $key = $key + 1", event.user.idLong, 1)
        } catch (e: Exception) {
            BoobBot.log.error("Command `${command.name}` encountered an error during execution", e)

            if (event.isAcknowledged) {
                event.channel.sendMessage("Error occurred during command processing.").queue()
            } else {
                event.reply("Error occurred during command processing.").queue()
            }

            Sentry.capture(e)
        }
    }
}
