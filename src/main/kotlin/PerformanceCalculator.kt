import json.Performance
import json.Play

abstract class PerformanceCalculator(open val aPerformance: Performance, open val play: Play) {

    abstract fun amount(): Int

    open fun volumeCredits(): Double {
        return (listOf(aPerformance.audience - 30, 0).maxOrNull() ?: 0).toDouble()
    }
}
