package bot.boobbot.commands.mod

import bot.boobbot.flight.AsyncCommand
import bot.boobbot.flight.CommandProperties
import bot.boobbot.flight.Context
import bot.boobbot.misc.awaitSuppressed
import bot.boobbot.misc.thenException
import bot.boobbot.models.ModCommand
import net.dv8tion.jda.api.Permission
import net.dv8tion.jda.api.entities.Invite
import net.dv8tion.jda.api.exceptions.ErrorResponseException
import java.util.concurrent.CompletableFuture
import java.util.concurrent.TimeUnit

@CommandProperties(
    description = "Quickly ban+unban a user to clean their messages.",
    donorOnly = true,
    guildOnly = true
)
class SoftBan : AsyncCommand, ModCommand() {

    override suspend fun executeAsync(ctx: Context) {
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

        val invite = generateInvite(ctx).awaitSuppressed()
        val extraMsg = invite?.let { "\nHere is an invite, don't be a dick.\n${it.url}" } ?: ""
        val banMsg = """
            You have been soft-banned in **${ctx.guild!!.name}** by **${ctx.author.asTag}** for: $auditReason
            $extraMsg
        """.trimIndent()

        target.user.openPrivateChannel().submit()
            .thenCompose { it.sendMessage(banMsg).submit() }
            .thenCompose { it.privateChannel.close().submit() }
            .awaitSuppressed()

        target.ban(7, "Soft-banned by: ${ctx.author.name} [${ctx.author.idLong}] for: $auditReason")
            .submit()
            .thenCompose { ctx.guild.unban(target.id).submit() }
            .thenAccept { ctx.send("done") }
            .thenException {
                if (it is ErrorResponseException) {
                    ctx.send("what the fuck an error occurred while trying to soft-ban\n```\n${it.meaning}")
                } else {
                    ctx.send("what the fuck i couldn't soft-ban?")
                }
            }
    }

    fun generateInvite(ctx: Context): CompletableFuture<Invite?> {
        val fut = CompletableFuture<Invite?>()

        if (ctx.selfMember!!.hasPermission(ctx.guildChannel!!, Permission.CREATE_INSTANT_INVITE)) {
            ctx.guildChannel.createInvite().setMaxAge(1, TimeUnit.DAYS).setMaxUses(1)
                .submit()
                .thenAccept { fut.complete(it) }
                .exceptionally {
                    fut.complete(null)
                    return@exceptionally null
                }
        } else {
            fut.complete(null)
        }

        return fut
    }
}
