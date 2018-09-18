package bot.boobbot.flight;

public interface Command {

    public void execute(Context ctx);

    default public String getName() {
        return this.getClass().getSimpleName().toLowerCase();
    }

    default public CommandProperties getProperties() {
        return this.getClass().getAnnotation(CommandProperties.class);
    }

}
