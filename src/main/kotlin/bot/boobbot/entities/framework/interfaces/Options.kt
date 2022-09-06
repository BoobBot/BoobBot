package bot.boobbot.entities.framework.interfaces

import bot.boobbot.entities.framework.impl.Resolver

interface Options {
    fun <T> getByNameOrNext(name: String, resolver: Resolver<T>): T?
}
