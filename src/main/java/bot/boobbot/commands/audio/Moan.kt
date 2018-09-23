package bot.boobbot.commands.audio

import bot.boobbot.BoobBot.Companion.getMusicManager
import bot.boobbot.BoobBot.Companion.playerManager
import bot.boobbot.flight.Command
import bot.boobbot.flight.CommandProperties
import bot.boobbot.flight.Context
import bot.boobbot.misc.AudioLoader
import bot.boobbot.misc.Utils.Companion.connectToVoiceChannel

import bot.boobbot.misc.Utils.Companion.getRandomMoan

@CommandProperties(description = "moans")
class Moan : Command {

//TODO voice checks
    override fun execute(ctx: Context) {
        val musicManager = getMusicManager(ctx.message.guild)
        connectToVoiceChannel(ctx.message)
        playerManager.loadItemOrdered(musicManager, getRandomMoan().toString(), AudioLoader(musicManager, ctx))

    }

}