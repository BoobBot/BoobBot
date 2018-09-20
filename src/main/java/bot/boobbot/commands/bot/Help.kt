package bot.boobbot.commands.bot

import bot.boobbot.BoobBot
import bot.boobbot.flight.Command
import bot.boobbot.flight.CommandProperties
import bot.boobbot.flight.Context
import bot.boobbot.misc.Colors
import bot.boobbot.misc.Formats
import bot.boobbot.misc.Utils
import net.dv8tion.jda.core.EmbedBuilder
import net.dv8tion.jda.core.Permission
import java.time.Instant

@CommandProperties(description = "help, --dm for dm", aliases = ["halp", "halllp", "coms", "commands"], category = CommandProperties.category.MISC)
class Help : Command {

    override fun execute(ctx: Context) {
        val commands = BoobBot.getCommands().values
        val builder = EmbedBuilder()

        builder.setAuthor(
                "${ctx.selfUser.name} help ${Formats.MAGIC_EMOTE}",
                ctx.jda.asBot().getInviteUrl(Permission.ADMINISTRATOR),
                ctx.selfUser.effectiveAvatarUrl
        )

        builder.setColor(Colors.getEffectiveColor(ctx.message))

        CommandProperties.category.values().forEach { category ->
            val list = commands
                    .filter { it.properties.category == category }
                    .joinToString("\n") { "`bb${padEnd(it.name)}:` ${it.properties.description}" }

            builder.addField(category.title, list, false)
        }

        builder.addField("${Formats.LINK_EMOTE} Links", Formats.LING_MSG, false)
        builder.setFooter("Help requested by ${ctx.author.name}", ctx.author.effectiveAvatarUrl)
        builder.setTimestamp(Instant.now())

        if (ctx.args.isEmpty()) {
            return ctx.embed(builder.build())
        }

        if (ctx.args[0].toLowerCase() == "--dm") {
            ctx.message.addReaction("\uD83D\uDCEC").queue(null, null)
            return ctx.dm(builder.build())

        }

        val command = Utils.getCommand(ctx.args[0])
                ?: return ctx.send("That command doesn't exist")


        val mappedAliases = command.properties.aliases.joinToString(", ")
        val aliases = if (mappedAliases.isEmpty()) "None" else mappedAliases

        val commandHelp = EmbedBuilder()
                .setColor(Colors.getEffectiveColor(ctx.message))
                .setAuthor("${ctx.selfUser.name} Command Info",
                        ctx.jda.asBot().getInviteUrl(Permission.ADMINISTRATOR),
                        ctx.selfUser.effectiveAvatarUrl)
                .addField(Formats.info("Info"),
                        String.format("Command:\n**%s%s**\nAliases:\n**%s**\nDescription:\n**%s**",
                                "bb", command.name, aliases, command.properties.description),
                        false
                )
                .setFooter("Help requested by ${ctx.author.name}", ctx.author.effectiveAvatarUrl)
                .setTimestamp(Instant.now())

        if (ctx.args.size >= 2 && ctx.args[1].toLowerCase() == "--dm") {
            ctx.dm(commandHelp.build())
            ctx.message.addReaction("\uD83D\uDCEC").queue(null, null)
        } else {
            ctx.embed(commandHelp.build())
        }
    }

    private fun padEnd(str: String, length: Int = 15): String {
        return str + "\u200B ".repeat(length - str.length)
    }

}