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
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.alhikmahpro.www.e_inventory.AppUtils;
import com.alhikmahpro.www.e_inventory.Data.Cart;
import com.alhikmahpro.www.e_inventory.Data.DataContract;
import com.alhikmahpro.www.e_inventory.Data.ItemModel;
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

public class GoodsReceiverActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener, ListItemFragment.OnCompleteListener {

    @BindView(R.id.txtDate)
    TextView txtDate;
    @BindView(R.id.txtDocNo)
    TextView txtDocNo;
    @BindView(R.id.editTextUser)
    EditText editTextUser;
    @BindView(R.id.editTextSupplier)
    EditText editTextSupplier;
    @BindView(R.id.txtInvoiceDate)
    TextView txtInvoiceDate;
    @BindView(R.id.txtInvoiceNo)
    TextView txtInvoiceNo;
    @BindView(R.id.editTextBarcode)
    EditText editTextBarcode;
    @BindView(R.id.imgBarcode)
    ImageView imgBarcode;
    @BindView(R.id.imgSearch)
    ImageView imgSearch;
    @BindView(R.id.header_layout)
    LinearLayout headerLayout;
    @BindView(R.id.editTextProductName)
    EditText editTextProductName;
    @BindView(R.id.editTextProductCode)
    EditText editTextProductCode;
    @BindView(R.id.spinner)
    Spinner spinner;
    @BindView(R.id.spinner_free)
    Spinner spinner_free;
    @BindView(R.id.editTextQuantity)
    EditText editTextQuantity;
    @BindView(R.id.editTextFreeQuantity)
    EditText editTextFreeQuantity;
    @BindView(R.id.editTextRate)
    EditText editTextRate;
    @BindView(R.id.editTextDiscount)
    EditText editTextDiscount;

    @BindView(R.id.editTextCost)
    EditText editTextCost;
    @BindView(R.id.editTextSale)
    EditText editTextSale;
    @BindView(R.id.editTextStock)
    EditText editTextStock;
    @BindView(R.id.editTextNet)
    EditText editTextNet;
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
    private static final int CAMERA_PERMISSION_CODE = 101;
    private static final String TAG = "GoodsReceiverActivity";
    @BindView(R.id.txtSupplierName)
    TextView txtSupplierName;
    @BindView(R.id.content_layout)
    LinearLayout contentLayout;
    @BindView(R.id.imgSubmit)
    ImageView imgSubmit;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    private String BASE_URL = "";
    private String companyCode, companyName, deviceId, branchCode, locationCode, periodCode;
    ProgressDialog progressDialog;
    private ConnectivityManager connectivityManager;
    String supplierName, supplierCode, invoiceNo, invoiceDate, User, orderNo;
    boolean is_first;

    double price1 = 0, price2 = 0, price3 = 0, cost = 0, costPrice = 0, salePrice = 0, discount = 0, rate = 0;
    int unit1Qty, unit2Qty, unit3Qty, unitIndex, mDoc;
    String barCode, selectedUnit, selectedFreeUnit, unit1, unit2, unit3, packing1, packing2, packing3, mDate;
    ArrayList<String> list;
    ArrayList<String> list2;
    volleyListener mVolleyListener;
    VolleyServiceGateway serviceGateway;


    // public static List<ItemModel>itemCart=new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_goods_receiver);
        ButterKnife.bind(this);


        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        toolbar.setTitle("Goods Receive");
        Intent mIntent = getIntent();
        supplierCode = mIntent.getStringExtra("SupplierCode");
        supplierName = mIntent.getStringExtra("SupplierName");
        invoiceNo = mIntent.getStringExtra("InvoiceNo");
        invoiceDate = mIntent.getStringExtra("Date");
        User = mIntent.getStringExtra("User");
        orderNo = mIntent.getStringExtra("OrderNo");
        Log.d(TAG, "onCreate: " + "supc:" + supplierCode + "supname:" + supplierName + "inv:" + invoiceNo + "date:" + invoiceDate);
        progressDialog = new ProgressDialog(this);
        connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        is_first = true;
        list = new ArrayList<>();
        list2 = new ArrayList<>();

        setDoc();
        initView();
        initVolleyCallBack();

        editTextQuantity.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                calculateNetValue();
            }
        });


        editTextRate.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                calculateNetValue();

            }
        });
        editTextDiscount.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                calculateNetValue();
            }
        });

        editTextBarcode.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                Log.d(TAG, "beforeTextChanged: ");
                if (editTextBarcode.getText().toString().contains("\n") || (editTextBarcode.getText().toString().contains("\r"))) {
                    Log.d(TAG, "onTextChanged:  enter key pressed");
                    clearView();
                    //sendToServer();
                    getDataFromVolley();

                }
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

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

                if (response.length() > 0) {
                    editTextQuantity.setFocusableInTouchMode(true);
                    editTextQuantity.requestFocus();
                    setValues(response);

                } else {
                    showAlert("Not Found..");

                }

            }

            @Override
            public void notifyError(String requestType, VolleyError error) {

            }
        };
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
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

    private void calculateNetValue() {
        double net, rate, quantity;

        quantity = ParseDouble(editTextQuantity.getText().toString());
        rate = ParseDouble(editTextRate.getText().toString());
        discount = ParseDouble(editTextDiscount.getText().toString());

        Log.d(TAG, "calculateNetValue: " + quantity + "rate :" + rate + "discount :" + discount);

        net = (quantity * rate) - (quantity * discount);
        editTextNet.setText(String.valueOf(net));

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


    @OnClick(R.id.btnAdd)
    public void onBtnAddClicked() {

        if (TextUtils.isEmpty(editTextProductCode.getText())) {
            editTextProductCode.setError("Invalid Item");
        } else {


            double qty, free_qty;
            try {
                qty = Double.valueOf(editTextQuantity.getText().toString());
                free_qty = Double.valueOf(editTextFreeQuantity.getText().toString());

            } catch (NumberFormatException e) {
                qty = 0;
                free_qty = 0;
            }
            Log.d(TAG, "onBtnAddClicked: qty=" + qty + "Free=" + free_qty);
            if (qty == 0 && free_qty < 1) {
                editTextQuantity.setError("invalid quantity");
            } else {

                //set added items view

                txtAddedBarcode.setText(barCode);
                txtAddedQuantity.setText(editTextQuantity.getText().toString());
                txtAddedPrice.setText(editTextSale.getText().toString());

                // add items into the cart
                addToCart();
                // clear the view and set focus into barcode

                editTextBarcode.setText("");
                clearView();
                editTextBarcode.setFocusableInTouchMode(true);
                editTextBarcode.requestFocus();
            }


        }


    }

    private void addToCart() {
        double qty = 0, rate = 0, disc = 0, net = 0, free_qty, old_qty = 0;
        String stock;
        qty = Double.valueOf(editTextQuantity.getText().toString());
        free_qty = Double.valueOf(editTextFreeQuantity.getText().toString());
        rate = Double.valueOf(editTextRate.getText().toString());
        disc = Double.valueOf(editTextDiscount.getText().toString());
        stock = editTextStock.getText().toString();
        net = Double.valueOf(editTextNet.getText().toString());

        Log.d(TAG, "addToCart: net amount " + net);


        // add to added items view

        txtAddedBarcode.setText(barCode);
        txtAddedQuantity.setText(editTextQuantity.getText().toString());
        txtAddedPrice.setText(editTextSale.getText().toString());

        // add items in to the cart...........................

        //check cart empty or not
//        if (Cart.gCart.size() > 0) {
//            boolean isFound = false;
//            int position;
//
//            //check item already added into the cart
//            for (position = 0; position < Cart.gCart.size(); position++) {
//                ItemModel itemModel = Cart.gCart.get(position);
//                if (itemModel.getProductCode().equals(editTextProductCode.getText().toString())) {
//                    old_qty = itemModel.getQty();
//                    isFound = true;
//                    break;
//                }
//            }
//
//            //if item found then update cart
//
//            if (isFound) {
//                ItemModel itemModel = new ItemModel();
//                Cart.gCart.remove(position);
//
//                itemModel.setDocNo(mDoc);
//                itemModel.setBarCode(barCode);
//                itemModel.setProductCode(editTextProductCode.getText().toString());
//                itemModel.setProductName(editTextProductName.getText().toString());
//                itemModel.setQty(qty + old_qty);
//                itemModel.setFreeQty(free_qty);
//                itemModel.setSelectedPackage(selectedUnit);
//                itemModel.setSelectedFreePackage(selectedFreeUnit);
//                itemModel.setRate(rate);
//                itemModel.setDiscount(discount);
//                itemModel.setCostPrice(costPrice);
//                itemModel.setSalePrice(salePrice);
//                itemModel.setStock(stock);
//                itemModel.setUnit1(unit1);
//                itemModel.setUnit2(unit2);
//                itemModel.setUnit3(unit3);
//                itemModel.setUnit1Qty(unit1Qty);
//                itemModel.setUnit2Qty(unit2Qty);
//                itemModel.setUnit3Qty(unit3Qty);
//                itemModel.setNet(net);
//                Cart.gCart.add(position, itemModel);
//
//            }//item not found in cart add to cart
//            else {
//                ItemModel itemModel = new ItemModel();
//                itemModel.setDocNo(mDoc);
//                itemModel.setBarCode(barCode);
//                itemModel.setProductCode(editTextProductCode.getText().toString());
//                itemModel.setProductName(editTextProductName.getText().toString());
//                itemModel.setQty(qty);
//                itemModel.setFreeQty(free_qty);
//                itemModel.setSelectedPackage(selectedUnit);
//                itemModel.setSelectedFreePackage(selectedFreeUnit);
//                itemModel.setRate(rate);
//                itemModel.setDiscount(discount);
//                itemModel.setCostPrice(costPrice);
//                itemModel.setSalePrice(salePrice);
//                itemModel.setStock(stock);
//                itemModel.setUnit1(unit1);
//                itemModel.setUnit2(unit2);
//                itemModel.setUnit3(unit3);
//                itemModel.setUnit1Qty(unit1Qty);
//                itemModel.setUnit2Qty(unit2Qty);
//                itemModel.setUnit3Qty(unit3Qty);
//                itemModel.setNet(net);
//                Cart.gCart.add(itemModel);
//            }
//
//        } // Cart is empty add new items
//        else {

        ItemModel itemModel = new ItemModel();
        itemModel.setDocNo(mDoc);
        itemModel.setBarCode(barCode);
        itemModel.setProductCode(editTextProductCode.getText().toString());
        itemModel.setProductName(editTextProductName.getText().toString());
        itemModel.setQty(qty);
        itemModel.setFreeQty(free_qty);
        itemModel.setSelectedPackage(selectedUnit);
        itemModel.setSelectedFreePackage(selectedFreeUnit);
        itemModel.setRate(rate);
        itemModel.setDiscount(discount);
        itemModel.setCostPrice(costPrice);
        itemModel.setSalePrice(salePrice);
        itemModel.setStock(stock);
        itemModel.setUnit1(unit1);
        itemModel.setUnit2(unit2);
        itemModel.setUnit3(unit3);
        itemModel.setUnit1Qty(unit1Qty);
        itemModel.setUnit2Qty(unit2Qty);
        itemModel.setUnit3Qty(unit3Qty);
        itemModel.setNet(net);
        Cart.gCart.add(itemModel);
        // }


    }

    @OnClick(R.id.btnNext)
    public void onBtnNextClicked() {
        txtAddedBarcode.setText("");
        txtAddedPrice.setText("");
        txtAddedQuantity.setText("");


        Intent intent = new Intent(this, GoodsItemListActivity.class);
        //intent.putExtra("DocNo", Integer.valueOf(txtDocNo.getText().toString()));
        intent.putExtra("ACTION", "New");
        intent.putExtra("DOC_NO", mDoc);
        intent.putExtra("ORD_NO", orderNo);
        intent.putExtra("SUPP_CODE", supplierCode);
        intent.putExtra("SUPP_NAME", supplierName);
        intent.putExtra("INV_NO", invoiceNo);
        intent.putExtra("INV_DATE", invoiceDate);
        intent.putExtra("USER", User);
        startActivity(intent);
    }

    private void clearView() {

        editTextProductName.setText("");
        editTextProductCode.setText("");
        editTextQuantity.setText("0");
        editTextFreeQuantity.setText("0");
        editTextRate.setText("");
        editTextDiscount.setText("0");
        editTextCost.setText("");
        editTextSale.setText("");
        editTextStock.setText("");
        editTextNet.setText("");
        list.clear();
        list2.clear();
        setSpinner();

    }


    private boolean validate(String p_code, String qty, String rate) {

        if (TextUtils.isEmpty(p_code)) {
            editTextProductCode.setError("Invalid Product");
            return false;
        } else if (TextUtils.isEmpty(qty)) {
            editTextProductCode.setError("Invalid Quantity");
            return false;
        } else if (TextUtils.isEmpty(rate)) {
            editTextProductCode.setError("Invalid Rate");
            return false;
        }
        return true;

    }

    private void requestStoragePermission() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(GoodsReceiverActivity.this, Manifest.permission.CAMERA)) {

            new AlertDialog.Builder(GoodsReceiverActivity.this)
                    .setTitle("Permission needed")
                    .setMessage("To continue please allow the permission ")
                    .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            ActivityCompat.requestPermissions(GoodsReceiverActivity.this, new String[]{Manifest.permission.CAMERA}, CAMERA_PERMISSION_CODE);

                        }
                    })
                    .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override

                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                        }
                    }).create().show();


        } else {
            ActivityCompat.requestPermissions(GoodsReceiverActivity.this, new String[]{Manifest.permission.CAMERA}, CAMERA_PERMISSION_CODE);
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

            progressDialog = AppUtils.showProgressDialog(this, "Loading....");
            serviceGateway = new VolleyServiceGateway(mVolleyListener, this);
            serviceGateway.postDataVolley("POSTCALL", "PriceChecker/check_price.php", postParam);
        }
    }

    private void setValues(JSONObject response) {

        try {
            Log.d(TAG, "setValues: " + response);

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

            editTextProductCode.setText(productCode);
            editTextProductName.setText(productName);
            editTextBarcode.setText(barCode);
            editTextStock.setText(response.getString("Stock"));
            if (packing1 != null && packing1.trim().length() > 0) {
                list.add(packing1);
                list2.add(packing1);
            }

            if (packing2 != null && packing2.trim().length() > 0) {
                list.add(packing2);
                list2.add(packing2);
            }
            if (packing3 != null && packing3.trim().length() > 0) {
                list.add(packing3);
                list2.add(packing3);
            }
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


        ArrayAdapter<String> arrayAdapter1 = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, list);
        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        arrayAdapter1.notifyDataSetChanged();
        spinner_free.setAdapter(arrayAdapter1);
        spinner_free.setSelection(unitIndex);
        spinner_free.setOnItemSelectedListener(this);


        //spinner_free.setOnItemClickListener(this);
    }


    private void initView() {
        txtAddedPrice.setEnabled(false);
        txtAddedBarcode.setEnabled(false);
        txtAddedQuantity.setEnabled(false);
        editTextCost.setEnabled(false);
        editTextProductCode.setEnabled(false);
        editTextProductName.setEnabled(false);
        editTextSupplier.setEnabled(false);
        editTextUser.setEnabled(false);
        editTextSale.setEnabled(false);
        editTextRate.setEnabled(false);
        editTextStock.setEnabled(false);
        editTextBarcode.setFocusableInTouchMode(true);
        editTextBarcode.requestFocus();

        dbHelper helper = new dbHelper(this);

        SQLiteDatabase sqLiteDatabase = helper.getReadableDatabase();
        Cursor cursor = helper.getSettings(sqLiteDatabase);
        if (cursor.moveToFirst()) {
            companyCode = cursor.getString(cursor.getColumnIndex(DataContract.Settings.COL_COMPANY_CODE));
            companyName = cursor.getString(cursor.getColumnIndex(DataContract.Settings.COL_COMPANY_NAME));
            deviceId = cursor.getString(cursor.getColumnIndex(DataContract.Settings.COL_DEVICE_ID));
            branchCode = cursor.getString(cursor.getColumnIndex(DataContract.Settings.COL_BRANCH_CODE));
            periodCode = cursor.getString(cursor.getColumnIndex(DataContract.Settings.COL_PERIOD_CODE));
            locationCode = cursor.getString(cursor.getColumnIndex(DataContract.Settings.COL_LOCATION_CODE));
            BASE_URL = SessionHandler.getInstance(GoodsReceiverActivity.this).getHost();

        }
        cursor.close();
        sqLiteDatabase.close();

        txtInvoiceDate.setText(invoiceDate);
        txtInvoiceNo.setText(invoiceNo);
        editTextUser.setText(User);
        editTextSupplier.setText(supplierCode);
        txtSupplierName.setText(supplierName);

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


    private void setDoc() {

        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
        dbHelper helper = new dbHelper(this);
        mDate = sdf.format(new Date());
        int last_no = helper.getGoodsLastDocNo();
        Log.d(TAG, "setDoc: " + last_no);
        mDoc = last_no + 1;
        txtDate.setText(mDate);
        txtDocNo.setText(String.valueOf(mDoc));
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        if (parent.getId() == R.id.spinner) {

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
            Log.d(TAG, "onItemSelected: " + position);
            DecimalFormat format = new DecimalFormat("#,###,##0.00");
            editTextSale.setText(format.format(salePrice));
            editTextCost.setText(format.format(costPrice));
            editTextRate.setText(String.valueOf(costPrice));
            calculateNetValue();
        } else if (parent.getId() == R.id.spinner_free) {
            selectedFreeUnit = parent.getItemAtPosition(position).toString();

        }


    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {


    }

    @Override
    public boolean onNavigateUp() {
        onBackPressed();
        return true;
    }

    @OnClick(R.id.imgSubmit)
    public void onViewClicked() {
        clearView();
        getDataFromVolley();
    }

    @Override
    public void onComplete(String code) {
        editTextBarcode.setText(code);
    }
}
