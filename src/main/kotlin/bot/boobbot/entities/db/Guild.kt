package bot.boobbot.entities.db

import bot.boobbot.BoobBot
import com.google.gson.annotations.SerializedName
import org.json.JSONArray
import org.json.JSONObject


data class Guild(
    val id: String,
    var dropEnabled: Boolean = false,
    var blacklisted: Boolean = false,
    var premiumRedeemer: Long? = null
) {
    fun save() = BoobBot.database.setGuild(this)
    fun delete() = BoobBot.database.deleteGuild(id)
}
