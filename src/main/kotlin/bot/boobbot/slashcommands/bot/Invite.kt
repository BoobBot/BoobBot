package bot.boobbot.slashcommands.bot

import bot.boobbot.entities.framework.CommandProperties
import bot.boobbot.entities.framework.SlashCommand
import bot.boobbot.entities.framework.SlashContext
import bot.boobbot.utils.Colors
import bot.boobbot.utils.Formats
import net.dv8tion.jda.api.interactions.components.buttons.ButtonStyle
import java.time.Instant

@CommandProperties(description = "Bot and support guild links", aliases = ["join", "oauth", "link", "links", "support"])
class Invite : SlashCommand {
    override fun execute(ctx: SlashContext) {
        ctx.message {
            embed {
                setColor(Colors.rndColor)
                setDescription(Formats.LING_MSG)
                setTimestamp(Instant.now())
            }
            row {
                button(ButtonStyle.LINK, "https://discord.boob.bot", "\uD83D\uDCAC")
            }
        }
    }
}
