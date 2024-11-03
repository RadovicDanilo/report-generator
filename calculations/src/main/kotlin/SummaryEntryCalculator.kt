object SummaryEntryCalculator {
    fun calculateSummaryEntry(
        values: Array<Double>,
        calcType: SummaryCalculationType,
        condition: (Double) -> Boolean = { true }
    ): Any {
        return when (calcType) {
            SummaryCalculationType.SUM -> sum(values, condition)
            SummaryCalculationType.AVERAGE -> average(values, condition)
            SummaryCalculationType.COUNT -> count(values, condition)
        }
    }

    private fun sum(values: Array<Double>, condition: (Double) -> Boolean = { true }): Double {
        return values.filter(condition).sum()
    }

    private fun average(values: Array<Double>, condition: (Double) -> Boolean = { true }): Double {
        val filteredValues = values.filter(condition)
        return if (filteredValues.isNotEmpty()) filteredValues.average() else 0.0
    }

    private fun count(values: Array<Double>, condition: (Double) -> Boolean = { true }): Int {
        return values.count(condition)
    }

    private fun count(values: Array<String>, condition: (String) -> Boolean = { true }): Int {
        return values.count(condition)
    }
}
