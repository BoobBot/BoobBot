package bot.boobbot.commands.audio

import bot.boobbot.entities.framework.Category
import bot.boobbot.entities.framework.CommandProperties
import bot.boobbot.entities.framework.Context

import bot.boobbot.utils.Formats
import bot.boobbot.entities.internals.Config
import bot.boobbot.entities.framework.VoiceCommand
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
            || Config.OWNERS.contains(ctx.author.idLong)
            || isDJ(ctx.member!!)
            || isAlone(ctx.member)
        ) {
            player.shutdown()
            return ctx.send(Formats.info("Done, Whore"))
        }
        return ctx.send(Formats.error("No whore, i can't let you do that"))
    }

}