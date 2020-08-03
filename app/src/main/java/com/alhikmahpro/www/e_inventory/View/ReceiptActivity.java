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
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
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
import com.alhikmahpro.www.e_inventory.Data.DashedSeparator;
import com.alhikmahpro.www.e_inventory.Data.DataContract;
import com.alhikmahpro.www.e_inventory.Data.PrinterCommands;
import com.alhikmahpro.www.e_inventory.Data.SessionHandler;
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

public class ReceiptActivity extends AppCompatActivity {

    @BindView(R.id.txt_date)
    TextView txtDate;
    @BindView(R.id.txt_receipt)
    TextView txtReceipt;
//    @BindView(R.id.textViewCustomerName)
//    TextView textViewCustomerName;
    @BindView(R.id.editTextBalance)
    TextView editTextBalance;
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

    @BindView(R.id.editTextRemark)
    EditText editTextRemark;
    @BindView(R.id.btnPrint)
    Button btnPrint;
    @BindView(R.id.btnNew)
    Button btnNew;
    //    @BindView(R.id.btnPdf)
//    Button btnPdf;
    String mDate, mDoc;

    String action, paymentType;
    String customerName, customerCode,salesmanId;
    double balanceAmount, receivedAmount;
    String serveNo="NA";
    RadioButton radioButton;
    Calendar myCalendar;
    @BindView(R.id.toolbar)
    Toolbar toolbar;

    volleyListener mVolleyListener;
    VolleyServiceGateway serviceGateway;
    AlertDialog alertDialog;
    AlertDialog.Builder builder;

    boolean billStatus;
    Context mContext;



    String companyName = "xxxxx", companyAddress = "xxxx", companyPhone = "xxxxx", footer = "xxxxxx", fileName;
    private static final String TAG = "ReceiptActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_receipt);
        ButterKnife.bind(this);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);


        editTextBalance.setEnabled(false);
        Intent intent = getIntent();
        action = intent.getStringExtra("ACTION");
        customerName = intent.getStringExtra("CUS_NAME");
        customerCode = intent.getStringExtra("CUS_CODE");
        balanceAmount = intent.getDoubleExtra("BALANCE_AMOUNT", 0);
        paymentType = intent.getStringExtra("PAYMENT_TYPE");
        salesmanId = intent.getStringExtra("SALESMAN");
        billStatus = false;
        mContext = getApplicationContext();
        Log.d(TAG, "onCreate: Action"+action);

        if (action.equals(DataContract.ACTION_EDIT)) {
            getSupportActionBar().setTitle(customerName);
        }else{
            getSupportActionBar().setTitle(customerName);
        }
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


        if (action != null && action.equals(DataContract.ACTION_NEW)) {
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
           // String recAmount = intent.getStringExtra("RECEIVED_AMOUNT");
            //double amount = ParseDouble(intent.getDoubleExtra("RECEIVED_AMOUNT",0));
            editTextAmount.setText(String.valueOf(intent.getDoubleExtra("RECEIVED_AMOUNT", 0)));
            editTextChequeDate.setText(intent.getStringExtra("CHEQUE_DATE"));
            editTextChequeNumber.setText(intent.getStringExtra("CHEQUE_NUMBER"));
            editTextRemark.setText(intent.getStringExtra("REMARK"));
        }

        //double balAmount = ParseDouble(balanceAmount);
        editTextBalance.setText(currencyFormatter(balanceAmount));
        //textViewCustomerName.setText(customerName);
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
                //saveToDatabase(DataContract.SYNC_STATUS_OK);
                sendToServer();
                break;
            case R.id.btnNew:
                clearActivity();
                break;
        }
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
            receiptObject.put(DataContract.Receipts.COL_REMARK, editTextRemark.getText().toString());
            receiptObject.put("receipt_type", paymentType);
            receiptArray.put(receiptObject);
            result.put("Receipt", receiptArray);


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
                //saveToDatabase(DataContract.SYNC_STATUS_FAILED);

            } else {
                // if internet available send to server
               // ShowProgressDialog();
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

                try {
                    serveNo= response.getString("Status");
                    if (!serveNo.equals("failed")) {
                        result = "Sync Successful";
                        saveToDatabase(DataContract.SYNC_STATUS_OK);
                    }else{
                        Toast.makeText(ReceiptActivity.this, result, Toast.LENGTH_SHORT).show();
                    }

                } catch (JSONException e) {
                    result = "Sync Error";
                    e.printStackTrace();
                }
//                finally {
//
//                    // save to local database
//                    Toast.makeText(ReceiptActivity.this, result, Toast.LENGTH_SHORT).show();
//                    saveToDatabase(syncStatus);
//
//
//                }
            }

            @Override
            public void notifyError(String requestType, VolleyError error) {
                Log.d(TAG, "notifyError: " + error);
                Toast.makeText(ReceiptActivity.this, "Connection Error !", Toast.LENGTH_SHORT).show();
                //saveToDatabase(DataContract.SYNC_STATUS_FAILED);

            }
        };

    }


    private void saveToDatabase(int sync) {
        Log.d(TAG, "saveToDatabase: " + sync);
        // save data to local data base
        receivedAmount = ParseDouble(editTextAmount.getText().toString());
        dbHelper helper = new dbHelper(this);
        if (action.equals(DataContract.ACTION_EDIT)) {
            if (helper.updateReceipt(txtReceipt.getText().toString(), mDate, salesmanId, customerCode, customerName, balanceAmount, receivedAmount,
                    paymentType, editTextChequeDate.getText().toString(), editTextChequeNumber.getText().toString(), editTextRemark.getText().toString(), sync,serveNo)) {
                Log.d(TAG, "saveToDatabase: updated");
                //HideProgressDialog();
                gotoPdfView();
            }

        } else {
            if (helper.saveReceipts(txtReceipt.getText().toString(), mDate, salesmanId, customerCode, customerName, balanceAmount, receivedAmount,
                    paymentType, editTextChequeDate.getText().toString(), editTextChequeNumber.getText().toString(), editTextRemark.getText().toString(), sync,serveNo)) {
                Log.d(TAG, "saveToDatabase: Saved");
                billStatus = true;
                //HideProgressDialog();
                gotoPdfView();
            } else {
                //HideProgressDialog();
                Toast.makeText(this, "Saving Failed", Toast.LENGTH_SHORT).show();
            }
        }

    }


    private void gotoPdfView() {
        Log.d(TAG, "gotoNext: re amount"+customerName);
        Intent view_pdf = new Intent(this, ReceiptPdfViewActivity.class);

        view_pdf.putExtra("CUSTOMER_NAME",customerName);
        view_pdf.putExtra("CUSTOMER_CODE",customerCode);
        view_pdf.putExtra("SALESMAN_ID",salesmanId);
        view_pdf.putExtra("RECEIPT_NO",txtReceipt.getText().toString());
        view_pdf.putExtra("RECEIPT_DATE",mDate);
      //  view_pdf.putExtra("BAL_AMOUNT",balanceAmount);
        view_pdf.putExtra("REC_AMOUNT",receivedAmount);
        view_pdf.putExtra("SERVER_INV",serveNo);
//        view_pdf.putExtra("CHQ_NUMBER",editTextChequeNumber.getText().toString());
//        view_pdf.putExtra("CHQ_DATE",editTextChequeDate.getText().toString());
//        view_pdf.putExtra("REMARK",editTextRemark.getText().toString());
        startActivity(view_pdf);

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
    private void clearActivity() {
        // finish all activity and go to home activity
        Intent intent = new Intent(getApplicationContext(), ListReceiptActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }






}
