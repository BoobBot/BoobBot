package bot.boobbot.commands.bot

import bot.boobbot.BoobBot
import bot.boobbot.entities.framework.*
import bot.boobbot.utils.Colors
import bot.boobbot.utils.Formats
import bot.boobbot.entities.internals.Config
import net.dv8tion.jda.api.EmbedBuilder
import net.dv8tion.jda.api.entities.emoji.Emoji
import java.time.Instant
import java.util.*

@CommandProperties(
    description = "help, --dm for dm",
    aliases = ["halp", "halllp", "coms", "commands", "cmds"],
    category = Category.MISC
)
class Help : Command {
    override fun execute(ctx: Context) {
        val isDm = ctx.args.lastOrNull()?.lowercase() == "--dm"
        val command = ctx.args.firstOrNull()?.let(BoobBot.commands::findCommand)
        val category = ctx.args.firstOrNull()?.let(::getCategoryByName)

        when {
            ctx.args.isEmpty() || ctx.args.first().equals("--dm", true) -> sendCategories(ctx, isDm)
            command != null -> sendCommandHelp(ctx, command, isDm)
            category != null -> sendCategoryCommands(ctx, category, isDm)
            else -> ctx.send("`${ctx.args.first().replace("@", "")}` is not a command/category, whore.")
        }
    }

    private fun sendCategories(ctx: Context, dm: Boolean) {
        val prefix = ctx.jda.selfUser.asMention
        val embed = builder(ctx)

        val content = StringBuilder()

        for (category in Category.values().sortedWith(compareBy({ it.nsfw }, { it.name }))) {
            if (category === Category.DEV && !Config.OWNERS.contains(ctx.author.idLong)) {
                continue
            }

            content.append("`${padEnd(category.name.lowercase(), 14)}:` ")

            if (category.nsfw && ctx.channelType.isGuild && !ctx.textChannel!!.isNSFW) { // todo test this check.
                content.append("Unavailable. Move to an NSFW channel.\n")
            } else {
                content.append(category.description).append("\n")
            }
        }

        content.append("\nTo view the commands of a category, send ${prefix}help <category>")
        embed.setDescription(Formats.LING_MSG)
        embed.addField("Command Categories", content.toString(), false)
        send(ctx, embed, dm)
    }

    private fun sendCategoryCommands(ctx: Context, category: Category, dm: Boolean) {
        val prefix = ctx.customPrefix ?: BoobBot.defaultPrefix

        val categoryCommands = BoobBot.commands.values
            .filter { it.properties.category == category }
            .joinToString("\n") {
                "`$prefix${padEnd(it.name)}:` ${it.properties.description}" + if (it.properties.donorOnly) " <:p_:475801484282429450>" else ""
            }
        val embed = builder(ctx)
            .setDescription("Commands in **${category.name.lowercase()}**\n$categoryCommands")
        //.addField("Commands in **${category.name.toLowerCase()}**", categoryCommands, false)
        send(ctx, embed, dm)
    }

    private fun sendCommandHelp(ctx: Context, command: ExecutableCommand, dm: Boolean) {
        val prefix = ctx.customPrefix ?: BoobBot.defaultPrefix
        val aliases = command.properties.aliases.takeUnless { it.isEmpty() }?.joinToString(", ") ?: "None"
        val info = String.format(
            "Command:\n**%s%s**\nAliases:\n**%s**\nDescription:\n**%s**%s",
            prefix, command.name, aliases, command.properties.description,
            if (command.properties.donorOnly) "\n\n**<:p_:475801484282429450> Patreon only**" else ""
        )
        val embed = builder(ctx).addField(Formats.info("Info"), info, false)
        send(ctx, embed, dm)
    }

    private fun send(ctx: Context, embed: EmbedBuilder, dm: Boolean) {
        if (dm) {
            ctx.dm(embed.build())
            ctx.message.addReaction(Emoji.fromUnicode("\uD83D\uDCEC")).queue()
        } else {
            ctx.send(embed.build())
        }
    }

    private fun builder(ctx: Context): EmbedBuilder {
        val requester = BoobBot.shardManager.authorOrAnonymous(ctx)

        return EmbedBuilder()
            .setColor(Colors.getEffectiveColor(ctx.message))
            .setAuthor(
                "${ctx.selfUser.name} help ${Formats.MAGIC_EMOTE}",
                "https://boob.bot/commands",
                ctx.selfUser.effectiveAvatarUrl
            )
            .setFooter("Help requested by ${requester.name}", requester.effectiveAvatarUrl)
            .setTimestamp(Instant.now())
    }

    private fun getCategoryByName(category: String) = Category.values().firstOrNull { it.name.equals(category, true) }
    private fun padEnd(str: String, length: Int = 15) = str + "\u200B ".repeat(length - str.length)
}
