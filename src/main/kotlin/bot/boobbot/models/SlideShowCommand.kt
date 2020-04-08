package bot.boobbot.models

import bot.boobbot.BoobBot
import bot.boobbot.flight.Command
import bot.boobbot.flight.Context
import bot.boobbot.misc.*
import net.dv8tion.jda.api.EmbedBuilder
import net.dv8tion.jda.api.Permission
import net.dv8tion.jda.api.entities.Message
import okhttp3.Headers
import java.awt.Color
import java.util.concurrent.TimeUnit


abstract class SlideShowCommand : Command {

    private val headers = Headers.of("Key", BoobBot.config.bbApiKey)

    private val aliases = mapOf(
        "dick" to "penis",
        "gif" to "Gifs",
        "aly" to "tentacle"
    )
    private val allowedEndpoints =
        arrayOf("boobs", "ass", "dick", "gif", "gay", "tiny", "cumsluts", "collared", "yiff", "aly")
    private val endpointStr = allowedEndpoints.joinToString(", ")

    override fun execute(ctx: Context) {
        if (ctx.args.isEmpty() || !allowedEndpoints.contains(ctx.args[0].toLowerCase())) {
            return ctx.embed {
                setColor(Color.red)
                setDescription(Formats.error("Missing Args\nbbslideshow <type>\nTypes: $endpointStr"))
            }
        }

        if (ctx.guild != null && ctx.botCan(Permission.MESSAGE_MANAGE)) {
            ctx.message.delete().queue(null, {})
        }

        val query = ctx.args[0].toLowerCase()
        val endpoint = aliases[query] ?: query
        val color = Colors.getEffectiveColor(ctx.message)

        BoobBot.requestUtil.get("https://boob.bot/api/v2/img/$endpoint?count=20", headers).queue { res ->
            val json = res?.json()
                ?: return@queue ctx.send(Formats.error(" oh? something broken af"))

            val (first, images) = json.getJSONArray("urls").map { it.toString() }.separate()

            ctx.channel.sendMessage(embedWith(1, first, color))
                .delay(5, TimeUnit.SECONDS)
                .intersect(images) { m, i, e ->
                    m.editMessage(embedWith(i + 2, e, color))
                        .delay(5, TimeUnit.SECONDS)
                }
                .flatMap(Message::delete)
                .queue()
        }
    }

    private fun embedWith(num: Int, url: String, color: Color) = EmbedBuilder()
        .apply {
            setColor(color)
            setDescription("$num of 20")
            setImage(url)
        }
        .build()
}
