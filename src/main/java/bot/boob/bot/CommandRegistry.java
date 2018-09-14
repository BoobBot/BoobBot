package bot.boob.bot;

import bot.boob.bot.commands.bot.*;
import bot.boob.bot.commands.nsfw.*;
import bot.boob.bot.commands.owner.EvalCommand;
import com.github.rainestormee.jdacommand.Command;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

class CommandRegistry {
    private static final Set<Command> commands = new HashSet<>();

    CommandRegistry() {
        register(
                new ThighCommand(),
                new SildeShowCommand(),
                new HelpCommand(),
                new PingCommand(),
                new CleanCommand(),
                new InviteCommand(),
                new EvalCommand(),
                new NsfwToggleCommand(),
                new SendCommand(),
                new FourkCommand(),
                new AssCommand(),
                new BlackCommand(),
                new BoobsCommand(),
                new CollaredCommand(),
                new DPCommand(),
                new FutaCommand(),
                new GifCommand(),
                new HentaiCommand(),
                new PeggedCommand(),
                new PHGifCommand(),
                new PussyCommand(),
                new TinyCommand(),
                new TrapsCommand(),
                new TattooCommand(),
                new YaoiCommand(),
                new AnalCommand(),
                new BDSMCommand(),
                new BlowJobCommand(),
                new BottomlessCommand(),
                new CumSlutsCommand(),
                new EasterCommand(),
                new GayCommand(),
                new GroupCommand(),
                new LesbiansCommand(),
                new PAWGCommand(),
                new DickCommand(),
                new PokeCommand(),
                new RealCommand(),
                new ToysCommand(),
                new VDayCommand(),
                new XmasCommand()
        );
    }
    private void register(Command... cmds) {
        commands.addAll(Arrays.asList(cmds));
    }

    Set<Command> getCommands() {
        return commands;
    }
}
