package bot.boobbot.commands.nsfw

import bot.boobbot.entities.framework.Category
import bot.boobbot.entities.framework.annotations.CommandProperties
import bot.boobbot.entities.framework.impl.BbApiCommand

@CommandProperties(
    description = "Bondage and Discipline (BD), Dominance and Submission (DS), Sadism and Masochism (SM)",
    nsfw = true,
    category = Category.KINKS
)
class BDSM : BbApiCommand("bdsm")
