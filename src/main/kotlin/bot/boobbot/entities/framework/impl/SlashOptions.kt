package bot.boobbot.entities.framework.impl

import bot.boobbot.entities.framework.interfaces.Options
import net.dv8tion.jda.api.interactions.commands.OptionMapping

class SlashOptions(private val options: List<OptionMapping>) : Options {
    override fun <T> getByNameOrNext(name: String, resolver: Resolver<T>): T? {
        return options.firstOrNull { it.name == name }?.let(resolver::resolve)
    }

    override fun getOptionStringOrGather(name: String): String? {
        return options.firstOrNull { it.name == name }?.let(OptionMapping::getAsString)
    }

    override fun raw(): List<String> {
        throw UnsupportedOperationException("SlashOptions does not support raw()!")
    }
}
