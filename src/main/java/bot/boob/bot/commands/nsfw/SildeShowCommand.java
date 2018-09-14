package bot.boob.bot.commands.nsfw;


import bot.boob.bot.BoobBot;
import bot.boob.bot.commons.Colors;
import bot.boob.bot.commons.Formats;
import bot.boob.bot.commons.Misc;

import com.github.rainestormee.jdacommand.Command;
import com.github.rainestormee.jdacommand.CommandAttribute;
import com.github.rainestormee.jdacommand.CommandDescription;
import com.jagrosh.jdautilities.menu.Slideshow;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.Message;

import java.text.MessageFormat;
import java.util.concurrent.TimeUnit;

import static bot.boob.bot.commons.apis.bbapi.getThigh;


@CommandDescription(
        name = "thighs",
        triggers = {"thighs"},
        attributes = {@CommandAttribute(key = "nsfw")},
        description = "thighs!"
)
public class SildeShowCommand implements Command {
  private Slideshow.Builder sbuilder =
          new Slideshow.Builder().setEventWaiter(BoobBot.waiter).setTimeout(1, TimeUnit.MINUTES);

  @Override
  public void execute(Message message, String args) {

    if (!message.getTextChannel().isNSFW()) {
      message
              .getChannel()
              .sendMessage(
                      new EmbedBuilder()
                              .setAuthor(
                                      message.getJDA().getSelfUser().getName(),
                                      message.getJDA().asBot().getInviteUrl(Permission.ADMINISTRATOR),
                                      message.getJDA().getSelfUser().getEffectiveAvatarUrl())
                              .setColor(Colors.getEffectiveColor(message))
                              .setDescription(
                                      "This isn't a nsfw channel whore.")
                              .build())
              .queue();
      return;
    }
    StringBuilder urls = new StringBuilder();
    message.delete().queue();
    for (int i = 0; i < 20; ++i) {
      try {
        urls.append(getThigh());
        urls.append(",");
      } catch (Exception e) {
        e.printStackTrace();
      }
    }
    Slideshow s =
            sbuilder
                    .setColor(Colors.getEffectiveColor(message))
                    .setText(Formats.LEWD_EMOTE)
                    .setUrls(urls.toString().split(","))
                    .setFinalAction(
                            msg -> {
                              msg.clearReactions().queue();
                              try {
                                msg.editMessage(
                                        new EmbedBuilder()
                                                .setImage(getThigh())
                                                .setColor(Colors.getEffectiveColor(msg))
                                                .setFooter(
                                                        MessageFormat.format(
                                                                "Requested by {0} | {1}", message.getAuthor().getName(), Misc.now()),
                                                        message.getAuthor().getEffectiveAvatarUrl())
                                                .build())
                                        .queue();
                              } catch (Exception e) {
                                e.printStackTrace();
                              }
                            })
                    .build();
    s.display(message.getChannel());
  }
}
