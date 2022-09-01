package bot.boobbot.entities.framework

import bot.boobbot.BoobBot
import bot.boobbot.utils.Formats
import bot.boobbot.utils.json
import kotlinx.coroutines.future.await
import net.dv8tion.jda.api.entities.Message
import net.dv8tion.jda.api.entities.User
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent
import net.dv8tion.jda.api.utils.messages.MessageCreateBuilder
import okhttp3.Headers
import okhttp3.Headers.Companion.headersOf

abstract class SlashSendCommand(private val category: String, private val endpoint: String) : AsyncSlashCommand {

    private val headers = headersOf("Key", BoobBot.config.BB_API_KEY)

    suspend fun dmUserAsync(user: User, message: String): Message? {
        return try {
            user.openPrivateChannel()
                .flatMap { it.sendMessage(MessageCreateBuilder().setContent(message).build()) }
                .submit()
                .await()
        } catch (e: Exception) {
            null
        }
    }


    override suspend fun executeAsync(event: SlashCommandInteractionEvent) {
        val user = event.getOption("member")?.asUser ?: event.user

        if (user.idLong == BoobBot.selfId) {
            return event.reply(Formats.error("Don't you fucking touch me whore, i will end you.")).queue()
        }

        if (user.isBot) {
            return event.reply(Formats.error("Bots can't appreciate $category, whore.")).queue()
        }

        val isUserReceivingNudes = BoobBot.database.getCanUserReceiveNudes(user.id)

        if (!isUserReceivingNudes) {
            return event.reply(Formats.error("wtf, **${user.asTag}** opted out of receiving nudes. What a whore. Tell them to opt back in with `bbopt in`"))
                .queue()
        }

        if (category == "dicks") {
            val isUserCockBlocked = BoobBot.database.getUserCockBlocked(user.id)

            if (isUserCockBlocked) {
                return event.reply(Formats.error("wtf, **${user.asTag}** is cockblocked. Whore.")).queue()
            }
        }

        val url = BoobBot.requestUtil.get("https://boob.bot/api/v2/img/$endpoint", headers)
            .await()?.json()?.getString("url")
            ?: return event.reply(Formats.error("wtf, api down?")).queue()

        dmUserAsync(user, "${Formats.LEWD_EMOTE} $url")
            ?: return event.reply(Formats.error("wtf, I can't DM **${user.asTag}**?")).queue()

        event.reply(Formats.info("Good job ${event.user.asMention}")).queue()
    }

}
