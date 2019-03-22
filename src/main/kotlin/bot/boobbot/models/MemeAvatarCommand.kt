package bot.boobbot.models

import bot.boobbot.BoobBot
import bot.boobbot.flight.AsyncCommand
import bot.boobbot.flight.Context
import bot.boobbot.misc.createHeaders
import com.mewna.catnip.entity.message.MessageOptions
import okhttp3.HttpUrl

abstract class MemeAvatarCommand(private val category: String) : AsyncCommand {

    private val endpointUrl = "https://dankmemer.services/api/$category"
    private val urlBuilder
        get() = HttpUrl.parse(endpointUrl)!!.newBuilder()

    override suspend fun executeAsync(ctx: Context) {
        val headers = createHeaders(Pair("Authorization", BoobBot.config.memerImgenKey))
        val user = ctx.message.mentionedUsers().firstOrNull() ?: ctx.author
        val url = urlBuilder.addQueryParameter("avatar1", user.effectiveAvatarUrl()).build()

        val res = BoobBot.requestUtil.get(url.toString(), headers).await()?.body()
            ?: return ctx.send("rip some error press f")

        val opts = MessageOptions()
            .addFile("$category.png", res.byteStream())
            .content("\u200b")

        ctx.channel.sendMessage(opts)
    }

}
