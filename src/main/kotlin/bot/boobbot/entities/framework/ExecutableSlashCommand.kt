package bot.boobbot.entities.framework

import net.dv8tion.jda.api.entities.Message
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent

class ExecutableSlashCommand(
    private val cmd: SlashCommand,
) {
    val name = cmd.name
    val hasProperties = cmd.hasProperties
    val properties = cmd.properties
    fun execute(event: SlashCommandEvent) {
        if (!cmd.localCheck(event)) {
            return
        }
        cmd.execute(event)

    }

}