
//package com.mycodlabs.pos.pdf.ui
//
//import android.content.Context
//import android.content.Intent
//import android.graphics.Bitmap
//import android.graphics.BitmapFactory
//import android.graphics.pdf.PdfRenderer
//import android.location.Location
//import android.net.Uri
//import android.os.*
//import android.preference.PreferenceManager
//import android.support.v7.app.AppCompatActivity
//import android.util.Base64
//import android.util.Log
//import android.view.Menu
//import android.view.MenuItem
//import com.alhikmahpro.www.e_inventory.Data.DashedSeparator
//
//
//import com.itextpdf.text.BaseColor
//import com.itextpdf.text.Chunk
//import com.itextpdf.text.Document
//import com.itextpdf.text.Element
//import com.itextpdf.text.Font
//import com.itextpdf.text.Paragraph
//import com.itextpdf.text.Phrase
//import com.itextpdf.text.Rectangle
//import com.itextpdf.text.pdf.BaseFont
//import com.itextpdf.text.pdf.PdfContentByte
//import com.itextpdf.text.pdf.PdfPCell
//import com.itextpdf.text.pdf.PdfPCellEvent
//import com.itextpdf.text.pdf.PdfPTable
//import com.itextpdf.text.pdf.PdfWriter
//import com.itextpdf.text.pdf.codec.BmpImage
//import com.itextpdf.text.pdf.draw.DottedLineSeparator
//import com.mocoo.hang.rtprinter.main.RTApplication
//
//
//import com.mycodlabs.pos.app.AppController
//
//
//
//
//import org.json.JSONObject
//
//
//
//import java.io.File
//import java.io.FileOutputStream
//
//import com.mycodlabs.pos.pdf.permission.PermissionsActivity.PERMISSION_REQUEST_CODE
//import com.mycodlabs.pos.pdf.permission.PermissionsChecker.REQUIRED_PERMISSION
//
//import com.shockwave.pdfium.PdfiumCore
//
//
//import java.io.UnsupportedEncodingException
//import java.math.BigDecimal
//import java.text.DecimalFormat


//class MainPDFReceiptActivity : AppCompatActivity() {
//    override fun locationupdate(location: Location) {
//
//    }
//
//    var mContext: Context? = null
    // var checker: PermissionsChecker? = null
//     var register: Register? = null
//     var paragraph: Paragraph? = null
//     var table: PdfPTable? = null
//     var cell: PdfPCell? = null
//     var cb: PdfContentByte? = null
//     var saleid=0
//    var paymnentRecordID=0
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        setContentView(R.layout.activity_pdf_main)
//        register = Register.getInstance()
//        saleid =intent.getIntExtra(SALES_ID,0)
//        paymnentRecordID =intent.getIntExtra(PYMENT_RECORD,0)
//        register?.setCurrentSale(saleid)
//        mContext = applicationContext
//        sendInvoice
//        checker = PermissionsChecker(this)
//            if (checker?.lacksPermissions(*REQUIRED_PERMISSION)!!) {
//                PermissionsActivity.startActivityForResult(this@MainPDFReceiptActivity, PERMISSION_REQUEST_CODE, *REQUIRED_PERMISSION)
//            } else {
//                createPdff(FileUtils.getAppPath(mContext) + "123.pdf", 0f)
//
//            }
//
//        sendInvoice?.setOnClickListener {
//
//            startSalesDataSyncService(saleid?.toString())
//        }
//        print?.setOnClickListener {
//
////       startActivity(Intent(this, MainActivity::class.java))
////            finish()
//
//            if(PreferenceManager.getDefaultSharedPreferences(application).getBoolean(getString(R.string.key_print_enabled),false))
//            {
//                if(isNFCE){hsPrintNFC()}
//                else
//                  hsPrint()
//            }
//
//
//        }
//
//    }
//
//    override fun onSyncCompleted() {
//        super.onSyncCompleted()
//        linkSales()
//
//    }
//
//
//
//    private fun linkSales(){
//
//        requestDidStart()
//        var salesRecord=SalesDbo.getInstance(activityContext)?.getSaleById(saleid)
//        var map = HashMap<String, String?>()
//        map["id"] = salesRecord?.serverRecordId
//        var requestBody= RequestBody.create(MediaType.parse("application/json; charset=utf-8"), JSONObject(map).toString())
//        MCRetrofitClient.create(activityContext).createNfcsales(requestBody).enqueue(object : Callback<MCLinkSalesResponse> {
//            override fun onFailure(call: Call<MCLinkSalesResponse>, t: Throwable) {
//                requestDidFinish()
//                with(Intent()) {
//                }
//            }
//
//
//            override fun onResponse(call: Call<MCLinkSalesResponse>, response: Response<MCLinkSalesResponse>) {
//                val userDataResponse = response.body()
//                userDataResponse?.let {
//                    requestDidFinish()
//                    if(it.status?.equals("success")) {
//                        var decodedBytes = Base64.decode(it.data?.toByteArray(), Base64.DEFAULT);
//                        var file = File(FileUtils.getAppPath(applicationContext) + "123.pdf")
//                        var fop = FileOutputStream(file);
//                        fop.write(decodedBytes);
//                        fop.flush();
//                        fop.close();
//                        pdfView?.recycle()
//                        generateImageNFCE()
//
//                        showPdf()
//
//                    }
//                    else
//                    {
//                        showToast("Error"+it.data);
//                    }
//                } ?: kotlin.run { }
//            }
//
//        })
//    }
//
//
//
//
//    override fun onPermissionGranted() {
//        createPdff(FileUtils.getAppPath(mContext) + "123.pdf", 0f)
//    }
//
//    fun getUnicodeString(myString: String): String {
//        var text = ""
//        try {
//
//            val utf8Bytes = myString.toByteArray(charset("UTF8"))
//            text = String(utf8Bytes, charset("UTF8"))
//
//        } catch (e: UnsupportedEncodingException) {
//            e.printStackTrace()
//        }
//
//        return text
//    }
//
//     fun createPdff(dest: String, docsize: Float) {
//
//        var paragraph: Paragraph? = null
//        val table: PdfPTable
//        var cell: PdfPCell
//        val cb: PdfContentByte
//        if (File(dest).exists()) {
//            File(dest).delete()
//        }
//
//        try {
//
//            val width = 480f
//            var max = 300f;
//            var fontSamll = 28f;
//            var fontBig = 45f;
//            var fontMediam = 28f;
//            val printNormal = BaseFont.createFont("assets/CourierBOLD.ttf", "UTF-8", BaseFont.EMBEDDED)//BaseFont.createFont("assets/cour.ttf", "UTF-8", BaseFont.EMBEDDED)
//            val printBold = BaseFont.createFont("assets/CourierBOLD.ttf", "UTF-8", BaseFont.EMBEDDED)
//            val mPrintNormal = Font(printNormal, fontSamll, Font.NORMAL, BaseColor.BLACK)
//            val mPrintBoldMediam = Font(printBold, fontMediam, Font.NORMAL, BaseColor.BLACK)
//            val mPrintBoldBig = Font(printBold, fontBig, Font.NORMAL, BaseColor.BLACK)
//            if (docsize != 0f) {
//                max = docsize
//            }
//            val pagesize = Rectangle(width, max)
//            // Document with predefined page size
//            val document = Document(pagesize, 5f, 5f, 0f, 0f)
//            // Getting PDF Writer
//            val writer = PdfWriter.getInstance(document, FileOutputStream(dest))
//            document.open()
//            // Column with a writer
//            paragraph = Paragraph(MCPreferences.getPrintHeadLine1(application),mPrintBoldBig)
//            paragraph.alignment = Element.ALIGN_CENTER
//            document.add(paragraph)
//
//            paragraph = Paragraph(MCPreferences.getPrintHeadLine2(application),mPrintBoldMediam)
//            paragraph.alignment = Element.ALIGN_CENTER
//            document.add(paragraph)
//
//            paragraph = Paragraph(MCPreferences.getPrintHeadLine3(application),mPrintBoldMediam)
//            paragraph.alignment = Element.ALIGN_CENTER
//            document.add(paragraph)
//
//            val separator = DashedSeparator()
//            separator.percentage = 59500f / 523f
//            val linebreak = Chunk(separator)
//            var customer= CustomerDbao?.getInstance(applicationContext)?.getCustomerByid(register?.getCurrentSale()?.customerId!!)
////            paragraph = Paragraph("To: "+customer?.customerName,mPrintBoldMediam)
////            paragraph.alignment = Element.ALIGN_LEFT
////            document.add(paragraph)
//            when(register?.getCurrentSale()?.documenType) {
//               DocumentTypes.DOC_INVOICE?.toString()-> {
//                   paragraph = Paragraph("${getString(R.string.invoice_number)}: " + register?.getCurrentSale()?.invoiceNo, mPrintBoldMediam)
//                   paragraph.alignment = Element.ALIGN_LEFT
//                   document.add(paragraph)
//                   paragraph = Paragraph("${getString(R.string.Invoice_type)}: "+getString(R.string.sales_invoice) , mPrintBoldMediam)
//                   paragraph.alignment = Element.ALIGN_LEFT
//                   document.add(paragraph)
//               }
//                DocumentTypes.DOC_SALES_ORDER?.toString()-> {
//                    paragraph = Paragraph("Order No: " + register?.getCurrentSale()?.invoiceNo, mPrintBoldMediam)
//                    paragraph.alignment = Element.ALIGN_LEFT
//                    document.add(paragraph)
//                    paragraph = Paragraph("Bill Type: Sales Order", mPrintBoldMediam)
//                    paragraph.alignment = Element.ALIGN_LEFT
//                    document.add(paragraph)
//                }
//                DocumentTypes.DOC_RETURN_ORDER?.toString()-> {
//                    paragraph = Paragraph("Return No: " + register?.getCurrentSale()?.invoiceNo, mPrintBoldMediam)
//                    paragraph.alignment = Element.ALIGN_LEFT
//                    document.add(paragraph)
//                    paragraph = Paragraph("Bill Type: Return Order", mPrintBoldMediam)
//                    paragraph.alignment = Element.ALIGN_LEFT
//                    document.add(paragraph)
//                }
//
//                DocumentTypes.DOC_RETURN_INVOICE?.toString()-> {
//                    paragraph = Paragraph("Return No: " + register?.getCurrentSale()?.invoiceNo, mPrintBoldMediam)
//                    paragraph.alignment = Element.ALIGN_LEFT
//                    document.add(paragraph)
//                    paragraph = Paragraph("Bill Type: Return Invoice", mPrintBoldMediam)
//                    paragraph.alignment = Element.ALIGN_LEFT
//                    document.add(paragraph)
//                }
//
//            }
//            paragraph = Paragraph("${getString(R.string.currency)}: Real(R$)",mPrintBoldMediam)
//            paragraph.alignment = Element.ALIGN_LEFT
//            document.add(paragraph)
//
//            paragraph = Paragraph(getString(R.string.bill_date)+": "+register?.getCurrentSale()?.startTime,mPrintBoldMediam)
//            paragraph.alignment = Element.ALIGN_LEFT
//            document.add(paragraph)
//
//
//
//            document.add(linebreak)
//            val snWidth=0f
//            val p = (width - 10) / 3
//            val re = ((p * 2)-snWidth) / 3
//
//          //  val rem = re / 3
//
//            val columnWidths = floatArrayOf( p, re , re , re )
//            table = PdfPTable(columnWidths)
//            table.totalWidth = width - 10
//            table.isLockedWidth = true
//            table.horizontalAlignment = Element.ALIGN_LEFT
//
////            cell = PdfPCell(Phrase(Chunk("SN",mPrintNormal)))
////            cell.horizontalAlignment = Element.ALIGN_LEFT
////            cell.border = Rectangle.NO_BORDER
////            table.addCell(cell)
//
//            cell = PdfPCell(Phrase(Chunk(getString(R.string.item_text),mPrintNormal)))
//            cell.horizontalAlignment = Element.ALIGN_LEFT
//            cell.border = Rectangle.NO_BORDER
//            table.addCell(cell)
//
//            cell = PdfPCell(Phrase(Chunk(getString(R.string.reate),mPrintNormal)))
//            cell.horizontalAlignment = Element.ALIGN_RIGHT
//            cell.border = Rectangle.NO_BORDER
//            table.addCell(cell)
//
//            cell = PdfPCell(Phrase(Chunk(getString(R.string.qty),mPrintNormal)))
//            cell.horizontalAlignment = Element.ALIGN_CENTER
//            cell.border = Rectangle.NO_BORDER
//            table.addCell(cell)
//
//            cell = PdfPCell(Phrase(Chunk(getString(R.string.total),mPrintNormal)))
//            cell.horizontalAlignment = Element.ALIGN_RIGHT
//            cell.border = Rectangle.NO_BORDER
//            table.addCell(cell)
//            val separato = DashedSeparator()
//            separato.percentage = 59500f / 523f
//            cell = PdfPCell(Phrase(Chunk(separato)))
//            cell.colspan = 5
//            cell.horizontalAlignment = Element.ALIGN_RIGHT
//            cell.border = Rectangle.NO_BORDER
//            table.addCell(cell)
//            table.headerRows = 1
//            var i=0
//            var currentSAles=register?.getCurrentSale()
//            register?.getSalesLine(currentSAles?.id!!)?.let {
//                for (recod in it) {
//                    i++;
////                    cell = PdfPCell(Phrase(Chunk((i).toString(),mPrintNormal)))
////                    cell.horizontalAlignment = Element.ALIGN_LEFT
////                    cell.border = Rectangle.NO_BORDER
////                    table.addCell(cell)
//
//                    cell = PdfPCell(Paragraph(recod.product?.name+"-"+recod.product?.uom,mPrintNormal))
//                    cell.horizontalAlignment = Element.ALIGN_LEFT
//                    cell.border = Rectangle.NO_BORDER
//                    table.addCell(cell)
//                    if(recod?.isFree)
//                    {      cell = PdfPCell(Phrase(Chunk(getString(R.string.free), mPrintNormal)))
//                        cell.horizontalAlignment = Element.ALIGN_RIGHT
//                        cell.border = Rectangle.NO_BORDER
//                        table.addCell(cell)}
//                    else {
//                        cell = PdfPCell(Phrase(Chunk(recod.product?.unitPrice?.toFormatedString(), mPrintNormal)))
//                        cell.horizontalAlignment = Element.ALIGN_RIGHT
//                        cell.border = Rectangle.NO_BORDER
//                        table.addCell(cell)
//                    }
//                    cell = PdfPCell(Phrase(Chunk(recod?.quantity?.toFormatedString(),mPrintNormal)))
//                    cell.horizontalAlignment = Element.ALIGN_CENTER
//                    cell.border = Rectangle.NO_BORDER
//                    table.addCell(cell)
//
//                    //total += Double.parseDouble(i+2);
//                    if(recod?.isFree) {
//                        cell = PdfPCell(Phrase(Chunk(getString(R.string.free), mPrintNormal)))
//                        cell.horizontalAlignment = Element.ALIGN_RIGHT
//                        cell.border = Rectangle.NO_BORDER
//                        table.addCell(cell)
//
//                    }
//                    else
//                    {
//                        cell = PdfPCell(Phrase(Chunk(recod?.totalAmount?.toFormatedString(), mPrintNormal)))
//                        cell.horizontalAlignment = Element.ALIGN_RIGHT
//                        cell.border = Rectangle.NO_BORDER
//                        table.addCell(cell)
//                    }
//
//                }
//            }
//            val separator1 = DashedSeparator()
//            separator1.percentage = 59500f / 523f
//
//            var salessummery=register?.getSalesSummery(saleid)
//            cell = PdfPCell(Phrase(Chunk(separator1)))
//            cell.colspan = 4
//            cell.horizontalAlignment = Element.ALIGN_RIGHT
//            cell.border = Rectangle.NO_BORDER
//            table.addCell(cell)
//
//
//            cell = PdfPCell(Phrase(getString(R.string.total_items),mPrintBoldMediam))
//            cell.colspan = 2
//            cell.horizontalAlignment = Element.ALIGN_LEFT
//            cell.border = Rectangle.NO_BORDER
//            table.addCell(cell)
//
//            cell = PdfPCell(Phrase((salessummery?.totalItems?.toString()),mPrintBoldMediam))
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
//
//
//
//
//            cell = PdfPCell(Phrase(getString(R.string.total),mPrintBoldMediam))
//            cell.colspan = 2
//            cell.horizontalAlignment = Element.ALIGN_LEFT
//            cell.border = Rectangle.NO_BORDER
//            table.addCell(cell)
//
//            cell = PdfPCell(Phrase((salessummery?.totalAmount?.toFormatedString()?:"00.00"),mPrintBoldMediam))
//            cell.colspan = 2
//            cell.horizontalAlignment = Element.ALIGN_RIGHT
//            cell.border = Rectangle.NO_BORDER
//            table.addCell(cell)
//
////            cell = PdfPCell(Phrase("Tax",mPrintBoldMediam))
////            cell.colspan = 2
////            cell.horizontalAlignment = Element.ALIGN_LEFT
////            cell.border = Rectangle.NO_BORDER
////            table.addCell(cell)
////
////            cell = PdfPCell(Phrase((salessummery?.totalTax?.toFormatedString()?:"00.00"),mPrintBoldMediam))
////            cell.colspan = 2
////            cell.horizontalAlignment = Element.ALIGN_RIGHT
////            cell.border = Rectangle.NO_BORDER
////            table.addCell(cell)
//
//
//            cell = PdfPCell(Phrase(getString(R.string.discount),mPrintBoldMediam))
//            cell.colspan = 2
//            cell.horizontalAlignment = Element.ALIGN_LEFT
//            cell.border = Rectangle.NO_BORDER
//            table.addCell(cell)
//
//            cell = PdfPCell(Phrase((currentSAles?.totalDiscountAmount?.toFormatedString()?:"0"),mPrintBoldMediam))
//            cell.colspan = 2
//            cell.horizontalAlignment = Element.ALIGN_RIGHT
//            cell.border = Rectangle.NO_BORDER
//            table.addCell(cell)
//
//            cell = PdfPCell(Phrase(Chunk(separator1)))
//            cell.colspan = 4
//            cell.horizontalAlignment = Element.ALIGN_RIGHT
//            cell.border = Rectangle.NO_BORDER
//            table.addCell(cell)
//
//
//
//            cell = PdfPCell(Phrase(getString(R.string.net_total),mPrintBoldMediam))
//            cell.colspan = 2
//            cell.horizontalAlignment = Element.ALIGN_LEFT
//            cell.border = Rectangle.NO_BORDER
//            table.addCell(cell)
//
//            cell = PdfPCell(Phrase((currentSAles?.netPayableAmount?.toFormatedString()?:"0"),mPrintBoldMediam))
//            cell.colspan = 2
//            cell.horizontalAlignment = Element.ALIGN_RIGHT
//            cell.border = Rectangle.NO_BORDER
//            table.addCell(cell)
//             // end of net totala
//            // begins payment
//            if(register?.getCurrentSale()?.documenType.equals( DocumentTypes.DOC_INVOICE?.toString())) {
//                var paymentRecord=PaymentRecordDbo?.getInstance(applicationContext)?.getLineBySalesID(currentSAles?.id!!)
//
//                paymentRecord?.let {
//                    if (paymentRecord?.size > 0) {
//                        cell = PdfPCell(Phrase(Chunk(separator1)))
//                        cell.colspan = 4
//                        cell.horizontalAlignment = Element.ALIGN_RIGHT
//                        cell.border = Rectangle.NO_BORDER
//                        table.addCell(cell)
//
//
//                        cell = PdfPCell(Phrase(getString(R.string.payments), mPrintBoldMediam))
//                        cell.colspan = 4
//                        cell.horizontalAlignment = Element.ALIGN_LEFT
//                        cell.border = Rectangle.NO_BORDER
//                        table.addCell(cell)
//
//                        cell = PdfPCell(Phrase(Chunk(separator1)))
//                        cell.colspan = 4
//                        cell.horizontalAlignment = Element.ALIGN_RIGHT
//                        cell.border = Rectangle.NO_BORDER
//                        table.addCell(cell)
//
//                        //begins payment headr
//
//                        cell = PdfPCell(Phrase(Chunk(getString(R.string.payment), mPrintNormal)))
//                        cell.horizontalAlignment = Element.ALIGN_LEFT
//                        cell.border = Rectangle.NO_BORDER
//
//                        table.addCell(cell)
//
//                        cell = PdfPCell(Phrase(Chunk(getString(R.string.payment_type), mPrintNormal)))
//                        cell.horizontalAlignment = Element.ALIGN_RIGHT
//                        cell.border = Rectangle.NO_BORDER
//                        cell.colspan = 2
//                        table.addCell(cell)
//                      //  var baseCurre = CurrencyDbo?.getInstance(applicationContext)?.getAllCurrencies(customer?.currencyId!!)
//                        cell = PdfPCell(Phrase(Chunk("R$", mPrintNormal)))
//                        cell.horizontalAlignment = Element.ALIGN_RIGHT
//                        cell.border = Rectangle.NO_BORDER
//
//                        table.addCell(cell)
//                        separato.percentage = 59500f / 523f
//                        cell = PdfPCell(Phrase(Chunk(separato)))
//                        cell.colspan = 5
//                        cell.horizontalAlignment = Element.ALIGN_RIGHT
//                        cell.border = Rectangle.NO_BORDER
//                        table.addCell(cell)
//                        table.headerRows = 1
//
//                        // payment lines
//                        var totlaPaid = BigDecimal.ZERO
//
//                        var k = 0
//                        var billamount=BigDecimal.ZERO
//                        for (data in it) {
//                            var payments = PaymentLinesDbo?.getInstance(applicationContext)?.getLine(data?.id)
//
//                        for(recod in payments?: arrayListOf()) {
//                            billamount=recod?.billAmount
//                            k++;
////                    cell = PdfPCell(Phrase(Chunk((i).toString(),mPrintNormal)))
////                    cell.horizontalAlignment = Element.ALIGN_LEFT
////                    cell.border = Rectangle.NO_BORDER
////                    table.addCell(cell)
//                            totlaPaid = totlaPaid?.add(recod?.paidAmountInBaseCurrency)
//                            cell = PdfPCell(Phrase(Chunk(recod.paidAmount?.toFormatedString(), mPrintNormal)))
//                            cell.horizontalAlignment = Element.ALIGN_LEFT
//                            cell.border = Rectangle.NO_BORDER
//
//                            table.addCell(cell)
//
//                            cell = PdfPCell(Phrase(Chunk(recod.paymentType?.split("_")?.get(0), mPrintNormal)))
//                            cell.horizontalAlignment = Element.ALIGN_RIGHT
//                            cell.colspan = 2
//                            cell.border = Rectangle.NO_BORDER
//                            table.addCell(cell)
//
//                            //total += Double.parseDouble(i+2);
//
//                            cell = PdfPCell(Phrase(Chunk(recod?.paidAmountInBaseCurrency?.toFormatedString(), mPrintNormal)))
//                            cell.horizontalAlignment = Element.ALIGN_RIGHT
//                            cell.border = Rectangle.NO_BORDER
//                            table.addCell(cell)
//                        }
//
//                        }
//
//
//                        // cash  section
//                        cell = PdfPCell(Phrase(Chunk(separator1)))
//                        cell.colspan = 4
//                        cell.horizontalAlignment = Element.ALIGN_RIGHT
//                        cell.border = Rectangle.NO_BORDER
//                        table.addCell(cell)
//
//
//                        cell = PdfPCell(Phrase(getString(R.string.total_payment), mPrintBoldMediam))
//                        cell.colspan = 2
//                        cell.horizontalAlignment = Element.ALIGN_LEFT
//                        cell.border = Rectangle.NO_BORDER
//                        table.addCell(cell)
//
//                        cell = PdfPCell(Phrase(totlaPaid?.toFormatedString(), mPrintBoldMediam))
//                        cell.colspan = 2
//                        cell.horizontalAlignment = Element.ALIGN_RIGHT
//                        cell.border = Rectangle.NO_BORDER
//                        table.addCell(cell)
//
//                        cell = PdfPCell(Phrase(Chunk(separator1)))
//                        cell.colspan = 4
//                        cell.horizontalAlignment = Element.ALIGN_RIGHT
//                        cell.border = Rectangle.NO_BORDER
//                        table.addCell(cell)
//
//                        cell = PdfPCell(Phrase(getString(R.string.due_amount), mPrintBoldMediam))
//                        cell.colspan = 2
//                        cell.horizontalAlignment = Element.ALIGN_LEFT
//                        cell.border = Rectangle.NO_BORDER
//                        table.addCell(cell)
//
//                        cell = PdfPCell(Phrase(billamount?.subtract(totlaPaid)?.toFormatedString(), mPrintBoldMediam))
//                        cell.colspan = 2
//                        cell.horizontalAlignment = Element.ALIGN_RIGHT
//                        cell.border = Rectangle.NO_BORDER
//                        table.addCell(cell)
//
//                        cell = PdfPCell(Phrase(Chunk(separator1)))
//                        cell.colspan = 4
//                        cell.horizontalAlignment = Element.ALIGN_RIGHT
//                        cell.border = Rectangle.NO_BORDER
//                        table.addCell(cell)
//                    }
//                }
//            }
//            document.add(table)
//
//                val separatorr = DottedLineSeparator()
//                cell = PdfPCell(Phrase(Chunk(separator1)))
//                cell.colspan = 4
//                cell.horizontalAlignment = Element.ALIGN_RIGHT
//                cell.border = Rectangle.NO_BORDER
//                table.addCell(cell)
//                paragraph = Paragraph(MCPreferences.getPrintFooter(application), mPrintNormal)
//                paragraph.alignment = Element.ALIGN_CENTER
//                document.add(paragraph)
//                var y=writer?.getVerticalPosition(true)
//                /* ct = new ColumnText(writer.getDirectContent());
//            ct.setSimpleColumn(pagesize);
//            for (int i=0;i<10;i++) {
//
//                ct.addText(new Chunk("Pratik Butani"));
//
//            }
//            ct.go();
//            // closing the document
//            document.close();*/
//
//                document.close()
//
//                if (docsize == 0f) {
//                    var pg = writer.pageNumber?.minus(1)
//                    var size = (pg* (max?.minus(30f))) ?.plus(y!!)
//                    createPdff(dest, size)
//                } else {
//                    generateThump()
//                    showPdf()
//                }
//
//        } catch (e: Exception) {
//            e.printStackTrace()
//
//        }
//
//    }
//
//    private fun generateThump() {
//        val pageNumber = 0
//        val pdfiumCore = PdfiumCore(this)
//        val uri = Uri.fromFile(File(FileUtils.getAppPath(applicationContext) + "123.pdf"))
//        try {
//            //http://www.programcreek.com/java-api-examples/index.php?api=android.os.ParcelFileDescriptor
//            val fd = contentResolver.openFileDescriptor(uri, "r")
//            val pdfDocument = pdfiumCore.newDocument(fd)
//            pdfiumCore.openPage(pdfDocument, pageNumber)
//            //                    val width = 250
//            //                    val height =250
//            val width = pdfiumCore.getPageWidthPoint(pdfDocument, pageNumber)
//            val height = pdfiumCore.getPageHeightPoint(pdfDocument, pageNumber)
//            val bmp = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565)
//            pdfiumCore.renderPageBitmap(pdfDocument, bmp, pageNumber, 0, 0, width, height)
//            pdfiumCore.closeDocument(pdfDocument) // important!
//            var f= File(FileUtils.getAppPath(applicationContext) + "123.png")
//            if (f.exists()) {
//                f.delete()
//                f.createNewFile()
//            } else {
//                println("File already exists")
//            }
//            var os=FileOutputStream(f,false)
//            bmp.compress(Bitmap.CompressFormat.PNG, 50, os);
//            os.close()
//
//        } catch (e: Exception) {
//            e.printStackTrace()
//        }
//
//    }
//
//    internal inner class DottedCell : PdfPCellEvent {
//        override fun cellLayout(cell: PdfPCell, position: Rectangle,
//                                canvases: Array<PdfContentByte>) {
//            val canvas = canvases[PdfPTable.LINECANVAS]
//            canvas.setLineDash(3f, 3f)
//            canvas.moveTo(position.left, position.top)
//            canvas.lineTo(position.right, position.top)
//            canvas.moveTo(position.left, position.bottom)
//            canvas.lineTo(position.right, position.bottom)
//            canvas.stroke()
//        }
//    }
//
//
//    override fun onCreateOptionsMenu(menu: Menu): Boolean {
//        // Inflate the menu; this adds items to the action bar if it is present.
//        //        getMenuInflater().inflate(R.menu.menu_main, menu);
//        return true
//    }
//
//    override fun onOptionsItemSelected(item: MenuItem): Boolean {
//        // Handle action bar item clicks here. The action bar will
//        // automatically handle clicks on the Home/Up button, so long
//        // as you specify a parent activity in AndroidManifest.xml.
//        val id = item.itemId
//
//
//        //        if (id == R.id.action_settings) {
//        //            return true;
//        //        }
//
//        return super.onOptionsItemSelected(item)
//    }
//    fun BigDecimal.toFormatedString(): String {
//        if(this?.equals(BigDecimal.ZERO)||this==null||this?.toString().equals("0.0"))
//        {
//            return "0"
//        }
//        val df = DecimalFormat("#,###.00")
//        return df.format(this)
//    }
//
//    fun String.toFormatedString(): String {
//
//        val df = DecimalFormat("#,###.00")
//        return df.format(this)
//    }
//
//    fun showPdf()
//    {
//        pdfView.recycle()
//        pdfView.fromFile(File(FileUtils.getAppPath(mContext) + "123.pdf"))
//                .pages(0)
//                .enableSwipe(true) // allows to block changing pages using swipe
//                .swipeHorizontal(false)
//                .enableDoubletap(true)
//                .defaultPage(0)
//                .enableAnnotationRendering(false) // render annotations (such as comments, colors or forms)
//                .password(null)
//                .scrollHandle(null)
//                .enableAntialiasing(true) // improve rendering a little bit on low-res screens
//                .spacing(0)
//                .load();
//
//        if(PreferenceManager.getDefaultSharedPreferences(application).getBoolean(getString(R.string.key_print_enabled),false)&&
//                PreferenceManager.getDefaultSharedPreferences(application).getBoolean(getString(R.string.key_auto_print),false)
//        )
//        {
//
//        hsPrintNFC()
//
//        }
//    }
//
//    var isNFCE=false
//    private fun generateImageNFCE() {
//
//        isNFCE = true
//
//        val pageNumber = 0
//        val pdfiumCore = PdfiumCore(this)
//        val uri = Uri.fromFile(File(FileUtils.getAppPath(applicationContext) + "123.pdf"))
//        try {
//            //http://www.programcreek.com/java-api-examples/index.php?api=android.os.ParcelFileDescriptor
//            val fd = contentResolver.openFileDescriptor(uri, "r")
//            val pdfDocument = pdfiumCore.newDocument(fd)
//            pdfiumCore.openPage(pdfDocument, pageNumber)
//            //                    val width = 250
//            //                    val height =250
//            val width = pdfiumCore.getPageWidthPoint(pdfDocument, pageNumber) * 3
//            val height = pdfiumCore.getPageHeightPoint(pdfDocument, pageNumber) * 3
//            val bmp = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565)
//            pdfiumCore.renderPageBitmap(pdfDocument, bmp, pageNumber, 0, 0, width, height)
//            pdfiumCore.closeDocument(pdfDocument) // important!
//            var f = File(FileUtils.getAppPath(applicationContext) + "123.png")
//            if (f.exists()) {
//                f.delete()
//                f.createNewFile()
//            } else {
//                println("File already exists")
//            }
//            var os = FileOutputStream(f, false)
//            bmp.compress(Bitmap.CompressFormat.PNG, 50, os);
//            os.close()
//        }catch (e: Exception) {
//            e.printStackTrace()
//        }
//    }
//
//
//    private fun generateImage(): Bitmap? {
//        isNFCE=false
//        val pageNumber = 0
//        val pdfiumCore = PdfiumCore(this)
//        val uri = Uri.fromFile(File(getAppPath(applicationContext) + "123.pdf"))
//        try {
//            //http://www.programcreek.com/java-api-examples/index.php?api=android.os.ParcelFileDescriptor
//            val fd = contentResolver.openFileDescriptor(uri, "r")
//            val pdfDocument = pdfiumCore.newDocument(fd)
//            pdfiumCore.openPage(pdfDocument, pageNumber)
//            //                    val width = 250
//            //                    val height =250
//            val width = pdfiumCore.getPageWidthPoint(pdfDocument, pageNumber)
//            val height = pdfiumCore.getPageHeightPoint(pdfDocument, pageNumber)
//            val bmp = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565)
//            pdfiumCore.renderPageBitmap(pdfDocument, bmp, pageNumber, 0, 0, width, height)
//            pdfiumCore.closeDocument(pdfDocument) // important!
//            return bmp
//        } catch (e: Exception) {
//            e.printStackTrace()
//        }
//
//        return null
//    }
//    fun hsPrint()
//    {
//        AppController?.instance?.hsPrint(generateImage(),0) //1 for 80 0 58
//    }
//
//    fun hsPrintNFC()
//    {
//        AppController?.instance?.hsPrint(generateImage(),0) //1 for 80 0 58
//    }
//}
//
//
