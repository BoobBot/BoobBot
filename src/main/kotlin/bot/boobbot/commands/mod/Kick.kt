package bot.boobbot.commands.mod

import bot.boobbot.entities.framework.Category
import bot.boobbot.entities.framework.Context
import bot.boobbot.entities.framework.annotations.CommandProperties
import bot.boobbot.entities.framework.annotations.Option
import bot.boobbot.entities.framework.impl.ModCommand
import net.dv8tion.jda.api.Permission
import net.dv8tion.jda.api.interactions.commands.OptionType

@CommandProperties(
    description = "Boot an asshat from the server.",
    donorOnly = true,
    guildOnly = true,
    category = Category.MOD,
    userPermissions = [Permission.KICK_MEMBERS],
    botPermissions = [Permission.KICK_MEMBERS],
    groupByCategory = true
)
@Option(name = "target", description = "The user to ban.", type = OptionType.USER)
@Option(name = "reason", description = "The reason for the action.", required = false)
class Kick : ModCommand() {
    override fun execute(ctx: Context) {
        val (user, member, reason) = resolveTargetAndReason(ctx)
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

        ctx.guild.kick(user)
            .reason("Kicked by: ${ctx.user.name} [${ctx.user.idLong}] for: $auditReason")
            .queue(
                { ctx.reply("kicked **${user.name}**, good riddance stupid bitch") },
                { ctx.reply("what the fuck i couldn't kick?") }
            )
    }
}
