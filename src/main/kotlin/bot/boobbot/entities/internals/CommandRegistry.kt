package bot.boobbot.entities.internals

import bot.boobbot.BoobBot
import bot.boobbot.entities.framework.impl.ExecutableCommand
import bot.boobbot.entities.framework.utils.Indexer

class CommandRegistry : HashMap<String, ExecutableCommand>() {
    init {
        val indexer = Indexer("bot.boobbot.commands")
        val commands = indexer.getCommands().associateBy { it.name }
        this.putAll(commands)
        BoobBot.log.info("Successfully loaded ${commands.size} commands!")
    }

    fun findCommand(parent: String, group: String): ExecutableCommand? { // slash
        return this.values.find { it.category == parent && it.name == group }
    }

    fun findCommand(commandName: String): ExecutableCommand? {
        return this[commandName]
            ?: values.firstOrNull { it.properties.aliases.contains(commandName) }
    }
}
