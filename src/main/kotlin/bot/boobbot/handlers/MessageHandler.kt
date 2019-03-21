package bot.boobbot.handlers

import bot.boobbot.BoobBot
import bot.boobbot.flight.Context
import bot.boobbot.misc.Constants
import bot.boobbot.misc.Formats
import bot.boobbot.misc.Utils
import de.mxro.metrics.jre.Metrics
import net.dv8tion.jda.core.Permission
import net.dv8tion.jda.core.events.message.MessageReceivedEvent
import net.dv8tion.jda.core.hooks.ListenerAdapter

class MessageHandler : ListenerAdapter() {

    private val botPrefix = if (BoobBot.isDebug) "!bb" else "bb"
    //private val noSpam = mutableListOf<Long>()
    override fun onMessageReceived(event: MessageReceivedEvent) {
        BoobBot.metrics.record(Metrics.happened("MessageReceived"))

        if (!BoobBot.isReady) {
            return
        }

        if (event.author.isBot || event.author.isFake) {
            return
        }
//        if (shitUsers.getOrDefault(event.author.idLong, 0) > 75 && !Utils.checkDonor(event)) {
//            BoobBot.log.warn("Shit user blocked ${event.author} on ${event.channel}")
//            return
//        }

        if (event.channelType.isGuild) {
            if (!event.guild.isAvailable || !event.textChannel.canTalk()) {
                return
            }

            if (event.message.mentionsEveryone()) {
                BoobBot.metrics.record(Metrics.happened("atEveryoneSeen"))
            }
        }

        val messageContent = event.message.contentRaw
        val acceptablePrefixes = arrayOf(
            botPrefix,
            "<@${event.jda.selfUser.id}> ",
            "<@!${event.jda.selfUser.id}> "
        )

        val trigger = acceptablePrefixes.firstOrNull { messageContent.toLowerCase().startsWith(it) }
            ?: return

        val args = messageContent.substring(trigger.length).split(" +".toRegex()).toMutableList()
        val commandString = args.removeAt(0)

        val command = Utils.getCommand(commandString) ?: return

        if (!command.properties.enabled) {
            return
        }

        if (command.properties.developerOnly && !Constants.OWNERS.contains(event.author.idLong)) {
            return
        }

        if (command.properties.guildOnly && !event.channelType.isGuild) {
            return event.channel.sendMessage("No, whore you can only use this in a guild").queue()
        }

        if (command.properties.nsfw && event.channelType.isGuild && !event.textChannel.isNSFW) {
            return event.channel.sendMessage("This isn't a NSFW channel you whore. Confused? try `bbhuh`").queue()
        }

        if (event.channelType.isGuild && !event.guild.selfMember.hasPermission(
                event.textChannel,
                Permission.MESSAGE_EMBED_LINKS
            )
        ) {
            return event.channel.sendMessage("I do not have permission to use embeds, da fuck?").queue()
        }

        if (command.properties.donorOnly && !Utils.checkDonor(event)) {
            return event.channel.sendMessage(
                Formats.error(
                    " Sorry this command is only available to our Patrons.\n"
                            + BoobBot
                        .shardManager
                        .getEmoteById(475801484282429450L)
                        .asMention
                            + "Stop being a cheap fuck and join today!\nhttps://www.patreon.com/OfficialBoobBot"
                )
            ).queue()
            /* event.channel.sendMessage(
                 Formats.info(
                     "This command is normally only available to our Patrons. But Merry Christmas, Enjoy until the 26th :^)\n"
                             + BoobBot
                         .shardManager
                         .getEmoteById(475801484282429450L)
                         .asMention
                             + "Still stop being a cheap fuck and join today!\n<https://www.patreon.com/OfficialBoobBot>"
                 )
             ).queue()*/
        }

//        //shit cool-down
//        if (noSpam.contains(event.author.idLong)) {
//            BoobBot.log.warn("hit no spam ${event.author} on ${event.channel}")
//            val allSpamCount = shitUsers.getOrDefault(event.author.idLong, 0)
//            return if (allSpamCount > 20 && 75-allSpamCount > 0) {
//                event.channel.sendMessage(
//                    Formats.error(
//                        "Slow down whore, don't spam me! <:dafuck:558146584148443136> \n:no_entry_sign: You have ${75-allSpamCount} warnings left until blacklist :middle_finger: :no_entry_sign:"
//                    )
//                ).queue()
//
//            } else {
//                event.channel.sendMessage(
//                    Formats.error(
//                        "Slow down whore, don't spam me! <:dafuck:558146584148443136> "
//                    )
//                ).queue()
//            }
//        }

//            if (!Utils.checkDonor(event)) {
//                noSpam.add(event.author.idLong)
//                var allSpamCount = shitUsers.getOrDefault(event.author.idLong, 0)
//                allSpamCount++
//                shitUsers[event.author.idLong] = allSpamCount
//                Timer().schedule(1200) {
//                    noSpam.remove(event.author.idLong)
//                }
//            }


        try {
            Utils.logCommand(event.message)
            BoobBot.metrics.record(Metrics.happened("command"))
            BoobBot.metrics.record(Metrics.happened(command.name))
            command.execute(Context(trigger, event, args.toTypedArray()))
        } catch (e: Exception) {
            BoobBot.log.error("Command `${command.name}` encountered an error during execution", e)
            event.message.addReaction("\uD83D\uDEAB").queue()
        }
    }

}