package bot.boobbot.entities.framework.annotations

import net.dv8tion.jda.api.interactions.commands.OptionType

@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.CLASS, AnnotationTarget.FUNCTION)
//@Repeatable  Cannot have this as it causes issues: https://youtrack.jetbrains.com/issue/KT-53279/Reflection-KotlinReflectionInternalError-Method-is-not-supported-caused-by-Repeatable-annotation-deserialization-at-runtime-if
annotation class Option(
    val name: String,
    val description: String,
    val required: Boolean = true,
    val type: OptionType = OptionType.STRING,
    val choices: Array<Choice> = []
)
