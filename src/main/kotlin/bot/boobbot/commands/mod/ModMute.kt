package bot.boobbot.commands.mod

import bot.boobbot.BoobBot
import bot.boobbot.flight.Category
import bot.boobbot.flight.CommandProperties
import bot.boobbot.flight.Context
import bot.boobbot.models.ModCommand

@CommandProperties(
    description = "Mute a Admin <:p_:475801484282429450>",
    donorOnly = true,
    guildOnly = true,
    aliases = ["mm"],
    category = Category.MOD
)
class ModMute : ModCommand() {

    override fun execute(ctx: Context) {
        val (target, reason) = resolveTargetAndReason(ctx)
        val auditReason = reason ?: "No reason was given"

        if (target == null) {
            return ctx.send("How in the fuck would i know who you want to mute if you don't give me a valid target?")
        }

        if (target.idLong == ctx.author.idLong) {
            return ctx.send("You must be special if you're really trying to mute yourself.")
        }

        if (!ctx.member!!.canInteract(target)) {
            return ctx.send("You dont have permission to do that, fuck off")
        }

        if (target.idLong == ctx.selfMember!!.idLong) {
            return ctx.send("Don't you fucking touch me whore, I will end you.")
        }

        if (!ctx.selfMember.canInteract(target)) {
            return ctx.send("I dont have permission to do that, Fix it or fuck off")
        }
        val g = BoobBot.database.getGuild(ctx.guild!!.id)
        val isMuted = g.modMute.contains(target.id)
        val status = if (isMuted) "Un-muted" else "Muted"

        if (isMuted) {
            g.modMute.remove(target.id)
        } else {
            g.modMute.add(target.id)
        }
        g.save()
        ctx.send("$status ${target.asMention}.")
    }
}
