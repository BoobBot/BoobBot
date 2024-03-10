package bot.boobbot.contextual.component

import bot.boobbot.entities.misc.DSLMessageBuilder
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import net.dv8tion.jda.api.events.GenericEvent
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent
import net.dv8tion.jda.api.hooks.EventListener

abstract class BaseButtonHandler(private val componentId: String) : EventListener {
    final override fun onEvent(event: GenericEvent) {
        if (event is ButtonInteractionEvent && event.componentId.startsWith(componentId)) {
            IoScope.launch {
                onButtonInteraction(event)
            }
        }
    }

    abstract suspend fun onButtonInteraction(event: ButtonInteractionEvent)

    fun ButtonInteractionEvent.message(ephemeral: Boolean = false, message: DSLMessageBuilder.() -> Unit) {
        reply(DSLMessageBuilder().apply(message).build()).setEphemeral(ephemeral).queue()
    }

    companion object {
        private val IoScope = CoroutineScope(Dispatchers.IO)
    }
}
