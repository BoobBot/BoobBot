package bot.boobbot.slashcommands.bot

import bot.boobbot.BoobBot
import bot.boobbot.entities.framework.*
import bot.boobbot.utils.Colors
import bot.boobbot.utils.Utils
import com.sedmelluq.discord.lavaplayer.tools.PlayerLibrary
import net.dv8tion.jda.api.EmbedBuilder
import net.dv8tion.jda.api.JDAInfo
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent

@CommandProperties(description = "Displays bot info.", category = Category.MISC)
class Info : SlashCommand {

    override fun execute(event: SlashCommandInteractionEvent) {
        val shards = BoobBot.shardManager.shardsTotal
        val shardsOnline = BoobBot.shardManager.onlineShards.size
        val averageShardLatency = BoobBot.shardManager.averageGatewayPing
        event.replyEmbeds(
            EmbedBuilder().apply {
            setAuthor(
                "BoobBot (Revision ${Utils.version})",
                event.jda.selfUser.effectiveAvatarUrl,
                event.jda.selfUser.effectiveAvatarUrl
            )
            setColor(Colors.rndColor)
            setDescription(
                """
                    JDA: ${JDAInfo.VERSION}
                    LP: ${PlayerLibrary.VERSION}
                    SHARDS: $shards\$shardsOnline
                    PING: ${averageShardLatency}ms
                """.trimIndent() // PYTHON TIME POGGERS
            )
        }.build()).queue()
    }

}