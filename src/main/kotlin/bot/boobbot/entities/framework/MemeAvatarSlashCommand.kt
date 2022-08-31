package bot.boobbot.entities.framework

import bot.boobbot.BoobBot
import bot.boobbot.entities.internals.Config
import bot.boobbot.utils.Formats
import net.dv8tion.jda.api.Permission
import net.dv8tion.jda.api.entities.GuildChannel
import net.dv8tion.jda.api.entities.Member
import net.dv8tion.jda.api.entities.User
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent
import net.dv8tion.jda.api.utils.FileUpload
import okhttp3.Headers
import okhttp3.Headers.Companion.headersOf
import okhttp3.HttpUrl
import okhttp3.HttpUrl.Companion.toHttpUrlOrNull

abstract class MemeAvatarSlashCommand(private val category: String) : AsyncSlashCommand {

    private val endpointUrl = "https://memes.subspace.gg/api/$category"
    private val urlBuilder
        get() = endpointUrl.toHttpUrlOrNull()!!.newBuilder()

    private val headers = headersOf("Authorization", BoobBot.config.MEMER_IMGEN_KEY)

    override suspend fun executeAsync(event: SlashCommandInteractionEvent) {
        fun permissionCheck(u: User, m: Member?, channel: GuildChannel, vararg permissions: Permission): Boolean {
            return !event.isFromGuild || Config.OWNERS.contains(u.idLong) || m?.hasPermission(channel, *permissions) == true
        }

        fun botCan(vararg check: Permission) = permissionCheck(event.jda.selfUser, event.guild?.selfMember, event.guildChannel, *check)

        if (!botCan(Permission.MESSAGE_SEND)) {
            return
        }

        if (!botCan(Permission.MESSAGE_ATTACH_FILES)) {
            return event.reply(Formats.error("I can't send images here, fix it whore.")).queue()
        }

        val user = event.getOption("member")?.asUser ?: event.user
        val url = urlBuilder.addQueryParameter("avatar1", user.effectiveAvatarUrl).build()

        val res = BoobBot.requestUtil.get(url.toString(), headers).await()?.body
            ?: return event.reply(Formats.error("rip some error press f")).queue()

       event.replyFiles(FileUpload.fromData(res.byteStream(), "$category.png")).queue()
    }

}
