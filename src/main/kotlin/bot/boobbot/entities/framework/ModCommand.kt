package bot.boobbot.entities.framework

import net.dv8tion.jda.api.entities.Member

abstract class ModCommand : Command {
    fun resolveTargetAndReason(ctx: Context): Pair<Member?, String?> {
        val userArgument = ctx.args.take(1)
        val reasonArgument = ctx.args.drop(1)

        if (userArgument.isEmpty()) {
            return Pair(null, null)
        }

        val target = ctx.message.mentionedMembers.firstOrNull()
            ?: return Pair(null, null)
        val reason = if (reasonArgument.isEmpty()) null else reasonArgument.joinToString(" ")

        return Pair(target, reason)
    }
}
