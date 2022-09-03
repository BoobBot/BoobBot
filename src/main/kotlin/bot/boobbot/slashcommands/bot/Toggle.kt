package bot.boobbot.slashcommands.bot

import bot.boobbot.entities.framework.*
import bot.boobbot.entities.internals.Config
import net.dv8tion.jda.api.Permission
import net.dv8tion.jda.api.entities.GuildChannel
import net.dv8tion.jda.api.entities.Member
import net.dv8tion.jda.api.entities.User
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent

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

    override fun execute(event: SlashCommandInteractionEvent) {
        if (!event.isFromGuild) {
            return event.reply("This can only be run within guilds.").queue()
        }

        if (!event.guild!!.selfMember.hasPermission(event.guildChannel, Permission.MANAGE_CHANNEL)) {
            return event.reply("\uD83D\uDEAB Hey whore, I lack the `MANAGE_CHANNEL` permission needed to do this").queue()
        }

        if (!permissionCheck(event.member!!, event.guildChannel, Permission.MANAGE_CHANNEL)) {
            return event.reply("\uD83D\uDEAB Hey whore, you lack the `MANAGE_CHANNEL` permission needed to do this").queue()
        }

        // TODO: Check channel type.

        val nsfwStatus = !event.guildChannel.asTextChannel().isNSFW

        event.guildChannel.asTextChannel().manager.setNSFW(nsfwStatus).queue({
            val changed = if (nsfwStatus) "allowed" else "disallowed"
            event.reply("NSFW on this channel is now $changed").queue()
        }, {
            event.reply("shit something broke\n\n$it").queue()
        })
    }

}
