package bot.boobbot.commands.mod


import bot.boobbot.flight.Command
import bot.boobbot.flight.CommandProperties
import bot.boobbot.flight.Context

@CommandProperties(description = "kick a asshat", donorOnly = true, guildOnly = true)
class Kick : Command {

    override fun execute(ctx: Context) {
        var reason = ctx.args.drop(1).joinToString(separator = " ")
        if (reason.isBlank() ) {
            reason = "No reason was given"
        }

        val member = ctx.message.mentionedMembers.firstOrNull()
            ?: return ctx.send("How in the fuck would i know who you want to kick if you don't mention a user?")

        if (member.idLong == ctx.author.idLong) {
            return ctx.send("You must be special if you're really trying to kick yourself.")
        }

        if (!ctx.member!!.canInteract(member)) {
            return ctx.send("You dont have permission to do that, fuck off")
        }

        if (member.idLong == ctx.selfMember!!.idLong) {
            return ctx.send("Don't you fucking touch me whore, I will end you.")
        }

        if (!ctx.selfMember.canInteract(member)) {
            return ctx.send("I dont have permission to do that, Fix it or fuck off")
        }

        member.kick("Kicked by: (${ctx.author.name} [${ctx.author.idLong}]) For: $reason").queue({ ctx.send("done, good riddance stupid bitch") }, { ctx.send("what the fuck i couldn't kick?") })



    }
}