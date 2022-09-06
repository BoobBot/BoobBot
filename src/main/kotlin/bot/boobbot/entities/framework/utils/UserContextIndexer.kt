package bot.boobbot.entities.framework.utils

import bot.boobbot.entities.framework.impl.ExecutableUserContextCommand
import bot.boobbot.entities.framework.interfaces.UserContextCommand
import org.reflections.Reflections
import java.lang.reflect.Modifier

class UserContextIndexer(pkg: String) {
    private val reflections = Reflections(pkg)

    fun getCommands(): List<ExecutableUserContextCommand> {
        val allCommands = reflections.getSubTypesOf(UserContextCommand::class.java)
            .filter { !Modifier.isAbstract(it.modifiers) && !it.isInterface }

        return allCommands.map { it.getDeclaredConstructor().newInstance() }
            .map(::ExecutableUserContextCommand)
    }

}