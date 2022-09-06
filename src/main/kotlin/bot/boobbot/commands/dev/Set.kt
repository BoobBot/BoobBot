package bot.boobbot.commands.dev

import bot.boobbot.BoobBot
import bot.boobbot.entities.framework.*
import bot.boobbot.entities.framework.annotations.CommandProperties
import bot.boobbot.entities.framework.annotations.SubCommand
import bot.boobbot.entities.framework.interfaces.Command
import bot.boobbot.utils.Formats
import bot.boobbot.utils.separate
import net.dv8tion.jda.api.Permission
import net.dv8tion.jda.api.entities.Activity
import net.dv8tion.jda.api.entities.Icon


@CommandProperties(description = "Modify bot settings.", category = Category.DEV, developerOnly = true)
class Set : Command {

    var isCustomGameSet = false
        private set

    override fun execute(ctx: MessageContext) {
        ctx.reply("Specify a subcommand: ${subcommands.keys.joinToString(", ")}")
    }

    @SubCommand
    fun name(ctx: MessageContext) {
        if (ctx.args.isEmpty()) {
            return ctx.reply("to what, whore?")
        }

        val args = ctx.args.joinToString(" ")

        ctx.jda.selfUser.manager.setName(args).queue(
            { ctx.reply(Formats.info("Set UserName to $args")) },
            { ctx.reply(Formats.error(" Failed to set UserName")) }
        )
    }

    @SubCommand(aliases = ["activity"])
    fun game(ctx: MessageContext) {
        if (ctx.args.isEmpty()) {
            return ctx.reply("${ctx.prefix}set game <type> <content...>")
        }

        val validTypes = Activity.ActivityType.values().map { it.name.lowercase() }
        val (type, content) = ctx.args.separate()

        if (type == "clear") {
            isCustomGameSet = false
            BoobBot.shardManager.setActivity(Activity.playing("bbhelp || bbinvite"))
            return ctx.reply(Formats.info("Yes daddy, cleared game"))
        }

        if (!validTypes.contains(type) && type != "playing") {
            return ctx.reply(Formats.monospaced(validTypes))
        }

        val activityType = gameTypeByString(type)

        if (activityType == Activity.ActivityType.STREAMING) { // Special handling
            val (url, extra) = content.separate()
            BoobBot.shardManager.setActivity(Activity.of(activityType, extra.joinToString(" "), url))
        } else {
            BoobBot.shardManager.setActivity(Activity.of(activityType, content.joinToString(" ")))
        }

        isCustomGameSet = true
        ctx.reply(Formats.info("Yes daddy, status set"))
    }

    @SubCommand
    fun nick(ctx: MessageContext) {
        if (ctx.guild == null) {
            return ctx.reply("This command must be executed within a guild.")
        }

        if (!ctx.botCan(Permission.NICKNAME_CHANGE)) {
            return ctx.reply("Missing `NICKNAME_CHANGE` permission.")
        }

        ctx.guild.selfMember.modifyNickname(ctx.args.joinToString(" "))
            .reason("BoobBot nick set")
            .queue(
                { ctx.reply(Formats.info("Yes daddy, nick set")) },
                { ctx.reply(Formats.error(" Failed to set nick")) }
            )
    }

    @SubCommand
    fun avatar(ctx: MessageContext) {
        BoobBot.requestUtil.get(ctx.args[0]).queue {
            val image = it?.body?.byteStream() ?: return@queue ctx.reply("Unable to fetch avatar")

            ctx.jda.selfUser.manager.setAvatar(Icon.from(image)).queue(
                { ctx.reply(Formats.info("Yes daddy, avatar set")) },
                { ctx.reply(Formats.error(" Failed to set avatar")) }
            )
            BoobBot.log.info("Setting New Avatar")
        }
    }

    private fun gameTypeByString(s: String) = Activity.ActivityType.valueOf(s.uppercase())
}
