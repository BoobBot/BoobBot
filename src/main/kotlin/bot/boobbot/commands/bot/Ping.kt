package bot.boobbot.commands.bot

import bot.boobbot.BoobBot
import bot.boobbot.flight.*

@CommandProperties(description = "Pong!", category = Category.MISC)
class Ping : Command {
    override fun execute(ctx: Context) {
        ctx.send("What do you want me to say, pong? No you can go fuck yourself~")
    }

    @SubCommand
    fun please(ctx: Context) {
        val shards = BoobBot.shardManager.shardsTotal
        val shardsOnline = BoobBot.shardManager.onlineShards.size
        val averageShardLatency = BoobBot.shardManager.averageGatewayPing
        ctx.send("**Shard info**: $shardsOnline/$shards\n**Average latency**: ${averageShardLatency}ms")
    }

}