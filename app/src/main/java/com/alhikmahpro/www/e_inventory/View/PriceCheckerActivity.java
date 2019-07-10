package com.alhikmahpro.www.e_inventory.View;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
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

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class PriceCheckerActivity extends AppCompatActivity {


    @BindView(R.id.txtBarcode)
    EditText txtBarcode;
    @BindView(R.id.imgBarcode)
    ImageView imgBarcode;
    @BindView(R.id.imgSearch)
    ImageView imgSearch;
    @BindView(R.id.txtName)
    EditText txtName;
    @BindView(R.id.txtCode)
    EditText txtCode;
    @BindView(R.id.txtPrice1)
    EditText txtPrice1;
    @BindView(R.id.txtPrice2)
    EditText txtPrice2;
    @BindView(R.id.txtPrice3)
    EditText txtPrice3;
    @BindView(R.id.txtStock)
    EditText txtStock;

    private static final String TAG = "PriceCheckerActivity";
    private static final int CAMERA_PERMISSION_CODE = 100;

    String companyCode, companyName, deviceId, branchCode, locationCode, periodCode;

    @BindView(R.id.txtViewPrice1)
    TextView txtViewPrice1;
    @BindView(R.id.txtViewPrice2)
    TextView txtViewPrice2;
    @BindView(R.id.txtViewPrice3)
    TextView txtViewPrice3;
    @BindView(R.id.btnClear)
    Button btnClear;
    private String BASE_URL = "";
    private ConnectivityManager connectivityManager;
    ProgressDialog progressDialog;

    volleyListener mVolleyListener;
    VolleyServiceGateway serviceGateway;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_price_checker);
        ButterKnife.bind(this);
        connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        //progressDialog = new ProgressDialog(this);
        getSupportActionBar().setTitle("Price Checker");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        initValues();
        initVolleyCallBack();


        txtBarcode.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                if (txtBarcode.getText().toString().contains("\n") || (txtBarcode.getText().toString().contains("\r"))) {
                    Log.d(TAG, "onTextChanged:  enter key pressed");
                    getDataFromVolley();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    private void initVolleyCallBack() {
        mVolleyListener = new volleyListener() {
            @Override
            public void notifySuccess(String requestType, JSONObject response) {
                progressDialog.cancel();
                Log.d(TAG, "Response: " + response);
                if (response.length() > 0) {
                    clearView();
                    setValues(response);
                } else {
                    clearView();
                    showAlert("Not Found..");
                    String val = txtBarcode.getText().toString();
                    val = val.replace("\n", "").replace("\r", "");
                    txtBarcode.setText(val);
                }

            }

            @Override
            public void notifyError(String requestType, VolleyError error) {
                progressDialog.cancel();
                showAlert("Network Error !");

            }
        };
    }

    private void initValues() {

        txtCode.setEnabled(false);
        txtName.setEnabled(false);
        txtPrice1.setEnabled(false);
        txtPrice2.setEnabled(false);
        txtPrice3.setEnabled(false);
        txtStock.setEnabled(false);
        txtPrice2.setVisibility(View.GONE);
        txtPrice3.setVisibility(View.GONE);

        txtViewPrice2.setVisibility(View.GONE);
        txtViewPrice3.setVisibility(View.GONE);

        txtBarcode.setFocusableInTouchMode(true);
        txtBarcode.requestFocus();
    }


    private void clearView() {


        txtCode.setText("");
        txtName.setText("");
        txtPrice1.setText("");
        txtPrice2.setText("");
        txtPrice3.setText("");
        txtStock.setText("");

    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
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

    @OnClick(R.id.imgSearch)
    public void onImgSearchClicked() {
        getDataFromVolley();
    }

    private boolean validate(String code) {

        if (TextUtils.isEmpty(code)) {
            txtBarcode.setError("Invalid barcode or product code");
            return false;
        }
        return true;

    }

    void getDataFromVolley() {
        View view = this.getCurrentFocus();
        AppUtils.hideKeyboard(this, view);
        String code = txtBarcode.getText().toString();
        code = code.replace("\n", "").replace("\r", "");

        if (validate(code)) {
            JSONObject postParam = new JSONObject();
            try {
                postParam.put("Code", code);
            } catch (JSONException e) {
                e.printStackTrace();
            }

            progressDialog = AppUtils.showProgressDialog(this, "Loading....");
            serviceGateway = new VolleyServiceGateway(mVolleyListener, this);
            serviceGateway.postDataVolley("POSTCALL", "PriceChecker/check_price.php", postParam);

        }
    }

    private void setValues(JSONObject response) {

        try {
            String val = txtBarcode.getText().toString();
            val = val.replace("\n", "").replace("\r", "");
            txtBarcode.setText(val);

            txtCode.setText(response.getString("ProductCode"));
            txtName.setText(response.getString("ProductName"));
            txtStock.setText("Stock -> " + response.getString("Stock"));
            double price1 = response.getDouble("SalesPrice1");
            double price2 = response.getDouble("SalesPrice2");
            double price3 = response.getDouble("SalesPrice3");
            String packing1 = response.getString("Packing1");
            String packing2 = response.getString("Packing2");
            String packing3 = response.getString("Packing3");
            int unitQty2 = response.getInt("Unit2Qty");
            int unitQty3 = response.getInt("Unit3Qty");


            DecimalFormat format = new DecimalFormat("#,###,##0.00");

            if (unitQty2 > 0) {
                txtViewPrice2.setVisibility(View.VISIBLE);
                txtPrice2.setVisibility(View.VISIBLE);
            } else {
                txtViewPrice2.setVisibility(View.GONE);
                txtPrice2.setVisibility(View.GONE);
            }

            if (unitQty3 > 0) {
                txtViewPrice3.setVisibility(View.VISIBLE);
                txtPrice3.setVisibility(View.VISIBLE);
            } else {
                txtViewPrice3.setVisibility(View.GONE);
                txtPrice3.setVisibility(View.GONE);
            }
            txtViewPrice1.setText("Price - " + packing1);
            txtViewPrice2.setText("Price - " + packing2);
            txtViewPrice3.setText("Price - " + packing3);


            txtPrice1.setText(format.format(price1));
            txtPrice2.setText(format.format(price2));
            txtPrice3.setText(format.format(price3));

        } catch (Exception e) {
            e.printStackTrace();
        }


    }


    private void showAlert(String Message) {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Response");
        builder.setMessage(Message);
        builder.setCancelable(false);
        builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                dialog.cancel();

            }
        }).create().show();
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

    private void requestStoragePermission() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(PriceCheckerActivity.this, Manifest.permission.CAMERA)) {

            new AlertDialog.Builder(PriceCheckerActivity.this)
                    .setTitle("Permission needed")
                    .setMessage("To continue please allow the permission ")
                    .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            ActivityCompat.requestPermissions(PriceCheckerActivity.this, new String[]{Manifest.permission.CAMERA}, CAMERA_PERMISSION_CODE);

                        }
                    })
                    .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override

                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                        }
                    }).create().show();


        } else {
            ActivityCompat.requestPermissions(PriceCheckerActivity.this, new String[]{Manifest.permission.CAMERA}, CAMERA_PERMISSION_CODE);
        }
    }


    private void closeKey() {
        View view = this.getCurrentFocus();
        if (view != null) {
            InputMethodManager methodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            methodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
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

            txtBarcode.setText(result.getContents());
        } else {
            Toast.makeText(this, "error", Toast.LENGTH_SHORT).show();
        }

    }


    @OnClick(R.id.btnClear)
    public void onViewClicked() {
        clearView();
        txtBarcode.setText("");
        txtBarcode.setFocusableInTouchMode(true);
        txtBarcode.requestFocus();


    }
}
