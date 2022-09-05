package bot.boobbot.commands.mod

import bot.boobbot.entities.framework.Category
import bot.boobbot.entities.framework.CommandProperties
import bot.boobbot.entities.framework.Context
import bot.boobbot.entities.framework.ModCommand
import net.dv8tion.jda.api.Permission

@CommandProperties(
    description = "Boot an asshat from the server.",
    donorOnly = true,
    guildOnly = true,
    category = Category.MOD,
    userPermissions = [Permission.KICK_MEMBERS],
    botPermissions = [Permission.KICK_MEMBERS]
)
class Kick : ModCommand() {
    override fun execute(ctx: Context) {
        val (member, user, reason) = resolveTargetAndReason(ctx)
        val auditReason = reason ?: "No reason was given"

        if (user == null) {
            return ctx.send("How in the fuck would i know who you want to kick if you don't give me a valid target?")
        }

        if (user.idLong == ctx.author.idLong) {
            return ctx.send("You must be special if you're really trying to kick yourself.")
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

        ctx.guild!!.kick(user, "Kicked by: ${ctx.author.name} [${ctx.author.idLong}] for: $auditReason")
            .queue(
                { ctx.send("done, good riddance stupid bitch") },
                { ctx.send("what the fuck i couldn't kick?") }
            )
    }
}
