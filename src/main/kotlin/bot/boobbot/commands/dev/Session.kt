package bot.boobbot.commands.dev

import bot.boobbot.BoobBot
import bot.boobbot.entities.framework.Category
import bot.boobbot.entities.framework.interfaces.Command
import bot.boobbot.entities.framework.annotations.CommandProperties
import bot.boobbot.entities.framework.MessageContext

@CommandProperties(description = "Check how fucked the bot session is", category = Category.DEV, developerOnly = true)
class Session : Command {

    override fun execute(ctx: MessageContext) {
        val sessionInfo = BoobBot.shardManager.retrieveSessionInfo()
            ?: return ctx.reply("fuck, some error")

        ctx.reply {
            setDescription("Session resets in ${sessionInfo.sessionResetAfter}")
            addField("Recommended Shards", sessionInfo.recommendedShards.toString(), true)
            addField("Session Limit Total", sessionInfo.sessionLimitTotal.toString(), true)
            addField("Session Limit Remaining", sessionInfo.sessionLimitRemaining.toString(), true)
        }
    }

}
