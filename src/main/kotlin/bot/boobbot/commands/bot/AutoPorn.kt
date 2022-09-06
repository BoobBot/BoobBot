package bot.boobbot.commands.bot

import bot.boobbot.BoobBot
import bot.boobbot.entities.framework.*
import bot.boobbot.entities.framework.annotations.CommandProperties
import bot.boobbot.entities.framework.annotations.SubCommand
import bot.boobbot.entities.framework.interfaces.Command
import bot.boobbot.utils.Formats
import net.dv8tion.jda.api.Permission
import net.dv8tion.jda.api.entities.TextChannel
import net.dv8tion.jda.api.exceptions.ErrorResponseException
import java.awt.Color

@CommandProperties(
    aliases = ["ap"],
    description = "AutoPorn, Sub-commands: set, delete, status",
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
    private val typeString = types.keys.joinToString("`, `", prefix = "`", postfix = "`")

    private fun formatWebhookUrl(channelId: String, token: String): String {
        return String.format("https://discordapp.com/api/webhooks/%s/%s", channelId, token)
    }

    override fun execute(ctx: MessageContext) {
        if (!ctx.userCan(Permission.MANAGE_CHANNEL)) {
            return ctx.reply("\uD83D\uDEAB Hey whore, you lack the `MANAGE_CHANNEL` permission needed to do this")
        }

        if (ctx.args.isEmpty()) {
            return ctx.reply {
                setColor(Color.red)
                setDescription(Formats.error("Missing subcommand\nbbautoporn <subcommand>\nSubcommands: `set`, `delete`, `status`"))
            }
        }
    }

    @SubCommand
    fun set(ctx: MessageContext) {
        if (ctx.args.size < 2 ||
            ctx.args[0].isEmpty() ||
            !types.containsKey(ctx.args[0].lowercase()) ||
            ctx.message.mentions.getChannels(TextChannel::class.java).isEmpty()
        ) {
            return ctx.reply {
                setColor(Color.red)
                setDescription(Formats.error("Missing Args\nbbautoporn set <type> <#channel>\nTypes: $typeString"))
            }
        }

        val channel = ctx.message.mentions.getChannels(TextChannel::class.java)[0]

        if (!channel.isNSFW) {
            return ctx.reply {
                setColor(Color.red)
                setDescription(Formats.error("That channel isn't marked NSFW you fuck"))
            }
        }

        if (!ctx.selfMember!!.hasPermission(channel, Permission.MANAGE_WEBHOOKS)) {
            return ctx.reply("\uD83D\uDEAB Hey whore, I need `MANAGE_WEBHOOKS` permission to do this")
        }

        channel.createWebhook("BoobBot")
            .reason("Auto-Porn Setup")
            .submit()
            .thenAccept {
                val url = formatWebhookUrl(it.id, it.token!!)
                val imageType = types[ctx.args[0].lowercase()]!!
                BoobBot.database.setWebhook(ctx.guild!!.id, url, imageType, channel.id)

                ctx.reply {
                    setColor(Color.red)
                    setDescription("Set Auto-Porn channel to ${channel.asMention}")
                }
            }
            .exceptionally {
                val erx = it as ErrorResponseException

                when (erx.errorCode) {
                    30007 -> ctx.reply("The provided channel has too many webhooks, wtf? delete some whore")
                    else -> {
                        BoobBot.log.error("Webhook creation error", it)
                        ctx.reply("Shit, couldn't make a webhook.\n${it.meaning}")
                    }
                }

                return@exceptionally null
            }
    }

    @SubCommand(aliases = ["disable"])
    fun delete(ctx: MessageContext) {
        if (BoobBot.database.getWebhook(ctx.guild!!.id) == null) {
            return ctx.reply {
                setColor(Color.red)
                setDescription("Wtf, this server doesn't even have Auto-Porn set up?")
            }
        }

        BoobBot.database.deleteWebhook(ctx.guild.id)
        ctx.reply {
            setColor(Color.red)
            setDescription("Auto-Porn is now disabled for this server")
        }
    }

    @SubCommand
    fun status(ctx: MessageContext) {
        val wh = BoobBot.database.getWebhook(ctx.guild!!.id)
            ?: return ctx.reply {
                setColor(Color.red)
                setDescription("Wtf, this server doesn't even have Auto-Porn set up?")
            }

        val channel = ctx.guild.getTextChannelById(wh.getString("channelId"))

        if (channel == null) {
            BoobBot.database.deleteWebhook(ctx.guild.id)

            return ctx.reply {
                setColor(Color.red)
                setDescription("The channel used for Auto-Porn no longer exists, wtf?")
            }
        }

        val category = wh.getString("category")

        ctx.reply {
            setColor(Color.red)
            setDescription("Auto-Porn is set up for ${channel.asMention} (**$category**)")
        }
    }
}
