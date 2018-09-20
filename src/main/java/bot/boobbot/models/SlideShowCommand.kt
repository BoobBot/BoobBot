package bot.boobbot.models

import bot.boobbot.BoobBot
import bot.boobbot.flight.AsyncCommand
import bot.boobbot.flight.Context
import bot.boobbot.misc.*
import kotlinx.coroutines.experimental.delay
import kotlinx.coroutines.experimental.future.await
import net.dv8tion.jda.core.EmbedBuilder
import net.dv8tion.jda.core.entities.Message
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

        val color = Colors.getEffectiveColor(ctx.message)
        val msg = ctx.channel.sendMessage("\u200B").submit().await()

        // .submit().await() - Asynchronous, doesn't suppress errors
        // .await()          - Asynchronous, suppresses any errors from Discord/JDA

        for (i in 1 until 21) { // 1-20
            val res = BoobBot.requestUtil.get("https://boob.bot/api/v2/img/$endpoint", headers).await()?.json()
                    ?: return ctx.send("\uD83D\uDEAB oh? something broken af")

            editMessage(msg, res.getString("url"), i, color)
            delay(5000)
        }

        ctx.message.delete().reason("no spam").submit()
        msg.delete().reason("no spam").submit()
    }

    private suspend fun editMessage(m: Message, url: String, num: Int, color: Color) {
        m.editMessage(EmbedBuilder()
                .setDescription("$num of 20")
                .setColor(color)
                .setImage(url)
                .build()
        ).await()
    }
}
