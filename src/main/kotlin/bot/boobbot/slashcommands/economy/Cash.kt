package bot.boobbot.slashcommands.economy

import bot.boobbot.BoobBot
import bot.boobbot.entities.framework.AsyncSlashCommand
import bot.boobbot.entities.framework.Category
import bot.boobbot.entities.framework.annotations.CommandProperties
import bot.boobbot.entities.framework.SlashContext
import com.mongodb.BasicDBObject
import com.mongodb.client.model.Filters
import kotlinx.coroutines.future.await

@CommandProperties(description = "Global leaderboards \uD83C\uDFC6.", aliases = [], category = Category.ECONOMY)
class Cash : AsyncSlashCommand {
    override suspend fun executeAsync(ctx: SlashContext) {
        val msg = buildMessage("balance", ctx)
        ctx.reply {
            setAuthor("Global economy leaderboard \uD83C\uDFC6", null, ctx.jda.selfUser.avatarUrl)
            addField("", msg, false)
            setFooter("Requested by ${ctx.user.name}", ctx.user.effectiveAvatarUrl)
        }
    }


    suspend fun buildMessage(key: String, ctx: SlashContext): String {
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
                val label = if (key.contains("balance")) {
                    "$key: $"
                } else "$key: "
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
