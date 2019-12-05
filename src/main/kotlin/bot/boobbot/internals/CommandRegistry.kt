package bot.boobbot.internals

import bot.boobbot.BoobBot
import bot.boobbot.flight.ExecutableCommand
import bot.boobbot.flight.Indexer

class CommandRegistry(
    private val commands: Map<String, ExecutableCommand>
) : Map<String, ExecutableCommand> by commands {

    init {
        BoobBot.log.info("Successfully loaded ${commands.size} commands!")
    }

    fun findCommand(commandName: String): ExecutableCommand? {
        return commands[commandName]
            ?: values.firstOrNull { it.properties.aliases.contains(commandName) }
    }

    companion object {
        fun load(): CommandRegistry {
            val indexer = Indexer("bot.boobbot.commands")
            val commands = indexer.getCommands().associateBy { it.name }
            return CommandRegistry(commands)
        }
    }

}