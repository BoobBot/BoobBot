package bot.boobbot.handlers

import bot.boobbot.BoobBot
import bot.boobbot.flight.Context
import bot.boobbot.misc.Formats
import bot.boobbot.misc.Utils
import bot.boobbot.misc.canTalk
import bot.boobbot.models.Config
import com.mewna.catnip.entity.message.Message
import com.mewna.catnip.entity.util.Permission
import de.mxro.metrics.jre.Metrics
import java.util.concurrent.Executors

class MessageHandler {

    private val botPrefix = if (BoobBot.isDebug) "!bb" else "bb"
    private val executor = Executors.newFixedThreadPool(300) // Adjust if needed.
    //private val noSpam = mutableListOf<Long>()

    fun processMessage(event: Message) {
        executor.submit {
            try {
                onMessageReceived(event)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun onMessageReceived(event: Message) {
        BoobBot.metrics.record(Metrics.happened("MessageReceived"))

//        if (!BoobBot.isReady) {
//            return
//        }

        if (event.author().bot()) {
            return
        }
//        if (shitUsers.getOrDefault(event.author.idLong, 0) > 75 && !Utils.checkDonor(event)) {
//            BoobBot.log.warn("Shit user blocked ${event.author} on ${event.channel}")
//            return
//        }

        if (event.channel().isGuild) {
            if (event.guild()?.unavailable() == true || !event.channel().asTextChannel().canTalk()) {
                return
            }

            if (event.mentionsEveryone()) {
                BoobBot.metrics.record(Metrics.happened("atEveryoneSeen"))
            }
        }

        val messageContent = event.content()
        val acceptablePrefixes = arrayOf(
            botPrefix,
            "<@${BoobBot.selfId}> ",
            "<@!${BoobBot.selfId}> "
        )

        val trigger = acceptablePrefixes.firstOrNull { messageContent.toLowerCase().startsWith(it) }
            ?: return

        val args = messageContent.substring(trigger.length).split(" +".toRegex()).toMutableList()
        val commandString = args.removeAt(0)

        val command = Utils.getCommand(commandString) ?: return

        if (!command.properties.enabled) {
            return
        }

        if (command.properties.developerOnly && !Config.owners.contains(event.author().idAsLong())) {
            return
        }

        if (command.properties.guildOnly && !event.channel().isGuild) {
            event.channel().sendMessage("No, whore you can only use this in a guild")
            return
        }

        if (command.properties.nsfw && event.channel().isGuild && !event.channel().asTextChannel().nsfw()) {
            event.channel().sendMessage("This isn't a NSFW channel you whore. Confused? try `bbhuh`")
            return
        }

        if (event.channel().isGuild && !event.guild()!!.selfMember().hasPermissions(event.channel().asTextChannel(), Permission.EMBED_LINKS)) {
            event.channel().sendMessage("I do not have permission to use embeds, da fuck?")
            return
        }

        if (command.properties.donorOnly && !Utils.checkDonor(event)) {
            event.channel().sendMessage(
                Formats.error(
                    " Sorry this command is only available to our Patrons.\n<:p_:475801484282429450> "
                            + "Stop being a cheap fuck and join today!\nhttps://www.patreon.com/OfficialBoobBot"
                )
            )
            return
                /* event.channel.sendMessage(
                 Formats.info(
                     "This command is normally only available to our Patrons. But Merry Christmas, Enjoy until the 26th :^)\n"
                             + "<:p_:475801484282429450> Still stop being a cheap fuck and join today!\n<https://www.patreon.com/OfficialBoobBot>"
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
            Utils.logCommand(event)
            BoobBot.metrics.record(Metrics.happened("command"))
            BoobBot.metrics.record(Metrics.happened(command.name))
            command.execute(Context(trigger, event, args.toTypedArray()))
        } catch (e: Exception) {
            BoobBot.log.error("Command `${command.name}` encountered an error during execution", e)
            event.react("\uD83D\uDEAB")
        }
    }

}