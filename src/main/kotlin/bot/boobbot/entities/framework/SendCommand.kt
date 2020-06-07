package bot.boobbot.entities.framework

import bot.boobbot.BoobBot
import bot.boobbot.utils.Formats
import bot.boobbot.utils.json
import okhttp3.Headers

abstract class SendCommand(private val category: String, private val endpoint: String) : AsyncCommand {

    private val headers = Headers.of("Key", BoobBot.config.BB_API_KEY)

    override suspend fun executeAsync(ctx: Context) {
        val user = ctx.mentions.firstOrNull() ?: ctx.author

        if (user.idLong == BoobBot.selfId) {
            return ctx.send(Formats.error("Don't you fucking touch me whore, i will end you."))
        }

        if (user.isBot) {
            return ctx.send(Formats.error("Bots can't appreciate $category, whore."))
        }

        val isUserReceivingNudes = BoobBot.database.getCanUserReceiveNudes(user.id)

        if (!isUserReceivingNudes) {
            return ctx.send(Formats.error("wtf, **${user.asTag}** opted out of receiving nudes. What a whore. Tell them to opt back in with `bbopt in`"))
        }

        if (category == "dicks") {
            val isUserCockBlocked = BoobBot.database.getUserCockBlocked(user.id)

            if (isUserCockBlocked) {
                return ctx.send(Formats.error("wtf, **${user.asTag}** is cockblocked. Whore."))
            }
        }

        val url = BoobBot.requestUtil.get("https://boob.bot/api/v2/img/$endpoint", headers)
            .await()?.json()?.getString("url")
            ?: return ctx.send(Formats.error("wtf, api down?"))

        ctx.dmUserAsync(user, "${Formats.LEWD_EMOTE} $url")
            ?: return ctx.send(Formats.error("wtf, I can't DM **${user.asTag}**?"))

        ctx.send(Formats.info("Good job ${ctx.author.asMention}"))
    }

}
