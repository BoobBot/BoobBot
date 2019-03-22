package bot.boobbot.commands.dev

import bot.boobbot.BoobBot
import bot.boobbot.flight.Category
import bot.boobbot.flight.Command
import bot.boobbot.flight.CommandProperties
import bot.boobbot.flight.Context
import bot.boobbot.misc.Formats


@CommandProperties(description = "Settings", category = Category.DEV, developerOnly = true)
class Set : Command {


    override fun execute(ctx: Context) {

        when (ctx.args[0]) {

            "name" -> {
                val newName = ctx.args.drop(1).joinToString(" ")

                ctx.jda.selfUser.manager.setName(newName).queue(
                    { ctx.send(Formats.info("Set UserName to $newName")) },
                    { ctx.send(Formats.error(" Failed to set UserName")) }
                )

            }

            "game" -> {

                val game = ctx.args.drop(2).joinToString(" ")

                when (ctx.args[1]) {

                    "playing" -> {

                        BoobBot.setGame = true
                        ctx.jda.asBot().shardManager.setGame(Game.playing(game))
                        ctx.send(Formats.info("Yes daddy, game set"))

                    }

                    "listening" -> {

                        BoobBot.setGame = true
                        ctx.jda.asBot().shardManager.setGame(Game.listening(game))
                        ctx.send(Formats.info("Yes daddy, game set"))

                    }

                    "watching" -> {

                        BoobBot.setGame = true
                        ctx.jda.asBot()
                            .shardManager.setGame(Game.watching(game)) // There is probly a better way to do this
                        ctx.send(Formats.info("Yes daddy, game set"))

                    }

                    "stream" -> {

                        val url = ctx.args[2]
                        val name = ctx.args.drop(3).joinToString(" ")

                        BoobBot.setGame = true
                        ctx.jda.asBot().shardManager.setGame(Game.streaming(name, url))
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
                if (ctx.guild == null) {
                    return ctx.send("This can only be run in a guild")
                }

                if (!ctx.botCan(Permission.NICKNAME_CHANGE)) {
                    ctx.guild.controller.setNickname(ctx.selfMember, ctx.args.drop(1).joinToString(" "))
                        .reason("BoobBot nick set")
                        .queue(
                            { ctx.send(Formats.info("Yes daddy, nick set")) },
                            { ctx.send(Formats.error(" Failed to set nick")) }
                        )
                }

            }

            "avatar" -> {

                BoobBot.requestUtil.get(ctx.args[1]).queue {
                    val image = it?.body()?.byteStream() ?: return@queue ctx.send("Unable to fetch avatar")

                    ctx.jda.selfUser.manager.setAvatar(Icon.from(image)).queue(
                        { ctx.send(Formats.info("Yes daddy, avatar set")) },
                        { ctx.send(Formats.error(" Failed to set avatar")) }
                    )
                    BoobBot.log.info("Setting New Avatar")
                    BoobBot.manSetAvatar = true
                }

            }

            "icons" -> {

                BoobBot.requestUtil.get(ctx.args[1]).queue {
                    val image = it?.body()?.byteStream() ?: return@queue ctx.send("Unable to fetch image")
                    val icon = Icon.from(image)

                    BoobBot.home?.manager?.setIcon(icon)?.queue()
                    ctx.jda.selfUser.manager.setAvatar(icon).queue(
                        { ctx.send(Formats.info("Yes daddy, icons set")) },
                        { ctx.send(Formats.error(" Failed to set avatar")) }
                    )
                    BoobBot.log.info("Setting New icons")
                    BoobBot.manSetAvatar = true
                }

            }


            else -> {
                //todo send com help
            }

        }
    }

}