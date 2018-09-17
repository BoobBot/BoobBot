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
        name = "rt",
        triggers = {"rt", "redtube"},
        attributes = {@CommandAttribute(key = "dm"), @CommandAttribute(key = "nsfw")}, description = "RedTube video search"
)

public class RedTubeCommand implements Command {
    @Override
    public void execute(Message trigger, String args) {
        TextChannel ch = trigger.getTextChannel();
        String tag;
        String[] arg = args.trim().split(" ");
        if (args.length() == 0) {
            ch.sendMessage(
                    new EmbedBuilder()
                            .setAuthor(
                                    trigger.getJDA().getSelfUser().getName(),
                                    trigger.getJDA().asBot().getInviteUrl(Permission.ADMINISTRATOR),
                                    trigger.getJDA().getSelfUser().getEffectiveAvatarUrl())
                            .setColor(Colors.getEffectiveColor(trigger))
                            .addField(
                                    Formats.error("Missing args"),
                                    "bbredtube <tag> or random",
                                    false)
                            .build())
                    .queue();
            return;
        }
        if (arg[0].toLowerCase().equals("random")) {
            Random random = new Random();
            int rnd = random.nextInt(Formats.tag.length);
            tag = Formats.tag[rnd];
        } else {
            tag = arg[0];
        }
        try {
            JSONObject rt = RTapi.makeReqAndGetRes(tag);
            if (rt.has("count") & (int) rt.get("count") > 0) {
                JSONObject video = rt.getJSONArray("videos").getJSONObject(0).getJSONObject("video");
                String duration = video.getString("duration");
                String views = video.getString("views");
                String rating = video.getString("rating");
                String ratings = video.getString("ratings");
                String title = video.getString("title");
                String url = video.getString("url");
                String embed_url = video.getString("embed_url");
                String thumb = video.getString("thumb");
                String publish_date = video.getString("publish_date");
                trigger
                        .getChannel()
                        .sendMessage(
                                new EmbedBuilder()
                                        .setAuthor("RedTube video search",
                                                embed_url,
                                                "https://cdn.discordapp.com/attachments/440667148315262978/490353839577497623/rt.png")
                                        .setTitle(title, url)
                                        .setDescription("RedTube video search")
                                        .setColor(Colors.getEffectiveColor(trigger))
                                        .setImage(thumb)
                                        .addField("Video stats",
                                                MessageFormat.format(
                                                        "Views: {0}\nRating: {1}\nRatings: {2}\nDuration: {3}\nDate published: {4}\nUrl: {5}",
                                                        views, rating, ratings, duration, publish_date, url), false)
                                        .setFooter(
                                                MessageFormat.format(
                                                        "Requested by {0} | {1}", trigger.getAuthor().getName(), Misc.now()),
                                                trigger.getAuthor().getEffectiveAvatarUrl())
                                        .build())
                        .queue();
            } else {
                trigger.getTextChannel().sendMessage(
                        new EmbedBuilder().setDescription(Formats.error("No videos found"))
                                .build()
                ).queue();
            }
        } catch (Exception e) {
            trigger.getChannel().sendMessage(Formats.error("Some kinda of redtube api error wtf")).queue();
            BoobBot.log.error("command broken? ", e);
        }
    }
}
