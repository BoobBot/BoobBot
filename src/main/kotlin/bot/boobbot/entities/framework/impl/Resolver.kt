package bot.boobbot.entities.framework.impl

import bot.boobbot.BoobBot
import net.dv8tion.jda.api.entities.Guild
import net.dv8tion.jda.api.entities.GuildChannel
import net.dv8tion.jda.api.entities.TextChannel
import net.dv8tion.jda.api.entities.channel.unions.GuildChannelUnion
import net.dv8tion.jda.api.interactions.commands.OptionMapping

typealias Mapping<T> = (OptionMapping) -> T?
typealias Parser<T> = (String) -> T?

class Resolver<T>(private val mapping: Mapping<T>, private val parser: Parser<T>) {
    fun resolve(s: String): T? = parser(s)

    fun resolve(o: OptionMapping): T? = mapping(o)

    companion object {
        private fun <T> parseSnowflake(id: String, getter: (Long) -> T?): T? {
            return id.toLongOrNull()?.let(getter)
        }

        fun localGuildChannel(guild: Guild): Resolver<GuildChannel> {
            return Resolver(OptionMapping::getAsChannel) { arg ->
                when {
                    arg.length > 1 && arg.startsWith('#') -> {
                        val parsed = arg.drop(1)
                        return@Resolver parseSnowflake(parsed, guild::getGuildChannelById)
                            ?: guild.channels.firstOrNull { it.name.equals(arg, true) }
                    }
                    arg.length > 1 && arg.startsWith('<') -> {
                        val parsed = arg.dropWhile { !it.isDigit() }.takeWhile { it.isDigit() }
                        return@Resolver parseSnowflake(parsed, guild::getGuildChannelById)
                    }
                    else -> return@Resolver parseSnowflake(arg, guild::getGuildChannelById)
                }
            }
        }

        val STRING = Resolver(OptionMapping::getAsString) { it }
        val INTEGER = Resolver(OptionMapping::getAsInt, String::toIntOrNull)
    }
}
