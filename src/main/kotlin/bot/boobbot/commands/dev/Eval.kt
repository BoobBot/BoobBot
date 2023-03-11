package bot.boobbot.commands.dev

import bot.boobbot.BoobBot
import bot.boobbot.entities.framework.Category
import bot.boobbot.entities.framework.Context
import bot.boobbot.entities.framework.interfaces.Command
import bot.boobbot.entities.framework.annotations.CommandProperties
import bot.boobbot.entities.framework.annotations.Option
import bot.boobbot.utils.Colors
import bot.boobbot.utils.Utils
import org.jetbrains.kotlin.script.jsr223.KotlinJsr223JvmLocalScriptEngineFactory


@CommandProperties(description = "Evaluate code.", category = Category.DEV, developerOnly = true, groupByCategory = true)
@Option(name = "code", description = "Self-explanatory.")
class Eval : Command {

    private val engine = KotlinJsr223JvmLocalScriptEngineFactory().scriptEngine
    private val evalThread = Thread("fuck")

    init {
        evalThread.priority = Thread.MIN_PRIORITY
    }

    override fun execute(ctx: Context) {
        val code = ctx.options.getOptionStringOrGather("code")
            ?: return ctx.reply("wtf? what do you want to evaluate?")

        val imports = code.lines()
            .takeWhile { it.startsWith("import ") }
            .joinToString("\n", postfix = "\n")

        val stripped = code.replace("^```\\w+".toRegex(), "")
            .removeSuffix("```")
            .lines()
            .dropWhile { it.startsWith("import ") }
            .joinToString("\n")

        val bindings = mapOf(
            "bb" to BoobBot,
            "ctx" to ctx,
            "jda" to ctx.jda,
            "sm" to BoobBot.shardManager,
            "colors" to Colors,
            "utils" to Utils,
            "self" to BoobBot.database.getUser(ctx.user.id)
        )

        val bindString = bindings.map { "val ${it.key} = bindings[\"${it.key}\"] as ${it.value.javaClass.kotlin.qualifiedName}" }
                .joinToString("\n")
        val bind = engine.createBindings()
        bind.putAll(bindings)

        evalThread.run {
            try {
                val result = engine.eval("$imports$bindString\n$stripped", bind)
                    ?: return ctx.reply("<null>")
                ctx.channel.sendMessage("```\n$result```").queue(null) {
                    ctx.channel.sendMessage("Response Error\n```\n$it```").queue()
                }
            } catch (e: Exception) {
                val error = e.localizedMessage.split("\n").first()
                ctx.channel.sendMessage("Engine Error\n```\n$error```").queue(null) {
                    ctx.channel.sendMessage("Response Error\n```\n$it```").queue(null, { println("fuck") })
                }
            }
        }
    }
}
