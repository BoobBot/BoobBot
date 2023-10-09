package bot.boobbot.commands.dev

import bot.boobbot.BoobBot
import bot.boobbot.entities.framework.Category
import bot.boobbot.entities.framework.Context
import bot.boobbot.entities.framework.annotations.CommandProperties
import bot.boobbot.entities.framework.interfaces.Command

@CommandProperties(description = "Check how fucked the bot session is", category = Category.DEV, developerOnly = true, groupByCategory = true)
class Session : Command {
    override fun execute(ctx: Context) {
        val sessionInfo = BoobBot.shardManager.retrieveSessionInfo()
            ?: return ctx.reply("fuck, some error")

        val guildCount = BoobBot.shardManager.guildCache.size()
        val shardCount = BoobBot.shardManager.shardCount

        ctx.reply {
            setColor(0xDC7A23)
            setDescription("""
                Sessions Remaining: ${sessionInfo.sessionLimitRemaining}/${sessionInfo.sessionLimitTotal}
                Session Concurrency: ${sessionInfo.maxConcurrency}x
                Resets After: ${sessionInfo.sessionResetAfter}
                
                Avg. Guilds Per Shard: ${(guildCount / shardCount)}
                Recommended Shards: ${sessionInfo.recommendedShards}
                """)
        }
    }
}
