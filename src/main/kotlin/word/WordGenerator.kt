package word

import com.microsoft.schemas.vml.CTTextbox
import model.schule.SchuleDto
import org.apache.poi.xwpf.usermodel.*
import org.apache.xmlbeans.XmlCursor
import utilty.anyOrNull
import word.enum.ZsbSignatur
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.util.*


class WordGenerator(templateFile: File) {
    private val templateDoc = XWPFDocument(FileInputStream(templateFile))
    //private val doc = XWPFDocument()

    fun generateLetter(letter: SerialLetterDto): Boolean {
        for (school in letter.addressees) {
            replaceHeader(school)
            replaceDate()
            replaceBody(school, letter)
            val id = UUID.randomUUID()
            val out = FileOutputStream("$id.doc")
            templateDoc.write(out)
            out.close()
        }

        //var header = false
        //var body = false
        //var footer = false

        /*try {
            for (element in templateDoc.bodyElements) {
                if (element is XWPFParagraph) {
                    val para = element
                    val newPara = doc.createParagraph()
                    newPara.ctp.set(para.ctp)
                    for (run in element.runs) {
                        val newRun = newPara.createRun()
                        newRun.setText(element.text)
                        newRun.isBold = run.isBold
                        newRun.isItalic = run.isItalic
                        newRun.underline = run.underline
                        newRun.color = run.color
                        newRun.fontFamily = run.fontFamily
                        newRun.fontSize = run.fontSize
                    }
                } else if (element is XWPFTable) {
                    val table = element
                    val newTable = doc.createTable()
                    newTable.ctTbl.set(table.ctTbl)
                } else if (element is XWPFHeader) {
                    val header = element
                    val newHeader = doc.createHeader(HeaderFooterType.DEFAULT)
                    copyHeaderFooter(newHeader, header)
                } else if (element is XWPFFooter) {
                    val footer = element
                    val newFooter = doc.createFooter(HeaderFooterType.DEFAULT)
                    copyHeaderFooter(newFooter, footer)
                }
            }

            // Kopieren der Bilder aus der Vorlage
            val pictures = templateDoc.allPictures
            for (picture in pictures) {
                val imageData = picture.data
                val format = picture.pictureType
                doc.addPictureData(imageData, format)
            }

            FileOutputStream(outputFile).use { outputStream ->
                doc.write(outputStream)
            }
            val out = FileOutputStream("copied_template.docx")
            doc.write(out)
            out.close()

            println("Vorlage erfolgreich kopiert.")
        } catch (e: IOException) {
            e.printStackTrace()
        }
         */

        /*letter.addressees.forEachIndexed { i, it ->
            // check if adresse is valid
            if (it.address == null) return@forEachIndexed

            // clone template content to new document
            cloneTemplateContent()

            header = replacePlaceholder("SCHOOL_NAME", it.name.trim())
            header = replacePlaceholder("STREET", it.address.street.trim())
            header = replacePlaceholder("HOUSE_NUMBER", it.address.houseNumber.trim())
            header = replacePlaceholder("POSTCODE", it.address.city?.postcode.toString())
            header = replacePlaceholder("CITY", it.address.city?.designation?.trim() ?: "")

            body = replacePlaceholder("DATE", LocalDate.now().format(DateTimeFormatter.ofPattern("d. MMMM yyyy")))
            body = replacePlaceholder("MESSAGE", letter.msg)

            footer = if (letter.signature_id != ZsbSignatur.NONE.ordinal) {
                val signature = anyOrNull { ZsbSignatur.values()[letter.signature_id] }
                replacePlaceholder("SIGNATURE", signature?.text ?: "")
            } else {
                true
            }

            // create new page for every page except the first
            if (i != 0) generateNewPage()
        }

        // write to file
        FileOutputStream(outputFile).use { outputStream ->
            doc.write(outputStream)
        }
         */

        // return header && body && footer
        return true
    }

    fun replaceTextInParagraph(paragraph: XWPFParagraph, originalText: String, updatedText: String) {
        val runs = paragraph.runs
        for (run in runs) {
            val text = run.getText(0)
            if (text != null && text.contains(originalText)) {
                val updatedRunText = text.replace(originalText, updatedText)
                run.setText(updatedRunText, 0)
            }
        }
    }

    fun replaceTextInTable(table: XWPFTable, originalText: String, updatedText: String) {
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

    fun replaceTextInTextFields(originalText: String, updatedText: String) {
        val bodyElements = templateDoc.bodyElements
        for (element in bodyElements) {
            if (element is CTTextbox) {
                val text = element.newCursor().textValue
                if (text.contains(originalText)) {
                    element.newCursor().textValue = text.replace(originalText, updatedText)
                }
            }
        }
    }

    fun replaceHeader(school: SchuleDto) {
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

    fun replaceBody(school: SchuleDto, letter: SerialLetterDto) {
        var signature: ZsbSignatur = ZsbSignatur.NONE
        if (letter.signature_id != ZsbSignatur.NONE.ordinal) {
            signature = anyOrNull { ZsbSignatur.values()[letter.signature_id] }!!
        }
        for (paragraph in templateDoc.paragraphs) {
            replaceTextInParagraph(paragraph, "MESSAGE", letter.msg)
            replaceTextInParagraph(paragraph, "SIGNATURE", signature.text)
        }
    }

    fun replaceDate() {
        val date = Date()
        println(date.toString())
        replaceTextInTextFields("DATE", date.toString())
    }

    /*fun copyHeaderFooter(newHeaderFooter: XWPFHeaderFooter, headerFooter: XWPFHeaderFooter) {
        for (element in headerFooter.paragraphs) {
            val newPara = newHeaderFooter.createParagraph()
            newPara.ctp.set(element.ctp)
            for (run in element.runs) {
                val newRun = newPara.createRun()
                newRun.setText(element.text) // Text kopieren
                // Formatierungsattribute kopieren
                newRun.isBold = run.isBold
                newRun.isItalic = run.isItalic
                newRun.underline = run.underline
                newRun.color = run.color
                newRun.fontFamily = run.fontFamily
                newRun.fontSize = run.fontSize
            }
        }
    }

    private fun cloneTemplateContent() {
        templateDoc.bodyElements.forEach { element ->
            when (element) {
                is XWPFParagraph -> {
                    val newParagraph = doc.createParagraph()
                    element.runs.forEach { run ->
                        val newRun = newParagraph.createRun()
                        newRun.isBold = run.isBold
                        newRun.isItalic = run.isItalic
                        newRun.fontFamily = run.fontFamily
                        newRun.fontSize = run.fontSize
                    }
                }
            }
        }
    }

    private fun replacePlaceholder(placeholder: String, replacement: String): Boolean {
        var replaced = false
        doc.paragraphs.forEach { paragraph ->
            paragraph.runs.forEachIndexed { index, run ->
                if (run.text().contains(placeholder)) {
                    // Split text around the placeholder
                    val parts = run.text().split(placeholder)
                    // Clear the existing run
                    paragraph.removeRun(index)
                    // Add runs for each part, with the replacement in between
                    parts.forEachIndexed { partIndex, part ->
                        if (partIndex != parts.size - 1 || part.isNotEmpty()) {
                            val newRun = if (partIndex == 0) run else paragraph.insertNewRun(index + partIndex)
                            newRun.setText(part, 0)
                        }
                        if (partIndex != parts.size - 1) {
                            val newRun = paragraph.insertNewRun(index + partIndex + 1)
                            newRun.setText(replacement, 0)
                        }
                    }
                    replaced = true
                }
            }
        }
        return replaced
    }

    private fun writeHeader(schule: SchuleDto, adresse: AdresseDto): Boolean {
        val paragraph = doc.createParagraph()
        val run = paragraph.createRun()
        val ort = adresse.city ?: return false

        repeat(3) { run.addBreak() }
        run.setText(schule.name.trim())
        run.addBreak()
        run.setText("${adresse.street.trim()} ${adresse.houseNumber.trim()}\n")
        run.addBreak()
        run.setText("${ort.postcode} ${ort.designation.trim()}\n")
        repeat(2) { run.addBreak() }

        val formatter = DateTimeFormatter.ofPattern("d. MMMM yyyy")
        val date = LocalDate.now().format(formatter)
        val paragraph2 = doc.createParagraph()
        paragraph2.alignment = ParagraphAlignment.RIGHT
        paragraph2.createRun().setText(date)
        return true
    }

    private fun writeBody(text: String): Boolean = writeTextWithLineBreaks(text)

    private fun writeFooter(signatureId: Int): Boolean {
        val signatur = anyOrNull { ZsbSignatur.values()[signatureId] } ?: return false

        if (signatur === ZsbSignatur.NONE) return true

        val paragraph = doc.createParagraph()
        paragraph.createRun()

        writeTextWithLineBreaks(signatur.text)

        return true
    }

    private fun writeTextWithLineBreaks(text: String): Boolean {
        var remainingText = text

        // search for line breaks
        while (remainingText.contains("\n")) {
            // split text by the next line break -> \n
            val split = remainingText.split("\n", limit = 2)

            // write current paragraph
            doc.createParagraph().createRun().setText(split.first())

            // save next paragraph
            remainingText = split.last()
        }

        // fill last paragraph
        doc.createParagraph().createRun().setText(remainingText)

        return true
    }

    private fun generateNewPage() {
        doc.createParagraph().isPageBreak = true
    }
     */
}