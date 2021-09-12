package word

import model.address.AdresseDto
import model.schule.SchuleDto
import mu.KotlinLogging
import org.apache.poi.util.Units
import org.apache.poi.xwpf.usermodel.ParagraphAlignment
import org.apache.poi.xwpf.usermodel.XWPFDocument
import org.apache.poi.xwpf.usermodel.XWPFRun
import utilty.ColoredLogging
import utilty.anyOrNull
import word.enum.ZsbSignatur
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.time.LocalDate
import java.time.format.DateTimeFormatter


class WordGenerator(private val file: File) {
    private val log = ColoredLogging(KotlinLogging.logger {})
    private val doc = XWPFDocument()


    fun generateLetter(letter: SerialLetterDto): Boolean {
        val outputStream = FileOutputStream(file)
        var header = false
        var body = false
        var footer = false

        letter.addressees.forEachIndexed { i, it ->
            // check if adresse is valid
            if (it.address == null) return@forEachIndexed

            // create new page for every page except the first
            if (i != 0) generateNewPage()

            header = writeHeader(it, it.address)
            body = writeBody(letter.msg)
            footer = writeFooter(letter.signature_id)
        }

        // check for errors
        if (!header || !body || !footer) {
            log.error("Error while writing. (Header: $header | Body: $body | Footer: $footer)")
            return false
        }

        // write to file
        doc.write(outputStream)
        outputStream.close()

        return true
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
        val run = paragraph.createRun()

        writeTextWithLineBreaks(signatur.text)
        appendSignatureAsPicture(signatur, run)

        return true
    }

    private fun appendSignatureAsPicture(signatur: ZsbSignatur, run: XWPFRun) {
        val inputStream = FileInputStream(signatur.path)
        run.addPicture(
            inputStream,
            XWPFDocument.PICTURE_TYPE_JPEG,
            signatur.path,
            Units.toEMU(50.0),
            Units.toEMU(50.0)
        )
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
}