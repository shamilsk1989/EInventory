package com.alhikmahpro.www.e_inventory.View;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.alhikmahpro.www.e_inventory.AppUtils;
import com.alhikmahpro.www.e_inventory.Data.DataContract;
import com.alhikmahpro.www.e_inventory.Data.dbHelper;
import com.alhikmahpro.www.e_inventory.FileUtils;
import com.alhikmahpro.www.e_inventory.R;
import com.github.barteksc.pdfviewer.PDFView;
import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Chunk;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.FontFactory;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.pdf.BaseFont;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import com.itextpdf.text.pdf.draw.LineSeparator;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ReportViewActivity extends AppCompatActivity {

    @BindView(R.id.pdfView)
    PDFView pdfView;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    String type, fromDate, toDate;
    String fileName;
    MenuItem itemShare, itemClear, itemPrint, itemSync;
    private static final String TAG = "ReportViewActivity";

    private double netAmount, grandTotal = 0;
    private static String PREF_KEY_HEADER1 = "key_header_1";
    private static String PREF_KEY_HEADER2 = "key_header_2";
    private static String PREF_KEY_HEADER3 = "key_header_3";
    private static String PREF_KEY_FOOTER = "key_footer";
    private static String PREF_KEY_SALESMAN = "key_employee";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report_view);
        ButterKnife.bind(this);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        Intent intent = getIntent();
        type = intent.getStringExtra("TYPE");
        fromDate = intent.getStringExtra("FROM_DATE");
        toDate = intent.getStringExtra("TO_DATE");

        requestPermission();


    }

    private void requestPermission() {
        Dexter.withActivity(this)
                .withPermissions(Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE)
                .withListener(new MultiplePermissionsListener() {
                    @Override
                    public void onPermissionsChecked(MultiplePermissionsReport report) {
                        if (report.areAllPermissionsGranted()) {
                            createPDF();
                        }
                        if (report.isAnyPermissionPermanentlyDenied()) {
                            showSettingDialog();
                        }

                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token) {

                    }
                }).onSameThread()
                .check();
    }

    private boolean createPDF() {
        String customerName, invoiceDate, invoiceNumber, salesmanId, customerCode, paymentMode, Type;
        String companyName, companyAddress, companyPhone, footer;
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        companyName = sharedPreferences.getString(PREF_KEY_HEADER1, "0");
        companyAddress = sharedPreferences.getString(PREF_KEY_HEADER2, "0");
        companyPhone = sharedPreferences.getString(PREF_KEY_HEADER3, "0");
        footer = sharedPreferences.getString(PREF_KEY_FOOTER, "0");
        salesmanId = sharedPreferences.getString(PREF_KEY_SALESMAN, "0");
        dbHelper helper = new dbHelper(this);
        SQLiteDatabase database = helper.getReadableDatabase();
        final DecimalFormat decimalFormat = new DecimalFormat("0.00");
        String mDate = AppUtils.getDateAndTime();
        Log.d(TAG, "createPDF: time and date"+AppUtils.getShortDate());
        Document document = new Document();
        File pdfFile;
        try {
            //create directory

            String appPath = FileUtils.getAppPath(this);
            File dir = new File(appPath + File.separator + DataContract.DIR_REPORTS);
            if (!dir.exists()) {
                dir.mkdir();
            }

            fileName = type + "-" + mDate + ".pdf";
            Log.d(TAG, "fileName: " + fileName);
            pdfFile = new File(dir, fileName);

            //PdfWriter.getInstance(document,new FileOutputStream(mFilePath));
            FileOutputStream stream = new FileOutputStream(pdfFile);
            //PdfWriter pdfWriter=PdfWriter.getInstance(document,stream);
            PdfWriter.getInstance(document, stream);
            //open document

            document.open();
            //document settings

            document.setPageSize(PageSize.A4);
            document.addCreationDate();
            BaseColor mColorAccent = new BaseColor(0, 153, 204, 255);
            float mHeadingFontSize = 13.0f;
            float mValueFontSize = 15.0f;
            float mItemFontSize = 10.0f;
            Paragraph paragraph = new Paragraph("");


            //adding title image
//            Image image=Image.getInstance("src/resource/logo.png");
//            image.scaleAbsolute(540f,75f);


            //font
            BaseFont urName = BaseFont.createFont("assets/brandon_bold.otf", "UTF-8", BaseFont.EMBEDDED);

            Font heading = new Font(Font.FontFamily.TIMES_ROMAN, 12, Font.BOLD, new BaseColor(0, 0, 0));
            Font normal = new Font(Font.FontFamily.TIMES_ROMAN, 12);
            Font arabic = FontFactory.getFont("assets/fonts/Arial.ttf", BaseFont.IDENTITY_H, 16, Font.BOLDITALIC);

            BaseFont bf = BaseFont.createFont("assets/fonts/Arial.ttf", BaseFont.IDENTITY_H, BaseFont.EMBEDDED);
            Font font = new Font(bf, 12);


            // LINE SEPARATOR
            LineSeparator lineSeparator = new LineSeparator();
            lineSeparator.setLineColor(new BaseColor(0, 0, 0, 68));

            // Title Order Details...
            // Adding Title....

            Font mOrderDetailsTitleFont = new Font(urName, 25.0f, Font.NORMAL, BaseColor.BLACK);
            Chunk mOrderDetailsTitleChunk = new Chunk(companyName, mOrderDetailsTitleFont);
            Paragraph mOrderDetailsTitleParagraph = new Paragraph(mOrderDetailsTitleChunk);
            mOrderDetailsTitleParagraph.setAlignment(Element.ALIGN_CENTER);
            document.add(mOrderDetailsTitleParagraph);

            Font mOrderDetailsTitle2Font = new Font(urName, 22.0f, Font.NORMAL, BaseColor.BLACK);
            Chunk mOrderDetailsTitle2Chunk = new Chunk(companyAddress, mOrderDetailsTitle2Font);
            Paragraph mOrderDetailsTitle2Paragraph = new Paragraph(mOrderDetailsTitle2Chunk);
            mOrderDetailsTitle2Paragraph.setAlignment(Element.ALIGN_CENTER);
            document.add(mOrderDetailsTitle2Paragraph);

            Font mOrderDetailsTitle3Font = new Font(urName, 20.0f, Font.NORMAL, BaseColor.BLACK);
            Chunk mOrderDetailsTitle3Chunk = new Chunk(companyPhone, mOrderDetailsTitle3Font);
            Paragraph mOrderDetailsTitle3Paragraph = new Paragraph(mOrderDetailsTitle3Chunk);
            mOrderDetailsTitle3Paragraph.setAlignment(Element.ALIGN_CENTER);
            document.add(mOrderDetailsTitle3Paragraph);


            // Fields of Order Details...
            // Adding Chunks for Title and value

//            Font mOrderIdFont = new Font(urName, mHeadingFontSize, Font.NORMAL, mColorAccent);
//            Chunk mOrderIdChunk = new Chunk("Invoice No:", mOrderIdFont);
//            Paragraph mOrderIdParagraph = new Paragraph(mOrderIdChunk);
//            document.add(mOrderIdParagraph);
//
//            Font mOrderIdValueFont = new Font(urName, mValueFontSize, Font.NORMAL, BaseColor.BLACK);
//            Chunk mOrderIdValueChunk = new Chunk("#" + "INV 20", mOrderIdValueFont);
//            Paragraph mOrderIdValueParagraph = new Paragraph(mOrderIdValueChunk);
//            document.add(mOrderIdValueParagraph);

            // Adding Line Breakable Space....
            // document.add(new Paragraph(""));
            // Adding Horizontal Line...
            //  document.add(new Chunk(lineSeparator));
            // Adding Line Breakable Space....
            document.add(new Paragraph(""));

            // Fields of Order Details...

            Font mOrderAcNameFont = new Font(urName, mHeadingFontSize, Font.NORMAL, mColorAccent);
            Chunk mOrderAcNameChunk = new Chunk("Date:", mOrderAcNameFont);
            Paragraph mOrderAcNameParagraph = new Paragraph(mOrderAcNameChunk);
            document.add(mOrderAcNameParagraph);

            Font mOrderAcNameValueFont = new Font(urName, mValueFontSize, Font.NORMAL, BaseColor.BLACK);
            Chunk mOrderAcNameValueChunk = new Chunk(mDate, mOrderAcNameValueFont);
            Paragraph mOrderAcNameValueParagraph = new Paragraph(mOrderAcNameValueChunk);
            document.add(mOrderAcNameValueParagraph);

            document.add(new Paragraph(""));
            //document.add(new Chunk(lineSeparator));
            document.add(new Paragraph(""));

            // Fields of Order Details...
            Font mOrderSalesmanFont = new Font(urName, mHeadingFontSize, Font.NORMAL, mColorAccent);
            Chunk mOrderSalesmanChunk = new Chunk("Salesman:", mOrderSalesmanFont);
            Paragraph mOrderSalesmanParagraph = new Paragraph(mOrderSalesmanChunk);
            document.add(mOrderSalesmanParagraph);

            Font mOrderSalesmanValueFont = new Font(urName, mValueFontSize, Font.NORMAL, BaseColor.BLACK);
            Chunk mOrderSalesmanValueChunk = new Chunk(salesmanId, mOrderSalesmanValueFont);
            Paragraph mOrderSalesmanValueParagraph = new Paragraph(mOrderSalesmanValueChunk);
            document.add(mOrderSalesmanValueParagraph);

            document.add(new Paragraph(""));
            document.add(new Chunk(lineSeparator));
            document.add(new Paragraph(""));

            //float[]columnWidth={}
            PdfPTable pTable = new PdfPTable(5);
            pTable.setWidthPercentage(100);
            //insert heading
            insertCell(pTable, "Doc Number", Element.ALIGN_RIGHT, 1, heading);
            insertCell(pTable, "Date", Element.ALIGN_LEFT, 1, heading);
            insertCell(pTable, "Customer Name", Element.ALIGN_LEFT, 1, heading);
            insertCell(pTable, "Net Amount", Element.ALIGN_LEFT, 1, heading);
            insertCell(pTable, "Payment Type", Element.ALIGN_LEFT, 1, heading);
            pTable.setHeaderRows(1);
            // Cursor cursor=h.....

            if (type.equals("Invoice")) {
                Cursor cursor1 = helper.getAllInvoiceByDate(fromDate, toDate);
                Log.d(TAG, "cursor size: " + cursor1.getCount());
                if (cursor1.moveToFirst()) {
                    do {
                        invoiceNumber = cursor1.getString(cursor1.getColumnIndex(DataContract.Invoice.COL_INVOICE_NUMBER));
                        invoiceDate = cursor1.getString(cursor1.getColumnIndex(DataContract.Invoice.COL_INVOICE_DATE));
                        customerName = cursor1.getString(cursor1.getColumnIndex(DataContract.Invoice.COL_CUSTOMER_NAME));
                        netAmount = cursor1.getDouble(cursor1.getColumnIndex(DataContract.Invoice.COL_NET_AMOUNT));
                        paymentMode = cursor1.getString(cursor1.getColumnIndex(DataContract.Invoice.COL_PAYMENT_TYPE));
                        grandTotal = grandTotal + netAmount;
                        insertCell(pTable, invoiceNumber, Element.ALIGN_RIGHT, 1, normal);
                        insertCell(pTable, invoiceDate, Element.ALIGN_LEFT, 1, normal);
                        insertCell(pTable, customerName, Element.ALIGN_LEFT, 1, normal);
                        insertCell(pTable, decimalFormat.format(netAmount), Element.ALIGN_LEFT, 1, normal);
                        insertCell(pTable, paymentMode, Element.ALIGN_LEFT, 1, normal);
                    } while (cursor1.moveToNext());
                }
                cursor1.close();

            } else {
                Cursor cursor = helper.getAllReceiptByDate(fromDate, toDate);
                Log.d(TAG, "cursor size: " + cursor.getCount());
                if (cursor.moveToFirst()) {
                    do {
                        invoiceNumber = cursor.getString(cursor.getColumnIndex(DataContract.Receipts.COL_RECEIPT_NUMBER));
                        invoiceDate = cursor.getString(cursor.getColumnIndex(DataContract.Receipts.COL_RECEIPT_DATE));
                        customerName = cursor.getString(cursor.getColumnIndex(DataContract.Receipts.COL_CUSTOMER_NAME));
                        netAmount = cursor.getDouble(cursor.getColumnIndex(DataContract.Receipts.COL_RECEIVED_AMOUNT));
                        paymentMode = cursor.getString(cursor.getColumnIndex(DataContract.Receipts.COL_PAYMENT_TYPE));
                        grandTotal = grandTotal + netAmount;
                        insertCell(pTable, invoiceNumber, Element.ALIGN_RIGHT, 1, normal);
                        insertCell(pTable, invoiceDate, Element.ALIGN_LEFT, 1, normal);
                        insertCell(pTable, customerName, Element.ALIGN_LEFT, 1, normal);
                        insertCell(pTable, decimalFormat.format(netAmount), Element.ALIGN_LEFT, 1, normal);
                        insertCell(pTable, paymentMode, Element.ALIGN_LEFT, 1, normal);
                    } while (cursor.moveToNext());

                }
                cursor.close();
            }


            database.close();

//            for (CartModel mm : Cart.mCart)
//            {
//                insertCell(pTable, item, Element.ALIGN_RIGHT, 1, normal);
//                insertCell(pTable, item_format, Element.ALIGN_LEFT, 1, normal);
//                insertCell(pTable, price, Element.ALIGN_LEFT, 1, normal);
//
//            }
            insertCell(pTable, "", Element.ALIGN_LEFT, 3, heading);
            insertCell(pTable, "Total ", Element.ALIGN_RIGHT, 1, normal);
            insertCell(pTable, decimalFormat.format(grandTotal), Element.ALIGN_RIGHT, 1, normal);
//            insertCell(pTable, "Discount ", Element.ALIGN_RIGHT, 2, normal);
//            insertCell(pTable, decimalFormat.format(discountAmount), Element.ALIGN_RIGHT, 1, normal);
//            insertCell(pTable, "Net :", Element.ALIGN_RIGHT, 2, normal);
//            insertCell(pTable, decimalFormat.format(netAmount), Element.ALIGN_RIGHT, 1, normal);
//            insertCell(pTable, "العناصر :", Element.ALIGN_RIGHT, 2, arabic);
//            insertCell(pTable, decimalFormat.format(netAmount), Element.ALIGN_RIGHT, 1, normal);


            paragraph.add(pTable);
            document.add(paragraph);
            document.close();

            viewPDF();
            return true;


        } catch (DocumentException de) {
            de.printStackTrace();
            return false;

        } catch (IOException e) {
            e.printStackTrace();
            return false;

        }
    }

    private void insertCell(PdfPTable pTable, String text, int align, int colspan, Font font) {

        PdfPCell cell = new PdfPCell(new Phrase(text.trim(), font));
        cell.setHorizontalAlignment(align);
        cell.setColspan(colspan);
        if (text.trim().equalsIgnoreCase("")) {
            cell.setMinimumHeight(10f);
        }
        pTable.addCell(cell);

    }


    private void viewPDF() {
        //File file = new File(FileUtils.getSubDirPath(this, DataContract.DIR_REPORTS) + fileName);
        Log.d(TAG, "viewPDF: " + fileName);
        String path = FileUtils.getSubDirPath(this, DataContract.DIR_REPORTS) + fileName;
        Log.d(TAG, "viewPDF: path" + path);


        pdfView.fromFile(new File(path))
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
                .load();


    }


    private void showSettingDialog() {
        android.support.v7.app.AlertDialog.Builder builder = new AlertDialog.Builder(ReportViewActivity.this);
        builder.setTitle("Need Permissions");
        builder.setMessage("This app needs permission to use this feature. You can grant them in app settings.");
        builder.setPositiveButton("GOTO SETTINGS", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
                openSettings();
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        builder.show();
    }

    private void openSettings() {
        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        Uri uri = Uri.fromParts("package", getPackageName(), null);
        intent.setData(uri);
        startActivityForResult(intent, 101);
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.tool_bar, menu);
        itemPrint = menu.findItem(R.id.action_print);
        itemSync = menu.findItem(R.id.action_sync);
        itemShare = menu.findItem(R.id.action_share);
        itemClear = menu.findItem(R.id.action_clear);

        itemSync.setVisible(false);
        itemPrint.setVisible(false);
        itemClear.setVisible(false);


        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_share) {
            sharePDF();

        }
        return super.onOptionsItemSelected(item);
    }

    private void sharePDF(){

        String filePath = FileUtils.getSubDirPath(this, DataContract.DIR_REPORTS) + fileName;
        File outputFile = new File(filePath);
        if (outputFile.exists()) {
            Uri uri = FileProvider.getUriForFile(ReportViewActivity.this, ReportViewActivity.this.getPackageName() + ".provider", outputFile);
            Intent share = new Intent();
            share.setAction(Intent.ACTION_SEND);
            share.setType("application/pdf");
            share.putExtra(Intent.EXTRA_STREAM, uri);
            startActivity(Intent.createChooser(share, "Share to :"));
        } else {
            Toast.makeText(this, "File not found", Toast.LENGTH_SHORT).show();
        }
    }

}
