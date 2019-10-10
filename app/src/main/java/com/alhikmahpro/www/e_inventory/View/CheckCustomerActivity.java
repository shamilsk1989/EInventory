package com.alhikmahpro.www.e_inventory.View;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.alhikmahpro.www.e_inventory.AppUtils;
import com.alhikmahpro.www.e_inventory.Interface.volleyListener;
import com.alhikmahpro.www.e_inventory.Network.VolleyServiceGateway;
import com.alhikmahpro.www.e_inventory.R;
import com.android.volley.VolleyError;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import com.journeyapps.barcodescanner.CaptureActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.text.NumberFormat;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class CheckCustomerActivity extends AppCompatActivity implements ListCustomerFragment.OnCompleteListener {

    @BindView(R.id.editTextBarcode)
    EditText editTextBarcode;
    @BindView(R.id.imgBarcode)
    ImageView imgBarcode;
    @BindView(R.id.imgSearch)
    ImageView imgSearch;
    @BindView(R.id.imgSubmit)
    ImageView imgSubmit;
    @BindView(R.id.textViewCustomerName)
    TextView textViewCustomerName;
    @BindView(R.id.editTextBalance)
    EditText editTextBalance;
    @BindView(R.id.editTextLastInvoice)
    EditText editTextLastInvoice;
    @BindView(R.id.editTextLastReceipt)
    EditText editTextLastReceipt;
//    @BindView(R.id.btnReceipt)
//    Button btnReceipts;
    @BindView(R.id.btnInvoice)
    Button btnInvoice;



    private static final int CAMERA_PERMISSION_CODE = 100;
    private static final String TAG = "CheckCustomerActivity";
    private ConnectivityManager connectivityManager;
    String companyCode, companyName, deviceId, locationCode, branchCode, periodCode;
    String customerCode, customerName, lastInvoiceNo, lastReceiptNo;
    double balanceAmount;
    String type;

    String BASE_URL = "";

    volleyListener mVolleyListener;
    VolleyServiceGateway serviceGateway;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_check_cutomer);
        ButterKnife.bind(this);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle("Check Customer");
        Intent intent=getIntent();
        type=intent.getStringExtra("Type");

        //disable edit text

        editTextLastReceipt.setEnabled(false);
        editTextLastInvoice.setEnabled(false);
        editTextBalance.setEnabled(false);

        editTextBarcode.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                if (editTextBarcode.getText().toString().contains("\n") || (editTextBarcode.getText().toString().contains("\r"))) {
                    Log.d(TAG, "onTextChanged:  enter key pressed");
                    getDataFromVolley();

                }

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        initVolleyCallBack();

    }

    private void initVolleyCallBack() {

        mVolleyListener = new volleyListener() {
            @Override
            public void notifySuccess(String requestType, JSONObject response) {
                if (response.length() > 0) {
                    try {
                        customerCode = response.getString("CustomerCode");
                        customerName = response.getString("CustomerName");
                        lastInvoiceNo = response.getString("LastInvoice");
                        lastReceiptNo = response.getString("LastReceipt");
                        balanceAmount =ParseDouble(response.getString("CurrentBalance"));

                        textViewCustomerName.setText(customerName);
                        editTextLastInvoice.setText(lastInvoiceNo);
                        editTextLastReceipt.setText(lastReceiptNo);
                        editTextBalance.setText(currencyFormatter(balanceAmount));
                        Log.d(TAG, "notifySuccess: "+currencyFormatter(balanceAmount));

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else {
                    showAlert("Not Found");

                }
            }

            @Override
            public void notifyError(String requestType, VolleyError error) {
                showAlert("Network error");

            }
        };
    }


    void getDataFromVolley() {
        String code = editTextBarcode.getText().toString();
        Log.d(TAG, "old code: " + code + "f");
        code = code.replace("\n", "").replace("\r", "");
        if (validate(code)) {
            Log.d(TAG, "getDataFromVolley: ");
            View view = this.getCurrentFocus();
            AppUtils.hideKeyboard(this, view);
            JSONObject postParam = new JSONObject();
            try {
                postParam.put("customerCode", code);

            } catch (JSONException e) {
                e.printStackTrace();
            }
            serviceGateway = new VolleyServiceGateway(mVolleyListener, this);
            serviceGateway.postDataVolley("POSTCALL", "PriceChecker/check_cust.php", postParam);

        }

    }

    private void showAlert(String Message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Error Message");
        builder.setMessage(Message);
        builder.setCancelable(false);
        builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                dialog.cancel();

            }
        }).create().show();
    }

    private void clearView() {
        textViewCustomerName.setText("");
        editTextBarcode.setText("");
        editTextBalance.setText("");
        editTextLastInvoice.setText("");
        editTextLastReceipt.setText("");


    }

    private boolean validate(String code) {
        if (TextUtils.isEmpty(code)) {
            editTextBarcode.setError("Invalid barcode or product code");
            return false;

        }
        return true;
    }

    @OnClick(R.id.imgBarcode)
    public void onImgBarcodeClicked() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
                Log.d(TAG, "Permission already granted: ");
                scanBarcode();
            } else {
                requestStoragePermission();
            }
        } else {
            scanBarcode();
        }
    }

    private void requestStoragePermission() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(CheckCustomerActivity.this, Manifest.permission.CAMERA)) {

            new AlertDialog.Builder(CheckCustomerActivity.this)
                    .setTitle("Permission needed")
                    .setMessage("To continue please allow the permission ")
                    .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            ActivityCompat.requestPermissions(CheckCustomerActivity.this, new String[]{Manifest.permission.CAMERA}, CAMERA_PERMISSION_CODE);

                        }
                    })
                    .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override

                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                        }
                    }).create().show();


        } else {
            ActivityCompat.requestPermissions(CheckCustomerActivity.this, new String[]{Manifest.permission.CAMERA}, CAMERA_PERMISSION_CODE);
        }
    }

    private void scanBarcode() {
        Log.d(TAG, "onScannerPressed: ");
        IntentIntegrator intentIntegrator = new IntentIntegrator(this);
        intentIntegrator.setCameraId(0);
        intentIntegrator.setPrompt("Scan code");
        intentIntegrator.setBeepEnabled(true);
        intentIntegrator.setOrientationLocked(false);
        intentIntegrator.setCaptureActivity(CaptureActivity.class);
        intentIntegrator.initiateScan();
    }


    @OnClick(R.id.imgSearch)
    public void onImgSearchClicked() {
        FragmentManager fragmentManager = getSupportFragmentManager();
        ListCustomerFragment listCustomerFragment = new ListCustomerFragment();
        listCustomerFragment.show(fragmentManager, "Customer");
    }

    @OnClick(R.id.imgSubmit)
    public void onImgSubmitClicked() {

        getDataFromVolley();


    }

    @OnClick(R.id.btnInvoice)
    public void onBtnInvoiceClicked() {
        if(type!=null && type.equals("REC")){
            loadReceiptActivity();
        }
        else if(type!=null & type.equals("SAL")){
            loadSalesActivity();
        }

    }
//    @OnClick(R.id.btnReceipt)
//    public void onBtnReceiptClicked() {
//        if (TextUtils.isEmpty(textViewCustomerName.getText().toString())) {
//            textViewCustomerName.setError("Invalid customer");
//        } else {
//            clearView();
//            Intent intent_rec = new Intent(CheckCustomerActivity.this, ReceiptActivity.class);
//            intent_rec.putExtra("TYPE", "NEW");
//            intent_rec.putExtra("PAYMENT_TYPE", "Cash");
//            intent_rec.putExtra("CUS_NAME", customerName);
//            intent_rec.putExtra("CUS_CODE", customerCode);
//            intent_rec.putExtra("BALANCE_AMOUNT", balanceAmount);
//            startActivity(intent_rec);
//        }
//
//
//    }

    private void loadSalesActivity(){
        if (TextUtils.isEmpty(textViewCustomerName.getText().toString())) {
            textViewCustomerName.setError("Invalid customer");
        } else {
            clearView();
            Intent intent_sale = new Intent(CheckCustomerActivity.this, SalesActivity.class);
            intent_sale.putExtra("Customer", customerName);
            intent_sale.putExtra("CustomerCode", customerCode);
            startActivity(intent_sale);
        }

    }
    private void loadReceiptActivity(){

        if (TextUtils.isEmpty(textViewCustomerName.getText().toString())) {
            textViewCustomerName.setError("Invalid customer");
        } else {
            clearView();
            Intent intent_rec = new Intent(CheckCustomerActivity.this, ReceiptActivity.class);
            intent_rec.putExtra("TYPE", "NEW");
            intent_rec.putExtra("PAYMENT_TYPE", "Cash");
            intent_rec.putExtra("CUS_NAME", customerName);
            intent_rec.putExtra("CUS_CODE", customerCode);
            //intent_rec.putExtra("SALESMAN", );
            intent_rec.putExtra("BALANCE_AMOUNT", balanceAmount);
            startActivity(intent_rec);
        }

    }



    @Override
    public boolean onSupportNavigateUp() {

        onBackPressed();
        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        if (requestCode == CAMERA_PERMISSION_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Log.d(TAG, "onRequestPermissionsResult: permission granted ");
                scanBarcode();
            } else {
                Log.d(TAG, "onRequestPermissionsResult: permission not granted");
            }
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {

        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if (result != null) {

            editTextBarcode.setText(result.getContents());
        } else {
            Toast.makeText(this, "error", Toast.LENGTH_SHORT).show();
        }

    }


    @Override
    public void onComplete(String code) {
        editTextBarcode.setText(code);
    }

    @Override
    protected void onResume() {
        super.onResume();

    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
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
}
