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

    fun execute(ctx: Context)

}
