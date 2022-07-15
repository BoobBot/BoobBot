package bot.boobbot.entities.misc

import net.dv8tion.jda.api.EmbedBuilder
import net.dv8tion.jda.api.MessageBuilder
import net.dv8tion.jda.api.entities.Message
import net.dv8tion.jda.api.entities.MessageEmbed
import net.dv8tion.jda.api.interactions.components.ActionRow
import net.dv8tion.jda.api.interactions.components.ItemComponent
import net.dv8tion.jda.api.interactions.components.buttons.Button
import net.dv8tion.jda.api.interactions.components.buttons.ButtonStyle
import net.dv8tion.jda.api.interactions.components.selections.SelectMenu

class DSLMessageBuilder {
    private val builder = MessageBuilder()
    private val embeds = mutableListOf<MessageEmbed>()
    private val actionRows = mutableListOf<ActionRow>()

    fun content(s: String) {
        builder.setContent(s)
    }

    fun embed(e: EmbedBuilder.() -> Unit) {
        embeds.add(EmbedBuilder().apply(e).build())
    }

    fun row(ar: ActionRowBuilder.() -> Unit) {
        actionRows.add(ActionRowBuilder().apply(ar).build())
    }

    fun build(): Message {
        return builder.setEmbeds(embeds).setActionRows(actionRows).build()
    }

    inner class ActionRowBuilder {
        private val components = mutableListOf<ItemComponent>()

        fun menu(id: String, sm: SelectMenu.Builder.() -> Unit) {
            components.add(SelectMenu.create(id).apply(sm).build())
        }

        fun button(style: ButtonStyle, idOrUrl: String, label: String) {
            components.add(Button.of(style, idOrUrl, label))
        }

        fun build(): ActionRow {
            return ActionRow.of(components)
        }
    }
}
