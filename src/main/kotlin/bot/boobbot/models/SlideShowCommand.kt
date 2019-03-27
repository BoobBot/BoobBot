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
import net.dv8tion.jda.core.Permission
import net.dv8tion.jda.core.entities.Message
import java.awt.Color


abstract class SlideShowCommand : AsyncCommand {

    private val headers = createHeaders(Pair("Key", BoobBot.config.bbApiKey))

    private val aliases = mapOf(
        "dick" to "penis",
        "gif" to "Gifs"
    )
    private val allowedEndpoints = arrayOf("boobs", "ass", "dick", "gif", "gay", "tiny", "cumsluts", "collared")
    private val endpointStr = allowedEndpoints.joinToString(", ")

    override suspend fun executeAsync(ctx: Context) {
        if (ctx.args.isEmpty() || !allowedEndpoints.contains(ctx.args[0].toLowerCase())) {
            return ctx.embed {
                setColor(Color.red)
                setDescription(Formats.error("Missing Args\nbbslideshow <type>\nTypes: boobs, ass, dick, gif, gay, tiny, cumsluts, collared"))
            }
        }

        val query = ctx.args[0].toLowerCase()
        val endpoint = aliases[query] ?: query

        val color = Colors.getEffectiveColor(ctx.message)
        val msg = ctx.sendAsync("\u200B")

        for (i in 1 until 21) { // 1-20
            val res = BoobBot.requestUtil.get("https://boob.bot/api/v2/img/$endpoint", headers).await()?.json()
                ?: return ctx.send("\uD83D\uDEAB oh? something broken af")

            editMessage(msg, res.getString("url"), i, color)
            delay(5000)
        }

        if (ctx.botCan(Permission.MESSAGE_MANAGE)) {
            ctx.message.delete().queue()
        }

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
