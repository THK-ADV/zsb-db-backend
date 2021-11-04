package excel

import model.schule.Schule
import model.schule.SchuleDto
import model.schule.Schulen
import org.apache.poi.xssf.usermodel.XSSFSheet
import org.apache.poi.xssf.usermodel.XSSFWorkbook
import org.jetbrains.exposed.sql.select
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.util.*

class ExcelGenerator() {

    fun generateSheet(schools: List<SchuleDto>): ByteArray {
        val outputStream = ByteArrayOutputStream()
        val workBook = XSSFWorkbook()
        val sheet = workBook.createSheet("Adressen")
        createHeader(sheet)
        createContent(schools, sheet)

        workBook.write(outputStream)
        return outputStream.toByteArray()
    }

    private fun createHeader(sheet: XSSFSheet) {
        val properties = arrayOf("Schulname", "Vorname", "Nachname", "Straße", "Hausnummer", "PLZ", "Ort")

        val headerRow = sheet.createRow(0)

        for (i in properties.indices) {
            val cell = headerRow.createCell(i)
            cell.setCellValue(properties[i])
        }
    }

    private fun createContent(schools: List<SchuleDto>, sheet: XSSFSheet) {
        var rowNum = 1;
        schools.forEach {
            it.contacts.forEach { c ->
                val row = sheet.createRow(rowNum++)
                row.createCell(0)
                    .setCellValue(it.name)
                row.createCell(1)
                    .setCellValue(c.firstname)
                row.createCell(2)
                    .setCellValue(c.surname)
                row.createCell(3)
                    .setCellValue(it.address?.street)
                row.createCell(4)
                    .setCellValue(it.address?.houseNumber)
                row.createCell(5)
                    .setCellValue(it.address?.city?.postcode.toString())
                row.createCell(6)
                    .setCellValue(it.address?.city?.designation)
            }
        }
    }

}