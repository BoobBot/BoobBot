package bot.boobbot.flight

enum class Category(val title: String, val description: String, val nsfw: Boolean) {
    GENERAL("<:Ass:520247323343978509> General NSFW", "Vanilla content that's sure to please.", true),
    KINKS("<:whip:440551663804350495> Kinks", "BDSM, Tentacles, Pegging, you'll find it here!", true),
    VIDEOSEARCHING("\uD83D\uDCF9 Video Searching", "Search homework websites for your favourite videos.", true),
    FANTASY("<:Pantsu:443870754107555842> Non-Real", "Cat-girls when?", true),
    HOLIDAY("\uD83C\uDF85 Holiday", "Holiday-themed content.", true),
    SEND("\uD83D\uDCE7 Send Commands", "Share porn with other users.", false),
    FUN("\u2728 Fun Commands", "Entertainment! (Who am I kidding, we have porn...)", false),
    AUDIO("\uD83D\uDD08 Audio Commands", "Music, straight from PornHub, RedTube etc.", false),
    DEV("\u2699\ufe0f Dev shit", "Debug commands for developers only.", false),
    MOD("\uD83D\uDD28 Moderator Commands", "Commands to remove shit users from your server", false),
    MISC("<:info:486945488080338944> Misc Commands", "Anything not covered by the other categories.", false),
    MEME("<:meme:539601224966864897> Meme", "Image generation with a memey twist.", false)
}
