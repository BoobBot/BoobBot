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

        val toDisable = ctx.args.map { it.toLowerCase() }
        val disabled = BoobBot.database.getDisabledCommands(ctx.guild!!.id)
        val alreadyDisabled = toDisable.filter { disabled.contains(it) }

        if (alreadyDisabled.isNotEmpty()) {
            val fmt = alreadyDisabled.joinToString(prefix = "`", postfix = "`", separator = "`, `")
            return ctx.send("$fmt ${englishIsHard(alreadyDisabled)} already disabled.")
        }

        val invalid = toDisable.filter {
            !BoobBot.commands.containsKey(it) ||
                    prohibitDisable.contains(it) ||
                    BoobBot.commands[it]?.properties?.developerOnly ?: false
        }

        if (invalid.isNotEmpty()) {
            val fmt = invalid.joinToString(prefix = "`", postfix = "`", separator = "`, `")
            return ctx.send("$fmt ${englishIsHard(invalid)} invalid. Fix it whore")
        }

        BoobBot.database.disableCommands(ctx.guild.id, toDisable)

        val d = toDisable.joinToString(prefix = "`", postfix = "`", separator = "`, `")
        ctx.send("Disabled $d.")
    }

    @SubCommand(aliases = ["enable", "ec"])
    fun enableCmds(ctx: Context) {
        if (ctx.args.isEmpty()) {
            return ctx.send("wtf, i don't mind read. Specify what commands you wanna disable, whore.")
        }

        val toEnable = ctx.args.map { it.toLowerCase() }
        val disabled = BoobBot.database.getDisabledCommands(ctx.guild!!.id)

        val invalid = toEnable.filter { !BoobBot.commands.containsKey(it) }

        if (invalid.isNotEmpty()) {
            val fmt = invalid.joinToString(prefix = "`", postfix = "`", separator = "`, `")
            return ctx.send("$fmt ${englishIsHard(invalid)} invalid. Fix it whore")
        }

        val alreadyEnabled = toEnable.filter { !disabled.contains(it) }

        if (alreadyEnabled.isNotEmpty()) {
            val fmt = alreadyEnabled.joinToString(prefix = "`", postfix = "`", separator = "`, `")
            return ctx.send("$fmt ${englishIsHard(alreadyEnabled)} already enabled.")
        }

        BoobBot.database.enableCommands(ctx.guild.id, toEnable)

        val d = toEnable.joinToString(prefix = "`", postfix = "`", separator = "`, `")
        ctx.send("Enabled $d.")
    }


    @SubCommand(aliases = ["prefix", "sp"])
    fun setPrefix(ctx: Context) {
        if (!Utils.checkDonor(ctx.message)) {
            ctx.channel.sendMessage(
                Formats.error(
                    " Sorry this command is only available to our Patrons.\n<:p_:475801484282429450> "
                            + "Stop being a cheap fuck and join today!\nhttps://www.patreon.com/OfficialBoobBot"
                )
            ).queue()
            return
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