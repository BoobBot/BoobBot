package bot.boobbot.commands.audio

import bot.boobbot.entities.framework.Category
import bot.boobbot.entities.framework.CommandProperties
import bot.boobbot.entities.framework.Context
import bot.boobbot.entities.framework.VoiceCommand
import bot.boobbot.entities.internals.Config
import bot.boobbot.utils.Colors
import bot.boobbot.utils.Formats

@CommandProperties(
    description = "Sets the Volume",
    category = Category.AUDIO,
    guildOnly = true,
    donorOnly = true,
    aliases = ["v", "vol"]
)
class Volume : VoiceCommand {
    override fun execute(ctx: Context) {
        if (!performVoiceChecks(ctx)) {
            return
        }

        if (ctx.args.firstOrNull()?.isEmpty() != false) {
            return ctx.send("Gotta specify a search query, whore")
        }

        val player = ctx.audioPlayer

        if (player.player.playingTrack == null) {
            return ctx.send(Formats.info("Im not playing anything? Play something or fuck off"))
        }

        if (!canSkip(ctx)) {
            return ctx.send(Formats.error("No whore, i can't let you do that"))
        }

        val isBotOwner = Config.OWNERS.contains(ctx.author.idLong)
        val volumeLimit = if (isBotOwner) 1000 else 100

        val oldVol = player.player.volume
        val newVol = ctx.args[0].toIntOrNull()?.coerceIn(0, volumeLimit)
            ?: return ctx.send("wtf whore, that's not a valid number")

        player.player.volume = newVol

        ctx.send {
            setColor(Colors.getEffectiveColor(ctx.message))
            addField(Formats.info(""), "Changed volume from $oldVol to $newVol", false)
        }
    }
}
