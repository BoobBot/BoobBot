package bot.boobbot.entities.internals

import bot.boobbot.BoobBot
import bot.boobbot.entities.framework.ExecutableCommand
import bot.boobbot.entities.framework.ExecutableSlashCommand
import bot.boobbot.entities.framework.Indexer
import bot.boobbot.entities.framework.SlashIndexer

class SlashCommandRegistry : HashMap<String, ExecutableSlashCommand>() {
    init {
        val indexer = SlashIndexer("bot.boobbot.slashcommands")
        val commands = indexer.getCommands().associateBy { it.name }
        this.putAll(commands)
        BoobBot.log.info("Successfully loaded ${commands.size} slash commands!")
    }

    fun findCommand(commandName: String): ExecutableSlashCommand? {
        return this[commandName]
            ?: values.firstOrNull { it.properties.aliases.contains(commandName) }
    }
}
