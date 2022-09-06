package bot.boobbot.slashcommands.nsfw

import bot.boobbot.entities.framework.BbApiSlashCommand
import bot.boobbot.entities.framework.Category
import bot.boobbot.entities.framework.annotations.CommandProperties

@CommandProperties(
    description = "Bondage and Discipline (BD), Dominance and Submission (DS), Sadism and Masochism (SM)",
    nsfw = true,
    category = Category.KINKS
)
class BDSM : BbApiSlashCommand("bdsm")
