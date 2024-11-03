object ColumnContentCalculator {
    fun calculateColumnContent(Arrays: Array<Array<Double>>, calculationType: ColumnCalculationType): Array<Double> {
        return when (calculationType) {
            ColumnCalculationType.ADD -> sum(Arrays)
            ColumnCalculationType.SUB -> sub(Arrays)
            ColumnCalculationType.PROD -> prod(Arrays)
            ColumnCalculationType.DIV -> div(Arrays)
        }
    }

    private fun sum(Arrays: Array<Array<Double>>): Array<Double> {
        require(Arrays.size >= 2) { "Add operation requires at least two Arrays." }

        val result = Array(Arrays[0].size) { 0.0 }
        for (i in Arrays[0].indices) {
            var sum = 0.0
            for (Array in Arrays) {
                sum += Array[i]
            }
            result[i] = sum
        }
        return result
    }

    private fun sub(Arrays: Array<Array<Double>>): Array<Double> {
        require(Arrays.size == 2) { "Subtract operation requires exactly two Arrays." }

        val result = Array(Arrays[0].size) { 0.0 }
        for (i in Arrays[0].indices) {
            result[i] = Arrays[0][i] - Arrays[1][i]
        }
        return result
    }

    private fun prod(Arrays: Array<Array<Double>>): Array<Double> {
        require(Arrays.size >= 2) { "Product operation requires at least two Arrays." }

        val result = Array(Arrays[0].size) { 1.0 }
        for (i in Arrays[0].indices) {
            var product = 1.0
            for (Array in Arrays) {
                product *= Array[i]
            }
            result[i] = product
        }
        return result
    }

    private fun div(Arrays: Array<Array<Double>>): Array<Double> {
        require(Arrays.size == 2) { "Division operation requires exactly two Arrays." }

        val result = Array(Arrays[0].size) { 0.0 }
        for (i in Arrays[0].indices) {
            require(Arrays[1][i] != 0.0) { "Division by zero at index $i" }
            result[i] = Arrays[0][i] / Arrays[1][i]
        }
        return result
    }
}
