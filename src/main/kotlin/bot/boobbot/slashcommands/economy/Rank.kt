package bot.boobbot.slashcommands.economy

import bot.boobbot.BoobBot
import bot.boobbot.entities.framework.Category
import bot.boobbot.entities.framework.annotations.CommandProperties
import bot.boobbot.entities.framework.SlashCommand
import bot.boobbot.entities.framework.SlashContext
import bot.boobbot.utils.Colors
import bot.boobbot.utils.Formats

@CommandProperties(description = "See your current rank info.", aliases = ["level", "lvl"], category = Category.ECONOMY)
class Rank : SlashCommand {

    override fun execute(ctx: SlashContext) {
        val user = ctx.getOption("member")?.asUser ?: ctx.user
        val u = BoobBot.database.getUser(user.id)

        ctx.reply {
            setColor(Colors.rndColor)
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
