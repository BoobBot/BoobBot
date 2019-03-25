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
import net.dv8tion.jda.core.Permission
import java.time.Instant

@CommandProperties(
    description = "help, --dm for dm",
    aliases = ["halp", "halllp", "coms", "commands"],
    category = Category.MISC
)
class Help : Command {

    override fun execute(ctx: Context) {
        val commands = BoobBot.commands.values

        if (ctx.args.isEmpty() || ctx.args[0] == "--dm") {
            val builder = EmbedBuilder()
                .setColor(Colors.getEffectiveColor(ctx.message))
                .setAuthor(
                    "${ctx.selfUser?.name} help ${Formats.MAGIC_EMOTE}",
                    ctx.selfUser.jda.asBot().getInviteUrl(Permission.ADMINISTRATOR),
                    ctx.selfUser.effectiveAvatarUrl
                )

            Category.values().forEach { category ->
                val list = commands
                    .filter { it.properties.category == category && !it.properties.developerOnly }
                    .joinToString("\n") { "`bb${padEnd(it.name)}:` ${it.properties.description}" }

                if (list.isNotEmpty()) {
                    builder.addField(category.title, list, false)
                }
            }

            builder.addField("${Formats.LINK_EMOTE} Links", Formats.LING_MSG, false)
            builder.setFooter("Help requested by ${ctx.author.name}", ctx.author.effectiveAvatarUrl)
            builder.setTimestamp(Instant.now())

            if (!ctx.args.isEmpty() && ctx.args[0] == "--dm") {
                ctx.message.addReaction("\uD83D\uDCEC").queue()
                ctx.dm(builder.build())
            } else {
                ctx.embed(builder.build())
            }

            if (Config.owners.contains(ctx.author.idLong)) {
                val d = EmbedBuilder()
                    .setTitle("You're a developer!")
                    .setColor(Colors.getEffectiveColor(ctx.message))

                Category.values().forEach { category ->
                    val list = commands
                        .filter { it.properties.category == category && it.properties.developerOnly }
                        .joinToString("\n") { "`bb${padEnd(it.name)}:` ${it.properties.description}" }

                    if (list.isNotEmpty()) {
                        d.addField(category.title, list, false)
                    }
                }

                if (!ctx.args.isEmpty() && ctx.args[0] == "--dm") {
                    ctx.dm(d.build())
                } else {
                    ctx.embed(d.build())
                }
            }

            return
        }

        val command = Utils.getCommand(ctx.args[0])
            ?: return ctx.send("That command doesn't exist")

        val mappedAliases = command.properties.aliases.joinToString(", ")
        val aliases = if (mappedAliases.isEmpty()) "None" else mappedAliases

        val commandHelp = EmbedBuilder()
            .setColor(Colors.getEffectiveColor(ctx.message))
            .setAuthor(
                "${ctx.selfUser.name} Command Info",
                ctx.selfUser.jda.asBot().getInviteUrl(Permission.ADMINISTRATOR),
                ctx.selfUser.effectiveAvatarUrl
            )
            .addField(
                Formats.info("Info"),
                String.format(
                    "Command:\n**%s%s**\nAliases:\n**%s**\nDescription:\n**%s**",
                    "bb", command.name, aliases, command.properties.description
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