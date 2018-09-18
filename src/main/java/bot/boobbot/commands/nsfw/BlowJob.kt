package bot.boobbot.commands.nsfw

import bot.boobbot.flight.CommandProperties
import bot.boobbot.models.BbApiCommand

@CommandProperties(description = "BlowJobs!", nsfw = true, aliases = ["bj"], category = CommandProperties.category.GENERAL)
class BlowJob : BbApiCommand("blowjob")
