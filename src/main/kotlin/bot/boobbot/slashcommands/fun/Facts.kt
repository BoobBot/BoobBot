package bot.boobbot.slashcommands.`fun`

import bot.boobbot.BoobBot
import bot.boobbot.entities.framework.AsyncSlashCommand
import bot.boobbot.entities.framework.Category
import bot.boobbot.entities.framework.annotations.CommandProperties
import bot.boobbot.entities.framework.SlashContext
import bot.boobbot.utils.Formats
import bot.boobbot.utils.json

@CommandProperties(description = "Random facts", aliases = ["fact"], category = Category.FUN)
class Facts : AsyncSlashCommand {

    override suspend fun executeAsync(ctx: SlashContext) {
        val res = BoobBot.requestUtil
            .get("https://nekos.life/api/v2/fact")
            .await()
            ?.json()
            ?: return ctx.reply("rip some error, press f")

        ctx.reply(Formats.info(res.get("fact").toString()))
    }

}