package bot.boobbot.commands.bot

import bot.boobbot.BoobBot
import bot.boobbot.flight.Command
import bot.boobbot.flight.CommandProperties
import bot.boobbot.flight.Context
import bot.boobbot.flight.SubCommand
import bot.boobbot.misc.Colors
import bot.boobbot.misc.Formats
import bot.boobbot.misc.Utils
import net.dv8tion.jda.api.Permission

@CommandProperties(description = "Manage BoobBot's settings for this server", guildOnly = true)
class Settings : Command {

    private val prohibitDisable = arrayOf("help", "settings")

    fun englishIsHard(a: List<*>): String {
        if (a.size == 1) {
            return "is"
        }

        return "are"
    }

    override fun localCheck(ctx: Context): Boolean {
        if (!ctx.userCan(Permission.MANAGE_SERVER)) {
            ctx.send("You don't have `MANAGE_SERVER` permission, whore.")
            return false
        }

        return true
    }

    override fun execute(ctx: Context) {
        ctx.embed {
            setColor(Colors.getEffectiveColor(ctx.message))
            setTitle("BoobBot Server Settings")
            setDescription("Subcommands: ${subcommands.keys.joinToString(", ")}")
        }
    }

    @SubCommand(aliases = ["vdc", "disabled"])
    fun viewDisabledCmds(ctx: Context) {
        val disabled = BoobBot.database.getDisabledCommands(ctx.guild!!.id)
        val disabledFmt = if (disabled.isEmpty()) "*No commands disabled.*" else disabled.sorted().joinToString(", ")

        ctx.embed {
            setColor(Colors.getEffectiveColor(ctx.message))
            setTitle("Disabled Commands")
            setDescription(disabledFmt)
        }
    }

    @SubCommand(aliases = ["disable", "dc"])
    fun disableCmds(ctx: Context) {
        if (ctx.args.isEmpty()) {
            return ctx.send("wtf, i don't mind read. Specify what commands you wanna disable, whore.")
        }

        val toDisable = ctx.args.map(String::toLowerCase)
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

    @SubCommand(aliases = ["enable", "ec"])
    fun enableCmds(ctx: Context) {
        if (ctx.args.isEmpty()) {
            return ctx.send("wtf, i don't mind read. Specify what commands you wanna re-enable, whore.")
        }

        val toEnable = ctx.args.map(String::toLowerCase)
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

    @SubCommand(aliases = ["vdh"])
    fun viewDisabledHere(ctx: Context) {
        val disabled = BoobBot.database.getDisabledForChannel(ctx.guild!!.id, ctx.channel.id)
        val disabledFmt = if (disabled.isEmpty()) "*No commands disabled in this channel.*" else disabled.sorted().joinToString(", ")

        ctx.embed {
            setColor(Colors.getEffectiveColor(ctx.message))
            setTitle("Commands Disabled in ${ctx.channel.name}")
            setDescription(disabledFmt)
        }
    }

    @SubCommand(aliases = ["dh"])
    fun disableHere(ctx: Context) {
        if (ctx.args.isEmpty()) {
            return ctx.send("wtf, i don't mind read. Specify what commands you wanna disable for this channel, whore.")
        }

        val toDisable = ctx.args.map(String::toLowerCase)
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

    @SubCommand(aliases = ["eh"])
    fun enableHere(ctx: Context) {
        if (ctx.args.isEmpty()) {
            return ctx.send("wtf, i don't mind read. Specify what commands you wanna re-enable for this channel, whore.")
        }

        val toEnable = ctx.args.map(String::toLowerCase)
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

    @SubCommand(aliases = ["prefix", "sp"])
    fun setPrefix(ctx: Context) {
        if (!Utils.checkDonor(ctx.message)) {
            return ctx.send(
                Formats.error(" Sorry this command is only available to our Patrons.\n<:p_:475801484282429450> "
                        + "Stop being a cheap fuck and join today!\nhttps://www.patreon.com/OfficialBoobBot")
            )
        }

        if (ctx.args.isEmpty()) {
            return ctx.send("wtf, i don't mind read. Specify what prefix you wanna set, whore.")
        }

        BoobBot.database.setPrefix(ctx.guild!!.id, ctx.args[0])
        ctx.send("Set prefix to ${ctx.args[0]}")
    }


    @SubCommand(aliases = ["clearprefix", "dp"])
    fun removePrefix(ctx: Context) {
        if (BoobBot.database.getPrefix(ctx.guild!!.id) == null) {
            return ctx.send("wtf, i don't have a prefix here, whore.")
        }

        BoobBot.database.removePrefix(ctx.guild.id)
        ctx.send("Done.")
    }

}