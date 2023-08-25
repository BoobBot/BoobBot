package bot.boobbot.entities.registries

import bot.boobbot.BoobBot
import bot.boobbot.entities.framework.impl.ExecutableUserContextCommand
import bot.boobbot.entities.framework.utils.UserContextIndexer
import net.dv8tion.jda.api.interactions.commands.Command
import net.dv8tion.jda.api.interactions.commands.build.CommandData
import net.dv8tion.jda.api.interactions.commands.build.Commands
import kotlin.script.experimental.jvm.JvmScriptEvaluationConfigurationBuilder.Companion.data

class UserContextCommandRegistry : HashMap<String, ExecutableUserContextCommand>() {
    init {
        val indexer = UserContextIndexer("bot.boobbot.contextual.user")
        val commands = indexer.getCommands().associateBy { it.name }
        this.putAll(commands)
        BoobBot.log.info("Successfully loaded ${commands.size} context commands!")
    }

    fun findCommand(commandName: String): ExecutableUserContextCommand? {
        return this[commandName] ?: values.firstOrNull { commandName in it.properties.aliases }
    }

    fun export(): List<CommandData> {
        return this.values.map {
            Commands.user(it.name)
                .setGuildOnly(it.properties.guildOnly)
                .setNSFW(it.properties.nsfw)
        }
    }
}
