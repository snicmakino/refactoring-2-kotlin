import kotlin.math.floor

fun statement(invoice: Map<String, *>, plays: Map<String, *>): String {
    var totalAmount = 0
    var volumeCredits = 0f
    var result = "Statement for %s\n".format(invoice["customer"])

//    val format =
    for (perf in invoice["performances"] as List<Map<String, *>>) {
        val play = plays[perf["playId"]] as Map<String, *>
        var thisAmount = 0


        when (play["type"]) {
            "tragedy" -> {
                thisAmount = 40000
                if (perf["audience"] as Int > 30) {
                    thisAmount += 1000 * (perf["audience"] as Int - 30)
                }
            }
            "comedy" -> {
                thisAmount = 30000
                if (perf["audience"] as Int > 20) {
                    thisAmount += 10000 + 500 * (perf["audience"] as Int - 20)
                }
                thisAmount += 300 * perf["audience"] as Int
            }
            else -> {
                throw Exception("unknown type: %s".format(play["type"]))
            }
        }

        // ボリューム特典のポイントを加算
        volumeCredits += listOf(perf["audience"] as Int - 30, 0).maxOrNull() ?: 0
        // 喜劇のときは10人に付き、さらにポイントを加算
        if ("comedy" == play["type"]) volumeCredits += floor(perf["audience"] as Float) / 5
        // 注文の内訳を出力
        result += " %s: %s (%s seats)\n".format(play["name"], formatUsd(thisAmount / 100), perf["audience"])
        totalAmount += thisAmount
    }

    result += "Amount owed is %s\n".format(formatUsd(totalAmount / 100))
    result += "You earned %d %s\n".format(volumeCredits)
    return result
}

fun formatUsd(amount: Int): String {
    return "\$%9.2f".format(amount.toFloat())
}
