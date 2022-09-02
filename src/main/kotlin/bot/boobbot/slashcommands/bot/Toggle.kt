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

    override fun execute(event: SlashCommandInteractionEvent) {

        fun permissionCheck(u: User, m: Member?, channel: GuildChannel, vararg permissions: Permission): Boolean {
            return !event.isFromGuild || Config.OWNERS.contains(u.idLong) || m?.hasPermission(channel, *permissions) == true
        }

        fun userCan(check: Permission) = event.guildChannel?.let { permissionCheck(event.user, event.member, event.guildChannel, check) } ?: false

        fun botCan(vararg check: Permission) = event.guildChannel?.let { permissionCheck(event.jda.selfUser, event.guild?.selfMember, it, *check) } ?: false



        if (!botCan(Permission.MANAGE_CHANNEL)) {
            return event.reply("\uD83D\uDEAB Hey whore, I lack the `MANAGE_CHANNEL` permission needed to do this").queue()
        }

        if (!userCan(Permission.MANAGE_CHANNEL)) {
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