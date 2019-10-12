package com.alhikmahpro.www.e_inventory.Data;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Toast;

import com.alhikmahpro.www.e_inventory.AppUtils;
import com.alhikmahpro.www.e_inventory.R;
import com.alhikmahpro.www.e_inventory.View.ListReceiptActivity;
import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Chunk;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.pdf.BaseFont;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import com.itextpdf.text.pdf.draw.LineSeparator;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class CreatePdf extends AsyncTask<String,Void,String> {
    private Context mContext;
    private String invoiceNo;
    private AlertDialog alertDialog;
    private AlertDialog.Builder builder;
    private static final String TAG = "CreatePdf";

    private String customerName="", invoiceDate, salesmanId, customerCode, paymentMode, Type;
    private String companyName="xxxx", companyAddress="xxxx", companyPhone="xxxx", footer="xxxx";
    private double netAmount, discountAmount, base_total;
    private String fileName;
    public CreatePdf(Context mContext, String invoiceNo) {
        this.mContext = mContext;
        this.invoiceNo = invoiceNo;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        ShowProgressDialog();

    }
    @Override
    protected String doInBackground(String... strings) {
        dbHelper helper=new dbHelper(mContext);
        SQLiteDatabase database = helper.getReadableDatabase();

        Cursor cursor = helper.getPaperSettings(database);
        if (cursor.moveToFirst()) {
            companyName = cursor.getString(cursor.getColumnIndex(DataContract.PaperSettings.COL_COMPANY_NAME));
            companyAddress = cursor.getString(cursor.getColumnIndex(DataContract.PaperSettings.COL_COMPANY_ADDRESS));
            companyPhone = cursor.getString(cursor.getColumnIndex(DataContract.PaperSettings.COL_COMPANY_PHONE));
            footer = cursor.getString(cursor.getColumnIndex(DataContract.PaperSettings.COL_FOOTER));
        }
        Log.d(TAG, "doInBackground: " + companyName);
        cursor.close();

        Cursor cursor1=helper.getInvoiceById(database,invoiceNo);
        if(cursor1.moveToFirst()){
            //invoiceNo=cursor.getString(cursor.getColumnIndex(DataContract.Invoice.COL_INVOICE_NUMBER));
            invoiceDate=cursor1.getString(cursor1.getColumnIndex(DataContract.Invoice.COL_INVOICE_DATE));
            salesmanId=cursor1.getString(cursor1.getColumnIndex(DataContract.Invoice.COL_SALESMAN_ID));
            customerCode=cursor1.getString(cursor1.getColumnIndex(DataContract.Invoice.COL_CUSTOMER_CODE));
            customerName=cursor1.getString(cursor1.getColumnIndex(DataContract.Invoice.COL_CUSTOMER_NAME));
            base_total=cursor1.getDouble(cursor1.getColumnIndex(DataContract.Invoice.COL_TOTAL_AMOUNT));
            discountAmount=cursor1.getDouble(cursor1.getColumnIndex(DataContract.Invoice.COL_DISCOUNT_AMOUNT));
            netAmount=cursor1.getDouble(cursor1.getColumnIndex(DataContract.Invoice.COL_NET_AMOUNT));
            paymentMode=cursor1.getString(cursor1.getColumnIndex(DataContract.Invoice.COL_PAYMENT_TYPE));
        }
        cursor1.close();
        database.close();
        // add invoiceDetails to mCart
        helper.getInvoiceDetailsById(invoiceNo);
        Log.d(TAG, "doInBackground: "+RuntimeData.mCartData.size());
        String res="failed";
        if(companyName.length()>0){
            if(Cart.mCart.size()>0){
                if(createPDF())
                    return "success";
            }
        }else {
            res="sss";
        }

        return res;
    }
    @Override
    protected void onPostExecute(String result) {
        HideProgressDialog();
        Log.d(TAG, "onPostExecute: "+result);
        if(result!=null && result.equals("success")){
            Cart.mCart.clear();
            openGeneratedPDF();
        }else {
            Toast.makeText(mContext, "Pdf Creation Failed", Toast.LENGTH_SHORT).show();
        }

    }


    private boolean createPDF(){
        final DecimalFormat decimalFormat = new DecimalFormat("0.00");

        String mDate= AppUtils.getFormattedDate();
        Document document = new Document();
        File pdfFile;
        File dir;
        try {
            //create directory
            String directoryPath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/PriceChecker/Invoice";
            dir = new File(directoryPath);
            if (!dir.exists()) {
                dir.mkdir();
            }

            fileName = invoiceNo+ "-"+mDate+".pdf";
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
            BaseFont urName = BaseFont.createFont("assets/fonts/brandon_medium.otf", "UTF-8", BaseFont.EMBEDDED);

            Font heading = new Font(Font.FontFamily.TIMES_ROMAN, 12, Font.BOLD, new BaseColor(0, 0, 0));
            Font normal = new Font(Font.FontFamily.TIMES_ROMAN, 12);

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

            Font mOrderIdFont = new Font(urName, mHeadingFontSize, Font.NORMAL, mColorAccent);
            Chunk mOrderIdChunk = new Chunk("Invoice No:", mOrderIdFont);
            Paragraph mOrderIdParagraph = new Paragraph(mOrderIdChunk);
            document.add(mOrderIdParagraph);

            Font mOrderIdValueFont = new Font(urName, mValueFontSize, Font.NORMAL, BaseColor.BLACK);
            Chunk mOrderIdValueChunk = new Chunk("#" + invoiceNo, mOrderIdValueFont);
            Paragraph mOrderIdValueParagraph = new Paragraph(mOrderIdValueChunk);
            document.add(mOrderIdValueParagraph);

            // Adding Line Breakable Space....
            document.add(new Paragraph(""));
            // Adding Horizontal Line...
            //  document.add(new Chunk(lineSeparator));
            // Adding Line Breakable Space....
            document.add(new Paragraph(""));

            // Fields of Order Details...
            Font mOrderAcNameFont = new Font(urName, mHeadingFontSize, Font.NORMAL, mColorAccent);
            Chunk mOrderAcNameChunk = new Chunk("Customer Name:", mOrderAcNameFont);
            Paragraph mOrderAcNameParagraph = new Paragraph(mOrderAcNameChunk);
            document.add(mOrderAcNameParagraph);

            Font mOrderAcNameValueFont = new Font(urName, mValueFontSize, Font.NORMAL, BaseColor.BLACK);
            Chunk mOrderAcNameValueChunk = new Chunk(customerName, mOrderAcNameValueFont);
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
            PdfPTable pTable = new PdfPTable(3);
            pTable.setWidthPercentage(100);
            //insert heading
            insertCell(pTable, " Item Name", Element.ALIGN_RIGHT, 1, heading);
            insertCell(pTable, " Quantity", Element.ALIGN_LEFT, 1, heading);
            insertCell(pTable, " Total", Element.ALIGN_LEFT, 1, heading);
            pTable.setHeaderRows(1);

            for (CartModel mm : Cart.mCart)
            {

                String item = mm.getProductName();
                String qty = String.valueOf(mm.getQty());
                String price = String.valueOf(decimalFormat.format(mm.getRate()));
                String sub_total = String.valueOf(decimalFormat.format(mm.getNet()));
                String item_format = price + " X " + mm.getQty();


                insertCell(pTable, item, Element.ALIGN_RIGHT, 1, normal);
                insertCell(pTable, item_format, Element.ALIGN_LEFT, 1, normal);
                insertCell(pTable, price, Element.ALIGN_LEFT, 1, normal);

            }
            insertCell(pTable, "", Element.ALIGN_LEFT, 3, heading);
            insertCell(pTable, "Total ", Element.ALIGN_RIGHT, 2, normal);
            insertCell(pTable, decimalFormat.format(base_total), Element.ALIGN_RIGHT, 1, normal);
            insertCell(pTable, "Discount ", Element.ALIGN_RIGHT, 2, normal);
            insertCell(pTable, decimalFormat.format(discountAmount), Element.ALIGN_RIGHT, 1, normal);
            insertCell(pTable, "Net :", Element.ALIGN_RIGHT, 2, normal);
            insertCell(pTable, decimalFormat.format(netAmount), Element.ALIGN_RIGHT, 1, normal);
            paragraph.add(pTable);
            document.add(paragraph);
            document.close();
            return true;



        }catch (DocumentException de){
            de.printStackTrace();
            return false;

        }catch (IOException e){
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

    private void ShowProgressDialog() {
        builder = new AlertDialog.Builder(mContext);
        LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View dialogView = inflater.inflate(R.layout.progress, null);
        builder.setView(dialogView);
        builder.setCancelable(false);
        alertDialog = builder.create();
        alertDialog.show();
    }

    private void HideProgressDialog() {

        alertDialog.dismiss();
    }

    private void openGeneratedPDF() {

//        File outputFile = new File(Environment.getExternalStoragePublicDirectory
//                (Environment.DIRECTORY_DOWNLOADS), "sample.pdf");
        File outputFile = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/PriceChecker/Invoice/"+fileName);
        // Uri uri = Uri.fromFile(outputFile);
        Uri uri = FileProvider.getUriForFile(mContext, mContext.getPackageName() + ".provider", outputFile);

        Intent share = new Intent();
        share.setAction(Intent.ACTION_SEND);
        share.setType("application/pdf");
        share.putExtra(Intent.EXTRA_STREAM, uri);
        mContext.startActivity(Intent.createChooser(share, "Share to :"));


        //ListReceiptActivity.startActivity(share);


    }


}
