package bot.boobbot.commands.nsfw

import bot.boobbot.flight.CommandProperties
import bot.boobbot.models.RtCommand

@CommandProperties(description = "RedTube video search", guildOnly = true, aliases = ["rt"], nsfw = true, category = CommandProperties.category.VIDEOSEARCHING)
class RedTube : RtCommand()
