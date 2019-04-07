package bot.boobbot.flight

@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.FUNCTION)
annotation class SubCommand(
    val async: Boolean = false,
    val aliases: Array<String> = []
)
