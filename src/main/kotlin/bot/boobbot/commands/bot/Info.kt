package bot.boobbot.commands.bot

import bot.boobbot.BoobBot
import bot.boobbot.entities.framework.Context
import bot.boobbot.entities.framework.annotations.CommandProperties
import bot.boobbot.entities.framework.interfaces.Command
import bot.boobbot.utils.Colors
import bot.boobbot.utils.Utils
import com.sedmelluq.discord.lavaplayer.tools.PlayerLibrary
import net.dv8tion.jda.api.JDAInfo
import kotlin.math.roundToInt

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
            addField("Bot Information", "WS Latency:\n${averageShardLatency.roundToInt()}ms\nShards: $shards/$shardsOnline", true)
            addField("Library Information",
                """
                    [`JDA ${JDAInfo.VERSION}`](${JDAInfo.GITHUB})
                    [`LavaPlayer ${PlayerLibrary.VERSION}`](https://github.com/sedmelluq/lavaplayer)
                """.trimIndent(),
                true)
        }
    }

}