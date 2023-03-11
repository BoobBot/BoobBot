package bot.boobbot.commands.bot

import bot.boobbot.BoobBot
import bot.boobbot.entities.framework.Context
import bot.boobbot.entities.framework.annotations.CommandProperties
import bot.boobbot.entities.framework.interfaces.Command
import bot.boobbot.utils.Colors
import bot.boobbot.utils.Utils
import com.sedmelluq.discord.lavaplayer.tools.PlayerLibrary
import net.dv8tion.jda.api.JDAInfo

@CommandProperties(description = "Displays bot info.", groupByCategory = true)
class Info : Command {

    override fun execute(ctx: Context) {
        val shards = BoobBot.shardManager.shardsTotal
        val shardsOnline = BoobBot.shardManager.onlineShards.size
        val averageShardLatency = BoobBot.shardManager.averageGatewayPing
        ctx.reply {
            setAuthor(
                "BoobBot (Revision ${Utils.version})",
                ctx.selfUser.effectiveAvatarUrl,
                ctx.selfUser.effectiveAvatarUrl
            )
            setColor(Colors.getEffectiveColor(ctx.member))
            setDescription(
                """
                    JDA: ${JDAInfo.VERSION}
                    LP: ${PlayerLibrary.VERSION}
                    SHARDS: $shards/$shardsOnline
                    PING: ${averageShardLatency}ms
                """.trimIndent() // PYTHON TIME POGGERS
            )
        }
    }

}