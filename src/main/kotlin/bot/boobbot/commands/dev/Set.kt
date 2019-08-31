package bot.boobbot.commands.dev

import bot.boobbot.BoobBot
import bot.boobbot.flight.*
import bot.boobbot.misc.Formats
import bot.boobbot.misc.separate
import net.dv8tion.jda.core.Permission
import net.dv8tion.jda.core.entities.Game
import net.dv8tion.jda.core.entities.Icon


@CommandProperties(description = "Settings", category = Category.DEV, developerOnly = true)
class Set : Command {

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

        val validTypes = Game.GameType.values().map { it.name.toLowerCase() }
        val (type, content) = ctx.args.separate()

        if (type == "clear") {
            BoobBot.setGame = false
            BoobBot.shardManager.setGame(Game.playing("bbhelp || bbinvite"))
            return ctx.send(Formats.info("Yes daddy, cleared game"))
        }

        if (!validTypes.contains(type)) {
            return ctx.send(Formats.monospaced(validTypes))
        }

        val activityType = gameTypeByString(type)

        if (activityType == Game.GameType.STREAMING) { // Special handling
            val (url, extra) = content.separate()
            BoobBot.shardManager.setGame(Game.of(activityType, extra.joinToString(" "), url))
        } else {
            BoobBot.shardManager.setGame(Game.of(activityType, content.joinToString(" ")))
        }

        BoobBot.setGame = true
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

        ctx.guild.controller.setNickname(ctx.selfMember, ctx.args.joinToString(" "))
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

            BoobBot.home?.manager?.setIcon(icon)?.queue()
            ctx.jda.selfUser.manager.setAvatar(icon).queue(
                { ctx.send(Formats.info("Yes daddy, icons set")) },
                { ctx.send(Formats.error(" Failed to set avatar")) }
            )
            BoobBot.log.info("Setting New icons")
            BoobBot.manSetAvatar = true
        }
    }

    fun gameTypeByString(s: String): Game.GameType {
        val t = s.toUpperCase()

        if (t == "PLAYING") { // TABLEFLIP
            return Game.GameType.DEFAULT
        }

        return Game.GameType.valueOf(s)
    }
}