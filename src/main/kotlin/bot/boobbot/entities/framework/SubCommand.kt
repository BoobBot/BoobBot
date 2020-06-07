package bot.boobbot.entities.framework

@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.FUNCTION)
annotation class SubCommand(
    val async: Boolean = false,
    val aliases: Array<String> = [],
    val description: String = "*No description available.*",
    val donorOnly: Boolean = false
)
