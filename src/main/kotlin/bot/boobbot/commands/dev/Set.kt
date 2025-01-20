package bot.boobbot.commands.dev

import bot.boobbot.BoobBot
import bot.boobbot.entities.framework.Category
import bot.boobbot.entities.framework.Context
import bot.boobbot.entities.framework.annotations.CommandProperties
import bot.boobbot.entities.framework.annotations.Option
import bot.boobbot.entities.framework.annotations.SubCommand
import bot.boobbot.entities.framework.impl.Resolver
import bot.boobbot.entities.framework.interfaces.Command
import bot.boobbot.utils.Formats
import bot.boobbot.utils.discard
import bot.boobbot.utils.separate
import net.dv8tion.jda.api.Permission
import net.dv8tion.jda.api.entities.Activity
import net.dv8tion.jda.api.entities.Icon
import java.util.concurrent.Executors
import java.util.concurrent.ScheduledExecutorService
import java.util.concurrent.ScheduledFuture
import java.util.concurrent.TimeUnit


@CommandProperties(description = "Modify bot settings.", category = Category.DEV, developerOnly = true, groupByCategory = true)
class Set : Command {
    private var activityResetTask: ScheduledFuture<*>? = null

    override fun execute(ctx: Context) {
        sendSubcommandHelp(ctx)
    }

    @SubCommand(description = "Set the bot username.")
    @Option(name = "new_name", description = "The new bot name.")
    fun name(ctx: Context) {
        val newName = ctx.options.getByNameOrNext("new_name", Resolver.STRING)
            ?: return ctx.reply("to what, whore?")

        ctx.jda.selfUser.manager.setName(newName).queue(
            { ctx.reply(Formats.info("Set UserName to $newName")) },
            { ctx.reply(Formats.error(" Failed to set UserName")) }
        )
    }

    @SubCommand(aliases = ["game"], description = "Set the bot activity.")
    @Option(name = "type", description = "The activity type, or 'clear' to remove.")
    @Option(name = "content", description = "<content>/<url> <content>")
    fun activity(ctx: Context) {
        val type = ctx.options.getByNameOrNext("type", Resolver.STRING)
            ?: return ctx.reply("${ctx.prefix}set activity <type> <content...>")

        val content = ctx.options.getOptionStringOrGather("content")?.split(' ')
        val validTypes = Activity.ActivityType.entries.map { it.name.lowercase() }

        if (type == "clear") {
            activityResetTask?.cancel(true)
            activityResetTask = null
            BoobBot.shardManager.setActivity(DEFAULT_ACTIVITY)
            return ctx.reply(Formats.info("Yes daddy, cleared activity"))
        }

        if (content == null) {
            return ctx.reply("${ctx.prefix}set activity <type> [reset after (e.g. 4h)] [stream URL] <content...>")
        }

        if (!validTypes.contains(type) && type != "playing") {
            return ctx.reply(Formats.monospaced(validTypes))
        }

        val activityType = gameTypeByString(type)
        val matcher = TIME_PATTERN.matcher(content[0])
        val hasTime = matcher.matches()
        val parsedContent = content.discard(if (hasTime) 1 else 0)

        when (activityType) {
            Activity.ActivityType.STREAMING -> { // Special handling
                val (url, extra) = parsedContent.separate()
                BoobBot.shardManager.setActivity(Activity.of(activityType, extra.joinToString(" "), url))
            }
            else -> BoobBot.shardManager.setActivity(Activity.of(activityType, parsedContent.joinToString(" ")))
        }

        var append = ""

        if (hasTime) {
            val resetAfter = parseTimeToMillis(matcher.group(1).toLong(), matcher.group(2))

            if (resetAfter == null) {
                append = "The status will not be automatically reset due to parsing failure (unrecognised unit?)"
            } else {
                activityResetTask?.cancel(true)
                activityResetTask = SCHEDULER.schedule({
                    BoobBot.shardManager.setActivity(DEFAULT_ACTIVITY)
                    activityResetTask = null
                }, resetAfter, TimeUnit.MILLISECONDS)
            }
        }

        ctx.reply(Formats.info("Yes daddy, status set\n$append"))
    }

    @SubCommand(description = "Set the bot nickname.")
    @Option(name = "new_nick", description = "The new bot nickname.")
    fun nick(ctx: Context) {
        if (ctx._guild == null) {
            return ctx.reply("This command must be executed within a guild.")
        }

        if (!ctx.botCan(Permission.NICKNAME_CHANGE)) {
            return ctx.reply("Missing `NICKNAME_CHANGE` permission.")
        }

        val newNick = ctx.options.getByNameOrNext("new_nick", Resolver.STRING)
            ?: return ctx.reply("to what, whore?")

        ctx.guild.selfMember.modifyNickname(newNick)
            .reason("BoobBot nick set")
            .queue(
                { ctx.reply(Formats.info("Yes daddy, nick set")) },
                { ctx.reply(Formats.error(" Failed to set nick")) }
            )
    }

    @SubCommand(description = "Set the bot avatar.")
    @Option(name = "avatar_url", description = "The URL of the new avatar.")
    fun avatar(ctx: Context) {
        val avatarUrl = ctx.options.getByNameOrNext("avatar_url", Resolver.STRING)
            ?: return ctx.reply("where tf am I supposed to download the avatar from?")

        BoobBot.requestUtil.get(avatarUrl).queue {
            val image = it?.body?.byteStream() ?: return@queue ctx.reply("Unable to fetch avatar")

            ctx.jda.selfUser.manager.setAvatar(Icon.from(image)).queue(
                { ctx.reply(Formats.info("Yes daddy, avatar set")) },
                { ctx.reply(Formats.error(" Failed to set avatar")) }
            )
            BoobBot.log.info("Setting New Avatar")
        }
    }

    private fun gameTypeByString(s: String) = Activity.ActivityType.valueOf(s.uppercase())

    companion object {
        private val DEFAULT_ACTIVITY = Activity.playing("discord.gg/boobbot | @BoobBot help")

        val SCHEDULER: ScheduledExecutorService = Executors.newSingleThreadScheduledExecutor()
        private val TIME_PATTERN = "(\\d+)(s|m|h|d)".toPattern()

        fun parseTimeToMillis(duration: Long, unit: String): Long? = when(unit) {
            "s" -> duration * 1000
            "m" -> duration * 60000
            "h" -> duration * 3600000
            "d" -> duration * 86400000
            else -> null
        }
    }
}
