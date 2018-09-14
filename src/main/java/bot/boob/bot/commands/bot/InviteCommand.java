package bot.boob.bot.commands.bot;


import bot.boob.bot.commons.Colors;
import bot.boob.bot.commons.Formats;
import bot.boob.bot.commons.Misc;
import com.github.rainestormee.jdacommand.Command;
import com.github.rainestormee.jdacommand.CommandAttribute;
import com.github.rainestormee.jdacommand.CommandDescription;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.Message;

import java.text.MessageFormat;

@CommandDescription(
        name = "invite",
        triggers = {"invite", "join", "oauth", "link", "links", "support"},
        attributes = {@CommandAttribute(key = "dm"), @CommandAttribute(key = "bot")},
        description = "bot and support guild links"
)
public class InviteCommand implements Command {
    @Override
    public void execute(Message trigger, String args) {
        trigger
                .getTextChannel()
                .sendMessage(
                        new EmbedBuilder()
                                .setColor(Colors.getRndColor())
                                .setAuthor(
                                        trigger.getJDA().getSelfUser().getName(),
                                        trigger.getJDA().getSelfUser().getEffectiveAvatarUrl(),
                                        trigger.getJDA().getSelfUser().getEffectiveAvatarUrl())
                                .setDescription(Formats.LING_MSG)
                                .setFooter(
                                        MessageFormat.format(
                                                "Requested by {0} | {1}",
                                                trigger.getMember().getEffectiveName(), Misc.now()),
                                        trigger.getAuthor().getEffectiveAvatarUrl())
                                .build())
                .queue();
    }
}
