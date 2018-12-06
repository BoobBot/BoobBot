package bot.boobbot.commands.bot

import bot.boobbot.BoobBot
import bot.boobbot.flight.AsyncCommand
import bot.boobbot.flight.Category
import bot.boobbot.flight.CommandProperties
import bot.boobbot.flight.Context
import bot.boobbot.misc.AutoPorn
import bot.boobbot.misc.Formats
import bot.boobbot.misc.createHeaders
import bot.boobbot.misc.json
import okhttp3.MediaType
import okhttp3.RequestBody
import java.awt.Color


@CommandProperties(
    description = "autoporn <:p_:475801484282429450>",
    nsfw = true,
    guildOnly = true,
    donorOnly = true,
    category = Category.GENERAL
)
class autoporn : AsyncCommand {

    override suspend fun executeAsync(ctx: Context) {

        if (ctx.args.isEmpty()) {
            return ctx.embed {
                setColor(Color.red)
                setDescription(Formats.error("Missing subcommand\nbbautoporn <subcommand>\nSubcommands: set, delete, status"))
            }
        }

        val types = arrayListOf("gif", "boobs", "ass", "gay")
        when (ctx.args[0]) {

            "set" -> {

                if (ctx.args.size < 2 || ctx.args[1].isEmpty() || !types.contains(ctx.args[1].toLowerCase())) {
                    return ctx.embed {
                        setColor(Color.red)
                        setDescription(Formats.error("Missing Args\nbbautoporn set <type> <#channel>\nTypes: gif,boobs,ass, gay"))
                    }
                }
                if (ctx.message.mentionedChannels.isEmpty()) {
                    return ctx.embed {
                        setColor(Color.red)
                        setDescription(Formats.error("Missing Args\nbbautoporn set <type> <#channel>\nTypes: gif,boobs,ass,gay"))
                    }
                }


                if (AutoPorn.checkExists(ctx.guild!!.id)) {
                    return ctx.embed {
                        setColor(Color.red)
                        setDescription(Formats.error("Auto-porn is already setup for this server"))
                    }
                }

               if (AutoPorn.createGuild(ctx.guild.id, ctx.message.mentionedChannels[0].id, ctx.args[1].toLowerCase())){
                   ctx.send("Yes it worked")
               }
                return ctx.send("broken")

            }

            "delete" -> {

                if (!AutoPorn.checkExists(ctx.guild!!.id)) {
                    return ctx.embed {
                        setColor(Color.red)
                        setDescription(Formats.error("Auto-porn is not setup for this server"))
                    }
                }
                AutoPorn.deleteGuild(ctx.guild.id)
                return ctx.send("done maybe")
            }

            "status" -> {

                val check = BoobBot.requestUtil
                    .get(
                        "http://localhost:5000/api/guilds/${ctx.guild!!.id}",
                        createHeaders(Pair("Authorization", "GAY"))
                    ).await()
                    ?: return ctx.send("rip some error, press f")
                if (check.code() == 404) {
                    check.close()
                    return ctx.embed {
                        setColor(Color.red)
                        setDescription(Formats.error("Auto-porn is not setup for this server"))
                    }
                }


                val data = check.json()!!
                val json = data.getJSONObject("guild").get("channel")
                val c = ctx.guild.getTextChannelById(json.toString())
                return ctx.embed {
                    setColor(Color.red)
                    setDescription(Formats.info("Auto-porn is setup for this server on ${c.asMention}"))
                }
            }

            else -> {
                return ctx.embed {
                    setColor(Color.red)
                    setDescription(Formats.error("Missing subcommand\nbbautoporn <subcommand>\nSubcommands: set, delete, status"))
                }
            }
        }

    }

}