package bot.boobbot.entities.framework

enum class Category(val title: String, val description: String, val nsfw: Boolean) {
    AUDIO("\uD83D\uDD08 Audio Commands", "Music and more in your voice-channel.", false),
    DEV("\u2699\ufe0f Dev shit", "Debug commands for developers only.", false),
    ECONOMY(":moneybag: Economy", " Economy and games", false),
    FANTASY("<:Pantsu:443870754107555842> Non-Real", "Futa, nekos, yiff etc.", true),
    FUN("\u2728 Fun Commands", "Random fun commands.", false),
    INTERACTIONS("Interactions", "Interact with someone.", false),
    GENERAL("<:Ass:520247323343978509> General NSFW", "Vanilla content.", true),
    HOLIDAY("\uD83C\uDF85 Holiday", "Holiday-themed content.", true),
    KINKS("<:whip:440551663804350495> Kinks", "BDSM, Tentacles, Pegging, and more.", true),
    MEME("<:meme:539601224966864897> Meme", "Image generation with a memey twist.", false),
    MISC("<:info:486945488080338944> Misc Commands", "Anything not covered by the other categories.", false),
    MOD("\uD83D\uDD28 Moderator Commands", "Commands to remove shit users from your server", false),
    SEND("\uD83D\uDCE7 Send Commands", "Share porn with other users.", false),
    VIDEOSEARCHING("\uD83D\uDCF9 Video Searching", "🌽", true)
}
