import json.Invoice
import json.Performance
import json.Play
import kotlin.math.floor

fun statement(invoice: Invoice, plays: Map<String, Play>): String {
    var totalAmount = 0
    var volumeCredits = 0.0
    var result = "Statement for %s\n".format(invoice.customer)

    val formatUsd = { amount: Int -> "\$%.2f".format(amount.toFloat()) }

    for (perf in invoice.performances) {
        val play = plays[perf.playId] ?: error("playId not found")
        val thisAmount = amountFor(perf, play)

        // ボリューム特典のポイントを加算
        volumeCredits += listOf(perf.audience - 30, 0).maxOrNull() ?: 0
        // 喜劇のときは10人に付き、さらにポイントを加算
        if ("comedy" == play.type) volumeCredits += floor(perf.audience.toDouble()) / 5
        // 注文の内訳を出力
        result += " %s: %s (%s seats)\n".format(play.name, formatUsd(thisAmount / 100), perf.audience)
        totalAmount += thisAmount
    }

    result += "Amount owed is %s\n".format(formatUsd(totalAmount / 100))
    result += "You earned %.0f credits\n".format(volumeCredits)
    return result
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
    return result
}
