package bot.boob.bot.commands.owner;


import bot.boob.bot.commons.Colors;
import bot.boob.bot.commons.Constants;
import bot.boob.bot.commons.Formats;
import bot.boob.bot.commons.Misc;
import com.github.rainestormee.jdacommand.Command;
import com.github.rainestormee.jdacommand.CommandAttribute;
import com.github.rainestormee.jdacommand.CommandDescription;

import groovy.lang.Binding;
import groovy.lang.GroovyShell;
import net.dv8tion.jda.core.entities.Message;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

@CommandDescription(
        name = "eval",
        triggers = {"eval", "debug"},
        attributes = {
                @CommandAttribute(key = "OwnerOnly"),
                @CommandAttribute(key = "dm"),
        },
        description = "Eval duh"
)
public class EvalCommand implements Command {
    private static final ThreadGroup EVALS = new ThreadGroup("Eval Thread Pool");
    private static final Executor POOL =
            Executors.newCachedThreadPool(
                    r -> new Thread(EVALS, r, EVALS.getName() + EVALS.activeCount()));

    static {
        // eval doesn't need to hog any resources, when it finishes it does
        EVALS.setMaxPriority(Thread.MIN_PRIORITY);
    }

    @Override
    public void execute(Message trigger, String args) {
        GroovyShell shell = this.createShell(trigger);
        String code = String.join(" ", args);
        POOL.execute(
                () -> {
                    try {
                        Object result = shell.evaluate(code);
                        if (result == null) {
                            trigger.getChannel().sendMessage("`null` **Executed successfully**").queue();
                            return;
                        }
                        trigger
                                .getChannel()
                                .sendMessage("```groovy\n" + Formats.clean(result.toString()) + "```")
                                .queue();
                    } catch (Exception ex) {
                        trigger.getChannel().sendMessage("\u274C **Error: **\n**" + ex + "**").queue();
                    }
                });
    }

    private GroovyShell createShell(Message e) {
        Binding binding = new Binding();
        binding.setVariable("sm", e.getJDA().asBot().getShardManager());
        binding.setVariable("api", e.getJDA());
        binding.setVariable("getEffectiveColor", Colors.getEffectiveColor(e));
        binding.setVariable("jda", e.getJDA());
        binding.setVariable("channel", e.getChannel());
        binding.setVariable("author", e.getAuthor());
        binding.setVariable("message", e);
        binding.setVariable("Misc", Misc.class);
        binding.setVariable("msg", e);
        binding.setVariable("owner", Constants.OWNERS);
        binding.setVariable("guild", e.getGuild());
        return new GroovyShell(binding);
    }
}
