package bot.boobbot.commands.bot

import bot.boobbot.BoobBot
import bot.boobbot.entities.framework.Context
import bot.boobbot.entities.framework.annotations.*
import bot.boobbot.entities.framework.impl.Resolver
import bot.boobbot.entities.framework.interfaces.Command
import bot.boobbot.utils.Formats
import net.dv8tion.jda.api.Permission
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel
import net.dv8tion.jda.api.exceptions.ErrorResponseException
import net.dv8tion.jda.api.interactions.commands.OptionType
import java.awt.Color

@CommandProperties(
    aliases = ["ap"],
    description = "AutoPorn, Sub-commands: set, delete, status",
    nsfw = true,
    guildOnly = true,
    donorOnly = true
)
class AutoPorn : Command {
    private val types = mapOf(
        "gif" to "Gifs",
        "boobs" to "boobs",
        "ass" to "ass",
        "gay" to "gay",
        "lesbians" to "lesbians",
        "random" to "nsfw"
    )
    private val typeString = types.keys.joinToString("`, `", prefix = "`", postfix = "`")

    private fun formatWebhookUrl(channelId: String, token: String) = "https://discordapp.com/api/webhooks/%s/%s".format(channelId, token)

    override fun execute(ctx: Context) {
        if (!ctx.userCan(Permission.MANAGE_CHANNEL)) {
            return ctx.reply("\uD83D\uDEAB Hey whore, you lack the `MANAGE_CHANNEL` permission needed to do this")
        }

        sendSubcommandHelp(ctx)
    }

    @SubCommand(description = "Set the Auto-Porn category and channel.")
    @Options([ // TODO: Revisit
        Option(name = "category", description = "The image category.", choices = [
            Choice("GIF", "gif"),
            Choice("Boobs", "boobs"),
            Choice("Ass", "ass"),
            Choice("Gay", "gay"),
            Choice("Lesbians", "lesbians"),
            Choice("Random", "random")
        ]),
        Option(name = "channel", description = "The channel to post to.", type = OptionType.CHANNEL)
    ])
    fun set(ctx: Context) {
        val imageCategory = ctx.options.getByNameOrNext("category", Resolver.STRING)
            ?.let(types::get)
            ?: return ctx.reply {
                setColor(Color.red)
                setDescription(Formats.error("Invalid Category\n/autoporn set <category> <#channel>\nCategories: $typeString"))
            }

        val channel = ctx.options.getByNameOrNext("channel", Resolver.localGuildChannel(ctx.guild)) as? TextChannel
            ?: return ctx.reply {
                setColor(Color.red)
                setDescription(Formats.error("Invalid Channel\n/autoporn set <category> <#channel>\nTypes: $typeString"))
            }

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
                BoobBot.database.setWebhook(ctx.guild.id, url, imageCategory, channel.id)

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

    @SubCommand(aliases = ["disable"], description = "Delete the Auto-Porn configuration for this server.")
    fun delete(ctx: Context) {
        if (BoobBot.database.getWebhook(ctx.guild.id) == null) {
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

    @SubCommand(description = "View the Auto-Porn configuration for this server.")
    fun status(ctx: Context) {
        val wh = BoobBot.database.getWebhook(ctx.guild.id)
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
