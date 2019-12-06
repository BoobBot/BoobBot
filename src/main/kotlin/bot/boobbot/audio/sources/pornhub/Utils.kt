package bot.boobbot.audio.sources.pornhub

object Utils {

    //private val assignmentPattern = "(var.+?mediastring[^<]+)".toPattern()
    //private val assignmentPattern = "(var.+?media_0[^<]+)".toPattern()
    private val assignmentPattern = "(var.+?media_0.+)".toPattern()
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

        /// media_0, media_1, media_2, media_3, media_4
        return vars["media_0"] ?: throw IllegalStateException("Missing media_0 var")
        // mediastring
    }

    fun extractAssignments(script: String): List<String> {
        val matcher = assignmentPattern.matcher(script)

        if (!matcher.find()) {
            throw IllegalStateException("No assignments found within the script!")
        }

        return matcher.group(1).split(';')
    }

    fun parseSegment(segment: String, v: HashMap<String, String>): String {
        val cleaned = segment.replace(cleanRegex, "").trim()

        if (cleaned.contains('+')) {
            val subSegments = cleaned.split('+')
            return subSegments.joinToString("") { parseSegment(it, v) }
        }

        return v[cleaned]
            ?: cleaned.replace("'", "").replace("\"", "")
    }

}