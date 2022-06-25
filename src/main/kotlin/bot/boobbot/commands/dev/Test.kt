package bot.boobbot.commands.dev

import bot.boobbot.entities.framework.*
import net.dv8tion.jda.api.interactions.commands.build.Commands


@CommandProperties(category = Category.DEV, developerOnly = true)
class Test : Command {
    override fun execute(ctx: Context) {
        ctx.jda.updateCommands().addCommands(Commands.user("Interact")).queue()
        sendSubcommandHelp(ctx)
    }
}


