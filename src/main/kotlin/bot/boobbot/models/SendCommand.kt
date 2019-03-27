package bot.boobbot.models

import bot.boobbot.BoobBot
import bot.boobbot.flight.AsyncCommand
import bot.boobbot.flight.Context
import bot.boobbot.misc.Formats
import bot.boobbot.misc.createHeaders
import bot.boobbot.misc.json
import kotlinx.coroutines.future.await

abstract class SendCommand(private val category: String, private val endpoint: String) : AsyncCommand {

    private val headers = createHeaders(Pair("Key", BoobBot.config.bbApiKey))

    override suspend fun executeAsync(ctx: Context) {
        val user = ctx.message.mentionedUsers.firstOrNull() ?: ctx.author

        if (user.idLong == BoobBot.selfId) {
            return ctx.send("Don't you fucking touch me whore, i will end you.")
        }

        if (user.isBot) {
            return ctx.send("Bots can't appreciate $category, whore.")
        }

        val prompt = ctx.dmUserAsync(
            user,
            "${ctx.author.name} has sent you some NSFW $category!\nAre you 18+ and wish to view this content? (`y`/`n`)"
        )
            ?: return ctx.send("hey, this whore ${user.name} has me blocked or their filter turned on \uD83D\uDD95")

        ctx.send("Good job ${ctx.author.asMention}")

        val res = BoobBot.waiter.waitForMessage({
            it.channel.id == prompt.channel.id &&
                    it.contentRaw.toLowerCase() == "y" || it.contentRaw.toLowerCase() == "n"
        }, 60000).await() ?: return ctx.send("${user.name} didn't respond") // timeout

        prompt.delete().queue()

        if (res.contentRaw.toLowerCase() == "y") { // yes
            val url = BoobBot.requestUtil.get("https://boob.bot/api/v2/img/$endpoint", headers)
                .await()?.json()?.getString("url")
                ?: return ctx.send("wtf, api down?")

            ctx.dmUserAsync(user, "${Formats.LEWD_EMOTE} $url")
        } else {
            ctx.dmUserAsync(user, "Alright then, I won't.")
        }

    }

}