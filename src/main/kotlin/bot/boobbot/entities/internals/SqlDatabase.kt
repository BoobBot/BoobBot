package bot.boobbot.entities.internals

import bot.boobbot.entities.db.WebhookConfiguration
import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import java.util.concurrent.TimeUnit

class SqlDatabase(host: String, port: String, databaseName: String, user: String, auth: String) {
    private val db: HikariDataSource

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
        // TODO
        // webhooks table should have channelId primary key (UNIQUE(channelId, guildId)) to allow for multiple webhook configs per guild.
        // actually need to decide whether channelId should be primary key. Users could set up multiple categories for a single channel.
        // should definitely have two indexes, one for channel ID, one for guild ID. Don't think category needs one
    }

    fun getWebhooks(guildId: String): List<WebhookConfiguration> {
        val rows = find("SELECT category, channelId, webhook FROM webhooks WHERE guild_id = ?", guildId)

        return rows.map {
            WebhookConfiguration(it["category"], it["channelId"], it["webhook"])
        }
    }

    fun setWebhook(guildId: String, webhookUrl: String, category: String, channelId: String) {
        execute("INSERT INTO webhooks VALUES (?, ?, ?, ?)", channelId, guildId, category, webhookUrl)
    }

    fun deleteWebhook(guildId: String, channelId: String, category: String) {
        execute("DELETE FROM webhooks WHERE channelId = ? AND guild_id = ? AND category = ?", channelId, guildId, category)
    }

    fun deleteWebhooks(guildId: String) {
        execute("DELETE FROM webhooks WHERE guild_id = ?", guildId)
    }

    // TODO: user (& premium user stuff, & user toggles (nudes, cockblock & anonymity)

    // TODO: guild (without prefix stuff, we don't have message intent so useless now)

    // TODO: disableable commands

    // TODO custom commands

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
