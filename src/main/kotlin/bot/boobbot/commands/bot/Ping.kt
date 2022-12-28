package bot.boobbot.commands.bot

import bot.boobbot.BoobBot
import bot.boobbot.entities.framework.*
import bot.boobbot.entities.framework.annotations.CommandProperties
import bot.boobbot.entities.framework.annotations.SubCommand
import bot.boobbot.entities.framework.impl.Resolver
import bot.boobbot.entities.framework.interfaces.Command

@CommandProperties(description = "Pong!", category = Category.MISC, groupByCategory = true)
class Ping : Command {
    override fun execute(ctx: Context) {
        if (ctx.options.getOptionStringOrGather("please") == "true") {
            return please(ctx)
        }

        if (ctx.options.getOptionStringOrGather("please") == "false") {
            return ctx.reply("What the fuck? No fucking manners, i should delete you.")
        }

        ctx.reply("What do you want me to say, pong? No you can go fuck yourself~")
    }

    @SubCommand()
    fun please(ctx: Context) {
        val shards = BoobBot.shardManager.shardsTotal
        val shardsOnline = BoobBot.shardManager.onlineShards.size
        val averageShardLatency = BoobBot.shardManager.averageGatewayPing
        ctx.reply("**Shard info**: $shardsOnline/$shards\n**Average latency**: ${averageShardLatency}ms")
    }

}