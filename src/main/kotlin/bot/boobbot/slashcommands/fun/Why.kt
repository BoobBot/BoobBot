package bot.boobbot.slashcommands.`fun`

import bot.boobbot.BoobBot
import bot.boobbot.entities.framework.AsyncSlashCommand
import bot.boobbot.entities.framework.Category
import bot.boobbot.entities.framework.CommandProperties
import bot.boobbot.utils.json
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent

@CommandProperties(description = "Random why questions", category = Category.FUN)
class Why : AsyncSlashCommand {
    override suspend fun executeAsync(event: SlashCommandInteractionEvent) {
        val res = BoobBot.requestUtil
            .get("https://nekos.life/api/v2/why")
            .await()
            ?.json()
            ?: return event.reply("rip some error, press f").queue()

        event.reply(res.get("why").toString()).queue()
    }

}