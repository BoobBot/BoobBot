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

    fun findCommand(commandName: String): ExecutableCommand? {
        return this[commandName]
            ?: values.firstOrNull { it.properties.aliases.contains(commandName) }
    }
}
