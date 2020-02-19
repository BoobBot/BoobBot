package bot.boobbot.commands.dev

import bot.boobbot.BoobBot
import bot.boobbot.flight.Category
import bot.boobbot.flight.Command
import bot.boobbot.flight.CommandProperties
import bot.boobbot.flight.Context
import bot.boobbot.misc.Colors
import bot.boobbot.misc.Formats
import bot.boobbot.misc.Formats.progressPercentage
import java.util.function.Consumer
import kotlin.math.pow


@CommandProperties(description = "test", category = Category.DEV, developerOnly = true)
class test : Command {

    override fun execute(ctx: Context) {
//        val g = BoobBot.database.getGuild(ctx.guild!!.id)
//        g!!.dropEnabled = !g.dropEnabled
//        BoobBot.database.setGuild(g)
//        ctx.channel.sendMessage("drop has been set to ${g.dropEnabled}").queue()
//        ctx.author.effectiveAvatarUrl
//        var ss = ""
//        var us = BoobBot.database.getAllUsers()
//        us.sortByDescending { it.experience }
//        us.take(10).forEach(Consumer { it -> ss+="${BoobBot.shardManager.getUserById(it._id)?.asTag}\n${it.experience}\n" })
//        print(us)
//        ctx.channel.sendMessage(ss).queue()
//        val u = BoobBot.database.getUser(ctx.author.id)
//        val e = ((u.level + 1).toDouble() * 10).pow(2.toDouble()).toInt()
//        var s = StringBuilder()
//        s.append("Profile for : ${ctx.author.asMention}\n")
//        s.append("Level: ${u.level}")
//        ctx.channel.sendMessage(
//            "exp: ${u.experience}/$e ${(e - u.experience)} remaining\n${u.level} `${progressPercentage(
//                u.experience,
//                e
//            )}` ${(u.level + 1)}"
//        ).queue()
//        val em = ctx.embed {
//            setAuthor("Profile for : ${ctx.author.asTag}", ctx.author.avatarUrl, ctx.author.avatarUrl)
//            setColor(Colors.getEffectiveColor(ctx.message))
//            addField(
//                Formats.info("**Level**"),
//                "**Current Level**: ${u.level}\n**Next Level**: ${(u.level + 1)} " +
//                        "`${progressPercentage(
//                            u.experience,
//                            e
//                        )}`\n" +
//                        "**Experience**: ${u.experience}/$e\n**Lewd Level**: ${u.lewdLevel}\n" +
//                        "**Lewd Points**: ${u.lewdPoints}\n",
//                false
//            )
//            addField(
//                Formats.info("**Balance Information**"), "" +
//                        "**Current Balance**: ${u.balance}$\n" +
//                        "**Total Assets**: ${(u.balance + u.bankBalance)}$", false
//            )
//
//            addField(
//                Formats.info("**General Information**"),
//                "**Protected**: ${u.protected}\n" +
//                        "**Jailed**: ${u.inJail}\n" +
//                        "**Commands Used**:\nsfw: ${u.commandsUsed}\nnsfw: ${u.nsfwCommandsUsed}\ntotal: ${(u.commandsUsed + u.nsfwCommandsUsed)}\n" +
//                        "**Messages Seen**:\nsfw: ${u.messagesSent}\nnsfw: ${u.nsfwMessagesSent}\ntotal: ${(u.messagesSent + u.nsfwMessagesSent)}\n",
//                false
//            )
//
//
//        }
        var u = BoobBot.database.getUser(ctx.author.id)
        ctx.send(u.toString())
        u.lewdPoints +=20
        ctx.send(u.toString())
        u.save()
        ctx.send(u.toString())
        val u2 =BoobBot.database.getUser(ctx.author.id)
        ctx.send(u2.toString())

    }

}
