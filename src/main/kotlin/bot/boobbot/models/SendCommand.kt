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
        val user = ctx.message.mentionedUsers().firstOrNull() ?: ctx.author

        if (user.idAsLong() == ctx.selfUser?.idAsLong()) {
            return ctx.send("Don't you fucking touch me whore, i will end you.")
        }

        if (user.bot()) {
            return ctx.send("Bots can't appreciate $category, whore.")
        }

        val prompt = ctx.dmUserAsync(
            user,
            "${ctx.author.username()} has sent you some NSFW $category!\nAre you 18+ and wish to view this content?"
        )
            ?: return ctx.send("hey, this whore ${user.username()} has me blocked or their filter turned on \uD83D\uDD95")

        ctx.send("Good job ${ctx.author.asMention()}")

        prompt.react("yes:443810942221025280").await()
        prompt.react("no:443810942099390464").await()

//        val emote = BoobBot.waiter.waitFor(PrivateMessageReactionAddEvent::class.java, {
//            it.user.idLong == ctx.author.idLong
//                    && it.reactionEmote != null
//                    && (it.reactionEmote.idLong == 443810942221025280L || it.reactionEmote.idLong == 443810942099390464L)
//        }, 60000).await() ?: return ctx.send("${user.name} didn't respond") // timeout

        //prompt.delete().await()

//        if (emote.reactionEmote.idLong == 443810942221025280L) { // yes
//            val url = BoobBot.requestUtil.get("https://boob.bot/api/v2/img/$endpoint", headers)
//                .await()?.json()?.getString("url")
//                ?: return ctx.send("wtf, api down?")
//
//            ctx.dmUserAsync(user, "${Formats.LEWD_EMOTE} $url")
//        } else {
//            ctx.dmUserAsync(user, "Alright then, I won't.")
//        }

    }

}