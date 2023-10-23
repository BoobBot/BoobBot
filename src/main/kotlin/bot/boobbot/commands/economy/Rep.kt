package bot.boobbot.commands.economy

import bot.boobbot.BoobBot
import bot.boobbot.entities.framework.Category
import bot.boobbot.entities.framework.Context
import bot.boobbot.entities.framework.MessageContext
import bot.boobbot.entities.framework.annotations.CommandProperties
import bot.boobbot.entities.framework.annotations.Option
import bot.boobbot.entities.framework.impl.Resolver
import bot.boobbot.entities.framework.interfaces.Command
import bot.boobbot.utils.Formats
import bot.boobbot.utils.Formats.getRemainingCoolDown
import net.dv8tion.jda.api.interactions.commands.OptionType
import java.awt.Color
import java.time.Instant
import java.time.temporal.ChronoUnit

@CommandProperties(aliases = ["+"], description = "Give rep.", category = Category.ECONOMY, groupByCategory = true)
@Option(name = "user", description = "The user to give rep to.", type = OptionType.USER)
class Rep : Command {

    override fun execute(ctx: Context) {
        val target = ctx.options.getByNameOrNext("user", Resolver.CONTEXT_AWARE_USER(ctx))
            ?: return ctx.reply {
                setColor(Color.red)
                setDescription(Formats.error("you didn't mention a @user, dumbass.\n"))
            }

        if (target.idLong == BoobBot.selfId) {
            return ctx.reply("Don't you fucking touch me whore, I will end you.")
        }

        if (target.idLong == ctx.user.idLong) {
            return ctx.reply("aww how sad you wanna rep yourself, well fucking don't. Go find a friend whore.")
        }

        val author = BoobBot.database.getUser(ctx.user.id)
        val now = Instant.now()
        val lastRep = author.lastRep

        if (lastRep != null) {
            val t = lastRep.plus(12, ChronoUnit.HOURS)
            val x = t.toEpochMilli() - now.toEpochMilli()
            if (!t.isBefore(now)) {
                return ctx.reply("You already gave rep today whore.\nFuck off and try again in ${getRemainingCoolDown(x)}")
            }
        }

        author.lastRep = now
        author.save()

        BoobBot.database.getUser(target.id)
            .apply { rep += 1 }
            .save()

        ctx.reply("You gave **${target.name}** a rep point, Good job! Seems you're not completely useless after all.")
    }

}
