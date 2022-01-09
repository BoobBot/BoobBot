package bot.boobbot.slashcommands.`fun`

import bot.boobbot.BoobBot
import bot.boobbot.entities.framework.AsyncSlashCommand
import bot.boobbot.entities.framework.Category
import bot.boobbot.entities.framework.CommandProperties
import bot.boobbot.utils.Formats
import bot.boobbot.utils.json
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent

@CommandProperties(description = "Random facts", aliases = ["fact"], category = Category.FUN)
class Facts : AsyncSlashCommand {

    override suspend fun executeAsync(event: SlashCommandInteractionEvent) {
        val res = BoobBot.requestUtil
            .get("https://nekos.life/api/v2/fact")
            .await()
            ?.json()
            ?: return event.reply("rip some error, press f").queue()

        event.reply(Formats.info(res.get("fact").toString())).queue()
    }

}