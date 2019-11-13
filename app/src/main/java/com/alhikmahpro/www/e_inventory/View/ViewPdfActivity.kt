package com.alhikmahpro.www.e_inventory.View

import android.Manifest
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.support.v4.content.FileProvider
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast

import com.alhikmahpro.www.e_inventory.Data.DashedSeparator
import com.alhikmahpro.www.e_inventory.Data.DataContract
import com.alhikmahpro.www.e_inventory.Data.dbHelper
import com.alhikmahpro.www.e_inventory.FileUtils
import com.alhikmahpro.www.e_inventory.R
import com.itextpdf.text.pdf.draw.DottedLineSeparator

import butterknife.ButterKnife
import com.alhikmahpro.www.e_inventory.App.AppController
import com.alhikmahpro.www.e_inventory.Data.Cart
import com.alhikmahpro.www.e_inventory.FileUtils.getAppPath
import com.itextpdf.text.*
import com.itextpdf.text.pdf.*
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

class ViewPdfActivity : AppCompatActivity() {

    //    @BindView(R.id.pdfView)
//    internal var pdfView: PDFView? = null
    lateinit var mContext: Context
    lateinit var itemPrint: MenuItem
    lateinit var itemSync: MenuItem
    lateinit var itemShare: MenuItem
    lateinit var itemClear: MenuItem
    lateinit var customerName: String
    lateinit var invoiceNo: String
    lateinit var invoiceDate: String
    lateinit var salesmanId: String
    lateinit var action: String
    lateinit var paymentMode: String
    internal var discountAmount: Double = 0.toDouble()
    internal var totalAmount: Double = 0.toDouble()
    internal var netAmount: Double = 0.toDouble()
    var TAG="ViewPdfActivity"



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
        intent?.getStringExtra("DOC_NO")?.let { invoiceNo = it }
                ?: kotlin.run { invoiceNo = "invoice no" }
        intent?.getStringExtra("DOC_DATE")?.let { invoiceDate = it }
                ?: kotlin.run { invoiceDate = "invoice date" }
        intent?.getStringExtra("SALESMAN_ID")?.let { salesmanId = it }
                ?: kotlin.run { salesmanId = "Id" }
        intent?.getStringExtra("PAY_MOD")?.let { paymentMode = it }
                ?: kotlin.run { paymentMode = "Cash" }

        intent?.getDoubleExtra("DISCOUNT", 0.0)?.let { discountAmount = it }
                ?: kotlin.run { discountAmount = 0.0 }
        intent?.getDoubleExtra("TOTAL", 0.0)?.let { totalAmount = it }
                ?: kotlin.run { totalAmount = 0.0 }
        intent?.getDoubleExtra("NET", 0.0)?.let { netAmount = it }
                ?: kotlin.run { netAmount = 0.0 }


        requestPermission();
    }

    private fun requestPermission() {
        Dexter.withActivity(this@ViewPdfActivity)
                .withPermissions(
                        Manifest.permission.READ_EXTERNAL_STORAGE
                        ,Manifest.permission.WRITE_EXTERNAL_STORAGE)
                .withListener(object: MultiplePermissionsListener {
                    override fun onPermissionsChecked(report: MultiplePermissionsReport?) {
                        report?.let {
                            if(report.areAllPermissionsGranted()){
                                Log.d(TAG,"Permission granted")
                                createPdf(FileUtils.getSubDirPath(mContext, DataContract.DIR_INVOICE), 0f)

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

    private fun showSettingDialog() {
        val builder = AlertDialog.Builder(this@ViewPdfActivity)
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






    private fun viewPdf() {
        Log.d(TAG, "viewPdf: ")
        // if (fileName != null && !fileName.isEmpty()){

        val name = FileUtils.getSubDirPath(mContext, DataContract.DIR_INVOICE) + invoiceNo + ".pdf"
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
        itemClear = menu.findItem(R.id.action_clear)
        itemSync.isVisible = false
        itemClear.isVisible=false

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
            sharePdf()
            Log.d(TAG,"action share")

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
        val fPath = FileUtils.getSubDirPath(applicationContext, DataContract.DIR_INVOICE) + invoiceNo + ".pdf"
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
        val fName = dest+invoiceNo+".pdf"
        if (File(fName).exists()) {
            File(fName).delete()
        }

        val helper = dbHelper(this)
        val database = helper.readableDatabase
        val cursor = helper.getPaperSettings(database)
        var companyName = "ecorn.com"
        var companyAddress = "+974 123456"
        var companyPhone = "mail@encorn.com"
        var footer = "Thank you"

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
            var max = 300f;
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
            val decimalFormat = DecimalFormat("0.00")
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
//                bmp = BitmapFactory.decodeByteArray(img, 0, img!!.size)
//                val scaledBitmap = scaleDown(bmp, 80f, true)
//                val stream = ByteArrayOutputStream()
//                scaledBitmap.compress(Bitmap.CompressFormat.PNG, 100, stream)
//                val image = Image.getInstance(stream.toByteArray())
//                image.alignment = Image.ALIGN_TOP or Image.ALIGN_CENTER
//                document.add(image)
                bmp = getBitmapFromAssets("icon.png")
                val scaledBitmap = ViewPdfActivity.scaleDown(bmp, 80f, true)
                val stream = ByteArrayOutputStream()
                scaledBitmap.compress(Bitmap.CompressFormat.PNG, 100, stream)
                val image = Image.getInstance(stream.toByteArray())
                image.alignment = Image.ALIGN_TOP or Image.ALIGN_CENTER
                document.add(image)
            } catch (e: Exception) {
                e.printStackTrace()
            }


            // Column with a writer
            paragraph = Paragraph(companyName, mPrintBoldBig)
            paragraph.alignment = Element.ALIGN_CENTER
            document.add(paragraph)

            paragraph = Paragraph(companyAddress, mPrintBoldMediam)
            paragraph.alignment = Element.ALIGN_CENTER
            document.add(paragraph)

            paragraph = Paragraph(companyPhone, mPrintBoldMediam)
            paragraph.alignment = Element.ALIGN_CENTER
            document.add(paragraph)

            val separator = DashedSeparator()
            separator.percentage = 59500f / 523f
            val linebreak = Chunk(separator)


            paragraph = Paragraph("Receipt#" + invoiceNo, mPrintBoldMediam)
            paragraph.alignment = Element.ALIGN_LEFT
            document.add(paragraph)

            paragraph = Paragraph("Salesman: "+salesmanId, mPrintBoldMediam)
            paragraph.alignment = Element.ALIGN_LEFT
            document.add(paragraph)

            paragraph = Paragraph("Payment Mode:"+paymentMode, mPrintBoldMediam)
            paragraph.alignment = Element.ALIGN_LEFT
            document.add(paragraph)

            paragraph = Paragraph("Date:" + invoiceDate, mPrintBoldMediam)
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
            for (mm in Cart.mCart) {
                val item = mm.productName
                val qty = mm.qty.toString()
                val price = decimalFormat.format(mm.rate).toString()
                val sub_total = decimalFormat.format(mm.net).toString()
                val item_format = price + " X " + mm.qty

                cell = PdfPCell(Paragraph("item", mPrintNormal))
                cell.horizontalAlignment = Element.ALIGN_LEFT
                cell.border = Rectangle.NO_BORDER
                table.addCell(cell)

                cell = PdfPCell(Phrase(Chunk(price, mPrintNormal)))
                cell.horizontalAlignment = Element.ALIGN_RIGHT
                cell.border = Rectangle.NO_BORDER
                table.addCell(cell)

                cell = PdfPCell(Phrase(Chunk(qty, mPrintNormal)))
                cell.horizontalAlignment = Element.ALIGN_CENTER
                cell.border = Rectangle.NO_BORDER
                table.addCell(cell)

                cell = PdfPCell(Phrase(Chunk(sub_total, mPrintNormal)))
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

            cell = PdfPCell(Phrase(Cart.mCart.size.toString(), mPrintBoldMediam))
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

            cell = PdfPCell(Phrase((currencyFormatter(totalAmount)
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

            cell = PdfPCell(Phrase((currencyFormatter(discountAmount)
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

            cell = PdfPCell(Phrase((currencyFormatter(netAmount) ?: "0"), mPrintBoldMediam))
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
            paragraph = Paragraph(footer, mPrintBoldBig)
            paragraph.alignment = Element.ALIGN_CENTER
            document.add(paragraph)
            var y = writer?.getVerticalPosition(true)
            document.close()

            if (docsize == 0f) {
                var pg = writer.pageNumber?.minus(1)
                var size = (pg * (max?.minus(30f)))?.plus(y!!)
                createPdf(dest, size)
            } else {

                viewPdf()
            }

        } catch (e: Exception) {
            e.printStackTrace()
        }
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
    fun currencyFormatter(amount: Double): String {
        val format = NumberFormat.getCurrencyInstance()
        val pattern = (format as DecimalFormat).toPattern()
        val newPattern = pattern.replace("\u00A4", "").trim { it <= ' ' }
        val newFormat = DecimalFormat(newPattern)
        return newFormat.format(amount).toString()

    }

    private fun sharePdf() {
        Log.d("share","clicked");
        val path = FileUtils.getSubDirPath(mContext, DataContract.DIR_INVOICE) + invoiceNo + ".pdf"
        val outputFile = File(path)
        if(outputFile.exists()){
            // Uri uri = Uri.fromFile(outputFile);
            val uri = FileProvider.getUriForFile(mContext, mContext.packageName + ".provider", outputFile)
            val share = Intent()
            share.action = Intent.ACTION_SEND
            share.type = "application/pdf"
            share.putExtra(Intent.EXTRA_STREAM, uri)
            mContext.startActivity(Intent.createChooser(share, "Share to :"))
        }
        else
            Toast.makeText(this,"File not found",Toast.LENGTH_LONG).show()
    }

    override fun onBackPressed() {
        clearActivity()
    }
    private fun clearActivity() {
        Cart.mCart.clear()
        // finish all activity and go to home activity
        val intent = Intent(applicationContext, ListSalesActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        startActivity(intent)
    }
}
