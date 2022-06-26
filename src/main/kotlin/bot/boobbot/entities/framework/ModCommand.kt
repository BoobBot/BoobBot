package bot.boobbot.entities.framework

import net.dv8tion.jda.api.entities.IMentionable
import net.dv8tion.jda.api.entities.Member
import net.dv8tion.jda.api.entities.User
import java.util.regex.Pattern

abstract class ModCommand : Command {
    fun resolveTargetAndReason(ctx: Context): Resolved {
        val userArgument = ctx.args.take(1)
        val reasonArgument = ctx.args.drop(1)

        if (userArgument.isEmpty()) {
            return Resolved(null, null, false)
        }

        val reason = if (reasonArgument.isEmpty()) null else reasonArgument.joinToString(" ")
        val target = ctx.message.mentions.members.firstOrNull()
            ?: userArgument.first().matchGroup(snowflakePattern)?.let(User::fromId)
            ?: return Resolved(null, null, false)

        return Resolved(target, reason, true)
    }

    class Resolved(target: IMentionable?, val actionReason: String?, val targetResolved: Boolean) {
        var member: Member? = null
        lateinit var user: User

        init {
            when (target) {
                is Member -> {
                    member = target
                    user = target.user
                }
                is User -> {
                    user = target
                }
            }
        }

        operator fun component1() = member
        operator fun component2() = user
        operator fun component3() = actionReason
        operator fun component4() = targetResolved
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
