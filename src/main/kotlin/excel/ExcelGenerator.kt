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

    fun generateSheet(school: SchuleDto): Boolean {
        val outputStream = FileOutputStream(file)
        val sheet = workBook.createSheet("Adressen")
        val header = createHeader(sheet)
        val body = createContent(school)

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

    fun createContent(school: SchuleDto) {

    }

}