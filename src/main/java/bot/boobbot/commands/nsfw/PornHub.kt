package bot.boobbot.commands.nsfw

import bot.boobbot.flight.CommandProperties
import bot.boobbot.models.PhCommand

@CommandProperties(description = "PornHub video search. <:p_:475801484282429450>", donorOnly = true, guildOnly = true, aliases = ["ph"], nsfw = true, category = CommandProperties.category.VIDEOSEARCHING)
class PornHub : PhCommand()
