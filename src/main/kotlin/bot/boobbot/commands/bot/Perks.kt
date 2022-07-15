package bot.boobbot.commands.bot

import bot.boobbot.BoobBot
import bot.boobbot.entities.framework.Command
import bot.boobbot.entities.framework.CommandProperties
import bot.boobbot.entities.framework.Context

@CommandProperties(description = "Receive your rewards after subscribing on Patreon.")
class Perks : Command {

    override fun execute(ctx: Context) {
        ctx.send("Searching for your subscription. This could take up to 30 seconds.")

        BoobBot.pApi.fetchPledgesOfCampaign("1928035").thenAccept {
            if (it.isEmpty()) {
                return@thenAccept ctx.send("Patreon API returned an invalid response. Report this to https://discord.gg/bra.")
            }

            val pledge = it.firstOrNull { u -> u.discordId != null && u.discordId == ctx.author.idLong }
                ?: return@thenAccept ctx.send("Unable to find your subscription. Make sure your Discord account is linked to your Patreon account, whore.")

            if (pledge.isDeclined) {
                return@thenAccept ctx.send("Your payment appears to have been declined. Fix it before attempting to claim your perks, whore.")
            }

            val pledgeAmount = pledge.pledgeCents.toDouble() / 100
            val pledgeFriendly = String.format("%1$,.2f", pledgeAmount)
            ctx.reply("Welcome to the club, whore. Your tier: `${BoobBot.pApi.getDonorType(pledgeAmount)}` ($$pledgeFriendly)")
            BoobBot.database.setDonor(ctx.author.id, pledgeAmount)
        }
    }

}
