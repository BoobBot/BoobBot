package bot.boobbot.slashcommands.bot

import bot.boobbot.BoobBot
import bot.boobbot.entities.framework.*
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent

@CommandProperties(description = "Changes whether you can receive nudes or not")
class Opt : SlashCommand {

    override fun execute(event: SlashCommandInteractionEvent) {
        when (event.subcommandName) {
            "in" -> `in`(event)
            "out" -> out(event)
            "status" -> status(event)
            else -> event.reply("Unknown subcommand").queue()
        }
    }

    fun `in`(event: SlashCommandInteractionEvent) {
        BoobBot.database.setUserCanReceiveNudes(event.user.id, true)
        event.reply("You're now able to receive nudes <:moans:583453348984913933>").queue()
    }

    fun out(event: SlashCommandInteractionEvent) {
        BoobBot.database.setUserCanReceiveNudes(event.user.id, false)
        event.reply("You can no longer receive nudes. Whore.").queue()
    }

    fun status(event: SlashCommandInteractionEvent) {
        val current = BoobBot.database.getCanUserReceiveNudes(event.user.id)
        val s = if (current) "can" else "can't"
        event.reply("You $s receive nudes.").queue()
    }

}
