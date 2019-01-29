package bot.boobbot.commands.meme
import bot.boobbot.flight.Category
import bot.boobbot.flight.CommandProperties
import bot.boobbot.models.MemeAvatarCommand

@CommandProperties(description = "Trigger.", category = Category.MEME, guildOnly = true, aliases = ["triggered"])
class Trigger : MemeAvatarCommand("trigger")