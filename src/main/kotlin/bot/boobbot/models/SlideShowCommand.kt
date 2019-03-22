package bot.boobbot.models

import bot.boobbot.BoobBot
import bot.boobbot.flight.AsyncCommand
import bot.boobbot.flight.Context
import bot.boobbot.misc.Colors
import bot.boobbot.misc.Formats
import bot.boobbot.misc.createHeaders
import bot.boobbot.misc.json
import com.mewna.catnip.entity.builder.EmbedBuilder
import com.mewna.catnip.entity.message.Message
import kotlinx.coroutines.delay
import kotlinx.coroutines.future.await
import java.awt.Color


abstract class SlideShowCommand : AsyncCommand {

    override suspend fun executeAsync(ctx: Context) {
        if (ctx.args.isEmpty()) {
            return ctx.embed {
                color(Color.red)
                description(Formats.error("Missing Args\nbbslideshow <type>\nTypes: boobs, ass, dick, gif, gay, tiny, cumsluts, collared"))
            }
        }

        val endpoint: String

        when (ctx.args[0].toLowerCase()) {
            "boobs" -> {
                endpoint = ctx.args[0]
            }
            "gay" -> {
                endpoint = ctx.args[0]
            }
            "tiny" -> {
                endpoint = ctx.args[0]
            }
            "cumsluts" -> {
                endpoint = ctx.args[0]
            }
            "collared" -> {
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
                    color(Color.red)
                    description(Formats.error("What?\nTypes: boobs, ass, dick, gif, gay, tiny, cumsluts, collared"))
                }
            }
        }
        val headers = createHeaders(Pair("Key", BoobBot.config.bbApiKey))

        val color = Colors.getEffectiveColor(ctx.message)
        val msg = ctx.sendAsync("\u200B")

        for (i in 1 until 21) { // 1-20
            val res = BoobBot.requestUtil.get("https://boob.bot/api/v2/img/$endpoint", headers).await()?.json()
                ?: return ctx.send("\uD83D\uDEAB oh? something broken af")

            editMessage(msg, res.getString("url"), i, color)
            delay(5000)
        }

        ctx.message.delete("No spam")
        msg.delete("No spam")
    }

    private suspend fun editMessage(m: Message, url: String, num: Int, color: Color) {
        m.edit(
            EmbedBuilder()
                .description("$num of 20")
                .color(color)
                .image(url)
                .build()
        ).await()
    }
}
