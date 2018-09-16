package bot.boob.bot.commands.nsfw;

import bot.boob.bot.BoobBot;
import bot.boob.bot.commons.Colors;
import bot.boob.bot.commons.Formats;
import bot.boob.bot.commons.Misc;
import bot.boob.bot.commons.apis.discordServicesapi;
import bot.boob.bot.commons.apis.oBoobs;
import bot.boob.bot.commons.apis.oButts;
import com.github.rainestormee.jdacommand.Command;
import com.github.rainestormee.jdacommand.CommandAttribute;
import com.github.rainestormee.jdacommand.CommandDescription;

import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.*;

import java.awt.*;
import java.net.URL;
import java.net.URLConnection;
import static bot.boob.bot.commons.Misc.UA;
import static bot.boob.bot.commons.apis.bbapi.makeReqAndGetResAsString;


@CommandDescription(
		name = "Magik",
		triggers = {"magik"},
		attributes = {@CommandAttribute(key = "nsfw")},
		description =
				""
)
public class MagikCommand implements Command {

	@Override
	public void execute(Message message, String args) {
    String[] arg = args.trim().split(" ");
        if (args.length() == 0) {

                    message
                        .getChannel()
                        .sendMessage(
                                new EmbedBuilder().setDescription(Formats.error("Missing args\n" +
                                        "bbmagik <type>\n" +
                                        "Types: boobs, ass, dick"))
                                        .setColor(Color.red)
                                        .build())
                        .queue();
                    return;
    }
        switch (arg[0]) {
        case "boobs":
            try {
                URL url = discordServicesapi.getMagikUrl(oBoobs.getBoobs());
                URLConnection connection = url.openConnection();
                connection.setRequestProperty(UA[0], UA[1]);
                message.getTextChannel().sendFile(connection.getInputStream(), "Magik.png").queue();
            } catch (Exception e) {
                BoobBot.log.error("rip "+ e);
                message.getTextChannel().sendMessage(Formats.error("rip some error, press f")).queue();
            }
            break;
        case "ass":
            try {
                URL url = discordServicesapi.getMagikUrl(oButts.getAss());
                URLConnection connection = url.openConnection();
                connection.setRequestProperty(UA[0], UA[1]);
                message.getTextChannel().sendFile(connection.getInputStream(), "Magik.png").queue();
            } catch (Exception e) {
                BoobBot.log.error("rip "+ e);
                message.getTextChannel().sendMessage(Formats.error("rip some error, press f")).queue();
            }
            break;
        case "dick":
            try {
                URL url = discordServicesapi.getMagikUrl(makeReqAndGetResAsString("penis","url"));
                URLConnection connection = url.openConnection();
                connection.setRequestProperty(UA[0], UA[1]);
                message.getTextChannel().sendFile(connection.getInputStream(), "Magik.png").queue();
            } catch (Exception e) {
                BoobBot.log.error("rip "+ e);
                message.getTextChannel().sendMessage(Formats.error("rip some error, press f")).queue();
            }
            break;
        default:
            message
                    .getChannel()
                    .sendMessage(
                            new EmbedBuilder().setDescription(Formats.error("what?\n" +
                                    "Types: boobs, ass, dick"))
                                    .setColor(Color.red)
                                    .build())
                    .queue();

    }
}
}