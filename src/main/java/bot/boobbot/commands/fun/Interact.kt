package bot.boobbot.commands._fun

import bot.boobbot.flight.CommandProperties
import bot.boobbot.models.FunCommand

@CommandProperties(description = "fun interactions.", nsfw = false, category = CommandProperties.category.FUN, aliases = ["ini"])
class Interact : FunCommand("interaction")
