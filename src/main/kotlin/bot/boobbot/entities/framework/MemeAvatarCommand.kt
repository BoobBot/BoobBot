package bot.boobbot.entities.framework

import bot.boobbot.BoobBot
import bot.boobbot.utils.Formats
import net.dv8tion.jda.api.Permission
import net.dv8tion.jda.api.utils.FileUpload
import okhttp3.Headers.Companion.headersOf
import okhttp3.HttpUrl.Companion.toHttpUrlOrNull

abstract class MemeAvatarCommand(private val category: String) : AsyncCommand {

    private val endpointUrl = "https://memes.subspace.gg/api/$category"
    private val urlBuilder
        get() = endpointUrl.toHttpUrlOrNull()!!.newBuilder()

    private val headers = headersOf("Authorization", BoobBot.config.MEMER_IMGEN_KEY)

    override suspend fun executeAsync(ctx: Context) {
        if (!ctx.botCan(Permission.MESSAGE_SEND)) {
            return
        }

        if (!ctx.botCan(Permission.MESSAGE_ATTACH_FILES)) {
            return ctx.send(Formats.error("I can't send images here, fix it whore."))
        }

        val user = ctx.mentions.firstOrNull() ?: ctx.author
        val url = urlBuilder.addQueryParameter("avatar1", user.effectiveAvatarUrl).build()

        val res = BoobBot.requestUtil.get(url.toString(), headers).await()?.body
            ?: return ctx.send(Formats.error("rip some error press f"))

        ctx.channel.sendFiles(FileUpload.fromData(res.byteStream(), "$category.png")).queue()
    }

}
