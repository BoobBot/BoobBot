package bot.boobbot.commands.interactions

import bot.boobbot.entities.framework.*

@CommandProperties(description = "lick someone.", category = Category.INTERACTIONS, nsfw = true)
class Lick : BbApiInteractionCommand("lick", "<a:play:866441014830563388> %s licks %s")
