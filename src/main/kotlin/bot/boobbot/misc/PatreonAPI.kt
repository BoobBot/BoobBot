package bot.boobbot.misc

import bot.boobbot.BoobBot
import okhttp3.Request
import org.apache.http.client.utils.URIBuilder
import org.json.JSONObject
import java.net.URI
import java.net.URLDecoder
import java.util.concurrent.CompletableFuture

class PatreonAPI(private val accessToken: String) {

    fun getCampaigns(): CompletableFuture<List<JSONObject>> {
        val url = "$BASE_URL/current_user/campaigns"
        val request = createRequest(url)
        val future = CompletableFuture<List<JSONObject>>()

        BoobBot.requestUtil.makeRequest(request).queue {
            if (it == null || !it.isSuccessful) {
                BoobBot.log.error("Unable to get list of campaigns ({}): {}", it?.code(), it?.message())
                it?.close()

                future.complete(emptyList())
                return@queue
            }

            val j = it.json()

            if (j == null) {
                future.complete(emptyList())
                return@queue
            }

            val a = j.getJSONArray("data").map { o -> o as JSONObject }.toList() // todo unshit
            future.complete(a)
        }

        return future
    }

    fun fetchPledgesOfCampaign(campaignId: String): CompletableFuture<List<PatreonUser>> {
        val future = CompletableFuture<List<PatreonUser>>()

        getPageOfPledge(campaignId, null) {
            future.complete(it)
        }

        return future
    }

    private fun getPageOfPledge(campaignId: String, offset: String?, cb: (List<PatreonUser>) -> Unit) {
        val users = mutableSetOf<PatreonUser>()

        val url = URIBuilder("$BASE_URL/campaigns/$campaignId/pledges")

        url.addParameter("include", "pledge,patron")

        if (offset != null) {
            url.addParameter("page[cursor]", offset)
        }

        val request = createRequest(url.build().toString())

        BoobBot.requestUtil.makeRequest(request).queue {
            if (it == null || !it.isSuccessful) {
                BoobBot.log.error("Unable to get list of pledges ({}): {}", it?.code(), it?.message())
                it?.close()

                return@queue cb(users.toList())
            }

            val json = it.json() ?: return@queue cb(users.toList())
            val pledges = json.getJSONArray("data")

            json.getJSONArray("included").forEachIndexed { index, user ->
                val obj = user as JSONObject

                if (obj.getString("type") == "user") {
                    users.add(buildUser(obj, pledges.getJSONObject(index)))
                }
            }

            val nextPage = getNextPage(json) ?: return@queue cb(users.toList())
            getPageOfPledge(campaignId, nextPage, cb)
        }
    }

    private fun getNextPage(json: JSONObject): String? {
        val links = json.getJSONObject("links")

        if (!links.has("next")) {
            return null
        }

        return parseQueryString(links.getString("next"))["page[cursor]"]
    }

    fun parseQueryString(url: String): HashMap<String, String> {
        val query = URI(url).query
        val pairs = query.split("&")
        val map = hashMapOf<String, String>()

        for (pair in pairs) {
            val nameValue = pair.split("=")
            val key = URLDecoder.decode(nameValue[0], CHARSET)
            val value = URLDecoder.decode(nameValue[1], CHARSET)

            map[key] = value
        }

        return map
    }

    private fun createRequest(url: String): Request {
        return Request.Builder()
            .url(url)
            .get()
            .header("Authorization", "Bearer $accessToken")
            .build()
    }

    private fun buildUser(user: JSONObject, pledge: JSONObject): PatreonUser {
        val userAttr = user.getJSONObject("attributes")
        val pledgeAttr = pledge.getJSONObject("attributes")

        val connections = userAttr.getJSONObject("social_connections")
        val discordId = if (!connections.isNull("discord")) {
            connections.getJSONObject("discord").getLong("user_id")
        } else {
            null
        }

        return PatreonUser(
            userAttr.getString("first_name"),
            userAttr.getString("last_name"),
            userAttr.getString("email"),
            pledgeAttr.getInt("amount_cents"),
            !pledgeAttr.isNull("declined_since"),
            discordId
        )
    }

    companion object {
        private const val BASE_URL = "https://www.patreon.com/api/oauth2/api/"
        private val CHARSET = Charsets.UTF_8
    }
}


class PatreonUser(
    val firstName: String,
    val lastName: String,
    val email: String,
    val pledgeCents: Int,
    val isDeclined: Boolean,
    val discordId: Long?
)
