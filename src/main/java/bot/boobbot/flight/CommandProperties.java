package bot.boobbot.flight;

import net.dv8tion.jda.core.Permission;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface CommandProperties {
    String[] aliases() default {};

    String description() default "No description available";

    category category() default category.MISC;

    boolean developerOnly() default false;

    boolean donorOnly() default false;

    boolean nsfw() default false;

    boolean enabled() default true;

    boolean guildOnly() default false;

    enum category {
        GENERAL("<:TouchMaBooty:444601938320031745> General NSFW"),
        KINKS("<:whip:440551663804350495> Kinks"),
        VIDEOSEARCHING("\uD83D\uDCF9 Video Searching"),
        FANTASY("<:Pantsu:443870754107555842> Non-Real"),
        HOLIDAY("\uD83C\uDF85 Holiday"),
        SEND("\uD83D\uDCE7 Send Commands"),
        FUN("✨ Fun Commands"),
        AUDIO("\uD83D\uDD08 Audio Commands"),
        MISC("<:info:486945488080338944> Misc Commands");

        public String title;

        category(String title) {
            this.title = title;
        }
    }

}