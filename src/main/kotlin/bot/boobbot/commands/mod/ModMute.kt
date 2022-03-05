package bot.boobbot.commands.mod

import bot.boobbot.BoobBot
import bot.boobbot.entities.framework.Category
import bot.boobbot.entities.framework.CommandProperties
import bot.boobbot.entities.framework.Context
import bot.boobbot.entities.framework.ModCommand
import net.dv8tion.jda.api.Permission

@CommandProperties(
    description = "Mute a Admin <:p_:475801484282429450>",
    donorOnly = true,
    guildOnly = true,
    aliases = ["mm"],
    category = Category.MOD,
    userPermissions = [Permission.KICK_MEMBERS],
    botPermissions = [Permission.KICK_MEMBERS]
)
class ModMute : ModCommand() {
    override fun execute(ctx: Context) {
        val (member, user, _, resolved) = resolveTargetAndReason(ctx)

        if (!resolved) {
            return ctx.send("How in the fuck would i know who you want to mute if you don't give me a valid target?")
        }

        if (user.idLong == ctx.author.idLong) {
            return ctx.send("You must be special if you're really trying to mute yourself.")
        }

        if (user.idLong == ctx.selfMember!!.idLong) {
            return ctx.send("Don't you fucking touch me whore, I will end you.")
        }

        if (member != null) {
            if (!ctx.member!!.canInteract(member)) {
                return ctx.send("You don't have permission to do that, fuck off.")
            }

            if (!ctx.selfMember.canInteract(member)) {
                return ctx.send("I don't have permission to do that, fix it or fuck off.")
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
        ctx.send("$status ${user.asMention}.")
    }
}
