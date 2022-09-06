package bot.boobbot.commands.economy

import bot.boobbot.BoobBot
import bot.boobbot.entities.framework.Category
import bot.boobbot.entities.framework.interfaces.Command
import bot.boobbot.entities.framework.annotations.CommandProperties
import bot.boobbot.entities.framework.MessageContext
import bot.boobbot.utils.Colors
import bot.boobbot.utils.Formats


@CommandProperties(description = "See your current rank info.", aliases = ["level", "lvl"], category = Category.ECONOMY)
class Rank : Command {

    override fun execute(ctx: MessageContext) {
        val user = ctx.mentions.firstOrNull() ?: ctx.user
        val u = BoobBot.database.getUser(user.id)

        ctx.reply {
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
