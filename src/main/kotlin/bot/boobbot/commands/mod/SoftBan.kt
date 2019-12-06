package bot.boobbot.commands.mod

import bot.boobbot.flight.CommandProperties
import bot.boobbot.flight.Context
import bot.boobbot.models.ModCommand
import net.dv8tion.jda.api.Permission
import java.util.concurrent.TimeUnit

@CommandProperties(
    description = "quickly ban-unban a user to clean there messages ",
    donorOnly = true,
    guildOnly = true
)
class SoftBan : ModCommand() {


    override fun execute(ctx: Context) {
        val (target, reason) = resolveTargetAndReason(ctx)
        val auditReason = reason ?: "No reason was given"

        if (target == null) {
            return ctx.send("How in the fuck would i know who you want to ban if you don't give me a valid target?")
        }

        if (target.idLong == ctx.author.idLong) {
            return ctx.send("You must be special if you're really trying to soft-ban yourself.")
        }

        if (!ctx.member!!.canInteract(target)) {
            return ctx.send("You don't have permission to do that, fuck off")
        }

        if (target.idLong == ctx.selfMember!!.idLong) {
            return ctx.send("Don't you fucking touch me whore, I will end you.")
        }

        if (!ctx.selfMember.canInteract(target)) {
            return ctx.send("I don't have permission to do that, Fix it or fuck off")
        }

        if (ctx.selfMember.hasPermission(Permission.CREATE_INSTANT_INVITE)) {
            ctx.guildChannel!!.createInvite().setMaxAge(1, TimeUnit.DAYS).setMaxUses(1).queue { invite ->
                val url = invite.url
                target.user.openPrivateChannel().queue { privateChannel ->
                    privateChannel.sendMessage(
                        "You have been soft-banned in ${ctx.guild!!.name} " +
                                "for: $auditReason \nHere is a invite, don't be a dick.\n${url}"
                    )

                        .queue()
                }
            }
        }

        target.ban(7, "Soft-banned by: ${ctx.author.name} [${ctx.author.idLong}] for: $auditReason")
            .queue(
                {
                    ctx.send("done.")
               },
               { ctx.send("what the fuck i couldn't ban?") })
        ctx.guild!!.unban(target.user).queue()
    }
}
