package com.alhikmahpro.www.e_inventory.View

import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View

import com.alhikmahpro.www.e_inventory.Data.DashedSeparator
import com.alhikmahpro.www.e_inventory.Data.DataContract
import com.alhikmahpro.www.e_inventory.Data.dbHelper
import com.alhikmahpro.www.e_inventory.FileUtils
import com.alhikmahpro.www.e_inventory.R
import com.itextpdf.text.pdf.draw.DottedLineSeparator

import java.io.File
import java.io.FileOutputStream

import butterknife.ButterKnife
import com.alhikmahpro.www.e_inventory.App.AppController
import com.alhikmahpro.www.e_inventory.FileUtils.getAppPath
import com.itextpdf.text.*
import com.itextpdf.text.pdf.*

import com.shockwave.pdfium.PdfiumCore
import kotlinx.android.synthetic.main.activity_view_pdf.*

class ViewPdfActivity : AppCompatActivity() {

    //    @BindView(R.id.pdfView)
//    internal var pdfView: PDFView? = null
    lateinit var mContext: Context
    lateinit var itemPrint: MenuItem
    lateinit var itemSync: MenuItem
    lateinit var itemShare: MenuItem
    lateinit var customerName: String
    lateinit var receiptNo: String
    lateinit var receiptDate: String
    lateinit var salesmanId: String
    lateinit var action: String
    internal var amount: Double = 0.toDouble()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_view_pdf)
        ButterKnife.bind(this)
        val mIntent = intent
        val toolbar = findViewById<View>(R.id.toolbar) as Toolbar
        setSupportActionBar(toolbar)
        mContext = applicationContext

        val intent = intent
        // action = intent.getStringExtra("TYPE")
        intent?.getStringExtra("CUS_NAME")?.let { customerName = it }
                ?: kotlin.run { customerName = "customer name" }
        intent?.getStringExtra("RECEIPT_NO")?.let { receiptNo = it }
                ?: kotlin.run { receiptNo = "receipt no" }
        intent?.getStringExtra("RECEIPT_DATE")?.let { receiptDate = it }
                ?: kotlin.run { receiptDate = "receipt date" }
        intent?.getStringExtra("SALESMAN_ID")?.let { salesmanId = it }
                ?: kotlin.run { salesmanId = "Id" }
        intent?.getDoubleExtra("REC_AMOUNT", 0.0)?.let { amount = it }
                ?: kotlin.run { amount = 0.0 }

        // receiptNo = intent.getStringExtra("RECEIPT_NO")

        // receiptDate = intent.getStringExtra("RECEIPT_DATE")
        //salesmanId = intent.getStringExtra("SALESMAN_ID")
        // amount = intent.getDoubleExtra("REC_AMOUNT", 0.0)

        createPdf(FileUtils.getSubDirPath(mContext, DataContract.DIR_RECEIPT), 0f)


    }



    private fun generateThump() {
        val pageNumber = 0
        val pdfiumCore = PdfiumCore(this)
        val uri = Uri.fromFile(File(FileUtils.getSubDirPath(applicationContext, DataContract.DIR_RECEIPT) + receiptNo + ".pdf"))
        try {
            //http://www.programcreek.com/java-api-examples/index.php?api=android.os.ParcelFileDescriptor
            val fd = contentResolver.openFileDescriptor(uri, "r")
            val pdfDocument = pdfiumCore.newDocument(fd)
            pdfiumCore.openPage(pdfDocument, pageNumber)
            //                    val width = 250
            //                    val height =250
            val width = pdfiumCore.getPageWidthPoint(pdfDocument, pageNumber)
            val height = pdfiumCore.getPageHeightPoint(pdfDocument, pageNumber)
            val bmp = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565)
            pdfiumCore.renderPageBitmap(pdfDocument, bmp, pageNumber, 0, 0, width, height)
            pdfiumCore.closeDocument(pdfDocument) // important!
            var f = File(FileUtils.getAppPath(applicationContext) + "1234.png")
            if (f.exists()) {
                f.delete()
                f.createNewFile()
            } else {
                println("File already exists")
            }
            var os = FileOutputStream(f, false)
            bmp.compress(Bitmap.CompressFormat.PNG, 50, os);
            os.close()

        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

    private fun viewPdf() {
        Log.d(TAG, "viewPdf: ")
        // if (fileName != null && !fileName.isEmpty()){

        val name = FileUtils.getSubDirPath(mContext, DataContract.DIR_RECEIPT) + receiptNo + ".pdf"
        //Log.d(TAG, "viewPdf: file name $fName")


        pdfView!!.fromFile(File(name))
                .pages(0)
                .enableSwipe(true) // allows to block changing pages using swipe
                .swipeHorizontal(false)
                .enableDoubletap(true)
                .defaultPage(0)
                .enableAnnotationRendering(false) // render annotations (such as comments, colors or forms)
                .password(null)
                .scrollHandle(null)
                .enableAntialiasing(true) // improve rendering a little bit on low-res screens
                .spacing(0)
                .load()
        //        }else {
        //            Log.d(TAG, "Invalid file : ");
        //        }


    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.tool_bar, menu)
        itemPrint = menu.findItem(R.id.action_print)
        itemSync = menu.findItem(R.id.action_sync)
        itemShare = menu.findItem(R.id.action_share)
        itemSync.isVisible = false
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        val id = item.itemId

        if (id == R.id.action_print) {
            hsPrint()

        } else if (id == R.id.action_share) {

        }
        return super.onOptionsItemSelected(item)

    }

    companion object {


        private val TAG = "ViewPdfActivity"

        fun scaleDown(realImage: Bitmap, maxImageSize: Float,
                      filter: Boolean): Bitmap {
            val ratio = Math.min(
                    maxImageSize / realImage.width,
                    maxImageSize / realImage.height)
            val width = Math.round(ratio * realImage.width)
            val height = Math.round(ratio * realImage.height)

            return Bitmap.createScaledBitmap(realImage, width,
                    height, filter)
        }
    }


    private fun generateImage(): Bitmap? {
        //isNFCE=false
        val pageNumber = 0
        val pdfiumCore = PdfiumCore(this)

        //val uri = Uri.fromFile(File(FileUtils.getAppPath(applicationContext) + "123.pdf"))
        val fPath = FileUtils.getSubDirPath(applicationContext, DataContract.DIR_RECEIPT) + receiptNo + ".pdf"
        Log.d(TAG, "generateImage: file name $fPath")
        val uri = Uri.fromFile(File(fPath))


        try {
            //http://www.programcreek.com/java-api-examples/index.php?api=android.os.ParcelFileDescriptor
            val fd = contentResolver.openFileDescriptor(uri, "r")
            val pdfDocument = pdfiumCore.newDocument(fd)
            pdfiumCore.openPage(pdfDocument, pageNumber)
            //                    val width = 250
            //                    val height =250
            val width = pdfiumCore.getPageWidthPoint(pdfDocument, pageNumber)
            val height = pdfiumCore.getPageHeightPoint(pdfDocument, pageNumber)
            val bmp = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565)
            pdfiumCore.renderPageBitmap(pdfDocument, bmp, pageNumber, 0, 0, width, height)
            pdfiumCore.closeDocument(pdfDocument) // important!
            return bmp
        } catch (e: Exception) {
            e.printStackTrace()
        }

        return null
    }



    fun hsPrint() {
        AppController?.instance?.hsPrint(generateImage(), 0) //1 for 80 0 58
    }


    fun createPdf(dest: String, docsize: Float) {

        var paragraph: Paragraph? = null
        val table: PdfPTable
        var cell: PdfPCell
        val cb: PdfContentByte
        val fName = dest + receiptNo + ".pdf"
        if (File(fName).exists()) {
            File(fName).delete()
        }

        val helper = dbHelper(this)
        val database = helper.readableDatabase
        val cursor = helper.getPaperSettings(database)
        var companyName = ""
        var companyAddress = ""
        var companyPhone = ""
        var footer = ""

        val bmp: Bitmap
        var img: ByteArray? = null

        if (cursor.moveToFirst()) {
            companyName = cursor.getString(cursor.getColumnIndex(DataContract.PaperSettings.COL_COMPANY_NAME))
            companyAddress = cursor.getString(cursor.getColumnIndex(DataContract.PaperSettings.COL_COMPANY_ADDRESS))
            companyPhone = cursor.getString(cursor.getColumnIndex(DataContract.PaperSettings.COL_COMPANY_PHONE))
            footer = cursor.getString(cursor.getColumnIndex(DataContract.PaperSettings.COL_FOOTER))
            img = cursor.getBlob(cursor.getColumnIndex(DataContract.PaperSettings.COL_LOGO))
            Log.d(TAG, "image : " + img!!)

        }
        cursor.close()
        database.close()



        try {

            val width = 480f
            var max = 350f;
            var fontSmall = 28f;
            var fontBig = 45f;
            var fontMediam = 28f;
            val printNormal = BaseFont.createFont("assets/brandon_bold.otf", "UTF-8", BaseFont.EMBEDDED)//BaseFont.createFont("assets/cour.ttf", "UTF-8", BaseFont.EMBEDDED)
            val printBold = BaseFont.createFont("assets/brandon_bold.otf", "UTF-8", BaseFont.EMBEDDED)
            val printArabic =BaseFont.createFont("assets/fonts/Arial.ttf", BaseFont.IDENTITY_H, BaseFont.EMBEDDED)
                    //BaseFont.createFont("assets/LateefRegOT.ttf", "UTF-8", BaseFont.EMBEDDED)

            val urName = BaseFont.createFont("assets/fonts/mll.ttf", BaseFont.IDENTITY_H, BaseFont.EMBEDDED)
            val mArabicFont = Font(urName, fontSmall, Font.NORMAL, BaseColor.BLACK)

            val mPrintNormal = Font(printNormal, fontSmall, Font.NORMAL, BaseColor.BLACK)
            val mPrintBoldMediam = Font(printBold, fontMediam, Font.NORMAL, BaseColor.BLACK)
            val mPrintBoldBig = Font(printBold, fontBig, Font.NORMAL, BaseColor.BLACK)
            //val mPrintArabic = Font(printArabic, fontMediam, Font.NORMAL, BaseColor.BLACK)
           // val mOrderIdFont = Font(urName, mHeadingFontSize, Font.NORMAL, BaseColor.BLACK)
            if (docsize != 0f) {
                max = docsize
            }
            val pagesize = Rectangle(width, max)
            // Document with predefined page size
            val document = Document(pagesize, 5f, 5f, 0f, 0f)
            // Getting PDF Writer
            val writer = PdfWriter.getInstance(document, FileOutputStream(fName))
            document.open()




            // add logo
//            try {
//                bmp = BitmapFactory.decodeByteArray(img, 0, img!!.size)
//
//                val scaledBitmap = scaleDown(bmp, 80f, true)
//                val stream = ByteArrayOutputStream()
//                scaledBitmap.compress(Bitmap.CompressFormat.PNG, 100, stream)
//                val image = Image.getInstance(stream.toByteArray())
//                image.alignment = Image.ALIGN_TOP or Image.ALIGN_CENTER
//                document.add(image)
//
//
//            } catch (e: Exception) {
//                e.printStackTrace()
//            }


            // Column with a writer
            paragraph = Paragraph("Header sample", mPrintBoldBig)
            paragraph.alignment = Element.ALIGN_CENTER
            document.add(paragraph)

            paragraph = Paragraph("Header 2 sample", mPrintBoldMediam)
            paragraph.alignment = Element.ALIGN_CENTER
            document.add(paragraph)

            paragraph = Paragraph("Header 3 sample", mPrintBoldMediam)
            paragraph.alignment = Element.ALIGN_CENTER
            document.add(paragraph)

            val separator = DashedSeparator()
            separator.percentage = 59500f / 523f
            val linebreak = Chunk(separator)


            paragraph = Paragraph("Receipt#" + receiptNo, mPrintBoldMediam)
            paragraph.alignment = Element.ALIGN_LEFT
            document.add(paragraph)
            paragraph = Paragraph("sale ", mPrintBoldMediam)
            paragraph.alignment = Element.ALIGN_LEFT
            document.add(paragraph)

            paragraph = Paragraph("QR", mPrintBoldMediam)
            paragraph.alignment = Element.ALIGN_LEFT
            document.add(paragraph)

            paragraph = Paragraph("Date " + receiptDate, mPrintBoldMediam)
            paragraph.alignment = Element.ALIGN_LEFT
            document.add(paragraph)



            document.add(linebreak)
            val snWidth = 0f
            val p = (width - 10) / 3
            val re = ((p * 2) - snWidth) / 3

            //  val rem = re / 3

            val columnWidths = floatArrayOf(p, re, re, re)
            table = PdfPTable(columnWidths)
            table.totalWidth = width - 10
            table.isLockedWidth = true
            table.horizontalAlignment = Element.ALIGN_LEFT

            cell = PdfPCell(Phrase(Chunk("Item", mPrintNormal)))
            cell.horizontalAlignment = Element.ALIGN_LEFT
            cell.border = Rectangle.NO_BORDER
            table.addCell(cell)

            cell = PdfPCell(Phrase(Chunk("Rate", mPrintNormal)))
            cell.horizontalAlignment = Element.ALIGN_RIGHT
            cell.border = Rectangle.NO_BORDER
            table.addCell(cell)

            cell = PdfPCell(Phrase(Chunk("Qty", mPrintNormal)))
            cell.horizontalAlignment = Element.ALIGN_CENTER
            cell.border = Rectangle.NO_BORDER
            table.addCell(cell)

            cell = PdfPCell(Phrase(Chunk("Total", mPrintNormal)))
            cell.horizontalAlignment = Element.ALIGN_RIGHT
            cell.border = Rectangle.NO_BORDER
            table.addCell(cell)
            val separato = DashedSeparator()
            separato.percentage = 59500f / 523f
            cell = PdfPCell(Phrase(Chunk(separato)))
            cell.colspan = 5
            cell.horizontalAlignment = Element.ALIGN_RIGHT
            cell.border = Rectangle.NO_BORDER
            table.addCell(cell)
            table.headerRows = 1
            var i = 0

            for (recod in 1..3) {
                i++;

                cell = PdfPCell(Paragraph("products", mPrintNormal))
                cell.horizontalAlignment = Element.ALIGN_LEFT
                cell.border = Rectangle.NO_BORDER
                table.addCell(cell)

                cell = PdfPCell(Phrase(Chunk("500.0", mPrintNormal)))
                cell.horizontalAlignment = Element.ALIGN_RIGHT
                cell.border = Rectangle.NO_BORDER
                table.addCell(cell)

                cell = PdfPCell(Phrase(Chunk("10", mPrintNormal)))
                cell.horizontalAlignment = Element.ALIGN_CENTER
                cell.border = Rectangle.NO_BORDER
                table.addCell(cell)

                cell = PdfPCell(Phrase(Chunk("500.00", mPrintNormal)))
                cell.horizontalAlignment = Element.ALIGN_RIGHT
                cell.border = Rectangle.NO_BORDER
                table.addCell(cell)


                // arabic

                val phrase = Phrase("المجموع الصافي ", mArabicFont)
                cell = PdfPCell(phrase)

                cell.runDirection = PdfWriter.RUN_DIRECTION_RTL
                cell.horizontalAlignment = Element.ALIGN_RIGHT
                cell.border = Rectangle.NO_BORDER
                table.addCell(cell)

//                cell = PdfPCell(Paragraph("products", mPrintNormal))
//                cell.horizontalAlignment = Element.ALIGN_LEFT
//                cell.border = Rectangle.NO_BORDER
//                table.addCell(cell)

                cell = PdfPCell(Phrase(Chunk("", mPrintNormal)))
                cell.horizontalAlignment = Element.ALIGN_RIGHT
                cell.border = Rectangle.NO_BORDER
                table.addCell(cell)

                cell = PdfPCell(Phrase(Chunk("", mPrintNormal)))
                cell.horizontalAlignment = Element.ALIGN_CENTER
                cell.border = Rectangle.NO_BORDER
                table.addCell(cell)

                cell = PdfPCell(Phrase(Chunk("", mPrintNormal)))
                cell.horizontalAlignment = Element.ALIGN_RIGHT
                cell.border = Rectangle.NO_BORDER
                table.addCell(cell)
            }

            val separator1 = DashedSeparator()
            separator1.percentage = 59500f / 523f

            cell = PdfPCell(Phrase(Chunk(separator1)))
            cell.colspan = 4
            cell.horizontalAlignment = Element.ALIGN_RIGHT
            cell.border = Rectangle.NO_BORDER
            table.addCell(cell)


            cell = PdfPCell(Phrase("Total Items", mPrintBoldMediam))
            cell.colspan = 2
            cell.horizontalAlignment = Element.ALIGN_LEFT
            cell.border = Rectangle.NO_BORDER
            table.addCell(cell)

            cell = PdfPCell(Phrase("5", mPrintBoldMediam))
            cell.colspan = 2
            cell.horizontalAlignment = Element.ALIGN_RIGHT
            cell.border = Rectangle.NO_BORDER
            table.addCell(cell)


            cell = PdfPCell(Phrase(Chunk(separator1)))
            cell.colspan = 4
            cell.horizontalAlignment = Element.ALIGN_RIGHT
            cell.border = Rectangle.NO_BORDER
            table.addCell(cell)




            cell = PdfPCell(Phrase("Total", mPrintBoldMediam))
            cell.colspan = 2
            cell.horizontalAlignment = Element.ALIGN_LEFT
            cell.border = Rectangle.NO_BORDER
            table.addCell(cell)

            cell = PdfPCell(Phrase(("500"
                    ?: "00.00"), mPrintBoldMediam))
            cell.colspan = 2
            cell.horizontalAlignment = Element.ALIGN_RIGHT
            cell.border = Rectangle.NO_BORDER
            table.addCell(cell)

            cell = PdfPCell(Phrase("Discount", mPrintBoldMediam))
            cell.colspan = 2
            cell.horizontalAlignment = Element.ALIGN_LEFT
            cell.border = Rectangle.NO_BORDER
            table.addCell(cell)

            cell = PdfPCell(Phrase(("10.00"
                    ?: "0"), mPrintBoldMediam))
            cell.colspan = 2
            cell.horizontalAlignment = Element.ALIGN_RIGHT
            cell.border = Rectangle.NO_BORDER
            table.addCell(cell)

            cell = PdfPCell(Phrase(Chunk(separator1)))
            cell.colspan = 4
            cell.horizontalAlignment = Element.ALIGN_RIGHT
            cell.border = Rectangle.NO_BORDER
            table.addCell(cell)


            cell = PdfPCell(Phrase("Net Total", mPrintBoldMediam))
            cell.colspan = 2
            cell.horizontalAlignment = Element.ALIGN_LEFT
            cell.border = Rectangle.NO_BORDER
            table.addCell(cell)

            cell = PdfPCell(Phrase(("490.00" ?: "0"), mPrintBoldMediam))
            cell.colspan = 2
            cell.horizontalAlignment = Element.ALIGN_RIGHT
            cell.border = Rectangle.NO_BORDER
            table.addCell(cell)


           //net total arabic
            val phrase = Phrase(
                    "المجموع الصافي ", mArabicFont)
            cell = PdfPCell(phrase)
            cell.colspan=2
            cell.runDirection = PdfWriter.RUN_DIRECTION_RTL
            cell.horizontalAlignment = Element.ALIGN_RIGHT
            cell.border = Rectangle.NO_BORDER
            table.addCell(cell)

            // add empty sapce

            cell = PdfPCell(Phrase("", mPrintBoldMediam))
            cell.colspan = 2
            cell.horizontalAlignment = Element.ALIGN_LEFT
            cell.border = Rectangle.NO_BORDER
            table.addCell(cell)

            document.add(table)


            val separatorr = DottedLineSeparator()

            cell = PdfPCell(Phrase(Chunk(separator1)))
            cell.colspan = 4
            cell.horizontalAlignment = Element.ALIGN_RIGHT
            cell.border = Rectangle.NO_BORDER
            table.addCell(cell)
            paragraph = Paragraph("Thank you", mPrintBoldBig)
            paragraph.alignment = Element.ALIGN_CENTER
            document.add(paragraph)

            var y = writer?.getVerticalPosition(true)


            document.close()

            if (docsize == 0f) {
                var pg = writer.pageNumber?.minus(1)
                var size = (pg * (max?.minus(30f)))?.plus(y!!)
                createPdf(dest, size)
            } else {
                generateThump()
                viewPdf()
            }

        } catch (e: Exception) {
            e.printStackTrace()

        }

    }
}
