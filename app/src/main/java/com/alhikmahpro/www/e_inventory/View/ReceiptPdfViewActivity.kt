package com.alhikmahpro.www.e_inventory.View

import android.Manifest
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.preference.PreferenceManager
import android.provider.Settings
import android.support.v4.content.FileProvider
import android.support.v7.app.AlertDialog
import android.support.v7.widget.Toolbar
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import butterknife.ButterKnife
import com.alhikmahpro.www.e_inventory.App.AppController
import com.alhikmahpro.www.e_inventory.Data.DashedSeparator
import com.alhikmahpro.www.e_inventory.Data.DataContract
import com.alhikmahpro.www.e_inventory.Data.dbHelper
import com.alhikmahpro.www.e_inventory.FileUtils
import com.alhikmahpro.www.e_inventory.R
import com.itextpdf.text.*
import com.itextpdf.text.pdf.*
import com.itextpdf.text.pdf.draw.DottedLineSeparator
import com.karumi.dexter.Dexter
import com.karumi.dexter.MultiplePermissionsReport
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.multi.MultiplePermissionsListener
import com.shockwave.pdfium.PdfiumCore
import kotlinx.android.synthetic.main.activity_view_pdf.*
import java.io.*
import java.text.DecimalFormat
import java.text.NumberFormat

class ReceiptPdfViewActivity : AppCompatActivity() {
    lateinit var mContext: Context
    lateinit var itemPrint: MenuItem
    lateinit var itemSync: MenuItem
    lateinit var itemShare: MenuItem
    lateinit var itemClear: MenuItem
    lateinit var customerName: String
    lateinit var receiptNo: String
    lateinit var receiptDate: String
    lateinit var salesmanId: String
    lateinit var action: String
    internal var amount: Double = 0.toDouble()
    var PREF_KEY_HEADER1 = "key_header_1"
    var PREF_KEY_HEADER2 = "key_header_2"
    var PREF_KEY_HEADER3 = "key_header_3"
    var PREF_KEY_FOOTER = "key_footer"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_receipt_pdf_view)
        ButterKnife.bind(this)
        val mIntent = intent
        val toolbar = findViewById<View>(R.id.toolbar) as Toolbar
        setSupportActionBar(toolbar)
        supportActionBar!!.title = "Receipt "
        mContext = applicationContext
        var TAG = "ReceiptPdfViewActivity"


        val intent = intent
        // action = intent.getStringExtra("TYPE")
        intent?.getStringExtra("CUSTOMER_NAME")?.let { customerName = it }
                ?: kotlin.run { customerName = "customer name" }
        intent?.getStringExtra("RECEIPT_NO")?.let { receiptNo = it }
                ?: kotlin.run { receiptNo = "receipt no" }
        intent?.getStringExtra("RECEIPT_DATE")?.let { receiptDate = it }
                ?: kotlin.run { receiptDate = "receipt date" }
        intent?.getStringExtra("SALESMAN_ID")?.let { salesmanId = it }
                ?: kotlin.run { salesmanId = "Id" }
        intent?.getDoubleExtra("REC_AMOUNT", 0.0)?.let { amount = it }
                ?: kotlin.run { amount = 0.0 }

        Log.d("TAG", "Received " + amount)

        requestPermission();
    }

    private fun requestPermission() {
        Dexter.withActivity(this@ReceiptPdfViewActivity)
                .withPermissions(
                        Manifest.permission.READ_EXTERNAL_STORAGE
                        , Manifest.permission.WRITE_EXTERNAL_STORAGE)
                .withListener(object : MultiplePermissionsListener {
                    override fun onPermissionsChecked(report: MultiplePermissionsReport?) {
                        report?.let {
                            if (report.areAllPermissionsGranted()) {
                                createPdf(FileUtils.getSubDirPath(mContext, DataContract.DIR_RECEIPT), 0f)
                            }
                        }
                    }

                    override fun onPermissionRationaleShouldBeShown(
                            permissions: MutableList<PermissionRequest>?,
                            token: PermissionToken?
                    ) {
                        // Remember to invoke this method when the custom rationale is closed
                        // or just by default if you don't want to use any custom rationale.
                        token?.continuePermissionRequest()
                    }
                })
                .withErrorListener {
                    showSettingDialog()

                }
                .check()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.tool_bar, menu)
        itemPrint = menu.findItem(R.id.action_print)
        itemSync = menu.findItem(R.id.action_sync)
        itemShare = menu.findItem(R.id.action_share)
        itemClear = menu.findItem(R.id.action_clear)
        itemSync.isVisible = false
        itemClear.isVisible = false

        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        when (item.itemId) {
            R.id.action_print -> {
                Log.d(TAG,"Print action")
                hsPrint()
                return true
            }
            R.id.action_share -> {
                Log.d(TAG,"share action")
                sharePdf()
                return true
            }

        }
        return super.onOptionsItemSelected(item)

    }


    private fun showSettingDialog() {
        val builder = AlertDialog.Builder(this@ReceiptPdfViewActivity)
        builder.setTitle("Need Permissions")
        builder.setMessage("This app needs permission to use this feature. You can grant them in app settings.")
        builder.setPositiveButton("GOTO SETTINGS") { dialog, which ->
            dialog.cancel()
            openSettings()
        }
        builder.setNegativeButton("Cancel") { dialog, which -> dialog.cancel() }
        builder.show()
    }

    private fun openSettings() {
        val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
        val uri = Uri.fromParts("package", packageName, null)
        intent.data = uri
        startActivityForResult(intent, 101)
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
        //Log.d(ViewPdfActivity.TAG, "viewPdf: ")
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

    private fun sharePdf() {
        Log.d("share","clicked");
        val path = FileUtils.getSubDirPath(mContext, DataContract.DIR_RECEIPT) + receiptNo + ".pdf"
        val outputFile = File(path)
        if(outputFile.exists()){
            // Uri uri = Uri.fromFile(outputFile);
            val uri = FileProvider.getUriForFile(mContext, mContext.packageName + ".provider", outputFile)
            val share = Intent()
            share.action = Intent.ACTION_SEND
            share.type = "application/pdf"
            share.putExtra(Intent.EXTRA_STREAM, uri)
            startActivity(Intent.createChooser(share, "Share to :"))
        }else{
            Toast.makeText(this,"File not found",Toast.LENGTH_LONG).show()
        }


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
        val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this)
        var header1 =sharedPreferences.getString(PREF_KEY_HEADER1, "0")
        var header2 = sharedPreferences.getString(PREF_KEY_HEADER2, "0")
        var header3 = sharedPreferences.getString(PREF_KEY_HEADER3, "0")
        var footer = sharedPreferences.getString(PREF_KEY_FOOTER, "0")


//        val bmp: Bitmap
//        var img: ByteArray? = null
//
//        if (cursor.moveToFirst()) {
//            companyName = cursor.getString(cursor.getColumnIndex(DataContract.PaperSettings.COL_COMPANY_NAME))
//            companyAddress = cursor.getString(cursor.getColumnIndex(DataContract.PaperSettings.COL_COMPANY_ADDRESS))
//            companyPhone = cursor.getString(cursor.getColumnIndex(DataContract.PaperSettings.COL_COMPANY_PHONE))
//            Log.d(TAG,"name arabic"+companyAddress)
//            footer = cursor.getString(cursor.getColumnIndex(DataContract.PaperSettings.COL_FOOTER))
//            img = cursor.getBlob(cursor.getColumnIndex(DataContract.PaperSettings.COL_LOGO))
//            //Log.d(ViewPdfActivity.TAG, "image : " + img!!)
//
//        }
//
//        cursor.close()
//        database.close()



        try {

            val width = 480f
            var max = 220f;
            val fontSmall = 30f;
            val fontMed = 36f;
            val fontBig = 40f;

            val baseFont = BaseFont.createFont("assets/brandon_bold.otf", "UTF-8", BaseFont.EMBEDDED)
            val baseFontArial = BaseFont.createFont("assets/fonts/Arial.ttf", "UTF-8", BaseFont.EMBEDDED)
            val arabicBaseFont = BaseFont.createFont("assets/fonts/mll.ttf", BaseFont.IDENTITY_H, BaseFont.EMBEDDED)


            val mArabicFont = Font(arabicBaseFont, fontSmall, Font.NORMAL, BaseColor.BLACK)
            val mPrintNormal = Font(baseFontArial, fontSmall, Font.NORMAL, BaseColor.BLACK)
            val mPrintMedium = Font(baseFontArial, fontMed, Font.NORMAL, BaseColor.BLACK)
            val mPrintBoldBig = Font(baseFontArial, fontBig, Font.NORMAL, BaseColor.BLACK)

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
            try {
                val bmp = getBitmapFromAssets("icon.png")
                val scaledBitmap = ViewPdfActivity.scaleDown(bmp, 80f, true)
                val stream = ByteArrayOutputStream()
                scaledBitmap.compress(Bitmap.CompressFormat.PNG, 100, stream)
                val image = Image.getInstance(stream.toByteArray())
                image.alignment = Image.ALIGN_TOP or Image.ALIGN_CENTER
                document.add(image)
            } catch (e: Exception) {
                e.printStackTrace()
            }

            val table2: PdfPTable
            val columnWidths2 = floatArrayOf(width - 20)
            table2 = PdfPTable(columnWidths2)
            table2.totalWidth = width - 20
            table2.isLockedWidth = true
            table2.horizontalAlignment = Element.ALIGN_LEFT

            var phraseh1 = Phrase(
                    header1, mArabicFont)
            cell = PdfPCell(phraseh1)
            cell.runDirection = PdfWriter.RUN_DIRECTION_RTL
            cell.horizontalAlignment = Element.ALIGN_CENTER
            cell.border = Rectangle.NO_BORDER
            table2.addCell(cell)

            var phraseh2 = Phrase(
                    header2,mPrintNormal)
            cell = PdfPCell(phraseh2)
            cell.horizontalAlignment = Element.ALIGN_CENTER
            cell.border = Rectangle.NO_BORDER
            table2.addCell(cell)

//            var phraseh3 = Phrase(
//                    companyAddress, mPrintNormal)
//            cell = PdfPCell(phraseh3)
//            cell.horizontalAlignment = Element.ALIGN_CENTER
//            cell.border = Rectangle.NO_BORDER
//            table2.addCell(cell)

            var phraseh4 = Phrase(
                    header3, mPrintNormal)
            cell = PdfPCell(phraseh4)
            cell.horizontalAlignment = Element.ALIGN_CENTER
            cell.border = Rectangle.NO_BORDER
            table2.addCell(cell)
            document.add(table2)
           // document.add( Chunk.NEWLINE )


            val separator = DashedSeparator()
            separator.percentage = 59500f / 523f
            val linebreak = Chunk(separator)

            //paragraph = Paragraph("Receipt# " + salesmanId+"/"+receiptNo, mPrintNormal)
            var res=salesmanId+"/"+receiptNo
            paragraph = Paragraph("ReceiptNo:"+res, mPrintNormal)
            paragraph.alignment = Element.ALIGN_LEFT
            document.add(paragraph)

            paragraph = Paragraph(customerName, mPrintNormal)
            paragraph.alignment = Element.ALIGN_LEFT
            document.add(paragraph)

            paragraph = Paragraph("Date " + receiptDate, mPrintNormal)
            paragraph.alignment = Element.ALIGN_LEFT
            document.add(paragraph)
            document.add(linebreak)

            val snWidth = 0f
            val p = (width - 10) / 3
            val re = ((p * 2) - snWidth) / 3

            //  val rem = re / 3

            val columnWidths = floatArrayOf(p, re, re, p)
            table = PdfPTable(columnWidths)
            table.totalWidth = width - 10
            table.isLockedWidth = true
            table.horizontalAlignment = Element.ALIGN_LEFT
//
//            cell = PdfPCell(Phrase(Chunk("Item", mPrintNormal)))
//            cell.horizontalAlignment = Element.ALIGN_LEFT
//            cell.border = Rectangle.NO_BORDER
//            table.addCell(cell)
//
//            cell = PdfPCell(Phrase(Chunk("", mPrintNormal)))
//            cell.horizontalAlignment = Element.ALIGN_RIGHT
//            cell.border = Rectangle.NO_BORDER
//            table.addCell(cell)
//
//            cell = PdfPCell(Phrase(Chunk("", mPrintNormal)))
//            cell.horizontalAlignment = Element.ALIGN_CENTER
//            cell.border = Rectangle.NO_BORDER
//            table.addCell(cell)
//
//            cell = PdfPCell(Phrase(Chunk("", mPrintNormal)))
//            cell.horizontalAlignment = Element.ALIGN_RIGHT
//            cell.border = Rectangle.NO_BORDER
//            table.addCell(cell)
//
//            val separato = DashedSeparator()
//            separato.percentage = 59500f / 523f
//            cell = PdfPCell(Phrase(Chunk(separato)))
//            cell.colspan = 5
//            cell.horizontalAlignment = Element.ALIGN_RIGHT
//            cell.border = Rectangle.NO_BORDER
//            table.addCell(cell)
//            table.headerRows = 1



            cell = PdfPCell(Paragraph("Received Amount", mPrintNormal))
            cell.horizontalAlignment = Element.ALIGN_LEFT
            cell.border = Rectangle.NO_BORDER
            table.addCell(cell)

            cell = PdfPCell(Phrase(Chunk("", mPrintNormal)))
            cell.horizontalAlignment = Element.ALIGN_RIGHT
            cell.border = Rectangle.NO_BORDER
            table.addCell(cell)

            cell = PdfPCell(Phrase(Chunk("", mPrintNormal)))
            cell.horizontalAlignment = Element.ALIGN_CENTER
            cell.border = Rectangle.NO_BORDER
            table.addCell(cell)

            cell = PdfPCell(Phrase(Chunk(currencyFormatter(amount), mPrintNormal)))
            cell.horizontalAlignment = Element.ALIGN_RIGHT
            cell.border = Rectangle.NO_BORDER
            table.addCell(cell)


            // arabic

//            val phrase = Phrase("المجموع الصافي ", mArabicFont)
//            cell = PdfPCell(phrase)
//            cell.runDirection = PdfWriter.RUN_DIRECTION_RTL
//            cell.horizontalAlignment = Element.ALIGN_RIGHT
//            cell.border = Rectangle.NO_BORDER
//            table.addCell(cell)
//
//
//            cell = PdfPCell(Phrase(Chunk("", mPrintNormal)))
//            cell.horizontalAlignment = Element.ALIGN_RIGHT
//            cell.border = Rectangle.NO_BORDER
//            table.addCell(cell)
//
//            cell = PdfPCell(Phrase(Chunk("", mPrintNormal)))
//            cell.horizontalAlignment = Element.ALIGN_CENTER
//            cell.border = Rectangle.NO_BORDER
//            table.addCell(cell)
//
//            cell = PdfPCell(Phrase(Chunk(" ", mPrintNormal)))
//            cell.horizontalAlignment = Element.ALIGN_RIGHT
//            cell.border = Rectangle.NO_BORDER
//            table.addCell(cell)
            // }

            val separator1 = DashedSeparator()
            separator1.percentage = 59500f / 523f

            cell = PdfPCell(Phrase(Chunk(separator1)))
            cell.colspan = 4
            cell.horizontalAlignment = Element.ALIGN_RIGHT
            cell.border = Rectangle.NO_BORDER
            table.addCell(cell)


//            cell = PdfPCell(Phrase("Total Items", mPrintBoldMediam))
//            cell.colspan = 2
//            cell.horizontalAlignment = Element.ALIGN_LEFT
//            cell.border = Rectangle.NO_BORDER
//            table.addCell(cell)
//
//            cell = PdfPCell(Phrase("5", mPrintBoldMediam))
//            cell.colspan = 2
//            cell.horizontalAlignment = Element.ALIGN_RIGHT
//            cell.border = Rectangle.NO_BORDER
//            table.addCell(cell)
//
//
//            cell = PdfPCell(Phrase(Chunk(separator1)))
//            cell.colspan = 4
//            cell.horizontalAlignment = Element.ALIGN_RIGHT
//            cell.border = Rectangle.NO_BORDER
//            table.addCell(cell)


            cell = PdfPCell(Phrase("Total", mPrintNormal))
            cell.colspan = 2
            cell.horizontalAlignment = Element.ALIGN_LEFT
            cell.border = Rectangle.NO_BORDER
            table.addCell(cell)

            cell = PdfPCell(Phrase((currencyFormatter(amount) ?: "00.00"), mPrintNormal))
            cell.colspan = 2
            cell.horizontalAlignment = Element.ALIGN_RIGHT
            cell.border = Rectangle.NO_BORDER
            table.addCell(cell)

            cell = PdfPCell(Phrase("Discount", mPrintNormal))
            cell.colspan = 2
            cell.horizontalAlignment = Element.ALIGN_LEFT
            cell.border = Rectangle.NO_BORDER
            table.addCell(cell)

            cell = PdfPCell(Phrase((currencyFormatter(0.0)
                    ?: "0"), mPrintNormal))
            cell.colspan = 2
            cell.horizontalAlignment = Element.ALIGN_RIGHT
            cell.border = Rectangle.NO_BORDER
            table.addCell(cell)

            cell = PdfPCell(Phrase(Chunk(separator1)))
            cell.colspan = 4
            cell.horizontalAlignment = Element.ALIGN_RIGHT
            cell.border = Rectangle.NO_BORDER
            table.addCell(cell)


            cell = PdfPCell(Phrase("Net Total", mPrintNormal))
            cell.colspan = 2
            cell.horizontalAlignment = Element.ALIGN_LEFT
            cell.border = Rectangle.NO_BORDER
            table.addCell(cell)

            cell = PdfPCell(Phrase((currencyFormatter(amount) ?: "0"), mPrintNormal))
            cell.colspan = 2
            cell.horizontalAlignment = Element.ALIGN_RIGHT
            cell.border = Rectangle.NO_BORDER
            table.addCell(cell)


            //net total arabic
//            val phraseAr = Phrase(
//                    "المجموع الصافي ", mArabicFont)
//            cell = PdfPCell(phraseAr)
//            cell.colspan = 2
//            cell.runDirection = PdfWriter.RUN_DIRECTION_RTL
//            cell.horizontalAlignment = Element.ALIGN_RIGHT
//            cell.border = Rectangle.NO_BORDER
//            table.addCell(cell)
//
//            // add empty sapce
//
//            cell = PdfPCell(Phrase("", mPrintBoldMediam))
//            cell.colspan = 2
//            cell.horizontalAlignment = Element.ALIGN_LEFT
//            cell.border = Rectangle.NO_BORDER
//            table.addCell(cell)

            document.add(table)


            val separatorr = DottedLineSeparator()

            cell = PdfPCell(Phrase(Chunk(separator1)))
            cell.colspan = 4
            cell.horizontalAlignment = Element.ALIGN_RIGHT
            cell.border = Rectangle.NO_BORDER
            table.addCell(cell)
            paragraph = Paragraph(footer, mPrintNormal)
            paragraph.alignment = Element.ALIGN_CENTER
            document.add(paragraph)

            var y = writer?.getVerticalPosition(true)


            document.close()

            if (docsize == 0f) {
                var  tableHeightTotal =table?.totalHeight;
                var pg = writer.pageNumber
                //var remove=pg*50f
                createPdf(dest, tableHeightTotal+900f)
            } else {
                generateThump()
                viewPdf()
            }

        } catch (e: Exception) {
            e.printStackTrace()

        }

    }

    fun currencyFormatter(amount: Double): String {
        val format = NumberFormat.getCurrencyInstance()
        val pattern = (format as DecimalFormat).toPattern()
        val newPattern = pattern.replace("\u00A4", "").trim { it <= ' ' }
        val newFormat = DecimalFormat(newPattern)
        return newFormat.format(amount).toString()

    }

    private fun getBitmapFromAssets(fileName: String): Bitmap {
        val assetManager = assets
        var inputStream: InputStream? = null
        try {
            inputStream = assetManager.open("images/$fileName")
        } catch (e: IOException) {
            e.printStackTrace()
        }

        return BitmapFactory.decodeStream(inputStream)
    }
    private fun clearActivity() {
        // finish all activity and go to home activity
        val intent = Intent(applicationContext, ListReceiptActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        startActivity(intent)
    }

    override fun onBackPressed() {
        super.onBackPressed()
        clearActivity()
        finish()
    }
}

