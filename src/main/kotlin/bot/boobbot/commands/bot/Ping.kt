package bot.boobbot.commands.bot

import bot.boobbot.BoobBot
import bot.boobbot.entities.framework.*
import bot.boobbot.entities.framework.annotations.CommandProperties
import bot.boobbot.entities.framework.annotations.SubCommand
import bot.boobbot.entities.framework.interfaces.Command

@CommandProperties(description = "Pong!", category = Category.MISC)
class Ping : Command {
    override fun execute(ctx: MessageContext) {
        ctx.reply("What do you want me to say, pong? No you can go fuck yourself~")
    }

    @SubCommand
    fun please(ctx: MessageContext) {
        val shards = BoobBot.shardManager.shardsTotal
        val shardsOnline = BoobBot.shardManager.onlineShards.size
        val averageShardLatency = BoobBot.shardManager.averageGatewayPing
        ctx.reply("**Shard info**: $shardsOnline/$shards\n**Average latency**: ${averageShardLatency}ms")
    }

}