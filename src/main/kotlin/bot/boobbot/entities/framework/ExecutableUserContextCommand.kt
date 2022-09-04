package bot.boobbot.entities.framework

import net.dv8tion.jda.api.events.interaction.command.UserContextInteractionEvent

class ExecutableUserContextCommand(
    private val cmd: UserContextCommand,
) {
    val name = cmd.name
    val hasProperties = cmd.hasProperties
    val properties = cmd.properties
    fun execute(event: UserContextInteractionEvent) {
        if (!cmd.localCheck(event)) {
            return
        }
        cmd.execute(event)

    }

}