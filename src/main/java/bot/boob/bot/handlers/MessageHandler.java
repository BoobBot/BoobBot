package bot.boob.bot.handlers;

import bot.boob.bot.BoobBot;
import bot.boob.bot.commons.Colors;
import bot.boob.bot.commons.Formats;
import bot.boob.bot.commons.apis.Nekos;
import bot.boob.bot.commons.checks.BotChecks;
import bot.boob.bot.commons.checks.MiscChecks;
import com.github.rainestormee.jdacommand.Command;
import com.github.rainestormee.jdacommand.CommandHandler;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;


public class MessageHandler extends ListenerAdapter {
    private static final ThreadGroup threadGroup = new ThreadGroup("Command Executor");
    private static final Executor commandsExecutor =
            Executors.newCachedThreadPool(r -> new Thread(threadGroup, r, "Command Pool"));

    static {
        threadGroup.setMaxPriority(Thread.MAX_PRIORITY);
    }

    private final CommandHandler handler;

    public MessageHandler(CommandHandler handler) {
        this.handler = handler;
    }

    @Override
    @SuppressWarnings("unchecked")
    public void onMessageReceived(MessageReceivedEvent event) {
        //if (event.getAuthor().getIdLong() != 472573259108319237L){return;} //debug stuff
        if (!EventHandler.getREADY()) {
            return;
        }
        commandsExecutor.execute(
                () -> {
                    JDA jda = event.getJDA();
                    if (BotChecks.noBot(event.getMessage())) return;
                    String prefix = "!bb";
                    String message = event.getMessage().getContentRaw();
                    if (message.startsWith(jda.getSelfUser().getAsMention())
                            & message.length() == jda.getSelfUser().getAsMention().length()) {
                        try {
                            event
                                    .getChannel()
                                    .sendMessage(
                                            event.getAuthor().getAsMention()
                                                    + ", My prefix is `"
                                                    + prefix
                                                    + "`\n`"
                                                    + prefix
                                                    + "help` to see my commands.")
                                    .queue();
                        } catch (Exception e) {
                            BoobBot.log.error("shit ", e);
                        }
                        return;
                    }

                    if (!message.toLowerCase().startsWith("!bb")) return;

                    String[] splitMessage = message.split("\\s+", 2);
                    String commandString;
                    try {
                        if (message.toLowerCase().startsWith(prefix.toLowerCase())) {
                            commandString = splitMessage[0].substring(prefix.length());
                        } else {
                            commandString = splitMessage[0].substring(jda.getSelfUser().getAsMention().length());
                        }
                    } catch (Exception e) {
                        return;
                    }
                    Command command = handler.findCommand(commandString.toLowerCase());
                    if (command == null) {
                        if (message.startsWith(jda.getSelfUser().getAsMention())) {
                            try {
                                event
                                        .getChannel()
                                        .sendMessage(
                                                event.getAuthor().getName()
                                                        + ", "
                                                        + Nekos.getChat(false, message.replace("@", "")).replace("@", "\\@\\"))
                                        .queue();
                            } catch (Exception e) {
                                BoobBot.log.error("shit ", e);
                            }
                            return;
                        } else return;
                    }

                    if (command.hasAttribute("OwnerOnly") && !MiscChecks.isOwner(event.getMessage())) {
                        return;
                    }


                    if (BotChecks.isDm(event.getMessage()) && !command.hasAttribute("dm")) {
                        event
                                .getChannel()
                                .sendMessage(Formats.error("No, whore you can only use this in a guild"))
                                .queue();
                        return;
                    }

                    if (!BotChecks.isDm(event.getMessage())) {
                        if (!BotChecks.canSend(event.getMessage())) {
                            return;
                        }
                        if (!BotChecks.canEmbed(event.getMessage())) {
                            event
                                    .getChannel()
                                    .sendMessage(
                                            Formats.error(
                                                    "I do not have permission to use embeds, Da fuck?"))
                                    .queue();
                            return;
                        }
                    }

                    if (!BotChecks.isDm(event.getMessage())) {
                        if (!event.getTextChannel().isNSFW() && command.hasAttribute("nsfw")) {
                            event
                                    .getChannel()
                                    .sendMessage(
                                            new EmbedBuilder()
                                                    .setColor(Colors.getEffectiveColor(event.getMessage()))
                                                    .setDescription(
                                                            "This isn't a nsfw channel you whore.")
                                                    .build())
                                    .queue();
                            return;
                        }
                    }

                    try {
                        Formats.logCommand(event.getMessage());
                        handler.execute(
                                command, event.getMessage(), splitMessage.length > 1 ? splitMessage[1] : "");
                    } catch (Exception e) {
                        BoobBot.log.error("Error on command", e);
                        event.getMessage().addReaction("\uD83D\uDEAB").queue();
                    }
                });
    }
}