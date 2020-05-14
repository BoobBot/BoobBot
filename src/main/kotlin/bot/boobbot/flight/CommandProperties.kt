package bot.boobbot.flight

import net.dv8tion.jda.api.Permission

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
    val guildOnly: Boolean = false,
    val hidden: Boolean = false,
    val userPermissions: Array<Permission> = [],
    val botPermissions: Array<Permission> = []
)

