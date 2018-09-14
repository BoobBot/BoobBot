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
             .addField(
                 Formats.LEWD_EMOTE + " Commands",
                 MessageFormat.format(
                     "**{0}ping**: pong!\n"
                         + "**{0}thigh**: Posts a random sexy thigh.\n"
                         + "**{0}thighs**: Random thighs as a slideshow.\n"
                         + "**{0}sendthighs**: Dms something hot! Mention a @user to send to a friend.\n"
                         + "**{0}4k**: 4k Hotness!\n"
                         + "**{0}ass**: Shows some ass.\n"
                         + "**{0}black**: Gotta have that black love as well.\n"
                         + "**{0}boobs**: Shows some boobs.\n"
                         + "**{0}collared**: Play nice.\n"
                         + "**{0}dp**: Gotta get that double love!\n"
                         + "**{0}futa**: Hentai Traps.\n"
                         + "**{0}gif**: Gifs!!!\n"
                         + "**{0}hentai**: Hentai.\n"
                         + "**{0}pegged**: Strap-on love!\n"
                         + "**{0}phgif**: PornHub gifs! (Some are trash #BlamePornHub)\n"
                         + "**{0}pussy**: Pussy!\n"
                         + "**{0}tiny**: Tiny girls!\n"
                         + "**{0}traps**: Traps are hot!\n"
                         + "**{0}tattoo**: Tatted up women.\n"
                         + "**{0}yaoi**: Boy love."
						 + "**{0}clean**: Deletes all bot messages and triggers.\n"
                         + "**{0}nsfw**: Toggles the current channels nsfw setting.\n"
                         + "**{0}invite**: bot, support server and paypal links.\n",
                     gp),
                 false)
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
