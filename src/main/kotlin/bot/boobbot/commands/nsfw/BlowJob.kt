package bot.boobbot.commands.nsfw

import bot.boobbot.entities.framework.Category
import bot.boobbot.entities.framework.CommandProperties
import bot.boobbot.entities.framework.BbApiCommand

@CommandProperties(description = "BlowJobs!", nsfw = true, aliases = ["bj"], category = Category.GENERAL)
class BlowJob : BbApiCommand("blowjob")
