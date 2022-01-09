package bot.boobbot.entities.framework

import org.reflections.Reflections
import java.lang.reflect.Modifier

class UserContextIndexer(pkg: String) {

    private val reflections = Reflections(pkg)

    fun getCommands(): List<ExecutableUserContextCommand> {
        val allCommands = reflections.getSubTypesOf(UserContextCommand::class.java)
            .filter { !Modifier.isAbstract(it.modifiers) && !it.isInterface }
        val cmds = mutableListOf<ExecutableUserContextCommand>()
        for (cmd in allCommands) {
            val kls = cmd.getDeclaredConstructor().newInstance()
            val e = ExecutableUserContextCommand(kls)
            cmds.add(e)
        }
        return cmds.toList()
    }

}