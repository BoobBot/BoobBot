package bot.boobbot.commands.`fun`

import bot.boobbot.BoobBot
import bot.boobbot.entities.framework.interfaces.AsyncCommand
import bot.boobbot.entities.framework.Category
import bot.boobbot.entities.framework.annotations.CommandProperties
import bot.boobbot.entities.framework.MessageContext
import bot.boobbot.utils.json

@CommandProperties(description = "Random why questions", category = Category.FUN)
class Why : AsyncCommand {

    override suspend fun executeAsync(ctx: MessageContext) {
        val res = BoobBot.requestUtil
            .get("https://nekos.life/api/v2/why")
            .await()
            ?.json()
            ?: return ctx.reply("rip some error, press f")

        ctx.reply(res.get("why").toString())
    }

}