package bot.boobbot.slashcommands.interactions

import bot.boobbot.BoobBot
import bot.boobbot.entities.framework.*
import bot.boobbot.utils.Colors
import bot.boobbot.utils.Formats
import bot.boobbot.utils.json
import net.dv8tion.jda.api.EmbedBuilder
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent
import net.dv8tion.jda.api.interactions.commands.OptionType
import okhttp3.Headers
import java.awt.Color
import java.time.Instant

@CommandProperties(description = "Spank someone.", category = Category.INTERACTIONS, nsfw = true)
class Spank : AsyncSlashCommand {
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
        val res =
            BoobBot.requestUtil.get("https://boob.bot/api/v2/img/spank", Headers.of("Key", BoobBot.config.BB_API_KEY))
                .await()?.json()
                ?: return event.reply(
                    Formats.error(" oh? something broken af")
                ).queue()

        event.replyEmbeds(
            EmbedBuilder().apply {
            setTitle("<:spank:866431559557054464> ${event.member!!.effectiveName} Spanks ${target.effectiveName}")
            setColor(Colors.rndColor)
            setImage(res.getString("url"))
            setTimestamp(Instant.now())
        }.build()).queue()

    }
}