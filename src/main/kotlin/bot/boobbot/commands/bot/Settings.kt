package bot.boobbot.commands.bot

import bot.boobbot.BoobBot
import bot.boobbot.entities.framework.Context
import bot.boobbot.entities.framework.annotations.CommandProperties
import bot.boobbot.entities.framework.annotations.Option
import bot.boobbot.entities.framework.annotations.SubCommand
import bot.boobbot.entities.framework.impl.Resolver
import bot.boobbot.entities.framework.interfaces.Command
import bot.boobbot.utils.Colors
import bot.boobbot.utils.Formats
import bot.boobbot.utils.ifEmpty
import bot.boobbot.utils.ifTrue
import net.dv8tion.jda.api.Permission
import net.dv8tion.jda.api.entities.channel.middleman.GuildMessageChannel
import net.dv8tion.jda.api.interactions.commands.OptionType
import org.checkerframework.checker.units.qual.g

@CommandProperties(description = "Manage BoobBot's settings for this server", guildOnly = true)
class Settings : Command {

    private val prohibitDisable = arrayOf("help", "settings")

    fun englishIsHard(a: List<*>) = (a.size == 1).ifTrue { "is" } ?: "are"

    override fun localCheck(ctx: Context): Boolean {
        if (!ctx.userCan(Permission.MANAGE_SERVER)) {
            ctx.reply("You don't have `MANAGE_SERVER` permission, whore.")
            return false
        }

        return true
    }

    override fun execute(ctx: Context) {
        sendSubcommandHelp(ctx)
    }

    @SubCommand(aliases = ["vdc", "disabled"], description = "Lists all disabled commands.")
    fun viewDisabledCmds(ctx: Context) {
        val disabled = BoobBot.database.getDisabledCommands(ctx.guild.idLong)
        val disabledFmt = if (disabled.isEmpty()) "*No commands disabled.*" else disabled.sorted().joinToString(", ")

        ctx.reply {
            setColor(Colors.getEffectiveColor(ctx.member))
            setTitle("Disabled Commands")
            setDescription(disabledFmt)
        }
    }

    @SubCommand(aliases = ["disable", "dc"], description = "Disables commands for the entire server.")
    @Option(name = "commands", description = "A list of commands separated by space")
    fun disableCmds(ctx: Context) {
        val disable = ctx.options.getOptionStringOrGather("commands")?.split(' ')?.takeIf { it.isNotEmpty() }?.map(String::lowercase)
            ?: return ctx.reply("wtf, i don't mind-read. Specify what commands you wanna disable, whore.")

        val disabled = BoobBot.database.getDisabledCommands(ctx.guild.idLong)
        val alreadyDisabled = disable.filter(disabled::contains)

        if (alreadyDisabled.isNotEmpty()) {
            return ctx.reply("${Formats.monospaced(alreadyDisabled)} ${englishIsHard(alreadyDisabled)} already disabled.")
        }

        val invalid = disable.filter {
            !BoobBot.commands.containsKey(it) || prohibitDisable.contains(it) || BoobBot.commands[it]?.properties?.developerOnly == true
        }

        if (invalid.isNotEmpty()) {
            return ctx.reply("${Formats.monospaced(invalid)} ${englishIsHard(invalid)} invalid. Fix it whore")
        }

        BoobBot.database.disableCommands(ctx.guild.idLong, disable)
        ctx.reply("Disabled ${Formats.monospaced(disable)}.")
    }

    @SubCommand(aliases = ["enable", "ec"], description = "Re-enable disabled commands for the entire server.")
    @Option(name = "commands", description = "A list of commands separated by space")
    fun enableCmds(ctx: Context) {
        val enable = ctx.options.getOptionStringOrGather("commands")?.split(' ')?.takeIf { it.isNotEmpty() }?.map(String::lowercase)
            ?: return ctx.reply("wtf, i don't mind-read. Specify what commands you wanna re-enable, whore.")

        val disabled = BoobBot.database.getDisabledCommands(ctx.guild.idLong)
        val invalid = enable.filterNot(BoobBot.commands::containsKey)

        if (invalid.isNotEmpty()) {
            return ctx.reply("${Formats.monospaced(invalid)} ${englishIsHard(invalid)} invalid. Fix it whore")
        }

        val alreadyEnabled = enable.filterNot(disabled::contains)

        if (alreadyEnabled.isNotEmpty()) {
            return ctx.reply("${Formats.monospaced(alreadyEnabled)} ${englishIsHard(alreadyEnabled)} already enabled.")
        }

        BoobBot.database.enableCommands(ctx.guild.idLong, enable)
        ctx.reply("Enabled ${Formats.monospaced(enable)}.")
    }

    @SubCommand(aliases = ["vdh"], description = "Lists commands disabled in the current channel.")
    fun viewDisabledHere(ctx: Context) {
        val disabled = BoobBot.database.getDisabledForChannel(ctx.guild.idLong, ctx.channel.idLong)
        val disabledFmt = disabled.ifEmpty("*No commands disabled in this channel.*") { disabled.sorted().joinToString(", ") }

        ctx.reply {
            setColor(Colors.getEffectiveColor(ctx.member))
            setTitle("Commands Disabled in ${ctx.channel.name}")
            setDescription(disabledFmt)
        }
    }

    @SubCommand(aliases = ["dh"], description = "Disables commands for the current channel.", donorOnly = true)
    @Option(name = "commands", description = "A list of commands separated by space")
    fun disableHere(ctx: Context) {
        val disable = ctx.options.getOptionStringOrGather("commands")?.split(' ')?.takeIf { it.isNotEmpty() }?.map(String::lowercase)
            ?: return ctx.reply("wtf, i don't mind-read. Specify what commands you wanna disable for this channel, whore.")

        val disabled = BoobBot.database.getDisabledForChannel(ctx.guild.idLong, ctx.channel.idLong)
        val alreadyDisabled = disable.filter(disabled::contains)

        if (alreadyDisabled.isNotEmpty()) {
            return ctx.reply("${Formats.monospaced(alreadyDisabled)} ${englishIsHard(alreadyDisabled)} already disabled.")
        }

        val invalid = disable.filter {
            !BoobBot.commands.containsKey(it) || prohibitDisable.contains(it) || BoobBot.commands[it]?.properties?.developerOnly == true
        }

        if (invalid.isNotEmpty()) {
            return ctx.reply("${Formats.monospaced(invalid)} ${englishIsHard(invalid)} invalid. Fix it whore")
        }

        BoobBot.database.disableForChannel(ctx.guild.idLong, ctx.channel.idLong, disable)
        ctx.reply("Disabled ${Formats.monospaced(disable)} for this channel.")
    }

    @SubCommand(aliases = ["eh"], description = "Re-enables disabled commands for the current channel.")
    @Option(name = "commands", description = "A list of commands separated by space")
    fun enableHere(ctx: Context) {
        val enable = ctx.options.getOptionStringOrGather("commands")?.split(' ')?.takeIf { it.isNotEmpty() }?.map(String::lowercase)
            ?: return ctx.reply("wtf, i don't mind-read. Specify what commands you wanna re-enable for this channel, whore.")

        val disabled = BoobBot.database.getDisabledForChannel(ctx.guild.idLong, ctx.channel.idLong)
        val invalid = enable.filterNot(BoobBot.commands::containsKey)

        if (invalid.isNotEmpty()) {
            return ctx.reply("${Formats.monospaced(invalid)} ${englishIsHard(invalid)} invalid. Fix it whore")
        }

        val alreadyEnabled = enable.filterNot(disabled::contains)

        if (alreadyEnabled.isNotEmpty()) {
            return ctx.reply("${Formats.monospaced(alreadyEnabled)} ${englishIsHard(alreadyEnabled)} already enabled.")
        }

        BoobBot.database.enableForChannel(ctx.guild.idLong, ctx.channel.idLong, enable)
        ctx.reply("Enabled ${Formats.monospaced(enable)} for this channel.")
    }


    @SubCommand(aliases = ["ic"], description = "Ignores messages in a channel for any member without \"manage messages\".")
    @Option(name = "channel", description = "The channel to ignore.", type = OptionType.CHANNEL, required = false)
    fun ignoreChannel(ctx: Context) {
        val c = ctx.options.getByNameOrNext("channel", Resolver.localGuildChannel(ctx.guild)) ?: ctx.channel

        if (c !is GuildMessageChannel) {
            return ctx.reply("wtf whore? I can only ignore message channels, not ${c.asMention}")
        }

        BoobBot.database.setIgnoredChannel(ctx.guild.idLong, c.idLong)
        ctx.reply("Done.")
    }

    @SubCommand(aliases = ["uic"], description = "Removes a channel from the ignored list.")
    @Option(name = "channel", description = "The channel to unignore.", type = OptionType.CHANNEL, required = false)
    fun unignoreChannel(ctx: Context) {
        val c = ctx.options.getByNameOrNext("channel", Resolver.localGuildChannel(ctx.guild)) ?: ctx.channel

        if (c !is GuildMessageChannel) {
            return ctx.reply("wtf whore? I can only ignore message channels, not ${c.asMention}")
        }

        BoobBot.database.deleteIgnoredChannel(ctx.guild.idLong, c.idLong)
        ctx.reply("Done.")
    }

    @SubCommand(aliases = ["lic"], description = "Lists all ignored channels.")
    fun listIgnoredChannels(ctx: Context) {
        val channelIds = BoobBot.database.getIgnoredChannels(ctx.guild.idLong)
        val ignored = channelIds.ifEmpty("*None*") { joinToString("\n") { "<#$it>" } }
        ctx.reply("__**Ignored Channels**__\n$ignored")
    }

    @SubCommand(aliases = ["economytoggle"], description = "Toggles economy drops.")
    fun economyEnable(ctx: Context) {
        val data = ctx.guildData.apply {
            dropEnabled = !dropEnabled
            save()
        }

        val dropStatus = if (data.dropEnabled) "enabled" else "disabled"
        ctx.reply("Drops are now $dropStatus for this server.")
    }

}
