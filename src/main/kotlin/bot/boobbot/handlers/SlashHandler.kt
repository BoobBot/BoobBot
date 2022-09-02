package bot.boobbot.handlers

import bot.boobbot.BoobBot
import bot.boobbot.entities.db.Guild
import bot.boobbot.entities.framework.ExecutableSlashCommand
import bot.boobbot.entities.internals.Config
import bot.boobbot.utils.Formats
import bot.boobbot.utils.Utils
import bot.boobbot.utils.Utils.checkMissingPermissions
import bot.boobbot.utils.json
import de.mxro.metrics.jre.Metrics
import net.dv8tion.jda.api.Permission
import net.dv8tion.jda.api.entities.ChannelType
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent
import net.dv8tion.jda.api.hooks.ListenerAdapter
import java.util.concurrent.Executors
import java.util.concurrent.atomic.AtomicInteger

class SlashHandler : ListenerAdapter() {
    private val threadCounter = AtomicInteger()
    private val commandExecutorPool = Executors.newCachedThreadPool {
        Thread(it, "Command-Executor-${threadCounter.getAndIncrement()}")
    }

    override fun onSlashCommandInteraction(event: SlashCommandInteractionEvent) {
        BoobBot.metrics.record(Metrics.happened("SlashCommandInteractionEvent"))
        commandExecutorPool.execute {
            processMessageEvent(event)
        }
    }

    private fun getCommand(event: SlashCommandInteractionEvent): ExecutableSlashCommand? {
        event.getOption("category")?.let {
            return BoobBot.slashCommands.findCommand(it.asString)
        }

        event.getOption("leaderboards")?.let {
            return BoobBot.slashCommands.findCommand(it.asString)
        }

        BoobBot.slashCommands.findCommand(event.name)?.let { return it }

        return event.subcommandName?.takeIf { it.isNotEmpty() }?.let(BoobBot.slashCommands::findCommand)
    }

    private fun processMessageEvent(event: SlashCommandInteractionEvent) {
        val guild: Guild by lazy { BoobBot.database.getGuild(event.guild!!.id) }

        if (event.isFromGuild) {
            if (!event.guildChannel.canTalk()) {
                return
            }

            if (guild.ignoredChannels.contains(event.channel.id) && !event.member!!.hasPermission(Permission.MESSAGE_MANAGE)) {
                return
            }
        }

        val command = getCommand(event) ?: return

        if (event.isFromGuild && (guild.disabled.contains(command.name) || guild.channelDisabled.any { it.name == command.name && it.channelId == event.channel.id })) {
            return
        }

        if (!command.properties.enabled) {
            return
        }

        if (command.properties.developerOnly && !Config.OWNERS.contains(event.user.idLong)) {
            return
        }

        if (command.properties.guildOnly && !event.channelType.isGuild) {
            return event.reply("No, whore you can only use this in a guild").queue()
        }

        if (command.properties.nsfw && event.isFromGuild && (event.channelType != ChannelType.TEXT || !event.channel.asTextChannel().isNSFW)) {
            BoobBot.requestUtil.get("https://nekos.life/api/v2/img/meow").queue {
                val j = it?.json()
                    ?: return@queue event.reply("This channel isn't NSFW, whore.").queue()

                event.reply(
                    "This isn't an NSFW channel whore, so have some SFW pussy.\n" +
                            "Confused? Try `bbhuh` or join the support server https://discord.gg/wFfFRb3Qbr\n" +
                            j.getString("url")
                ).queue()
            }
            return
        }

        if (command.properties.donorOnly && !Utils.checkSlashDonor(event)) {
            return event.reply(
                Formats.error(
                    " Sorry this command is only available to our Patrons.\n<:p_:475801484282429450> "
                            + "Stop being a cheap fuck and join today!\nhttps://www.patreon.com/OfficialBoobBot"
                )
            ).queue()
        }

        if (event.channelType.isGuild && !event.guild!!.selfMember.hasPermission(
                event.guildChannel,
                Permission.MESSAGE_EMBED_LINKS
            )
        ) {
            return event.reply("I do not have permission to use embeds, da fuck?").queue()
        }


        if (event.isFromGuild && command.properties.userPermissions.isNotEmpty()) {
            val missing =
                checkMissingPermissions(event.member!!, event.guildChannel, command.properties.userPermissions)

            if (missing.isNotEmpty()) {
                val fmt = missing.joinToString("`\n `", prefix = "`", postfix = "`", transform = Permission::getName)
                return event.reply("You need these permissions, whore:\n$fmt").queue()
            }
        }

        if (event.isFromGuild && command.properties.botPermissions.isNotEmpty()) {
            val missing =
                checkMissingPermissions(event.guild!!.selfMember, event.guildChannel, command.properties.botPermissions)

            if (missing.isNotEmpty()) {
                val fmt = missing.joinToString("`\n `", prefix = "`", postfix = "`", transform = Permission::getName)
                return event.reply("I need these permissions, whore:\n$fmt").queue()
            }
        }

        try {
            command.execute(event)
            BoobBot.metrics.record(Metrics.happened("SlashCommand"))
            BoobBot.metrics.record(Metrics.happened(command.name))
        } catch (e: Exception) {
            BoobBot.log.error("Command `${command.name}` encountered an error during execution", e)
            event.reply("\uD83D\uDEAB Command `${command.name}` encountered an error during execution").queue()
        }
    }
}
