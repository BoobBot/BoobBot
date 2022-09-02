package bot.boobbot.slashcommands.bot

import bot.boobbot.BoobBot
import bot.boobbot.entities.framework.*
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent

@CommandProperties(description = "Changes whether you can receive nudes or not")
class Opt : SlashCommand {

    override fun execute(event: SlashCommandInteractionEvent) {

        if(event.subcommandName.toString() == "in"){
            return `in`(event)
        }
        if(event.subcommandName.toString() == "out"){
            return out(event)
        }
        if(event.subcommandName.toString() == "status"){
            return status(event)
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