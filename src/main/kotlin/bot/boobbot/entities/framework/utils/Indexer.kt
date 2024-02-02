package bot.boobbot.entities.framework.utils

import bot.boobbot.entities.framework.annotations.Option
import bot.boobbot.entities.framework.annotations.Options
import bot.boobbot.entities.framework.annotations.SubCommand
import bot.boobbot.entities.framework.impl.ExecutableCommand
import bot.boobbot.entities.framework.impl.SubCommandWrapper
import bot.boobbot.entities.framework.interfaces.Command
import org.reflections.Reflections
import java.lang.reflect.Modifier
import kotlin.reflect.full.declaredFunctions
import kotlin.reflect.full.findAnnotation
import kotlin.reflect.full.hasAnnotation
import kotlin.reflect.jvm.javaMethod

class Indexer(pkg: String) {

    private val reflections = Reflections(pkg)

    fun getCommands(): List<ExecutableCommand> {
        return reflections.getSubTypesOf(Command::class.java)
            .filter { !Modifier.isAbstract(it.modifiers) && !it.isInterface }
            .map { it.getDeclaredConstructor().newInstance() }
            .map {
                val category = if (it.properties.groupByCategory) it::class.java.packageName.split('.').last() else null
                val options = it::class.findAnnotation<Options>()?.value?.toList()
                    ?: it::class.annotations.filterIsInstance<Option>()

                ExecutableCommand(it, getSubCommands(it).associateBy(SubCommandWrapper::name), it.properties.slashEnabled, category, options)
            }
    }

    fun getSubCommands(kls: Command): List<SubCommandWrapper> {
        return kls::class.declaredFunctions.filter { it.hasAnnotation<SubCommand>() }
            .map {
                val name = it.name.lowercase()
                val props = it.findAnnotation<SubCommand>()!!
                val options = it.findAnnotation<Options>()?.value?.toList()
                    ?: it.annotations.filterIsInstance<Option>()

                SubCommandWrapper(name, props.aliases, it.isSuspend, props.description, props.donorOnly, options, it.javaMethod!!, kls)
            }
    }

}