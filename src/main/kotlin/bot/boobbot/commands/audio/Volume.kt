package bot.boobbot.commands.audio

import bot.boobbot.flight.Category
import bot.boobbot.flight.CommandProperties
import bot.boobbot.flight.Context
import bot.boobbot.misc.Colors
import bot.boobbot.misc.Formats
import bot.boobbot.models.Config
import bot.boobbot.models.VoiceCommand

@CommandProperties(
    description = "Sets the Volume <:p_:475801484282429450> ",
    category = Category.AUDIO,
    guildOnly = true,
    donorOnly = true,
    aliases = ["v", "vol"]
)
class Volume : VoiceCommand {

    override fun execute(ctx: Context) {
        val shouldPlay = performVoiceChecks(ctx)

        if (!shouldPlay) {
            return
        }

        if (ctx.args.isEmpty() || ctx.args[0].isEmpty()) {
            return ctx.send("Gotta specify a search query, whore")
        }

        val player = ctx.audioPlayer!!
        player.player.playingTrack ?: return ctx.send(Formats.info("Im not playing anything? Play something or fuck off"))
        if (!canSkip(ctx)) {
            return ctx.send(Formats.error("No whore, i can't let you do that"))
        }

        val oldVol = player.player.volume
        try {
            var newVol = Integer.parseInt(ctx.args[0])
            if (newVol > 100 && !Config.owners.contains(ctx.author.idLong)) {
                newVol = 100
            }
            if (newVol < 0) {
                newVol = 0
            }

            player.player.volume = newVol
            return ctx.embed {
                setColor(Colors.getDominantColor(ctx.author))
                addField(Formats.info(""), "Changed volume from $oldVol to $newVol", false)
            }

        } catch (ex: NumberFormatException) {
            ctx.send(Formats.error("wtf whore, ${ctx.args[0]} isn't a valid number"))
        }
    }
}
