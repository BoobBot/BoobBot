package bot.boobbot.entities.framework.impl

import bot.boobbot.entities.framework.MessageContext
import bot.boobbot.entities.framework.interfaces.Command
import net.dv8tion.jda.api.entities.IMentionable
import net.dv8tion.jda.api.entities.Member
import net.dv8tion.jda.api.entities.User
import java.util.regex.Pattern

abstract class ModCommand : Command {
    fun resolveTargetAndReason(ctx: MessageContext): Resolved {
        val userArgument = ctx.args.take(1)
        val reasonArgument = ctx.args.drop(1)

        if (userArgument.isEmpty()) {
            return Resolved(null, null)
        }

        val reason = if (reasonArgument.isEmpty()) null else reasonArgument.joinToString(" ")
        val target = ctx.message.mentions.members.firstOrNull()
            ?: userArgument.first().matchGroup(snowflakePattern)?.let(User::fromId)
            ?: return Resolved(null, null)

        return Resolved(target, reason)
    }

    class Resolved(target: IMentionable?, val actionReason: String?) {
        val member: Member? = target as? Member
        val user: User? = target as? User ?: member?.user

        operator fun component1() = member
        operator fun component2() = user
        operator fun component3() = actionReason
    }

    companion object {
        internal fun String.matchGroup(pattern: Pattern, group: Int = 0): String? {
            val matcher = pattern.matcher(this)
            if (matcher.matches()) {
                return matcher.group(group)
            }
            return null
        }

        val snowflakePattern = "\\d{17,21}".toPattern()
    }
}
