package bot.boobbot.handlers

import bot.boobbot.contextual.component.MoreButtonHandler
import io.sentry.Sentry
import net.dv8tion.jda.api.events.GenericEvent
import net.dv8tion.jda.api.events.interaction.component.GenericComponentInteractionCreateEvent
import net.dv8tion.jda.api.hooks.EventListener

class ComponentHandler : EventListener {
    private val handlers = listOf(MoreButtonHandler())

    override fun onEvent(event: GenericEvent) {
        if (event !is GenericComponentInteractionCreateEvent) {
            return
        }

        for (handler in handlers) {
            try {
                handler.onEvent(event)
            } catch (t: Throwable) {
                Sentry.capture(t)
            }
        }
    }
}
