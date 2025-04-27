package bot.boobbot.entities.misc

import bot.boobbot.BoobBot
import bot.boobbot.utils.TimerUtil
import bot.boobbot.utils.json
import okhttp3.Request
import org.apache.http.client.utils.URIBuilder
import org.json.JSONObject
import org.slf4j.LoggerFactory
import java.util.concurrent.CompletableFuture
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit

class PatreonAPI(private val accessToken: String, enableMonitoring: Boolean = true) {
    /**
     * Scheduler stuff
     */
    private val monitor = Executors.newSingleThreadScheduledExecutor { Thread(it, "Pledge-Monitor") }

    init {
        if (enableMonitoring) {
            monitor.scheduleAtFixedRate({
                try {
                    monitorPledges()
                } catch (e: Throwable) {
                    log.error("Error in Patreon monitor", e)
                }
            }, 0, 1, TimeUnit.DAYS)
        }
    }

    fun getDonorType(userId: Long): DonorType {
        return when {
            BoobBot.owners.contains(userId) -> DonorType.DEVELOPER
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

            var processed = 0
            var removed = 0
            var adjusted = 0

            for (row in BoobBot.database.iterate("SELECT userId, JSON_UNQUOTE(JSON_EXTRACT(json, '$.pledge')) as pledge FROM users")) {
                processed++

                val id: Long = row["userId"]
                val pledge: Double = row["pledge"]
                val user = users.firstOrNull { it.discordId != null && it.discordId == id }

                if (user == null || user.status != PatronStatus.ACTIVE_PATRON) {
                    BoobBot.database.setDonor(id, 0.0)
                    BoobBot.database.setUserCockBlocked(id, false)
                    BoobBot.database.setUserAnonymity(id, false)

                    for (guildId in BoobBot.database.getPremiumServers(id)) {
                        BoobBot.database.setPremiumServer(guildId, null)
                        BoobBot.database.deleteWebhooks(guildId)
                    }

                    removed += 1
                    log.debug("User $id removed: ${user?.let { "declined payment" } ?: "not found"}") // user can only exist in this block if they're declined.
                    continue
                }

                val amount = user.entitledAmountCents.toDouble() / 100

                if (pledge != amount) {
                    BoobBot.database.setDonor(id, amount)
                    adjusted += 1
                    log.debug("User $id adjusted: $amount -> $pledge")
                }
            }

            val taskElapsedTime = timer.elapsedFormatted()
            log.info("Patreon cleanup task completed in $taskElapsedTime (fetch took $fetchElapsedTime). Adjusted $adjusted/$processed. Removed $removed.")

            // The above only handles users that have registered into the system.
            // To register into the system, users can run `bbperks` to receive their rewards after
            // pledging on Patreon.
        }
    }


    /**
     * Functional stuff
     */

    fun fetchPledgesOfCampaign(campaignId: String) = CompletableFuture<List<PatreonUser>>().also { f -> getPageOfPledge0(campaignId) { f.complete(it) } }

    private fun getPageOfPledge0(campaignId: String, cb: (List<PatreonUser>) -> Unit) {
        val url = URIBuilder("$BASE_URL/campaigns/$campaignId/members")
            .addParameter("include", "user")
            .addParameter("fields[member]", "full_name,email,last_charge_status,currently_entitled_amount_cents,patron_status")
            .addParameter("fields[user]", "social_connections")
            .addParameter("page[size]", "1000")
            .build()

        return getPageOfPledge(url.toString(), cb)
    }

    private fun getPageOfPledge(url: String, cb: (List<PatreonUser>) -> Unit, users: MutableList<PatreonUser> = mutableListOf()) {
        val request = createRequest(url)

        BoobBot.requestUtil.makeRequest(request).queue {
            if (it == null || !it.isSuccessful) {
                BoobBot.log.error("Unable to get list of pledges ({}): {}", it?.code, it?.message)
                it?.close()

                return@queue cb(users)
            }

            val json = it.json() ?: return@queue cb(users)
            val members = json.getJSONArray("data")

            json.getJSONArray("included").forEachIndexed { index, user ->
                val obj = user as JSONObject

                if (obj.getString("type") == "user") {
                    users.add(buildUser(obj, members.getJSONObject(index)))
                }
            }

            val nextPageUrl = getNextPage(json) ?: return@queue cb(users)
            getPageOfPledge(nextPageUrl, cb, users)
        }
    }

    private fun getNextPage(json: JSONObject): String? {
        val links = json.optJSONObject("links")

        if (links == null || !links.has("next") || links.isNull("next")) {
            return null
        }

        return links.getString("next")
    }

    private fun createRequest(url: String): Request {
        return Request.Builder()
            .url(url)
            .get()
            .header("Authorization", "Bearer $accessToken")
            .build()
    }

    private fun buildUser(user: JSONObject, member: JSONObject): PatreonUser {
        val memberAttr = member.getJSONObject("attributes")
        val userAttr = user.getJSONObject("attributes")

        val connections = userAttr.optJSONObject("social_connections")
        val discordId = connections?.optJSONObject("discord")?.getLong("user_id")

        // patron_status: String? = [declined_patron, active_patron, former_patron]
        // email: String?
        // last_charge_status: String? = [Paid, Declined]

        return PatreonUser(
            memberAttr.optString("full_name"),
            memberAttr.optString("email"),
            memberAttr.getInt("currently_entitled_amount_cents"),
            memberAttr.optString("last_charge_status"),
            PatronStatus.from(memberAttr.optString("patron_status")),
            discordId
        )
    }

    inner class PatreonUser(
        val fullName: String?,
        val email: String?,
        /** If this isn't 0, it's the price of the tier the user is signed up to. */
        val entitledAmountCents: Int,
        val lastChargeStatus: String,
        val status: PatronStatus,
        val discordId: Long?
    )

    companion object {
        private const val BASE_URL = "https://www.patreon.com/api/oauth2/v2"
        private val log = LoggerFactory.getLogger(PatreonAPI::class.java)
    }
}

enum class PatronStatus {
    UNKNOWN,
    DECLINED_PATRON,
    FORMER_PATRON,
    ACTIVE_PATRON;

    companion object {
        fun from(type: String?) = PatronStatus.entries.firstOrNull { it.name.lowercase() == type } ?: UNKNOWN
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
                pledgeAmount >= 30 -> SERVER_OWNER
                pledgeAmount >= 5 -> SUPPORTER
                else -> NONE
            }
        }
    }
}
