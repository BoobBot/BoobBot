package bot.boobbot.audio.sources.pornhub


object Utils {
    //private val assignmentPattern = "(var.+?media_0[^<]+)".toPattern()
    //private val assignmentPattern = "(var.+?media_0.+)".toPattern()

    private val jsVarPattern = "(var\\s+(?:media|quality|qualityItems)_.+)".toPattern()
    private val tvMediaStringPattern = "(var.+?mediastring[^<]+)".toPattern()
    private val flashVarRegex = "var flashvars_\\d+ = (\\{.+})".toPattern()
    private val cleanRegex = "/\\*(?:(?!\\*/).)*?\\*/".toRegex()
    private val cleanVarRegex = "var\\s+".toRegex()

    fun extractMediaString(page: String): String {
        val vars = hashMapOf<String, String>()
        val assignments = extractAssignments(page)

        for (assignment in assignments) {
            val trimmed = assignment.trim()

            if (trimmed.isEmpty()) {
                continue
            }

            val noVar = trimmed.replace(cleanVarRegex, "")
            val (name, value) = noVar.split('=', limit = 2)

            vars[name] = parseSegment(value, vars)
        }


        val formats = vars.filter { it.key.startsWith("media") || it.key.startsWith("quality_") }

        return formats["quality_720p"]
            ?: formats["quality_480p"]
            ?: formats["quality_240p"]
            ?: throw IllegalStateException("No formats detected")
    }

    private fun extractAssignments(script: String): List<String> {
        val formats = jsVarPattern.matcher(script)

        if (formats.find()) {
            return formats.group(1).split(';')
        }

        val assignments = tvMediaStringPattern.matcher(script)

        if (!assignments.find()) {
            throw IllegalStateException("No assignments or formats found within the script!")
        }

        return assignments.group(1).split(';')
    }

    private fun extractFlashvars(script: String): String? {
        val flashVars = flashVarRegex.matcher(script)

        if (flashVars.find()) {
            return flashVars.group(1)
        }

        return null
    }

    private fun parseSegment(segment: String, v: HashMap<String, String>): String {
        val cleaned = segment.replace(cleanRegex, "").trim()

        if (cleaned.contains('+')) {
            val subSegments = cleaned.split('+')
            return subSegments.joinToString("") { parseSegment(it, v) }
        }

        return v[cleaned]
            ?: cleaned.replace("'", "").replace("\"", "")
    }
}
