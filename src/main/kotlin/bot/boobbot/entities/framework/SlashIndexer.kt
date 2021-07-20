package bot.boobbot.entities.framework

import org.reflections.Reflections
import java.lang.reflect.Modifier

class SlashIndexer(pkg: String) {

    private val reflections = Reflections(pkg)

    fun getCommands(): List<ExecutableSlashCommand> {
        val allCommands = reflections.getSubTypesOf(SlashCommand::class.java)
            .filter { !Modifier.isAbstract(it.modifiers) && !it.isInterface }
        val cmds = mutableListOf<ExecutableSlashCommand>()
        for (cmd in allCommands) {
            val kls = cmd.getDeclaredConstructor().newInstance()
            val e = ExecutableSlashCommand(kls)
            cmds.add(e)
        }
        return cmds.toList()
    }

}