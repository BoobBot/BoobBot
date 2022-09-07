package bot.boobbot.entities.framework.annotations

@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.CLASS, AnnotationTarget.FUNCTION)
annotation class Choice(
    val name: String,
    val value: String
)
