package bot.boobbot.misc

import bot.boobbot.BoobBot
import bot.boobbot.flight.Context
import bot.boobbot.models.Config
import okhttp3.Request
import org.apache.http.client.utils.URIBuilder
import org.json.JSONObject
import org.slf4j.LoggerFactory
import java.net.URI
import java.net.URLDecoder
import java.util.concurrent.CompletableFuture
import java.util.concurrent.Executors

class PatreonAPI(private val accessToken: String) {

    /**
     * Scheduler stuff
     */
    private val monitor = Executors.newSingleThreadScheduledExecutor { Thread(it, "Pledge-Monitor") }

    init {
        monitorPledges()
    }
    // $5 = paid commands for current user
    // $30 = paid commands for everyone in all servers owned by the user

    fun getDonorType(userId: String): DonorType {
        if (Config.owners.contains(userId.toLong())) {
            return DonorType.DEVELOPER
        }

        val pledge = BoobBot.database.getDonor(userId)
        return DonorType.which(pledge)
    }

    fun getDonorType(amount: Double): DonorType {
        return DonorType.which(amount)
    }

    fun monitorPledges() {
        log.info("Checking pledges...")
        val s = System.currentTimeMillis()

        fetchPledgesOfCampaign("1928035").thenAccept { users ->
            val e = System.currentTimeMillis()
            println("Found ${users.size} in ${e - s}ms")

            if (users.isEmpty()) {
                println("empty, fuck")
                return@thenAccept log.warn("[SUSPICIOUS] Scheduled pledge clean failed: No users to check")
            }

            val allDonors = BoobBot.database.getAllDonors()

            for (( id, pledge ) in allDonors) {
                val idLong = id.toLong()
                val user = users.firstOrNull { it.discordId != null && it.discordId == idLong }

                if (user == null || user.isDeclined) {
                    BoobBot.database.removeDonor(id)
                    log.info("Removed $id from donors")
                    continue
                }

                val amount = user.pledgeCents.toDouble() / 100

                if (pledge != amount) {
                    log.info("Adjusting $id's pledge (saved: $pledge, current: $amount)")
                    BoobBot.database.setDonor(id, amount)
                }
            }

            // The above only handles users that have registered into the system.
            // To register into the system, users can run `bbperks` to receive their rewards after
            // pledging on Patreon.
        }
    }


    /**
     * Functional stuff
     */

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

        getPageOfPledge(campaignId) {
            future.complete(it)
        }

        return future
    }

    private fun getPageOfPledge(campaignId: String, offset: String? = null,
                                users: MutableSet<PatreonUser> = mutableSetOf(), cb: (List<PatreonUser>) -> Unit) {
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
            getPageOfPledge(campaignId, nextPage, users, cb)
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
        private val log = LoggerFactory.getLogger(PatreonAPI::class.java)
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

enum class DonorType(val tier: Int) {
    NONE(0),
    SUPPORTER(1),
    SERVER_OWNER(2),
    DEVELOPER(3);

    companion object {
        fun which(pledgeAmount: Double): DonorType {
            return when {
                pledgeAmount >= 30 -> SERVER_OWNER
                pledgeAmount >= 5 -> SUPPORTER
                else -> NONE
            }
        }
    }
}
