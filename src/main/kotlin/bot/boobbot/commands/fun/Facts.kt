package bot.boobbot.commands.`fun`

import bot.boobbot.BoobBot
import bot.boobbot.entities.framework.AsyncCommand
import bot.boobbot.entities.framework.Category
import bot.boobbot.entities.framework.CommandProperties
import bot.boobbot.entities.framework.Context
import bot.boobbot.utils.Formats
import bot.boobbot.utils.json

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