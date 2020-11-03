import json.Performance
import json.Play

class PerformanceCalculator(val aPerformance: Performance, val play: Play) {

    fun amount(): Int {
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
}
