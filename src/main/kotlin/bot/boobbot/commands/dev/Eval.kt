package bot.boobbot.commands.dev

import bot.boobbot.BoobBot
import bot.boobbot.flight.Category
import bot.boobbot.flight.Command
import bot.boobbot.flight.CommandProperties
import bot.boobbot.flight.Context
import bot.boobbot.misc.Colors
import bot.boobbot.misc.Constants
import bot.boobbot.misc.Formats
import groovy.lang.Binding
import groovy.lang.GroovyShell
import net.dv8tion.jda.core.entities.Message
import net.dv8tion.jda.core.requests.Route.Misc
import java.util.concurrent.Executors


@Suppress("LABEL_NAME_CLASH")
@CommandProperties(description = "Eval", category = Category.DEV, developerOnly = true)
class Eval : Command {
    private val evalThreadGroup = ThreadGroup("Eval Thread Pool")
    private val pool = Executors.newCachedThreadPool { r -> Thread(evalThreadGroup, r, evalThreadGroup.name + evalThreadGroup.activeCount()) }


    override fun execute(ctx: Context) {
        evalThreadGroup.maxPriority = Thread.MIN_PRIORITY
        val shell = this.createShell(ctx.message)
        val code = ctx.args.joinToString(separator = " ") { it }
        pool.execute {
            try {
                val result = shell.evaluate(code)
                if (result == null) {
                    ctx.send("`null` **Executed successfully**")
                    return@execute
                }
                ctx.send("```groovy\n" + Formats.clean(result.toString()) + "```")
            } catch (ex: Exception) {
                ctx.send("\u274C **Error: **\n**$ex**")
            }
        }
    }

    private fun createShell(e: Message): GroovyShell {
        val binding = Binding()
        binding.setVariable("log", BoobBot.log)
        binding.setVariable("sm", e.jda.asBot().shardManager)
        binding.setVariable("api", e.jda)
        binding.setVariable("getEffectiveColor", Colors.getEffectiveColor(e))
        binding.setVariable("jda", e.jda)
        binding.setVariable("channel", e.channel)
        binding.setVariable("author", e.author)
        binding.setVariable("message", e)
        binding.setVariable("Misc", Misc::class.java)
        binding.setVariable("msg", e)
        binding.setVariable("owner", Constants.OWNERS)
        binding.setVariable("guild", e.guild)
        return GroovyShell(binding)
    }

}