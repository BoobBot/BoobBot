package bot.boobbot.commands.bot

import bot.boobbot.BoobBot
import bot.boobbot.entities.framework.Context
import bot.boobbot.entities.framework.annotations.CommandProperties
import bot.boobbot.entities.framework.annotations.SubCommand
import bot.boobbot.entities.framework.interfaces.Command
import bot.boobbot.entities.misc.DonorType
import bot.boobbot.entities.misc.PatronStatus
import bot.boobbot.utils.Colors
import bot.boobbot.utils.Constants
import net.dv8tion.jda.api.components.buttons.ButtonStyle
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent
import net.dv8tion.jda.api.events.interaction.component.GenericComponentInteractionCreateEvent
import net.dv8tion.jda.api.events.interaction.component.StringSelectInteractionEvent

@CommandProperties(description = "Receive your rewards after subscribing on Patreon.", groupByCategory = true)
class Perks : Command {
    companion object {
        private const val PREMIUM_SERVERS = 3
    }

    override fun execute(ctx: Context) {
        ctx.reply {
            setColor(Colors.rndColor)
            setTitle("Perks.")
            setDescription("""
                This command has been changed.
                Now, you can manage your subscription all in one place.
                Run this command again with one of the subcommands listed below.
                Example: `${ctx.prefix}perks link`.
                
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
        ctx.reply("Searching for your subscription. This could take up to 30 seconds.")

        BoobBot.pApi.fetchPledgesOfCampaign("1928035").thenAccept {
            if (it.isEmpty()) {
                return@thenAccept ctx.reply("Patreon API returned an invalid response. Report this to ${Constants.SUPPORT_SERVER_URL}.")
            }

            val pledge = it.firstOrNull { u -> u.discordId != null && u.discordId == ctx.user.idLong }
                ?: return@thenAccept ctx.reply("Unable to find your subscription. Make sure your Discord account is linked to your Patreon account, whore.")

            if (pledge.status == PatronStatus.FORMER_PATRON) {
                return@thenAccept ctx.reply("You don't appear to be currently subscribed, whore.")
            } else if (pledge.status == PatronStatus.DECLINED_PATRON) {
                return@thenAccept ctx.reply("Your payment appears to have been declined. Fix it before attempting to claim your perks, whore.")
            }

            val pledgeAmount = pledge.entitledAmountCents.toDouble() / 100
            val pledgeFriendly = String.format("%1$,.2f", pledgeAmount)
            ctx.reply("Welcome to the club, whore. Your tier: `${BoobBot.pApi.getDonorType(pledgeAmount)}` ($$pledgeFriendly)")
            BoobBot.database.setDonor(ctx.user.idLong, pledgeAmount)
        }
    }

    @SubCommand(description = "Link a server to your subscription.")
    fun add(ctx: Context) {
        if (!ctx.isFromGuild) {
            return ctx.reply("Run this command in a guild, whore.")
        }

        val guildId = ctx.guild.idLong

        when {
            BoobBot.database.isPremiumServer(guildId) -> ctx.reply("This server is already premium, whore.")
            BoobBot.pApi.getDonorType(ctx.user.idLong) < DonorType.SERVER_OWNER -> ctx.reply("You need to be subscribed to the Server Owner tier, whore.\nJoin here: <https://www.patreon.com/join/OfficialBoobBot/checkout?rid=3186958>")
            ctx.member!!.isOwner -> ctx.reply("You own this server, whore, so it's already premium.")
            BoobBot.database.getPremiumServers(ctx.user.idLong).size > PREMIUM_SERVERS -> ctx.reply("You've hit the maximum number of premium servers. Remove some or fuck off, whore.")
            else -> {
                val waiterSetup = ctx.onButtonInteraction("ps:${ctx.user.id}", { it.componentId == "ps:accept:${ctx.user.id}" || it.componentId == "ps:cancel:${ctx.user.id}" }, 20000) {
                    when {
                        it == null -> ctx.reply("Fine, whore. The server won't be added.")
                        it.componentId == "ps:cancel:${ctx.user.id}" -> it.editComponents().setContent("Fine, whore. The server won't be added.").queue()
                        else -> {
                            BoobBot.database.setPremiumServer(guildId, ctx.user.idLong)
                            it.editComponents().setContent("Server added, whore.").queue()
                        }
                    }
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

    @SubCommand(description = "Remove a server from your subscription.")
    fun remove(ctx: Context) {
        val servers = BoobBot.database.getPremiumServers(ctx.user.idLong)

        if (servers.isEmpty()) {
            return ctx.reply("You don't have any premium servers, whore.")
        }

        val guilds = servers.map { (BoobBot.shardManager.getGuildById(it)?.name ?: "Inaccessible Server") to it }

        val predicate = { e: GenericComponentInteractionCreateEvent -> e.componentId == "menu:ps:${ctx.user.id}" || e.componentId == "ps:cancel:${ctx.user.id}" }
        val waiterSetup = ctx.onMenuInteraction("ps:${ctx.user.id}", predicate, 30000) {
            if (it == null) {
                return@onMenuInteraction ctx.reply("Fine, whore. No servers will be removed.")
            }

            if (it is ButtonInteractionEvent && it.componentId == "ps:cancel:${ctx.user.id}") {
                return@onMenuInteraction it.editComponents().setContent("Fine, whore. No servers will be removed.").queue()
            }

            val selected = (it as StringSelectInteractionEvent).selectedOptions[0]
            BoobBot.database.setPremiumServer(selected.value.toLong(), null)
            it.editComponents().setContent("Removed **${selected.label}**, whore.").queue()
        }

        if (!waiterSetup) {
            return ctx.reply("wtf? go cancel your other interaction first, whore.")
        }

        ctx.message {
            content("Select the server you want to remove from the list below.\nThis prompt will time out in 30 seconds.")
            row {
                menu("menu:ps:${ctx.user.id}") {
                    for ((name, id) in guilds) {
                        addOption(name, id.toString())
                    }
                }
            }
            row {
                button(ButtonStyle.DANGER, "ps:cancel:${ctx.user.id}", "Cancel")
            }
        }
    }

    @SubCommand(description = "Lists all servers attached to your subscription.")
    fun list(ctx: Context) {
        val servers = BoobBot.database.getPremiumServers(ctx.user.idLong)

        if (servers.isEmpty()) {
            return ctx.reply("You don't have any premium servers, whore.")
        }

        val guilds = servers.joinToString("`\n`", prefix = "`", postfix = "`") { BoobBot.shardManager.getGuildById(it)?.name ?: "Inaccessible Server" }

        ctx.reply {
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
