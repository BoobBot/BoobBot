package bot.boobbot.slashcommands.bot

import bot.boobbot.BoobBot
import bot.boobbot.entities.framework.Category
import bot.boobbot.entities.framework.CommandProperties
import bot.boobbot.entities.framework.SlashCommand
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent

@CommandProperties(description = "Pong!", category = Category.MISC)
class Ping : SlashCommand {
    override fun execute(event: SlashCommandEvent) {
        val shards = BoobBot.shardManager.shardsTotal
        val shardsOnline = BoobBot.shardManager.onlineShards.size
        val averageShardLatency = BoobBot.shardManager.averageGatewayPing
        event.reply("**Shard info**: $shardsOnline/$shards\n**Average latency**: ${averageShardLatency}ms").queue();
    }
}