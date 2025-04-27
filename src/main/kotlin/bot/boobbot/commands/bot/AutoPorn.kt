package bot.boobbot.commands.bot

import bot.boobbot.BoobBot
import bot.boobbot.entities.framework.Context
import bot.boobbot.entities.framework.annotations.Choice
import bot.boobbot.entities.framework.annotations.CommandProperties
import bot.boobbot.entities.framework.annotations.Option
import bot.boobbot.entities.framework.annotations.SubCommand
import bot.boobbot.entities.framework.impl.Resolver
import bot.boobbot.entities.framework.impl.Resolver.Companion
import bot.boobbot.entities.framework.interfaces.Command
import bot.boobbot.utils.Formats
import io.sentry.Sentry
import kotlinx.coroutines.future.await
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
    donorOnly = true,
    userPermissions = [Permission.MANAGE_SERVER]
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
        sendSubcommandHelp(ctx)
    }

    @SubCommand(description = "Add a Auto-Porn schedule.")
    @Option(name = "channel", description = "The channel to post to.", type = OptionType.CHANNEL)
    @Option(name = "category", description = "The image category.", choices = [
        Choice("GIF", "gif"),
        Choice("Boobs", "boobs"),
        Choice("Ass", "ass"),
        Choice("Gay", "gay"),
        Choice("Lesbians", "lesbians"),
        Choice("Random", "random")
    ])
    suspend fun add(ctx: Context) {
        val hooks = BoobBot.database.getWebhooks(ctx.guild.idLong)

        if (hooks.size >= MAX_CONFIGURATIONS_PER_GUILD) {
            return ctx.reply("This server has **${hooks.size}** auto-porn configurations (maximum **${MAX_CONFIGURATIONS_PER_GUILD}**). Delete some or fuck off.")
        }

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

        if (hooks.any { it.channelId == channel.idLong && it.category == imageCategory }) {
            return ctx.reply("You already have `$imageCategory` posting to ${channel.asMention} whore.")
        }

        if (!ctx.selfMember!!.hasPermission(channel, Permission.MANAGE_WEBHOOKS)) {
            return ctx.reply("\uD83D\uDEAB Hey whore, I need `MANAGE_WEBHOOKS` permission to do this")
        }

        ctx.defer()

        val res = channel.runCatching {
            createWebhook("BoobBot")
                .reason("Auto-Porn Setup")
                .submit()
                .await()
        }

        if (res.isFailure) {
            val error = res.exceptionOrNull()
                ?: return ctx.reply("Couldn't create a webhook for this channel, but there was no error either wtf?")

            error.printStackTrace()
            Sentry.capture(error)

            if (error !is ErrorResponseException) {
                return ctx.reply("An error occurred whilst creating a webhook for autoporn, wtf")
            }

            when (error.errorCode) {
                30007 -> ctx.reply("The provided channel has too many webhooks, wtf? delete some whore")
                else -> {
                    BoobBot.log.error("Webhook creation error", error)
                    ctx.reply("Shit, couldn't make a webhook.\n${error.meaning}")
                }
            }
        }

        val webhook = res.getOrThrow()
        val url = formatWebhookUrl(webhook.id, webhook.token!!)
        BoobBot.database.setWebhook(ctx.guild.idLong, url, imageCategory, channel.idLong)

        ctx.reply {
            setColor(Color.red)
            setDescription("Added `$imageCategory` to ${channel.asMention} auto-porn schedule, whore.\nYou can remove this with `/autoporn delete #${channel.name} $imageCategory`")
        }
    }

    @SubCommand(description = "Clear all Auto-Porn configurations for this server.")
    fun clear(ctx: Context) {
        BoobBot.database.deleteWebhooks(ctx.guild.idLong)
        ctx.reply("Auto-Porn configurations cleared, whore.")
    }

    @SubCommand(aliases = ["disable"], description = "Delete an Auto-Porn configuration for this server.")
    @Option("channel", description = "The channel to remove the configuration for.", type = OptionType.CHANNEL)
    @Option(name = "category", description = "The type of post to remove. Omit to remove ALL configurations.", choices = [
        Choice("GIF", "gif"),
        Choice("Boobs", "boobs"),
        Choice("Ass", "ass"),
        Choice("Gay", "gay"),
        Choice("Lesbians", "lesbians"),
        Choice("Random", "random")
    ])
    fun delete(ctx: Context) {
        val hooks = BoobBot.database.getWebhooks(ctx.guild.idLong).takeIf { it.isNotEmpty() }
            ?: return ctx.reply {
                setColor(Color.red)
                setDescription("Wtf, this server doesn't even have Auto-Porn set up?")
            }

        val channel = ctx.options.getByNameOrNext("channel", Resolver.localGuildChannel(ctx.guild))
            ?: return ctx.reply("You need to mention the channel that you want to disable auto-porn for, whore.")

        val category = ctx.options.getByNameOrNext("category", Resolver.STRING)
            ?.let(types::get)
            ?: return ctx.reply {
                setColor(Color.red)
                setDescription(Formats.error("Invalid Category\nCategories: $typeString"))
            }

        val toDelete = hooks.firstOrNull { it.channelId == channel.idLong && it.category == category }
            ?: return ctx.reply("No matching auto-porn configuration found, whore. Use `/autoporn status` to see which channels are set up or fuck off.")

        BoobBot.database.deleteWebhook(ctx.guild.idLong, toDelete.channelId, category)

        ctx.reply {
            setColor(Color.red)
            setDescription("I've disabled auto-porn for that channel, whore.")
        }
    }

    @SubCommand(description = "View the Auto-Porn configuration for this server.")
    fun status(ctx: Context) {
        val hooks = BoobBot.database.getWebhooks(ctx.guild.idLong).takeIf { it.isNotEmpty() }
            ?: return ctx.reply {
                setColor(Color.red)
                setDescription("Wtf, this server doesn't even have Auto-Porn set up?")
            }

        val statuses = buildString {
            for ((index, config) in hooks.withIndex()) {
                val channel = ctx.guild.getTextChannelById(config.channelId)

                append("`${index + 1}.` Posting `")
                append(config.category)
                append("` to ")

                if (channel == null) {
                    // delete V2 as the get call should've migrated this for us.
                    BoobBot.database.deleteWebhook(ctx.guild.idLong, config.channelId)
                    appendLine("Deleted Channel\n*This entry has been automatically deleted.*\n")
                } else {
                    appendLine(channel.asMention)
                    appendLine()
                }
            }
        }

        ctx.reply {
            setColor(Color.red)
            setTitle("Auto-Porn Statuses")
            setDescription(statuses)
        }
    }

    companion object {
        private const val MAX_CONFIGURATIONS_PER_GUILD = 5
    }
}
