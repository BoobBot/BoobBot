package bot.boobbot.commands.dev

import bot.boobbot.BoobBot
import bot.boobbot.flight.Category
import bot.boobbot.flight.Command
import bot.boobbot.flight.CommandProperties
import bot.boobbot.flight.Context
import bot.boobbot.misc.Formats
import net.dv8tion.jda.core.Permission
import net.dv8tion.jda.core.entities.Game
import net.dv8tion.jda.core.entities.Icon
import java.net.URL


@CommandProperties(description = "Settings", category = Category.DEV, developerOnly = true)
class Set : Command {


    override fun execute(ctx: Context) {

        when (ctx.args[0]) {

            "name" -> {

                ctx.jda.selfUser.manager.setName(ctx.args.sliceArray(1 until ctx.args.size).joinToString(" ")).queue({ ctx.send(Formats.info("Set UserName to ${ctx.args.sliceArray(1 until ctx.args.size).joinToString(" ")}")) }, { ctx.send(Formats.error(" Failed to set UserName")) })

            }

            "game" -> {

                when (ctx.args[1]) {

                    "playing" -> {

                        BoobBot.setGame = true
                        ctx.jda.asBot().shardManager.setGame(Game.playing(ctx.args.sliceArray(2 until ctx.args.size).joinToString(" ")))
                        ctx.send(Formats.info("Yes daddy, game set"))

                    }

                    "listening" -> {

                        BoobBot.setGame = true
                        ctx.jda.asBot().shardManager.setGame(Game.listening(ctx.args.sliceArray(2 until ctx.args.size).joinToString(" ")))
                        ctx.send(Formats.info("Yes daddy, game set"))

                    }

                    "watching" -> {

                        BoobBot.setGame = true
                        ctx.jda.asBot().shardManager.setGame(Game.watching(ctx.args.sliceArray(2 until ctx.args.size).joinToString(" "))) // There is probly a better way to do this
                        ctx.send(Formats.info("Yes daddy, game set"))

                    }

                    "stream" -> {

                        BoobBot.setGame = true
                        ctx.jda.asBot().shardManager.setGame(Game.streaming(ctx.args.sliceArray(3 until ctx.args.size).joinToString(" "), ctx.args[2]))
                        ctx.send(Formats.info("Yes daddy, Stream set"))

                    }

                    "clear" -> {

                        BoobBot.setGame = false
                        ctx.jda.asBot().shardManager.setGame(Game.playing("bbhelp || bbinvite"))
                        ctx.send(Formats.info("Yes daddy, cleared game"))

                    }

                    else -> {
                        //todo send com help
                    }
                }

            }

            "nick" -> {
                if (ctx.botCan(Permission.NICKNAME_CHANGE)) {
                    ctx.guild?.controller?.setNickname(ctx.selfMember, ctx.args.sliceArray(1 until ctx.args.size).joinToString(" "))?.reason("BoobBot nick set")?.queue({ ctx.send(Formats.info("Yes daddy, nick set")) }, { ctx.send(Formats.error(" Failed to set nick")) })
                }

            }

            "avatar" -> {

                val url = URL(ctx.args[1])
                val connection = url.openConnection()
                connection.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 6.1; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/61.0.3163.100 Safari/537.36")
                connection.connect()
                val icon = Icon.from(connection.getInputStream())
                ctx.jda.selfUser.manager.setAvatar(icon).queue({ ctx.send(Formats.info("Yes daddy, avatar set")) }, { ctx.send(Formats.error(" Failed to set avatar")) })
                BoobBot.log.info("Setting New Avatar")

            }

            else -> {
                //todo send com help
            }

        }
    }

}