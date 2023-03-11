package bot.boobbot.entities.games;

abstract class BaseRom(
    val GuildId: Long = 0,
    var ChannelID: Long = 0,
    var MessageID: Long = 0,
    var Running: Boolean = false,
    var Finshed: Boolean = false,
) {

    open fun Setup() {
    }

    fun Shutdown() {

    }

    fun Tick() {

    }


}
