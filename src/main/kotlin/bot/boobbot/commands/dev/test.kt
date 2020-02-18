package bot.boobbot.commands.dev

import bot.boobbot.BoobBot
import bot.boobbot.flight.Category
import bot.boobbot.flight.Command
import bot.boobbot.flight.CommandProperties
import bot.boobbot.flight.Context


@CommandProperties(description = "test", category = Category.DEV, developerOnly = true)
class test : Command {

    override fun execute(ctx: Context) {
//        val g = BoobBot.database.getGuild(ctx.guild!!.id)
//        g!!.dropEnabled = !g.dropEnabled
//        BoobBot.database.setGuild(g)
//        ctx.channel.sendMessage("drop has been set to ${g.dropEnabled}").queue()
        val u = BoobBot.database.getUser(ctx.author.id)
        ctx.channel.sendMessage(u.toString()).queue()
    }
}
