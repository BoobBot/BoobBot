package bot.boobbot.commands.bot

import bot.boobbot.BoobBot
import bot.boobbot.flight.Command
import bot.boobbot.flight.CommandProperties
import bot.boobbot.flight.Context

@CommandProperties(description = "Receive your rewards after pledging on Patreon")
class Perks : Command {

    override fun execute(ctx: Context) {
        ctx.send("Searching for your pledge. This could take up to 30 seconds.")

        BoobBot.pApi.fetchPledgesOfCampaign("1928035").thenAccept {
            if (it.isEmpty()) {
                return@thenAccept ctx.send("Suspicious response from Patreon API. Report this to the devs.")
            }

            val pledge = it.firstOrNull { u -> u.discordId != null && u.discordId == ctx.author.idLong }
                ?: return@thenAccept ctx.send("Unable to find your pledge. Make sure your Discord account is linked to your Patreon account.")

            if (pledge.isDeclined) {
                return@thenAccept ctx.send("Your payment was declined by Patreon. Please fix this issue before attempting to claim your perks.")
            }

            val pledgeAmount = pledge.pledgeCents.toDouble() / 100
            val pledgeFriendly = String.format("%1$,.2f", pledgeAmount)
            ctx.send(
                "Thanks for your donation, **${ctx.author.asTag}**! Your tier: `${BoobBot.pApi.getDonorType(
                    pledgeAmount
                )}` ($$pledgeFriendly)"
            )

            BoobBot.database.setDonor(ctx.author.id, pledgeAmount)
        }
    }

}