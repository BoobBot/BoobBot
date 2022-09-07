package bot.boobbot.commands.mod

import bot.boobbot.BoobBot
import bot.boobbot.entities.framework.Category
import bot.boobbot.entities.framework.Context
import bot.boobbot.entities.framework.annotations.CommandProperties
import bot.boobbot.entities.framework.MessageContext
import bot.boobbot.entities.framework.annotations.Option
import bot.boobbot.entities.framework.impl.ModCommand
import net.dv8tion.jda.api.Permission
import net.dv8tion.jda.api.interactions.commands.OptionType

@CommandProperties(
    description = "Mute an Admin.",
    donorOnly = true,
    guildOnly = true,
    aliases = ["mm"],
    category = Category.MOD,
    userPermissions = [Permission.KICK_MEMBERS],
    botPermissions = [Permission.KICK_MEMBERS]
)
@Option(name = "target", description = "The user to ban.", type = OptionType.USER)
class ModMute : ModCommand() {
    override fun execute(ctx: Context) {
        val (user, member, _) = resolveTargetAndReason(ctx)

        if (user == null) {
            return ctx.reply("How in the fuck would i know who you want to mute if you don't give me a valid target?")
        }

        if (user.idLong == ctx.user.idLong) {
            return ctx.reply("You must be special if you're really trying to mute yourself.")
        }

        if (user.idLong == ctx.selfMember!!.idLong) {
            return ctx.reply("Don't you fucking touch me whore, I will end you.")
        }

        if (member != null) {
            if (!ctx.member!!.canInteract(member)) {
                return ctx.reply("You don't have permission to do that, fuck off.")
            }

            if (!ctx.selfMember.canInteract(member)) {
                return ctx.reply("I don't have permission to do that, fix it or fuck off.")
            }
        }

        val g = BoobBot.database.getGuild(ctx.guild!!.id)
        val isMuted = g.modMute.contains(user.id)
        val status = if (isMuted) "Un-muted" else "Muted"

        if (isMuted) {
            g.modMute.remove(user.id)
        } else {
            g.modMute.add(user.id)
        }
        g.save()
        ctx.reply("$status ${user.asMention}.")
    }
}
