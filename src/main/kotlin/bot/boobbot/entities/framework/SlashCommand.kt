package bot.boobbot.entities.framework

interface SlashCommand {

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
    fun localCheck(ctx: SlashContext): Boolean = true

    fun execute(ctx: SlashContext)

}
