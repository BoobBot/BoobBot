package bot.boobbot.flight

import org.reflections.Reflections
import java.lang.reflect.Modifier

class Indexer(pkg: String) {

    private val reflections = Reflections(pkg)

    fun getCommands(): List<ExecutableCommand> {
        val allCommands = reflections.getSubTypesOf(Command::class.java)
            .filter { !Modifier.isAbstract(it.modifiers) && !it.isInterface }

        val cmds = mutableListOf<ExecutableCommand>()

        for (cmd in allCommands) {
            val kls = cmd.getDeclaredConstructor().newInstance()
            val subcommands = getSubCommands(kls).associateBy { it.name }
            val e = ExecutableCommand(kls, subcommands)

            cmds.add(e)
        }

        return cmds.toList()
    }

    fun getSubCommands(kls: Command): List<SubCommandWrapper> {
        val subcommands = mutableListOf<SubCommandWrapper>()

        for (meth in kls::class.java.methods) { // WAS' BREWING Y'ALLL
            if (!meth.isAnnotationPresent(SubCommand::class.java)) {
                continue
            }

            val props = meth.getDeclaredAnnotation(SubCommand::class.java)

            val name = meth.name.toLowerCase()
            val wrapper = SubCommandWrapper(name, props.aliases, props.async, props.description, meth, kls)
            subcommands.add(wrapper)
        }

        return subcommands.toList()
    }

}