package com.alhikmahpro.www.e_inventory.View;

import android.Manifest;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v4.view.ViewCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.alhikmahpro.www.e_inventory.Adapter.ReceiptAdapter;
import com.alhikmahpro.www.e_inventory.AppUtils;
import com.alhikmahpro.www.e_inventory.Data.Cart;
import com.alhikmahpro.www.e_inventory.Data.CartModel;
import com.alhikmahpro.www.e_inventory.Data.CreatePdf;
import com.alhikmahpro.www.e_inventory.Data.DataContract;
import com.alhikmahpro.www.e_inventory.Data.ItemModel;
import com.alhikmahpro.www.e_inventory.Data.ReceiptModel;
import com.alhikmahpro.www.e_inventory.Data.dbHelper;
import com.alhikmahpro.www.e_inventory.FileUtils;
import com.alhikmahpro.www.e_inventory.Interface.OnAdapterClickListener;
import com.alhikmahpro.www.e_inventory.Interface.OnListAdapterClickListener;
import com.alhikmahpro.www.e_inventory.R;
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
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class ListReceiptActivity extends AppCompatActivity {

    @BindView(R.id.doc_list_rv)
    RecyclerView docListRv;
    @BindView(R.id.txtEmpty)
    TextView txtEmpty;
    @BindView(R.id.fab)
    FloatingActionButton fab;
    RecyclerView.Adapter adapter;
    RecyclerView.LayoutManager layoutManager;
    private String type;
    List<ReceiptModel> list;
    private static final String TAG = "ListReceiptActivity";
    String companyName = "xxxx", companyAddress = "xxxx", companyPhone = "xxxx", footer = "xxxx";
    String customerName, customerCode, receiptNo, receiptDate;
    String chqNumber, chqDate, paymentType, salesman, remark;
    String fileName;
    double balanceAmount, receiptAmount;
    AlertDialog alertDialog;
    AlertDialog.Builder builder;
    private static final int PERMISSION_CODE = 100;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_receipt);
        ButterKnife.bind(this);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
//        Intent intent = getIntent();
//        type= intent.getStringExtra("Type");
        getSupportActionBar().setTitle("Receipts");
        list = new ArrayList<>();

    }

    @Override
    protected void onResume() {
        super.onResume();
        loadReceipts();
    }

    private void loadReceipts() {


        list.clear();
        layoutManager = new LinearLayoutManager(this);
        docListRv.setLayoutManager(layoutManager);
        docListRv.setItemAnimator(new DefaultItemAnimator());
        docListRv.setHasFixedSize(true);
        dbHelper helper = new dbHelper(ListReceiptActivity.this);
        SQLiteDatabase database = helper.getReadableDatabase();
        Cursor cursor = helper.getAllReceipts(database);
        Log.d(TAG, "loadReceipts: " + cursor.getCount());
        if (cursor.moveToFirst()) {

            txtEmpty.setVisibility(View.GONE);
            do {
                //Log.d(TAG, "loadReceipts: "+cursor.getInt(cursor.getColumnIndex(DataContract.Stocks.COL_DOCUMENT_NUMBER)));
                ReceiptModel model = new ReceiptModel();
                model.setReceiptNo(cursor.getString(cursor.getColumnIndex(DataContract.Receipts.COL_RECEIPT_NUMBER)));
                model.setReceiptDate(cursor.getString(cursor.getColumnIndex(DataContract.Receipts.COL_RECEIPT_DATE)));
                model.setSalesmanId(cursor.getString(cursor.getColumnIndex(DataContract.Receipts.COL_SALESMAN_ID)));
                model.setCustomerCode(cursor.getString(cursor.getColumnIndex(DataContract.Receipts.COL_CUSTOMER_CODE)));
                model.setCustomerName(cursor.getString(cursor.getColumnIndex(DataContract.Receipts.COL_CUSTOMER_NAME)));
                model.setReceivedAmount(cursor.getDouble(cursor.getColumnIndex(DataContract.Receipts.COL_RECEIVED_AMOUNT)));
                model.setBalanceAmount(cursor.getDouble(cursor.getColumnIndex(DataContract.Receipts.COL_BALANCE_AMOUNT)));
                model.setPaymentType(cursor.getString(cursor.getColumnIndex(DataContract.Receipts.COL_PAYMENT_TYPE)));
                model.setChequeNumber(cursor.getString(cursor.getColumnIndex(DataContract.Receipts.COL_CHEQUE_NUMBER)));
                model.setChequeDate(cursor.getString(cursor.getColumnIndex(DataContract.Receipts.COL_CHEQUE_DATE)));
                model.setRemark(cursor.getString(cursor.getColumnIndex(DataContract.Receipts.COL_REMARK)));
                model.setIs_sync(cursor.getInt(cursor.getColumnIndex(DataContract.Receipts.COL_IS_SYNC)));
                list.add(model);
            } while (cursor.moveToNext());


            adapter = new ReceiptAdapter(this, list, new OnListAdapterClickListener() {
                @Override
                public void OnEditClicked(int position) {

                    ReceiptModel receiptModel = list.get(position);
                    customerName = receiptModel.getCustomerName();
                    customerCode = receiptModel.getCustomerCode();
                    salesman = receiptModel.getSalesmanId();
                    receiptNo = receiptModel.getReceiptNo();
                    receiptDate = receiptModel.getReceiptDate();
                    balanceAmount = receiptModel.getBalanceAmount();
                    receiptAmount = receiptModel.getReceivedAmount();
                    paymentType = receiptModel.getPaymentType();
                    chqNumber = receiptModel.getChequeNumber();
                    chqDate = receiptModel.getChequeDate();
                    remark = receiptModel.getRemark();

                    Log.d(TAG, "OnEditClicked: date" + receiptDate + "number" + receiptNo);

                    Intent intent = new Intent(ListReceiptActivity.this, ReceiptActivity.class);
                    intent.putExtra("TYPE", "EDIT");
                    intent.putExtra("CUS_NAME", customerName);
                    intent.putExtra("CUS_CODE", customerCode);
                    intent.putExtra("SALESMAN", salesman);
                    intent.putExtra("RECEIPT_NO", receiptNo);
                    intent.putExtra("RECEIPT_DATE", receiptDate);
                    intent.putExtra("BALANCE_AMOUNT", balanceAmount);
                    intent.putExtra("RECEIVED_AMOUNT", receiptAmount);
                    intent.putExtra("PAYMENT_TYPE", paymentType);
                    intent.putExtra("CHEQUE_NUMBER", chqNumber);
                    intent.putExtra("CHEQUE_DATE", chqDate);
                    intent.putExtra("REMARK", remark);
                    startActivity(intent);
                }

                @Override
                public void OnSyncClicked(int position) {

                }

                @Override
                public void OnShareClicked(int position) {

                    ReceiptModel receiptModel = list.get(position);
                    customerName = receiptModel.getCustomerName();
                    customerCode = receiptModel.getCustomerCode();
                    salesman = receiptModel.getSalesmanId();
                    receiptNo = receiptModel.getReceiptNo();
                    receiptDate = receiptModel.getReceiptDate();
                    balanceAmount = receiptModel.getBalanceAmount();
                    receiptAmount = receiptModel.getReceivedAmount();
                    paymentType = receiptModel.getPaymentType();
                    chqNumber = receiptModel.getChequeNumber();
                    chqDate = receiptModel.getChequeDate();
                    Log.d(TAG, "onShare: date" + receiptDate + "number" + receiptNo);

                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

                        requestStoragePermission();
                    } else {
                        createPdfWrapper();
                    }
                }

                @Override
                public void OnDeleteClicked(int position) {

                }
            });

            docListRv.setAdapter(adapter);
            ViewCompat.setNestedScrollingEnabled(docListRv, false);

        } else {
            txtEmpty.setVisibility(View.VISIBLE);
        }
        cursor.close();
        database.close();
    }



    private void createPdfWrapper() {
        Log.d(TAG, "createPdfWrapper: ");
        dbHelper helper = new dbHelper(ListReceiptActivity.this);
        SQLiteDatabase database = helper.getReadableDatabase();
        Cursor cursor = helper.getPaperSettings(database);
        if (cursor.moveToFirst()) {
            companyName = cursor.getString(cursor.getColumnIndex(DataContract.PaperSettings.COL_COMPANY_NAME));
            companyAddress = cursor.getString(cursor.getColumnIndex(DataContract.PaperSettings.COL_COMPANY_ADDRESS));
            companyPhone = cursor.getString(cursor.getColumnIndex(DataContract.PaperSettings.COL_COMPANY_PHONE));
            footer = cursor.getString(cursor.getColumnIndex(DataContract.PaperSettings.COL_FOOTER));

        }
        CreatePDF createPdf = new CreatePDF();
        createPdf.execute();
//        else {
//            Toast.makeText(ListReceiptActivity.this, "Setup header and footer ", Toast.LENGTH_SHORT).show();
//        }
        cursor.close();
        database.close();
    }

    @OnClick(R.id.fab)
    public void onViewClicked() {

        Intent intent = new Intent(ListReceiptActivity.this, CheckCustomerActivity.class);
        intent.putExtra("Type", "REC");
        startActivity(intent);
    }

    private void requestStoragePermission() {
        Dexter.withActivity(this)
                .withPermissions(
                        Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.READ_EXTERNAL_STORAGE
                )
                .withListener(new MultiplePermissionsListener() {
                    @Override
                    public void onPermissionsChecked(MultiplePermissionsReport report) {
                        //check all permission granted
                        if (report.areAllPermissionsGranted()) {
                            createPdfWrapper();
                        }
                        //check any permission permanent denied
                        if (report.isAnyPermissionPermanentlyDenied()) {
                            showSettingDialog();
                        }
                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token) {
                        token.continuePermissionRequest();

                    }
                }).check();


    }
    private void showSettingDialog() {
        android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(ListReceiptActivity.this);
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


    private class CreatePDF extends AsyncTask<String, Void, String> {


        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            ShowProgressDialog();

        }

        @Override
        protected String doInBackground(String... strings) {

            if (createPDF()) {
                return "success";
            } else {
                return "failed";
            }

        }

        @Override
        protected void onPostExecute(String result) {
            HideProgressDialog();
            Log.d(TAG, "onPostExecute: " + result);
            if (result != null & result.equals("success")) {
                openGeneratedPDF();
            }
        }

    }


    private boolean createPDF() {
        final DecimalFormat decimalFormat = new DecimalFormat("0.00");
        String mDate = AppUtils.getFormattedDate();
        Document document = new Document();
        File pdfFile;
        File dir;
        try {


            //create directory/PriceChecker/Receipts
            String directoryPath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/PriceChecker/Receipts";
            dir = new File(directoryPath);
            if (!dir.exists()) {
                dir.mkdir();
            }

            fileName = receiptNo + "-" + mDate + ".pdf";
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
            Chunk mOrderIdChunk = new Chunk("Date:", mOrderIdFont);
            Paragraph mOrderIdParagraph = new Paragraph(mOrderIdChunk);
            document.add(mOrderIdParagraph);

            Font mOrderIdValueFont = new Font(urName, mValueFontSize, Font.NORMAL, BaseColor.BLACK);
            Chunk mOrderIdValueChunk = new Chunk(receiptDate, mOrderIdValueFont);
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
            Chunk mOrderSalesmanChunk = new Chunk("Receipt No:", mOrderSalesmanFont);
            Paragraph mOrderSalesmanParagraph = new Paragraph(mOrderSalesmanChunk);
            document.add(mOrderSalesmanParagraph);

            Font mOrderSalesmanValueFont = new Font(urName, mValueFontSize, Font.NORMAL, BaseColor.BLACK);
            Chunk mOrderSalesmanValueChunk = new Chunk(receiptNo, mOrderSalesmanValueFont);
            Paragraph mOrderSalesmanValueParagraph = new Paragraph(mOrderSalesmanValueChunk);
            document.add(mOrderSalesmanValueParagraph);

            document.add(new Paragraph(""));
            document.add(new Chunk(lineSeparator));
            document.add(new Paragraph(""));


            Font mOrderAmountFont = new Font(urName, mHeadingFontSize, Font.BOLD, mColorAccent);
            Chunk mOrderAmountChunk = new Chunk("Received Amount:", mOrderAmountFont);
            Paragraph mOrderAmountParagraph = new Paragraph(mOrderAmountChunk);
            document.add(mOrderAmountParagraph);


            Font mOrderAmountValueFont = new Font(urName, mValueFontSize, Font.NORMAL, BaseColor.BLACK);
            Chunk mOrderAmountValueChunk = new Chunk(decimalFormat.format(receiptAmount), mOrderAmountValueFont);
            Paragraph mOrderAmountValueParagraph = new Paragraph(mOrderAmountValueChunk);
            document.add(mOrderAmountValueParagraph);

            document.add(paragraph);
            document.close();
            Log.d(TAG, "createPDF: pdf created");
            return true;


        } catch (DocumentException de) {
            de.printStackTrace();
            return false;

        } catch (IOException e) {
            e.printStackTrace();
            return false;

        }
    }


    public void ShowProgressDialog() {
        builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View dialogView = inflater.inflate(R.layout.progress, null);
        builder.setView(dialogView);
        builder.setCancelable(false);
        alertDialog = builder.create();
        alertDialog.show();
    }

    public void HideProgressDialog() {

        alertDialog.dismiss();
    }


    private void openGeneratedPDF() {

//        File outputFile = new File(Environment.getExternalStoragePublicDirectory
//                (Environment.DIRECTORY_DOWNLOADS), "sample.pdf");
        File outputFile = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/PriceChecker/Receipts/" + fileName);
        // Uri uri = Uri.fromFile(outputFile);
        Uri uri = FileProvider.getUriForFile(ListReceiptActivity.this, ListReceiptActivity.this.getPackageName() + ".provider", outputFile);

        Intent share = new Intent();
        share.setAction(Intent.ACTION_SEND);
        share.setType("application/pdf");
        share.putExtra(Intent.EXTRA_STREAM, uri);
        startActivity(Intent.createChooser(share, "Share to :"));


        //ListReceiptActivity.startActivity(share);


    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

}
