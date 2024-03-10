package bot.boobbot.commands.bot

import bot.boobbot.BoobBot
import bot.boobbot.entities.framework.Category
import bot.boobbot.entities.framework.Context
import bot.boobbot.entities.framework.annotations.CommandProperties
import bot.boobbot.entities.framework.impl.ExecutableCommand
import bot.boobbot.entities.framework.interfaces.Command
import bot.boobbot.entities.internals.Config
import bot.boobbot.utils.Colors
import bot.boobbot.utils.Formats
import net.dv8tion.jda.api.EmbedBuilder
import net.dv8tion.jda.api.entities.emoji.Emoji
import java.time.Instant

@CommandProperties(
    description = "help, --dm for dm",
    aliases = ["halp", "halllp", "coms", "commands", "cmds"],
    slashEnabled = false
)
class Help : Command {
    override fun execute(ctx: Context) {
        if (ctx.isSlashContext) {
            return ctx.reply("This command can't be used as a slash command, whore.")
        }

        val args = ctx.options.raw()
        val isDm = args.lastOrNull()?.lowercase() == "--dm"
        val command = args.firstOrNull()?.let(BoobBot.commands::findCommand)
        val category = args.firstOrNull()?.let(::getCategoryByName)

        when {
            args.isEmpty() || args.first().equals("--dm", true) -> sendCategories(ctx, isDm)
            command != null -> sendCommandHelp(ctx, command, isDm)
            category != null -> sendCategoryCommands(ctx, category, isDm)
            else -> ctx.reply("`${args.first().replace("@", "")}` is not a command/category, whore.")
        }
    }

    private fun sendCategories(ctx: Context, dm: Boolean) {
        val prefix = ctx.selfUser.asMention
        val embed = builder(ctx)

        val content = StringBuilder()
        val categories = Category.entries.sortedWith(compareBy({ it.nsfw }, { it.name }))
        val longestCategoryNameLength = categories.maxOf { it.name.length }

        for (category in categories) {
            if (category === Category.DEV && !Config.OWNERS.contains(ctx.user.idLong)) {
                continue
            }

            content.append("`${category.name.lowercase().padEnd(longestCategoryNameLength)}:` ")

            if (category.nsfw && ctx.channelType.isGuild && ctx.textChannel?.isNSFW != true) {
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
        val categoryCommands = BoobBot.commands.values.filter { it.properties.category == category }
        val longestCommandNameLength = categoryCommands.maxOf { it.name.length }
        val prefix = ctx.prefix
        val commandList = categoryCommands.joinToString("\n") {
            "`$prefix${it.name.padEnd(longestCommandNameLength)}:` ${it.properties.description}" + if (it.properties.donorOnly) " <:p_:475801484282429450>" else ""
        }

        val embed = builder(ctx)
            .setDescription("Commands in **${category.name.lowercase()}**\n$commandList")
//            .addField("Commands in **${category.name.toLowerCase()}**", categoryCommands, false)
        send(ctx, embed, dm)
    }

    private fun sendCommandHelp(ctx: Context, command: ExecutableCommand, dm: Boolean) {
        val aliases = command.properties.aliases.takeUnless { it.isEmpty() }?.joinToString(", ") ?: "None"
        val info = String.format(
            "Command:\n**%s%s**\nAliases:\n**%s**\nDescription:\n**%s**%s",
            ctx.prefix, command.name, aliases, command.properties.description,
            if (command.properties.donorOnly) "\n\n**<:p_:475801484282429450> Patreon only**" else ""
        )
        val embed = builder(ctx).addField(Formats.info("Info"), info, false)
        send(ctx, embed, dm)
    }

    private fun send(ctx: Context, embed: EmbedBuilder, dm: Boolean) {
        if (dm) {
            ctx.dm(embed.build())
            ctx.react(Emoji.fromUnicode("\uD83D\uDCEC"))
        } else {
            ctx.reply(embed.build())
        }
    }

    private fun builder(ctx: Context): EmbedBuilder {
        val requester = BoobBot.shardManager.authorOrAnonymous(ctx)

        return EmbedBuilder()
            .setColor(Colors.getEffectiveColor(ctx.member))
            .setAuthor(
                "${ctx.selfUser.name} help ${Formats.MAGIC_EMOTE}",
                "https://boob.bot/commands",
                ctx.selfUser.effectiveAvatarUrl
            )
            .setFooter("Help requested by ${requester.name}", requester.effectiveAvatarUrl)
            .setTimestamp(Instant.now())
    }

    private fun getCategoryByName(category: String) = Category.entries.firstOrNull { it.name.equals(category, true) }
}
