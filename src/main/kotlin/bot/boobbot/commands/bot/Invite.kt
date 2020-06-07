package bot.boobbot.commands.bot

import bot.boobbot.BoobBot
import bot.boobbot.entities.framework.Command
import bot.boobbot.entities.framework.CommandProperties
import bot.boobbot.entities.framework.Context
import bot.boobbot.utils.Colors
import bot.boobbot.utils.Formats
import java.time.Instant

@CommandProperties(description = "Bot and support guild links", aliases = ["join", "oauth", "link", "links", "support"])
class Invite : Command {

    override fun execute(ctx: Context) {
        val requester = BoobBot.shardManager.authorOrAnonymous(ctx)

        ctx.embed {
            setColor(Colors.rndColor)
            setAuthor(ctx.selfUser.name, ctx.selfUser.effectiveAvatarUrl, ctx.selfUser.effectiveAvatarUrl)
            setDescription(Formats.LING_MSG)
            setFooter("Requested by ${requester.name}", requester.effectiveAvatarUrl)
            setTimestamp(Instant.now())
        }
    }

}