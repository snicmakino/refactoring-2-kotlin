import json.Invoice
import json.Performance
import json.Play
import kotlin.math.floor


fun statement(invoice: Invoice, plays: Map<String, Play>): String {
    return renderPlainText(StatementData(invoice.customer, invoice.performances, plays))
}

fun renderPlainText(data: StatementData): String {
    var result = "Statement for ${data.customer}\n"

    for (perf in data.performances) {
        // 注文の内訳を出力
        result += " ${perf.play.name}: ${usd(perf.amount)} (${perf.audience} seats)\n"
    }

    result += "Amount owed is ${usd(data.totalAmount)}\n"
    result += "You earned ${"%.0f".format(data.totalVolumeCredits)} credits\n"
    return result
}

fun htmlStatement(invoice: Invoice, plays: Map<String, Play>): String {
    return renderHtml(StatementData(invoice.customer, invoice.performances, plays))
}

fun renderHtml(data: StatementData): String {
    var result = "<h1>Statement for ${data.customer}</h1>\n"
    result += "<table>\n"
    result += "<tr><th>play</th><th>seats</th><th>cost</th></tr>"
    for (perf in data.performances) {
        result += "  <tr><td>${perf.play.name}</td><td>${perf.audience}</td>"
        result += "<td>${usd(perf.amount)}</td></tr>\n"
    }
    result += "</table>\n"
    result += "<p>Amount owed is <em>${usd(data.totalAmount)}</em></p>\n"
    result += "<p>You earned <em>${"%.0f".format(data.totalVolumeCredits)}</em> credits</p>\n"
    return result
}

fun usd(amount: Int): String {
    return "\$%.2f".format(amount.toFloat())
}

class StatementData(
    _customer: String,
    _performances: List<Performance>,
    plays: Map<String, Play>
) {
    val customer: String = _customer
    val performances: List<StatementPerformance>
    val totalAmount: Int
    val totalVolumeCredits: Double

    init {
        fun playFor(it: Performance): Play {
            return plays[it.playId] ?: error("playId not found")
        }

        fun amountFor(aPerformance: Performance, play: Play): Int {
            var result: Int
            when (play.type) {
                "tragedy" -> {
                    result = 40000
                    if (aPerformance.audience > 30) {
                        result += 1000 * (aPerformance.audience - 30)
                    }
                }
                "comedy" -> {
                    result = 30000
                    if (aPerformance.audience > 20) {
                        result += 10000 + 500 * (aPerformance.audience - 20)
                    }
                    result += 300 * aPerformance.audience
                }
                else -> {
                    throw Exception("unknown type: %s".format(play.type))
                }
            }
            return result / 100
        }

        fun volumeCreditsFor(aPerformance: Performance, play: Play): Double {
            var result = 0.0
            result += listOf(aPerformance.audience - 30, 0).maxOrNull() ?: 0
            if ("comedy" == play.type) result += floor(aPerformance.audience.toDouble()) / 5
            return result
        }

        fun totalAmount(performances: List<StatementPerformance>): Int {
            return performances.fold(0) { acc, performance -> acc + performance.amount }
        }

        fun totalVolumeCredits(performances: List<StatementPerformance>): Double {
            return performances.fold(0.0) { acc, performance -> acc + performance.volumeCredits }
        }

        performances = _performances.map {
            StatementPerformance(
                playFor(it),
                it.audience,
                amountFor(it, playFor(it)),
                volumeCreditsFor(it, playFor(it))
            )
        }
        totalAmount = totalAmount(performances)
        totalVolumeCredits = totalVolumeCredits(performances)
    }

    class StatementPerformance(
        val play: Play,
        val audience: Int,
        val amount: Int,
        val volumeCredits: Double
    )
}
