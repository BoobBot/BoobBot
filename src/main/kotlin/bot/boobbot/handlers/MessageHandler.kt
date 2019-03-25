package bot.boobbot.handlers

import bot.boobbot.BoobBot
import bot.boobbot.flight.Context
import bot.boobbot.misc.Formats
import bot.boobbot.misc.Utils
import bot.boobbot.models.Config
import de.mxro.metrics.jre.Metrics
import net.dv8tion.jda.core.Permission
import net.dv8tion.jda.core.entities.Message
import net.dv8tion.jda.core.hooks.ListenerAdapter

class MessageHandler : ListenerAdapter() {

    private val botPrefix = if (BoobBot.isDebug) "!bb" else "bb"
    //private val executor = Executors.newFixedThreadPool(300) // Adjust if needed.


    //fun processMessage(event: Message) {
        //executor.submit {
        //    try {
        //        onMessageReceived(event)
        //    } catch (e: Exception) {
        //        e.printStackTrace()
        //    }
        //}
    //}

    fun onMessageReceived(event: Message) {
        BoobBot.metrics.record(Metrics.happened("MessageReceived"))

        if (!BoobBot.isReady) {
            return
        }

        if (event.author.isBot) {
            return
        }

        if (event.channelType.isGuild) {
            if (!event.guild!!.isAvailable || !event.textChannel.canTalk()) {
                return
            }

            if (event.mentionsEveryone()) {
                BoobBot.metrics.record(Metrics.happened("atEveryoneSeen"))
            }
        }

        val messageContent = event.contentRaw
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

        if (command.properties.developerOnly && !Config.owners.contains(event.author.idLong)) {
            return
        }

        if (command.properties.guildOnly && !event.channelType.isGuild) {
            event.channel.sendMessage("No, whore you can only use this in a guild").queue()
            return
        }

        if (command.properties.nsfw && event.channelType.isGuild && !event.textChannel.isNSFW) {
            event.channel.sendMessage("This isn't a NSFW channel you whore. Confused? try `bbhuh`")
            return
        }

        if (event.channelType.isGuild && !event.guild!!.selfMember.hasPermission(event.textChannel, Permission.MESSAGE_EMBED_LINKS)) {
            event.channel.sendMessage("I do not have permission to use embeds, da fuck?").queue()
            return
        }

        if (command.properties.donorOnly && !Utils.checkDonor(event)) {
            event.channel.sendMessage(
                Formats.error(
                    " Sorry this command is only available to our Patrons.\n<:p_:475801484282429450> "
                            + "Stop being a cheap fuck and join today!\nhttps://www.patreon.com/OfficialBoobBot"
                )
            ).queue()
            return
        }


        try {
            Utils.logCommand(event)
            BoobBot.metrics.record(Metrics.happened("command"))
            BoobBot.metrics.record(Metrics.happened(command.name))
            command.execute(Context(trigger, event, args.toTypedArray()))
        } catch (e: Exception) {
            BoobBot.log.error("Command `${command.name}` encountered an error during execution", e)
            event.addReaction("\uD83D\uDEAB").queue()
        }
    }

}