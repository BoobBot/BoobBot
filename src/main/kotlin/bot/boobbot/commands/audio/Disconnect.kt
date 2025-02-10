package bot.boobbot.commands.audio

import bot.boobbot.BoobBot
import bot.boobbot.entities.framework.Category
import bot.boobbot.entities.framework.Context
import bot.boobbot.entities.framework.annotations.CommandProperties
import bot.boobbot.entities.framework.interfaces.VoiceCommand
import bot.boobbot.entities.internals.Config
import bot.boobbot.utils.Formats
import net.dv8tion.jda.api.Permission

@CommandProperties(
    description = "Disconnects bot",
    aliases = ["stop", "leave"],
    category = Category.AUDIO,
    guildOnly = true,
    groupByCategory = true
)
class Disconnect : VoiceCommand {
    override fun execute(ctx: Context) {
        if (ctx.userCan(Permission.MESSAGE_MANAGE) || BoobBot.owners.contains(ctx.user.idLong) ||
            isDJ(ctx.member!!) || isAlone(ctx.member)) {
            ctx.audioPlayer.shutdown()
            return ctx.reply(Formats.info("Done, Whore"))
        }
        return ctx.reply(Formats.error("No whore, i can't let you do that"))
    }
}
