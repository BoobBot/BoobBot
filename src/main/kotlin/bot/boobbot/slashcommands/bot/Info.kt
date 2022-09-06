package bot.boobbot.slashcommands.bot

import bot.boobbot.BoobBot
import bot.boobbot.entities.framework.Category
import bot.boobbot.entities.framework.annotations.CommandProperties
import bot.boobbot.entities.framework.SlashCommand
import bot.boobbot.entities.framework.SlashContext
import bot.boobbot.utils.Colors
import bot.boobbot.utils.Utils
import com.sedmelluq.discord.lavaplayer.tools.PlayerLibrary
import net.dv8tion.jda.api.JDAInfo

@CommandProperties(description = "Displays bot info.", category = Category.MISC)
class Info : SlashCommand {
    override fun execute(ctx: SlashContext) {
        val shards = BoobBot.shardManager.shardsTotal
        val shardsOnline = BoobBot.shardManager.onlineShards.size
        val averageShardLatency = BoobBot.shardManager.averageGatewayPing
        ctx.reply {
            setAuthor("BoobBot (Revision ${Utils.version})", ctx.jda.selfUser.effectiveAvatarUrl, ctx.jda.selfUser.effectiveAvatarUrl)
            setColor(Colors.rndColor)
            setDescription(
                """
                    JDA: ${JDAInfo.VERSION}
                    LP: ${PlayerLibrary.VERSION}
                    SHARDS: $shards\$shardsOnline
                    PING: ${averageShardLatency}ms
                """.trimIndent() // PYTHON TIME POGGERS
            )
        }
    }
}
