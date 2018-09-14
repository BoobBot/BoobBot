package bot.boob.bot.commands.nsfw;


import bot.boob.bot.BoobBot;
import bot.boob.bot.commons.Colors;
import bot.boob.bot.commons.Formats;
import bot.boob.bot.commons.Misc;
import com.github.rainestormee.jdacommand.Command;
import com.github.rainestormee.jdacommand.CommandAttribute;
import com.github.rainestormee.jdacommand.CommandDescription;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.Message;

import java.text.MessageFormat;

import static bot.boob.bot.commons.apis.bbapi.makeReqAndGetResAsString;


@CommandDescription(
        name = "ass",
        triggers = {"ass"},
        attributes = {@CommandAttribute(key = "dm"), @CommandAttribute(key = "nsfw")},
        description = "Shows some ass."
)
public class AssCommand implements Command {
    @Override
    public void execute(Message trigger, String args) {
        try {
            trigger
                    .getChannel()
                    .sendMessage(
                            new EmbedBuilder()
                                    .setDescription(Formats.LEWD_EMOTE)
                                    .setColor(Colors.getEffectiveColor(trigger))
                                    .setImage(makeReqAndGetResAsString("ass","url"))
                                    .setFooter(
                                    MessageFormat.format(
                                            "Requested by {0} | {1}", trigger.getAuthor().getName(), Misc.now()),
                                    trigger.getAuthor().getEffectiveAvatarUrl())
                                    .build())
                    .queue();
        } catch (Exception e) {
            trigger.getChannel().sendMessage(Formats.error("oh? something broken af")).queue();
            BoobBot.log.error("lewd command broken? ", e);
        }
    }
}
