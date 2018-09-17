package bot.boob.bot.commands.bot;

import bot.boob.bot.BoobBot;
import bot.boob.bot.commons.Colors;
import bot.boob.bot.commons.Formats;
import com.github.rainestormee.jdacommand.Command;
import com.github.rainestormee.jdacommand.CommandAttribute;
import com.github.rainestormee.jdacommand.CommandDescription;
import com.jagrosh.jdautilities.menu.Paginator;
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.exceptions.PermissionException;

import java.text.MessageFormat;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import static bot.boob.bot.handlers.EventHandler.getShards;

@CommandDescription(
        name = "ping",
        triggers = "ping",
        attributes = {@CommandAttribute(key = "dm"), @CommandAttribute(key = "bot")},
        description = "Pong!"
)
public class PingCommand implements Command {
    private Paginator.Builder pbuilder =
            new Paginator.Builder()
                    .setColumns(1)
                    .setItemsPerPage(10)
                    .showPageNumbers(true)
                    .waitOnSinglePage(false)
                    .useNumberedItems(true)
                    .setFinalAction(
                            m -> {
                                try {
                                    m.clearReactions().queue();
                                } catch (PermissionException ex) {
                                    m.delete().queue();
                                }
                            })
                    .setEventWaiter(BoobBot.waiter)
                    .setTimeout(1, TimeUnit.MINUTES);

    @Override
    public void execute(Message trigger, String args) {
        if (args.toLowerCase().contains("--all")) {
            List<String> pinglist = new ArrayList<>();
            Map<JDA, JDA.Status> s = getShards().getStatuses();
            pbuilder.clearItems();
            for (Map.Entry<JDA, JDA.Status> e : s.entrySet()) {
                if (trigger.getJDA().getShardInfo().getShardId()
                        == e.getKey().getShardInfo().getShardId()) {
                    pbuilder.addItems(
                            MessageFormat.format(
                                    "Shard: {0}, Ping: {1}ms, Status: {2} (This Guild)\n",
                                    e.getKey().getShardInfo().getShardId(), e.getKey().getPing(), e.getValue()));
                } else {
                    pbuilder.addItems(
                            MessageFormat.format(
                                    "Shard: {0}, Ping: {1}ms, Status: {2}\n",
                                    e.getKey().getShardInfo().getShardId(), e.getKey().getPing(), e.getValue()));
                }
            }
            Paginator p =
                    pbuilder
                            .setColor(Colors.getEffectiveColor(trigger))
                            .setText(Formats.MAGIC_EMOTE + " **Global Pings** " + Formats.LEWD_EMOTE)
                            .setUsers(trigger.getAuthor())
                            .build();
            p.paginate(trigger.getChannel(), 1);
            return;
        }

        trigger
                .getChannel()
                .sendMessage("Ping!")
                .queue(
                        m1 ->
                                m1.editMessage("Ping: \uD83C\uDFD3").queue(
                                        m2 ->
                                                m2.editMessage(
                                                        "⏳ Ping: "
                                                                + m1
                                                                .getCreationTime()
                                                                .until(m2.getCreationTime(), ChronoUnit.MILLIS)
                                                                + "ms | \uD83D\uDC93 Web-socket: "
                                                                + trigger.getJDA().getPing()
                                                                + "ms")
                                                        .queue()));
    }
}
