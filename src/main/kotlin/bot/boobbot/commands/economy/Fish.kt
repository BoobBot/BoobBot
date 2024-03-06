package bot.boobbot.commands.economy

import bot.boobbot.BoobBot
import bot.boobbot.entities.framework.Category
import bot.boobbot.entities.framework.Context
import bot.boobbot.entities.framework.annotations.Choice
import bot.boobbot.entities.framework.annotations.CommandProperties
import bot.boobbot.entities.framework.annotations.Option
import bot.boobbot.entities.framework.impl.Resolver
import bot.boobbot.entities.framework.interfaces.Command
import bot.boobbot.utils.Formats
import net.dv8tion.jda.api.interactions.commands.OptionType

@CommandProperties(
    description = "Go fishing and catch some fish.",
    aliases = ["fish"],
    category = Category.ECONOMY,
    groupByCategory = true
)
@Option(name = "bait", description = "Type of bait to use.", choices = [Choice("Worm", "worm"), Choice("Lure", "lure")])
@Option(name = "attempts", description = "Number of fishing attempts.", type = OptionType.INTEGER)
class Fish : Command {

    private val fishEmotes = mapOf(
        "common" to "🐟",
        "uncommon" to "🦐",
        "rare" to "🐠"
    )

    private val fishValues = mapOf(
        "common" to 20,
        "uncommon" to 50,
        "rare" to 100
    )

    override fun execute(ctx: Context) {
        val bait = ctx.options.getByNameOrNext("bait", Resolver.STRING)?.lowercase()
            ?: return ctx.reply(Formats.error("Pick `worm` or `lure`, whore."))
        val attempts = ctx.options.getByNameOrNext("attempts", Resolver.INTEGER) ?: 1

        if (bait !in listOf("worm", "lure")) {
            return ctx.reply(Formats.error("Invalid bait type. Choose either `worm` or `lure`."))
        }

        if (attempts < 1 || attempts > 10) {
            return ctx.reply(Formats.error("You can only fish between 1 and 10 times at a time."))
        }

        val u = BoobBot.database.getUser(ctx.user.id)

        val baitCost = if (bait == "worm") 10 else 20
        val totalCost = baitCost * attempts

        if (totalCost > u.balance) {
            return ctx.reply(Formats.error("You don't have enough money to go fishing with this bait."))
        }

        u.balance -= totalCost

        val results = (1..attempts).map { fishAttempt(bait) }

        val totalFishValue = results.sumOf { fishValues[it] ?: 0 }

        u.balance += totalFishValue

        u.save()

        val formattedResults = results.joinToString(" ") { fishEmotes[it] ?: "❓" }
        ctx.message { content(Formats.info("Fishing Results:\n$formattedResults\nTotal Fish Value: $$totalFishValue")) }
    }

    private fun fishAttempt(bait: String): String {
        val rng = (0..99).random()
        val fishType = when {
            rng < 70 -> "common"
            rng < 90 -> "uncommon"
            else -> "rare"
        }
        return fishType
    }
}