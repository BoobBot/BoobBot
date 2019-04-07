package bot.boobbot.flight

import bot.boobbot.BoobBot

interface Command {

    val name: String
        get() = this.javaClass.simpleName.toLowerCase()

    val properties: CommandProperties
        get() = this.javaClass.getAnnotation(CommandProperties::class.java)

    val hasProperties: Boolean
        get() = this.javaClass.isAnnotationPresent(CommandProperties::class.java)

    val subcommands: Map<String, SubCommandWrapper>
        get() = BoobBot.commands.getValue(name).subcommands

    /**
     * Command-local check that is executed before the command or any subcommands are
     * executed.
     *
     * @returns Boolean
     *          Whether or not command execution can proceed.
     */
    fun localCheck(ctx: Context): Boolean = true

    fun execute(ctx: Context)

}
