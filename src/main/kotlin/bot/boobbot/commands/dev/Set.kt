package bot.boobbot.commands.dev

import bot.boobbot.BoobBot
import bot.boobbot.flight.*
import bot.boobbot.misc.Formats
import bot.boobbot.misc.separate
import net.dv8tion.jda.api.Permission
import net.dv8tion.jda.api.entities.Activity
import net.dv8tion.jda.api.entities.Icon


@CommandProperties(description = "Settings", category = Category.DEV, developerOnly = true)
class Set : Command {

    var isCustomGameSet = false
        private set

    override fun execute(ctx: Context) {
        ctx.send("Specify a subcommand: ${subcommands.keys.joinToString(", ")}")
    }

    @SubCommand
    fun name(ctx: Context) {
        if (ctx.args.isEmpty()) {
            return ctx.send("to what, whore?")
        }

        val args = ctx.args.joinToString(" ")

        ctx.jda.selfUser.manager.setName(args).queue(
            { ctx.send(Formats.info("Set UserName to $args")) },
            { ctx.send(Formats.error(" Failed to set UserName")) }
        )
    }

    @SubCommand(aliases = ["activity"])
    fun game(ctx: Context) {
        if (ctx.args.isEmpty()) {
            return ctx.send("${ctx.trigger}set game <type> <content...>")
        }

        val validTypes = Activity.ActivityType.values().map { it.name.toLowerCase() }
        val (type, content) = ctx.args.separate()

        if (type == "clear") {
            isCustomGameSet = false
            BoobBot.shardManager.setActivity(Activity.playing("bbhelp || bbinvite"))
            return ctx.send(Formats.info("Yes daddy, cleared game"))
        }

        if (!validTypes.contains(type)) {
            return ctx.send(Formats.monospaced(validTypes))
        }

        val activityType = gameTypeByString(type)

        if (activityType == Activity.ActivityType.STREAMING) { // Special handling
            val (url, extra) = content.separate()
            BoobBot.shardManager.setActivity(Activity.of(activityType, extra.joinToString(" "), url))
        } else {
            BoobBot.shardManager.setActivity(Activity.of(activityType, content.joinToString(" ")))
        }

        isCustomGameSet = true
        ctx.send(Formats.info("Yes daddy, status set"))
    }

    @SubCommand
    fun nick(ctx: Context) {
        if (ctx.guild == null) {
            return ctx.send("This command must be executed within a guild.")
        }

        if (!ctx.botCan(Permission.NICKNAME_CHANGE)) {
            return ctx.send("Missing `NICKNAME_CHANGE` permission.")
        }

        ctx.guild.selfMember.modifyNickname(ctx.args.joinToString(" "))
            .reason("BoobBot nick set")
            .queue(
                { ctx.send(Formats.info("Yes daddy, nick set")) },
                { ctx.send(Formats.error(" Failed to set nick")) }
            )
    }

    @SubCommand
    fun avatar(ctx: Context) {
        BoobBot.requestUtil.get(ctx.args[0]).queue {
            val image = it?.body()?.byteStream() ?: return@queue ctx.send("Unable to fetch avatar")

            ctx.jda.selfUser.manager.setAvatar(Icon.from(image)).queue(
                { ctx.send(Formats.info("Yes daddy, avatar set")) },
                { ctx.send(Formats.error(" Failed to set avatar")) }
            )
            BoobBot.log.info("Setting New Avatar")
            BoobBot.manSetAvatar = true
        }
    }

    @SubCommand
    fun icons(ctx: Context) {
        BoobBot.requestUtil.get(ctx.args[0]).queue {
            val image = it?.body()?.byteStream() ?: return@queue ctx.send("Unable to fetch image")
            val icon = Icon.from(image)

            BoobBot.shardManager.home?.manager?.setIcon(icon)?.queue()
            ctx.jda.selfUser.manager.setAvatar(icon).queue(
                { ctx.send(Formats.info("Yes daddy, icons set")) },
                { ctx.send(Formats.error(" Failed to set avatar")) }
            )
            BoobBot.log.info("Setting New icons")
            BoobBot.manSetAvatar = true
        }
    }

    fun gameTypeByString(s: String): Activity.ActivityType {
        val t = s.toUpperCase()

        if (t == "PLAYING") { // TABLEFLIP
            return Activity.ActivityType.DEFAULT
        }

        return Activity.ActivityType.valueOf(s)
    }
}
