package bot.boobbot.entities.framework.impl

import net.dv8tion.jda.api.interactions.commands.OptionMapping

typealias Mapping<T> = (OptionMapping) -> T?
typealias Parser<T> = (String) -> T?

class Resolver<T>(private val mapping: Mapping<T>, private val parser: Parser<T>) {
    fun resolve(s: String): T? = parser(s)

    fun resolve(o: OptionMapping): T? = mapping(o)

    companion object {

    }
}
