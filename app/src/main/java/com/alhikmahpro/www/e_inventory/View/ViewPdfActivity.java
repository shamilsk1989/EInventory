package com.alhikmahpro.www.e_inventory.View;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import com.alhikmahpro.www.e_inventory.Data.DashedSeparator;
import com.alhikmahpro.www.e_inventory.Data.DataContract;
import com.alhikmahpro.www.e_inventory.Data.dbHelper;
import com.alhikmahpro.www.e_inventory.FileUtils;
import com.alhikmahpro.www.e_inventory.R;
import com.github.barteksc.pdfviewer.PDFView;
import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Chunk;
import com.itextpdf.text.Document;
import com.itextpdf.text.Element;
import com.itextpdf.text.Image;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.PdfContentByte;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import com.itextpdf.text.pdf.draw.DottedLineSeparator;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class ViewPdfActivity extends AppCompatActivity {

    @BindView(R.id.pdfView)
    PDFView pdfView;


    private static final String TAG = "ViewPdfActivity";
    Context mContext;
    MenuItem itemPrint,itemSync,itemShare;
    String customerName,receiptNo,receiptDate,salesmanId,action;
    double amount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_pdf);
        ButterKnife.bind(this);
        Intent mIntent = getIntent();
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        mContext=getApplicationContext();

        Intent intent = getIntent();
        action = intent.getStringExtra("TYPE");
        customerName = intent.getStringExtra("CUS_NAME");
        receiptNo = intent.getStringExtra("RECEIPT_NO");
        receiptDate = intent.getStringExtra("RECEIPT_DATE");
        salesmanId = intent.getStringExtra("SALESMAN_ID");
        amount = intent.getDoubleExtra("REC_AMOUNT",0);

        createPdff(FileUtils.getSubDirPath(mContext,DataContract.DIR_RECEIPT),0);



    }
    private  void createPdff(String dest ,float docsize) {

        dbHelper helper = new dbHelper(this);
        SQLiteDatabase database = helper.getReadableDatabase();
        Cursor cursor = helper.getPaperSettings(database);
        String companyName="",companyAddress="",companyPhone="",footer="";
        byte[] img;
//        InputStream ims = null;
//        try {
//            ims = getAssets().open("img.png");
//            Log.d(TAG, "createPdff: "+ims);
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//        Bitmap bitmap = BitmapFactory.decodeStream(ims);
        Bitmap bitmap=null;



        if (cursor.moveToFirst()) {
            companyName = cursor.getString(cursor.getColumnIndex(DataContract.PaperSettings.COL_COMPANY_NAME));
            companyAddress = cursor.getString(cursor.getColumnIndex(DataContract.PaperSettings.COL_COMPANY_ADDRESS));
            companyPhone = cursor.getString(cursor.getColumnIndex(DataContract.PaperSettings.COL_COMPANY_PHONE));
            footer = cursor.getString(cursor.getColumnIndex(DataContract.PaperSettings.COL_FOOTER));

            img = cursor.getBlob(cursor.getColumnIndex(DataContract.PaperSettings.COL_LOGO));
            try {
                 bitmap = BitmapFactory.decodeByteArray(img, 0, img.length);

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        cursor.close();
        database.close();



        Paragraph paragraph=null;
        PdfPTable table;
        PdfPCell cell;
        PdfContentByte cb;
        String fileName=dest+"sample2.pdf";
        if (new File(fileName).exists()) {
            new File(fileName).delete();
        }
        Log.d(TAG, "createPdff: "+fileName);

        try {


            /***
             * Variables for further use....
             */
            BaseColor mColorAccent = new BaseColor(0, 153, 204, 255);
            float mHeadingFontSize = 20.0f;
            float mValueFontSize = 26.0f;
            //  ElementList el = parseToElementList(is, new XMLWorkerFontProvider("resources/fonts/"));
//
            // width of 204pt
            float width = 316;

            // height as 10000pt (which is much more than we'll ever need)
            float max =100;// PageSize.LETTER.getHeight();
            if(docsize!=0)
            {
                max=docsize;
            }



            Rectangle pagesize = new Rectangle(width, max + 25);
            // Document with predefined page size
            Document document = new Document(pagesize, 10, 10, 50, 0);
            // Getting PDF Writer
            PdfWriter writer = PdfWriter.getInstance(document, new FileOutputStream(fileName));
            document.open();
            // Column with a writer
            try {

//                Drawable d = getResources().getDrawable(R.mipmap.ic_launcher);
//                BitmapDrawable bitDw = ((BitmapDrawable) d);
//                Bitmap bmp = bitDw.getBitmap();
//                ByteArrayOutputStream stream = new ByteArrayOutputStream();
//                bmp.compress(Bitmap.CompressFormat.PNG, 100, stream);
//                Image image = Image.getInstance(stream.toByteArray());
//                // image.setAbsolutePosition(((width/2)-(image.getPlainWidth()/2)),max-image.getHeight());
//                image.setAlignment(Image.ALIGN_TOP|Image.ALIGN_CENTER);
//                document.add(image);

                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
                Image image = Image.getInstance(stream.toByteArray());
                document.add(image);

            } catch (Exception e) {
                e.printStackTrace();
            }
            paragraph = new Paragraph(companyName);
            paragraph.setAlignment(Element.ALIGN_CENTER);
            document.add(paragraph);

            paragraph = new Paragraph("Phone:"+companyPhone);
            paragraph.setAlignment(Element.ALIGN_CENTER);
            document.add(paragraph);

            paragraph = new Paragraph(companyAddress);
            paragraph.setAlignment(Element.ALIGN_CENTER);
            document.add(paragraph);

            DashedSeparator separator = new DashedSeparator();
            separator.setPercentage(59500f / 523f);
            Chunk linebreak = new Chunk(separator);
            document.add(linebreak);

            paragraph = new Paragraph("Receipt No:"+receiptNo);
            paragraph.setAlignment(Element.ALIGN_LEFT);
            document.add(paragraph);

            paragraph = new Paragraph("Receipt Date:"+receiptDate);
            paragraph.setAlignment(Element.ALIGN_LEFT);
            document.add(paragraph);
            document.add(linebreak)  ;
            float p=(width-20)/3;
            float re= (p*2)/5;
            float rem=re/3;

            float[] columnWidths = { re,p, re+rem,re+rem,re+rem };
            table = new PdfPTable(columnWidths);
            table.setTotalWidth(width-20);
            table.setLockedWidth(true);
            table.setHorizontalAlignment(Element.ALIGN_LEFT);

            cell = new PdfPCell(new Phrase("SN"));
            cell.setHorizontalAlignment(Element.ALIGN_LEFT);
            cell.setBorder(Rectangle.NO_BORDER);
            table.addCell(cell);

            cell = new PdfPCell(new Phrase("Item "));
            cell.setHorizontalAlignment(Element.ALIGN_LEFT);
            cell.setBorder(Rectangle.NO_BORDER);
            table.addCell(cell);

            cell = new PdfPCell(new Phrase("Rate"));
            cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
            cell.setBorder(Rectangle.NO_BORDER);
            table.addCell(cell);

            cell = new PdfPCell(new Phrase("Qty"));
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            cell.setBorder(Rectangle.NO_BORDER);
            table.addCell(cell);

            cell = new PdfPCell(new Phrase("Total"));
            cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
            cell.setBorder(Rectangle.NO_BORDER);
            table.addCell(cell);
            DashedSeparator separato= new DashedSeparator();
            separato.setPercentage(59500f / 523f);
            cell = new PdfPCell(new Phrase(new Chunk(separato)));
            cell.setColspan(5);
            cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
            cell.setBorder(Rectangle.NO_BORDER);
            table.addCell(cell);
            table.setHeaderRows(1);

            for (int i = 0; i <  15; i++) {
                cell = new PdfPCell(new Phrase(String.valueOf(i + 1)));
                cell.setHorizontalAlignment(Element.ALIGN_LEFT);
                cell.setBorder(Rectangle.NO_BORDER);
                table.addCell(cell);

                cell = new PdfPCell(new Phrase("product"+i));
                cell.setHorizontalAlignment(Element.ALIGN_LEFT);
                cell.setBorder(Rectangle.NO_BORDER);
                table.addCell(cell);

                cell = new PdfPCell(new Phrase(i+10+""));
                cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
                cell.setBorder(Rectangle.NO_BORDER);
                table.addCell(cell);

                cell = new PdfPCell(new Phrase(2+i+""));
                cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                cell.setBorder(Rectangle.NO_BORDER);
                table.addCell(cell);

                //total += Double.parseDouble(i+2);

                cell = new PdfPCell(new Phrase("10"));
                cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
                cell.setBorder(Rectangle.NO_BORDER);
                table.addCell(cell);

            }
            DashedSeparator separator1 = new DashedSeparator();
            separator1.setPercentage(59500f / 523f);
            cell = new PdfPCell(new Phrase(new Chunk(separator1)));
            cell.setColspan(5);
            cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
            cell.setBorder(Rectangle.NO_BORDER);
            table.addCell(cell);

            cell = new PdfPCell(new Phrase("Amount "));
            cell.setColspan(3);
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            cell.setBorder(Rectangle.NO_BORDER);
            table.addCell(cell);

            cell = new PdfPCell(new Phrase(String.valueOf(amount)));
            cell.setColspan(2);
            cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
            cell.setBorder(Rectangle.NO_BORDER);
            table.addCell(cell);

            cell = new PdfPCell(new Phrase("Tax"));
            cell.setColspan(3);
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            cell.setBorder(Rectangle.NO_BORDER);
            table.addCell(cell);

            cell = new PdfPCell(new Phrase(String.valueOf(100)));
            cell.setColspan(2);
            cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
            cell.setBorder(Rectangle.NO_BORDER);
            table.addCell(cell);

            cell = new PdfPCell(new Phrase("Total"));
            cell.setColspan(3);
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            cell.setBorder(Rectangle.NO_BORDER);
            table.addCell(cell);

            cell = new PdfPCell(new Phrase(String.valueOf(100)));
            cell.setColspan(2);
            cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
            cell.setBorder(Rectangle.NO_BORDER);
            table.addCell(cell);

            cell = new PdfPCell(new Phrase(new Chunk(separator1)));
            cell.setColspan(5);
            cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
            cell.setBorder(Rectangle.NO_BORDER);
            table.addCell(cell);

            cell = new PdfPCell(new Phrase("Discount"));
            cell.setColspan(3);
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            cell.setBorder(Rectangle.NO_BORDER);
            table.addCell(cell);

            cell = new PdfPCell(new Phrase(String.valueOf(100)));
            cell.setColspan(2);
            cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
            cell.setBorder(Rectangle.NO_BORDER);
            table.addCell(cell);

            cell = new PdfPCell(new Phrase(new Chunk(separator1)));
            cell.setColspan(5);
            cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
            cell.setBorder(Rectangle.NO_BORDER);
            table.addCell(cell);

            cell = new PdfPCell(new Phrase("Cash"));
            cell.setColspan(3);
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            cell.setBorder(Rectangle.NO_BORDER);
            table.addCell(cell);

            cell = new PdfPCell(new Phrase(String.valueOf(100)));
            cell.setColspan(2);
            cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
            cell.setBorder(Rectangle.NO_BORDER);
            table.addCell(cell);

            cell = new PdfPCell(new Phrase(new Chunk(separator1)));
            cell.setColspan(5);
            cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
            cell.setBorder(Rectangle.NO_BORDER);
            table.addCell(cell);

            document.add(table);
            DottedLineSeparator separatorr = new DottedLineSeparator();
            separator1.setPercentage(59500f / 523f);
            cell = new PdfPCell(new Phrase(new Chunk(separator1)));
            cell.setColspan(5);
            cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
            cell.setBorder(Rectangle.NO_BORDER);
            table.addCell(cell);
            paragraph = new Paragraph(footer);
            paragraph.setAlignment(Element.ALIGN_CENTER);
            document.add(paragraph);
           /* ct = new ColumnText(writer.getDirectContent());
            ct.setSimpleColumn(pagesize);
            for (int i=0;i<10;i++) {

                ct.addText(new Chunk("Pratik Butani"));

            }
            ct.go();
            // closing the document
            document.close();*/

            document.close();
            Log.d(TAG, "PDF created: "+docsize);
            if(docsize==0) {
                float size=(writer.getPageNumber())*max;
                createPdff(dest, size);
            }
            else
            {viewPdf();}
        } catch (Exception e) {
            e.printStackTrace();

        }
    }
    private void viewPdf() {
        Log.d(TAG, "viewPdf: ");
       // if (fileName != null && !fileName.isEmpty()){

            String name =FileUtils.getSubDirPath(mContext, DataContract.DIR_RECEIPT) + "sample2.pdf";
            Log.d(TAG, "viewPdf: file name "+name);

            pdfView.fromFile(new File(name))
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
//        }else {
//            Log.d(TAG, "Invalid file : ");
//        }


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.tool_bar, menu);
        itemPrint=menu.findItem(R.id.action_print);
        itemSync=menu.findItem(R.id.action_sync);
        itemShare=menu.findItem(R.id.action_share);
         itemSync.setVisible(false);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_print) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

}
