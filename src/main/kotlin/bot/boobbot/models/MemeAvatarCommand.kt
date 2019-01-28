package bot.boobbot.models

import bot.boobbot.BoobBot
import bot.boobbot.flight.AsyncCommand
import bot.boobbot.flight.Context
import bot.boobbot.misc.Constants
import bot.boobbot.misc.createHeaders
import okhttp3.HttpUrl

abstract class MemeAvatarCommand(private val category: String) : AsyncCommand {

    private val filename = "$category.png"
    private val endpointUrl = "https://dankmemer.services/api/$category"
    private val httpUrl = HttpUrl.parse(endpointUrl)!!

    private val urlBuilder
        get() = httpUrl.newBuilder()

    override suspend fun executeAsync(ctx: Context) {
        val headers = createHeaders(Pair("Authorization", Constants.MEMER_IMGEN_KEY))
        val user = ctx.message.mentionedUsers.firstOrNull() ?: ctx.author
        val url = urlBuilder.addQueryParameter("avatar1", user.effectiveAvatarUrl).build()

        val res = BoobBot.requestUtil.get(url.toString(), headers).await()?.body()
            ?: return ctx.send("rip some error press f")

        ctx.channel.sendFile(res.byteStream(), filename).queue()
    }

}
