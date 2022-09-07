package bot.boobbot.entities.framework.utils

import bot.boobbot.entities.framework.annotations.SubCommand
import bot.boobbot.entities.framework.impl.ExecutableCommand
import bot.boobbot.entities.framework.impl.SubCommandWrapper
import bot.boobbot.entities.framework.interfaces.Command
import org.reflections.Reflections
import java.lang.reflect.Modifier

class Indexer(pkg: String) {

    private val reflections = Reflections(pkg)

    fun getCommands(): List<ExecutableCommand> {
        val allCommands = reflections.getSubTypesOf(Command::class.java)
            .filter { !Modifier.isAbstract(it.modifiers) && !it.isInterface }

        // TODO: Options
        return allCommands.map { it.getDeclaredConstructor().newInstance() }.map {
            val category = if (it.properties.groupByCategory) it::class.java.packageName.split('.').last() else null
            ExecutableCommand(it, getSubCommands(it).associateBy(SubCommandWrapper::name), it.properties.slashEnabled, category)
        }
    }

    fun getSubCommands(kls: Command): List<SubCommandWrapper> {
        // TODO: Options

        return kls::class.java.methods.filter { it.isAnnotationPresent(SubCommand::class.java) }
            .map {
                val name = it.name.lowercase()
                val props = it.getDeclaredAnnotation(SubCommand::class.java)
                SubCommandWrapper(name, props.aliases, props.async, props.description, props.donorOnly, it, kls)
            }
    }

}