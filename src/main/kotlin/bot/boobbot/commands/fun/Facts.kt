package bot.boobbot.commands.`fun`

import bot.boobbot.BoobBot
import bot.boobbot.flight.AsyncCommand
import bot.boobbot.flight.Category
import bot.boobbot.flight.CommandProperties
import bot.boobbot.flight.Context
import bot.boobbot.misc.Formats
import bot.boobbot.misc.json

@CommandProperties(description = "Random facts", aliases = ["fact"], category = Category.FUN)
class Facts : AsyncCommand {

    override suspend fun executeAsync(ctx: Context) {

        val res = BoobBot.requestUtil
                .get("https://nekos.life/api/v2/fact")
                .await()
                ?: return ctx.send("rip some error, press f")
        val body = res.json() ?: return ctx.send("rip some error, press f")
        ctx.send(Formats.info(body.get("fact").toString()))
    }

}