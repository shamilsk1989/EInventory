package com.alhikmahpro.www.e_inventory.View;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.alhikmahpro.www.e_inventory.AppUtils;
import com.alhikmahpro.www.e_inventory.Data.DataContract;
import com.alhikmahpro.www.e_inventory.Data.RuntimeData;
import com.alhikmahpro.www.e_inventory.Data.SessionHandler;
import com.alhikmahpro.www.e_inventory.Data.dbHelper;
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
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class InventoryActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener, ListItemFragment.OnCompleteListener {

    @BindView(R.id.txtviewDate)
    TextView txtviewDate;
    @BindView(R.id.txtDate)
    TextView txtDate;
    @BindView(R.id.txtviewDocNo)
    TextView txtviewDocNo;
    @BindView(R.id.txtDocNo)
    TextView txtDocNo;
    @BindView(R.id.txtBarcode)
    EditText txtBarcode;
    @BindView(R.id.imgBarcode)
    ImageView imgBarcode;
    @BindView(R.id.imgSearch)
    ImageView imgSearch;
    @BindView(R.id.header_layout)
    LinearLayout headerLayout;
    @BindView(R.id.txtName)
    EditText txtName;
    @BindView(R.id.txtCode)
    EditText txtCode;
    @BindView(R.id.spinner)
    Spinner spinner;
    @BindView(R.id.txtQuantity)
    EditText txtQuantity;
    @BindView(R.id.txtCost)
    EditText txtCost;
    @BindView(R.id.txtSalePrice)
    EditText txtSalePrice;
    @BindView(R.id.btnAdd)
    Button btnAdd;
    @BindView(R.id.txtAddedBarcode)
    EditText txtAddedBarcode;
    @BindView(R.id.txtAddedPrice)
    EditText txtAddedPrice;
    @BindView(R.id.txtAddedQuantity)
    EditText txtAddedQuantity;
    @BindView(R.id.btnNext)
    Button btnNext;
    private static final String TAG = "InventoryActivity";
    @BindView(R.id.txtUser)
    EditText txtUser;
    @BindView(R.id.content_layout)
    LinearLayout contentLayout;
    @BindView(R.id.imgSubmit)
    ImageView imgSubmit;
    private String BASE_URL = "";
    ProgressDialog progressDialog;
    String companyCode, companyName, deviceId, branchCode, periodCode, locationCode;
    private ConnectivityManager connectivityManager;
    double price1 = 0, price2 = 0, price3 = 0, cost = 0, costPrice = 0, salePrice = 0;
    int unit1Qty, unit2Qty, unit3Qty, unitIndex, mDoc;
    String barCode, selectedUnit, unit1, unit2, unit3, packing1, packing2, packing3, mDate;
    dbHelper helper;
    private static final int CAMERA_PERMISSION_CODE = 101;
    boolean is_first;

    volleyListener mVolleyListener;
    VolleyServiceGateway serviceGateway;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inventory);
        ButterKnife.bind(this);
        helper = new dbHelper(this);
        connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        progressDialog = new ProgressDialog(this);

        getSupportActionBar().setTitle("Inventory");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        is_first = true;
        setDoc();
        initView();
        initVolleyCallBack();

        txtBarcode.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

//                if( -1 != txtBarcode.getText().toString().indexOf( "\n" ) ){
//                    .setText("Enter was pressed!");
//                }

                Log.d(TAG, "beforeTextChanged: ");
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                Log.d(TAG, "beforeTextChanged: ");
                if (txtBarcode.getText().toString().contains("\n") || (txtBarcode.getText().toString().contains("\r"))) {
                    Log.d(TAG, "onTextChanged:  enter key pressed");
                    //sendToServer();
                    getDataFromVolley();
                }

            }

            @Override
            public void afterTextChanged(Editable s) {
                Log.d(TAG, "afterTextChanged: ");
            }
        });


    }

    private void initVolleyCallBack() {
        mVolleyListener = new volleyListener() {
            @Override
            public void notifySuccess(String requestType, JSONObject response) {

                if (response.length() > 0) {
                    Log.d(TAG, "notifySuccess: " + response);

                    clearView();
                    txtQuantity.setFocusableInTouchMode(true);
                    txtQuantity.requestFocus();
                    setValues(response);
                } else {
                    showAlert("Not Found !");

                }

            }

            @Override
            public void notifyError(String requestType, VolleyError error) {
                showAlert("Network error !");
            }
        };

    }


    @Override
    protected void onResume() {
        Log.d(TAG, "onResume: ");
        super.onResume();
        //setDoc();
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.d(TAG, "onPause: ");
        //setDoc();
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

        FragmentManager fragmentManager = getSupportFragmentManager();
        ListItemFragment listCustomerFragment = new ListItemFragment();
        listCustomerFragment.show(fragmentManager, "Items");

        //sendToServer();
    }

    @OnClick(R.id.btnAdd)
    public void onBtnAddClicked() {


        //String code,String p_code, String qty,String cost,String sale
        if (validate(txtCode.getText().toString(), txtQuantity.getText().toString(), txtCost.getText().toString(), txtSalePrice.getText().toString(), txtUser.getText().toString())) {

            double qty = 0;
            qty = Double.valueOf(txtQuantity.getText().toString());
            if (qty < 1) {
                Toast.makeText(this, "Invalid Quantity", Toast.LENGTH_SHORT).show();
            } else {
//                ArrayList<ItemModel>listItem=new ArrayList<>();
//                ItemModel itemModel = new ItemModel();
//                itemModel.setBarCode(barCode);
//                itemModel.setProductCode(txtCode.getText().toString());
//                itemModel.setProductName(txtName.getText().toString());
//                itemModel.setQty(Double.valueOf(txtQuantity.getText().toString()));
//                itemModel.setUnit1(unit1);
//                itemModel.setUnit2(unit2);
//                itemModel.setUnit3(unit3);
//                itemModel.setUnit1Qty(unit1Qty);
//                itemModel.setUnit2Qty(unit2Qty);
//                itemModel.setUnit3Qty(unit3Qty);
//                itemModel.setUnitIndex(unitIndex);
//                itemModel.setCostPrice(cost);
//                itemModel.setSalePrice(Double.valueOf(txtSalePrice.getText().toString()));
//                itemModel.setSelectedPackage(selectedUnit);
//                listItem.add(itemModel);
                Log.d(TAG, "onBtnAddClicked: ");

                if (is_first) {//first time..... so insert to Stock table;
                    double total = 0;//set dummy value
                    Log.d(TAG, "is first: " + txtUser.getText().toString());
                    boolean stock_res = helper.saveStocks(mDoc, total, txtUser.getText().toString(), mDate);
                    if (stock_res) {
                        is_first = false;
                    }

                }
                boolean res = helper.saveStocksDetails(mDoc, barCode, txtCode.getText().toString(),
                        txtName.getText().toString(), selectedUnit, qty, salePrice, costPrice);

                txtAddedBarcode.setText(barCode);
                txtAddedQuantity.setText(txtQuantity.getText().toString());
                txtAddedPrice.setText(txtSalePrice.getText().toString());
                txtBarcode.setText("");
                clearView();
                setSpinner(false);
                txtBarcode.setFocusableInTouchMode(true);
                txtBarcode.requestFocus();

            }

        }
        Log.d(TAG, "onBtnAddClicked: Cart size" + RuntimeData.mCartData.size());
    }

    @OnClick(R.id.btnNext)
    public void onBtnNextClicked() {

        txtAddedBarcode.setText("");
        txtAddedPrice.setText("");
        txtAddedQuantity.setText("");

        Intent intent = new Intent(this, ListItemActivity.class);
        intent.putExtra("Action", "New");
        intent.putExtra("DocNo", Integer.valueOf(txtDocNo.getText().toString()));
        intent.putExtra("User", txtUser.getText().toString());
        startActivity(intent);


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

            txtBarcode.setText(result.getContents());
        } else {
            Toast.makeText(this, "error", Toast.LENGTH_SHORT).show();
        }

    }


    private boolean validate(String p_code, String qty, String cost, String sale, String user) {


        if (TextUtils.isEmpty(user)) {
            txtUser.setError("Empty");
            return false;
        } else if (TextUtils.isEmpty(p_code)) {
            txtCode.setError("Invalid barcode or product code");
            return false;
        } else if (TextUtils.isEmpty(qty)) {
            txtQuantity.setError("Invalid Quantity");
            return false;
        } else if (TextUtils.isEmpty(cost)) {
            txtCost.setError("Invalid Cost Price");
            return false;
        } else if (TextUtils.isEmpty(sale)) {
            txtBarcode.setError("Invalid Sale Price");
            return false;
        }
        return true;

    }

    private void clearView() {
        txtName.setText("");
        txtCode.setText("");
        txtQuantity.setText("");
        txtCost.setText("");
        txtSalePrice.setText("");
        setSpinner(false);


    }


    void getDataFromVolley() {
        View view = this.getCurrentFocus();
        AppUtils.hideKeyboard(this, view);
        String code = txtBarcode.getText().toString();
        code = code.replace("\n", "").replace("\r", "");

        if (TextUtils.isEmpty(code)) {
            txtAddedBarcode.setError("Invalid BarCode");
        } else {
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
        Log.d(TAG, "setValues called: ");


        try {
            String productCode = response.getString("ProductCode");
            String productName = response.getString("ProductName");


            barCode = response.getString("BarCode");
            price1 = response.getDouble("SalesPrice1");
            price2 = response.getDouble("SalesPrice2");
            price3 = response.getDouble("SalesPrice3");

            unit1 = response.getString("Unit1");
            unit2 = response.getString("Unit2");
            unit3 = response.getString("Unit3");
            unitIndex = response.getInt("UnitIndex");

            unit1Qty = response.getInt("Unit1Qty");
            unit2Qty = response.getInt("Unit2Qty");
            unit3Qty = response.getInt("Unit3Qty");
            cost = response.getDouble("LandingCost");

            packing1 = response.getString("Packing1");
            packing2 = response.getString("Packing2");
            packing3 = response.getString("Packing3");

            txtCode.setText(productCode);
            txtName.setText(productName);
            txtBarcode.setText(barCode);
            setSpinner(true);


        } catch (Exception e) {
            e.printStackTrace();
            Log.d(TAG, " Exception: " + e.getMessage());
        }
    }

    private void setSpinner(boolean is_set) {
        Log.d(TAG, "unit index: " + unitIndex);

        ArrayList<String> list = new ArrayList<>();
        if (is_set) {

            switch (unitIndex) {
                case 0:
                    list.add(packing1);
                    salePrice = price1;
                    costPrice = cost;
                    break;
                case 1:
                    list.add(packing2);
                    salePrice = price2;
                    costPrice = cost * unit2Qty;
                    break;
                case 2:
                    list.add(packing3);
                    salePrice = price3;
                    costPrice = cost * unit2Qty * unit3Qty;
                    break;
                default:
                    list.add(" ");
                    break;
            }
            DecimalFormat format = new DecimalFormat("#,###,##0.00");
            txtSalePrice.setText(format.format(salePrice));
            txtCost.setText(format.format(costPrice));
        } else {
            list.clear();

        }


        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, list);
        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        arrayAdapter.notifyDataSetChanged();
        spinner.setAdapter(arrayAdapter);
        spinner.setOnItemSelectedListener(this);


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
        if (ActivityCompat.shouldShowRequestPermissionRationale(InventoryActivity.this, Manifest.permission.CAMERA)) {

            new AlertDialog.Builder(InventoryActivity.this)
                    .setTitle("Permission needed")
                    .setMessage("To continue please allow the permission ")
                    .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            ActivityCompat.requestPermissions(InventoryActivity.this, new String[]{Manifest.permission.CAMERA}, CAMERA_PERMISSION_CODE);

                        }
                    })
                    .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override

                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                        }
                    }).create().show();


        } else {
            ActivityCompat.requestPermissions(InventoryActivity.this, new String[]{Manifest.permission.CAMERA}, CAMERA_PERMISSION_CODE);
        }
    }


    private void setDoc() {

        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
        mDate = sdf.format(new Date());
        int last_no = helper.getLastDocNo();
        Log.d(TAG, "setDoc: " + last_no);
        mDoc = last_no + 1;
        txtDate.setText(mDate);
        txtDocNo.setText(String.valueOf(mDoc));
    }

    private void initView() {
        txtAddedPrice.setEnabled(false);
        txtAddedBarcode.setEnabled(false);
        txtAddedQuantity.setEnabled(false);
        txtCode.setEnabled(false);
        txtCost.setEnabled(false);
        txtSalePrice.setEnabled(false);

        SQLiteDatabase sqLiteDatabase = helper.getReadableDatabase();
        Cursor cursor = helper.getSettings(sqLiteDatabase);
        if (cursor.moveToFirst()) {
            companyCode = cursor.getString(cursor.getColumnIndex(DataContract.Settings.COL_COMPANY_CODE));
            companyName = cursor.getString(cursor.getColumnIndex(DataContract.Settings.COL_COMPANY_NAME));
            deviceId = cursor.getString(cursor.getColumnIndex(DataContract.Settings.COL_DEVICE_ID));
            branchCode = cursor.getString(cursor.getColumnIndex(DataContract.Settings.COL_BRANCH_CODE));
            periodCode = cursor.getString(cursor.getColumnIndex(DataContract.Settings.COL_PERIOD_CODE));
            locationCode = cursor.getString(cursor.getColumnIndex(DataContract.Settings.COL_LOCATION_CODE));
            BASE_URL = SessionHandler.getInstance(InventoryActivity.this).getHost();
        }
        cursor.close();
        sqLiteDatabase.close();
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {


        selectedUnit = parent.getItemAtPosition(position).toString();


    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {


    }

    @Override
    public void onBackPressed() {
        //super.onBackPressed();
        new AlertDialog.Builder(InventoryActivity.this)
                .setTitle(" Warning ")
                .setMessage("Do you want to exit !")
                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        RuntimeData.mCartData.clear();
                        finish();
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override

                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                }).create().show();

    }

    @OnClick(R.id.imgSubmit)
    public void onViewClicked() {

        getDataFromVolley();
    }

    @Override
    public void onComplete(String code) {
        txtBarcode.setText(code);
    }
}
