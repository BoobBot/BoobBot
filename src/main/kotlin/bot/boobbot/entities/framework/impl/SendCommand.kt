package bot.boobbot.entities.framework.impl

import bot.boobbot.BoobBot
import bot.boobbot.entities.framework.Context
import bot.boobbot.entities.framework.interfaces.AsyncCommand
import bot.boobbot.utils.Formats
import bot.boobbot.utils.json
import okhttp3.Headers.Companion.headersOf

abstract class SendCommand(private val category: String, private val endpoint: String) : AsyncCommand {

    private val headers = headersOf("Key", BoobBot.config.BB_API_KEY)

    override suspend fun executeAsync(ctx: Context) {
        ctx.defer()

        val user = ctx.options.getByNameOrNext("to", Resolver.USER) ?: ctx.user

        if (user.idLong == BoobBot.selfId) {
            return ctx.reply(Formats.error("Don't you fucking touch me whore, i will end you."))
        }

        if (user.isBot) {
            return ctx.reply(Formats.error("Bots can't appreciate $category, whore."))
        }

        val isUserReceivingNudes = BoobBot.database.getCanUserReceiveNudes(user.id)

        if (!isUserReceivingNudes) {
            return ctx.reply(Formats.error("wtf, **${user.asTag}** opted out of receiving nudes. What a whore. Tell them to opt back in with `@BoobBot opt in`"))
        }

        if (category == "dicks") {
            val isUserCockBlocked = BoobBot.database.getUserCockBlocked(user.id)

            if (isUserCockBlocked) {
                return ctx.reply(Formats.error("wtf, **${user.asTag}** is cockblocked. Whore."))
            }
        }

        val url = BoobBot.requestUtil.get("https://boob.bot/api/v2/img/$endpoint", headers)
            .await()?.json()?.getString("url")
            ?: return ctx.reply(Formats.error("wtf, api down?"))

        ctx.dmUserAsync(user, "${Formats.LEWD_EMOTE} $url")
            ?: return ctx.reply(Formats.error("wtf, I can't DM **${user.asTag}**?"))

        ctx.reply(Formats.info("Good job ${ctx.user.asMention}"))
    }

}
