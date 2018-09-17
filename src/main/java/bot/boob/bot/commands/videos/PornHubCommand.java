package bot.boob.bot.commands.videos;


import com.github.rainestormee.jdacommand.Command;
import com.github.rainestormee.jdacommand.CommandAttribute;
import com.github.rainestormee.jdacommand.CommandDescription;
import net.dv8tion.jda.core.entities.Message;


@CommandDescription(
        name = "ph",
        triggers = {"ph", "pornhub"},
        attributes = {@CommandAttribute(key = "dm"), @CommandAttribute(key = "nsfw")},
        description = "PornHib video search"
)
public class PornHubCommand implements Command {
    @Override
    public void execute(Message trigger, String args) {

    }
}
