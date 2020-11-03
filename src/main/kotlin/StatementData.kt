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
                calculator.amount(),
                calculator.volumeCredits(),
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
