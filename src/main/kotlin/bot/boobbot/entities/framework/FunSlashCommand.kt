package bot.boobbot.entities.framework

import bot.boobbot.BoobBot
import bot.boobbot.utils.Formats
import bot.boobbot.utils.Utils.getRandomFunString
import net.dv8tion.jda.api.EmbedBuilder
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent
import java.awt.Color
import java.text.MessageFormat

abstract class FunSlashCommand(private val category: String) : AsyncSlashCommand {
    override suspend fun executeAsync(event: SlashCommandInteractionEvent) {
        val target = event.options.firstOrNull()?.asMember
            ?: return event.replyEmbeds(
                EmbedBuilder().apply {
                setColor(Color.red)
                setDescription(Formats.error("you didn't mention a @user, dumbass.\n"))
            }.build()).queue()

        if (target.idLong == BoobBot.selfId) {
            return event.reply("Don't you fucking touch me whore, i will end you.").queue()
        }

        if (target.idLong == event.member!!.idLong) {
            return event.reply("aww how sad you wanna play with yourself, well fucking don't go find a friend whore.").queue()
        }

        val funString = MessageFormat.format(getRandomFunString(category), event.member!!.effectiveName, target.effectiveName)
        event.reply(funString).queue()
    }
}
