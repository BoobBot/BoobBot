package bot.boobbot.entities.framework.impl

import bot.boobbot.entities.framework.Context
import bot.boobbot.entities.framework.interfaces.Command
import net.dv8tion.jda.api.entities.Member
import net.dv8tion.jda.api.entities.User
import java.util.regex.Pattern

abstract class ModCommand : Command {
    fun resolveTargetAndReason(ctx: Context): Resolved {
        val user = ctx.options.getByNameOrNext("target", Resolver.USER)
        val reason = ctx.options.getOptionStringOrGather("reason")

        if (user == null) {
            return Resolved.EMPTY
        }

        return Resolved(user, ctx.guild.getMemberById(user.idLong), reason)
    }

    class Resolved(val user: User?, val member: Member?, val actionReason: String?) {
        operator fun component1() = user
        operator fun component2() = member
        operator fun component3() = actionReason

        companion object {
            val EMPTY = Resolved(null, null, null)
        }
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
