package bot.boob.bot.commands.bot;


import bot.boob.bot.commons.Formats;
import bot.boob.bot.commons.checks.BotChecks;
import com.github.rainestormee.jdacommand.Command;
import com.github.rainestormee.jdacommand.CommandAttribute;
import com.github.rainestormee.jdacommand.CommandDescription;
import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.managers.ChannelManager;
import net.dv8tion.jda.core.utils.PermissionUtil;

@CommandDescription(
        name = "Toggle",
        triggers = {"nsfw", "toggle"},
        attributes = {
                @CommandAttribute(key = "guild"),
        },
        description = "Toggles the current channels nsfw setting"
)
public class NsfwToggleCommand implements Command {
    @Override
    public void execute(Message message, String args) {
        if (!BotChecks.canEdit(message)) {
            message
                    .getTextChannel()
                    .sendMessage(
                            Formats.error("Hey whore, I lack the `MANAGE_CHANNEL` permission needed to do this"))
                    .queue();
            return;
        }
        if (!PermissionUtil.checkPermission(
                message.getTextChannel(), message.getMember(), Permission.MANAGE_CHANNEL)) {
            message
                    .getTextChannel()
                    .sendMessage(
                            Formats.error("Hey whore, You lack the `MANAGE_CHANNEL` permission needed to do this"))
                    .queue();
            return;
        }
        ChannelManager cm = new ChannelManager(message.getTextChannel());
        if (message.getTextChannel().isNSFW()) {
            cm.setNSFW(false).queue();
            message
                    .getTextChannel()
                    .sendMessage(
                            Formats.info(

                                            "I have set nsfw on this channel to False!"))
                    .queue();
            return;
        }
        if (!message.getTextChannel().isNSFW()) {
            cm.setNSFW(true).queue();
            message
                    .getTextChannel()
                    .sendMessage(
                            Formats.info(
                                            "I have set nsfw on this channel to True!"))
                    .queue();
        }
    }
}
