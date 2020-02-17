package bot.boobbot.commands.dev

import bot.boobbot.BoobBot
import bot.boobbot.flight.Category
import bot.boobbot.flight.Command
import bot.boobbot.flight.CommandProperties
import bot.boobbot.flight.Context
import org.jetbrains.kotlin.backend.common.pop


@CommandProperties(description = "modmute", category = Category.DEV, developerOnly = true)
class ModMute : Command {

    override fun execute(ctx: Context) {
        val g = BoobBot.database.getGuild(ctx.guild!!.id)!!
        if(g.modMute.contains(ctx.mentions.first().id)) g.modMute.remove(ctx.mentions.first().id) else g.modMute.add(ctx.mentions.first().id)
        BoobBot.database.saveGuild(g)
        ctx.channel.sendMessage("Muted/unmuted ${ctx.mentions.first().asMention}").queue()
    }
}
