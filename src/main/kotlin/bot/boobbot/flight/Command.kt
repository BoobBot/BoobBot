package bot.boobbot.flight

import bot.boobbot.BoobBot
import bot.boobbot.misc.Colors
import bot.boobbot.misc.Formats
import net.dv8tion.jda.api.EmbedBuilder
import net.dv8tion.jda.api.entities.User
import net.dv8tion.jda.internal.JDAImpl
import net.dv8tion.jda.internal.entities.UserImpl
import java.time.Instant

interface Command {

    val name: String
        get() = this.javaClass.simpleName.toLowerCase()

    val properties: CommandProperties
        get() = this.javaClass.getAnnotation(CommandProperties::class.java)

    val hasProperties: Boolean
        get() = this.javaClass.isAnnotationPresent(CommandProperties::class.java)

    val subcommands: Map<String, SubCommandWrapper>
        get() = BoobBot.commands.getValue(name).subcommands

    /**
     * Command-local check that is executed before the command or any subcommands are
     * executed.
     *
     * @returns Whether or not command execution can proceed.
     */
    fun localCheck(ctx: Context): Boolean = true

    fun execute(ctx: Context)

    fun sendSubcommandHelp(ctx: Context) {
        val requester = BoobBot.shardManager.authorOrAnonymous(ctx)
        val embed = EmbedBuilder()
                .setColor(Colors.getEffectiveColor(ctx.message))
                .setAuthor(
                    "${ctx.selfUser.name} help ${Formats.MAGIC_EMOTE}",
                    "https://boob.bot/commands",
                    ctx.selfUser.effectiveAvatarUrl
                )
                .setFooter("Help requested by ${requester.name}", requester.effectiveAvatarUrl)
                .setTimestamp(Instant.now())

        for (sc in this.subcommands.values.sortedBy { it.name }) {
            embed.appendDescription("`${padEnd(sc.name, 16)}:` ${sc.description}\n")
        }

        ctx.embed(embed.build())
    }

    private fun padEnd(str: String, length: Int = 15): String {
        return str + "\u200B ".repeat(length - str.length)
    }

}
