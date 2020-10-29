import json.Invoice
import json.Performance
import json.Play
import kotlin.math.floor


fun statement(invoice: Invoice, plays: Map<String, Play>): String {
    val statementData = StatementData(invoice.customer, invoice.performances, plays)
    return renderPlainText(statementData)
}

fun renderPlainText(data: StatementData): String {
    var result = "Statement for %s\n".format(data.customer)
    val usd: (Int) -> String = { "\$%.2f".format(it.toFloat()) }


    fun totalVolumeCredits(): Double {
        var result = 0.0
        for (perf in data.performances) {
            result += perf.volumeCredits
        }
        return result
    }

    fun totalAmount(): Int {
        var result = 0
        for (perf in data.performances) {
            // 注文の内訳を出力
            result += perf.amount
        }
        return result
    }

    for (perf in data.performances) {
        // 注文の内訳を出力
        result += " %s: %s (%s seats)\n".format(perf.play.name, usd(perf.amount / 100), perf.audience)
    }

    result += "Amount owed is %s\n".format(usd(totalAmount() / 100))
    result += "You earned %.0f credits\n".format(totalVolumeCredits())
    return result
}

class StatementData(
    _customer: String,
    _performances: List<Performance>,
    plays: Map<kotlin.String, json.Play>
) {
    val customer: String = _customer
    val performances: List<StatementPerformance>

    private val playFor: (Performance) -> Play = { plays[it.playId] ?: error("playId not found") }
    private val amountFor: (Performance, Play) -> Int = { aPerformance, play ->
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
        result
    }
    private val volumeCreditsFor: (Performance, Play) -> Double = { aPerformance, play ->
        var result = 0.0
        result += listOf(aPerformance.audience - 30, 0).maxOrNull() ?: 0
        if ("comedy" == play.type) result += floor(aPerformance.audience.toDouble()) / 5
        result
    }

    init {
        performances = _performances.map {
            StatementPerformance(
                playFor(it),
                it.audience,
                amountFor(it, playFor(it)),
                volumeCreditsFor(it, playFor(it))
            )
        }
    }

    class StatementPerformance(
        val play: Play,
        val audience: Int,
        val amount: Int,
        val volumeCredits: Double
    )
}
