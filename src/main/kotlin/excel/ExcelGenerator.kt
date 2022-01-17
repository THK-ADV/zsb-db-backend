package excel

import model.kontakt.KontaktDto
import model.kontakt.enum.KontaktFunktion
import model.schule.SchuleDto
import org.apache.poi.xssf.usermodel.XSSFSheet
import org.apache.poi.xssf.usermodel.XSSFWorkbook
import java.io.ByteArrayOutputStream


class ExcelGenerator() {

    fun generateSheet(schools: List<SchuleDto>): ByteArray {
        val outputStream = ByteArrayOutputStream()
        val workBook = XSSFWorkbook()
        val sheet = workBook.createSheet("Adressen")
        createHeader(sheet, workBook)
        createContent(schools, sheet)
        workBook.write(outputStream)
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

    infix fun <A> List<A>.or(that: List<A>): List<A> =
        if (this.isNotEmpty()) this else that

    private fun createContent(schools: List<SchuleDto>, sheet: XSSFSheet) {
        fun findContacts(s: SchuleDto, f: KontaktFunktion): List<KontaktDto> =
            s.contacts.filter { it.feature == f.id }

        var rowNum = 1
        var columnIndex = 0
        schools.forEach { s ->
            val contacts = findContacts(s, KontaktFunktion.STUBO) or
                    findContacts(s, KontaktFunktion.SECRETARIAT) or
                    findContacts(s, KontaktFunktion.SCHULLEITUNG) or
                    s.contacts
            contacts.forEach {
                printRows(sheet, s, it, rowNum++)
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

    private fun printRows(sheet: XSSFSheet, s: SchuleDto, c: KontaktDto, row: Int): Int {
        var columnIndex = 0
        val row = sheet.createRow(row)
        row.createCell(columnIndex)
            .setCellValue(s.name)
        row.createCell(++columnIndex)
            .setCellValue(c.firstname)
        row.createCell(++columnIndex)
            .setCellValue(c.surname)
        row.createCell(++columnIndex)
            .setCellValue(s.address?.street)
        row.createCell(++columnIndex)
            .setCellValue(s.address?.houseNumber)
        row.createCell(++columnIndex)
            .setCellValue(s.address?.city?.postcode.toString())
        row.createCell(++columnIndex)
            .setCellValue(s.address?.city?.designation)
        return columnIndex
    }
}