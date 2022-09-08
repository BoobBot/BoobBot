package bot.boobbot.entities.framework.annotations

@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.CLASS, AnnotationTarget.FUNCTION)
annotation class Options(
    val value: Array<Option> = []
)
