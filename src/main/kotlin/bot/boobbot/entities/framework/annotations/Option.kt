package bot.boobbot.entities.framework.annotations

import net.dv8tion.jda.api.interactions.commands.OptionType

@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.CLASS, AnnotationTarget.FUNCTION)
@Repeatable
annotation class Option(
    val name: String,
    val description: String,
    val required: Boolean = true,
    val type: OptionType = OptionType.STRING,
    val choices: Array<Choice> = []
)
