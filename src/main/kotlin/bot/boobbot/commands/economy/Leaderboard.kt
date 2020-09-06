package bot.boobbot.commands.economy

import bot.boobbot.BoobBot
import bot.boobbot.entities.framework.Command
import bot.boobbot.entities.framework.CommandProperties
import bot.boobbot.entities.framework.Context

@CommandProperties(description = "Global economy leaderboard \uD83C\uDFC6", guildOnly = true)
class Leaderboard : Command {

    override fun execute(ctx: Context) {
        var msg = ""
        var c = 0
        BoobBot.database.getAllUsers().iterator().forEach { u ->
            c++
            val user = ctx.jda.retrieveUserById(u.getString("_id")).complete()
            msg += "$c: ***${user.name}***  balance: $***${u["balance"]}***\n"
        }
        ctx.embed {
            setAuthor("Global economy leaderboard \uD83C\uDFC6", null, ctx.selfUser.avatarUrl)
            addField("", msg, false)
            setFooter("Requested by ${ctx.author.name}", ctx.author.effectiveAvatarUrl)
        }
    }

}
