package bot.boob.bot.commands.bot;


import bot.boob.bot.BoobBot;
import bot.boob.bot.commons.Colors;
import bot.boob.bot.commons.Formats;
import bot.boob.bot.commons.Misc;
import com.github.rainestormee.jdacommand.Command;
import com.github.rainestormee.jdacommand.CommandAttribute;
import com.github.rainestormee.jdacommand.CommandDescription;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.MessageEmbed;

import java.text.MessageFormat;

@CommandDescription(
        name = "help",
        triggers = {"help", "halp", "halllp", "coms", "commands"},
        attributes = {@CommandAttribute(key = "dm"), @CommandAttribute(key = "bot")},
        description = "help, --dm for dm"
)
@SuppressWarnings("unchecked")
public class HelpCommand implements Command {

    private static MessageEmbed comHelp(Message msg, Command command, String prefix) {
        CommandDescription description = command.getDescription();
        return new EmbedBuilder()
                .setAuthor(
                        msg.getJDA().getSelfUser().getName() + " Command info",
                        msg.getJDA().asBot().getInviteUrl(Permission.ADMINISTRATOR),
                        msg.getJDA().getSelfUser().getEffectiveAvatarUrl())
                .addField(
                        Formats.info("Info"),
                        MessageFormat.format(
                                "Command:\n**{0}{1}**\nAliases:\n**{2}**\nDescription:\n**{3}**",
                                prefix,
                                description.name(),
                                String.join(", ", description.triggers()),
                                command.getDescription().description()),
                        false)
                .setColor(Colors.getRndColor())
                .setFooter(
                        MessageFormat.format(
                                "Help requested by {0} | {1}", msg.getAuthor().getName(), Misc.now()),
                        msg.getAuthor().getEffectiveAvatarUrl())
                .build();
    }

    private static boolean isCom(Command command) {
        return command.getDescription() != null || command.hasAttribute("description");
    }

    @Override
    public void execute(Message trigger, String args) {
        String gp = "!bb";
        MessageEmbed embed =
                new EmbedBuilder()
                        .setAuthor(
                                trigger.getJDA().getSelfUser().getName() + " help " + Formats.MAGIC_EMOTE,
                                trigger.getJDA().asBot().getInviteUrl(Permission.ADMINISTRATOR),
                                trigger.getJDA().getSelfUser().getEffectiveAvatarUrl())
                        .setColor(Colors.getRndColor())
                        .addField("\uD83D\uDCF9 Video Searching", "bb**ph**: Searches PornHub for videos.\n" +
                                "bb**rt**: Searches RedTube for videos.\n" +
                                "bb**phgif**: PornHub gifs! (Some are trash #BlamePornHub)\n", false)
                        .addField("<:TouchMaBooty:444601938320031745> General NSFW", "bb**boobs**: Shows some boobs.\n" +
                                "bb**ass**: Shows some ass.\n" +
                                "bb**pussy**: Pussy!\n" +
                                "bb**dick**: Got dick?\n" +
                                "bb**real**: Real girls!\n" +
                                "bb**gif**: Sexy gifs!\n" +
                                "bb**4k**: 4k Hotness!\n" +
                                "bb**black**: Gotta have that black love as well.\n" +
                                "bb**bj**: BlowJobs!\n" +
                                "bb**dp**: Gotta get that double love!\n" +
                                "bb**cumsluts**: Sticky Love!", false)
                        .addField("<:whip:440551663804350495> Kinks", "bb**gay**: Got men?\n" +
                                "bb**lesbians**: Lesbians are sexy!\n" +
                                "bb**group**: For when 2 are not enough..\n" +
                                "bb**thigh**: Posts a random sexy thigh.\n" +
                                "bb**thighs**: Random thighs as a slideshow.\n" +
                                "bb**collared**: Play nice.\n" +
                                "bb**bottomless**: Sexy!\n" +
                                "bb**toys**: Everything is better with toys \uD83D\uDE09\n" +
                                "bb**anal**: That ass love tho.\n" +
                                "bb**bdsm**: Bondage and Discipline (BD), Dominance and Submission (DS), Sadism and Masochism (SM)\n" +
                                "bb**pegged**: Strap-on love!\n" +
                                "bb**traps**: Traps are hot!\n" +
                                "bb**pawg**: Phat Ass White Girls!\n" +
                                "bb**tiny**: Tiny girls!\n" +
                                "bb**tattoo**: Tatted up women.", false)
                        .addField("<:Pantsu:443870754107555842> Non real", "bb**hentai**: Hentai!\n" +
                                "bb**futa**: Hentai traps.\n" +
                                "bb**yaoi**: Boy love.\n" +
                                "bb**poke**: Pokemon porn!", false)
                        .addField("\uD83C\uDF85 Holiday", "bb**xmas**: Christmas \uD83C\uDF85\n" +
                                "bb**vday**: Valentines ❤\n" +
                                "bb**easter**: Easter \uD83D\uDC30", false)
                        .addField("\uD83D\uDCE7 Send commands", "bb**sendthighs**: Dms something hot! Mention a @user to send to a friend.\n" +
                                "bb**sendnudes**: Send some love.\n" +
                                "bb**senddick**: Send some dick.", false)
                        .addField("Misc commands", "bb**ping**: Pings the bot.\n" +
                                "bb**clean**: Deletes all bot messages and triggers.\n" +
                                "bb**nsfw**: Toggles the current channels nsfw setting.\n" +
                                "bb**invite**: bot, support server and PayPal links.", false)
                        .addField(Formats.LINK_EMOTE + " Links", Formats.LING_MSG, false)
                        .setFooter(
                                MessageFormat.format(
                                        "Help requested by {0} | {1}", trigger.getAuthor().getName(), Misc.now()),
                                trigger.getAuthor().getEffectiveAvatarUrl())
                        .build();
        if (args.length() != 0) {
            if (args.toLowerCase().endsWith("--dm")) {
                trigger.getAuthor().openPrivateChannel().queue(pm -> pm.sendMessage(embed).queue());
                trigger.addReaction("📬").queue();
                return;
            }
            Command command = BoobBot.commandHandler.findCommand(args.split(" ")[0]);
            if (command == null || !isCom(command)) {
                trigger
                        .getChannel()
                        .sendMessage("That command does not exist or has no CommandDescription annotation.")
                        .queue();
                return;
            }
            if (args.toLowerCase().endsWith("--dm")) {
                trigger
                        .getAuthor()
                        .openPrivateChannel()
                        .queue(pm -> pm.sendMessage(comHelp(trigger, command, gp)).queue());
                trigger.addReaction("📬").queue();
                return;
            }
            trigger.getChannel().sendMessage(comHelp(trigger, command, gp)).queue();
        } else {
            trigger.getChannel().sendMessage(embed).queue();
        }
    }
}
