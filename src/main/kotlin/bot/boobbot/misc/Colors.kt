package bot.boobbot.misc

import de.androidpit.colorthief.ColorThief
import net.dv8tion.jda.api.entities.Message
import net.dv8tion.jda.api.entities.User
import java.awt.Color
import java.util.*

object Colors {
    private val BLACK = -0x1000000
    private val DKGRAY = -0xbbbbbc
    private val GRAY = -0x777778
    private val LTGRAY = -0x333334
    private val WHITE = -0x1
    private val RED = -0x10000
    private val GREEN = -0xff0100
    private val BLUE = -0xffff01
    private val YELLOW = -0x100
    private val CYAN = -0xff0001
    private val MAGENTA = -0xff01

    private val colorGen = Random()
    private val sColorNameMap = HashMap<String, Int>()

    val rndColor: Color
        get() {
            val red = colorGen.nextInt(256)
            val green = colorGen.nextInt(256)
            val blue = colorGen.nextInt(256)
            return Color(red, green, blue)
        }

    init {
        sColorNameMap["black"] = BLACK
        sColorNameMap["darkgray"] = DKGRAY
        sColorNameMap["gray"] = GRAY
        sColorNameMap["lightgray"] = LTGRAY
        sColorNameMap["white"] = WHITE
        sColorNameMap["red"] = RED
        sColorNameMap["green"] = GREEN
        sColorNameMap["blue"] = BLUE
        sColorNameMap["yellow"] = YELLOW
        sColorNameMap["cyan"] = CYAN
        sColorNameMap["magenta"] = MAGENTA
        sColorNameMap["aqua"] = -0xff0001
        sColorNameMap["fuchsia"] = -0xff01
        sColorNameMap["darkgrey"] = DKGRAY
        sColorNameMap["grey"] = GRAY
        sColorNameMap["lightgrey"] = LTGRAY
        sColorNameMap["lime"] = -0xff0100
        sColorNameMap["maroon"] = -0x800000
        sColorNameMap["navy"] = -0xffff80
        sColorNameMap["olive"] = -0x7f8000
        sColorNameMap["purple"] = -0x7fff80
        sColorNameMap["silver"] = -0x3f3f40
        sColorNameMap["teal"] = -0xff7f80
    }

    /**
     * Returns the effective avatar of a user as a 256x256 PNG.
     */
    fun getStaticAvatar(user: User): String {
        return if (user.avatarId == null) {
            "https://cdn.discordapp.com/embed/avatars/${user.discriminator.toInt() % 5}.png"
        } else {
            "https://cdn.discordapp.com/avatars/${user.id}/${user.avatarId}.png?size=256"
        }
    }

    fun getDominantColor(user: User): Color {
        val img = Utils.downloadAvatar(getStaticAvatar(user)) ?: return rndColor
        val rgb = ColorThief.getColor(img)

        return if (rgb != null) {
            Color(rgb[0], rgb[1], rgb[2])
        } else {
            rndColor
        }
    }

    fun getEffectiveColor(msg: Message): Color {
        return msg.member?.color ?: Color(255, 0, 128)
        //return msg.member?.color ?: getDominantColor(msg.author)
    }

    fun parseColor(colorString: String): Int {
        if (colorString[0] == '#') {
            var color = java.lang.Long.parseLong(colorString.substring(1), 16)
            if (colorString.length == 7) {
                color = color or -0x1000000
            } else if (colorString.length != 9) {
                throw IllegalArgumentException("Unknown color")
            }
            return color.toInt()
        } else {
            val color = sColorNameMap[colorString.toLowerCase()]
            if (color != null) {
                return color
            }
        }
        throw IllegalArgumentException("Unknown color")
    }

    fun randomCodeGenerator(colorCount: Int): Map<Int, String> {
        val hexColorMap = HashMap<Int, String>()
        for (a in 0 until colorCount) {
            var code = "" + (Math.random() * 256).toInt()
            code = code + code + code
            val i = Integer.parseInt(code)
            hexColorMap[a] = Integer.toHexString(0x1000000 or i).substring(1).toUpperCase()
        }
        return hexColorMap
    }
}