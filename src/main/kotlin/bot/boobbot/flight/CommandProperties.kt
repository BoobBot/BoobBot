package bot.boobbot.flight

@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.CLASS)
annotation class CommandProperties(
        val aliases: Array<String> = [],
        val description: String = "No description available",
        val category: Category = Category.MISC,
        val developerOnly: Boolean = false,
        val donorOnly: Boolean = false,
        val nsfw: Boolean = false,
        val enabled: Boolean = true,
        val guildOnly: Boolean = false
)


enum class Category(val title: String) {
    GENERAL("<:TouchMaBooty:444601938320031745> General NSFW"),
    KINKS("<:whip:440551663804350495> Kinks"),
    VIDEOSEARCHING("\uD83D\uDCF9 Video Searching"),
    FANTASY("<:Pantsu:443870754107555842> Non-Real"),
    HOLIDAY("\uD83C\uDF85 Holiday"),
    SEND("\uD83D\uDCE7 Send Commands"),
    FUN("\u2728 Fun Commands"),
    AUDIO("\uD83D\uDD08 Audio Commands"),
    MISC("<:info:486945488080338944> Misc Commands")
}
