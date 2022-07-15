package bot.boobbot.commands.economy

import bot.boobbot.BoobBot
import bot.boobbot.entities.framework.AsyncCommand
import bot.boobbot.entities.framework.CommandProperties
import bot.boobbot.entities.framework.Context
import bot.boobbot.entities.framework.SubCommand
import com.mongodb.BasicDBObject
import com.mongodb.client.model.Filters
import kotlinx.coroutines.future.await

@CommandProperties(description = "Global leaderboards \uD83C\uDFC6", aliases = ["lb"], guildOnly = true)
class Leaderboard : AsyncCommand {

    override suspend fun executeAsync(ctx: Context) {
        sendSubcommandHelp(ctx)
    }

    @SubCommand(aliases = ["richest", "$"], description = "Global economy leaderboard \uD83C\uDFC6", async = true)
    suspend fun cash(ctx: Context) {
        val msg = buildMessage("balance", ctx)
                 ctx.send {
                    setAuthor("Global economy leaderboard \uD83C\uDFC6", null, ctx.selfUser.avatarUrl)
                    addField("", msg, false)
                    setFooter("Requested by ${ctx.author.name}", ctx.author.effectiveAvatarUrl)
                }
            }



    @SubCommand(aliases = ["exp", "xp", "rank"], description = "Global rank leaderboard \uD83C\uDFC6", async = true)
    suspend fun level(ctx: Context) {
        val msg = buildMessage("level", ctx)
            ctx.send {
                setAuthor("Global rank leaderboard \uD83C\uDFC6", null, ctx.selfUser.avatarUrl)
                addField("", msg, false)
                setFooter("Requested by ${ctx.author.name}", ctx.author.effectiveAvatarUrl)
            }
    }

    @SubCommand(aliases = ["reputation"], description = "Global reputation leaderboard \uD83C\uDFC6", async = true)
    suspend fun rep(ctx: Context) {
        val msg = buildMessage("rep", ctx)
                ctx.send {
                    setAuthor("Global reputation leaderboard \uD83C\uDFC6", null, ctx.selfUser.avatarUrl)
                    addField("", msg, false)
                    setFooter("Requested by ${ctx.author.name}", ctx.author.effectiveAvatarUrl)
                }
        }


    suspend fun buildMessage(key: String, ctx: Context): String {
        var msg = ""
        var count = 0
        BoobBot.database.getAllUsers().find().sort(BasicDBObject(key, -1)).limit(25).iterator().forEach { u ->

            if (count >= 15) {
                return@forEach
            }
            try {
                val user = ctx.jda.retrieveUserById(u.getString("_id")).submit().await()

                if (user.name.contains("Deleted User")) {
                    return@forEach
                }

                if (user.isBot) {
                    BoobBot.database.getAllUsers().deleteOne(Filters.eq("_id", u["_id"]))
                    return@forEach
                }
                val label = if (key.contains("balance")) {"$key: $"} else "$key: "
                msg += "${count + 1}:  ***${user.name}***   $label***${u[key]}***\n"
                count++
            } catch (e: Exception) {
                BoobBot.database.getAllUsers().deleteOne(Filters.eq("_id", u["_id"]))
                return@forEach
            }
        }

        return msg

    }
}
