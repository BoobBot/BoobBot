package bot.boob.bot.commons.checks;


import bot.boob.bot.handlers.EventHandler;
import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.ChannelType;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.utils.PermissionUtil;

public class BotChecks {

    public static boolean canSend(Message msg) {
        return PermissionUtil.checkPermission(
                msg.getTextChannel(), msg.getGuild().getSelfMember(), Permission.MESSAGE_WRITE);
    }

    public static boolean canEmbed(Message msg) {
        return PermissionUtil.checkPermission(
                msg.getTextChannel(), msg.getGuild().getSelfMember(), Permission.MESSAGE_EMBED_LINKS);
    }

    public static boolean canDelete(Message msg) {
        return PermissionUtil.checkPermission(
                msg.getTextChannel(), msg.getGuild().getSelfMember(), Permission.MESSAGE_MANAGE);
    }

    public static boolean canReact(Message msg) {
        return PermissionUtil.checkPermission(
                msg.getTextChannel(), msg.getGuild().getSelfMember(), Permission.MESSAGE_ADD_REACTION);
    }

    public static boolean isDm(Message msg) {
        return msg.isFromType(ChannelType.PRIVATE);
    }

    public static boolean noBot(Message msg) {
        return msg.getAuthor() == EventHandler.getJDA().getSelfUser() || msg.getAuthor().isBot();
    }

    public static boolean canEdit(Message msg) {
        return PermissionUtil.checkPermission(
                msg.getTextChannel(), msg.getGuild().getSelfMember(), Permission.MANAGE_CHANNEL);
    }


}
