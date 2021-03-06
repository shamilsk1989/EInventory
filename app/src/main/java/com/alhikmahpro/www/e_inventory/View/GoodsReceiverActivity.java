package com.alhikmahpro.www.e_inventory.View;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.Settings;
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
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
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
import com.alhikmahpro.www.e_inventory.Data.Converter;
import com.alhikmahpro.www.e_inventory.Data.DataContract;
import com.alhikmahpro.www.e_inventory.Data.ItemModel;
import com.alhikmahpro.www.e_inventory.Data.SessionHandler;
import com.alhikmahpro.www.e_inventory.Data.dbHelper;
import com.alhikmahpro.www.e_inventory.Interface.volleyListener;
import com.alhikmahpro.www.e_inventory.Network.VolleyServiceGateway;
import com.alhikmahpro.www.e_inventory.R;
import com.android.volley.VolleyError;
import com.google.android.gms.vision.barcode.Barcode;

import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;
import com.notbytes.barcode_reader.BarcodeReaderActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
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
    TextView editTextUser;
    @BindView(R.id.editTextSupplier)
    TextView editTextSupplier;
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
    TextView editTextProductName;
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
    @BindView(R.id.editTextDiscountPercentage)
    EditText editTextDiscountPercentage;
    @BindView(R.id.editTextTotal)
    EditText editTextTotal;
    @BindView(R.id.editTextCost)
    EditText editTextCost;
    @BindView(R.id.editTextSale)
    EditText editTextSale;
    @BindView(R.id.editTextStock)
    EditText editTextStock;
    @BindView(R.id.editTextNet)
    EditText editTextNet;
    @BindView(R.id.txtAddedBarcode)
    EditText txtAddedBarcode;

    private static final int CAMERA_PERMISSION_CODE = 101;
    private static final int BARCODE_READER_ACTIVITY_REQUEST = 100;
    private static final String TAG = "GoodsReceiverActivity";
    //    @BindView(R.id.txtSupplierName)
//    TextView txtSupplierName;
    @BindView(R.id.content_layout)
    LinearLayout contentLayout;
    @BindView(R.id.imgSubmit)
    ImageView imgSubmit;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    private String BASE_URL = "";
    private String companyCode, companyName, deviceId, branchCode, locationCode, periodCode;
    //ProgressDialog progressDialog;
    private ConnectivityManager connectivityManager;
    String supplierName, supplierCode, invoiceNo, invoiceDate, User, orderNo;
    boolean is_first;
    private static int cart_count = 0;

    double price1 = 0, price2 = 0, price3 = 0, cost = 0, costPrice = 0, salePrice = 0, discount = 0, rate = 0, discountPercentage = 0;
    int unit1Qty, unit2Qty, unit3Qty, unitIndex, mDoc;
    String barCode, selectedUnit, selectedFreeUnit, unit1, unit2, unit3, packing1, packing2, packing3, mDate;
    ArrayList<String> list;
    ArrayList<String> list2;
    volleyListener mVolleyListener;
    VolleyServiceGateway serviceGateway;
    dbHelper helper;
    SharedPreferences pref;
    SharedPreferences.Editor editor;
    MenuItem itemAdd,itemDelete,itemCart;

    // public static List<ItemModel>itemCart=new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_goods_receiver);
        ButterKnife.bind(this);


        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        Intent mIntent = getIntent();
        supplierCode = mIntent.getStringExtra("SupplierCode");
        supplierName = mIntent.getStringExtra("SupplierName");
        invoiceNo = mIntent.getStringExtra("InvoiceNo");
        invoiceDate = mIntent.getStringExtra("Date");
        User = mIntent.getStringExtra("User");
        orderNo = mIntent.getStringExtra("OrderNo");

        getSupportActionBar().setTitle(supplierName);
        Log.d(TAG, "onCreate: " + "supc:" + supplierCode + "supname:" + supplierName + "inv:" + invoiceNo + "date:" + invoiceDate);
       // progressDialog = new ProgressDialog(this);
        connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        is_first = true;
        list = new ArrayList<>();
        list2 = new ArrayList<>();
        helper=new dbHelper(this);
        pref=getApplicationContext().getSharedPreferences("Goods",0);
        editor=pref.edit();

        setDoc();
        initView();
        initVolleyCallBack();

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

        editTextQuantity.setFocusable(false);
        editTextQuantity.setClickable(true);
        editTextFreeQuantity.setFocusable(false);
        editTextFreeQuantity.setClickable(true);
        editTextDiscount.setFocusable(false);
        editTextDiscount.setClickable(true);
        editTextDiscountPercentage.setFocusable(false);
        editTextDiscountPercentage.setClickable(true);
        editTextRate.setFocusable(false);
        editTextRate.setClickable(true);


        editTextRate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG, "onClick: ");
                rateCustomDialog(editTextRate.getText().toString());
            }
        });

        editTextQuantity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG, "onClick: ");
                quantityCustomDialog(editTextQuantity.getText().toString());
            }
        });
        editTextDiscount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                discountAmountCustomDialog(editTextDiscount.getText().toString());
            }
        });
        editTextDiscountPercentage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                discountPercentageCustomDialog(editTextDiscountPercentage.getText().toString());
            }
        });
        editTextFreeQuantity.setOnClickListener(view -> freeQuantityCustomDialog(editTextFreeQuantity.getText().toString()));
    }


    private void initVolleyCallBack() {
        mVolleyListener = new volleyListener() {
            @Override
            public void notifySuccess(String requestType, JSONObject response) {

                if (response.length() > 0) {

                    setValues(response);

                } else {
                    showAlert("Not Found..");
                }

            }

            @Override
            public void notifyError(String requestType, VolleyError error) {

                generateLog(String.valueOf(error));

            }
        };
    }
    public void generateLog(String error){
        Log.d(TAG, "generateLog: ");
        try{
            String body="/**GoodsReceiverActivity**/"+error;
            File root=new File(Environment.getExternalStorageDirectory(),"Logs");
            if(!root.exists()){
                root.mkdir();
            }
            String fileName=AppUtils.getDateAndTime()+".txt";
            File file=new File(root,fileName);
            FileWriter writer=new FileWriter(file);
            writer.append(body);
            writer.flush();
            writer.close();

        }catch (IOException e){
            e.printStackTrace();
        }
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
        double net, rate, quantity, total, discountAmount;

        total = ParseDouble(editTextTotal.getText().toString());
        discountAmount = ParseDouble(editTextDiscount.getText().toString());
        net = total - discountAmount;


//        quantity = ParseDouble(editTextQuantity.getText().toString());
//        rate = ParseDouble(editTextRate.getText().toString());
//        discount = ParseDouble(editTextDiscount.getText().toString());
//
//        Log.d(TAG, "calculateNetValue: " + quantity + "rate :" + rate + "discount :" + discount);
//        net = (quantity * rate) - (quantity * discount);
        editTextNet.setText(String.valueOf(net));

    }


    @OnClick(R.id.imgBarcode)
    public void onImgBarcodeClicked() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
            requestCameraPermission();
        else
            scanBarcode();
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




    private void addToCart() {
        double qty = 0, rate = 0, disc = 0, net = 0, free_qty, old_qty = 0, discPercentage;
        String stock;
        qty = Double.valueOf(editTextQuantity.getText().toString());
        free_qty = Double.valueOf(editTextFreeQuantity.getText().toString());
        rate = Double.valueOf(editTextRate.getText().toString());
        // disc = Double.valueOf(editTextDiscount.getText().toString());
        stock = editTextStock.getText().toString();
        net = Double.valueOf(editTextNet.getText().toString());
        //discPercentage=ParseDouble(editTextDiscountPercentage.getText().toString());

        Log.d(TAG, "addToCart: net amount " + net);


        // add to added items view

        txtAddedBarcode.setText(barCode + " X " + editTextQuantity.getText().toString() + " X " + editTextSale.getText().toString());

       // add items into goods_details table
        long id=helper.saveGoodsDetails(mDoc,barCode,editTextProductCode.getText().toString(),editTextProductName.getText().toString(),
                editTextProductName.getText().toString(),selectedUnit,selectedFreeUnit,qty,free_qty,rate,discount,discountPercentage,
                salePrice,costPrice,stock,net,unit1,unit2,unit3,unit1Qty,unit2Qty,unit3Qty,DataContract.SYNC_STATUS_FAILED);



        // add items in to the cart...........................
        //check insertion successfully
        if(id>0){
            Log.d("LastID", "Saved: "+id);
            ItemModel itemModel = new ItemModel();
            itemModel.set_id((int)(long)id);
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
            itemModel.setDiscountPercentage(discountPercentage);
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
            cart_count++;
            invalidateOptionsMenu();
        }



        // }


    }


    private void clearView() {

        editTextProductName.setText("");
        editTextProductCode.setText("");
        editTextQuantity.setText("0");
        editTextFreeQuantity.setText("0");
        editTextRate.setText("");
        editTextTotal.setText("0.0");
        editTextCost.setText("");
        editTextSale.setText("");
        editTextStock.setText("");
        editTextNet.setText("");
        editTextDiscount.setText("0");
        editTextDiscountPercentage.setText("0");
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


    private void showSettingDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(GoodsReceiverActivity.this);
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
        startActivityForResult(launchIntent, BARCODE_READER_ACTIVITY_REQUEST);
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

           // progressDialog = AppUtils.showProgressDialog(this, "Loading....");
            serviceGateway = new VolleyServiceGateway(mVolleyListener, this);
            serviceGateway.postDataVolley("POSTCALL", "PriceChecker/check_price.php", postParam);
        }
    }

    private void setValues(JSONObject response) {

        try {

            editTextDiscount.setEnabled(true);
            editTextQuantity.setEnabled(true);
            editTextFreeQuantity.setEnabled(true);
            editTextDiscountPercentage.setEnabled(true);
            editTextRate.setEnabled(true);

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


    private void  initView() {

        //txtAddedPrice.setEnabled(false);
        txtAddedBarcode.setEnabled(false);
        //txtAddedQuantity.setEnabled(false);
        editTextCost.setEnabled(false);
        editTextProductCode.setEnabled(false);
        editTextProductName.setEnabled(false);
        editTextSupplier.setEnabled(false);
        editTextUser.setEnabled(false);
        editTextSale.setEnabled(false);
        editTextRate.setEnabled(false);
        editTextStock.setEnabled(false);
        editTextQuantity.setEnabled(false);
        editTextDiscount.setEnabled(false);
        editTextTotal.setEnabled(false);
        editTextDiscountPercentage.setEnabled(false);
        editTextFreeQuantity.setEnabled(false);
        editTextBarcode.setFocusableInTouchMode(true);
        editTextBarcode.requestFocus();



        txtInvoiceDate.setText("INV:DATE: " + invoiceDate);
        txtInvoiceNo.setText("INV:NO: " + invoiceNo);
        editTextUser.setText(User);
        editTextSupplier.setText(supplierCode);
        //txtSupplierName.setText(supplierName);

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
        int last_no = helper.getAutoId(DataContract.AutoIdGenerator.COL_GOODS_TABLE);
                //helper.getLastID(DataContract.GoodsReceive.TABLE_NAME);
        Log.d(TAG, "setDoc: " + last_no);
        mDoc = last_no + 1;
        txtDate.setText(mDate);
        txtDocNo.setText("DOC NO:" + String.valueOf(mDoc));


        // save document number in shared preference;
        editor.putInt("Doc",mDoc);
        editor.commit();
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

    @Override
    protected void onResume() {
        super.onResume();
        cart_count=Cart.gCart.size();
        Log.d(TAG, "onResume: "+cart_count);

        invalidateOptionsMenu();
    }

    @Override
    public void onBackPressed() {

        if (Cart.gCart.size() > 0) {
            new android.app.AlertDialog.Builder(GoodsReceiverActivity.this)
                    .setTitle("Warning")
                    .setMessage("Do you want to cancel ?")
                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                           // Cart.gCart.clear();
                            ClearCart();
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

    private void ClearCart() {
        Cart.gCart.clear();
        if(pref.contains("Doc")){
            int doc = pref.getInt("Doc", 0);
            boolean del = helper.deleteGoodsDetailsByDoc(doc);
            if (del) {
                editor.remove("Doc");
                editor.clear();
                editor.commit();

            }
        }
    }

    @OnClick(R.id.imgSubmit)
    public void onViewClicked() {
        clearView();
        getDataFromVolley();
    }

    @Override
    public void onComplete(String code) {
        clearView();
        editTextBarcode.setText(code);
    }


    private void rateCustomDialog(String value) {

        LayoutInflater layoutInflater = LayoutInflater.from(this);
        View dialogView = layoutInflater.inflate(R.layout.custom_dialog, null);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(dialogView);

        final EditText editTextUserInput = (EditText) dialogView.findViewById(R.id.editTextDialogUserInput);
        final TextView textView = (TextView) dialogView.findViewById(R.id.textViewTitle);
        editTextUserInput.setText(value);
        editTextUserInput.setSelection(editTextUserInput.getText().length());
        textView.setText("Enter the rate");

        builder.setCancelable(false)
                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int i) {
                        InputMethodManager im = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                        im.hideSoftInputFromWindow(editTextUserInput.getWindowToken(), 0);
                        editTextRate.setText(editTextUserInput.getText().toString());
                        editTextRate.setError(null);
                        //calculate total
                        double total = (ParseDouble(editTextQuantity.getText().toString()) * ParseDouble(editTextRate.getText().toString()));
                        editTextTotal.setText(String.valueOf(total));
                        calculateNetValue();
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int i) {
                        InputMethodManager im = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                        im.hideSoftInputFromWindow(editTextUserInput.getWindowToken(), 0);
                        dialog.cancel();


                    }
                });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();


    }
    private void quantityCustomDialog(String value) {

        LayoutInflater layoutInflater = LayoutInflater.from(this);
        View dialogView = layoutInflater.inflate(R.layout.custom_dialog, null);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(dialogView);

        final EditText editTextUserInput = (EditText) dialogView.findViewById(R.id.editTextDialogUserInput);
        final TextView textView = (TextView) dialogView.findViewById(R.id.textViewTitle);
        editTextUserInput.setText(value);
        editTextUserInput.setSelection(editTextUserInput.getText().length());
        textView.setText("Enter the quantity");

        builder.setCancelable(false)
                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int i) {
                        InputMethodManager im = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                        im.hideSoftInputFromWindow(editTextUserInput.getWindowToken(), 0);
                        editTextQuantity.setText(editTextUserInput.getText().toString());
                        editTextQuantity.setError(null);
                        //calculate total
                        double total = (ParseDouble(editTextQuantity.getText().toString()) * ParseDouble(editTextRate.getText().toString()));
                        editTextTotal.setText(String.valueOf(total));
                        calculateNetValue();
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int i) {
                        InputMethodManager im = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                        im.hideSoftInputFromWindow(editTextUserInput.getWindowToken(), 0);
                        dialog.cancel();


                    }
                });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();


    }

    private void freeQuantityCustomDialog(String value) {

        LayoutInflater layoutInflater = LayoutInflater.from(this);
        View dialogView = layoutInflater.inflate(R.layout.custom_dialog, null);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(dialogView);

        final EditText editTextUserInput = (EditText) dialogView.findViewById(R.id.editTextDialogUserInput);
        final TextView textView = (TextView) dialogView.findViewById(R.id.textViewTitle);
        editTextUserInput.setText(value);
        editTextUserInput.setSelection(editTextUserInput.getText().length());
        textView.setText("Enter free quantity");

        builder.setCancelable(false)
                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int i) {
                        InputMethodManager im = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                        im.hideSoftInputFromWindow(editTextUserInput.getWindowToken(), 0);
                        editTextFreeQuantity.setText(editTextUserInput.getText().toString());
                        //calculate total
                        double total = (ParseDouble(editTextQuantity.getText().toString()) * ParseDouble(editTextRate.getText().toString()));
                        editTextTotal.setText(String.valueOf(total));
                        calculateNetValue();
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int i) {
                        InputMethodManager im = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                        im.hideSoftInputFromWindow(editTextUserInput.getWindowToken(), 0);
                        dialog.cancel();


                    }
                });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();


    }

    private void discountAmountCustomDialog(String value) {

        LayoutInflater layoutInflater = LayoutInflater.from(this);
        View dialogView = layoutInflater.inflate(R.layout.custom_dialog, null);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(dialogView);

        final EditText editTextUserInput = (EditText) dialogView.findViewById(R.id.editTextDialogUserInput);
        final TextView textView = (TextView) dialogView.findViewById(R.id.textViewTitle);
        editTextUserInput.setText(value);
        editTextUserInput.setSelection(editTextUserInput.getText().length());
        textView.setText("Discount amount");

        builder.setCancelable(false)
                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int i) {
                        InputMethodManager im = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                        im.hideSoftInputFromWindow(editTextUserInput.getWindowToken(), 0);
                        //editTextDiscount.setText(editTextUserInput.getText().toString());
                        double disAmount=ParseDouble(editTextUserInput.getText().toString());
                        if(disAmount==0){
                            editTextDiscount.setText("0");
                            editTextDiscountPercentage.setText("0");
                        }else {
                            double total = ParseDouble(editTextTotal.getText().toString());
                            if(total==0){
                                editTextDiscount.setText("0");
                                editTextDiscountPercentage.setText("0");
                            }else {
                                editTextDiscount.setText(String.valueOf(disAmount));
                                double discountPercentage = ParseDouble(editTextUserInput.getText().toString()) / total * 100;
                                editTextDiscountPercentage.setText(String.format("%.2f", discountPercentage));
                            }
                        }
                        calculateNetValue();


                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int i) {
                        InputMethodManager im = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                        im.hideSoftInputFromWindow(editTextUserInput.getWindowToken(), 0);
                        dialog.cancel();


                    }
                });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();


    }

    private void discountPercentageCustomDialog(String value) {

        LayoutInflater layoutInflater = LayoutInflater.from(this);
        View dialogView = layoutInflater.inflate(R.layout.custom_dialog, null);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(dialogView);

        final EditText editTextUserInput = (EditText) dialogView.findViewById(R.id.editTextDialogUserInput);
        final TextView textView = (TextView) dialogView.findViewById(R.id.textViewTitle);
        editTextUserInput.setText(value);
        editTextUserInput.setSelection(editTextUserInput.getText().length());
        textView.setText("Discount percentage");

        builder.setCancelable(false)
                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int i) {
                        InputMethodManager im = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                        im.hideSoftInputFromWindow(editTextUserInput.getWindowToken(), 0);

                        editTextDiscountPercentage.setText(editTextUserInput.getText().toString());
                        discountPercentage = ParseDouble(editTextUserInput.getText().toString());
                        double percentageDecimal = discountPercentage / 100;
                        double discountAmount = percentageDecimal * ParseDouble(editTextTotal.getText().toString());
                        editTextDiscount.setText(String.format("%.2f", discountAmount));
                        calculateNetValue();
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int i) {
                        InputMethodManager im = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                        im.hideSoftInputFromWindow(editTextUserInput.getWindowToken(), 0);
                        dialog.cancel();


                    }
                });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();


    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.cart_toolbar, menu);
        itemCart = menu.findItem(R.id.action_cart);
        Log.d(TAG, "onCreateOptionsMenu: " + Converter.convertLayoutToImage(GoodsReceiverActivity.this, cart_count, R.drawable.ic_shopping_cart));
        itemCart.setIcon(Converter.convertLayoutToImage(GoodsReceiverActivity.this, cart_count, R.drawable.ic_shopping_cart));
        itemDelete = menu.findItem(R.id.action_delete);
        itemDelete.setVisible(false);
        itemAdd = menu.findItem(R.id.action_add);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection

        int id = item.getItemId();
        if(id==R.id.action_cart){
            txtAddedBarcode.setText("");
//        txtAddedPrice.setText("");
//        txtAddedQuantity.setText("");


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

        } else if (id==R.id.action_add) {

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
                    txtAddedBarcode.setText(barCode + " X " + editTextQuantity.getText().toString() + " X " + editTextSale.getText().toString());
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

        return super.onOptionsItemSelected(item);
    }
}
