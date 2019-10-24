package com.alhikmahpro.www.e_inventory.View;

import android.Manifest;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.alhikmahpro.www.e_inventory.AppUtils;
import com.alhikmahpro.www.e_inventory.Data.Cart;
import com.alhikmahpro.www.e_inventory.Data.DashedSeparator;
import com.alhikmahpro.www.e_inventory.Data.DataContract;
import com.alhikmahpro.www.e_inventory.Data.PrinterCommands;
import com.alhikmahpro.www.e_inventory.Data.SessionHandler;
import com.alhikmahpro.www.e_inventory.Data.Utils;
import com.alhikmahpro.www.e_inventory.Data.dbHelper;
import com.alhikmahpro.www.e_inventory.FileUtils;
import com.alhikmahpro.www.e_inventory.Interface.volleyListener;
import com.alhikmahpro.www.e_inventory.Network.VolleyServiceGateway;
import com.alhikmahpro.www.e_inventory.R;
import com.android.volley.VolleyError;
import com.ganesh.intermecarabic.Arabic864;
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
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.UUID;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class    ReceiptActivity extends AppCompatActivity {

    @BindView(R.id.txt_date)
    TextView txtDate;
    @BindView(R.id.txt_receipt)
    TextView txtReceipt;
    @BindView(R.id.textViewCustomerName)
    TextView textViewCustomerName;
    @BindView(R.id.editTextBalance)
    EditText editTextBalance;
    @BindView(R.id.editTextAmount)
    EditText editTextAmount;
    @BindView(R.id.radioCash)
    RadioButton radioCash;
    @BindView(R.id.radioCheque)
    RadioButton radioCheque;
    @BindView(R.id.radioGroup)
    RadioGroup radioGroup;
    @BindView(R.id.editTextChequeNumber)
    EditText editTextChequeNumber;
    @BindView(R.id.editTextChequeDate)
    EditText editTextChequeDate;
    @BindView(R.id.cheque_layout)
    LinearLayout chequeLayout;
    @BindView(R.id.txt_remark)
    TextView txtRemark;
    @BindView(R.id.editTextRemark)
    EditText editTextRemark;
    @BindView(R.id.btnPrint)
    Button btnPrint;
    @BindView(R.id.btnNew)
    Button btnNew;
    //    @BindView(R.id.btnPdf)
//    Button btnPdf;
    String mDate, mDoc;

    String type, paymentType;
    String customerName, customerCode;
    double balanceAmount,receivedAmount;
    RadioButton radioButton;
    Calendar myCalendar;

    private ProgressDialog dialog;
    volleyListener mVolleyListener;
    VolleyServiceGateway serviceGateway;
    BluetoothAdapter bluetoothAdapter;
    BluetoothSocket bluetoothSocket;
    BluetoothDevice bluetoothDevice;

    OutputStream outputStream;
    InputStream inputStream;
    Thread thread;

    byte[] readBuffer;
    int readBufferPosition;
    volatile boolean stopWorker;
    private static final int REQUEST_CODE_ENABLING_BT = 1;
    String mDeviceAddress, mDeviceName;
    private UUID applicationUUID = UUID
            .fromString("00001101-0000-1000-8000-00805F9B34FB");
    BluetoothDevice mBluetoothDevice;
    boolean billStatus;
    Context mContext;

    String companyName="xxxxx", companyAddress="xxxx", companyPhone="xxxxx", footer="xxxxxx",fileName;
    private static final String TAG = "ReceiptActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_receipt);
        ButterKnife.bind(this);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        editTextBalance.setEnabled(false);
        getSupportActionBar().setTitle("New Receipts");
        Intent intent = getIntent();
        type = intent.getStringExtra("TYPE");
        customerName = intent.getStringExtra("CUS_NAME");
        customerCode = intent.getStringExtra("CUS_CODE");
        balanceAmount = intent.getDoubleExtra("BALANCE_AMOUNT",0);
        paymentType = intent.getStringExtra("PAYMENT_TYPE");
        billStatus = false;
        mContext=getApplicationContext();

        Log.d(TAG, "onCreate: balance :" + balanceAmount);
        myCalendar = Calendar.getInstance();
        initView();
        initVolleyCallBack();


    }

    DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker datePicker, int year, int monthOfYear, int dayOfMonth) {
            // TODO Auto-generated method stub
            myCalendar.set(Calendar.YEAR, year);
            myCalendar.set(Calendar.MONTH, monthOfYear);
            myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
            updateLabel();
        }
    };

    private void updateLabel() {
        String myFormat = "dd-MM-yy"; //In which you need put here
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);
        editTextChequeDate.setText(sdf.format(myCalendar.getTime()));
    }

    private void initView() {


        if (paymentType != null && paymentType.equals("Cash")) {
            radioCash.setChecked(true);
            chequeLayout.setVisibility(View.GONE);
        } else {
            radioCheque.setChecked(true);
            chequeLayout.setVisibility(View.VISIBLE);
        }


        if (type != null && type.equals("NEW")) {
            SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
            dbHelper helper = new dbHelper(this);
            mDate = sdf.format(new Date());
            int last_no = helper.getLastReceiptNo();
            Log.d(TAG, "setDoc: " + last_no);
            int doc = last_no + 1;
            mDoc = String.valueOf(doc);

        } else {
            Intent intent = getIntent();
            mDate = intent.getStringExtra("RECEIPT_DATE");
            mDoc = intent.getStringExtra("RECEIPT_NO");
            String recAmount = intent.getStringExtra("RECEIVED_AMOUNT");
            //double amount = ParseDouble(intent.getDoubleExtra("RECEIVED_AMOUNT",0));
            editTextAmount.setText(String.valueOf(intent.getDoubleExtra("RECEIVED_AMOUNT",0)));
            editTextChequeDate.setText(intent.getStringExtra("CHEQUE_DATE"));
            editTextChequeNumber.setText(intent.getStringExtra("CHEQUE_NUMBER"));
            editTextRemark.setText(intent.getStringExtra("REMARK"));


        }

        //double balAmount = ParseDouble(balanceAmount);
        editTextBalance.setText(currencyFormatter(balanceAmount));
        textViewCustomerName.setText(customerName);
        txtDate.setText(mDate);
        txtReceipt.setText(String.valueOf(mDoc));
    }

    @OnClick({R.id.editTextChequeDate, R.id.btnPrint, R.id.btnNew})
    public void onViewClicked(View view) {
        switch (view.getId()) {

            case R.id.editTextChequeDate:
                new DatePickerDialog(this, date, myCalendar.get(Calendar.YEAR), myCalendar.get(Calendar.MONTH), myCalendar.get(Calendar.DAY_OF_MONTH)).show();
                break;
            case R.id.btnPrint:

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    requestStoragePermission();
                } else {
                    //createPdff(FileUtils.getSubDirPath(mContext,DataContract.DIR_RECEIPT),0);
                    showPdf("sample");
                }


                //check its first time or not; is first time then just print the bill;else send to server and save to local db then print
//                if (billStatus) {
//
//                    printingJob();
//                } else {
//                    sendToServer();
//                }

                break;
            case R.id.btnNew:
                clearActivity();
                break;
        }

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
                        if(report.areAllPermissionsGranted()){
                            createPdff(FileUtils.getSubDirPath(mContext,DataContract.DIR_RECEIPT),0);
                        }
                        //check any permission permanent denied
                        if(report.isAnyPermissionPermanentlyDenied()){
                            showSettingDialog();
                        }
                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token) {
                        token.continuePermissionRequest();

                    }
                }).check();
    }
    private  void createPdff(String dest ,float docsize) {

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

            } catch (Exception e) {
                e.printStackTrace();
            }
            paragraph = new Paragraph("Hotel VApps");
            paragraph.setAlignment(Element.ALIGN_CENTER);
            document.add(paragraph);

            paragraph = new Paragraph("Phone: 9943123000");
            paragraph.setAlignment(Element.ALIGN_CENTER);
            document.add(paragraph);

            paragraph = new Paragraph("Email : vijaydhasxx@gmail.com");
            paragraph.setAlignment(Element.ALIGN_CENTER);
            document.add(paragraph);

            DashedSeparator separator = new DashedSeparator();
            separator.setPercentage(59500f / 523f);
            Chunk linebreak = new Chunk(separator);
            document.add(linebreak);

            paragraph = new Paragraph("Bill No: 12345");
            paragraph.setAlignment(Element.ALIGN_LEFT);
            document.add(paragraph);

            paragraph = new Paragraph("Bill Date: 01/04/2015 10:30:55 PM");
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

            cell = new PdfPCell(new Phrase("Item Total"));
            cell.setColspan(3);
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            cell.setBorder(Rectangle.NO_BORDER);
            table.addCell(cell);

            cell = new PdfPCell(new Phrase(String.valueOf(100)));
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
            paragraph = new Paragraph("Thank You for Your Purchase");
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
            {showPdf(fileName);}
        } catch (Exception e) {
            e.printStackTrace();

        }
    }

    private void showPdf(String filename) {

        Intent view_pdf = new Intent(this, ViewPdfActivity.class);
        view_pdf.putExtra("FILE_NAME", filename);
        startActivity(view_pdf);



    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        clearActivity();
        finish();
    }

    private void sendToServer() {
        String url = "PriceChecker/Receipt_voucher.php";
        JSONObject receiptObject = new JSONObject();
        JSONArray receiptArray = new JSONArray();
        JSONObject result = new JSONObject();
        try {
            receiptObject.put("receipt_no", txtReceipt.getText().toString());
            receiptObject.put("receipt_date", mDate);
            receiptObject.put(DataContract.Receipts.COL_CUSTOMER_CODE, customerCode);
            receiptObject.put(DataContract.Receipts.COL_CUSTOMER_NAME, customerName);
            //receiptObject.put(DataContract.Receipts.COL_SALESMAN_ID, salesmanId);
            receiptObject.put(DataContract.Receipts.COL_RECEIVED_AMOUNT, editTextAmount.getText().toString());
            receiptObject.put(DataContract.Receipts.COL_CHEQUE_NUMBER, editTextChequeNumber.getText().toString());
            receiptObject.put(DataContract.Receipts.COL_CHEQUE_DATE, editTextChequeDate.getText().toString());
            receiptObject.put(DataContract.Receipts.COL_REMARK,editTextRemark.getText().toString());
            receiptObject.put("receipt_type",paymentType );
            receiptArray.put(receiptObject);

            result.put("Receipt",receiptArray);


        } catch (JSONException e) {
            e.printStackTrace();
        }

        if (result.length() > 0) {



            //send to volley
            View view = this.getCurrentFocus();
            AppUtils.hideKeyboard(this, view);

            if (!AppUtils.isNetworkAvailable(this)) {
                Toast.makeText(this, "No Internet ", Toast.LENGTH_SHORT).show();
                // no internet save to local db directly
                saveToDatabase(DataContract.SYNC_STATUS_FAILED);

            } else {
                // if internet available send to server
                serviceGateway = new VolleyServiceGateway(mVolleyListener, this);
                serviceGateway.postDataVolley("POSTCALL", url, result);
            }

        }
    }

    private void initVolleyCallBack() {

        //server response

        mVolleyListener = new volleyListener() {
            @Override
            public void notifySuccess(String requestType, JSONObject response) {
                String result = "Sync Failed !";
                int syncStatus = DataContract.SYNC_STATUS_FAILED;

                try {
                    String res = response.getString("Status");
                    if (res.equals("success")) {
                        syncStatus = DataContract.SYNC_STATUS_OK;
                        result = "Sync Successful";
                    }

                } catch (JSONException e) {
                    result = "Sync Error";
                    e.printStackTrace();
                } finally {

                    // save to local database
                    Toast.makeText(ReceiptActivity.this, result, Toast.LENGTH_SHORT).show();
                    saveToDatabase(syncStatus);

                }
            }

            @Override
            public void notifyError(String requestType, VolleyError error) {
                Log.d(TAG, "notifyError: " + error);
                Toast.makeText(ReceiptActivity.this, "Connection Error !", Toast.LENGTH_SHORT).show();
                saveToDatabase(DataContract.SYNC_STATUS_FAILED);

            }
        };

    }


    private void saveToDatabase(int sync) {
        Log.d(TAG, "saveToDatabase: "+sync);
        // save data to local data base
        double receivedAmount=ParseDouble(editTextAmount.getText().toString());
        dbHelper helper = new dbHelper(this);
        if (type.equals("EDIT")) {
            if (helper.updateReceipt(txtReceipt.getText().toString(), mDate, "Tab", customerCode, customerName, balanceAmount,receivedAmount,
                    paymentType, editTextChequeDate.getText().toString(), editTextChequeNumber.getText().toString(),editTextRemark.getText().toString(), sync)) {
                Log.d(TAG, "saveToDatabase: updated");
                billStatus = true;
                printingJob();
            }

        } else {
            if (helper.saveReceipts(txtReceipt.getText().toString(), mDate, "Tab", customerCode, customerName, balanceAmount,receivedAmount,
                    paymentType, editTextChequeDate.getText().toString(), editTextChequeNumber.getText().toString(), editTextRemark.getText().toString(),sync)) {
                Log.d(TAG, "saveToDatabase: Saved");
                billStatus = true;
                printingJob();
            } else {
                Toast.makeText(this, "Saving Failed", Toast.LENGTH_SHORT).show();
            }
        }

    }

    public void onRadioButtonClicked(View view) {
        int radioId = radioGroup.getCheckedRadioButtonId();
        Log.d(TAG, "onRadioButtonClicked: " + radioId);
        radioButton = findViewById(radioId);
        paymentType = radioButton.getText().toString();
        if (paymentType.equals("Cash")) {
            chequeLayout.setVisibility(View.GONE);
        } else {
            chequeLayout.setVisibility(View.VISIBLE);
        }

        Toast.makeText(this, "selected type" + paymentType, Toast.LENGTH_SHORT).show();
    }


    public String currencyFormatter(double val) {
        NumberFormat format = NumberFormat.getCurrencyInstance();
        String pattern = ((DecimalFormat) format).toPattern();
        String newPattern = pattern.replace("\u00A4", "").trim();
        NumberFormat newFormat = new DecimalFormat(newPattern);
        return String.valueOf(newFormat.format(val));

    }

    double ParseDouble(String strNumber) {
        if (strNumber != null && strNumber.length() > 0) {
            try {
                return Double.parseDouble(strNumber);
            } catch (Exception e) {
                return -1;   // or some value to mark this field is wrong. or make a function validates field first ...
            }
        } else return 0;
    }


    private void closePrinter() {
        try {
            stopWorker = true;
            if (outputStream != null && inputStream != null && bluetoothSocket != null) {
                outputStream.close();
                inputStream.close();
                bluetoothSocket.close();
                Log.d(TAG, "Printer Disconnected.");
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }


    private void printingJob() {
        dbHelper helper = new dbHelper(this);
        SQLiteDatabase database = helper.getReadableDatabase();
        Cursor cursor = helper.getPaperSettings(database);

        if (cursor.moveToFirst()) {
            companyName = cursor.getString(cursor.getColumnIndex(DataContract.PaperSettings.COL_COMPANY_NAME));
            companyAddress = cursor.getString(cursor.getColumnIndex(DataContract.PaperSettings.COL_COMPANY_ADDRESS));
            companyPhone = cursor.getString(cursor.getColumnIndex(DataContract.PaperSettings.COL_COMPANY_PHONE));
            footer = cursor.getString(cursor.getColumnIndex(DataContract.PaperSettings.COL_FOOTER));
        }

            try {
                // close if any connection exists
                closePrinter();
                FindBluetoothDevice();
                // openBluetoothPrinter();
                printData();

            } catch (Exception ex) {
                ex.printStackTrace();
            }

//        else {
//            Toast.makeText(this, "Setup header and footer before printing..", Toast.LENGTH_SHORT).show();
//        }
        Log.d(TAG, "initView: " + companyName);
        cursor.close();
        database.close();
    }



    private void clearActivity() {
        closePrinter();

        // finish all activity and go to home activity
        Intent intent = new Intent(getApplicationContext(), ListReceiptActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }

    private void FindBluetoothDevice() {
        try {
            String spDeviceAddress = SessionHandler.getInstance(this).getPrinterName();
            Log.d(TAG, "spDeviceAddress" + spDeviceAddress);

            bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
            if (bluetoothAdapter == null) {
                Log.d(TAG, "Bluetooth Adapter not found");
            }
            if (!bluetoothAdapter.isEnabled()) {
                Intent enableBT = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(enableBT, REQUEST_CODE_ENABLING_BT);
            }

            Set<BluetoothDevice> pairedDevice = bluetoothAdapter.getBondedDevices();

            if (pairedDevice.size() > 0) {
                for (BluetoothDevice pairedDev : pairedDevice) {

                    Log.d(TAG, "printer name:" + pairedDev.getName());
                    Log.d(TAG, "printer address:" + pairedDev.getAddress());
                    if (pairedDev.getAddress().equals(spDeviceAddress)) {
                        bluetoothDevice = pairedDev;
                        Log.d(TAG, "Printer Found " + pairedDev.getName());
                        openBluetoothPrinter();
                        break;
                    }
                    else {
                        Toast.makeText(this, "Printer not found", Toast.LENGTH_SHORT).show();
                    }
                }
            }else {
                Toast.makeText(this, "Printer not paired", Toast.LENGTH_SHORT).show();
            }

        } catch (Exception ex) {
            ex.printStackTrace();
        }

    }

    void openBluetoothPrinter() throws IOException {
        try {

            //Standard uuid from string //
            UUID uuidSting = UUID.fromString("00001101-0000-1000-8000-00805f9b34fb");
            bluetoothSocket = bluetoothDevice.createRfcommSocketToServiceRecord(uuidSting);
            bluetoothSocket.connect();
            outputStream = bluetoothSocket.getOutputStream();
            inputStream = bluetoothSocket.getInputStream();
            Log.d(TAG, "printer connected:");
            beginListenData();

        } catch (Exception ex) {
            ex.printStackTrace();

        }
    }

    void beginListenData() {
        try {

            final byte delimiter = 10;
            stopWorker = false;
            readBufferPosition = 0;
            readBuffer = new byte[1024];

            thread = new Thread(new Runnable() {
                @Override
                public void run() {

                    while (!Thread.currentThread().isInterrupted() && !stopWorker) {
                        try {
                            int byteAvailable = inputStream.available();
                            if (byteAvailable > 0) {
                                byte[] packetByte = new byte[byteAvailable];
                                inputStream.read(packetByte);

                                for (int i = 0; i < byteAvailable; i++) {
                                    byte b = packetByte[i];
                                    if (b == delimiter) {
                                        byte[] encodedByte = new byte[readBufferPosition];
                                        System.arraycopy(
                                                readBuffer, 0,
                                                encodedByte, 0,
                                                encodedByte.length
                                        );
                                        final String data = new String(encodedByte, "US-ASCII");
                                        readBufferPosition = 0;
                                    } else {
                                        readBuffer[readBufferPosition++] = b;
                                    }
                                }
                            }
                        } catch (Exception ex) {
                            stopWorker = true;
                        }
                    }

                }
            });

            thread.start();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void printData() {

        final DecimalFormat decimalFormat = new DecimalFormat("0.00");
        Log.d(TAG, "printBill");
       // Thread thread = new Thread() {

            //public void run() {

                try {
                    // byte[] printformat = new byte[]{0x1B, 0x21, 0x03};
                    // byte[] printformat = new byte[]{30,35,0};
                    //outputStream.write(printformat);

                    printCustom(companyName, 2, 1);
                    printCustom(companyAddress, 1, 1);
                    printCustom("Tel:" + companyPhone, 1, 1);
                    printNewLine();


                    printCustom("Receipt #" + txtReceipt.getText().toString(), 1, 0);
                    printCustom("Date :" + mDate, 1, 0);
                    printCustom("Received from :" + customerName, 1, 0);
                    //printCustom("Salesman :" + salesmanId, 1, 0);
                    printCustom(new String(new char[32]).replace("\0", "."), 1, 1);
                    double recAmount = ParseDouble(editTextAmount.getText().toString());

                    leftRightAlignLarge("Amount", decimalFormat.format(recAmount));//currencyFormatter(recAmount)
                    printNewLine();
                   // leftRightAlignLarge("      ", convertToArabic(editTextAmount.getText().toString()));
                    printNewLine();
                    printCustom(new String(new char[32]).replace("\0", " "), 1, 1);

                    printCustom(new String(new char[32]).replace("\0", "."), 1, 1);
                    printCustom("فاشىن غخع", 1, 1);
                   // printArabic("فاشىن غخع");
                    printNewLine();
                    printNewLine();
                    outputStream.flush();

                } catch (Exception e) {
                    Log.e(TAG, "Exe ", e);
                }
                finally {
                    clearActivity();
                }

            //}
       // };
        //thread.start();
    }

    private String convertToArabic(String str){
        Log.d(TAG, "convertToArabic: ");

        char[] arabicChars = {'٠','١','٢','٣','٤','٥','٦','٧','٨','٩'};
        StringBuilder builder = new StringBuilder();
        for(int i =0;i<str.length();i++)
        {
            if(Character.isDigit(str.charAt(i)))
            {
                builder.append(arabicChars[(int)(str.charAt(i))-48]);
            }
            else
            {
                builder.append(str.charAt(i));
            }
        }
        Log.d(TAG, "Number in English : "+str);
        Log.d(TAG, "Number In Arabic :"+builder.toString());
        return  builder.toString();
    }
    private void printArabic(String msg){
       // String print2 = "قيمت واحد" ;
       // byte[] bytes23 = Utils.getBytes(print2,"windows-1256");
        //outputStream.write(bytes23);
        try {

            Arabic864 arabic = new Arabic864();
            byte[] arabicArr = arabic.Convert("للغة العربية", false);
//
//            outputStream.write(msg.getBytes());
//            outputStream.write(arabicArr);
            byte[] arab = new String("للغة العربية").getBytes("ISO-8859-1");
            byte[] cc = new byte[]{0x1B, 0x21, 0x03};
//            String str = new String(msg.getBytes(), "UTF-8");
//            outputStream.write(PrinterCommands.ESC_ALIGN_CENTER);
            outputStream.write(cc);
            outputStream.write(arabicArr);
            outputStream.write(PrinterCommands.LF);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    private void  printCustom(String msg, int size, int align) {
        //Print config "mode"
        byte[] cc = new byte[]{0x1B, 0x21, 0x03};  // 0- normal size text
        //byte[] cc1 = new byte[]{0x1B,0x21,0x00};  // 0- normal size text
        byte[] bb = new byte[]{0x1B, 0x21, 0x08};  // 1- only bold text
        byte[] bb2 = new byte[]{0x1B, 0x21, 0x20}; // 2- bold with medium text
        byte[] bb3 = new byte[]{0x1B, 0x21, 0x10}; // 3- bold with large text
        try {
            switch (size) {
                case 0:
                    outputStream.write(cc);
                    break;
                case 1:
                    outputStream.write(bb);
                    break;
                case 2:
                    outputStream.write(bb2);
                    break;
                case 3:
                    outputStream.write(bb3);
                    break;
            }

            switch (align) {
                case 0:
                    //left align
                    outputStream.write(PrinterCommands.ESC_ALIGN_LEFT);
                    break;
                case 1:
                    //center align
                    outputStream.write(PrinterCommands.ESC_ALIGN_CENTER);
                    break;
                case 2:
                    //right align
                    outputStream.write(PrinterCommands.ESC_ALIGN_RIGHT);
                    break;
            }
            outputStream.write(msg.getBytes());
            outputStream.write(PrinterCommands.LF);
            //outputStream.write(cc);
            //printNewLine();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    String addspace(int i) {
        String temp = " ";
        StringBuilder str1 = new StringBuilder();
        for (int j = 0; j < i; j++) {
            str1.append(" ");
        }
        //  str1.append(temp);
        return str1.toString();
    }


    //print new line
    private void printNewLine() {
        try {
            outputStream.write(PrinterCommands.FEED_LINE);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private void leftRightAlignLarge(String str1, String str2) {

        byte[] bb3 = new byte[]{0x1B, 0x21, 0x10};


        Log.d(TAG, "leftRightAlign called");
        if (str1.length() > 12) {
            str1 = str1.substring(0, Math.min(str1.length(), 12));
        } else {

            int no_space = 12 - str1.length();
            String space = addspace(no_space);
            str1 = str1 + space;

        }
        Log.d(TAG, "first str with space:" + str1 + "#");
        Log.d(TAG, "first str with space count:" + str1.length());
        if (str2.length() < 8) {
            int no_space = 7 - str2.length();
            String space = addspace(no_space);
            str2 = space + str2;

        }
        Log.d(TAG, "second str with space:" + str2);
        Log.d(TAG, "second str with space count:" + str2.length());
        String ans = str1 + str2;
        Log.d(TAG, "final:" + ans);

        if (ans.length() < 33) {
            int n = (32 - (str1.length() + str2.length()));
            Log.d(TAG, "n count:" + n);
            ans = str1 + new String(new char[n]).replace("\0", " ") + str2;
            Log.d(TAG, "final str with space count:" + ans.length());
        }


        try {
            outputStream.write(bb3);
            outputStream.write(ans.getBytes());

        } catch (IOException e) {
            e.printStackTrace();
        }
    }



    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    private void showSettingDialog() {
        android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(ReceiptActivity.this);
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

}
