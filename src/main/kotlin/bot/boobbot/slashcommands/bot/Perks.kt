package bot.boobbot.slashcommands.bot

import bot.boobbot.BoobBot
import bot.boobbot.entities.framework.*
import bot.boobbot.entities.misc.DSLMessageBuilder
import bot.boobbot.entities.misc.DonorType
import bot.boobbot.utils.Colors
import net.dv8tion.jda.api.EmbedBuilder
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent
import net.dv8tion.jda.api.events.interaction.component.GenericComponentInteractionCreateEvent
import net.dv8tion.jda.api.events.interaction.component.SelectMenuInteractionEvent
import net.dv8tion.jda.api.interactions.components.buttons.ButtonStyle

@CommandProperties(description = "Receive your rewards after subscribing on Patreon.")
class Perks : SlashCommand {
    companion object {
        private const val PREMIUM_SERVERS = 3
    }

    fun awaitNonConcurrentButton(
        uniqueId: String,
        predicate: (ButtonInteractionEvent) -> Boolean,
        timeout: Long,
        cb: (ButtonInteractionEvent?) -> Unit
    ): Boolean {
        return BoobBot.waiter.waitForButton(uniqueId, predicate, timeout, cb)
    }

    fun awaitNonConcurrentMenu(
        uniqueId: String,
        predicate: (GenericComponentInteractionCreateEvent) -> Boolean,
        timeout: Long,
        cb: (GenericComponentInteractionCreateEvent?) -> Unit
    ): Boolean {
        return BoobBot.waiter.waitForMenu(uniqueId, predicate, timeout, cb)
    }

    override fun execute(event: SlashCommandInteractionEvent) {
        when (event.subcommandName) {
            "link" -> link(event)
            "add" -> add(event)
            "remove" -> remove(event)
            "list" -> list(event)
            else -> event.reply("Unknown subcommand.").queue()
        }
    }

    fun link(event: SlashCommandInteractionEvent) {
        event.reply("Searching for your subscription. This could take up to 30 seconds.").queue()

        BoobBot.pApi.fetchPledgesOfCampaign("1928035").thenAccept {
            if (it.isEmpty()) {
                return@thenAccept event.hook.editOriginal("Patreon API returned an invalid response. Report this to https://discord.gg/bra.")
                    .queue()
            }

            val pledge = it.firstOrNull { u -> u.discordId != null && u.discordId == event.user.idLong }
                ?: return@thenAccept event.hook.editOriginal("Unable to find your subscription. Make sure your Discord account is linked to your Patreon account, whore.")
                    .queue()

            if (pledge.isDeclined) {
                return@thenAccept event.hook.editOriginal("Your payment appears to have been declined. Fix it before attempting to claim your perks, whore.")
                    .queue()
            }

            val pledgeAmount = pledge.pledgeCents.toDouble() / 100
            val pledgeFriendly = String.format("%1$,.2f", pledgeAmount)
            event.hook.editOriginal("Welcome to the club, whore. Your tier: `${BoobBot.pApi.getDonorType(pledgeAmount)}` ($$pledgeFriendly)").queue()
            BoobBot.database.setDonor(event.user.id, pledgeAmount)
        }
    }


    fun add(event: SlashCommandInteractionEvent) {
        if (!event.isFromGuild) {
            return event.reply("Run this command in a guild, whore.").queue()
        }

        val guildId = event.guild!!.id

        when {
            BoobBot.database.isPremiumServer(guildId) -> event.reply("This server is already premium, whore.").queue()
            BoobBot.pApi.getDonorType(event.user.id) < DonorType.SERVER_OWNER -> event.reply("You need to be subscribed to the Server Owner tier, whore.\nJoin here: <https://www.patreon.com/join/OfficialBoobBot/checkout?rid=3186958>").queue()
            event.member!!.isOwner -> event.reply("You own this server, whore, so it's already premium.").queue()
            BoobBot.database.getPremiumServers(event.user.idLong).size > PREMIUM_SERVERS -> event.reply("You've hit the maximum number of premium servers. Remove some or fuck off, whore.").queue()
            else -> {
                val predicate =
                    { e: ButtonInteractionEvent -> e.componentId == "ps:accept:${event.user.id}" || e.componentId == "ps:cancel:${event.user.id}" }
                val waiterSetup = awaitNonConcurrentButton("ps:${event.user.id}", predicate, 10000) {
                    if (it == null) {
                        return@awaitNonConcurrentButton event.reply("Fine, whore. The server won't be added.").queue()
                    }

                    if (it.componentId == "ps:cancel:${event.user.id}") {
                        return@awaitNonConcurrentButton it.editComponents().setContent("Fine, whore. The server won't be added.").queue()
                    }

                    BoobBot.database.setPremiumServer(event.guild!!.id, event.user.idLong)
                    it.editComponents().setContent("Server added, whore.").queue()
                }

                if (!waiterSetup) {
                    return event.reply("wtf? go cancel your other interaction first, whore.").queue()
                }
                event.deferReply().queue()
                val m = DSLMessageBuilder().apply {
                    content("Hey whore, are you *really* sure you want to add **${event.guild!!.name}** to your premium servers?")
                    row {
                        button(ButtonStyle.SUCCESS, "ps:accept:${event.user.id}", "Add Server")
                        button(ButtonStyle.DANGER, "ps:cancel:${event.user.id}", "Cancel")
                    }
                }.build()
                event.hook.sendMessage(m).queue()
            }
        }
    }


    fun remove(event: SlashCommandInteractionEvent) {
        val servers = BoobBot.database.getPremiumServers(event.user.idLong)

        if (servers.isEmpty()) {
            return event.reply("You don't have any premium servers, whore.").queue()
        }

        val guilds = servers.map { (BoobBot.shardManager.getGuildById(it._id)?.name ?: "Inaccessible Server") to it._id }

        val predicate = { e: GenericComponentInteractionCreateEvent -> e.componentId == "menu:ps:${event.user.id}" || e.componentId == "ps:cancel:${event.user.id}" }
        val waiterSetup = awaitNonConcurrentMenu("ps:${event.user.id}", predicate, 15000) {
            if (it == null) {
                return@awaitNonConcurrentMenu event.reply("Fine, whore. No servers will be removed.").queue()
            }

            if (it is ButtonInteractionEvent && it.componentId == "ps:cancel:${event.user.id}") {
                return@awaitNonConcurrentMenu it.editComponents().setContent("Fine, whore. No servers will be removed.").queue()
            }

            val selected = (it as SelectMenuInteractionEvent).selectedOptions[0]
            BoobBot.database.removePremiumServer(selected.value)
            it.editComponents().setContent("Removed **${selected.label}**, whore.").queue()
        }

        if (!waiterSetup) {
            return event.reply("wtf? go cancel your other interaction first, whore.").queue()
        }

        val m = DSLMessageBuilder().apply {
            content("Select the server you want to remove from the list below.\nThis prompt will time out in 15 seconds.")
            row {
                menu("menu:ps:${event.user.id}") {
                    for ((name, id) in guilds) {
                        addOption(name, id)
                    }
                }
            }
            row {
                button(ButtonStyle.DANGER, "ps:cancel:${event.user.id}", "Cancel")
            }
        }.build()

        event.deferReply().queue()
        event.hook.sendMessage(m).queue()
    }

    fun list(event: SlashCommandInteractionEvent) {
        val servers = BoobBot.database.getPremiumServers(event.user.idLong)

        if (servers.isEmpty()) {
            return event.reply("You don't have any premium servers, whore.").queue()
        }

        val guilds = servers.joinToString("`\n`", prefix = "`", postfix = "`") {
            BoobBot.shardManager.getGuildById(it._id)?.name ?: "Inaccessible Server"
        }

        event.replyEmbeds(
            EmbedBuilder().apply {
                setColor(Colors.rndColor)
                setTitle("Your Premium Servers")
                setDescription(
                    """
                All your additional premium servers are listed below.
                Any servers you own are already premium without counting towards your limit.
                
                $guilds
            """.trimIndent()
                )
            }.build()
        ).queue()
    }
}
