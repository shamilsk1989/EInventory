package com.alhikmahpro.www.e_inventory.View;

import android.Manifest;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.alhikmahpro.www.e_inventory.AppUtils;
import com.alhikmahpro.www.e_inventory.Data.CartModel;
import com.alhikmahpro.www.e_inventory.Data.CustomerModel;
import com.alhikmahpro.www.e_inventory.Data.DataContract;
import com.alhikmahpro.www.e_inventory.Interface.volleyListener;
import com.alhikmahpro.www.e_inventory.Network.VolleyServiceGateway;
import com.alhikmahpro.www.e_inventory.R;
import com.android.volley.VolleyError;
import com.google.android.gms.vision.barcode.Barcode;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;
import com.notbytes.barcode_reader.BarcodeReaderActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class CustomerStatementActivity extends AppCompatActivity implements ListCustomerDetailsFragment.OnCompleteListener {

    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.editTextBarcode)
    EditText editTextBarcode;

    @BindView(R.id.textViewCustomerName)
    TextView textViewCustomerName;
    @BindView(R.id.editTextBalance)
    EditText editTextBalance;
    @BindView(R.id.editTextLastInvoice)
    EditText editTextLastInvoice;
    @BindView(R.id.editTextLastReceipt)
    EditText editTextLastReceipt;


    private static final int BARCODE_READER_ACTIVITY_REQUEST = 100;
    private static final String TAG = "CusStatementActivity";
    private static final String PREF_KEY_DEVICE = "key_employee";
    @BindView(R.id.imgBarcode)
    ImageView imgBarcode;
    @BindView(R.id.imgSearch)
    ImageView imgSearch;
    @BindView(R.id.imgSubmit)
    ImageView imgSubmit;
    @BindView(R.id.btnView)
    Button btnView;

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
        setContentView(R.layout.activity_customer_statement);
        ButterKnife.bind(this);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle("Customer Statement");
        Intent intent = getIntent();
        type = intent.getStringExtra("TYPE");
        Log.d(TAG, "onCreate:type " + type);
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        deviceId = sharedPreferences.getString(PREF_KEY_DEVICE, "0");

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

                        JSONArray cusArray=response.getJSONArray("customer");
                        JSONArray stmtArray=response.getJSONArray("statement");
                        JSONObject cusObj=cusArray.getJSONObject(0);
                        customerName=cusObj.getString("Name");
                        customerCode=cusObj.getString("Code");


                        for(int i=0;i<stmtArray.length();i++){
                            JSONObject st=stmtArray.getJSONObject(i);
                            CustomerModel customerModel=new CustomerModel();
                            customerModel.setInvoiceDate(st.getString("Date"));
                            customerModel.setAmount(st.getDouble("Paid"));
                            customerModel.setDays(st.getInt("Days"));
                            customerModel.setBalance(st.getDouble("Bal"));
                            customerModel.setInvoiceNo(st.getString("Inv"));

                        }
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


    void  getDataFromVolley() {
        String code = editTextBarcode.getText().toString();
        Log.d(TAG, "old code: " + code + "f");
        code = code.replace("\n", "").replace("\r", "");
       // if (validate(code)) {
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

        //}

    }

    public String currencyFormatter(double val) {
        NumberFormat format = NumberFormat.getCurrencyInstance();
        String pattern = ((DecimalFormat) format).toPattern();
        String newPattern = pattern.replace("\u00A4", "").trim();
        NumberFormat newFormat = new DecimalFormat(newPattern);
        return String.valueOf(newFormat.format(val));

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

    double ParseDouble(String strNumber) {
        if (strNumber != null && strNumber.length() > 0) {
            try {
                return Double.parseDouble(strNumber);
            } catch (Exception e) {
                return -1;   // or some value to mark this field is wrong. or make a function validates field first ...
            }
        } else return 0;
    }

    private boolean validate(String code) {
        if (TextUtils.isEmpty(code)) {
            editTextBarcode.setError("Invalid barcode or product code");
            return false;

        }
        return true;
    }

    private void requestCameraPermission() {
        Dexter.withActivity(this)
                .withPermission(Manifest.permission.CAMERA)
                .withListener(new PermissionListener() {
                    @Override
                    public void onPermissionGranted(PermissionGrantedResponse response) {
                        //permission granted
                        scanBarcode();

                    }

                    @Override
                    public void onPermissionDenied(PermissionDeniedResponse response) {

                        showSettingDialog();

                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(PermissionRequest permission, PermissionToken token) {

                        token.continuePermissionRequest();
                    }
                }).check();

    }



    private void scanBarcode() {
        Log.d(TAG, "onScannerPressed: ");
//        IntentIntegrator intentIntegrator = new IntentIntegrator(this);
//        intentIntegrator.setCameraId(0);
//        intentIntegrator.setPrompt("Scan code");
//        intentIntegrator.setBeepEnabled(true);
//        intentIntegrator.setOrientationLocked(false);
//        intentIntegrator.setCaptureActivity(CaptureActivity.class);
//        intentIntegrator.initiateScan();
        Intent launchIntent = BarcodeReaderActivity.getLaunchIntent(this, true, false);
        startActivityForResult(launchIntent,BARCODE_READER_ACTIVITY_REQUEST);
    }

    private void showSettingDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(CustomerStatementActivity.this);
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

    @OnClick(R.id.imgBarcode)
    public void onImgBarcodeClicked() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//            if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
//                Log.d(TAG, "Permission already granted: ");
//                scanBarcode();
//            } else {
//                requestStoragePermission();
//            }
            requestCameraPermission();
        } else {
            scanBarcode();
        }
    }

    @OnClick(R.id.imgSearch)
    public void onImgSearchClicked() {
        String item_key = editTextBarcode.getText().toString();
        Bundle bundle = new Bundle();
        bundle.putString("CUS_NAME", item_key);
        FragmentManager fragmentManager = getSupportFragmentManager();
        ListCustomerDetailsFragment listFragment = new ListCustomerDetailsFragment();
        listFragment.setArguments(bundle);
        listFragment.show(fragmentManager, "Customer");
    }

    @OnClick(R.id.imgSubmit)
    public void onImgSubmitClicked() {
        getDataFromVolley();
    }

    @OnClick(R.id.btnView)
    public void onBtnViewClicked() {

        Intent intent_print = new Intent(CustomerStatementActivity.this, StatementPdfViewActivity.class);
        intent_print.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        intent_print.putExtra("CUS_NAME", customerName);
        intent_print.putExtra("CUS_CODE", customerCode);
        intent_print.putExtra("SALESMAN_ID", "none");
        startActivity(intent_print);
    }



    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {

//        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
//        if (result != null) {
//
//            editTextBarcode.setText(result.getContents());
//        } else {
//            Toast.makeText(this, "error", Toast.LENGTH_SHORT).show();
//        }
        if (resultCode != Activity.RESULT_OK) {
            Toast.makeText(this, "error in  scanning", Toast.LENGTH_SHORT).show();
            return;
        }

        if (requestCode == BARCODE_READER_ACTIVITY_REQUEST && data != null) {
            Barcode barcode = data.getParcelableExtra(BarcodeReaderActivity.KEY_CAPTURED_BARCODE);
            editTextBarcode.setText(barcode.rawValue);
        }

    }



    @Override
    public void onComplete(String code, String name) {
        editTextBarcode.setText(code);
        customerCode=code;
        customerName=name;

    }
}
