package bot.boobbot.commands.dev

import bot.boobbot.BoobBot
import bot.boobbot.entities.framework.*
import bot.boobbot.entities.framework.annotations.CommandProperties
import bot.boobbot.entities.framework.annotations.Option
import bot.boobbot.entities.framework.annotations.Options
import bot.boobbot.entities.framework.annotations.SubCommand
import bot.boobbot.entities.framework.impl.Resolver
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

    @SubCommand(aliases = ["activity"], description = "Set the bot activity.")
    @Options([ // TODO: Revisit
        Option(name = "type", description = "The activity type."),
        Option(name = "content", description = "<content>/<url> <content>/clear")
    ])
    fun game(ctx: Context) {
        val type = ctx.options.getByNameOrNext("type", Resolver.STRING)
            ?: return ctx.reply("${ctx.prefix}set game <type> <content...>")

        val content = ctx.options.getOptionStringOrGather("content")?.split(' ')
            ?: return ctx.reply("${ctx.prefix}set game <type> <content...>")

        val validTypes = Activity.ActivityType.values().map { it.name.lowercase() }

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

    @SubCommand(description = "Set the bot nickname.")
    @Option(name = "new_nick", description = "The new bot nickname.")
    fun nick(ctx: Context) {
        if (ctx.guild == null) {
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
}
