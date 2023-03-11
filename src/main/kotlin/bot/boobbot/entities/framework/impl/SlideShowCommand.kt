package bot.boobbot.entities.framework.impl

import bot.boobbot.BoobBot
import bot.boobbot.entities.framework.Context
import bot.boobbot.entities.framework.interfaces.Command
import bot.boobbot.utils.*
import net.dv8tion.jda.api.EmbedBuilder
import net.dv8tion.jda.api.Permission
import net.dv8tion.jda.api.entities.Message
import net.dv8tion.jda.api.utils.messages.MessageCreateBuilder
import net.dv8tion.jda.api.utils.messages.MessageEditBuilder
import okhttp3.Headers.Companion.headersOf
import java.awt.Color
import java.util.concurrent.TimeUnit


abstract class SlideShowCommand : Command {

    companion object {
        private val headers = headersOf("Key", BoobBot.config.BB_API_KEY)
        private val allowedEndpoints = arrayOf("boobs", "ass", "penis", "Gifs", "gay", "tiny", "cumsluts", "collared", "yiff", "tentacle", "thicc", "red")
        private val endpointStr = allowedEndpoints.joinToString(", ")
    }

    override fun execute(ctx: Context) {
        val category = ctx.options.getByNameOrNext("category", Resolver.STRING)?.takeIf { it in allowedEndpoints }
            ?: return ctx.reply {
                setColor(Color.red)
                setDescription(Formats.error("Missing Args\n/slideshow <category>\nTypes: $endpointStr"))
            }

        val color = Colors.getEffectiveColor(ctx.member)

        BoobBot.requestUtil.get("https://boob.bot/api/v2/img/$category?count=20", headers).queue { res ->
            val json = res?.json()
                ?: return@queue ctx.reply(Formats.error(" oh? something broken af"))

            val (first, images) = json.getJSONArray("urls").map { it.toString() }.separate()

            ctx.reply("Your slideshow should appear soon, whore.", ephemeral = true)

            ctx.channel.sendMessage(embedWith(1, first, color))
                .delay(5, TimeUnit.SECONDS)
                .intersect(images) { m, i, e ->
                    m.editMessage(embedEdit(i + 2, e, color))
                        .delay(5, TimeUnit.SECONDS)
                }
                .flatMap(Message::delete)
                .queue()
        }
    }

    private fun embedWith(num: Int, url: String, color: Color) = MessageCreateBuilder().addEmbeds(EmbedBuilder()
        .apply {
            setColor(color)
            setDescription("$num of 20")
            setImage(url)
        }
        .build()).build()


    private fun embedEdit(num: Int, url: String, color: Color) = MessageEditBuilder().setEmbeds(EmbedBuilder()
        .apply {
            setColor(color)
            setDescription("$num of 20")
            setImage(url)
        }
        .build()).build()
}
