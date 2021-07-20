package bot.boobbot.slashcommands.`fun`

import bot.boobbot.BoobBot
import bot.boobbot.entities.framework.AsyncSlashCommand
import bot.boobbot.entities.framework.Category
import bot.boobbot.entities.framework.CommandProperties
import bot.boobbot.utils.json
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent

@CommandProperties(description = "Random why questions", category = Category.FUN)
class Why : AsyncSlashCommand {
    override suspend fun executeAsync(event: SlashCommandEvent) {
        val res = BoobBot.requestUtil
            .get("https://nekos.life/api/v2/why")
            .await()
            ?: return event.reply("rip some error, press f").queue()
        val body = res.json() ?: return event.reply("rip some error, press f").queue()
        event.reply(body.get("why").toString()).queue()
    }

}