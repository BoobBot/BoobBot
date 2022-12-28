package bot.boobbot.entities.framework.impl

import bot.boobbot.BoobBot
import net.dv8tion.jda.api.entities.Guild
import net.dv8tion.jda.api.entities.Member
import net.dv8tion.jda.api.entities.channel.middleman.GuildChannel
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

        fun member(guild: Guild): Resolver<Member> {
            return Resolver(OptionMapping::getAsMember) { arg ->
                when {
                    arg.length > 1 && arg.startsWith('@') -> {
                        val parsed = arg.drop(1)
                        return@Resolver guild.memberCache.find { m -> m.user.name.equals(parsed, true) }
                    }
                    arg.length > 1 && arg.startsWith('<') -> {
                        val parsed = arg.dropWhile { !it.isDigit() }.takeWhile { it.isDigit() }
                        return@Resolver parseSnowflake(parsed, guild::getMemberById)
                    }
                    else -> return@Resolver parseSnowflake(arg, guild::getMemberById)
                }
            }
        }

        val STRING = Resolver(OptionMapping::getAsString) { it.takeIf { it.isNotEmpty() } }
        val INTEGER = Resolver(OptionMapping::getAsInt, String::toIntOrNull)
        val USER = Resolver(OptionMapping::getAsUser) { arg ->
            when {
                arg.length > 1 && arg.startsWith('@') -> {
                    val parsed = arg.drop(1)
                    return@Resolver BoobBot.shardManager.userCache.find { u -> u.name.equals(parsed, true) }
                }
                arg.length > 1 && arg.startsWith('<') -> {
                    val parsed = arg.dropWhile { !it.isDigit() }.takeWhile { it.isDigit() }
                    return@Resolver parseSnowflake(parsed, BoobBot.shardManager::getUserById)
                }
                else -> return@Resolver parseSnowflake(arg, BoobBot.shardManager::getUserById)
            }
        }
    }
}
