package bot.boobbot.commands.nsfw

import bot.boobbot.entities.framework.Category
import bot.boobbot.entities.framework.annotations.Choice
import bot.boobbot.entities.framework.annotations.CommandProperties
import bot.boobbot.entities.framework.annotations.Option
import bot.boobbot.entities.framework.impl.SlideShowCommand

@CommandProperties(
    description = "Cycles though 20 images at 5 seconds each.",
    category = Category.GENERAL,
    aliases = ["ss"],
    nsfw = true,
    donorOnly = true
)
@Option(name = "category", description = "The image category to view.", choices = [Choice("Boobs", "boobs"), Choice("Ass", "ass"), Choice("Dick", "penis"), Choice("Gif", "Gifs"), Choice("Gay", "gay"), Choice("Tiny", "tiny"), Choice("Cum Sluts", "cumsluts"), Choice("Collared", "collared"), Choice("Yiff", "yiff"), Choice("Tentacle", "tentacle"), Choice("Thicc", "thicc"), Choice("Red", "red")])
class SlideShow : SlideShowCommand()
