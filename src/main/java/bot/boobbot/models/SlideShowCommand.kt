package bot.boobbot.models

import bot.boobbot.BoobBot
import bot.boobbot.flight.AsyncCommand
import bot.boobbot.flight.Context
import bot.boobbot.misc.*
import net.dv8tion.jda.core.EmbedBuilder
import java.awt.Color


abstract class SlideShowCommand : AsyncCommand {

    override suspend fun executeAsync(ctx: Context) {
        if (ctx.args.isEmpty()) {
            return ctx.embed {
                setColor(Color.red)
                setDescription(Formats.error("Missing Args\nbbslideshow <type>\nTypes: boobs, ass, dick, gif"))
            }
        }

        val endpoint: String

        when (ctx.args[0].toLowerCase()) {
            "boobs" -> {
                endpoint = ctx.args[0]
            }
            "ass" -> {
                endpoint = ctx.args[0]
            }
            "dick" -> {
                endpoint = "penis"
            }
            "gif" -> {
                endpoint = "Gifs"
            }
            else -> {
                return ctx.embed {
                    setColor(Color.red)
                    setDescription(Formats.error("What?\nTypes: boobs, ass, dick, gif"))
                }
            }
        }
        val headers = createHeaders(Pair("Key", Constants.BB_API_KEY))

        var x = 0
        val msg = ctx.channel.sendMessage("\u200B").complete()
        while (x < 20) {
            val res = BoobBot.requestUtil.get("https://boob.bot/api/v2/img/$endpoint", headers).await()?.json()
                    ?: return ctx.send("\uD83D\uDEAB oh? something broken af")
            x++
            msg.editMessage("\u200B")
                    .complete()
                    .editMessage(EmbedBuilder()
                            .setDescription("$x of 20")
                            .setColor(Colors.getEffectiveColor(ctx.message)
                            ).setImage(res.getString("url"))
                            .build()).queue()
            Thread.sleep(5000)
        }
        ctx.message.delete().reason("no spam").queue(null, null)
        msg.delete().reason("no spam").queue(null, null)

    }
}
