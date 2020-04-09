package bot.boobbot.commands.`fun`

import bot.boobbot.BoobBot
import bot.boobbot.flight.AsyncCommand
import bot.boobbot.flight.Category
import bot.boobbot.flight.CommandProperties
import bot.boobbot.flight.Context
import bot.boobbot.misc.Formats.countryCodeToEmote
import bot.boobbot.misc.json
import org.json.JSONObject
import java.time.OffsetDateTime
import java.time.format.DateTimeFormatter

@CommandProperties(description = "info", category = Category.FUN)
class Covid : AsyncCommand {

    override suspend fun executeAsync(ctx: Context) {
        val res = BoobBot.requestUtil
            .get("https://api.covid19api.com/summary")
            .await()
            ?.json()
            ?: return ctx.send("rip some error, press f")

        val g = res.getJSONObject("Global")

        if (ctx.args.isEmpty() || ctx.args[0].isEmpty()) {
            return sendGlobalStats(ctx, g)
        }

        val countryCode = ctx.args[0].toLowerCase().replace("uk", "gb")
        val j = res.getJSONArray("Countries")
            .firstOrNull { (it as JSONObject).getString("CountryCode").toLowerCase() == countryCode }
            ?: return sendGlobalStats(ctx, g, true)

        j as JSONObject

        val out = buildString {
            appendln("New Confirmed: ${j["NewConfirmed"]}")
            appendln("Total Confirmed: ${j["TotalConfirmed"]}")
            appendln("New Deaths: ${j["NewDeaths"]}")
            appendln("Total Deaths: ${j["TotalDeaths"]}")
            appendln("New Recovered: ${j["NewRecovered"]}")
            appendln("Total Recovered: ${j["TotalRecovered"]}")

            val date = j.getString("Date")
            val parse = OffsetDateTime.parse(date, utcParser)
            val formatted = parse.format(dateFormatter)

            appendln("Last Update: $formatted")
        }

        ctx.embed {
            setTitle("${countryCodeToEmote(countryCode)} ${countryCode.toUpperCase()} Metrics")
            setDescription(out)
        }
    }

    fun sendGlobalStats(ctx: Context, data: JSONObject, wasInvalid: Boolean = false) {
        val out = buildString {
            if (wasInvalid) {
                appendln("You provided an invalid country code, so here's global stats.\n")
            }

            appendln("New Confirmed: ${data["NewConfirmed"]}")
            appendln("New Deaths: ${data["NewDeaths"]}")
            appendln("Total Deaths: ${data["TotalDeaths"]}")
            appendln("New Recovered: ${data["NewRecovered"]}")
            appendln("Total Recovered: ${data["TotalRecovered"]}")
        }

        return ctx.embed {
            setTitle("\uD83C\uDF0E Global Metrics")
            setDescription(out)
            setFooter("Try it with a country code, example: bbcovid us")
        }
    }

    companion object {
        private val utcParser = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ssX")
        private val dateFormatter = DateTimeFormatter.ofPattern("HH:mm:ss dd-MMM-yyyy")
    }

}