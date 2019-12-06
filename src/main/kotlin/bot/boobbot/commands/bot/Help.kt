package bot.boobbot.commands.bot

import bot.boobbot.BoobBot
import bot.boobbot.flight.*
import bot.boobbot.misc.Colors
import bot.boobbot.misc.Formats
import bot.boobbot.models.Config
import net.dv8tion.jda.api.EmbedBuilder
import java.time.Instant

@CommandProperties(
    description = "help, --dm for dm",
    aliases = ["halp", "halllp", "coms", "commands", "cmds"],
    category = Category.MISC
)
class Help : Command {

    override fun execute(ctx: Context) {
        val prefix = ctx.customPrefix ?: BoobBot.defaultPrefix
        val isDm = ctx.args.lastOrNull()?.equals("--dm", true) ?: false

        val command = if (ctx.args.isEmpty()) null else BoobBot.commands.findCommand(ctx.args.first())
        val category = if (ctx.args.isEmpty()) null else getCategoryByName(ctx.args.first())

        when {
            ctx.args.isEmpty() || ctx.args.first().equals("--dm", true) -> sendCategories(ctx, isDm)
            command != null -> sendCommandHelp(ctx, command, isDm)
            category != null -> sendCategoryCommands(ctx, category, isDm)
            else -> ctx.send("`${ctx.args.first()}` is not a command/category, whore.")
        }

        return
    }

    fun sendCategories(ctx: Context, dm: Boolean) {
        val prefix = ctx.customPrefix ?: BoobBot.defaultPrefix
        val embed = builder(ctx)

        val content = StringBuilder()

        for (category in Category.values().sortedWith(compareBy({ it.nsfw }, { it.name }))) {
            if (category === Category.DEV && !Config.owners.contains(ctx.author.idLong)) {
                continue
            }

            content.append("`${padEnd(category.name.toLowerCase(), 14)}:` ")

            if (category.nsfw && ctx.channelType.isGuild && !ctx.textChannel!!.isNSFW) {
                content.append("Unavailable. Move to an NSFW channel.\n")
            } else {
                content.append(category.description).append("\n")
            }
        }

        content.append("\nTo view the commands of a category, send `${prefix}help <category>`")
        embed.addField("Command Categories", content.toString(), false)
        send(ctx, embed, dm)
    }

    fun sendCategoryCommands(ctx: Context, category: Category, dm: Boolean) {
        val prefix = ctx.customPrefix ?: BoobBot.defaultPrefix

        val categoryCommands = BoobBot.commands.values
            .filter { it.properties.category == category }
            .joinToString("\n") { "`$prefix${padEnd(it.name)}:` ${it.properties.description}" }

        val embed = builder(ctx)
            .addField("Commands in **${category.name.toLowerCase()}**", categoryCommands, false)

        send(ctx, embed, dm)
    }

    fun sendCommandHelp(ctx: Context, command: ExecutableCommand, dm: Boolean) {
        val prefix = ctx.customPrefix ?: BoobBot.defaultPrefix

        val aliases = if (command.properties.aliases.isEmpty()) {
            "None"
        } else {
            command.properties.aliases.joinToString(", ")
        }

        val info = String.format(
            "Command:\n**%s%s**\nAliases:\n**%s**\nDescription:\n**%s**",
            prefix, command.name, aliases, command.properties.description
        )

        val embed = builder(ctx)
            .addField(Formats.info("Info"), info, false)

        send(ctx, embed, dm)
    }

    private fun send(ctx: Context, embed: EmbedBuilder, dm: Boolean) {
        if (dm) {
            ctx.dm(embed.build())
            ctx.message.addReaction("\uD83D\uDCEC").queue()
        } else {
            ctx.embed(embed.build())
        }
    }

    private fun builder(ctx: Context): EmbedBuilder {
        return EmbedBuilder()
            .setColor(Colors.getEffectiveColor(ctx.message))
            .setAuthor(
                "${ctx.selfUser.name} help ${Formats.MAGIC_EMOTE}",
                "https://boob.bot/commands",
                ctx.selfUser.effectiveAvatarUrl
            )
            .setFooter("Help requested by ${ctx.author.name}", ctx.author.effectiveAvatarUrl)
            .setTimestamp(Instant.now())
    }

    private fun getCategoryByName(category: String): Category? {
        return Category.values().firstOrNull { it.name.equals(category, true) }
    }

    private fun padEnd(str: String, length: Int = 15): String {
        return str + "\u200B ".repeat(length - str.length)
    }

}