package bot.boobbot.commands.bot

import bot.boobbot.BoobBot
import bot.boobbot.entities.framework.Context
import bot.boobbot.entities.framework.MessageContext
import bot.boobbot.entities.framework.annotations.CommandProperties
import bot.boobbot.entities.framework.annotations.Option
import bot.boobbot.entities.framework.annotations.SubCommand
import bot.boobbot.entities.framework.impl.Resolver
import bot.boobbot.entities.framework.interfaces.Command
import bot.boobbot.utils.Formats
import bot.boobbot.utils.Utils
import net.dv8tion.jda.api.Permission

@CommandProperties(
    aliases = ["cc"],
    description = "Custom commands",
    guildOnly = true,
    groupByCategory = true,
    slashEnabled = false  // TODO: revisit this?
)
class Custom : Command {
    override fun execute(ctx: Context) = sendSubcommandHelp(ctx)

    @SubCommand(description = "Add a custom tag.")
    @Option(name = "name", description = "The name of the tag.")
    @Option(name = "content", description = "The tag content.")
    fun add(ctx: Context) {
        if (!ctx.userCan(Permission.MANAGE_SERVER)) {
            return ctx.reply("You don't have `MANAGE_SERVER` permission, whore.")
        }

        if (!Utils.checkDonor(ctx)) {
            return ctx.reply(Formats.error(" Sorry this command is only available to our Patrons.\n<:p_:475801484282429450> Stop being a cheap fuck and join today!\nhttps://www.patreon.com/OfficialBoobBot"))
        }

        val tagName = ctx.options.getByNameOrNext("name", Resolver.STRING)
            ?: return ctx.reply("You need to specify a tag name, whore.")

        if (tagName.length > 128) {
            return ctx.reply("Keep the tag name short, whore. (${tagName.length} > 128 characters)")
        }

        val tagContent = ctx.options.getOptionStringOrGather("content")
            ?: return ctx.reply("You need to specify tag content, whore.")

        BoobBot.database.setCustomCommand(ctx.guild.idLong, tagName, tagContent)
        ctx.reply("done whore")
    }

    @SubCommand(aliases = ["del", "remove", "rem"])
    @Option(name = "name", description = "The name of the tag to delete.")
    fun delete(ctx: MessageContext) {
        if (!ctx.userCan(Permission.MANAGE_SERVER)) {
            return ctx.reply("You don't have `MANAGE_SERVER` permission, whore.")
        }

        val tagName = ctx.options.getByNameOrNext("name", Resolver.STRING)
            ?: return ctx.reply("what tag do you want to delete, whore")

        if (!BoobBot.database.getCustomCommands(ctx.guild.idLong).containsKey(tagName)) {
            return ctx.reply("wtf, why are you trying to remove a non-existent command?")
        }

        BoobBot.database.deleteCustomCommand(ctx.guild.idLong, tagName)
        ctx.reply("done whore")
    }

    @SubCommand(description = "List all custom commands in this server.")
    fun list(ctx: MessageContext) {
        val allCommands = BoobBot.database.getCustomCommands(ctx.guild.idLong)

        if (allCommands.isEmpty()) {
            return ctx.reply("This server has no custom commands.")
        }

        ctx.reply("```\n${allCommands.keys.joinToString(", ")}```")
    }

    @SubCommand(description = "Clear all custom commands in this server.")
    fun clear(ctx: MessageContext) {
        BoobBot.database.deleteCustomCommands(ctx.guild.idLong)
        ctx.reply("Done, whore.")
    }
}