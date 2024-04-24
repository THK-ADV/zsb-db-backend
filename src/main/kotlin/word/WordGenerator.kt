package word

import io.ktor.util.logging.*
import log
import model.schule.SchuleDto
import org.apache.poi.xwpf.usermodel.XWPFDocument
import org.apache.poi.xwpf.usermodel.XWPFParagraph
import org.apache.poi.xwpf.usermodel.XWPFTable
import utilty.ColoredLogging
import utilty.anyOrNull
import word.enum.ZsbSignatur
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.util.*
import java.util.zip.ZipEntry
import java.util.zip.ZipOutputStream


class WordGenerator(templateFile: File) {
    private val templateDoc = XWPFDocument(FileInputStream(templateFile))

    fun generateLetter(letter: SerialLetterDto): File? {
        val zipId = UUID.randomUUID()
        val output = "letters_$zipId.zip"
        val files = mutableListOf<String>()
        try {
            for (school in letter.addressees) {
                replaceHeader(school)
                replaceBody(letter)
                val id = UUID.randomUUID()
                val name = "${school.name}_$id.docx"
                val out = FileOutputStream(name)
                templateDoc.write(out)
                out.close()
                files.add(name)
            }
            val stream = FileOutputStream(output)
            val zipOutput = ZipOutputStream(stream)
            for (file in files) {
                val entry = ZipEntry(File(file).name)
                zipOutput.putNextEntry(entry)
                val input = File(file).inputStream()
                input.copyTo(zipOutput)
                input.close()
            }
            zipOutput.close()
            stream.close()
            for (file in files) {
                File(file).delete()
            }
            files.clear()
            return File(output)
        } catch (e: Exception) {
            log.error(e)
            ColoredLogging.LOG.error("Could not generate letter.")
            return null
        }
    }

    private fun replaceHeader(school: SchuleDto) {
        val schoolName = school.name
        val street = school.address?.street ?: ""
        val houseNumber = school.address?.houseNumber ?: ""
        val postcode = school.address?.city?.postcode.toString() ?: ""
        val designation = school.address?.city?.designation ?: ""
        for (table in templateDoc.tables) {
            replaceTextInTable(table, "SCHOOL_NAME", schoolName)
            replaceTextInTable(table, "STREET", street)
            replaceTextInTable(table, "HOUSE_NUMBER", houseNumber)
            replaceTextInTable(table, "POSTCODE", postcode)
            replaceTextInTable(table, "CITY", designation)
        }
    }

    private fun replaceTextInTable(table: XWPFTable, originalText: String, updatedText: String) {
        for (row in table.rows) {
            for (cell in row.tableCells) {
                for (paragraph in cell.paragraphs) {
                    for (run in paragraph.runs) {
                        val text = run.getText(0)
                        if (text != null && text.contains(originalText)) {
                            val updatedRunText = text.replace(originalText, updatedText)
                            run.setText(updatedRunText, 0)
                        }
                    }
                }
            }
        }
    }

    private fun replaceBody(letter: SerialLetterDto) {
        var signature: ZsbSignatur = ZsbSignatur.NONE
        if (letter.signature_id != ZsbSignatur.NONE.ordinal) {
            signature = anyOrNull { ZsbSignatur.values()[letter.signature_id] }!!
        }
        val paragraphs = ArrayList(templateDoc.paragraphs)
        for (paragraph in paragraphs) {
            replaceTextInParagraph(paragraph, "MESSAGE", letter.msg)
            replaceTextInParagraph(paragraph, "SIGNATURE", signature.text)
        }
    }

    private fun replaceTextInParagraph(paragraph: XWPFParagraph, originalText: String, updatedText: String) {
        val runs = paragraph.runs
        for (run in runs) {
            val text = run.getText(0)
            if (text != null && text.contains(originalText)) {
                if (updatedText.contains("\n")) {
                    val updatedLines = updatedText.split("\n")
                    run.setText(updatedLines[0], 0)
                    for (i in 1 until updatedLines.size) {
                        val newParagraph = templateDoc.createParagraph() // Neuen Absatz erstellen
                        val newRun = newParagraph.createRun()
                        newRun.setText(updatedLines[i])
                    }
                } else {
                    val updatedRunText = text.replace(originalText, updatedText)
                    run.setText(updatedRunText, 0)
                }
            }
        }
    }
}