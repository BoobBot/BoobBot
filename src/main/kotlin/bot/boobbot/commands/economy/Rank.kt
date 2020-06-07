package bot.boobbot.commands.economy

import bot.boobbot.BoobBot
import bot.boobbot.entities.framework.Category
import bot.boobbot.entities.framework.Command
import bot.boobbot.entities.framework.CommandProperties
import bot.boobbot.entities.framework.Context
import bot.boobbot.utils.Colors
import bot.boobbot.utils.Formats


@CommandProperties(description = "See your current rank info.", aliases = ["level", "lvl"], category = Category.ECONOMY)
class Rank : Command {

    override fun execute(ctx: Context) {
        val user = ctx.mentions.firstOrNull() ?: ctx.author
        val u = BoobBot.database.getUser(user.id)

        ctx.embed {
            setColor(Colors.getEffectiveColor(ctx.message))
            addField(
                Formats.info("**Rank Information**"),
                "**Current Rep**: ${u.rep}\n" +
                        "**Current Level**: ${u.level}\n" +
                        "**Current Lewd Level**: ${(u.lewdLevel)}",
                false
            )
        }
    }

}
