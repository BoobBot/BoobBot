package bot.boobbot.commands.mod

import bot.boobbot.entities.framework.*
import bot.boobbot.entities.framework.annotations.CommandProperties
import bot.boobbot.entities.framework.annotations.Option
import bot.boobbot.entities.framework.annotations.Options
import bot.boobbot.entities.framework.impl.ModCommand
import bot.boobbot.entities.framework.interfaces.AsyncCommand
import bot.boobbot.utils.awaitSuppressed
import bot.boobbot.utils.thenException
import net.dv8tion.jda.api.Permission
import net.dv8tion.jda.api.entities.Invite
import net.dv8tion.jda.api.exceptions.ErrorResponseException
import net.dv8tion.jda.api.interactions.commands.OptionType
import java.util.concurrent.CompletableFuture
import java.util.concurrent.TimeUnit

@CommandProperties(
    description = "Quickly ban+unban a user to clean their messages.",
    donorOnly = true,
    guildOnly = true,
    category = Category.MOD,
    userPermissions = [Permission.BAN_MEMBERS],
    botPermissions = [Permission.BAN_MEMBERS],
    groupByCategory = true
)
@Options([ // TODO: Revisit
    Option(name = "target", description = "The user to ban.", type = OptionType.USER),
    Option(name = "reason", description = "The reason for the action.", required = false)
])
class SoftBan : AsyncCommand, ModCommand() {
    override suspend fun executeAsync(ctx: Context) {
        val (user, member, reason) = resolveTargetAndReason(ctx)
        val auditReason = reason ?: "No reason was given"

        if (user == null) {
            return ctx.reply("How in the fuck would i know who you want to ban if you don't give me a valid target?")
        }

        if (user.idLong == ctx.user.idLong) {
            return ctx.reply("You must be special if you're really trying to ban yourself.")
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

        val invite = generateInvite(ctx).awaitSuppressed()
        val extraMsg = invite?.let { "\nHere is an invite, don't be a dick.\n${it.url}" } ?: ""
        val banMsg = """
            You have been soft-banned in **${ctx.guild.name}** by **${ctx.user.asTag}** for: $auditReason
            $extraMsg
        """.trimIndent()

        user.openPrivateChannel()
            .flatMap { it.sendMessage(banMsg) }
            .flatMap { it.channel.delete() }
            .submit()
            .awaitSuppressed()

        ctx.guild.ban(user, 7, TimeUnit.DAYS)
            .reason("Soft-banned by: ${ctx.user.name} [${ctx.user.idLong}] for: $auditReason")
            .flatMap { ctx.guild.unban(user) }
            .queue({ ctx.reply("done") }, {
                if (it is ErrorResponseException) {
                    ctx.reply("what the fuck an error occurred while trying to soft-ban\n```\n${it.meaning}```")
                } else {
                    ctx.reply("what the fuck i couldn't soft-ban?")
                }
            })
    }

    fun generateInvite(ctx: Context): CompletableFuture<Invite?> {
        val fut = CompletableFuture<Invite?>()
        val targetChannel = ctx.guild.textChannelCache.firstOrNull { ctx.selfMember!!.hasPermission(it, Permission.CREATE_INSTANT_INVITE) }

        if (targetChannel != null) {
            targetChannel.createInvite()
                .setMaxAge(1, TimeUnit.DAYS)
                .setMaxUses(1)
                .submit()
                .thenAccept(fut::complete)
                .thenException { fut.complete(null) }
        } else {
            fut.complete(null)
        }

        return fut
    }
}
