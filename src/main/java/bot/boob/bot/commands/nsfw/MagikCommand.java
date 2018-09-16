package bot.boob.bot.commands.nsfw;

import bot.boob.bot.BoobBot;
import bot.boob.bot.commons.Formats;
import bot.boob.bot.commons.apis.oBoobs;
import com.github.rainestormee.jdacommand.Command;
import com.github.rainestormee.jdacommand.CommandAttribute;
import com.github.rainestormee.jdacommand.CommandDescription;

import net.dv8tion.jda.core.entities.Message;

import java.net.URL;
import java.net.URLConnection;
import static bot.boob.bot.commons.Misc.UA;


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
		try {
			URL url = new URL("https://discord.services/api/magik/?url="+ oBoobs.getBoobs());
			URLConnection connection = url.openConnection();
			connection.setRequestProperty(UA[0], UA[1]);
			message.getTextChannel().sendFile(connection.getInputStream(), "Magik.png").queue();
		} catch (Exception e) {
			BoobBot.log.error("rip "+ e);
			message.getTextChannel().sendMessage(Formats.error("rip some error, press f")).queue();
		}
	}
}