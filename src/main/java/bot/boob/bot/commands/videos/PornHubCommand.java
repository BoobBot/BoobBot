package bot.boob.bot.commands.videos;


import bot.boob.bot.BoobBot;
import bot.boob.bot.commons.Colors;
import bot.boob.bot.commons.Formats;
import bot.boob.bot.commons.Misc;
import bot.boob.bot.commons.apis.RTapi;
import com.github.rainestormee.jdacommand.Command;
import com.github.rainestormee.jdacommand.CommandAttribute;
import com.github.rainestormee.jdacommand.CommandDescription;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.TextChannel;
import org.json.JSONObject;

import java.text.MessageFormat;
import java.util.Random;


@CommandDescription(
        name = "ph",
        triggers = {"ph", "pornhub"},
        attributes = {@CommandAttribute(key = "dm"), @CommandAttribute(key = "nsfw")},
        description = "PornHib video search"
)
public class PornHubCommand implements Command {
    @Override
    public void execute(Message trigger, String args) {

}}
