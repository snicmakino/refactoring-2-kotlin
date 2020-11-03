import json.Performance
import json.Play
import kotlin.math.floor

class ComedyCalculator(override val aPerformance: Performance, override val play: Play) :
    PerformanceCalculator(aPerformance, play) {

    override fun amount(): Int {
        var result = 30000
        if (aPerformance.audience > 20) {
            result += 10000 + 500 * (aPerformance.audience - 20)
        }
        result += 300 * aPerformance.audience
        return result / 100
    }

    override fun volumeCredits(): Double {
        return super.volumeCredits() + floor(aPerformance.audience.toDouble()) / 5
    }
}
