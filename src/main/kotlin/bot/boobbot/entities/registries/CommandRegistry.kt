package bot.boobbot.entities.registries

import bot.boobbot.BoobBot
import bot.boobbot.entities.framework.annotations.Option
import bot.boobbot.entities.framework.impl.ExecutableCommand
import bot.boobbot.entities.framework.impl.SubCommandWrapper
import bot.boobbot.entities.framework.utils.Indexer
import bot.boobbot.entities.misc.ApiServer
import bot.boobbot.entities.misc.ApiServer.Companion.getContexts
import net.dv8tion.jda.api.interactions.commands.build.*

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

    fun export(): List<CommandData> {
        val allSlashCommands = BoobBot.commands.values.filter { it.slashEnabled }
        val categorised = allSlashCommands.filter { it.category != null }.groupBy { it.category!! }.map(::buildCategory)
        val remaining = allSlashCommands.filter { it.category == null }.map(::buildCommand)

        return categorised + remaining
    }

    private fun buildCategory(entry: Map.Entry<String, List<ExecutableCommand>>): SlashCommandData {
        val (category, cmds) = entry

        if (cmds.size > 25) {
            throw IllegalArgumentException("Cannot have more than 25 subcommands/groups per command!")
        }

        val slash = Commands.slash(category, "$category commands")
            .also {
                it.setContexts(getContexts(entry.value.all { props -> props.properties.guildOnly }))
                it.isNSFW = entry.value.any { props -> props.properties.nsfw }
            }

        for (cmd in cmds) {
            if (cmd.subcommands.isNotEmpty()) {
                val group = SubcommandGroupData(cmd.name, cmd.properties.description).also {
                    it.addSubcommands(cmd.subcommands.values.map(::buildSubcommand))
                }

                slash.addSubcommandGroups(group)
                continue
            }

            val sc = SubcommandData(cmd.name, cmd.properties.description)
                .also { it.addOptions(buildOptions(cmd.options)) }

            slash.addSubcommands(sc)
        }

        return slash
    }

    fun buildCommand(cmd: ExecutableCommand): SlashCommandData {
        return Commands.slash(cmd.name.lowercase(), cmd.properties.description)
            .also {
                it.setContexts(getContexts(cmd.properties.guildOnly))
                it.isNSFW = cmd.properties.nsfw
                it.addOptions(buildOptions(cmd.options))
                it.addSubcommands(cmd.subcommands.values.map(::buildSubcommand))
            }
    }

    fun buildSubcommand(cmd: SubCommandWrapper): SubcommandData {
        return SubcommandData(cmd.name.lowercase(), cmd.description)
            .addOptions(buildOptions(cmd.options))
    }

    private fun buildOptions(options: List<Option>): List<OptionData> {
        return options.map {
            OptionData(it.type, it.name, it.description, it.required).also { data ->
                for (choice in it.choices) {
                    data.addChoice(choice.name, choice.value)
                }
            }
        }
    }
}
