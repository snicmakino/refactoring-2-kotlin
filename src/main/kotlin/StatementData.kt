import json.Performance
import json.Play
import kotlin.math.floor

class StatementData(
    _customer: String,
    _performances: List<Performance>,
    plays: Map<String, Play>
) {
    val customer: String = _customer
    val performances: List<PerformanceData>
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

        fun totalAmount(performances: List<PerformanceData>): Int {
            return performances.fold(0) { acc, performance -> acc + performance.amount }
        }

        fun totalVolumeCredits(performances: List<PerformanceData>): Double {
            return performances.fold(0.0) { acc, performance -> acc + performance.volumeCredits }
        }

        performances = _performances.map {
            val calculator = PerformanceCalculator(it, playFor(it))
            PerformanceData(
                calculator.play,
                it.audience,
                amountFor(it, playFor(it)),
                volumeCreditsFor(it, playFor(it))
            )
        }
        totalAmount = totalAmount(performances)
        totalVolumeCredits = totalVolumeCredits(performances)
    }
}

class PerformanceData(
    val play: Play,
    val audience: Int,
    val amount: Int,
    val volumeCredits: Double
)
