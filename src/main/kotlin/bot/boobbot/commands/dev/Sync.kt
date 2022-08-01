package bot.boobbot.commands.dev

import bot.boobbot.entities.framework.*
import net.dv8tion.jda.api.interactions.commands.build.Commands


@CommandProperties(description = "Sync context commands.", category = Category.DEV, developerOnly = true)
class Sync : Command {
    override fun execute(ctx: Context) {
        ctx.jda.updateCommands().addCommands(Commands.user("Interact")).queue()
        sendSubcommandHelp(ctx)
    }
}


