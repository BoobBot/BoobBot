package bot.boobbot.entities.framework.annotations

@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.FUNCTION)
annotation class SubCommand(
    val aliases: Array<String> = [],
    val description: String = "*No description available.*",
    val donorOnly: Boolean = false
)
