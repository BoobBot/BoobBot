package bot.boobbot.entities.misc

import bot.boobbot.BoobBot
import bot.boobbot.entities.framework.Context
import net.dv8tion.jda.api.EmbedBuilder
import net.dv8tion.jda.api.entities.MessageEmbed
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent
import net.dv8tion.jda.api.interactions.components.ActionRow
import net.dv8tion.jda.api.interactions.components.ItemComponent
import net.dv8tion.jda.api.interactions.components.buttons.Button
import net.dv8tion.jda.api.interactions.components.buttons.ButtonStyle
import net.dv8tion.jda.api.interactions.components.selections.StringSelectMenu
import net.dv8tion.jda.api.utils.FileUpload
import net.dv8tion.jda.api.utils.messages.MessageCreateBuilder
import net.dv8tion.jda.api.utils.messages.MessageCreateData

class DSLMessageBuilder {
    private val builder = MessageCreateBuilder()
    private val embeds = mutableListOf<MessageEmbed>()
    private val actionRows = mutableListOf<ActionRow>()

    fun content(s: String) {
        builder.setContent(s)
    }

    fun embed(e: EmbedBuilder.() -> Unit) {
        embeds.add(EmbedBuilder().apply(e).build())
    }

    fun embed(e: MessageEmbed) {
        embeds.add(e)
    }

    fun file(f: FileUpload) {
        builder.addFiles(f)
    }

    fun files(fs: List<FileUpload>) {
        builder.addFiles(fs)
    }

    fun row(ar: ActionRowBuilder.() -> Unit) {
        actionRows.add(ActionRowBuilder().apply(ar).build())
    }

    fun build(): MessageCreateData {
        return builder.setEmbeds(embeds).setComponents(actionRows).build()
    }

    inner class ActionRowBuilder {
        private val components = mutableListOf<ItemComponent>()

        fun menu(id: String, sm: StringSelectMenu.Builder.() -> Unit) {
            components.add(StringSelectMenu.create(id).apply(sm).build())
        }

        fun button(style: ButtonStyle, idOrUrl: String, label: String, builder: ButtonBuilder.() -> Unit = {}) {
            components.add(Button.of(style, idOrUrl, label))
            builder(ButtonBuilder(idOrUrl))
        }

        fun build(): ActionRow {
            return ActionRow.of(components)
        }
    }

    inner class ButtonBuilder(private val id: String) {
        fun clicked(timeout: Long,
                    predicate: (ButtonInteractionEvent) -> Boolean = { it.componentId == id },
                    event: (ButtonInteractionEvent?) -> Unit) {
            BoobBot.waiter.waitForButton(id, predicate, timeout, event)
        }
    }
}
