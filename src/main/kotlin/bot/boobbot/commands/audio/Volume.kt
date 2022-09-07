package bot.boobbot.commands.audio

import bot.boobbot.entities.framework.Category
import bot.boobbot.entities.framework.Context
import bot.boobbot.entities.framework.annotations.CommandProperties
import bot.boobbot.entities.framework.MessageContext
import bot.boobbot.entities.framework.impl.Resolver
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
    override fun execute(ctx: Context) {
        if (!performVoiceChecks(ctx)) {
            return
        }

        val volume = ctx.options.getByNameOrNext("volume", Resolver.INTEGER)
            ?: return ctx.reply("Gotta specify a number, whore")

        val player = ctx.audioPlayer

        if (player.player.playingTrack == null) {
            return ctx.reply(Formats.info("I'm not playing anything? Play something or fuck off"))
        }

        if (!canSkip(ctx)) {
            return ctx.reply(Formats.error("No whore, i can't let you do that"))
        }

        val isBotOwner = Config.OWNERS.contains(ctx.user.idLong)
        val volumeLimit = if (isBotOwner) 1000 else 100

        val oldVol = player.player.volume
        val newVol = volume.coerceIn(0, volumeLimit)

        player.player.volume = newVol

        ctx.reply {
            setColor(Colors.getEffectiveColor(ctx.member))
            addField(Formats.info(""), "Changed volume from $oldVol to $newVol", false)
        }
    }
}
