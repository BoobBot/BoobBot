package bot.boobbot.commands.dev

import bot.boobbot.BoobBot
import bot.boobbot.flight.Category
import bot.boobbot.flight.Command
import bot.boobbot.flight.CommandProperties
import bot.boobbot.flight.Context


@CommandProperties(description = "modmute", category = Category.DEV, developerOnly = true)
class ModMute : Command {

    override fun execute(ctx: Context) {
        val g = BoobBot.database.getGuild(ctx.guild!!.id)
        val target = ctx.mentions.firstOrNull()
            ?: return ctx.send("Mention someone, whore.")
        val unmuted = g.modMute.contains(target.id)
        val status = if (unmuted) "Unmuted" else "Muted"

        if (g.modMute.contains(target.id)) {
            g.modMute.remove(target.id)
        } else {
            g.modMute.add(target.id)
        }

        BoobBot.database.setGuild(g)
        ctx.send("$status ${target.asMention}.")
    }
}
