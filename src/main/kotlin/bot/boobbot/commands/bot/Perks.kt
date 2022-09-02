package bot.boobbot.commands.bot

import bot.boobbot.BoobBot
import bot.boobbot.entities.framework.Command
import bot.boobbot.entities.framework.CommandProperties
import bot.boobbot.entities.framework.Context
import bot.boobbot.entities.framework.SubCommand
import bot.boobbot.entities.misc.DonorType
import bot.boobbot.utils.Colors
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent
import net.dv8tion.jda.api.events.interaction.component.GenericComponentInteractionCreateEvent
import net.dv8tion.jda.api.events.interaction.component.SelectMenuInteractionEvent
import net.dv8tion.jda.api.interactions.components.buttons.ButtonStyle

@CommandProperties(description = "Receive your rewards after subscribing on Patreon.")
class Perks : Command {
    companion object {
        private const val PREMIUM_SERVERS = 3
    }

    override fun execute(ctx: Context) {
        ctx.send {
            setColor(Colors.rndColor)
            setTitle("Perks.")
            setDescription("""
                This command has been changed.
                Now, you can manage your subscription all in one place.
                Run this command again with one of the subcommands listed below.
                Example: `${ctx.friendlyTrigger}perks link`.
                
                Premium servers are only available as part of our "Server Owners" tier.
                If eligible, by default any servers you own will automatically be upgraded to
                premium. You get **$PREMIUM_SERVERS slots** for upgrading servers you don't own.
                
                `link  ` - Link your Patreon subscription to the bot.
                `add   ` - Link a server to your subscription.
                `remove` - Remove a server from your subscription.
                `list  ` - Lists all servers attached to your subscription.
            """.trimIndent())
        }
    }

    @SubCommand(aliases = ["redeem"], description = "Link your Patreon subscription to the bot.")
    fun link(ctx: Context) {
        ctx.send("Searching for your subscription. This could take up to 30 seconds.")

        BoobBot.pApi.fetchPledgesOfCampaign("1928035").thenAccept {
            if (it.isEmpty()) {
                return@thenAccept ctx.send("Patreon API returned an invalid response. Report this to https://discord.gg/bra.")
            }

            val pledge = it.firstOrNull { u -> u.discordId != null && u.discordId == ctx.author.idLong }
                ?: return@thenAccept ctx.send("Unable to find your subscription. Make sure your Discord account is linked to your Patreon account, whore.")

            if (pledge.isDeclined) {
                return@thenAccept ctx.send("Your payment appears to have been declined. Fix it before attempting to claim your perks, whore.")
            }

            val pledgeAmount = pledge.pledgeCents.toDouble() / 100
            val pledgeFriendly = String.format("%1$,.2f", pledgeAmount)
            ctx.reply("Welcome to the club, whore. Your tier: `${BoobBot.pApi.getDonorType(pledgeAmount)}` ($$pledgeFriendly)")
            BoobBot.database.setDonor(ctx.author.id, pledgeAmount)
        }
    }

    @SubCommand(description = "Link a server to your subscription.")
    fun add(ctx: Context) {
        if (!ctx.isFromGuild) {
            return ctx.send("Run this command in a guild, whore.")
        }

        val guildId = ctx.guild!!.id

        when {
            BoobBot.database.isPremiumServer(guildId) -> ctx.send("This server is already premium, whore.")
            BoobBot.pApi.getDonorType(ctx.author.id) < DonorType.SERVER_OWNER -> ctx.send("You need to be subscribed to the Server Owner tier, whore.\nJoin here: <https://www.patreon.com/join/OfficialBoobBot/checkout?rid=3186958>")
            ctx.member!!.isOwner -> ctx.send("You own this server, whore, so it's already premium.")
            BoobBot.database.getPremiumServers(ctx.author.idLong).size > PREMIUM_SERVERS -> ctx.send("You've hit the maximum number of premium servers. Remove some or fuck off, whore.")
            else -> {
                val predicate = { e: ButtonInteractionEvent -> e.componentId == "ps:accept:${ctx.author.id}" || e.componentId == "ps:cancel:${ctx.author.id}" }
                val waiterSetup = ctx.onButtonInteraction("ps:${ctx.author.id}", predicate, 10000) {
                    if (it == null) {
                        return@onButtonInteraction ctx.send("Fine, whore. The server won't be added.")
                    }

                    if (it.componentId == "ps:cancel:${ctx.author.id}") {
                        return@onButtonInteraction it.editComponents().setContent("Fine, whore. The server won't be added.").queue()
                    }

                    BoobBot.database.setPremiumServer(ctx.guild.id, ctx.author.idLong)
                    it.editComponents().setContent("Server added, whore.").queue()
                }

                if (!waiterSetup) {
                    return ctx.send("wtf? go cancel your other interaction first, whore.")
                }

                ctx.message {
                    content("Hey whore, are you *really* sure you want to add **${ctx.guild.name}** to your premium servers?")
                    row {
                        button(ButtonStyle.SUCCESS, "ps:accept:${ctx.author.id}", "Add Server")
                        button(ButtonStyle.DANGER, "ps:cancel:${ctx.author.id}", "Cancel")
                    }
                }
            }
        }
    }

    @SubCommand(description = "Remove a server from your subscription.")
    fun remove(ctx: Context) {
        val servers = BoobBot.database.getPremiumServers(ctx.author.idLong)

        if (servers.isEmpty()) {
            return ctx.send("You don't have any premium servers, whore.")
        }

        val guilds = servers.map { (BoobBot.shardManager.getGuildById(it._id)?.name ?: "Inaccessible Server") to it._id }

        val predicate = { e: GenericComponentInteractionCreateEvent -> e.componentId == "menu:ps:${ctx.author.id}" || e.componentId == "ps:cancel:${ctx.author.id}" }
        val waiterSetup = ctx.onMenuInteraction("ps:${ctx.author.id}", predicate, 15000) {
            if (it == null) {
                return@onMenuInteraction ctx.send("Fine, whore. No servers will be removed.")
            }

            if (it is ButtonInteractionEvent && it.componentId == "ps:cancel:${ctx.author.id}") {
                return@onMenuInteraction it.editComponents().setContent("Fine, whore. No servers will be removed.").queue()
            }

            val selected = (it as SelectMenuInteractionEvent).selectedOptions[0]
            BoobBot.database.removePremiumServer(selected.value)
            it.editComponents().setContent("Removed **${selected.label}**, whore.").queue()
        }

        if (!waiterSetup) {
            return ctx.send("wtf? go cancel your other interaction first, whore.")
        }

        ctx.message {
            content("Select the server you want to remove from the list below.\nThis prompt will time out in 15 seconds.")
            row {
                menu("menu:ps:${ctx.author.id}") {
                    for ((name, id) in guilds) {
                        addOption(name, id)
                    }
                }
            }
            row {
                button(ButtonStyle.DANGER, "ps:cancel:${ctx.author.id}", "Cancel")
            }
        }
    }

    @SubCommand(description = "Lists all servers attached to your subscription.")
    fun list(ctx: Context) {
        val servers = BoobBot.database.getPremiumServers(ctx.author.idLong)

        if (servers.isEmpty()) {
            return ctx.send("You don't have any premium servers, whore.")
        }

        val guilds = servers.joinToString("`\n`", prefix = "`", postfix = "`") { BoobBot.shardManager.getGuildById(it._id)?.name ?: "Inaccessible Server" }

        ctx.send {
            setColor(Colors.rndColor)
            setTitle("Your Premium Servers")
            setDescription("""
                All your additional premium servers are listed below.
                Any servers you own are already premium without counting towards your limit.
                
                $guilds
            """.trimIndent())
        }
    }
}
