package com.alhikmahpro.www.e_inventory.View;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
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
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.alhikmahpro.www.e_inventory.AppUtils;
import com.alhikmahpro.www.e_inventory.Data.Cart;
import com.alhikmahpro.www.e_inventory.Data.CartModel;
import com.alhikmahpro.www.e_inventory.Data.Converter;
import com.alhikmahpro.www.e_inventory.Data.DataContract;
import com.alhikmahpro.www.e_inventory.Data.dbHelper;
import com.alhikmahpro.www.e_inventory.FileUtils;
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

public class SalesActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener, ListItemFragment.OnCompleteListener {

    @BindView(R.id.textViewInvoice)
    TextView textViewInvoice;
    @BindView(R.id.textViewDate)
    TextView textViewDate;
//    @BindView(R.id.textViewCustomer)
//    TextView textViewCustomer;
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
    //    @BindView(R.id.spinner2)
//    Spinner spinner2;
    @BindView(R.id.editTextQuantity)
    EditText editTextQuantity;

    @BindView(R.id.editTextRate)
    EditText editTextRate;
    @BindView(R.id.editTextDiscount)
    EditText editTextDiscount;
    @BindView(R.id.editTextTotal)
    EditText editTextTotal;
    @BindView(R.id.editTextDiscountPercentage)
    EditText editTextDiscountPercentage;
    @BindView(R.id.editTextStock)
    EditText editTextStock;
    @BindView(R.id.editTextNet)
    EditText editTextNet;
    @BindView(R.id.radioGroup)
    RadioGroup radioGroup;
//    @BindView(R.id.btnNext)
//    Button btnNext;
//    @BindView(R.id.btnAdd)
//    Button btnAdd;
    @BindView(R.id.txtAddedBarcode)
    EditText txtAddedBarcode;
    MenuItem itemCart,itemDelete,itemAdd;
//    @BindView(R.id.txtAddedPrice)
//    EditText txtAddedPrice;
//    @BindView(R.id.txtAddedQuantity)
//    EditText txtAddedQuantity;

    RadioButton radioButton;
    private static final String TAG = "SalesActivity";
    private static final int CAMERA_PERMISSION_CODE = 101;
    private static final int BARCODE_READER_ACTIVITY_REQUEST=100;
    @BindView(R.id.progressBar)
    ProgressBar progressBar;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
//    @BindView(R.id.title_layout2)
//    LinearLayout titleLayout2;
    @BindView(R.id.radioReturn)
    RadioButton radioReturn;
    @BindView(R.id.radioFree)
    RadioButton radioFree;
    private String customerName, salesmanId, customerCode,type;
    volleyListener mVolleyListener;
    VolleyServiceGateway serviceGateway;

    double price1 = 0, price2 = 0, price3 = 0, cost = 0, costPrice = 0, salePrice = 0, quantity;
    double discount = 0, discountPercentage = 0, discountAmount = 0;
    int unit1Qty, unit2Qty, unit3Qty, unitIndex;
    String barCode, productCode, productName, saleType, selectedUnit, unit1, unit2, unit3, packing1, packing2, packing3, invoiceDate, invoiceNo;
    ArrayList<String> list;
    private static int cart_count = 0;

    SharedPreferences pref;
    SharedPreferences.Editor editor;
    dbHelper helper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sales);
        ButterKnife.bind(this);

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        //getSupportActionBar().setTitle("New Sales");
        list = new ArrayList<>();
        helper = new dbHelper(this);
        pref=getApplicationContext().getSharedPreferences("Invoice",0);
        editor=pref.edit();
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


        editTextQuantity.setFocusable(false);
        editTextQuantity.setClickable(true);
        editTextDiscount.setFocusable(false);
        editTextDiscount.setClickable(true);
        editTextDiscountPercentage.setFocusable(false);
        editTextDiscountPercentage.setClickable(true);

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

    }


    private void calculateNetValue() {
        Log.d(TAG, "calculateNetValue: ");
        int saleValue = 1;
        if (saleType.equals("Return"))
            saleValue = -1;
        else if (saleType.equals("Free"))
            saleValue = 0;


        double total=Double.parseDouble(editTextRate.getText().toString())* ParseDouble(editTextQuantity.getText().toString());
        double net = total-ParseDouble(editTextDiscount.getText().toString());
        editTextTotal.setText(String.format("%.2f", total));

        //set free, sale, return
        net = net * saleValue;
        Log.d(TAG, "calculateNetValue: net value"+net);
        editTextNet.setText(String.format("%.2f", net));
    }


    private void clearView() {

        radioButton = findViewById(R.id.radioSale);
        radioButton.setChecked(true);
        textViewProductName.setText("");
        editTextProductCode.setText("");
        editTextQuantity.setText("0");
        //editTextFreeQuantity.setText("0");
        editTextRate.setText("0.0");
        editTextDiscount.setText("");
        editTextDiscountPercentage.setText("");

        //editTextCost.setText("");
        editTextStock.setText("");
        //editTextStock.setText("");
        editTextNet.setText("0.0");
        editTextTotal.setText("0.0");

        editTextStock.setEnabled(false);
        editTextQuantity.setEnabled(false);
        editTextDiscount.setEnabled(false);
        list.clear();
        setSpinner();

    }


    private void initVolleyCallBack() {
        mVolleyListener = new volleyListener() {
            @Override
            public void notifySuccess(String requestType, JSONObject response) {
                hideProgressBar();
                if (response.length() > 0) {
//                    editTextQuantity.setFocusableInTouchMode(true);
//                    editTextQuantity.requestFocus();
                    setValues(response);
                } else
                    showAlert("Not Found");
            }

            @Override
            public void notifyError(String requestType, VolleyError error) {
                hideProgressBar();
                generateLog(String.valueOf(error));
                showAlert("Network Error");

            }
        };
    }
    public void generateLog(String error){
        Log.d(TAG, "generateLog: ");
        try{
            String path= FileUtils.getSubDirPath(this, DataContract.DIR_LOGS);
            String header="/**SalesActivity**/";
            String fileName="Logs.txt";
            File file=new File(path,fileName);
            FileWriter writer=new FileWriter(file);
            writer.append(header);
            writer.append("\n");
            writer.append(AppUtils.getDateAndTime());
            writer.append("\n");
            writer.append("Response");
            writer.append("\n");

            writer.append(error);
            writer.append("****************End*******");
            writer.flush();
            writer.close();

        }catch (IOException e){
            e.printStackTrace();
        }
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
        calculateNetValue();

    }


    private void requestStoragePermission() {
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
        AlertDialog.Builder builder = new AlertDialog.Builder(SalesActivity.this);
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
        Intent launchIntent = BarcodeReaderActivity.getLaunchIntent(this, true, false);
        startActivityForResult(launchIntent,BARCODE_READER_ACTIVITY_REQUEST);
    }

    private void addToCart() {
        Log.d(TAG, "addToCart: selected unit " + selectedUnit);

        int radioId = radioGroup.getCheckedRadioButtonId();
        radioButton = findViewById(radioId);
        barCode = editTextBarcode.getText().toString();
        productCode = editTextProductCode.getText().toString();
        productName = textViewProductName.getText().toString();
        saleType = radioButton.getText().toString();
        double qty, rate, disc, net, free_qty, old_qty = 0,discPercentage;
        int position;
        qty = ParseDouble(editTextQuantity.getText().toString());
        rate = ParseDouble(editTextRate.getText().toString());
        disc = ParseDouble(editTextDiscount.getText().toString());
        net = ParseDouble(editTextNet.getText().toString());
        discPercentage=ParseDouble(editTextDiscountPercentage.getText().toString());

        // add to added items view
        txtAddedBarcode.setText(barCode+ " X "+editTextQuantity.getText().toString()+" X "+editTextRate.getText().toString());

        Log.d(TAG, "addToCart: invoice no"+invoiceNo);
        // save to database
        long lastId=0;
        if(type.equals("SAL")){
            lastId=helper.saveInvoiceDetails(invoiceNo,barCode,productCode,productName,productName,qty,selectedUnit,
                    unit1,unit2,unit3,unit1Qty,unit2Qty,unit3Qty,rate,disc,discPercentage,net,saleType,
                    DataContract.SYNC_STATUS_FAILED);

        }else  if(type.equals("ORD")){
            lastId=helper.saveOrderDetails(invoiceNo,barCode,productCode,productName,productName,qty,selectedUnit,
                    unit1,unit2,unit3,unit1Qty,unit2Qty,unit3Qty,rate,disc,discPercentage,net,saleType,
                    DataContract.SYNC_STATUS_FAILED);
        }
        Log.d("LastID", "Added: "+lastId);

        //add item into cart
        if(lastId>0)//check inserted successfully
        {
            CartModel cartModel = new CartModel();
            cartModel.set_id((int)(long) lastId);
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
            Log.d(TAG, "addToCart: "+discPercentage);
            cartModel.setDiscPercentage(discPercentage);
            cartModel.setDiscount(disc);
            cartModel.setNet(net);
            Cart.mCart.add(cartModel);
        }


        //clear the view and focus into barcode text

        editTextBarcode.setText("");
        clearView();
        editTextBarcode.setFocusableInTouchMode(true);
        editTextBarcode.requestFocus();

        //set badge count
        cart_count++;
        invalidateOptionsMenu();
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

            //enable editing

            editTextDiscount.setEnabled(true);
            editTextQuantity.setEnabled(true);
            if(type.equals("ORD")){
                editTextQuantity.setText("1");
            }
            String pCode,pName,stock,bCode,p1,p2,p3,u1,u2,u3,uIndex,u1Qty,u2Qty,u3qty,lCost,pk1,pk2,pk3;
            productCode = response.getString("ProductCode");
            productName = response.getString("ProductName");
            stock = response.getString("Stock");

            Log.d(TAG, "setValues: SalesPrice1 in double"+response.getDouble("SalesPrice1"));
            Log.d(TAG, "setValues: SalesPrice1 in string"+response.getString("SalesPrice1"));

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
        Log.d(TAG, "setSpinner: ");
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
                    .setMessage("Do you want to cancel ?")
                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            //Cart.mCart.clear();
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
        Cart.mCart.clear();
        if(pref.contains("Inv")){
            String inv = pref.getString("Inv", "");
            boolean del=false;
            if(type.equals("SAL")){
              del = helper.deleteInvoiceDetailsByInvoiceNo(inv);
            }else if(type.equals("ORD")) {
              del = helper.deleteOrderDetailsByInvoiceNo(inv);
            }

            if (del) {
                editor.remove("Inv");
                editor.clear();
                editor.commit();

            }
        }


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
            if(type.equals("ORD")){
                clearView();
                getDataFromVolley();

                if (TextUtils.isEmpty(editTextProductCode.getText())) {
                    editTextProductCode.setError("Invalid Item");
                } else {
                    editTextProductCode.setError(null);
                    double qty = ParseDouble(editTextQuantity.getText().toString());
                    if (qty < 1) {
                        editTextQuantity.setError("invalid quantity");
                    } else {
                        editTextQuantity.setError(null);
                        addToCart();
                    }
                }

            } 
        }
    }


    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

        Log.d(TAG, "spinner changed");
        Spinner spin = (Spinner) parent;
        //Spinner spin2 = (Spinner) parent;
        if (spin.getId() == R.id.spinner) {
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
            //DecimalFormat format = new DecimalFormat("#,###,##0.00");
            editTextRate.setText(String.valueOf(salePrice));
            calculateNetValue();

        }

//        } else if (spin2.getId() == R.id.spinner2) {
//            Log.d(TAG, "onItemSelected: dis type" + parent.getItemAtPosition(position).toString());
//            discountCode = parent.getItemAtPosition(position).toString();
//        }
        calculateNetValue();


    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }


    private void initView() {
        // clear the cart before starting
        Cart.mCart.clear();
        if (pref.contains("Inv")) {
            editor.remove("Inv");
            editor.clear();
            editor.commit();
        }

        Intent intent = getIntent();
        type=intent.getStringExtra("Type");
        customerName = intent.getStringExtra("Customer");
        customerCode = intent.getStringExtra("CustomerCode");
        getSupportActionBar().setTitle(customerName);
        Log.d(TAG, "customer code: " + customerCode+type);
        //get default radio button values

        int radioId = radioGroup.getCheckedRadioButtonId();
        Log.d(TAG, "onRadioButtonClicked: " + radioId);
        radioButton = findViewById(radioId);
        saleType = radioButton.getText().toString();
        //Toast.makeText(this, "sale type : " + radioButton.getText(), Toast.LENGTH_SHORT).show();

        //editTextCost.setEnabled(false);
        editTextProductCode.setEnabled(false);
        editTextStock.setEnabled(false);
        editTextNet.setEnabled(false);
        editTextRate.setEnabled(false);
        editTextQuantity.setEnabled(false);
        editTextDiscount.setEnabled(false);
        editTextTotal.setEnabled(false);
        //editTextDiscountPer.setEnabled(false);
        editTextBarcode.setFocusableInTouchMode(true);
        editTextBarcode.requestFocus();
        SharedPreferences sharedPreferences= PreferenceManager.getDefaultSharedPreferences(this);
        salesmanId=sharedPreferences.getString("key_employee","0");
        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
        invoiceDate= sdf.format(new Date());
        helper = new dbHelper(this);
        textViewDate.setText(invoiceDate);

        if(type.equals("SAL")){
            int last_no =helper.getAutoId(DataContract.AutoIdGenerator.COL_INVOICE_TABLE);
                    //helper.getLastID(DataContract.Invoice.TABLE_NAME);

            Log.d(TAG, "setDoc: " + last_no);
            int mDoc = last_no + 1;
            invoiceNo = "INV" + "-" + mDoc;
            //delete invoiceDetails before first time insertion
            helper.deleteInvoiceDetailsByInvoiceNo(invoiceNo);


        }else if(type.equals("ORD")){
            int last_no = helper.getAutoId(DataContract.AutoIdGenerator.COL_ORDER_TABLE);
                    //helper.getLastID(DataContract.Order.TABLE_NAME);
            Log.d(TAG, "setDoc: " + last_no);
            int mDoc = last_no + 1;
            invoiceNo = "ORD" + "-" + mDoc;

            //delete orderDetails before first time insertion
            helper.deleteOrderDetailsByInvoiceNo(invoiceNo);
        }

        textViewInvoice.setText(invoiceNo);
        //textViewCustomer.setText(customerName);
        textViewSalesman.setText(salesmanId);

        // save invoice number in shared preference;
        editor.putString("Inv",invoiceNo);
        editor.commit();
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

//    @OnClick(R.id.btnAdd)
//    public void onBtnAddClicked() {
//        if (TextUtils.isEmpty(editTextProductCode.getText())) {
//            editTextProductCode.setError("Invalid Item");
//        } else {
//            editTextProductCode.setError(null);
//            double qty = ParseDouble(editTextQuantity.getText().toString());
//            if (qty < 1) {
//                editTextQuantity.setError("invalid quantity");
//            } else {
//                editTextQuantity.setError(null);
//                addToCart();
//            }
//        }
//    }

    @OnClick(R.id.imgSubmit)
    public void onImgSubmitClicked() {
        clearView();
        getDataFromVolley();
    }

//    @OnClick(R.id.btnNext)
//    public void onBtnNextClicked() {
//
//        clearView();
//        txtAddedBarcode.setText("");
////        txtAddedPrice.setText("0.0");
////        txtAddedQuantity.setText("0");
//
//        Intent intent = new Intent(SalesActivity.this, ViewCartActivity.class);
//        intent.putExtra("TYPE",type);
//        intent.putExtra("ACTION",DataContract.ACTION_NEW);
//        intent.putExtra("CUS_NAME", customerName);
//        intent.putExtra("CUS_CODE", customerCode);
//        intent.putExtra("SALESMAN_ID", salesmanId);
//        intent.putExtra("DOC_NO", invoiceNo);
//        intent.putExtra("DOC_DATE", invoiceDate);
//        startActivity(intent);
//    }

    @Override
    public void onComplete(String code) {
        clearView();
        editTextBarcode.setText(code);
    }

    @Override
    protected void onResume() {
        super.onResume();
        hideProgressBar();
        cart_count=Cart.mCart.size();
        Log.d(TAG, "onResume: "+cart_count);
        Log.d(TAG, "onResume invoice: "+invoiceNo);
        invalidateOptionsMenu();
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

                        double qty=ParseDouble(editTextUserInput.getText().toString());
                        if(qty>0){
                            editTextQuantity.setText(editTextUserInput.getText().toString());
                            //reset discount values
                            editTextDiscountPercentage.setText("0");
                            editTextDiscount.setText("0");
                        }else{
                            editTextQuantity.setText("0");
                            //reset discount values
                            editTextDiscountPercentage.setText("0");
                            editTextDiscount.setText("0");
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

                        double disAmount=ParseDouble(editTextUserInput.getText().toString());
                        double total = ParseDouble(editTextTotal.getText().toString());
                        if(disAmount>0 && total>0){
                            editTextDiscount.setText(editTextUserInput.getText().toString());
                            double discountPercentage=disAmount/total*100;
                            editTextDiscountPercentage.setText(String.format("%.2f",discountPercentage));
                        }else{
                            editTextDiscount.setText("0");
                            editTextDiscountPercentage.setText("0");
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

                        double total=ParseDouble(editTextTotal.getText().toString());
                        double discountPercentage=ParseDouble(editTextUserInput.getText().toString());
                        if(discountPercentage>0 && total>0){
                            editTextDiscountPercentage.setText(editTextUserInput.getText().toString());
                            double percentageDecimal = discountPercentage / 100;
                            double discountAmount = percentageDecimal * total;
                            editTextDiscount.setText(String.format("%.2f",discountAmount));

                        }else{

                           editTextDiscountPercentage.setText("0");
                           editTextDiscount.setText("0");
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


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.cart_toolbar, menu);
        itemCart = menu.findItem(R.id.action_cart);
        Log.d(TAG, "onCreateOptionsMenu: "+Converter.convertLayoutToImage(SalesActivity.this,cart_count,R.drawable.ic_shopping_cart_48dp));
        itemCart.setIcon(Converter.convertLayoutToImage(SalesActivity.this,cart_count,R.drawable.ic_shopping_cart_48dp));
        itemDelete= menu.findItem(R.id.action_delete);
        itemDelete.setVisible(false);
        itemAdd= menu.findItem(R.id.action_add);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection

        int id = item.getItemId();
        if(id==R.id.action_add){
            Log.d(TAG, "onOptionsItemSelected: add");
            if (TextUtils.isEmpty(editTextProductCode.getText())) {
                editTextProductCode.setError("Invalid Item");
            } else {
                editTextProductCode.setError(null);
                double qty = ParseDouble(editTextQuantity.getText().toString());
                if (qty < 1) {
                    editTextQuantity.setError("invalid quantity");
                } else {
                    editTextQuantity.setError(null);
                    addToCart();
                }
            }
        }else if(id==R.id.action_cart){
            Log.d(TAG, "onOptionsItemSelected: cart");
            clearView();
            txtAddedBarcode.setText("");
//        txtAddedPrice.setText("0.0");
//        txtAddedQuantity.setText("0");

            Intent intent = new Intent(SalesActivity.this, ViewCartActivity.class);
            intent.putExtra("TYPE",type);
            intent.putExtra("ACTION",DataContract.ACTION_NEW);
            intent.putExtra("CUS_NAME", customerName);
            intent.putExtra("CUS_CODE", customerCode);
            intent.putExtra("SALESMAN_ID", salesmanId);
            intent.putExtra("DOC_NO", invoiceNo);
            intent.putExtra("DOC_DATE", invoiceDate);
            startActivity(intent);
        }

        return super.onOptionsItemSelected(item);
    }
}
