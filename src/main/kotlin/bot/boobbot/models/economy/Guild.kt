package bot.boobbot.models.economy


data class Guild(
    val _id: String,
    var dropEnabled: Boolean,
    var blacklisted: Boolean,
    var ignoredChannels: MutableList<String>,
    var modMute: MutableList<String>
) {
    companion object {
        fun new(guildId: String): Guild {
            return Guild(guildId,
                false,
                false,
                mutableListOf(),
                mutableListOf()
            )
        }
    }
}
