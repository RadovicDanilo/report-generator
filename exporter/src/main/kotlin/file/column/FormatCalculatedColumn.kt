package src.main.kotlin.file.column

import src.main.kotlin.file.format_options.CellFormatOptions

class FormatCalculatedColumn(
    header: String = "",
    columnsForCalculations: Array<Column<Double>>,
    calculation: Calculation,
    val cellFormatOptions: CellFormatOptions
) : CalculatedColumn(header, columnsForCalculations, calculation)