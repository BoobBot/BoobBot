package bot.boobbot.commands.audio

import bot.boobbot.entities.framework.Category
import bot.boobbot.entities.framework.annotations.CommandProperties
import bot.boobbot.entities.framework.MessageContext
import bot.boobbot.entities.framework.interfaces.VoiceCommand
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
    override fun execute(ctx: MessageContext) {
        if (!performVoiceChecks(ctx)) {
            return
        }

        if (ctx.args.firstOrNull()?.isEmpty() != false) {
            return ctx.reply("Gotta specify a search query, whore")
        }

        val player = ctx.audioPlayer

        if (player.player.playingTrack == null) {
            return ctx.reply(Formats.info("Im not playing anything? Play something or fuck off"))
        }

        if (!canSkip(ctx)) {
            return ctx.reply(Formats.error("No whore, i can't let you do that"))
        }

        val isBotOwner = Config.OWNERS.contains(ctx.user.idLong)
        val volumeLimit = if (isBotOwner) 1000 else 100

        val oldVol = player.player.volume
        val newVol = ctx.args[0].toIntOrNull()?.coerceIn(0, volumeLimit)
            ?: return ctx.reply("wtf whore, that's not a valid number")

        player.player.volume = newVol

        ctx.reply {
            setColor(Colors.getEffectiveColor(ctx.message))
            addField(Formats.info(""), "Changed volume from $oldVol to $newVol", false)
        }
    }
}
