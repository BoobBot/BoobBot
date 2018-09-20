package bot.boobbot.commands.`fun`

import bot.boobbot.flight.CommandProperties
import bot.boobbot.models.FunCommand

@CommandProperties(description = "fun interactions.", category = CommandProperties.category.FUN, aliases = ["ini"])
class Interact : FunCommand("interaction")
