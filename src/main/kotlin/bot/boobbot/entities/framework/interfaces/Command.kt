package bot.boobbot.entities.framework.interfaces

import bot.boobbot.BoobBot
import bot.boobbot.entities.framework.Context
import bot.boobbot.entities.framework.annotations.CommandProperties
import bot.boobbot.entities.framework.impl.SubCommandWrapper
import bot.boobbot.utils.Colors
import bot.boobbot.utils.Formats
import net.dv8tion.jda.api.EmbedBuilder
import java.time.Instant

interface Command {

    val name: String
        get() = this.javaClass.simpleName.lowercase()

    val properties: CommandProperties
        get() = this.javaClass.getAnnotation(CommandProperties::class.java)

    val subcommands: Map<String, SubCommandWrapper>
        get() = BoobBot.commands.getValue(name).subcommands

    /**
     * Command-local check that is executed before the command or any subcommands are
     * executed.
     *
     * @returns Whether command execution can proceed.
     */
    fun localCheck(ctx: Context): Boolean = true

    fun execute(ctx: Context)

    fun sendSubcommandHelp(ctx: Context) {
        val requester = BoobBot.shardManager.authorOrAnonymous(ctx)
        val embed = EmbedBuilder()
            .setColor(Colors.getEffectiveColor(ctx.member))
            .setAuthor(
                "${ctx.selfUser.name} help ${Formats.MAGIC_EMOTE}",
                "https://boob.bot/commands",
                ctx.selfUser.effectiveAvatarUrl
            )
            .setFooter("Help requested by ${requester.name}", requester.effectiveAvatarUrl)
            .setTimestamp(Instant.now())

        val maxLen = this.subcommands.values.maxOf { it.name.length }

        for (sc in this.subcommands.values.sortedBy { it.name }) {
            val patreonSymbol = if (sc.donorOnly) " <:p_:475801484282429450>" else ""
            embed.appendDescription("`${padEnd(sc.name, maxLen)}:` ${sc.description}$patreonSymbol\n")
        }

        ctx.reply(embed.build())
    }

    private fun padEnd(str: String, length: Int = 15): String {
        return str + "\u200B ".repeat(length - str.length)
    }

}
