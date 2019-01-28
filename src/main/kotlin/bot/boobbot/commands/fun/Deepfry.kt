package bot.boobbot.commands.`fun`

import bot.boobbot.BoobBot
import bot.boobbot.flight.*
import bot.boobbot.misc.Constants

import bot.boobbot.misc.createHeaders
import net.dv8tion.jda.core.entities.Member

import java.net.URLEncoder

@CommandProperties(description = "Deepfry.", nsfw = false, category = Category.FUN, guildOnly = true)
class Deepfry : AsyncCommand {

    override suspend fun executeAsync(ctx: Context) {
        val user: Member
        if (ctx.message.mentionedMembers.isEmpty()) {
            user = ctx.member!!
        }
        else {
            user = ctx.message.mentionedMembers.firstOrNull()!!
        }

        val res = BoobBot.requestUtil
            .get(
                "https://dankmemer.services/api/deepfry?avatar1=${URLEncoder.encode(user.user.avatarUrl, Charsets.UTF_8.name())}",
                createHeaders(Pair("Authorization", Constants.MEMER_IMGEN_KEY))
            )
            .await()
            ?: return ctx.send("rip some error, press f")


        val body = res.body() ?: return ctx.send("rip some error, press f")

        ctx.channel.sendFile(body.byteStream(), "uwu.png").queue()

    }
}
