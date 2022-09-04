package bot.boobbot.entities.framework

class ExecutableSlashCommand(
    private val cmd: SlashCommand,
) {
    val name = cmd.name
    val hasProperties = cmd.hasProperties
    val properties = cmd.properties
    fun execute(event: SlashContext) {
        if (!cmd.localCheck(event)) {
            return
        }

        cmd.execute(event)
    }

}