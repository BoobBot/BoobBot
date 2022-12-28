package bot.boobbot.entities.misc

import net.dv8tion.jda.api.EmbedBuilder
import net.dv8tion.jda.api.entities.MessageEmbed
import net.dv8tion.jda.api.interactions.components.ActionRow
import net.dv8tion.jda.api.interactions.components.ItemComponent
import net.dv8tion.jda.api.interactions.components.buttons.Button
import net.dv8tion.jda.api.interactions.components.buttons.ButtonStyle
import net.dv8tion.jda.api.interactions.components.selections.SelectMenu
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

        fun button(style: ButtonStyle, idOrUrl: String, label: String) {
            components.add(Button.of(style, idOrUrl, label))
        }

        fun build(): ActionRow {
            return ActionRow.of(components)
        }
    }
}
