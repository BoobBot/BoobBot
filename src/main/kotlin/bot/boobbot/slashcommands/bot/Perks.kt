package bot.boobbot.slashcommands.bot

import bot.boobbot.BoobBot
import bot.boobbot.entities.framework.annotations.CommandProperties
import bot.boobbot.entities.framework.SlashCommand
import bot.boobbot.entities.framework.SlashContext
import bot.boobbot.entities.misc.DonorType
import bot.boobbot.utils.Colors
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

    override fun execute(ctx: SlashContext) {
        when (ctx.subcommandName) {
            "link" -> link(ctx)
            "add" -> add(ctx)
            "remove" -> remove(ctx)
            "list" -> list(ctx)
            else -> ctx.reply("Unknown subcommand.")
        }
    }

    fun link(ctx: SlashContext) {
        ctx.event.reply("Searching for your subscription. This could take up to 30 seconds.").queue()

        BoobBot.pApi.fetchPledgesOfCampaign("1928035").thenAccept {
            if (it.isEmpty()) {
                return@thenAccept ctx.event.hook.editOriginal("Patreon API returned an invalid response. Report this to https://discord.gg/bra.")
                    .queue()
            }

            val pledge = it.firstOrNull { u -> u.discordId != null && u.discordId == ctx.user.idLong }
                ?: return@thenAccept ctx.event.hook.editOriginal("Unable to find your subscription. Make sure your Discord account is linked to your Patreon account, whore.")
                    .queue()

            if (pledge.isDeclined) {
                return@thenAccept ctx.event.hook.editOriginal("Your payment appears to have been declined. Fix it before attempting to claim your perks, whore.")
                    .queue()
            }

            val pledgeAmount = pledge.pledgeCents.toDouble() / 100
            val pledgeFriendly = String.format("%1$,.2f", pledgeAmount)
            BoobBot.database.setDonor(ctx.user.id, pledgeAmount)
            ctx.event.hook.editOriginal("Welcome to the club, whore. Your tier: `${BoobBot.pApi.getDonorType(pledgeAmount)}` ($$pledgeFriendly)").queue()
        }
    }


    fun add(ctx: SlashContext) {
        if (!ctx.event.isFromGuild) {
            return ctx.reply("Run this command in a guild, whore.")
        }

        val guildId = ctx.guild!!.id

        when {
            BoobBot.database.isPremiumServer(guildId) -> ctx.reply("This server is already premium, whore.")
            BoobBot.pApi.getDonorType(ctx.user.id) < DonorType.SERVER_OWNER -> ctx.reply("You need to be subscribed to the Server Owner tier, whore.\nJoin here: <https://www.patreon.com/join/OfficialBoobBot/checkout?rid=3186958>")
            ctx.member!!.isOwner -> ctx.reply("You own this server, whore, so it's already premium.")
            BoobBot.database.getPremiumServers(ctx.user.idLong).size > PREMIUM_SERVERS -> ctx.reply("You've hit the maximum number of premium servers. Remove some or fuck off, whore.")
            else -> {
                val predicate =
                    { e: ButtonInteractionEvent -> e.componentId == "ps:accept:${ctx.user.id}" || e.componentId == "ps:cancel:${ctx.user.id}" }
                val waiterSetup = awaitNonConcurrentButton("ps:${ctx.user.id}", predicate, 10000) {
                    if (it == null) {
                        return@awaitNonConcurrentButton ctx.reply("Fine, whore. The server won't be added.")
                    }

                    if (it.componentId == "ps:cancel:${ctx.user.id}") {
                        return@awaitNonConcurrentButton it.editComponents().setContent("Fine, whore. The server won't be added.").queue()
                    }

                    BoobBot.database.setPremiumServer(ctx.guild.id, ctx.user.idLong)
                    it.editComponents().setContent("Server added, whore.").queue()
                }

                if (!waiterSetup) {
                    return ctx.reply("wtf? go cancel your other interaction first, whore.")
                }

                ctx.message {
                    content("Hey whore, are you *really* sure you want to add **${ctx.guild.name}** to your premium servers?")
                    row {
                        button(ButtonStyle.SUCCESS, "ps:accept:${ctx.user.id}", "Add Server")
                        button(ButtonStyle.DANGER, "ps:cancel:${ctx.user.id}", "Cancel")
                    }
                }
            }
        }
    }


    fun remove(ctx: SlashContext) {
        val servers = BoobBot.database.getPremiumServers(ctx.user.idLong)

        if (servers.isEmpty()) {
            return ctx.reply("You don't have any premium servers, whore.")
        }

        val guilds = servers.map { (BoobBot.shardManager.getGuildById(it._id)?.name ?: "Inaccessible Server") to it._id }

        val predicate = { e: GenericComponentInteractionCreateEvent -> e.componentId == "menu:ps:${ctx.user.id}" || e.componentId == "ps:cancel:${ctx.user.id}" }
        val waiterSetup = awaitNonConcurrentMenu("ps:${ctx.user.id}", predicate, 15000) {
            if (it == null) {
                return@awaitNonConcurrentMenu ctx.reply("Fine, whore. No servers will be removed.")
            }

            if (it is ButtonInteractionEvent && it.componentId == "ps:cancel:${ctx.user.id}") {
                return@awaitNonConcurrentMenu it.editComponents().setContent("Fine, whore. No servers will be removed.").queue()
            }

            val selected = (it as SelectMenuInteractionEvent).selectedOptions[0]
            BoobBot.database.removePremiumServer(selected.value)
            it.editComponents().setContent("Removed **${selected.label}**, whore.").queue()
        }

        if (!waiterSetup) {
            return ctx.reply("wtf? go cancel your other interaction first, whore.")
        }

        ctx.message {
            content("Select the server you want to remove from the list below.\nThis prompt will time out in 15 seconds.")
            row {
                menu("menu:ps:${ctx.user.id}") {
                    for ((name, id) in guilds) {
                        addOption(name, id)
                    }
                }
            }
            row {
                button(ButtonStyle.DANGER, "ps:cancel:${ctx.user.id}", "Cancel")
            }
        }
    }

    fun list(ctx: SlashContext) {
        val servers = BoobBot.database.getPremiumServers(ctx.user.idLong)

        if (servers.isEmpty()) {
            return ctx.reply("You don't have any premium servers, whore.")
        }

        val guilds = servers.joinToString("`\n`", prefix = "`", postfix = "`") {
            BoobBot.shardManager.getGuildById(it._id)?.name ?: "Inaccessible Server"
        }

        ctx.reply {
            setColor(Colors.rndColor)
            setTitle("Your Premium Servers")
            setDescription(
                """
                All your additional premium servers are listed below.
                Any servers you own are already premium without counting towards your limit.
                
                $guilds
                """.trimIndent()
            )
        }
    }
}
