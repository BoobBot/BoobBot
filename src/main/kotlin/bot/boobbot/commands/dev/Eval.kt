package bot.boobbot.commands.dev

import bot.boobbot.BoobBot
import bot.boobbot.flight.Category
import bot.boobbot.flight.Command
import bot.boobbot.flight.CommandProperties
import bot.boobbot.flight.Context
import bot.boobbot.misc.Colors
import bot.boobbot.misc.Utils
import org.jetbrains.kotlin.script.jsr223.KotlinJsr223JvmLocalScriptEngineFactory


@CommandProperties(description = "Eval", category = Category.DEV, developerOnly = true)
class Eval : Command {

    private val engine = KotlinJsr223JvmLocalScriptEngineFactory().scriptEngine
    private val evalThread = Thread("fuck")

    init {
        evalThread.priority = Thread.MIN_PRIORITY
    }

    override fun execute(ctx: Context) {
        val bindings = mapOf(
            "bb" to BoobBot,
            "ctx" to ctx,
            "jda" to ctx.jda,
            "sm" to BoobBot.shardManager,
            "colors" to Colors,
            "utils" to Utils,
            "self" to BoobBot.database.getUser(ctx.author.id)
        )

        val bindString =
            bindings.map { "val ${it.key} = bindings[\"${it.key}\"] as ${it.value.javaClass.kotlin.qualifiedName}" }
                .joinToString("\n")
        val bind = engine.createBindings()
        bind.putAll(bindings)

        evalThread.run {
            try {
                val result = engine.eval("$bindString\n${ctx.args.joinToString(" ")}", bind)
                ctx.channel.sendMessage("```\n$result```").queue(null) {
                    ctx.channel.sendMessage("Response Error\n```\n$it```").queue()
                }
            } catch (e: Exception) {
                val error = e.localizedMessage.split("\n").first()
                ctx.channel.sendMessage("Engine Error\n```\n$error```").queue(null) {
                    ctx.channel.sendMessage("Response Error\n```\n$it```").queue(null, { println("fuc") })
                }
            }
        }
    }
}
