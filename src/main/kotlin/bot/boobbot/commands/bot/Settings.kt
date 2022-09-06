package bot.boobbot.commands.bot

import bot.boobbot.BoobBot
import bot.boobbot.entities.framework.Command
import bot.boobbot.entities.framework.CommandProperties
import bot.boobbot.entities.framework.Context
import bot.boobbot.entities.framework.SubCommand
import bot.boobbot.utils.Colors
import bot.boobbot.utils.Formats
import bot.boobbot.utils.ifEmpty
import net.dv8tion.jda.api.Permission
import net.dv8tion.jda.api.entities.TextChannel
import org.jetbrains.kotlin.utils.addToStdlib.ifTrue

@CommandProperties(description = "Manage BoobBot's settings for this server", guildOnly = true)
class Settings : Command {

    private val prohibitDisable = arrayOf("help", "settings")

    fun englishIsHard(a: List<*>) = (a.size == 1).ifTrue { "is" } ?: "are"

    override fun localCheck(ctx: Context): Boolean {
        if (!ctx.userCan(Permission.MANAGE_SERVER)) {
            ctx.send("You don't have `MANAGE_SERVER` permission, whore.")
            return false
        }

        return true
    }

    override fun execute(ctx: Context) {
        sendSubcommandHelp(ctx)
    }

    @SubCommand(aliases = ["vdc", "disabled"], description = "Lists all disabled commands.")
    fun viewDisabledCmds(ctx: Context) {
        val disabled = BoobBot.database.getDisabledCommands(ctx.guild!!.id)
        val disabledFmt = if (disabled.isEmpty()) "*No commands disabled.*" else disabled.sorted().joinToString(", ")

        ctx.send {
            setColor(Colors.getEffectiveColor(ctx.message))
            setTitle("Disabled Commands")
            setDescription(disabledFmt)
        }
    }

    @SubCommand(aliases = ["disable", "dc"], description = "Disables commands for the entire server.")
    fun disableCmds(ctx: Context) {
        if (ctx.args.isEmpty()) {
            return ctx.send("wtf, i don't mind read. Specify what commands you wanna disable, whore.")
        }

        val toDisable = ctx.args.map(String::lowercase)
        val disabled = BoobBot.database.getDisabledCommands(ctx.guild!!.id)
        val alreadyDisabled = toDisable.filter(disabled::contains)

        if (alreadyDisabled.isNotEmpty()) {
            return ctx.send("${Formats.monospaced(alreadyDisabled)} ${englishIsHard(alreadyDisabled)} already disabled.")
        }

        val invalid = toDisable.filter {
            !BoobBot.commands.containsKey(it) ||
                    prohibitDisable.contains(it) ||
                    BoobBot.commands[it]?.properties?.developerOnly ?: false
        }

        if (invalid.isNotEmpty()) {
            return ctx.send("${Formats.monospaced(invalid)} ${englishIsHard(invalid)} invalid. Fix it whore")
        }

        BoobBot.database.disableCommands(ctx.guild.id, toDisable)
        ctx.send("Disabled ${Formats.monospaced(toDisable)}.")
    }

    @SubCommand(aliases = ["enable", "ec"], description = "Re-enable disabled commands for the entire server.")
    fun enableCmds(ctx: Context) {
        if (ctx.args.isEmpty()) {
            return ctx.send("wtf, i don't mind read. Specify what commands you wanna re-enable, whore.")
        }

        val toEnable = ctx.args.map(String::lowercase)
        val disabled = BoobBot.database.getDisabledCommands(ctx.guild!!.id)
        val invalid = toEnable.filterNot(BoobBot.commands::containsKey)

        if (invalid.isNotEmpty()) {
            return ctx.send("${Formats.monospaced(invalid)} ${englishIsHard(invalid)} invalid. Fix it whore")
        }

        val alreadyEnabled = toEnable.filterNot(disabled::contains)

        if (alreadyEnabled.isNotEmpty()) {
            return ctx.send("${Formats.monospaced(alreadyEnabled)} ${englishIsHard(alreadyEnabled)} already enabled.")
        }

        BoobBot.database.enableCommands(ctx.guild.id, toEnable)
        ctx.send("Enabled ${Formats.monospaced(toEnable)}.")
    }

    @SubCommand(aliases = ["vdh"], description = "Lists commands disabled in the current channel.")
    fun viewDisabledHere(ctx: Context) {
        val disabled = BoobBot.database.getDisabledForChannel(ctx.guild!!.id, ctx.channel.id)
        val disabledFmt =
            if (disabled.isEmpty()) "*No commands disabled in this channel.*" else disabled.sorted().joinToString(", ")

        ctx.send {
            setColor(Colors.getEffectiveColor(ctx.message))
            setTitle("Commands Disabled in ${ctx.channel.name}")
            setDescription(disabledFmt)
        }
    }

    @SubCommand(aliases = ["dh"], description = "Disables commands for the current channel.", donorOnly = true)
    fun disableHere(ctx: Context) {
        if (ctx.args.isEmpty()) {
            return ctx.send("wtf, i don't mind read. Specify what commands you wanna disable for this channel, whore.")
        }

        val toDisable = ctx.args.map(String::lowercase)
        val disabled = BoobBot.database.getDisabledForChannel(ctx.guild!!.id, ctx.channel.id)
        val alreadyDisabled = toDisable.filter(disabled::contains)

        if (alreadyDisabled.isNotEmpty()) {
            return ctx.send("${Formats.monospaced(alreadyDisabled)} ${englishIsHard(alreadyDisabled)} already disabled.")
        }

        val invalid = toDisable.filter {
            !BoobBot.commands.containsKey(it) ||
                    prohibitDisable.contains(it) ||
                    BoobBot.commands[it]?.properties?.developerOnly ?: false
        }

        if (invalid.isNotEmpty()) {
            return ctx.send("${Formats.monospaced(invalid)} ${englishIsHard(invalid)} invalid. Fix it whore")
        }

        BoobBot.database.disableForChannel(ctx.guild.id, ctx.channel.id, toDisable)
        ctx.send("Disabled ${Formats.monospaced(toDisable)} for this channel.")
    }

    @SubCommand(aliases = ["eh"], description = "Re-enables disabled commands for the current channel.")
    fun enableHere(ctx: Context) {
        if (ctx.args.isEmpty()) {
            return ctx.send("wtf, i don't mind read. Specify what commands you wanna re-enable for this channel, whore.")
        }

        val toEnable = ctx.args.map(String::lowercase)
        val disabled = BoobBot.database.getDisabledForChannel(ctx.guild!!.id, ctx.channel.id)
        val invalid = toEnable.filterNot(BoobBot.commands::containsKey)

        if (invalid.isNotEmpty()) {
            return ctx.send("${Formats.monospaced(invalid)} ${englishIsHard(invalid)} invalid. Fix it whore")
        }

        val alreadyEnabled = toEnable.filterNot(disabled::contains)

        if (alreadyEnabled.isNotEmpty()) {
            return ctx.send("${Formats.monospaced(alreadyEnabled)} ${englishIsHard(alreadyEnabled)} already enabled.")
        }

        BoobBot.database.enableForChannel(ctx.guild.id, ctx.channel.id, toEnable)
        ctx.send("Enabled ${Formats.monospaced(toEnable)} for this channel.")
    }


    @SubCommand(
        aliases = ["ic"],
        description = "Ignores messages in a channel for any member without \"manage messages\"."
    )
    fun ignoreChannel(ctx: Context) {
        val g = BoobBot.database.getGuild(ctx.guild!!.id)
        val c = ctx.message.mentions.getChannels(TextChannel::class.java).firstOrNull() ?: ctx.textChannel

        g.ignoredChannels.add(c!!.id)
        BoobBot.database.setGuild(g)
        ctx.send("Done.")
    }

    @SubCommand(aliases = ["uic"], description = "Removes a channel from the ignored list.")
    fun unIgnoreChannel(ctx: Context) {
        val g = BoobBot.database.getGuild(ctx.guild!!.id)
        val c = ctx.message.mentions.getChannels(TextChannel::class.java).firstOrNull() ?: ctx.textChannel

        g.ignoredChannels.remove(c!!.id)
        BoobBot.database.setGuild(g)
        ctx.send("Done.")
    }

    @SubCommand(aliases = ["lic"], description = "Lists all ignored channels.")
    fun listIgnoredChannels(ctx: Context) {
        val g = BoobBot.database.getGuild(ctx.guild!!.id)
        val ignored = g.ignoredChannels.ifEmpty("*None*") { joinToString("\n", transform = { "<#$it>" }) }
        ctx.send("__**Ignored Channels**__\n$ignored")
    }

    @SubCommand(aliases = ["economytoggle"], description = "Toggles economy drops.")
    fun economyEnable(ctx: Context) {
        val g = BoobBot.database.getGuild(ctx.guild!!.id)

        g.dropEnabled = !g.dropEnabled
        BoobBot.database.setGuild(g)

        val dropStatus = if (g.dropEnabled) "enabled" else "disabled"
        ctx.send("Drops are now $dropStatus for this server.")
    }

}
