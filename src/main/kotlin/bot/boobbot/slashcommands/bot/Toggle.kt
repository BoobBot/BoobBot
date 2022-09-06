package bot.boobbot.slashcommands.bot

import bot.boobbot.entities.framework.Category
import bot.boobbot.entities.framework.annotations.CommandProperties
import bot.boobbot.entities.framework.SlashCommand
import bot.boobbot.entities.framework.SlashContext
import bot.boobbot.entities.internals.Config
import net.dv8tion.jda.api.Permission
import net.dv8tion.jda.api.entities.GuildChannel
import net.dv8tion.jda.api.entities.Member

@CommandProperties(
    description = "Toggles the current channels nsfw setting",
    aliases = [],
    guildOnly = true,
    category = Category.MISC
)

class Toggle : SlashCommand {
    private fun permissionCheck(m: Member, channel: GuildChannel, vararg permissions: Permission): Boolean {
        return Config.OWNERS.contains(m.user.idLong) || m.hasPermission(channel, *permissions)
    }

    override fun execute(ctx: SlashContext) {
        if (!ctx.isFromGuild) {
            return ctx.reply("This can only be run within guilds.")
        }

        if (!ctx.guild!!.selfMember.hasPermission(ctx.guildChannel!!, Permission.MANAGE_CHANNEL)) {
            return ctx.reply("\uD83D\uDEAB Hey whore, I lack the `MANAGE_CHANNEL` permission needed to do this")
        }

        if (!permissionCheck(ctx.member!!, ctx.guildChannel, Permission.MANAGE_CHANNEL)) {
            return ctx.reply("\uD83D\uDEAB Hey whore, you lack the `MANAGE_CHANNEL` permission needed to do this")
        }

        // TODO: Check channel type.

        val channel = ctx.textChannel ?: return ctx.reply("This can only be run in a text channel, whore.")
        val newNsfw = !channel.isNSFW

        channel.manager.setNSFW(newNsfw).queue({
            val changed = if (newNsfw) "allowed" else "disallowed"
            ctx.reply("NSFW on this channel is now $changed")
        }, {
            ctx.reply("shit something broke\n\n$it")
        })
    }

}
