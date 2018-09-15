package bot.boob.bot.commands.nsfw;

import bot.boob.bot.BoobBot;
import bot.boob.bot.commons.Colors;
import bot.boob.bot.commons.Formats;
import bot.boob.bot.commons.menu.ButtonMenu;
import com.github.rainestormee.jdacommand.Command;
import com.github.rainestormee.jdacommand.CommandAttribute;
import com.github.rainestormee.jdacommand.CommandDescription;
import net.dv8tion.jda.core.entities.*;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

import static bot.boob.bot.commons.apis.bbapi.getThigh;

@CommandDescription(
        name = "SendThighCommand",
        triggers = {"sendthighs", "st"},
        attributes = {
                @CommandAttribute(key = "dm", value = "no")
        },
        description = "SendThighCommand"
)
public class SendThighCommand implements Command {
    @Override
    public void execute(Message event, String args) {
        User author = event.getAuthor();
        List<User> u = event.getMentionedUsers();
        List<User> users = new ArrayList<>(u);
        AtomicBoolean blocked = new AtomicBoolean(false);
        if (users.isEmpty()) {
            users.add(author);
        }
        users.get(0).openPrivateChannel()
                .queue(
                        ((PrivateChannel pm) -> {
                            pm.sendMessage("hey!").queue(message -> {
                                message.delete().queue();
                                new ButtonMenu.Builder()
                                        .setColor(Colors.getEffectiveMemberColor(event.getMember()))
                                        .setDescription(
                                                Formats.info(
                                                        event.getMember().getEffectiveName() + " Has sent you some nsfw thighs, Are you 18+ and sure you want to continue?"))
                                        .setChoices(
                                                pm
                                                        .getJDA()
                                                        .asBot()
                                                        .getShardManager()
                                                        .getEmoteById(Formats.getEmoteID("<:yes:443810942221025280>")),
                                                pm
                                                        .getJDA()
                                                        .asBot()
                                                        .getShardManager()
                                                        .getEmoteById(Formats.getEmoteID("<:no:443810942099390464:>")))
                                        .setEventWaiter(BoobBot.waiter)
                                        .setTimeout(1, TimeUnit.MINUTES)
                                        .setFinalAction(
                                                m2 -> m2.delete().queue())
                                        .setUsers(pm.getUser())
                                        .setAction(
                                                re -> {
                                                    if (re.getEmote()
                                                            .equals(
                                                                    pm
                                                                            .getJDA()
                                                                            .asBot()
                                                                            .getShardManager()
                                                                            .getEmoteById(Formats.getEmoteID("<:yes:443810942221025280>")))) {
                                                        try {
                                                            pm.sendMessage(Formats.LEWD_EMOTE + " " + getThigh()).queue();
                                                        }catch (Exception e){
                                                           BoobBot.log.error("wtf ? " + e);
                                                        }
                                                    } else {
                                                        pm
                                                                .sendMessage("Alright i wont then")
                                                                .queue(m3 -> m3.delete().queueAfter(45, TimeUnit.SECONDS), null);
                                                    }
                                                })
                                        .build()
                                        .display(pm);
                            }, failed -> blocked.set(true));
                            try {
                                Thread.sleep(100);
                            } catch (Exception e){
                                BoobBot.log.error("tf" + e);
                            }
                            if (blocked.get()) {
                                event.getChannel().sendMessage(
                                        MessageFormat.format(
                                                "hey, This {0} {1} has me blocked or there filter turned on \uD83D\uDD95",
                                                "whore", users.get(0).getName())).queue();

                            }
                            else {
                                event.getChannel().sendMessage(MessageFormat.format("Good job {0}", event.getAuthor().getAsMention()))
                                        .queue();
                            }
                        }
                        ));

    }
}





