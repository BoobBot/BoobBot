package bot.boob.bot.commons;

import de.androidpit.colorthief.ColorThief;
import net.dv8tion.jda.core.entities.ChannelType;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.User;

import java.awt.*;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class Colors {
    private static final int BLACK = 0xFF000000;
    private static final int DKGRAY = 0xFF444444;
    private static final int GRAY = 0xFF888888;
    private static final int LTGRAY = 0xFFCCCCCC;
    private static final int WHITE = 0xFFFFFFFF;
    private static final int RED = 0xFFFF0000;
    private static final int GREEN = 0xFF00FF00;
    private static final int BLUE = 0xFF0000FF;
    private static final int YELLOW = 0xFFFFFF00;
    private static final int CYAN = 0xFF00FFFF;
    private static final int MAGENTA = 0xFFFF00FF;
    private static final HashMap<String, Integer> sColorNameMap;

    static {
        sColorNameMap = new HashMap<>();
        sColorNameMap.put("black", BLACK);
        sColorNameMap.put("darkgray", DKGRAY);
        sColorNameMap.put("gray", GRAY);
        sColorNameMap.put("lightgray", LTGRAY);
        sColorNameMap.put("white", WHITE);
        sColorNameMap.put("red", RED);
        sColorNameMap.put("green", GREEN);
        sColorNameMap.put("blue", BLUE);
        sColorNameMap.put("yellow", YELLOW);
        sColorNameMap.put("cyan", CYAN);
        sColorNameMap.put("magenta", MAGENTA);
        sColorNameMap.put("aqua", 0xFF00FFFF);
        sColorNameMap.put("fuchsia", 0xFFFF00FF);
        sColorNameMap.put("darkgrey", DKGRAY);
        sColorNameMap.put("grey", GRAY);
        sColorNameMap.put("lightgrey", LTGRAY);
        sColorNameMap.put("lime", 0xFF00FF00);
        sColorNameMap.put("maroon", 0xFF800000);
        sColorNameMap.put("navy", 0xFF000080);
        sColorNameMap.put("olive", 0xFF808000);
        sColorNameMap.put("purple", 0xFF800080);
        sColorNameMap.put("silver", 0xFFC0C0C0);
        sColorNameMap.put("teal", 0xFF008080);
    }

    public static Color getRndColor() {
        Random colorGen = new Random();
        int red = colorGen.nextInt(256);
        int green = colorGen.nextInt(256);
        int blue = colorGen.nextInt(256);
        return new Color(red, green, blue);
    }

    public static Color getDominantColor(User user) {
        int[] rgb = ColorThief.getColor(Misc.getAvatar(user));
        if (rgb != null) {
            return new Color(rgb[0], rgb[1], rgb[2]);
        } else {
            return getRndColor();
        }
    }

    public static Color getEffectiveColor(Message msg) {
        return msg.isFromType(ChannelType.TEXT)
                && msg.getGuild().getMember(msg.getAuthor()).getColor() != null
                ? msg.getGuild().getMember(msg.getAuthor()).getColor()
                : getDominantColor(msg.getAuthor());
    }

    public static Color getEffectiveMemberColor(Member member) {
        return member.getColor() != null ? member.getColor() : getDominantColor(member.getUser());
    }

    public static int parseColor(String colorString) {
        if (colorString.charAt(0) == '#') {
            long color = Long.parseLong(colorString.substring(1), 16);
            if (colorString.length() == 7) {
                color |= 0x00000000ff000000;
            } else if (colorString.length() != 9) {
                throw new IllegalArgumentException("Unknown color");
            }
            return (int) color;
        } else {
            Integer color = sColorNameMap.get(colorString.toLowerCase());
            if (color != null) {
                return color;
            }
        }
        throw new IllegalArgumentException("Unknown color");
    }

    public static Map<Integer, String> randomCodeGenerator(int colorCount) {
        HashMap<Integer, String> hexColorMap = new HashMap<>();
        for (int a = 0; a < colorCount; a++) {
            String code = "" + (int) (Math.random() * 256);
            code = code + code + code;
            int i = Integer.parseInt(code);
            hexColorMap.put(a, Integer.toHexString(0x1000000 | i).substring(1).toUpperCase());
        }
        return hexColorMap;
    }
}
