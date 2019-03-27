package bot.boobbot.models

import bot.boobbot.BoobBot
import bot.boobbot.flight.AsyncCommand
import bot.boobbot.flight.Context
import bot.boobbot.misc.Colors
import bot.boobbot.misc.Formats
import bot.boobbot.misc.createHeaders
import bot.boobbot.misc.json
import kotlinx.coroutines.delay
import kotlinx.coroutines.future.await
import net.dv8tion.jda.core.EmbedBuilder
import net.dv8tion.jda.core.entities.Message
import java.awt.Color


abstract class SlideShowCommand : AsyncCommand {

    private val headers = createHeaders(Pair("Key", BoobBot.config.bbApiKey))

    override suspend fun executeAsync(ctx: Context) {
        if (ctx.args.isEmpty()) {
            return ctx.embed {
                setColor(Color.red)
                setDescription(Formats.error("Missing Args\nbbslideshow <type>\nTypes: boobs, ass, dick, gif, gay, tiny, cumsluts, collared"))
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
                    setColor(Color.red)
                    setDescription(Formats.error("What?\nTypes: boobs, ass, dick, gif, gay, tiny, cumsluts, collared"))
                }
            }
        }

        val color = Colors.getEffectiveColor(ctx.message)
        val msg = ctx.sendAsync("\u200B")

        for (i in 1 until 21) { // 1-20
            val res = BoobBot.requestUtil.get("https://boob.bot/api/v2/img/$endpoint", headers).await()?.json()
                ?: return ctx.send("\uD83D\uDEAB oh? something broken af")

            editMessage(msg, res.getString("url"), i, color)
            delay(5000)
        }

        ctx.message.delete().queue()
        msg.delete().queue()
    }

    private suspend fun editMessage(m: Message, url: String, num: Int, color: Color) {
        m.editMessage(
            EmbedBuilder()
                .setColor(color)
                .setDescription("$num of 20")
                .setImage(url)
                .build()
        ).submit().await()
    }
}
