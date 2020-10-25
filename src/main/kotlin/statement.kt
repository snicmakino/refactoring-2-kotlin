import json.Invoice
import json.Play
import kotlin.math.floor

fun statement(invoice: Invoice, plays: Map<String, Play>): String {
    var totalAmount = 0
    var volumeCredits = 0f
    var result = "Statement for %s\n".format(invoice.customer)


    for (perf in invoice.performances) {
        val play = plays[perf.playId] ?: error("playId not found")
        var thisAmount: Int


        when (play.type) {
            "tragedy" -> {
                thisAmount = 40000
                if (perf.audience > 30) {
                    thisAmount += 1000 * (perf.audience - 30)
                }
            }
            "comedy" -> {
                thisAmount = 30000
                if (perf.audience > 20) {
                    thisAmount += 10000 + 500 * (perf.audience - 20)
                }
                thisAmount += 300 * perf.audience
            }
            else -> {
                throw Exception("unknown type: %s".format(play.type))
            }
        }

        // ボリューム特典のポイントを加算
        volumeCredits += listOf(perf.audience - 30, 0).maxOrNull() ?: 0
        // 喜劇のときは10人に付き、さらにポイントを加算
        if ("comedy" == play.type) volumeCredits += floor(perf.audience.toFloat()) / 5
        // 注文の内訳を出力
        result += " %s: %s (%s seats)\n".format(play.name, formatUsd(thisAmount / 100), perf.audience)
        totalAmount += thisAmount
    }

    result += "Amount owed is %s\n".format(formatUsd(totalAmount / 100))
    result += "You earned %d %s\n".format(volumeCredits)
    return result
}

fun formatUsd(amount: Int): String {
    return "\$%9.2f".format(amount.toFloat())
}
