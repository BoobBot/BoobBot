package bot.boobbot.commands.bot

import bot.boobbot.BoobBot
import bot.boobbot.entities.framework.Command
import bot.boobbot.entities.framework.CommandProperties
import bot.boobbot.entities.framework.Context
import bot.boobbot.utils.Colors
import java.time.Instant

@CommandProperties(description = "Gaming ", aliases = ["sigma"])
class Games : Command {

    override fun execute(ctx: Context) {
        val requester = BoobBot.shardManager.authorOrAnonymous(ctx)

        ctx.embed {
            setColor(Colors.rndColor)
            setAuthor(ctx.selfUser.name, ctx.selfUser.effectiveAvatarUrl, ctx.selfUser.effectiveAvatarUrl)
            setDescription("Sigma Draconis Gaming")
            addField("Discord", "https://discord.gg/vbgJvMn", false)
            addField(
                "Space Engineers", "Epsilon Draconis - 63.141.225.67:27017\n" +
                        "Delta Draconis - 63.141.225.67:27016\n" +
                        "Omicron Draconis - 107.150.47.52:27016\n" +
                        "Gamma Draconis - 107.150.47.52:27017\n" +
                        "Sigma Draconis - 107.150.38.60:27016\n" +
                        "Tau Draconis - 107.150.38.60:27017", false
            )

            addField(
                "Ark", "The island - 199.15.253.66:8030\n" +
                        "Aberration - 199.15.253.66:7500\n" +
                        "Scorched-Earth - 199.15.253.66:7530\n" +
                        "Extinction - 199.15.253.66:7640\n" +
                        "The Volcano - 199.15.253.66:8150\n" +
                        "Ragnarok - 199.15.253.66:8180", false
            )

            addField("7 Days 2 Die", "Server IP - 199.15.253.138:7490", false)

            setFooter("Requested by ${requester.name}", requester.effectiveAvatarUrl)
            setTimestamp(Instant.now())
        }
    }

}