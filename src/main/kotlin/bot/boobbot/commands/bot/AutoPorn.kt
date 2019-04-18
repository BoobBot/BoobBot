package bot.boobbot.commands.bot

import bot.boobbot.BoobBot
import bot.boobbot.flight.Category
import bot.boobbot.flight.Command
import bot.boobbot.flight.CommandProperties
import bot.boobbot.flight.Context
import bot.boobbot.misc.Formats
import net.dv8tion.jda.core.Permission
import net.dv8tion.jda.core.exceptions.ErrorResponseException
import java.awt.Color


@CommandProperties(
    aliases = ["ap"],
    description = "AutoPorn, Sub-commands: set, delete, status <:p_:475801484282429450>",
    nsfw = true,
    guildOnly = true,
    category = Category.MISC,
    donorOnly = true
)
class AutoPorn : Command {

    private val types = mapOf(
        "gif" to "Gifs",
        "boobs" to "boobs",
        "ass" to "ass",
        "gay" to "gay",
        "random" to "nsfw"
    )
    private val typeString = types.entries.joinToString(", ")

    fun formatWebhookUrl(channelId: String, token: String): String {
        return String.format("https://discordapp.com/api/webhooks/%s/%s", channelId, token)
    }

    override fun execute(ctx: Context) {
        if (!ctx.userCan(Permission.MANAGE_CHANNEL)) {
            return ctx.send("\uD83D\uDEAB Hey whore, you lack the `MANAGE_CHANNEL` permission needed to do this")
        }

        if (ctx.args.isEmpty()) {
            return ctx.embed {
                setColor(Color.red)
                setDescription(Formats.error("Missing subcommand\nbbautoporn <subcommand>\nSubcommands: set, delete, status"))
            }
        }

        when (ctx.args[0]) {
            "set" -> {
                if (ctx.args.size < 2 ||
                    ctx.args[1].isEmpty() ||
                    !types.containsKey(ctx.args[1].toLowerCase()) ||
                    ctx.message.mentionedChannels.isEmpty()
                ) {
                    return ctx.embed {
                        setColor(Color.red)
                        setDescription(Formats.error("Missing Args\nbbautoporn set <type> <#channel>\nTypes: $typeString"))
                    }
                }

                val channel = ctx.message.mentionedChannels[0]

                if (!channel.isNSFW) {
                    return ctx.embed {
                        setColor(Color.red)
                        setDescription(Formats.error("That channel isn't marked NSFW you fuck"))
                    }
                }

                if (!ctx.selfMember!!.hasPermission(channel, Permission.MANAGE_WEBHOOKS)) {
                    return ctx.send("\uD83D\uDEAB Hey whore, I need `MANAGE_WEBHOOKS` permission to do this")
                }

                channel.createWebhook("BoobBot").reason("Auto-Porn setup").queue({
                    val url = formatWebhookUrl(it.id, it.token!!)
                    val imageType = types.getValue(ctx.args[1].toLowerCase())
                    BoobBot.database.setWebhook(ctx.guild!!.id, url, imageType, channel.id)

                    ctx.embed {
                        setColor(Color.red)
                        setDescription("Set Auto-Porn channel to ${channel.asMention}")
                    }
                }, {
                    val erx = it as ErrorResponseException

                    when (erx.errorCode) {
                        30007 -> ctx.send("The provided channel has too many webhooks, wtf? delete some whore")
                        else -> {
                            BoobBot.log.error("Webhook creation error", it)
                            ctx.send("Shit, couldn't make a webhook.\n${it.meaning}")
                        }
                    }
                })
            }

            "delete", "disable" -> {
                if (BoobBot.database.getWebhook(ctx.guild!!.id) == null) {
                    return ctx.embed {
                        setColor(Color.red)
                        setDescription("Wtf, this server doesn't even have Auto-Porn set up?")
                    }
                }

                BoobBot.database.deleteWebhook(ctx.guild.id)
                ctx.embed {
                    setColor(Color.red)
                    setDescription("Auto-Porn is now disabled for this server")
                }
            }

            "status" -> {
                val wh = BoobBot.database.getWebhook(ctx.guild!!.id) ?: return ctx.embed {
                    setColor(Color.red)
                    setDescription("Wtf, this server doesn't even have Auto-Porn set up?")
                }

                val channel = ctx.guild.getTextChannelById(wh.getString("channelId"))

                if (channel == null) {
                    BoobBot.database.deleteWebhook(ctx.guild.id)

                    return ctx.embed {
                        setColor(Color.red)
                        setDescription("The channel used for Auto-Porn no longer exists, wtf?")
                    }
                }

                val category = wh.getString("category")

                ctx.embed {
                    setColor(Color.red)
                    setDescription("Auto-Porn is set up for ${channel.asMention} (**$category**)")
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