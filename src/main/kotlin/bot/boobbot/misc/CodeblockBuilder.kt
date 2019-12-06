package bot.boobbot.misc

class CodeblockBuilder(language: String, private val builder: ContentBuilder.() -> Unit) {
    private val stringBuilder = StringBuilder("```").append(language).append("\n")

    fun build(): String {
        ContentBuilder().apply(builder)
        stringBuilder.append("```")
        return stringBuilder.toString()
    }

    inner class ContentBuilder {
        operator fun String.unaryPlus() {
            stringBuilder.append(this).append("\n")
        }

        operator fun String.rangeTo(other: Any) {
            stringBuilder.append(this).append(other).append("\n")
        }
    }
}
