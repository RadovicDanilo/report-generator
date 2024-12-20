# Report Generator App (Kotlin)

🎯 **Report Generator App** is a modular Kotlin-based application designed to generate various types of reports from a MySQL database. It allows users to export reports in different formats, such as CSV, Excel, PDF, and plain text (TXT), with support for both **formatted** and **unformatted** reports. The app also supports calculations like COUNT, SUM, and AVERAGE on columns. 🚀

## 📝 Project Overview

This app was developed as part of the **Software Components** course at the **Univerzitet Union - Računarski fakultet**. It demonstrates the implementation of reusable and extendable software components that generate reports in various formats. These components were built using the **Service Provider Interface (SPI)** architecture, allowing easy integration into different software systems. 📚💻

## Features

- 📊 **Report Generation**: Connect to a MySQL database and export reports in multiple formats (CSV, PDF, Excel, TXT).
- 🛠️ **Calculations**: Perform calculations such as SUM, COUNT, and AVERAGE on numeric columns.
- 🖥️ **Command-Line Interface (CLI)**: Interact with the app using a terminal to connect to a database, select a report format, and apply necessary customizations.
- 🏗️ **Modular Architecture**: Built using the **Service Provider Interface (SPI)**, making the components reusable in other projects.

## Supported File Formats

| File Format | Unformatted Export | Formatted Export |
|--------------|--------------------|------------------|
| **TXT**      | ✅ Yes              | ❌ No            |
| **CSV**      | ✅ Yes              | ❌ No            |
| **PDF**      | ✅ No               | ✅ Yes           |
| **Excel**    | ✅ No               | ✅ Yes           |

## Supported Calculations

- **COUNT**: Count values in a column (with conditions, if needed).
- **SUM**: Sum up values in a numeric column.
- **AVERAGE**: Calculate the average of values in a numeric column.
- **Custom Calculations**: Perform custom calculations like addition, subtraction, multiplication, or division on columns.

## Architecture

The application follows a **modular architecture** and uses the **Service Provider Interface (SPI)** pattern. The architecture includes:

- **API Specification**: A common interface that defines the required methods for generating reports.
- **Report Implementations**: Four different types of reports, supporting both formatted and unformatted outputs.
- **Calculation Component**: A separate module for performing calculations on the data before generating the report.

## Example Usage

1. **Connect to the Database**: The app allows you to specify connection details for a MySQL database as a command line argument.
2. **Select Report Type**: Choose from CSV, TXT, Excel, or PDF as the desired export format.
3. **Apply Calculations**: For numeric columns, you can apply SUM, AVERAGE, or COUNT.
4. **Generate the Report**: Once everything is configured, the app generates the report and exports it in the chosen format.

## Made as Part of a Software Component Course

This project was created as part of the **Software Component** course at **Univerzitet Union - Računarski fakultet**, focusing on designing and implementing modular software components. The project demonstrates key software development concepts like SPI, report generation, and working with databases. 🎓
