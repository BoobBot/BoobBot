package bot.boobbot.entities.internals.sql

import bot.boobbot.entities.db.Guild
import bot.boobbot.entities.db.User
import bot.boobbot.entities.db.WebhookConfiguration
import com.google.gson.Gson
import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import org.jsoup.internal.StringUtil.StringJoiner
import java.sql.Connection
import java.sql.PreparedStatement
import java.sql.ResultSet
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
            maximumPoolSize = 100
            leakDetectionThreshold = TimeUnit.SECONDS.toMillis(10)
            driverClassName = "org.mariadb.jdbc.Driver"
        }
        db = HikariDataSource(config)
        setupTables()
    }

    private fun setupTables() {
        execute("CREATE TABLE IF NOT EXISTS guilds(" +
                "guildId BIGINT PRIMARY KEY NOT NULL," +
                "dropEnabled BOOLEAN NOT NULL DEFAULT FALSE," +
                "blacklisted BOOLEAN NOT NULL DEFAULT FALSE," +
                "premiumRedeemer BIGINT DEFAULT NULL," +
                "INDEX premiumRedeemer(premiumRedeemer));")

        execute("CREATE TABLE IF NOT EXISTS webhooks(" +
                "guildId BIGINT NOT NULL," +
                "channelId BIGINT NOT NULL," +
                "category VARCHAR(32) NOT NULL," +
                "webhook VARCHAR(256) NOT NULL," +
                // we may not have duplicate entries with the same 3 fields.
                "UNIQUE(channelId, guildId, category)," +
                "FOREIGN KEY (guildId) REFERENCES guilds(guildId) ON DELETE CASCADE);")

        execute("CREATE TABLE IF NOT EXISTS ignored_channels(" +
                "guildId BIGINT NOT NULL," +
                "channelId BIGINT NOT NULL," +
                "UNIQUE(guildId, channelId)," +
                // joins this table up to `guilds` so that whenever a guild is removed from `guilds`,
                // it is also deletes from this table.
                "FOREIGN KEY (guildId) REFERENCES guilds(guildId) ON DELETE CASCADE);")

        execute("CREATE TABLE IF NOT EXISTS disabled_commands(" +
                "guildId BIGINT NOT NULL," +
                "name VARCHAR(64) NOT NULL," +
                "UNIQUE(guildId, name)," +
                // joins this table up to `guilds` so that whenever a guild is removed from `guilds`,
                // it is also deletes from this table.
                "FOREIGN KEY (guildId) REFERENCES guilds(guildId) ON DELETE CASCADE);")

        execute("CREATE TABLE IF NOT EXISTS channel_disabled(" +
                "guildId BIGINT NOT NULL," +
                "channelId BIGINT NOT NULL," +
                "name VARCHAR(64) NOT NULL," +
                "UNIQUE(guildId, channelId, name)," +
                // joins this table up to `guilds` so that whenever a guild is removed from `guilds`,
                // it is also deletes from this table.
                "FOREIGN KEY (guildId) REFERENCES guilds(guildId) ON DELETE CASCADE);")

        execute("CREATE TABLE IF NOT EXISTS mod_mute(" +
                "guildId BIGINT NOT NULL," +
                "userId BIGINT NOT NULL," +
                "INDEX guildId(guildId), INDEX userId(userId)," +
                // joins this table up to `guilds` so that whenever a guild is removed from `guilds`,
                // it is also deletes from this table.
                "FOREIGN KEY (guildId) REFERENCES guilds(guildId) ON DELETE CASCADE);")

        execute("CREATE TABLE IF NOT EXISTS custom_commands(" +
                "guildId BIGINT NOT NULL," +
                "name VARCHAR(128) NOT NULL," +
                "content VARCHAR(4000) NOT NULL," + // max message length is 4000.
                "UNIQUE(guildId, name)," + // set a unique constraint to ensure we don't have entries with duplicate guildId and name values.
                "FOREIGN KEY (guildId) REFERENCES guilds(guildId) ON DELETE CASCADE);")

        execute("CREATE TABLE IF NOT EXISTS users(" +
                "userId BIGINT PRIMARY KEY NOT NULL," +
                "json JSON NOT NULL," + // don't want to deal with converting this to proper SQL structure for now.
                "CHECK (JSON_VALID(json)));")
    }

    fun getWebhooks(guildId: Long): List<WebhookConfiguration> {
        return find("SELECT channelId, category, webhook FROM webhooks WHERE guildId = ?", guildId)
            .map { WebhookConfiguration(it["category"], it["channelId"], it["webhook"]) }
    }

    fun setWebhook(guildId: Long, channelId: Long, category: String, webhookUrl: String) {
        execute("INSERT INTO webhooks (guildId, channelId, category, webhook) VALUES (?, ?, ?, ?)", guildId, channelId, category, webhookUrl)
    }

    fun deleteWebhooks(guildId: Long) {
        execute("DELETE FROM webhooks WHERE guildId = ?", guildId)
    }

    fun deleteWebhook(guildId: Long, channelId: Long) {
        execute("DELETE FROM webhooks WHERE guildId = ? AND channelId = ?", guildId, channelId)
    }

    fun deleteWebhook(guildId: Long, channelId: Long, category: String) {
        execute("DELETE FROM webhooks WHERE guildId = ? AND channelId = ? AND category = ?", guildId, channelId, category)
    }

    fun getGuild(guildId: Long): Guild {
        return findOne("SELECT dropEnabled, blacklisted, premiumRedeemer FROM guilds WHERE guildId = ?", guildId)
            ?.let { Guild(guildId, it["dropEnabled"], it["blacklisted"], it.getOrNull("premiumRedeemer")) }
            ?: return Guild.new(guildId)
    }

    fun setGuild(guild: Guild) {
        execute(
            "INSERT INTO guilds (guildId, dropEnabled, blacklisted, premiumRedeemer) VALUES (?, ?, ?, ?) " +
                    "ON DUPLICATE KEY UPDATE dropEnabled = VALUES(dropEnabled), blacklisted = VALUES(blacklisted), premiumRedeemer = VALUES(premiumRedeemer)",
            guild.id, guild.dropEnabled, guild.blacklisted, guild.premiumRedeemer
        )
    }

    fun deleteGuild(guildId: Long) {
        execute("DELETE FROM guilds WHERE guildId = ?", guildId)
    }

    fun isIgnoredChannel(guildId: Long, channelId: Long) = findOne("SELECT 1 FROM ignored_channels WHERE guildId = ? AND channelId = ?", guildId, channelId) != null

    fun getIgnoredChannels(guildId: Long): List<Long> {
        return find("SELECT channelId FROM ignored_channels WHERE guildId = ?", guildId).map { it["channelId"] }
    }

    fun setIgnoredChannel(guildId: Long, channelId: Long) {
        execute("INSERT IGNORE INTO ignored_channels (guildId, channelId) VALUES (?, ?)", guildId, channelId)
    }

    fun deleteIgnoredChannel(guildId: Long, channelId: Long) {
        execute("DELETE FROM ignored_channels WHERE guildId = ? AND channelId = ?", guildId, channelId)
    }

    fun isModMuted(guildId: Long, userId: Long) = findOne("SELECT 1 FROM mod_mute WHERE guildId = ? AND userId = ?", guildId, userId) != null

    fun setModMute(guildId: Long, userId: Long) {
        execute("INSERT IGNORE INTO mod_mute (guildId, userId) VALUES (?, ?)", guildId, userId)
    }

    fun deleteModMute(guildId: Long, userId: Long) {
        execute("DELETE FROM mod_mute WHERE guildId = ? AND userId = ?", guildId, userId)
    }

    /**
     * @param by "balance", "level", "rep"
     */
    fun getTopUsers(by: String, limit: Int = 25): List<User> {
        return find("SELECT json, CAST(JSON_UNQUOTE(JSON_EXTRACT(json, '$.$by')) AS UNSIGNED) AS $by FROM users ORDER BY $by DESC LIMIT $limit")
            .map { deserialize<User>(it["json"]) }
    }

    fun getUser(userId: Long): User {
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

    fun deleteUser(userId: Long) {
        execute("DELETE FROM users WHERE userId = ?", userId)
    }

    fun getCustomCommands(guildId: Long): Map<String, String> {
        return find("SELECT name, content FROM custom_commands WHERE guildId = ?", guildId)
            .associate { it.get<String>("name") to it["content"] }
    }

    fun setCustomCommand(guildId: Long, name: String, content: String) {
        execute(
            "INSERT INTO custom_commands (guildId, name, content) VALUES (?, ?, ?) ON DUPLICATE KEY UPDATE content = VALUES(content)",
            guildId, name, content
        )
    }

    fun deleteCustomCommand(guildId: Long, name: String) {
        execute("DELETE FROM custom_commands WHERE guildId = ? AND name = ?", guildId, name)
    }

    fun deleteCustomCommands(guildId: Long) {
        execute("DELETE FROM custom_commands WHERE guildId = ?", guildId)
    }

    //<editor-fold desc="Disable-able Commands">
    fun isCommandDisabled(guildId: Long, name: String) = findOne("SELECT 1 FROM disabled_commands WHERE guildId = ? AND name = ?", guildId, name) != null

    fun getDisabledCommands(guildId: Long): List<String> {
        return find("SELECT name FROM disabled_commands WHERE guildId = ?", guildId).map { it["name"] }
    }

    fun disableCommands(guildId: Long, commands: List<String>) {
        val placeholders = StringJoiner(", ")
        val entries = Array<Any>(2 * commands.size) { "" }

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

    fun enableCommands(guildId: Long, commands: List<String>) {
        val placeholders = StringJoiner(", ")

        for (item in commands) {
            placeholders.add("?")
        }

        execute("DELETE FROM disabled_commands WHERE guildId = ? AND name IN ($placeholders)", guildId, *commands.toTypedArray())
    }

    fun isCommandDisabledInChannel(guildId: Long, channelId: Long, name: String) = findOne("SELECT 1 FROM channel_disabled WHERE guildId = ? AND channelId = ? AND name = ?", guildId, channelId, name) != null

    fun getDisabledForChannel(guildId: Long, channelId: Long): List<String> {
        return find("SELECT name FROM channel_disabled WHERE guildId = ? AND channelId = ?", guildId, channelId)
            .map { it["name"] }
    }

    fun disableForChannel(guildId: Long, channelId: Long, commands: List<String>) {
        val placeholders = StringJoiner(", ")
        val entries = Array<Any>(3 * commands.size) { "" }

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

    fun enableForChannel(guildId: Long, channelId: Long, commands: List<String>) {
        val placeholders = StringJoiner(", ")

        for (item in commands) {
            placeholders.add("?")
        }

        execute("DELETE FROM channel_disabled WHERE guildId = ? AND channelId = ? AND name IN ($placeholders)", guildId, channelId, *commands.toTypedArray())
    }
    //</editor-fold>

    fun getDonor(userId: Long) = getUser(userId).pledge
    fun setDonor(userId: Long, pledge: Double) = getUser(userId).apply { this.pledge = pledge }.save()

    fun isPremiumServer(guildId: Long) = findOne("SELECT 1 FROM guilds WHERE guildId = ? AND premiumRedeemer IS NOT NULL AND premiumRedeemer > 0", guildId) != null
    fun setPremiumServer(guildId: Long, redeemerId: Long?) = execute("UPDATE guilds SET premiumRedeemer = ? WHERE guildId = ?", redeemerId, guildId)
    fun getPremiumServers(redeemerId: Long) = find("SELECT guildId FROM guilds WHERE premiumRedeemer = ?", redeemerId).map { it.get<Long>("guildId") }

    // everything below here is crude and should probably be improved when the user stuff is *properly* done.
    fun getCanUserReceiveNudes(userId: Long) = getUser(userId).nudes
    fun setUserCanReceiveNudes(userId: Long, canReceive: Boolean) = getUser(userId).apply { nudes = canReceive }.save()

    fun getUserCockBlocked(userId: Long) = getUser(userId).cockblocked
    fun setUserCockBlocked(userId: Long, cockblocked: Boolean) = getUser(userId).apply { this.cockblocked = cockblocked }.save()

    fun getUserAnonymity(userId: Long) = getUser(userId).anonymity
    fun setUserAnonymity(userId: Long, anonymity: Boolean) = getUser(userId).apply { this.anonymity = anonymity }.save()

    /**
     * Execute a statement on the database. This method cannot be used to find anything,
     * but is more for setting data.
     */
    private fun execute(query: String, vararg parameters: Any?) {
        db.connection.use { conn ->
            buildPreparedStatement(conn, query, *parameters).use {
                it.executeUpdate()
            }
        }
    }

    private fun find(query: String, vararg parameters: Any): List<Row> {
        db.connection.use { conn ->
            buildPreparedStatement(conn, query, *parameters).use {
                it.executeQuery().use { result ->
                    val rows = mutableListOf<Row>()

                    while (result.next()) {
                        rows.add(buildRow(result))
                    }

                    return rows
                }
            }
        }
    }

    private fun findOne(query: String, vararg parameters: Any): Row? {
        db.connection.use { conn ->
            buildPreparedStatement(conn, query, *parameters).use {
                it.executeQuery().use { result ->
                    if (result.next()) {
                        return buildRow(result)
                    }

                    return null
                }
            }
        }
    }

    fun iterate(query: String, vararg parameters: Any?): Sequence<Row> {
        val connection = db.connection
        val statement = buildPreparedStatement(connection, query, *parameters)
        val result = statement.executeQuery()

        return generateSequence {
            try {
                when (result.next()) {
                    true -> buildRow(result)
                    else -> {
                        result.close()
                        statement.close()
                        connection.close()
                        null
                    }
                }
            } catch (t: Throwable) {
                result.close()
                statement.close()
                connection.close()
                null
            }
        }
    }

    private fun buildPreparedStatement(connection: Connection, query: String, vararg parameters: Any?): PreparedStatement {
        val statement = connection.prepareStatement(query)

        for (i in parameters.indices) {
            statement.setObject(i + 1, parameters[i])
        }

        return statement
    }

    private fun buildRow(result: ResultSet): Row {
        val data = mutableMapOf<String, Any?>()

        for (i in 1..result.metaData.columnCount) {
            data[result.metaData.getColumnName(i)] = result.getObject(i)
        }

        return Row(data)
    }

    private inline fun <reified T> deserialize(json: String): T = gson.fromJson(json, T::class.java)
    private fun serialize(entity: Any) = gson.toJson(entity)

    inner class Row(val dataDoNotAccessDirectly: Map<String, Any?>) {
        inline operator fun <reified T> get(column: String): T {
            return dataDoNotAccessDirectly[column]
                ?.let { it as? T ?: throw IllegalStateException("Database type does not match ${T::class.simpleName} (got type ${it::class.java.simpleName})") }
                ?: throw IllegalStateException("Key $column does not exist")
        }

        inline fun <reified T> getOrNull(column: String): T? {
            return dataDoNotAccessDirectly[column]?.let {
                it as? T ?: throw IllegalStateException("Database type does not match ${T::class.simpleName} (got type ${it::class.java.simpleName})")
            }
        }
    }
}
