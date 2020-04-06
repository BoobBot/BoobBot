package bot.boobbot.commands.`fun`

import bot.boobbot.BoobBot
import bot.boobbot.flight.AsyncCommand
import bot.boobbot.flight.Category
import bot.boobbot.flight.CommandProperties
import bot.boobbot.flight.Context
import bot.boobbot.misc.Formats.countryCodeToEmote
import bot.boobbot.misc.json
import org.json.JSONObject

@CommandProperties(description = "info", category = Category.FUN)
class Covid : AsyncCommand {

    override suspend fun executeAsync(ctx: Context) {
        val msg = StringBuilder()
        val res = BoobBot.requestUtil
            .get("https://api.covid19api.com/summary")
            .await()
            ?: return ctx.send("rip some error, press f")
        val body = res.json() ?: return ctx.send("rip some error, press f")
        val g = body.getJSONObject("Global")
        if (ctx.args.isEmpty() || ctx.args[0].isEmpty()) {
            msg.append("\nNew Confirmed: " + g.get("NewConfirmed"))
            msg.append("\nTotal Confirmed: " + g.get("TotalConfirmed"))
            msg.append("\nNew Deaths: " + g.get("NewDeaths"))
            msg.append("\nTotal Deaths: " + g.get("TotalDeaths"))
            msg.append("\nNew Recovered: " + g.get("NewRecovered"))
            msg.append("\nTotal Recovered: " + g.get("TotalRecovered"))
            return ctx.embed {
                setTitle("\uD83C\uDF0E Global Metrics")
                setDescription(msg)
                setFooter("Try it with a Country Code ie: bbcovid us")
            }
        }
        val l = body.getJSONArray("Countries")
        print(ctx.args[0])
        l.forEach { t ->
            val j = t as JSONObject
            val c = j.getString("CountryCode")
            if (c.toLowerCase() == ctx.args[0].toLowerCase()) {
                val cMsg = StringBuilder()
                cMsg.append("\nNew Confirmed: " + j.get("NewConfirmed"))
                cMsg.append("\nTotal Confirmed: " + j.get("TotalConfirmed"))
                cMsg.append("\nNew Deaths: " + j.get("NewDeaths"))
                cMsg.append("\nTotal Deaths: " + j.get("TotalDeaths"))
                cMsg.append("\nNew Recovered: " + j.get("NewRecovered"))
                cMsg.append("\nTotal Recovered: " + j.get("TotalRecovered"))
                cMsg.append("\nLast Update: " + j.get("Date"))
                return ctx.embed {
                    setTitle("${countryCodeToEmote(c)} $c Metrics")
                    setDescription(cMsg)
                }

            }
        }
        ctx.send("Wrong Country Code or missing data, Here are the global stats")
        msg.append("\nNew Confirmed: " + g.get("NewConfirmed"))
        msg.append("\nTotal Confirmed: " + g.get("TotalConfirmed"))
        msg.append("\nNew Deaths: " + g.get("NewDeaths"))
        msg.append("\nTotal Deaths: " + g.get("TotalDeaths"))
        msg.append("\nNew Recovered: " + g.get("NewRecovered"))
        msg.append("\nTotal Recovered: " + g.get("TotalRecovered"))
        return ctx.embed {
            setTitle("\uD83C\uDF0E Global Metrics")
            setDescription(msg)
            setFooter("Try it with a Country Code ie: bbcovid us")
        }
    }

}