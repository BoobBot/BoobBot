package bot.boobbot.commands.bot

import bot.boobbot.flight.Command
import bot.boobbot.flight.CommandProperties
import bot.boobbot.flight.Context

@CommandProperties(description = "Pong!", donorOnly = true)
class Ping : Command {

    override fun execute(ctx: Context) {
        ctx.send("What do you want me to say, pong? No you can go fuck yourself~")// TODO: full shard pings
    }

}