package bot.boobbot.entities.framework.annotations

import bot.boobbot.entities.framework.Category
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
    val botPermissions: Array<Permission> = [],

    // Whether this should be indexed as a slash command.
    val slashEnabled: Boolean = true,
    // Whether this command should be grouped by its parent package name.
    val groupByCategory: Boolean = false
)
