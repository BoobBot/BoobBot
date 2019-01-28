package bot.boobbot.models

import bot.boobbot.BoobBot
import bot.boobbot.flight.AsyncCommand
import bot.boobbot.flight.Context
import bot.boobbot.misc.Constants
import bot.boobbot.misc.createHeaders
import net.dv8tion.jda.core.entities.User
import java.net.URLEncoder

abstract class MemeAvatarCommand(private val category: String) : AsyncCommand {
    override suspend fun executeAsync(ctx: Context) {
        val headers = createHeaders(Pair("Authorization", Constants.MEMER_IMGEN_KEY))
        val user: User = if (ctx.message.mentionedUsers.isEmpty()) {
            ctx.author
        } else {
            ctx.message.mentionedUsers.firstOrNull()!!
        }

        val res = BoobBot.requestUtil
            .get(
                "https://dankmemer.services/api/$category?avatar1=${URLEncoder.encode(
                    user.avatarUrl,
                    Charsets.UTF_8.name()
                )}",
                headers
            )
            .await()
            ?: return ctx.send("rip some error, press f")

        val body = res.body() ?: return ctx.send("rip some error, press f")
        ctx.channel.sendFile(body.byteStream(), "$category.png").queue()
    }
}
