package bot.boob.bot.commons.checks;

import bot.boob.bot.commons.Constants;
import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.User;
import net.dv8tion.jda.core.utils.PermissionUtil;

public class UserChecks {
    public static boolean isAdmin(Message msg) {
        return MiscChecks.isOwner(msg)
                || PermissionUtil.checkPermission(msg.getMember(), Permission.BAN_MEMBERS);
    }

    public static boolean isMod(Message msg) {
        return MiscChecks.isOwner(msg)
                || PermissionUtil.checkPermission(msg.getMember(), Permission.MESSAGE_MANAGE);
    }

    public static boolean isDJ(Member member) {
        return member.getRoles().stream().allMatch(x -> x.getName().equalsIgnoreCase("dj"));
    }

    public static boolean isDonor(User user) {
        Member member = user.getJDA().asBot().getShardManager().getGuildById(Constants.HOME_GUILD).getMember(user);
        if (member != null) {
            return member.getRoles().parallelStream().anyMatch(x -> x.getIdLong() == 440542799658483713L);
        } else return false;
    }

}