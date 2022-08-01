package bot.boobbot.commands.interactions

import bot.boobbot.BoobBot
import bot.boobbot.entities.framework.*
import bot.boobbot.utils.Colors
import bot.boobbot.utils.Formats
import bot.boobbot.utils.json
import okhttp3.Headers
import okhttp3.Headers.Companion.headersOf
import org.jetbrains.kotlin.builtins.StandardNames.FqNames.target
import java.awt.Color
import java.time.Instant

@CommandProperties(description = "Fuck someone.", category = Category.FUN, aliases = ["bang"], nsfw = true)
class Fuck : BbApiInteractionCommand("fuck", "<:bunnyfuck:505072924449964053> %s fucks %s")
