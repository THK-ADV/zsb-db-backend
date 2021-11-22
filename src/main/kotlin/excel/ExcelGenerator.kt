package excel

import model.schule.SchuleDto
import org.apache.poi.xssf.usermodel.XSSFSheet
import org.apache.poi.xssf.usermodel.XSSFWorkbook
import java.io.ByteArrayOutputStream


class ExcelGenerator {

    fun generateSheet(schools: List<SchuleDto>): ByteArray {
        val outputStream = ByteArrayOutputStream()
        val workBook = XSSFWorkbook()
        val sheet = workBook.createSheet("Adressen")
        createHeader(sheet, workBook)
        createContent(schools, sheet)
        workBook.write(outputStream)
        outputStream.close()
        workBook.close()
        return outputStream.toByteArray()
    }

    private fun createHeader(sheet: XSSFSheet, workbook: XSSFWorkbook) {
        val properties = arrayOf("Schulname", "Vorname", "Nachname", "Straße", "Hausnummer", "PLZ", "Ort")
        val headerRow = sheet.createRow(0)

        val font = workbook.createFont()
        font.bold = true
        val style = workbook.createCellStyle()
        style.setFont(font)

        for (i in properties.indices) {
            val cell = headerRow.createCell(i)
            cell.setCellValue(properties[i])
            cell.cellStyle = style
        }
    }

    private fun createContent(schools: List<SchuleDto>, sheet: XSSFSheet) {
        var rowNum = 1
        var columnIndex = 0
        schools.forEach {
            it.contacts.forEach { c ->
                columnIndex = 0
                val row = sheet.createRow(rowNum++)
                row.createCell(columnIndex)
                    .setCellValue(it.name)
                row.createCell(++columnIndex)
                    .setCellValue(c.firstname)
                row.createCell(++columnIndex)
                    .setCellValue(c.surname)
                row.createCell(++columnIndex)
                    .setCellValue(it.address?.street)
                row.createCell(++columnIndex)
                    .setCellValue(it.address?.houseNumber)
                row.createCell(++columnIndex)
                    .setCellValue(it.address?.city?.postcode.toString())
                row.createCell(++columnIndex)
                    .setCellValue(it.address?.city?.designation)
            }
        }
        for (i in 0..columnIndex) {
            try {
                sheet.autoSizeColumn(i)
            } catch (e: Exception) {
                println("could not auto size column $i")
            }
        }
    }
}