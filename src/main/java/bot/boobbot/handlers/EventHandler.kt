package bot.boobbot.handlers

import bot.boobbot.BoobBot
import bot.boobbot.misc.Constants
import net.dv8tion.jda.core.events.DisconnectEvent
import net.dv8tion.jda.core.events.ReadyEvent
import net.dv8tion.jda.core.events.ReconnectedEvent
import net.dv8tion.jda.core.events.ResumedEvent
import net.dv8tion.jda.core.events.guild.GuildJoinEvent
import net.dv8tion.jda.core.events.guild.GuildLeaveEvent
import net.dv8tion.jda.core.hooks.ListenerAdapter
import net.dv8tion.jda.webhook.WebhookClientBuilder


class EventHandler : ListenerAdapter() {


    override fun onReady(event: ReadyEvent?) {
        val readyClient = WebhookClientBuilder(Constants.RDY_WEBHOOK).build()
        BoobBot.log.info("ready?")
        readyClient.send("ready?")
        readyClient.close()
    }

    override fun onReconnect(event: ReconnectedEvent?) {
        super.onReconnect(event)
    }

    override fun onResume(event: ResumedEvent?) {
        super.onResume(event)
    }

    override fun onDisconnect(event: DisconnectEvent?) {
        super.onDisconnect(event)
    }

    override fun onGuildJoin(event: GuildJoinEvent?) {
        BoobBot.log.info("Joined ${event?.guild?.name}")
    }

    override fun onGuildLeave(event: GuildLeaveEvent?) {
        BoobBot.log.info("left ${event?.guild?.name}")
    }
}
