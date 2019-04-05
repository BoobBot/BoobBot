package bot.boobbot.flight

import org.reflections.Reflections
import java.lang.reflect.Modifier

class Indexer(private val pkg: String) {

    private val reflections = Reflections(pkg)

    public fun getCommands(): List<ExecutableCommand> {
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
//            if (Modifier.isAbstract(command.modifiers) || command.isInterface) {
//                return@forEach
//            }
//
//            try {
//                val cmd = command.getDeclaredConstructor().newInstance()
//                if (!cmd.hasProperties) {
//                    return@forEach BoobBot.log.warn("Command `${cmd.name}` is missing CommandProperties annotation. Will not load.")
//                }
//
//                BoobBot.commands[cmd.name] = cmd
//            } catch (e: InstantiationException) {
//                BoobBot.log.error("Failed to load command `${command.simpleName}`", e)
//            } catch (e: IllegalAccessException) {
//                BoobBot.log.error("Failed to load command `${command.simpleName}`", e)
//            }


    }

    public fun getSubCommands(kls: Command): List<SubCommandWrapper> {
        val subcommands = mutableListOf<SubCommandWrapper>()

        for (meth in kls::class.java.methods) { // WAS' BREWING Y'ALLL
            if (!meth.isAnnotationPresent(SubCommand::class.java)) {
                continue
            }

            val props = meth.getDeclaredAnnotation(SubCommand::class.java)

            val name = meth.name.toLowerCase()
            val isAsync = props.async
            val wrapper = SubCommandWrapper(name, isAsync, meth, kls)
            subcommands.add(wrapper)
        }

        return subcommands.toList()
    }

}