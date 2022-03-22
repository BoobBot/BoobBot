package bot.boobbot.commands.mod

import bot.boobbot.entities.framework.AsyncCommand
import bot.boobbot.entities.framework.Category
import bot.boobbot.entities.framework.CommandProperties
import bot.boobbot.entities.framework.Context
import bot.boobbot.utils.awaitSuppressed
import bot.boobbot.utils.thenException
import bot.boobbot.entities.framework.ModCommand
import net.dv8tion.jda.api.Permission
import net.dv8tion.jda.api.entities.Invite
import net.dv8tion.jda.api.exceptions.ErrorResponseException
import java.util.concurrent.CompletableFuture
import java.util.concurrent.TimeUnit

@CommandProperties(
    description = "Quickly ban+unban a user to clean their messages.",
    donorOnly = true,
    guildOnly = true,
    category = Category.MOD,
    userPermissions = [Permission.BAN_MEMBERS],
    botPermissions = [Permission.BAN_MEMBERS]
)
class SoftBan : AsyncCommand, ModCommand() {
    override suspend fun executeAsync(ctx: Context) {
        val (member, user, reason, resolved) = resolveTargetAndReason(ctx)
        val auditReason = reason ?: "No reason was given"

        if (!resolved) {
            return ctx.send("How in the fuck would i know who you want to ban if you don't give me a valid target?")
        }

        if (user.idLong == ctx.author.idLong) {
            return ctx.send("You must be special if you're really trying to ban yourself.")
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

        val invite = generateInvite(ctx).awaitSuppressed()
        val extraMsg = invite?.let { "\nHere is an invite, don't be a dick.\n${it.url}" } ?: ""
        val banMsg = """
            You have been soft-banned in **${ctx.guild!!.name}** by **${ctx.author.asTag}** for: $auditReason
            $extraMsg
        """.trimIndent()

        user.openPrivateChannel()
            .flatMap { it.sendMessage(banMsg) }
            .flatMap { it.privateChannel.delete() }
            .submit()
            .awaitSuppressed()

        ctx.guild.ban(user, 7, "Soft-banned by: ${ctx.author.name} [${ctx.author.idLong}] for: $auditReason")
            .flatMap { ctx.guild.unban(user.id) }
            .queue({ ctx.send("done") }, {
                if (it is ErrorResponseException) {
                    ctx.send("what the fuck an error occurred while trying to soft-ban\n```\n${it.meaning}```")
                } else {
                    ctx.send("what the fuck i couldn't soft-ban?")
                }
            })
    }

    fun generateInvite(ctx: Context): CompletableFuture<Invite?> {
        val fut = CompletableFuture<Invite?>()

        if (ctx.selfMember!!.hasPermission(ctx.guildChannel!!, Permission.CREATE_INSTANT_INVITE)) {
            ctx.textChannel!!.createInvite().setMaxAge(1, TimeUnit.DAYS).setMaxUses(1)
                .submit()
                .thenAccept(fut::complete)
                .thenException { fut.complete(null) }
        } else {
            fut.complete(null)
        }

        return fut
    }
}
