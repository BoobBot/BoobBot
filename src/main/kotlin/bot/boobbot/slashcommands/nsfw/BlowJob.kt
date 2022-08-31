package bot.boobbot.slashcommands.nsfw

import bot.boobbot.entities.framework.Category
import bot.boobbot.entities.framework.CommandProperties
import bot.boobbot.entities.framework.BbApiSlashCommand

@CommandProperties(description = "BlowJobs!", nsfw = true, aliases = ["bj"], category = Category.GENERAL)
class BlowJob : BbApiSlashCommand("blowjob")
