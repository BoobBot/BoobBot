package bot.boobbot.entities.framework

import bot.boobbot.BoobBot
import bot.boobbot.utils.Formats
import net.dv8tion.jda.api.Permission
import net.dv8tion.jda.api.utils.FileUpload
import okhttp3.Headers.Companion.headersOf
import okhttp3.HttpUrl.Companion.toHttpUrlOrNull

abstract class MemeAvatarSlashCommand(private val category: String) : AsyncSlashCommand {

    private val endpointUrl = "https://memes.subspace.gg/api/$category"
    private val urlBuilder
        get() = endpointUrl.toHttpUrlOrNull()!!.newBuilder()

    private val headers = headersOf("Authorization", BoobBot.config.MEMER_IMGEN_KEY)

    override suspend fun executeAsync(ctx: SlashContext) {
        if (!ctx.botCan(Permission.MESSAGE_SEND)) {
            return
        }

        if (!ctx.botCan(Permission.MESSAGE_ATTACH_FILES)) {
            return ctx.reply(Formats.error("I can't send images here, fix it whore."))
        }

        val user = ctx.getOption("member")?.asUser ?: ctx.user
        val url = urlBuilder.addQueryParameter("avatar1", user.effectiveAvatarUrl).build()

        val res = BoobBot.requestUtil.get(url.toString(), headers).await()?.body
            ?: return ctx.reply(Formats.error("rip some error press f"))

       ctx.reply(FileUpload.fromData(res.byteStream(), "$category.png"))
    }

}
