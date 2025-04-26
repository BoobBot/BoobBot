package bot.boobbot.entities.internals.sql

import bot.boobbot.entities.db.DisabledCommand
import bot.boobbot.entities.db.Guild
import bot.boobbot.entities.db.User
import bot.boobbot.entities.db.WebhookConfiguration
import com.google.gson.Gson
import com.mongodb.client.MongoCollection
import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import org.bson.Document
import org.json.JSONObject
import org.jsoup.internal.StringUtil.StringJoiner
import java.time.Instant
import java.util.concurrent.TimeUnit

class SqlDatabase(host: String, port: String, databaseName: String, user: String, auth: String) {
    private val db: HikariDataSource
    private val gson = Gson()

    init {
        val config = HikariConfig().apply {
            jdbcUrl = "jdbc:mariadb://$host:$port/$databaseName"
            username = user
            password = auth
            leakDetectionThreshold = TimeUnit.SECONDS.toMillis(5)
            driverClassName = "org.mariadb.jdbc.Driver"
        }
        db = HikariDataSource(config)
        setupTables()
    }

    private fun setupTables() {
        execute("CREATE TABLE IF NOT EXISTS webhooks(" +
                "guildId BIGINT NOT NULL," +
                "channelId BIGINT NOT NULL," +
                "category VARCHAR(32) NOT NULL," +
                "webhook VARCHAR(256) NOT NULL," +
                // we may not have duplicate entries with the same 3 fields.
                "UNIQUE(channelId, guildId, category)," +
                // create an index on the fields we're likely to query by (channelId and guildId) so lookups are fast.
                "INDEX channelId(channelId), INDEX guildId(guildId));")

        execute("CREATE TABLE IF NOT EXISTS guilds(" +
                "guildId BIGINT NOT NULL PRIMARY KEY," +
                "dropEnabled BOOLEAN NOT NULL DEFAULT FALSE," +
                "blacklisted BOOLEAN NOT NULL DEFAULT FALSE," +
                "premiumRedeemer BIGINT DEFAULT NULL," +
                "INDEX premiumRedeemer(premiumRedeemer);")

        // TODO --- we store this separately to guild data so the codebase needs refactoring to account for this.
        // also needs methods for fetching and setting these.
        execute("CREATE TABLE IF NOT EXISTS ignored_channels(" +
                "guildId BIGINT NOT NULL," +
                "channelId BIGINT NOT NULL," +
                "UNIQUE(guildId, channelId)," +
                "INDEX guildId(guildId), INDEX channelId(channelId)," +
                // joins this table up to `guilds` so that whenever a guild is removed from `guilds`,
                // it is also deletes from this table.
                "FOREIGN KEY (guildId) REFERENCES guilds(guildId) ON DELETE CASCADE);")

        execute("CREATE TABLE IF NOT EXISTS disabled_commands(" +
                "guildId BIGINT NOT NULL," +
                "name VARCHAR(64) NOT NULL," +
                "UNIQUE(guildId, name)," +
                "INDEX guildId(guildId), INDEX name(name)," +
                // joins this table up to `guilds` so that whenever a guild is removed from `guilds`,
                // it is also deletes from this table.
                "FOREIGN KEY (guildId) REFERENCES guilds(guildId) ON DELETE CASCADE);")

        execute("CREATE TABLE IF NOT EXISTS channel_disabled(" +
                "guildId BIGINT NOT NULL," +
                "channelId BIGINT NOT NULL," +
                "name VARCHAR(64) NOT NULL," +
                "UNIQUE(guildId, channelId, name)," +
                "INDEX guildId(guildId), INDEX channelId(channelId), INDEX name(name)," +
                // joins this table up to `guilds` so that whenever a guild is removed from `guilds`,
                // it is also deletes from this table.
                "FOREIGN KEY (guildId) REFERENCES guilds(guildId) ON DELETE CASCADE);")

        execute("CREATE TABLE IF NOT EXISTS mod_mute(" +
                "guildId BIGINT NOT NULL," +
                "userId BIGINT NOT NULL," +
                "name VARCHAR(64) NOT NULL," +
                "INDEX guildId(guildId), INDEX userId(userId)," +
                "UNIQUE(guildId, userId)," +
                // joins this table up to `guilds` so that whenever a guild is removed from `guilds`,
                // it is also deletes from this table.
                "FOREIGN KEY (guildId) REFERENCES guilds(guildId) ON DELETE CASCADE);")
        // -------------------------------------

        execute("CREATE TABLE IF NOT EXISTS users(" +
                "userId BIGINT NOT NULL PRIMARY KEY," +
                "json LONGTEXT NOT NULL);") // don't want to deal with converting this to proper SQL structure for now.

        execute("CREATE TABLE IF NOT EXISTS custom_commands(" +
                "guildId BIGINT NOT NULL," +
                "name VARCHAR(128) NOT NULL," +
                "content VARCHAR(4000) NOT NULL," + // max message length is 4000.
                "UNIQUE(guildId, name));") // set a unique constraint to ensure we don't have entries with duplicate guildId and name values.
    }

    fun getWebhooks(guildId: String): List<WebhookConfiguration> {
        return find("SELECT channelId, category, webhook FROM webhooks WHERE guildId = ?", guildId)
            .map { WebhookConfiguration(it["category"], it["channelId"], it["webhook"]) }
    }

    fun setWebhook(guildId: String, webhookUrl: String, category: String, channelId: String) {
        execute("INSERT INTO webhooks VALUES (?, ?, ?, ?)", channelId, guildId, category, webhookUrl)
    }

    fun deleteWebhook(guildId: String, channelId: String, category: String) {
        execute("DELETE FROM webhooks WHERE guildId = ? AND channelId = ? AND category = ?", guildId, channelId, category)
    }

    fun deleteWebhooks(guildId: String) {
        execute("DELETE FROM webhooks WHERE guildId = ?", guildId)
    }

    fun getGuild(guildId: String): Guild {
        return findOne("SELECT json FROM guilds WHERE guildId = ?", guildId)
            // TODO: test return type of blacklisted as booleans are stored as ints
            ?.let { Guild(guildId, it["dropEnabled"], it["blacklisted"], it["premiumRedeemer"]) }
            ?: return Guild(guildId)
    }

    fun setGuild(guild: Guild) {
        execute(
            "INSERT INTO guilds (guildId, dropEnabled, blacklisted, premiumRedeemer) VALUES (?, ?, ?, ?) " +
                    "ON DUPLICATE KEY UPDATE dropEnabled = VALUES(dropEnabled), blacklisted = VALUES(blacklisted), premiumRedeemer = VALUES(premiumRedeemer)",
            // TODO: test whether booleans are coerced to an int
            guild.id, guild.dropEnabled, guild.blacklisted, guild.premiumRedeemer
        )
    }

    fun deleteGuild(guildId: String) {
        execute("DELETE FROM guilds WHERE guildId = ?", guildId)
    }

    fun getAllUsers(): List<User> {
        // TODO this will probably be SUPER expensive, needs sorting.
        return find("SELECT json FROM users").map { deserialize<User>(it["json"]) }
    }

    fun getUser(userId: String): User {
        return findOne("SELECT json FROM users WHERE userId = ?", userId)
            ?.get<String>("json")
            ?.let { deserialize<User>(it) }
            ?: return User(userId)
    }

    fun setUser(user: User) {
        user.lastSaved = Instant.now()
        val serialized = serialize(user)
        execute("INSERT INTO users (userId, json) VALUES (?, ?) ON DUPLICATE KEY UPDATE json = VALUES(json)", user._id, serialized)
    }

    fun deleteUser(userId: String) {
        execute("DELETE FROM users WHERE userId = ?", userId)
    }

    fun getCustomCommands(guildId: String): Map<String, String> {
        return find("SELECT name, content FROM custom_commands WHERE guildId = ?", guildId)
            .associate { it.get<String>("name") to it["content"] }
    }

    fun setCustomCommand(guildId: String, name: String, content: String) {
        execute(
            "INSERT INTO custom_commands (guildId, name, content) VALUES (?, ?, ?) ON DUPLICATE KEY UPDATE content = VALUES(content)",
            guildId, name, content
        )
    }

    fun deleteCustomCommand(guildId: String, name: String) {
        execute("DELETE FROM custom_commands WHERE guildId = ? AND name = ?", guildId, name)
    }

    fun deleteCustomCommands(guildId: String) {
        execute("DELETE FROM custom_commands WHERE guildId = ?", guildId)
    }

    // todo below here.
    //<editor-fold desc="Disable-able Commands">
    fun getDisabledCommands(guildId: String): List<String> {
        return find("SELECT name FROM disabled_commands WHERE guildId = ?", guildId).map { it["name"] }
    }

    fun disableCommands(guildId: String, commands: List<String>) {
        val placeholders = StringJoiner(", ")
        val entries = Array(2 * commands.size) { "" }

        for (index in commands.indices step 2) {
            // crude way of being able to insert all commands in one INSERT statement.
            // we add two placeholders for each command, (guildId, command).
            placeholders.add("(?, ?)")
            // and then populate an array of entries so we can pass this to the execute command with the spread operator.
            entries[index] = guildId
            entries[index + 1] = commands[index]
        }

        // insert ignore basically allows us to insert as many commands as we can, ignoring any that raise
        // exceptions (i.e. because of duplicate keys failing the constraint check (UNIQUE(guildId, name)).
        execute("INSERT IGNORE INTO disabled_commands (guildId, name) VALUES $placeholders", *entries)
    }

    fun enableCommands(guildId: String, commands: List<String>) {
        val placeholders = StringJoiner(", ")

        for (item in commands) {
            placeholders.add("?")
        }

        execute("DELETE FROM disabled_commands WHERE guildId = ? AND name IN ($placeholders)", guildId, *commands.toTypedArray())
    }

    fun getDisabledForChannel(guildId: String, channelId: String): List<String> {
        return find("SELECT name FROM channel_disabled WHERE guildId = ? AND channelId = ?", guildId, channelId)
            .map { it["name"] }
    }

    fun disableForChannel(guildId: String, channelId: String, commands: List<String>) {
        val placeholders = StringJoiner(", ")
        val entries = Array(3 * commands.size) { "" }

        for (index in commands.indices step 3) {
            // crude way of being able to insert all commands in one INSERT statement.
            // we add two placeholders for each command, (guildId, channelId, command).
            placeholders.add("(?, ?, ?)")
            // and then populate an array of entries so we can pass this to the execute command with the spread operator.
            entries[index] = guildId
            entries[index + 1] = channelId
            entries[index + 2] = commands[index]
        }

        // insert ignore basically allows us to insert as many commands as we can, ignoring any that raise
        // exceptions (i.e. because of duplicate keys failing the constraint check (UNIQUE(guildId, name)).
        execute("INSERT IGNORE INTO channel_disabled (guildId, channelId, name) VALUES $placeholders", *entries)
    }

    fun enableForChannel(guildId: String, channelId: String, commands: List<String>) {
        val placeholders = StringJoiner(", ")

        for (item in commands) {
            placeholders.add("?")
        }

        execute("DELETE FROM channel_disabled WHERE guildId = ? AND channelId = ? AND name IN ($placeholders)", guildId, channelId, *commands.toTypedArray())
    }
    //</editor-fold>

    fun getDonor(userId: String) = getUser(userId).pledge
    fun setDonor(userId: String, pledge: Double) = getUser(userId).apply { this.pledge = pledge }.save()
    fun getAllDonors() = getAllUsers().associate { it._id to it.pledge }

    // TODO this needs testing as MariaDB returns booleans as ints
    fun isPremiumServer(guildId: String) = findOne("SELECT premiumRedeemer IS NOT NULL and premiumRedeemer > 0 AS has_premium FROM guilds WHERE guildId = ?", guildId)?.get<Boolean>("has_premium") ?: false
    fun setPremiumServer(guildId: String, redeemerId: Long?) = execute("UPDATE guilds SET premiumRedeemer = ? WHERE guildId = ?", redeemerId, guildId)
    fun getPremiumServers(redeemerId: Long) = find("SELECT guildId FROM guilds WHERE premiumRedeemer = ?", redeemerId).map { it.get<Long>("guildId") }

    // everything below here is crude and should probably be improved when the user stuff is *properly* done.
    fun getCanUserReceiveNudes(userId: String) = getUser(userId).nudes
    fun setUserCanReceiveNudes(userId: String, canReceive: Boolean) = getUser(userId).apply { nudes = canReceive }.save()

    fun getUserCockBlocked(userId: String) = getUser(userId).cockblocked
    fun setUserCockBlocked(userId: String, cockblocked: Boolean) = getUser(userId).apply { this.cockblocked = cockblocked }.save()

    fun getUserAnonymity(userId: String) = getUser(userId).anonymity
    fun setUserAnonymity(userId: String, anonymity: Boolean) = getUser(userId).apply { this.anonymity = anonymity }.save()

    /**
     * Execute a statement on the database. This method cannot be used to find anything,
     * but is more for setting data.
     */
    private fun execute(query: String, vararg parameters: Any?) {
        db.connection.use { conn ->
            conn.prepareStatement(query).use {
                for (i in parameters.indices) {
                    it.setObject(i + 1, parameters[i])
                }

                it.executeUpdate()
            }
        }
    }

    private fun find(query: String, vararg parameters: Any): List<Row> {
        db.connection.use { conn ->
            conn.prepareStatement(query).use {
                for (i in parameters.indices) {
                    it.setObject(i + 1, parameters[i])
                }

                it.executeQuery().use { result ->
                    val rows = mutableListOf<Row>()

                    while (result.next()) {
                        val data = mutableMapOf<String, Any>()

                        for (i in 1..result.metaData.columnCount) {
                            data[result.metaData.getColumnName(i)] = result.getObject(i)
                        }

                        rows.add(Row(data))
                    }

                    return rows
                }
            }
        }
    }

    private fun findOne(query: String, vararg parameters: Any): Row? {
        db.connection.use { conn ->
            conn.prepareStatement(query).use {
                for (i in parameters.indices) {
                    it.setObject(i + 1, parameters[i])
                }

                it.executeQuery().use { result ->
                    if (result.next()) {
                        val data = mutableMapOf<String, Any>()

                        for (i in 1..result.metaData.columnCount) {
                            data[result.metaData.getColumnName(i)] = result.getObject(i)
                        }

                        return Row(data)
                    }

                    return null
                }
            }
        }
    }

    private inline fun <reified T> deserialize(json: String): T = gson.fromJson(json, T::class.java)
    private fun serialize(entity: Any): Document = Document.parse(gson.toJson(entity))

    private inner class Row(private val data: Map<String, Any>) {
        inline operator fun <reified T> get(column: String): T {
            return data[column]
                ?.let { it as? T ?: throw IllegalStateException("Database type does not match ${T::class.simpleName}") }
                ?: throw IllegalStateException("Key does not exist")
        }

        inline fun <reified T> getOrNull(column: String): T? {
            return data[column]?.let {
                it as? T ?: throw IllegalStateException("Database type does not match ${T::class.simpleName}")
            }
        }
    }
}
