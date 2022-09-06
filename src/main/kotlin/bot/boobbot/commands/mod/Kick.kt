package bot.boobbot.commands.mod

import bot.boobbot.entities.framework.Category
import bot.boobbot.entities.framework.annotations.CommandProperties
import bot.boobbot.entities.framework.MessageContext
import bot.boobbot.entities.framework.impl.ModCommand
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
    override fun execute(ctx: MessageContext) {
        val (member, user, reason) = resolveTargetAndReason(ctx)
        val auditReason = reason ?: "No reason was given"

        if (user == null) {
            return ctx.reply("How in the fuck would i know who you want to kick if you don't give me a valid target?")
        }

        if (user.idLong == ctx.user.idLong) {
            return ctx.reply("You must be special if you're really trying to kick yourself.")
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

        ctx.guild!!.kick(user, "Kicked by: ${ctx.user.name} [${ctx.user.idLong}] for: $auditReason")
            .queue(
                { ctx.reply("done, good riddance stupid bitch") },
                { ctx.reply("what the fuck i couldn't kick?") }
            )
    }
}
