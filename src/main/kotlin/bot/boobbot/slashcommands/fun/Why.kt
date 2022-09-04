package bot.boobbot.slashcommands.`fun`

import bot.boobbot.BoobBot
import bot.boobbot.entities.framework.AsyncSlashCommand
import bot.boobbot.entities.framework.Category
import bot.boobbot.entities.framework.CommandProperties
import bot.boobbot.entities.framework.SlashContext
import bot.boobbot.utils.json

@CommandProperties(description = "Random why questions", category = Category.FUN)
class Why : AsyncSlashCommand {
    override suspend fun executeAsync(ctx: SlashContext) {
        val res = BoobBot.requestUtil
            .get("https://nekos.life/api/v2/why")
            .await()
            ?.json()
            ?: return ctx.reply("rip some error, press f")

        ctx.reply(res.get("why").toString())
    }

}