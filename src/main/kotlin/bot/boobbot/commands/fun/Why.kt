package bot.boobbot.commands.`fun`

import bot.boobbot.BoobBot
import bot.boobbot.entities.framework.Category
import bot.boobbot.entities.framework.Context
import bot.boobbot.entities.framework.annotations.CommandProperties
import bot.boobbot.entities.framework.interfaces.AsyncCommand
import bot.boobbot.utils.Formats
import bot.boobbot.utils.json

@CommandProperties(description = "Random why questions", aliases = ["fact"], category = Category.FUN, groupByCategory = true)
class Why : AsyncCommand {

    override suspend fun executeAsync(ctx: Context) {
        val res = BoobBot.requestUtil
            .get("https://nekos.life/api/v2/why")
            .await()
            ?.json()
            ?: return ctx.reply("rip some error, press f")

        ctx.reply(Formats.info(res.get("why").toString()))
    }

}