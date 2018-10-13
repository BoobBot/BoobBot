package bot.boobbot.commands.nsfw

import bot.boobbot.flight.Category
import bot.boobbot.flight.CommandProperties
import bot.boobbot.models.BbApiCommand

@CommandProperties(description = "BlowJobs!", nsfw = true, aliases = ["bj"], category = Category.GENERAL)
class BlowJob : BbApiCommand("blowjob")
