package bot.boobbot.models;

import bot.boobbot.flight.Command;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

public abstract class BbApiCommand implements Command {

    private String category;

    public BbApiCommand(String category) {
        this.category = category;
    }

    @Override
    public void execute(MessageReceivedEvent event, String[] args) {
        // TODO: Permission checks, API request, send result.
        // key: url
    }
}
