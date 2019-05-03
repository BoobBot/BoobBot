package bot.boobbot.commands.bot

import bot.boobbot.BoobBot
import bot.boobbot.flight.Category
import bot.boobbot.flight.Command
import bot.boobbot.flight.CommandProperties
import bot.boobbot.flight.Context
import bot.boobbot.misc.Colors
import bot.boobbot.misc.Formats
import bot.boobbot.misc.Utils
import bot.boobbot.models.Config
import net.dv8tion.jda.core.EmbedBuilder
import net.dv8tion.jda.core.entities.MessageEmbed
import java.time.Instant

@CommandProperties(
    description = "help, --dm for dm",
    aliases = ["halp", "halllp", "coms", "commands"],
    category = Category.MISC
)
class Help : Command {

    override fun execute(ctx: Context) {
        val prefix = ctx.customPrefix ?: BoobBot.defaultPrefix

        val commands = BoobBot.commands.values
        if (ctx.args.isEmpty() || ctx.args[0] == "--dm") {
            val embeds = mutableListOf<MessageEmbed>()
            val builder = EmbedBuilder()
                .setColor(Colors.getEffectiveColor(ctx.message))
                .setAuthor(
                    "${ctx.selfUser.name} help ${Formats.MAGIC_EMOTE}",
                    "https://boob.bot/commands",
                    ctx.selfUser.effectiveAvatarUrl
                )
                .setFooter("Help requested by ${ctx.author.name}", ctx.author.effectiveAvatarUrl)
                .setTimestamp(Instant.now())

            for (category in Category.values()) {
                val cmds = commands
                    .filter { it.properties.category == category && !it.properties.developerOnly }
                    .joinToString("\n") { "`$prefix${padEnd(it.name)}:` ${it.properties.description}" }

                if (cmds.isNotEmpty()) {
                    builder.addField(category.title, cmds, false)
                }

                if (builder.length() > 4500 || category.ordinal == Category.values().size - 1) { // 4500 chars or last category
                    builder.addField("${Formats.LINK_EMOTE} Links", Formats.LING_MSG, false)
                    embeds.add(builder.build())

                    builder.clearFields()
                }
            }

            if (Config.owners.contains(ctx.author.idLong)) {
                for (category in Category.values()) {
                    val cmds = commands
                        .filter { it.properties.category == category && it.properties.developerOnly }
                        .joinToString("\n") { "`$prefix${padEnd(it.name)}:` ${it.properties.description}" }

                    if (cmds.isNotEmpty()) {
                        builder.addField(category.title, cmds, false)
                    }
                }

                embeds.add(builder.build())
            }

            if (!ctx.args.isEmpty() && ctx.args[0] == "--dm") {
                ctx.message.addReaction("\uD83D\uDCEC").queue()

                for (embed in embeds) {
                    ctx.dm(embed)
                }
            } else {
                for (embed in embeds) {
                    ctx.embed(embed)
                }
            }

            return
        }

        sendCommandHelp(ctx)
    }

    fun sendCommandHelp(ctx: Context) {
        val prefix = ctx.customPrefix ?: BoobBot.defaultPrefix

        val command = Utils.getCommand(ctx.args[0])
            ?: return ctx.send("That command doesn't exist")

        val mappedAliases = command.properties.aliases.joinToString(", ")
        val aliases = if (mappedAliases.isEmpty()) "None" else mappedAliases

        val commandHelp = EmbedBuilder()
            .setColor(Colors.getEffectiveColor(ctx.message))
            .setAuthor(
                "${ctx.selfUser.name} Command Info",
                "https://boob.bot/commands",
                ctx.selfUser.effectiveAvatarUrl
            )
            .addField(
                Formats.info("Info"),
                String.format(
                    "Command:\n**%s%s**\nAliases:\n**%s**\nDescription:\n**%s**",
                    prefix, command.name, aliases, command.properties.description
                ),
                false
            )
            .setFooter("Help requested by ${ctx.author.name}", ctx.author.effectiveAvatarUrl)
            .setTimestamp(Instant.now())

        if (ctx.args.size >= 2 && ctx.args[1].toLowerCase() == "--dm") {
            ctx.dm(commandHelp.build())
            ctx.message.addReaction("\uD83D\uDCEC").queue()
        } else {
            ctx.embed(commandHelp.build())
        }
    }

    private fun padEnd(str: String, length: Int = 15): String {
        return str + "\u200B ".repeat(length - str.length)
    }

}