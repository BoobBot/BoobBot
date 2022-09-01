package bot.boobbot.entities.framework

import bot.boobbot.BoobBot
import bot.boobbot.utils.Colors
import bot.boobbot.utils.Formats
import bot.boobbot.utils.json
import net.dv8tion.jda.api.EmbedBuilder
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent
import okhttp3.Headers
import java.awt.Color
import java.time.Instant

abstract class BbApiInteractionSlashCommand(private val category: String, private val title: String) : AsyncSlashCommand {
    private val headers = Headers.headersOf("Key", BoobBot.config.BB_API_KEY)

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

        if (target.user.isBot) {
            return event.reply("Don't you fucking touch the bots, I will end you.").queue()
        }

        val res = BoobBot.requestUtil.get("https://boob.bot/api/v2/img/$category", headers).await()?.json()
            ?: return event.reply(Formats.error(" oh? something broken af")).queue()

        event.replyEmbeds(
            EmbedBuilder().apply {
            setTitle(title.format(event.user.name, target.user.name))
            setColor(Colors.rndColor)
            setImage(res.getString("url"))
            setTimestamp(Instant.now())
        }.build()).queue()
    }
}
