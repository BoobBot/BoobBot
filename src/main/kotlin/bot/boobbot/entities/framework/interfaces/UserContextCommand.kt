package bot.boobbot.entities.framework.interfaces

import bot.boobbot.entities.framework.annotations.CommandProperties
import net.dv8tion.jda.api.events.interaction.command.UserContextInteractionEvent
interface UserContextCommand {

    val name: String
        get() = this.javaClass.simpleName.lowercase()

    val properties: CommandProperties
        get() = this.javaClass.getAnnotation(CommandProperties::class.java)

    val hasProperties: Boolean
        get() = this.javaClass.isAnnotationPresent(CommandProperties::class.java)

    /**
     * Command-local check that is executed before the command or any subcommands are
     * executed.
     *
     * @returns Whether command execution can proceed.
     */
    fun localCheck(event: UserContextInteractionEvent): Boolean = true

    fun execute(event: UserContextInteractionEvent)

}
