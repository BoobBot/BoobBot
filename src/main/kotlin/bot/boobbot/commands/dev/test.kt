package bot.boobbot.commands.dev

import bot.boobbot.BoobBot
import bot.boobbot.flight.Category
import bot.boobbot.flight.Command
import bot.boobbot.flight.CommandProperties
import bot.boobbot.flight.Context
import bot.boobbot.misc.Colors
import bot.boobbot.misc.Utils
import org.jetbrains.kotlin.script.jsr223.KotlinJsr223JvmLocalScriptEngineFactory


@CommandProperties(description = "test", category = Category.DEV, developerOnly = true)
class test : Command {

    override fun execute(ctx: Context) {
        val g = BoobBot.database.getGuild(ctx.guild!!.id)
        g!!.enabled = !g.enabled
        BoobBot.database.saveGuild(g)
        ctx.channel.sendMessage("drop has been set to ${g.enabled}").queue()
    }
}
