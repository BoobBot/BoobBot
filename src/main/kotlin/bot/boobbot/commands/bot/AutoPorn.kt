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
import net.dv8tion.jda.core.Permission
import okhttp3.MediaType
import okhttp3.RequestBody
import java.awt.Color


@CommandProperties(
    description = "AutoPorn, Sub-commands: set, delete, status <:p_:475801484282429450>",
    nsfw = true,
    guildOnly = true,
    category = Category.MISC,
    donorOnly = true
)
class AutoPorn : AsyncCommand {

    override suspend fun executeAsync(ctx: Context) {

        if (!ctx.userCan(Permission.MANAGE_CHANNEL)) {
            return ctx.send("\uD83D\uDEAB Hey whore, you lack the `MANAGE_CHANNEL` permission needed to do this")
        }

        if (ctx.args.isEmpty()) {
            return ctx.embed {
                setColor(Color.red)
                setDescription(Formats.error("Missing subcommand\nbbautoporn <subcommand>\nSubcommands: set, delete, status"))
            }
        }

        val types = arrayListOf("gif", "boobs", "ass", "gay", "random")
        when (ctx.args[0]) {

            "set" -> {

                if (ctx.args.size < 2 || ctx.args[1].isEmpty() || !types.contains(ctx.args[1].toLowerCase())) {
                    return ctx.embed {
                        setColor(Color.red)
                        setDescription(Formats.error("Missing Args\nbbautoporn set <type> <#channel>\nTypes: gif,boobs,ass,gay,random"))
                    }
                }
                if (ctx.message.mentionedChannels.isEmpty()) {
                    return ctx.embed {
                        setColor(Color.red)
                        setDescription(Formats.error("Missing Args\nbbautoporn set <type> <#channel>\nTypes: gif,boobs,ass,gay,random"))
                    }
                }
                if (!ctx.message.mentionedChannels[0].isNSFW) {
                    return ctx.embed {
                        setColor(Color.red)
                        setDescription(Formats.error("That's not a nsfw channel you fuck"))
                    }
                }


                if (AutoPorn.checkExists(ctx.guild!!.id)) {
                    return ctx.embed {
                        setColor(Color.red)
                        setDescription(Formats.error("Auto-porn is already setup for this server"))
                    }
                }

               if (AutoPorn.createGuild(ctx.guild.id, ctx.message.mentionedChannels[0].id, ctx.args[1].toLowerCase())){
                   return ctx.embed {
                       setColor(Color.green)
                       setDescription(Formats.info("Auto-porn is setup for this server on ${ctx.message.mentionedChannels[0].asMention}"))
                   }
               }
                return ctx.embed {
                    setColor(Color.red)
                    setDescription(Formats.error("Shit, some error try again"))
                }

            }

            "delete" -> {

                if (!AutoPorn.checkExists(ctx.guild!!.id)) {
                    return ctx.embed {
                        setColor(Color.red)
                        setDescription(Formats.error("Auto-porn is not setup for this server"))
                    }
                }
                AutoPorn.deleteGuild(ctx.guild.id)
                return ctx.send(Formats.info("done, whore!"))
            }

            "status" -> {

                if (!AutoPorn.checkExists(ctx.guild!!.id)) {
                    return ctx.embed {
                        setColor(Color.red)
                        setDescription(Formats.error("Auto-porn is not setup for this server"))
                    }
                }
                val id = AutoPorn.getStatus(ctx.guild.id)
                BoobBot.log.info(id.length.toString())
                if (id.length < 6){
                    return ctx.embed {
                        setColor(Color.red)
                        setDescription(Formats.error("Auto-porn is not setup for this server"))
                    }
                }
                val c = ctx.guild.getTextChannelById(AutoPorn.getStatus(ctx.guild.id))
                if (c == null){AutoPorn.deleteGuild(ctx.guild.id)}
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