package bot.boobbot.slashcommands.economy

import bot.boobbot.BoobBot
import bot.boobbot.entities.framework.*
import com.mongodb.BasicDBObject
import com.mongodb.client.model.Filters
import kotlinx.coroutines.future.await
import net.dv8tion.jda.api.EmbedBuilder
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent

@CommandProperties(description = "See your current rank info.", aliases = ["level", "lvl"], category = Category.ECONOMY)
class Level : AsyncSlashCommand {

    override suspend fun executeAsync(event: SlashCommandInteractionEvent) {
        val msg = buildMessage("level", event)
        event.replyEmbeds(
            EmbedBuilder().apply {
                setAuthor("Global rank leaderboard \uD83C\uDFC6", null, event.jda.selfUser.avatarUrl)
                addField("", msg, false)
                setFooter("Requested by ${event.user.name}", event.user.effectiveAvatarUrl)
            }.build()
        ).queue()
    }


    suspend fun buildMessage(key: String, event: SlashCommandInteractionEvent): String {
        var msg = ""
        var count = 0
        BoobBot.database.getAllUsers().find().sort(BasicDBObject(key, -1)).limit(25).iterator().forEach { u ->
            if (count >= 15) {
                return@forEach
            }
            try {

                val user = event.jda.retrieveUserById(u.getString("_id")).submit().await()

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
