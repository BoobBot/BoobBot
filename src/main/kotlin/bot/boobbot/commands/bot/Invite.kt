package bot.boobbot.commands.bot

import bot.boobbot.BoobBot
import bot.boobbot.entities.framework.Context
import bot.boobbot.entities.framework.annotations.CommandProperties
import bot.boobbot.entities.framework.interfaces.Command
import bot.boobbot.utils.Colors
import bot.boobbot.utils.Formats
import net.dv8tion.jda.api.components.buttons.ButtonStyle
import java.time.Instant

@CommandProperties(description = "Bot and support guild links", aliases = ["join", "oauth", "link", "links", "support"], groupByCategory = true)
class Invite : Command {

    override fun execute(ctx: Context) {
        val requester = BoobBot.shardManager.authorOrAnonymous(ctx)

        ctx.message {
            embed {
                setColor(Colors.rndColor)
                setAuthor(ctx.selfUser.name, ctx.selfUser.effectiveAvatarUrl, ctx.selfUser.effectiveAvatarUrl)
                setDescription(Formats.LING_MSG)
                setFooter("Requested by ${requester.name}", requester.effectiveAvatarUrl)
                setTimestamp(Instant.now())
            }
            row {
                button(ButtonStyle.LINK, "https://discord.boob.bot", "Join the Server")
                button(ButtonStyle.LINK, "https://invite.boob.bot", "Get Support")
                button(ButtonStyle.LINK, "https://bot.boob.bot", "Add Slash Commands")
            }
        }
    }
}
