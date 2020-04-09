package bot.boobbot.commands.audio

import bot.boobbot.flight.Category
import bot.boobbot.flight.CommandProperties
import bot.boobbot.flight.Context

import bot.boobbot.misc.Formats
import bot.boobbot.models.Config
import bot.boobbot.models.VoiceCommand
import net.dv8tion.jda.api.Permission

@CommandProperties(
    description = "Disconnects bot",
    aliases = ["stop", "leave"],
    category = Category.AUDIO,
    guildOnly = true
)
class Disconnect : VoiceCommand {

    override fun execute(ctx: Context) {
        val player = ctx.audioPlayer
        if (
            ctx.userCan(Permission.MESSAGE_MANAGE)
            || Config.owners.contains(ctx.author.idLong)
            || isDJ(ctx.member!!)
            || ctx.member.voiceState!!.channel!!.members.filter { !it.user.isBot }.size == 1
        ) {
            player.shutdown()
            return ctx.send(Formats.info("Done, Whore"))
        }
        return ctx.send(Formats.error("No whore, i can't let you do that"))
    }

}