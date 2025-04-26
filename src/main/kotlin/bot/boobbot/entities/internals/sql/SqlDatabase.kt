package bot.boobbot.entities.internals.sql

import bot.boobbot.entities.db.Guild
import bot.boobbot.entities.db.User
import bot.boobbot.entities.db.WebhookConfiguration
import com.google.gson.Gson
import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import org.bson.Document
import org.json.JSONObject
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
                "channelId BIGINT NOT NULL," +
                "guildId BIGINT NOT NULL," +
                "category VARCHAR(32) NOT NULL," +
                "webhook VARCHAR(256) NOT NULL," +
                "INDEX channelId(channelId), INDEX guildId(guildId));")

        execute("CREATE TABLE IF NOT EXISTS guilds(" +
                "guildId BIGINT NOT NULL PRIMARY KEY," +
                "json LONGTEXT NOT NULL);") // don't want to deal with converting this to proper SQL structure for now.

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
        return find("SELECT category, channelId, webhook FROM webhooks WHERE guildId = ?", guildId)
            .map { WebhookConfiguration(it["category"], it["channelId"], it["webhook"]) }
    }

    fun setWebhook(guildId: String, webhookUrl: String, category: String, channelId: String) {
        execute("INSERT INTO webhooks VALUES (?, ?, ?, ?)", channelId, guildId, category, webhookUrl)
    }

    fun deleteWebhook(guildId: String, channelId: String, category: String) {
        execute("DELETE FROM webhooks WHERE channelId = ? AND guildId = ? AND category = ?", channelId, guildId, category)
    }

    fun deleteWebhooks(guildId: String) {
        execute("DELETE FROM webhooks WHERE guildId = ?", guildId)
    }

    fun getGuild(guildId: String): Guild {
        val json = findOne("SELECT json FROM guilds WHERE guildId = ?", guildId)
            ?.get<String>("json")
            ?: return Guild(guildId)

        return Guild.fromJson(JSONObject(json))
    }

    // TODO: reset of guilds

    fun getUser(userId: String): User {
        val json = findOne("SELECT json FROM users WHERE userId = ?", userId)
            ?.get<String>("json")
            ?: return User(userId)

        return deserialize<User>(json)
    }

    // TODO: rest of users

    fun getCustomCommands(guildId: String): Map<String, String> {
        return find("SELECT name, content FROM custom_commands WHERE guildId = ?", guildId)
            .associate { it.get<String>("name") to it["content"] }
    }

    fun setCustomCommand(guildId: String, name: String, content: String) {
        if (getCustomCommands(guildId).containsKey(name)) {
            execute("UPDATE custom_commands SET content = ? WHERE guildId = ?", content, guildId)
        } else {
            execute("INSERT INTO custom_commands(guildId, name, content) VALUES (?, ?, ?)", guildId, name, content)
        }
    }

    fun deleteCustomCommand(guildId: String, name: String) {
        execute("DELETE FROM custom_commands WHERE guildId = ? AND name = ?", guildId, name)
    }

    fun deleteCustomCommands(guildId: String) {
        execute("DELETE FROM custom_commands WHERE guildId = ?", guildId)
    }

    /**
     * Execute a statement on the database. This method cannot be used to find anything,
     * but is more for setting data.
     */
    private fun execute(query: String, vararg parameters: Any) {
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
