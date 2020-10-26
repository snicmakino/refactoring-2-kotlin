import json.Invoice
import json.Performance
import json.Play
import kotlin.math.floor


fun statement(invoice: Invoice, plays: Map<String, Play>): String {
    var result = "Statement for %s\n".format(invoice.customer)

    val usd: (Int) -> String = { "\$%.2f".format(it.toFloat()) }
    val playFor: (Performance) -> Play = { plays[it.playId] ?: error("playId not found") }

    fun amountFor(aPerformance: Performance): Int {
        var result: Int
        when (playFor(aPerformance).type) {
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
                throw Exception("unknown type: %s".format(playFor(aPerformance).type))
            }
        }
        return result
    }

    fun volumeCreditsFor(aPerformance: Performance): Double {
        var result = 0.0
        result += listOf(aPerformance.audience - 30, 0).maxOrNull() ?: 0
        if ("comedy" == playFor(aPerformance).type) result += floor(aPerformance.audience.toDouble()) / 5
        return result
    }

    fun totalVolumeCredits(): Double {
        var result = 0.0
        for (perf in invoice.performances) {
            result += volumeCreditsFor(perf)
        }
        return result
    }

    fun totalAmount(): Int {
        var result = 0
        for (perf in invoice.performances) {
            // 注文の内訳を出力
            result += amountFor(perf)
        }
        return result
    }

    for (perf in invoice.performances) {
        // 注文の内訳を出力
        result += " %s: %s (%s seats)\n".format(playFor(perf).name, usd(amountFor(perf) / 100), perf.audience)
    }

    result += "Amount owed is %s\n".format(usd(totalAmount() / 100))
    result += "You earned %.0f credits\n".format(totalVolumeCredits())
    return result
}

