package bot.boobbot.entities.framework

import net.dv8tion.jda.api.events.interaction.command.UserContextInteractionEvent
interface ContextCommand {

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
     * @returns Whether or not command execution can proceed.
     */
    fun localCheck(event: UserContextInteractionEvent): Boolean = true

    fun execute(event: UserContextInteractionEvent)

}
