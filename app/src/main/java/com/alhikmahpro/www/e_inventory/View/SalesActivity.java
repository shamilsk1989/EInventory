package com.alhikmahpro.www.e_inventory.View;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
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
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.alhikmahpro.www.e_inventory.AppUtils;
import com.alhikmahpro.www.e_inventory.Data.Cart;
import com.alhikmahpro.www.e_inventory.Data.CartModel;
import com.alhikmahpro.www.e_inventory.Data.DataContract;
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

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class SalesActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener, ListItemFragment.OnCompleteListener {

    @BindView(R.id.textViewInvoice)
    TextView textViewInvoice;
    @BindView(R.id.textViewDate)
    TextView textViewDate;
    @BindView(R.id.textViewCustomer)
    TextView textViewCustomer;
    @BindView(R.id.textViewSalesman)
    TextView textViewSalesman;
    @BindView(R.id.editTextBarcode)
    EditText editTextBarcode;
    @BindView(R.id.imgBarcode)
    ImageView imgBarcode;
    @BindView(R.id.imgSearch)
    ImageView imgSearch;
    @BindView(R.id.imgSubmit)
    ImageView imgSubmit;
    @BindView(R.id.textViewProductName)
    TextView textViewProductName;
    @BindView(R.id.editTextProductCode)
    EditText editTextProductCode;
    @BindView(R.id.spinner)
    Spinner spinner;
    @BindView(R.id.editTextQuantity)
    EditText editTextQuantity;
    @BindView(R.id.editTextRate)
    EditText editTextRate;
    @BindView(R.id.editTextDiscount)
    EditText editTextDiscount;
    //    @BindView(R.id.editTextCost)
//    EditText editTextCost;
//    @BindView(R.id.editTextSale)
//    EditText editTextSale;
    @BindView(R.id.editTextStock)
    EditText editTextStock;
    @BindView(R.id.editTextNet)
    EditText editTextNet;
    @BindView(R.id.radioGroup)
    RadioGroup radioGroup;
    @BindView(R.id.btnNext)
    Button btnNext;
    @BindView(R.id.btnAdd)
    Button btnAdd;
    @BindView(R.id.txtAddedBarcode)
    EditText txtAddedBarcode;
    @BindView(R.id.txtAddedPrice)
    EditText txtAddedPrice;
    @BindView(R.id.txtAddedQuantity)
    EditText txtAddedQuantity;

    RadioButton radioButton;
    private static final String TAG = "SalesActivity";
    private static final int CAMERA_PERMISSION_CODE = 101;
    @BindView(R.id.progressBar)
    ProgressBar progressBar;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.title_layout2)
    LinearLayout titleLayout2;
    @BindView(R.id.radioReturn)
    RadioButton radioReturn;
    @BindView(R.id.radioFree)
    RadioButton radioFree;
    private String customerName, salesmanId, customerCode;
    volleyListener mVolleyListener;
    VolleyServiceGateway serviceGateway;

    double price1 = 0, price2 = 0, price3 = 0, cost = 0, costPrice = 0, salePrice = 0, quantity, discount = 0, rate = 0;
    int unit1Qty, unit2Qty, unit3Qty, unitIndex;
    String barCode, productCode, productName, saleType, selectedUnit, unit1, unit2, unit3, packing1, packing2, packing3, invoiceDate, invoiceNo;
    ArrayList<String> list;
    boolean is_first;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sales);
        ButterKnife.bind(this);

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        toolbar.setTitle("New Sales");
        list = new ArrayList<>();
        initView();
        initVolleyCallBack();


        editTextBarcode.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                Log.d(TAG, "beforeTextChanged: ");
                if (editTextBarcode.getText().toString().contains("\n") || (editTextBarcode.getText().toString().contains("\r"))) {
                    Log.d(TAG, "onTextChanged:  enter key pressed");
                    clearView();
                    getDataFromVolley();

                }
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        editTextQuantity.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                //String qty=editTextQuantity.getText().toString();
                calculateNetValue();
            }
        });

        editTextRate.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                calculateNetValue();
            }
        });
        editTextDiscount.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {

                calculateNetValue();
            }
        });
    }

    private void calculateNetValue() {
        double net, rate;
        int saleValue = 1;
        if (saleType.equals("Return"))
            saleValue = -1;
        else if (saleType.equals("Free"))
            saleValue = 0;
        quantity = ParseDouble(editTextQuantity.getText().toString());
        discount = ParseDouble(editTextDiscount.getText().toString());
        rate = ParseDouble(editTextRate.getText().toString());
        Log.d(TAG, "calculateNetValue: " + quantity + "rate :" + rate + "discount :" + discount);

        net = (quantity * rate) - (quantity * discount);
        //set free, sale, return
        net = net * saleValue;
        editTextNet.setText(String.valueOf(net));
    }

    private void clearView() {

        radioButton = findViewById(R.id.radioSale);
        radioButton.setChecked(true);
        textViewProductName.setText("");
        editTextProductCode.setText("");
        editTextQuantity.setText("");
        //editTextFreeQuantity.setText("0");
        editTextRate.setText("");
        editTextDiscount.setText("");
        //editTextCost.setText("");
        editTextStock.setText("");
        //editTextStock.setText("");
        editTextNet.setText("");
        list.clear();
        setSpinner();

    }

    private void initVolleyCallBack() {
        mVolleyListener = new volleyListener() {
            @Override
            public void notifySuccess(String requestType, JSONObject response) {
                hideProgressBar();
                if (response.length() > 0) {
                    editTextQuantity.setFocusableInTouchMode(true);
                    editTextQuantity.requestFocus();
                    setValues(response);
                } else
                    showAlert("Not Found");
            }

            @Override
            public void notifyError(String requestType, VolleyError error) {
                hideProgressBar();
                showAlert("Network Error");

            }
        };
    }

    private void showProgressBar() {
        progressBar.setVisibility(View.VISIBLE);
    }

    private void hideProgressBar() {
        progressBar.setVisibility(View.INVISIBLE);
    }


    public void onRadioButtonClicked(View view) {

        int radioId = radioGroup.getCheckedRadioButtonId();
        Log.d(TAG, "onRadioButtonClicked: " + radioId);
        radioButton = findViewById(radioId);
        saleType = radioButton.getText().toString();
        //Toast.makeText(this, "selected radio button" + radioButton.getText(), Toast.LENGTH_SHORT).show();
        calculateNetValue();
    }


    private void requestStoragePermission() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(SalesActivity.this, Manifest.permission.CAMERA)) {

            new AlertDialog.Builder(SalesActivity.this)
                    .setTitle("Permission needed")
                    .setMessage("To continue please allow the permission ")
                    .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            ActivityCompat.requestPermissions(SalesActivity.this, new String[]{Manifest.permission.CAMERA}, CAMERA_PERMISSION_CODE);

                        }
                    })
                    .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override

                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                        }
                    }).create().show();


        } else {
            ActivityCompat.requestPermissions(SalesActivity.this, new String[]{Manifest.permission.CAMERA}, CAMERA_PERMISSION_CODE);
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

    private void addToCart() {
        Log.d(TAG, "addToCart: selected unit " + selectedUnit);

        int radioId = radioGroup.getCheckedRadioButtonId();
        radioButton = findViewById(radioId);
        barCode = editTextBarcode.getText().toString();
        productCode = editTextProductCode.getText().toString();
        productName = textViewProductName.getText().toString();
        saleType = radioButton.getText().toString();
        double qty, rate, disc, net, free_qty, old_qty = 0;
        int position;
        qty = ParseDouble(editTextQuantity.getText().toString());
        rate = ParseDouble(editTextRate.getText().toString());
        disc = ParseDouble(editTextDiscount.getText().toString());
        net = ParseDouble(editTextNet.getText().toString());

        // add to added items view
        txtAddedBarcode.setText(barCode);
        txtAddedQuantity.setText(editTextQuantity.getText().toString());
        txtAddedPrice.setText(editTextRate.getText().toString());

        //add item into cart


        CartModel cartModel = new CartModel();
        cartModel.setBarcode(barCode);
        cartModel.setProductCode(productCode);
        cartModel.setProductName(productName);
        cartModel.setSaleType(saleType);
        cartModel.setQty(qty);
        cartModel.setSelectedUnit(selectedUnit);
        cartModel.setUnit1(unit1);
        cartModel.setUnit2(unit2);
        cartModel.setUnit3(unit3);
        cartModel.setUnit1Qty(unit1Qty);
        cartModel.setUnit2Qty(unit2Qty);
        cartModel.setUnit3Qty(unit3Qty);
        cartModel.setRate(rate);
        cartModel.setDiscount(disc);
        cartModel.setNet(net);
        Cart.mCart.add(cartModel);
        // }


        //clear the view and focus into barcode text

        editTextBarcode.setText("");
        clearView();
        editTextBarcode.setFocusableInTouchMode(true);
        editTextBarcode.requestFocus();


    }

    void getDataFromVolley() {
        View view = this.getCurrentFocus();
        AppUtils.hideKeyboard(this, view);
        String code = editTextBarcode.getText().toString();
        code = code.replace("\n", "").replace("\r", "");

        if (TextUtils.isEmpty(code)) {
            editTextBarcode.setError("Invalid BarCode");
        } else {
            JSONObject postParam = new JSONObject();
            try {
                postParam.put("Code", code);
            } catch (JSONException e) {
                e.printStackTrace();
            }

            showProgressBar();
            serviceGateway = new VolleyServiceGateway(mVolleyListener, this);
            serviceGateway.postDataVolley("POSTCALL", "PriceChecker/check_price.php", postParam);
        }
    }


    private void setValues(JSONObject response) {

        try {
            Log.d(TAG, "setValues: " + response);

            //set default values
            editTextQuantity.setText("0");
            editTextDiscount.setText("0");

            productCode = response.getString("ProductCode");
            productName = response.getString("ProductName");
            String stock = response.getString("Stock");


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

            editTextProductCode.setText(productCode);
            editTextBarcode.setText(barCode);
            textViewProductName.setText(productName);
            editTextStock.setText(stock);

            list.add(packing1);
            list.add(packing2);
            list.add(packing3);

            setSpinner();

        } catch (Exception e) {
            e.printStackTrace();
            Log.d(TAG, " Exception: " + e.getMessage());
        }


    }

    private void setSpinner() {
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, list);
        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        arrayAdapter.notifyDataSetChanged();
        spinner.setAdapter(arrayAdapter);
        spinner.setSelection(unitIndex);
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

    @Override
    public boolean onSupportNavigateUp() {

        onBackPressed();
        return true;
    }

    @Override
    public void onBackPressed() {
        hideProgressBar();
        if (Cart.mCart.size() > 0) {
            new android.app.AlertDialog.Builder(SalesActivity.this)
                    .setTitle("Warning")
                    .setMessage("Do you want to cancel the sales ?")
                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Cart.mCart.clear();
                            finish();
                        }
                    })
                    .setNegativeButton("No", new DialogInterface.OnClickListener() {
                        @Override

                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                        }
                    }).create().show();
        } else {
            finish();
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

            editTextBarcode.setText(result.getContents());
        } else {
            Toast.makeText(this, "error", Toast.LENGTH_SHORT).show();
        }
    }


    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

        selectedUnit = parent.getItemAtPosition(position).toString();
        if (position == 0) {
            salePrice = price1;
            costPrice = cost;
        } else if (position == 1) {
            salePrice = price2;
            costPrice = cost * unit2Qty;
        } else if (position == 2) {
            salePrice = price3;
            costPrice = cost * unit2Qty * unit3Qty;
        }
        Log.d(TAG, "onItemSelected: Rate " + salePrice);
        DecimalFormat format = new DecimalFormat("#,###,##0.00");
//        editTextSale.setText(format.format(salePrice));
//        editTextCost.setText(format.format(costPrice));

        editTextRate.setText(String.valueOf(salePrice));
        calculateNetValue();
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }


    private void initView() {
        // delete all data from cart if not empty
        if (Cart.mCart.size() > 0) {
            Log.d(TAG, "initView: cart not empty");
            Cart.mCart.clear();
        }
        Intent intent = getIntent();
        customerName = intent.getStringExtra("Customer");
        customerCode = intent.getStringExtra("CustomerCode");
        Log.d(TAG, "customer name: " + customerName);
        //get default radio button values

        int radioId = radioGroup.getCheckedRadioButtonId();
        Log.d(TAG, "onRadioButtonClicked: " + radioId);
        radioButton = findViewById(radioId);
        saleType = radioButton.getText().toString();
        Toast.makeText(this, "sale type : " + radioButton.getText(), Toast.LENGTH_SHORT).show();

        //editTextCost.setEnabled(false);
        editTextProductCode.setEnabled(false);
        editTextStock.setEnabled(false);
        editTextNet.setEnabled(false);
        editTextRate.setEnabled(false);
        editTextBarcode.setFocusableInTouchMode(true);
        editTextBarcode.requestFocus();

        dbHelper helper = new dbHelper(this);

        SQLiteDatabase sqLiteDatabase = helper.getReadableDatabase();
        Cursor cursor = helper.getSettings(sqLiteDatabase);
        if (cursor.moveToFirst()) {
            salesmanId = cursor.getString(cursor.getColumnIndex(DataContract.Settings.COL_DEVICE_ID));
        }
        cursor.close();
        sqLiteDatabase.close();


        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
        //mDate = sdf.format(new Date());
        invoiceDate = AppUtils.getDateAndTime();
        helper = new dbHelper(this);
        int last_no = helper.getLastInvoiceNo();
        Log.d(TAG, "setDoc: " + last_no + "***" + invoiceDate);
        int mDoc = last_no + 1;
        textViewDate.setText(invoiceDate);
        invoiceNo = "INV" + "-" + mDoc;
        textViewInvoice.setText(invoiceNo);
        textViewCustomer.setText(customerName);
        textViewSalesman.setText(salesmanId);


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
        String item_key = editTextBarcode.getText().toString();
        if (item_key.length() < 3) {
            editTextBarcode.setError("Minimum 3 letters");
        } else {
            Bundle bundle = new Bundle();
            bundle.putString("ITEM_NAME", item_key);
            FragmentManager fragmentManager = getSupportFragmentManager();
            ListItemFragment listItemFragment = new ListItemFragment();
            listItemFragment.setArguments(bundle);
            listItemFragment.show(fragmentManager, "Items");

        }


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

    @OnClick(R.id.btnAdd)
    public void onBtnAddClicked() {
        if (TextUtils.isEmpty(editTextProductCode.getText())) {
            editTextProductCode.setError("Invalid Item");
        } else {
            double qty = ParseDouble(editTextQuantity.getText().toString());
            if (qty < 1) {
                editTextQuantity.setError("invalid quantity");
            } else {
                addToCart();
            }


        }
    }

    @OnClick(R.id.imgSubmit)
    public void onImgSubmitClicked() {
        getDataFromVolley();
    }

    @OnClick(R.id.btnNext)
    public void onBtnNextClicked() {

        clearView();
        txtAddedBarcode.setText("");
        txtAddedPrice.setText("");
        txtAddedQuantity.setText("");

        Intent intent = new Intent(SalesActivity.this, ViewCartActivity.class);
        intent.putExtra("ACTION", "NEW");
        intent.putExtra("CUS_NAME", customerName);
        intent.putExtra("CUS_CODE", customerCode);
        intent.putExtra("SALESMAN_ID", salesmanId);
        intent.putExtra("DOC_NO", invoiceNo);
        intent.putExtra("DOC_DATE", invoiceDate);
        startActivity(intent);


    }

    @Override
    public void onComplete(String code) {
        editTextBarcode.setText(code);
    }

    @Override
    protected void onResume() {
        super.onResume();
        hideProgressBar();
    }

}
