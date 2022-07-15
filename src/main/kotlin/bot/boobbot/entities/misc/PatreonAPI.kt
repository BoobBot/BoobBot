package bot.boobbot.entities.misc

import bot.boobbot.BoobBot
import bot.boobbot.entities.internals.Config
import bot.boobbot.utils.TimerUtil
import bot.boobbot.utils.json
import okhttp3.Request
import org.apache.http.client.utils.URIBuilder
import org.json.JSONObject
import org.slf4j.LoggerFactory
import java.net.URI
import java.net.URLDecoder
import java.util.concurrent.CompletableFuture
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit

class PatreonAPI(private val accessToken: String) {

    /**
     * Scheduler stuff
     */
    private val monitor = Executors.newSingleThreadScheduledExecutor { Thread(it, "Pledge-Monitor") }

    init {
        monitor.scheduleAtFixedRate({
            try {
                monitorPledges()
            } catch (e: Throwable) {
                log.error("Error in Patreon monitor", e)
            }
        }, 0, 1, TimeUnit.DAYS)
    }

    fun getDonorType(userId: String): DonorType {
        return when {
            Config.OWNERS.contains(userId.toLong()) -> DonorType.DEVELOPER
            else -> DonorType.which(BoobBot.database.getDonor(userId))
        }
    }

    fun getDonorType(amount: Double): DonorType = DonorType.which(amount)

    private fun monitorPledges() {
        log.info("Patreon cleanup task running...")
        val timer = TimerUtil("cleanup-task")

        fetchPledgesOfCampaign("1928035").thenAccept { users ->
            val fetchElapsedTime = timer.elapsedFormatted()

            if (users.isEmpty()) {
                return@thenAccept log.warn("Scheduled pledge clean failed: No users to check")
            }

            val allDonors = BoobBot.database.getAllDonors()
            var removed = 0
            var adjusted = 0

            for ((id, pledge) in allDonors) {
                val idLong = id.toLong()
                val user = users.firstOrNull { it.discordId != null && it.discordId == idLong }

                @Suppress("KotlinConstantConditions")
                if (user == null || user.isDeclined) {
                    BoobBot.database.removeDonor(id)
                    BoobBot.database.setUserCockBlocked(id, false)
                    BoobBot.database.setUserAnonymity(id, false)
                    removed += 1
                    log.debug("User $id removed: ${user?.isDeclined?.let { "declined payment" } ?: "not found"}")
                    continue
                }

                val amount = user.pledgeCents.toDouble() / 100

                if (pledge != amount) {
                    BoobBot.database.setDonor(id, amount)
                    adjusted += 1
                    log.debug("User $id adjusted: $amount -> $pledge")
                }
            }

            val taskElapsedTime = timer.elapsedFormatted()
            log.info("Patreon cleanup task completed in $taskElapsedTime (fetch took $fetchElapsedTime). Adjusted $adjusted/${allDonors.size}. Removed $removed.")

            // The above only handles users that have registered into the system.
            // To register into the system, users can run `bbperks` to receive their rewards after
            // pledging on Patreon.
        }
    }


    /**
     * Functional stuff
     */

    fun fetchPledgesOfCampaign(campaignId: String) = CompletableFuture<List<PatreonUser>>().also { f -> getPageOfPledge(campaignId) { f.complete(it) } }

    private fun getPageOfPledge(
        campaignId: String, offset: String? = null,
        users: MutableList<PatreonUser> = mutableListOf(), cb: (List<PatreonUser>) -> Unit
    ) {
        val url = URIBuilder("$BASE_URL/campaigns/$campaignId/pledges")
            .addParameter("include", "pledge,patron")
            .also { url -> offset?.let { url.addParameter("page[cursor]", offset) } }
            .build()

        val request = createRequest(url.toString())

        BoobBot.requestUtil.makeRequest(request).queue {
            if (it == null || !it.isSuccessful) {
                BoobBot.log.error("Unable to get list of pledges ({}): {}", it?.code, it?.message)
                it?.close()

                return@queue cb(users)
            }

            val json = it.json() ?: return@queue cb(users)
            val pledges = json.getJSONArray("data")

            json.getJSONArray("included").forEachIndexed { index, user ->
                val obj = user as JSONObject

                if (obj.getString("type") == "user") {
                    users.add(buildUser(obj, pledges.getJSONObject(index)))
                }
            }

            val nextPage = getNextPage(json) ?: return@queue cb(users)
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

    private fun parseQueryString(url: String): Map<String, String> {
        return URI(url).query.split("&")
            .map { it.split("=") }
            .associate { Pair(decode(it[0]), decode(it[1])) }
    }

    private fun decode(s: String) = URLDecoder.decode(s, Charsets.UTF_8)

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
            userAttr.get("first_name").toString(),
            userAttr.get("last_name").toString(),
            userAttr.getString("email"),
            pledgeAttr.getInt("amount_cents"),
            !pledgeAttr.isNull("declined_since"),
            discordId
        )
    }

    inner class PatreonUser(
        val firstName: String,
        val lastName: String,
        val email: String,
        val pledgeCents: Int,
        val isDeclined: Boolean,
        val discordId: Long?
    )

    companion object {
        private const val BASE_URL = "https://www.patreon.com/api/oauth2/api"
        private val log = LoggerFactory.getLogger(PatreonAPI::class.java)
    }
}

enum class DonorType(val tier: Int) {
    NONE(0),
    SUPPORTER(1),
    SERVER_OWNER(2),
    DEVELOPER(3);

    companion object {
        fun which(pledgeAmount: Double): DonorType {
            return when {
                pledgeAmount >= 23 -> SERVER_OWNER
                pledgeAmount >= 4 -> SUPPORTER
                else -> NONE
            }
        }
    }
}
