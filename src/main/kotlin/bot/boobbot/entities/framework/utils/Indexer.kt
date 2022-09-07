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

        return allCommands.map { it.getDeclaredConstructor().newInstance() }.map {
            val category = if (it.properties.groupByCategory) it::class.java.packageName.split('.').last() else null
            ExecutableCommand(it, getSubCommands(it).associateBy(SubCommandWrapper::name), it.properties.slashEnabled, category)
        }
    }

    fun getSubCommands(kls: Command): List<SubCommandWrapper> {
        val subcommands = mutableListOf<SubCommandWrapper>()

        for (meth in kls::class.java.methods) { // WAS' BREWING Y'ALLL
            if (!meth.isAnnotationPresent(SubCommand::class.java)) {
                continue
            }

            val props = meth.getDeclaredAnnotation(SubCommand::class.java)

            val name = meth.name.lowercase()
            val wrapper = SubCommandWrapper(name, props.aliases, props.async, props.description, props.donorOnly, meth, kls)
            subcommands.add(wrapper)
        }

        return subcommands.toList()
    }

}