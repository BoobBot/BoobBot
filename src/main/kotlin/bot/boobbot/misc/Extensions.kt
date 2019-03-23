package bot.boobbot.misc

import com.mewna.catnip.entity.channel.GuildChannel
import com.mewna.catnip.entity.channel.MessageChannel
import com.mewna.catnip.entity.util.Permission
import io.github.cdimascio.dotenv.Dotenv
import java.util.concurrent.CompletionStage

fun Dotenv.get(key: String, default: String): String = get(key) ?: default

fun <T> CompletionStage<T>.thenException(handler: ((Throwable) -> Unit)?): CompletionStage<T> {
    exceptionally {
        if (handler != null) {
            handler(it)
        }
        return@exceptionally null
    }

    return this
}

fun GuildChannel.canTalk(): Boolean {
    val guild = this.catnip().cache().guild(guildIdAsLong()) ?: return false
    return guild.selfMember().hasPermissions(this, Permission.VIEW_CHANNEL, Permission.SEND_MESSAGES)
}