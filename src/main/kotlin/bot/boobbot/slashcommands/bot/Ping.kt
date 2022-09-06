package bot.boobbot.slashcommands.bot

import bot.boobbot.BoobBot
import bot.boobbot.entities.framework.Category
import bot.boobbot.entities.framework.annotations.CommandProperties
import bot.boobbot.entities.framework.SlashCommand
import bot.boobbot.entities.framework.SlashContext
import net.dv8tion.jda.api.interactions.commands.OptionMapping

@CommandProperties(description = "Pong!", category = Category.MISC)
class Ping : SlashCommand {
    override fun execute(ctx: SlashContext) {
        if (ctx.getOption("please", OptionMapping::getAsBoolean) == true){
            val shards = BoobBot.shardManager.shardsTotal
            val shardsOnline = BoobBot.shardManager.onlineShards.size
            val averageShardLatency = BoobBot.shardManager.averageGatewayPing
            return ctx.reply("**Shard info**: $shardsOnline/$shards\n**Average latency**: ${averageShardLatency}ms")
        }

        ctx.reply("What do you want me to say, pong? No you can go fuck yourself~")
    }
}
