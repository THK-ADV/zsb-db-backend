package excel

import model.schule.Schule
import model.schule.SchuleDto
import org.apache.poi.xssf.usermodel.XSSFSheet
import org.apache.poi.xssf.usermodel.XSSFWorkbook
import java.io.File
import java.io.FileOutputStream
import java.util.*

class ExcelGenerator(private val file: File) {
    private val workBook = XSSFWorkbook()

    fun generateSheet(schools: List<SchuleDto>): Boolean {
        val outputStream = FileOutputStream(file)
        val sheet = workBook.createSheet("Adressen")
        val header = createHeader(sheet)
        val body = createContent(schools, sheet)

        workBook.write(outputStream)
        outputStream.close()

        return true
    }

    private fun createHeader(sheet: XSSFSheet) {

        // Schulname, Vorname, Nachname, Straße, Hausnummer, PLZ, Ort
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
            for(i in 0..it.contacts.size) {
                val row = sheet.createRow(rowNum++)
                row.createCell(0)
                    .setCellValue(it.name)
                row.createCell(1)
                    .setCellValue(it.contacts[i].firstname)
                row.createCell(2)
                    .setCellValue(it.contacts[i].surname)
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