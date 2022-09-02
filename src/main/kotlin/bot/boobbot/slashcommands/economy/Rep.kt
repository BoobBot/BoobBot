package bot.boobbot.slashcommands.economy

import bot.boobbot.BoobBot
import bot.boobbot.entities.framework.*
import bot.boobbot.utils.Formats
import bot.boobbot.utils.Formats.getRemainingCoolDown
import net.dv8tion.jda.api.EmbedBuilder
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent
import java.awt.Color
import java.time.Instant
import java.time.temporal.ChronoUnit


@CommandProperties(aliases = ["+"], description = "Give rep.", category = Category.ECONOMY)
class Rep : SlashCommand {

    override fun execute(event: SlashCommandInteractionEvent) {
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

        val author = BoobBot.database.getUser(event.user.id)
        val now = Instant.now()

        if (author.lastRep != null) {
            val t = author.lastRep!!.plus(12, ChronoUnit.HOURS)
            val x = t.toEpochMilli() - now.toEpochMilli()
            if (!t.isBefore(now)) {
                return event.reply("You already gave rep today whore.\nFuck off and try again in ${getRemainingCoolDown(x)}").queue()
            }
        }

        author.lastRep = now
        author.save()

        BoobBot.database.getUser(target.id)
            .apply { rep += 1 }
            .save()

        event.reply("You gave ${target.asMention} a rep point, Good job! Seems you're not completely useless after all.").queue()
    }

}
