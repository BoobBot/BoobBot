package bot.boobbot.flight;

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

    boolean nsfw() default false;

    boolean enabled() default true;

    boolean guildOnly() default false;

    enum category {
        CONTROLS, MEDIA, MISC
    }

}