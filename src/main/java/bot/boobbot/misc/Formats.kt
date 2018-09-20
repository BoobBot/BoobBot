package bot.boobbot.misc

import net.dv8tion.jda.core.JDA
import net.dv8tion.jda.core.JDAInfo
import net.dv8tion.jda.core.Permission
import net.dv8tion.jda.core.entities.Guild
import java.text.MessageFormat


/**
 * Created by Tom on 10/5/2017.
 */

class Formats {
    companion object {
        const val BOOT_BANNER = "(.Y.)"

        const val LEWD_EMOTE = "<:TouchMaBooty:444601938320031745>"
        const val DISCORD_EMOTE = "<:discord:486939267365470210>"
        const val BOT_EMOTE = "<:bot:486939322151338005>"
        const val LINK_EMOTE = "\uD83D\uDD17"
        const val PAYPAL_EMOTE = "<:paypal:486945242369490945>"
        const val MAGIC_EMOTE = "✨"
        const val INFO_EMOTE = "<:info:486945488080338944>"
        val LING_MSG = MessageFormat.format(
                "\n{0} **Server**: [https://invite.boob.bot](https://invite.boob.bot)"
                        + "\n{1} **Bot**: [https://bot.boob.bot](https://bot.boob.bot)"
                        + "\n{2} **Website**: [https:/boob.bot](https://boob.bot)"
                        + "\n{3} **Paypal**: [https://paypal.boob.bot](https://paypal.boob.bot)",
                DISCORD_EMOTE, BOT_EMOTE, LINK_EMOTE, PAYPAL_EMOTE)
        val tag = arrayOf("Amateur", "Anal Masturbation", "Anal Sex", "Animated", "Asian", "Bareback", "Bathroom", "BBW", "Behind the Scenes", "Big Ass", "Big Cock", "Big Tits", "Bikini", "Bisexual", "Black-haired", "Blonde", "Blowjob", "Bondage", "Boots", "Brunette", "Bukkake", "Car", "Cartoon", "Caucasian", "Celebrity", "CFNM", "Chubby", "Compilation", "Couple", "Cream Pie", "Cum Shot", "Cum Swap", "Deepthroat", "Domination", "Double Penetration", "Ebony", "Facial", "Fat", "Female-Friendly", "Femdom", "Fetish", "Fisting", "Footjob", "Funny", "Gagging", "Gangbang", "Gay", "Gay Couple", "Gay Group Sex", "German", "Glamour", "Glasses", "Glory Hole", "Granny", "Group Sex", "Gym", "Hairy", "Handjob", "Hentai", "High Heels", "Hospital", "Indian", "Interracial", "Japanese", "Kissing", "Latex", "Latin", "Lesbian", "Licking Vagina", "Lingerie", "Maid", "Massage", "Masturbation", "Mature", "Midget", "MILF", "Muscular", "Nurse", "Office", "Oral Sex", "Oriental", "Outdoor", "Pantyhose", "Party", "Peeing", "Piercings", "Police", "Pool", "Pornstar", "Position 69", "POV", "Pregnant", "Public", "Redhead", "Rimming", "Romantic", "Russian", "School", "Secretary", "Shaved", "Shemale", "Skinny", "Small Tits", "Solo Gay", "Solo Girl", "Solo Male", "Spanking", "Spectacular", "Spycam", "Squirting", "Stockings", "Strap-on", "Striptease", "Swallow", "Tattoos", "Teen", "Threesome", "Titfuck", "Toilet", "Toys", "Tribbing", "Uniform", "Vaginal Masturbation", "Vaginal Sex", "Vintage", "Wanking", "Webcam", "Young & Old")

        fun getReadyFormat(jda: JDA, HOME: Guild): String {
            return MessageFormat.format(
                    "Logging in\r\n{0}\r\n"
                            + "Oauth link:\r\n{1}\r\n"
                            + "JDA Version:\r\n{13}\n\r"
                            + "Docs halp:\r\nhttp://home.dv8tion.net:8080/job/JDA/javadoc/\r\n"
                            + "Logged in as:\r\n{2}({3})\r\n"
                            + "Guilds:\r\n{4}\r\n"
                            + "Shards:\r\n{5}\r\n"
                            + "Users:\r\n{6}\r\n"
                            + "Bots:\r\n{7}\r\n"
                            + "Total Users:\r\n{8}\r\n"
                            + "Home Guild:\r\n{9}\r\n"
                            + "Users:\r\n{10}\r\n"
                            + "Bots:\r\n{11}\r\n"
                            + "Total Users:\r\n{12}",
                    BOOT_BANNER,
                    jda.asBot().getInviteUrl(Permission.ADMINISTRATOR),
                    jda.selfUser.name,
                    jda.selfUser.id,
                    jda.asBot().shardManager.guilds.toTypedArray().size,
                    jda.asBot().shardManager.shardsTotal,
                    jda.asBot().shardManager.users.parallelStream().filter { user -> !user.isBot }.toArray().size,
                    jda.asBot().shardManager.users.parallelStream().filter { it.isBot }.toArray().size,
                    jda.asBot().shardManager.users.toTypedArray().size,
                    HOME.name,
                    HOME.members.stream().filter { user -> !user.user.isBot }.toArray().size,
                    HOME.members.stream().filter { user -> user.user.isBot }.toArray().size,
                    HOME.members.toTypedArray().size,
                    JDAInfo.VERSION)
        }

        fun codeBox(text: String, lang: String): String {
            return MessageFormat.format("```{0}\n{1}```", lang, text)
        }

        fun bold(text: String): String {
            return MessageFormat.format("**{0}**", text)
        }

        fun inline(text: String): String {
            return MessageFormat.format("`{0}`", text)
        }

        fun italics(text: String): String {
            return MessageFormat.format("*{0}*", text)
        }

        fun error(text: String): String {
            return MessageFormat.format("\uD83D\uDEAB {0}", text)
        }

        fun warning(text: String): String {
            return MessageFormat.format("\u26A0 {0}", text)
        }

        fun info(text: String): String {
            return MessageFormat.format("{1} {0}", text, INFO_EMOTE)
        }

        fun clean(text: String): String {
            return text.replace("@everyone", "@\u200beveryone").replace("@here", "@\u200bhere")
        }


    }
}