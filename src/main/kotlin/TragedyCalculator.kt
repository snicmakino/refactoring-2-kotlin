import json.Performance
import json.Play
import kotlin.math.floor

class TragedyCalculator(override val aPerformance: Performance, override val play: Play) :
    PerformanceCalculator(aPerformance, play) {

    override fun amount(): Int {
        var result = 40000
        if (aPerformance.audience > 30) {
            result += 1000 * (aPerformance.audience - 30)
        }
        return result / 100
    }
}
