package bot.boobbot.utils

import net.dv8tion.jda.api.entities.Message
import java.awt.Color
import java.util.*

object Colors {
    private const val BLACK = -0x1000000
    private const val DKGRAY = -0xbbbbbc
    private const val GRAY = -0x777778
    private const val LTGRAY = -0x333334
    private const val WHITE = -0x1
    private const val RED = -0x10000
    private const val GREEN = -0xff0100
    private const val BLUE = -0xffff01
    private const val YELLOW = -0x100
    private const val CYAN = -0xff0001
    private const val MAGENTA = -0xff01

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
        sColorNameMap["darkgrey"] = DKGRAY
        sColorNameMap["gray"] = GRAY
        sColorNameMap["grey"] = GRAY
        sColorNameMap["lightgray"] = LTGRAY
        sColorNameMap["lightgrey"] = LTGRAY
        sColorNameMap["white"] = WHITE
        sColorNameMap["red"] = RED
        sColorNameMap["green"] = GREEN
        sColorNameMap["blue"] = BLUE
        sColorNameMap["yellow"] = YELLOW
        sColorNameMap["cyan"] = CYAN
        sColorNameMap["magenta"] = MAGENTA
        sColorNameMap["aqua"] = -0xff0001
        sColorNameMap["fuchsia"] = -0xff01
        sColorNameMap["lime"] = -0xff0100
        sColorNameMap["maroon"] = -0x800000
        sColorNameMap["navy"] = -0xffff80
        sColorNameMap["olive"] = -0x7f8000
        sColorNameMap["purple"] = -0x7fff80
        sColorNameMap["silver"] = -0x3f3f40
        sColorNameMap["teal"] = -0xff7f80
    }

    fun getEffectiveColor(msg: Message): Color = msg.member?.color ?: Color(255, 0, 128)

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
            val color = sColorNameMap[colorString.lowercase()]
            if (color != null) {
                return color
            }
        }
        throw IllegalArgumentException("Unknown color")
    }

    fun randomCodeGenerator(colorCount: Int): Map<Int, String> {
        return (0 until colorCount).associateWith {
            val colorInt = (Math.random() * 0xFFFFFF).toInt()
            Integer.toHexString(colorInt).uppercase()
        }
    }
}
