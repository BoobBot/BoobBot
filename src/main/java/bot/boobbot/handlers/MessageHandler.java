package bot.boobbot.handlers;

import bot.boobbot.BoobBot;
import bot.boobbot.flight.Command;
import bot.boobbot.flight.Context;
import bot.boobbot.misc.Constants;
import bot.boobbot.misc.Formats;
import bot.boobbot.misc.Utils;
import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;

import static bot.boobbot.misc.Utils.isDonor;

public class MessageHandler extends ListenerAdapter {

    private static String prefix = BoobBot.isDebug() ? "!bb" : "bb";

    @Override
    public void onMessageReceived(MessageReceivedEvent event) {

        if (event.getAuthor().isFake() || event.getAuthor().isBot()) {
            return;
        }

        if (event.getChannelType().isGuild()) {
            if (!event.getGuild().isAvailable() || !event.getTextChannel().canTalk()) {
                return;
            }
        }

        final String messageContent = event.getMessage().getContentRaw();
        final String mention = event.getChannelType().isGuild() ? event.getGuild().getSelfMember().getAsMention() : "";

        boolean isMentionTrigger = messageContent.startsWith(mention);
        boolean hasPrefix = isMentionTrigger || messageContent.startsWith(prefix);

        if (!hasPrefix) {
            return;
        }

        final String trigger = isMentionTrigger ? mention + " " : prefix;

        final String[] content = messageContent.substring(trigger.length())
                .split(" +", 2);

        if (content.length == 0) {
            return;
        }

        final String commandString = content[0].toLowerCase();
        final String[] args = content.length > 1 ? content[1].split(" +") : new String[0];

        final Command command = Utils.getCommand(commandString);

        if (command == null) {
            return; // TODO Check if mention prefix and call Nekos.getChat?
        }

        if (!command.getProperties().enabled()) { // Is command enabled?
            return;
        }

        if (command.getProperties().developerOnly() &&
                !Constants.OWNERS.contains(event.getAuthor().getIdLong())) { // Is command developer only?
            return;
        }

        if (command.getProperties().guildOnly() && !event.getChannelType().isGuild()) { // Is command guild-only?
            event.getChannel().sendMessage("No, whore you can only use this in a guild").queue();
            return;
        }

        if (command.getProperties().nsfw() && event.getChannelType().isGuild() &&
                !event.getTextChannel().isNSFW()) {
            event.getChannel().sendMessage("This isn't a NSFW channel you whore.").queue();
            return;
        }

        if (!event.getGuild().getSelfMember().hasPermission(event.getTextChannel(), Permission.MESSAGE_EMBED_LINKS)) {
            event.getChannel().sendMessage("I do not have permission to use embeds, da fuck?").queue();
            return;
        }

        if (command.getProperties().donorOnly() && !isDonor(event.getAuthor())) {
            event.getChannel().sendMessage(Formats.error(
                    " Sorry this command is only available to our Patrons.\n"
                            + event
                            .getJDA()
                            .asBot()
                            .getShardManager()
                            .getEmoteById(475801484282429450L)
                            .getAsMention()
                            + " [Stop being a cheap fuck and join today]()")).queue(); // TODO patreon link
            return;
        }

        try {
            command.execute(new Context(trigger, event, args));
        } catch (Exception e) {
            BoobBot.log.error("Command `" + command.getName() + "` encountered an error during execution", e);
            event.getMessage().addReaction("\uD83D\uDEAB").queue();
        }
    }
}
