package com.alhikmahpro.www.e_inventory.View;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.alhikmahpro.www.e_inventory.AppUtils;
import com.alhikmahpro.www.e_inventory.Data.Cart;
import com.alhikmahpro.www.e_inventory.Data.CartModel;
import com.alhikmahpro.www.e_inventory.Data.Converter;
import com.alhikmahpro.www.e_inventory.Data.DataContract;
import com.alhikmahpro.www.e_inventory.Data.GoodsData;
import com.alhikmahpro.www.e_inventory.Data.ItemModel;
import com.alhikmahpro.www.e_inventory.Data.SaleData;
import com.alhikmahpro.www.e_inventory.Data.SaveGoods;
import com.alhikmahpro.www.e_inventory.Data.SaveSales;
import com.alhikmahpro.www.e_inventory.Data.dbHelper;
import com.alhikmahpro.www.e_inventory.FileUtils;
import com.alhikmahpro.www.e_inventory.Interface.volleyListener;
import com.alhikmahpro.www.e_inventory.Network.VolleyServiceGateway;
import com.alhikmahpro.www.e_inventory.R;
import com.android.volley.AuthFailureError;
import com.android.volley.NetworkError;
import com.android.volley.NoConnectionError;
import com.android.volley.ParseError;
import com.android.volley.ServerError;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.text.DecimalFormat;
import java.text.NumberFormat;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class PaymentActivity extends AppCompatActivity {

    @BindView(R.id.textViewTotal)
    TextView textViewTotal;
    @BindView(R.id.textViewInvoice)
    TextView textViewInvoice;
    @BindView(R.id.textViewDate)
    TextView textViewDate;
    //    @BindView(R.id.textViewCustomer)
//    TextView textViewCustomer;
    @BindView(R.id.textViewSalesman)
    TextView textViewSalesman;
    @BindView(R.id.editTextDiscount)
    EditText editTextDiscount;
    @BindView(R.id.editTextDiscountPercentage)
    EditText editTextDiscountPercentage;
    @BindView(R.id.textViewNet)
    TextView textViewNet;
    @BindView(R.id.btn_pay)
    Button btnPay;
    @BindView(R.id.radioGroup)
    RadioGroup radioGroup;

    RadioButton radioButton;
    String paymentMode;
    private static final String TAG = "PaymentActivity";
    JSONObject resultObject;
    String url = "";
    MenuItem itemDelete, itemCart, itemAdd;


    String customerName, invoiceNo, total, invoiceDate, salesmanId, mDate, customerCode, type, orderNo,serverInvoice, Action;

    int docNo;
//    double totalValue=0;
//    double discAmount=0;
//    double discPers=0;
//    double netTotal=0;
//    double otherCharge=0;
//    double grandTotal=0;


    volleyListener mVolleyListener;
    VolleyServiceGateway serviceGateway;
    ProgressDialog progressDialog;
    dbHelper helper;
    //    @BindView(R.id.textViewTotalRow)
//    TextView textViewTotalRow;
    @BindView(R.id.btn_delete)
    Button btnDelete;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    private static int cart_count = 0;
    @BindView(R.id.editTextOtherAmount)
    EditText editTextOtherAmount;
    @BindView(R.id.textViewGrantTotal)
    TextView textViewGrantTotal;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment);
        ButterKnife.bind(this);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        Intent intent = getIntent();
        Action = intent.getStringExtra("ACTION");
        type = intent.getStringExtra("TYPE");
        customerName = intent.getStringExtra("CUS_NAME");
        customerCode = intent.getStringExtra("CUS_CODE");
        invoiceDate = intent.getStringExtra("INV_DATE");
        Log.d(TAG, " payment activity onCreate: CUS_CODE" + customerCode);
        invoiceNo = intent.getStringExtra("INV_NO");
        salesmanId = intent.getStringExtra("SALESMAN_ID");
        cart_count = intent.getIntExtra("TOTAL_ROW", 0);
        double total = intent.getDoubleExtra("TOTAL", 0);
        getSupportActionBar().setTitle(customerName);
        invoiceDate = invoiceDate.substring(0, 10);
        resultObject = new JSONObject();
//        netAmount = base_total;
//        grandTotal=base_total;

        if (type.equals("GDS")) {
            orderNo = intent.getStringExtra("ORD_NO");
            docNo = intent.getIntExtra("DOC_NO", 0);
            textViewInvoice.setText(String.valueOf(docNo));
        } else {
            textViewInvoice.setText(invoiceNo);
        }

        //textViewCustomer.setText(customerName);
        textViewSalesman.setText(salesmanId);
        textViewNet.setText(String.format("%.2f", total));
        textViewNet.setText(String.format("%.2f", total));
        textViewTotal.setText(String.format("%.2f", total));
        textViewGrantTotal.setText(String.format("%.2f", total));
        //textViewTotalRow.setText(String.valueOf(count));
        textViewDate.setText(invoiceDate);


        int radioId = radioGroup.getCheckedRadioButtonId();
        radioButton = findViewById(radioId);
        paymentMode = radioButton.getText().toString();
        //SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
        //mDate = sdf.format(new Date());
        mDate = AppUtils.getDateAndTime();
        helper = new dbHelper(this);
        editTextDiscount.addTextChangedListener(amountWatcher);
        editTextDiscountPercentage.addTextChangedListener(percentageWatcher);
        //editTextOtherAmount.addTextChangedListener(otherAmountWatcher);
        editTextOtherAmount.setFocusable(false);
        editTextOtherAmount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG, "onClick: ");
                otherAmountCustomDialog(editTextOtherAmount.getText().toString());
            }
        });
        initVolleyCallBack();
        calculateValues();

    }

    TextWatcher amountWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void onTextChanged(CharSequence s, int i, int i1, int i2) {
            editTextDiscountPercentage.removeTextChangedListener(percentageWatcher);
            if (!s.toString().equals("")) {
                double discountAmount = ParseDouble(s.toString());
                double baseTotal = ParseDouble(textViewTotal.getText().toString());
                if (discountAmount > 0 && baseTotal > 0) {
                    double discountPercentage = discountAmount / baseTotal * 100;
                    editTextDiscountPercentage.setText(String.format("%.2f", discountPercentage));
                }

            } else {
                editTextDiscountPercentage.setText("");
            }
            editTextDiscountPercentage.addTextChangedListener(percentageWatcher);
            calculateValues();

        }

        @Override
        public void afterTextChanged(Editable editable) {

        }
    };


    TextWatcher percentageWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void onTextChanged(CharSequence s, int i, int i1, int i2) {
            editTextDiscount.removeTextChangedListener(amountWatcher);
            if (!s.toString().equals("")) {
                double discountPercentage = ParseDouble(s.toString());
                double baseTotal = ParseDouble(textViewTotal.getText().toString());
                if (discountPercentage > 0 && baseTotal > 0) {
                    double percentageDecimal = discountPercentage / 100;
                    double discountAmount = percentageDecimal * baseTotal;
                    editTextDiscount.setText(String.format("%.2f", discountAmount));
                }

            } else {
                editTextDiscount.setText("");
            }
            editTextDiscount.addTextChangedListener(amountWatcher);

            calculateValues();

        }

        @Override
        public void afterTextChanged(Editable editable) {

        }
    };


    private void initVolleyCallBack() {

        //server response

        mVolleyListener = new volleyListener() {
            @Override
            public void notifySuccess(String requestType, JSONObject response) {
                try {
                    String res = response.getString("Status");
                    long id = 0;
                    Log.d(TAG, "notifySuccess: " + res);
                    if (!res.equals("failed")) {
                        if (type.equals("GDS")) {
                            serverInvoice = res;
                            id = saveGoods(DataContract.SYNC_STATUS_OK);
                        } else if (type.equals("SAL")) {
                            serverInvoice = res;
                            id = saveSales(DataContract.SYNC_STATUS_OK);
                        } else if (type.equals("ORD")) {
                            serverInvoice = res;
                            id = saveOrders(DataContract.SYNC_STATUS_OK);
                        }
                    } else {
                        generateLog("Failed To Save Server");
                        handleError("Failed To Save Server");
                    }

                    if (id > 0) {
                        gotoNext();
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void notifyError(String requestType, VolleyError error) {
                Log.d(TAG, "notifyError: " + error);


                String errorDescription = "Error";

                if (error instanceof TimeoutError || error instanceof NoConnectionError) {
                    // Toast.makeText(PaymentActivity.this, "Network Time Out", Toast.LENGTH_SHORT).show();
                    errorDescription = "Network Time Out";
                } else if (error instanceof AuthFailureError) {
                    //TODO
                    // Toast.makeText(PaymentActivity.this, "AuthFailure Error", Toast.LENGTH_SHORT).show();
                    errorDescription = "AuthFailure Error";
                } else if (error instanceof ServerError) {
                    //TODO
                    // Toast.makeText(PaymentActivity.this, "Server Error", Toast.LENGTH_SHORT).show();
                    errorDescription = "Server Error";
                } else if (error instanceof NetworkError) {
                    //TODO
                    // Toast.makeText(PaymentActivity.this, "NetWork Error", Toast.LENGTH_SHORT).show();
                    errorDescription = "NetWork Error";
                } else if (error instanceof ParseError) {
                    //TODO
                    // Toast.makeText(PaymentActivity.this, "Parse Error", Toast.LENGTH_SHORT).show();
                    errorDescription = "Parse Error";
                }
                generateLog(errorDescription);
                handleError(errorDescription);


            }
        };

    }

    private void handleError(String errorDescription) {

        new AlertDialog.Builder(PaymentActivity.this)
                .setTitle("Server Error")
                .setMessage(errorDescription + "! Do you want to cancel or retry?")
                .setPositiveButton("Retry", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (resultObject.length() > 0) {
                            sendToServer();
                        }
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override

                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                        if (type.equals("GDS")) {
                            cancelGoods();
                        } else if (type.equals("SAL")) {
                            cancelSale();
                        } else if (type.equals("ORD")) {
                            cancelOrder();
                        }

                    }
                }).create().show();
    }


    private JSONObject generateSalesJSON() {

        //create invoice json array

        JSONArray invoiceArray = new JSONArray();
        JSONObject invoiceObject = new JSONObject();
        try {
            invoiceObject.put(DataContract.Invoice.COL_INVOICE_NUMBER, invoiceNo);
            invoiceObject.put(DataContract.Invoice.COL_INVOICE_DATE, invoiceDate);
            invoiceObject.put(DataContract.Invoice.COL_CUSTOMER_CODE, customerCode);
            invoiceObject.put(DataContract.Invoice.COL_CUSTOMER_NAME, customerName);
            invoiceObject.put(DataContract.Invoice.COL_SALESMAN_ID, salesmanId);
            invoiceObject.put(DataContract.Invoice.COL_TOTAL_AMOUNT, ParseDouble(textViewTotal.getText().toString()));
            invoiceObject.put(DataContract.Invoice.COL_DISCOUNT_AMOUNT, ParseDouble(editTextDiscount.getText().toString()));
            invoiceObject.put(DataContract.Invoice.COL_DISCOUNT_PERCENTAGE, ParseDouble(editTextDiscountPercentage.getText().toString()));
            invoiceObject.put(DataContract.Invoice.COL_OTHER_AMOUNT, ParseDouble(editTextOtherAmount.getText().toString()));
            invoiceObject.put(DataContract.Invoice.COL_NET_AMOUNT, ParseDouble(textViewGrantTotal.getText().toString()));
            invoiceObject.put(DataContract.Invoice.COL_PAYMENT_TYPE, paymentMode);

            invoiceArray.put(invoiceObject);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        //create invoiceDetails json array
        JSONArray invoiceDetailsArray = new JSONArray();
        int slNo = 0;

        for (CartModel cartModel : Cart.mCart) {
            slNo++;

            JSONObject detailsObject = new JSONObject();
            try {
                detailsObject.put("serial_no", slNo);
                detailsObject.put(DataContract.InvoiceDetails.COL_INVOICE_NUMBER, invoiceNo);
                detailsObject.put(DataContract.InvoiceDetails.COL_BAR_CODE, cartModel.getBarcode());
                detailsObject.put(DataContract.InvoiceDetails.COL_PRODUCT_CODE, cartModel.getProductCode());
                detailsObject.put(DataContract.InvoiceDetails.COL_PRODUCT_NAME, cartModel.getProductName());
                detailsObject.put(DataContract.InvoiceDetails.COL_QUANTITY, cartModel.getQty());
                detailsObject.put(DataContract.InvoiceDetails.COL_UNIT1, cartModel.getUnit1());
                detailsObject.put(DataContract.InvoiceDetails.COL_UNIT2, cartModel.getUnit2());
                detailsObject.put(DataContract.InvoiceDetails.COL_UNIT3, cartModel.getUnit3());
                detailsObject.put(DataContract.InvoiceDetails.COL_UNIT, cartModel.getSelectedUnit());
                detailsObject.put(DataContract.InvoiceDetails.COL_UN_QTY1, cartModel.getUnit1Qty());
                detailsObject.put(DataContract.InvoiceDetails.COL_UN_QTY2, cartModel.getUnit2Qty());
                detailsObject.put(DataContract.InvoiceDetails.COL_UN_QTY3, cartModel.getUnit3Qty());
                detailsObject.put(DataContract.InvoiceDetails.COL_RATE, cartModel.getRate());
                detailsObject.put(DataContract.InvoiceDetails.COL_DISCOUNT, cartModel.getDiscount());
                detailsObject.put(DataContract.InvoiceDetails.COL_DISCOUNT_PERCENTAGE, cartModel.getDiscPercentage());
                detailsObject.put(DataContract.InvoiceDetails.COL_NET_AMOUNT, cartModel.getNet());
                detailsObject.put(DataContract.InvoiceDetails.COL_SALE_TYPE, cartModel.getSaleType());
                invoiceDetailsArray.put(detailsObject);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        Log.d(TAG, "generateSalesJSON: invoice details JSON" + invoiceDetailsArray);
        JSONObject result = new JSONObject();

        try {
            result.put("Invoice", invoiceArray);
            result.put("Details", invoiceDetailsArray);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return result;

    }

    private JSONObject generateGoodsJSON() {

        //Add goodsItems into json array
        JSONArray goodsArray = new JSONArray();
        JSONObject object = new JSONObject();
        try {


            object.put(DataContract.GoodsReceive.COL_DOCUMENT_NUMBER, docNo);
            object.put(DataContract.GoodsReceive.COL_ORDER_NUMBER, orderNo);
            object.put(DataContract.GoodsReceive.COL_SUPPLIER_CODE, customerCode);
            object.put(DataContract.GoodsReceive.COL_INVOICE_NUMBER, invoiceNo);
            object.put(DataContract.GoodsReceive.COL_INVOICE_DATE, invoiceDate);
            object.put(DataContract.GoodsReceive.COL_STAFF_NAME, salesmanId);
            object.put(DataContract.GoodsReceive.COL_TOTAL, ParseDouble(textViewTotal.getText().toString()));
            object.put(DataContract.GoodsReceive.COL_DISCOUNT_AMOUNT, ParseDouble(editTextOtherAmount.getText().toString()));
            object.put(DataContract.GoodsReceive.COL_DISCOUNT_PERCENTAGE, ParseDouble(editTextDiscountPercentage.getText().toString()));
            object.put(DataContract.GoodsReceive.COL_OTHER_AMOUNT, ParseDouble(editTextOtherAmount.getText().toString()));
            object.put(DataContract.GoodsReceive.COL_NET_AMOUNT, ParseDouble(textViewGrantTotal.getText().toString()));
            object.put(DataContract.GoodsReceive.COL_PAYMENT_TYPE, paymentMode);
            object.put(DataContract.GoodsReceive.COL_DATE_TIME, mDate.substring(0, 10));
            goodsArray.put(object);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        //Add goodsITemDetails into json array

        JSONArray goodsDetailsArray = new JSONArray();
        int slNo = 0;
        for (ItemModel model : Cart.gCart) {
            slNo++;
            Log.d(TAG, "listitem: " + model.getUnit1());
            JSONObject jsonObject = new JSONObject();
            try {
                jsonObject.put("serial_no", slNo);
                jsonObject.put("doc_no", model.getDocNo());
                jsonObject.put("bar_code", model.getBarCode());
                jsonObject.put("product_code", model.getProductCode());
                jsonObject.put("product_name", model.getProductName());
                jsonObject.put("quantity", model.getQty());
                jsonObject.put("free_quantity", model.getFreeQty());
                jsonObject.put("rate", model.getRate());
                jsonObject.put("discount", model.getDiscount());
                jsonObject.put("discount_percentage", model.getDiscountPercentage());
                //jsonObject.put("discount_percentage", model.getD
                jsonObject.put("net_value", model.getNet());
                jsonObject.put("um1", model.getUnit1());
                jsonObject.put("um2", model.getUnit2());
                jsonObject.put("um3", model.getUnit3());
                jsonObject.put("selected_unit", model.getSelectedPackage());
                jsonObject.put("selected_free_unit", model.getSelectedFreePackage());
                jsonObject.put("um1_qty", model.getUnit1Qty());
                jsonObject.put("um2_qty", model.getUnit2Qty());
                jsonObject.put("um3_qty", model.getUnit3Qty());
                goodsDetailsArray.put(jsonObject);

            } catch (JSONException e) {
                e.printStackTrace();
            }

        }

        JSONObject result = new JSONObject();
        try {
            result.put("Goods", goodsArray);
            result.put("Details", goodsDetailsArray);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return result;
    }

    private void saveToLocal() {

    }

    private void gotoNext() {
        Log.d(TAG, "gotoNext: " + type);
        if (type.equals("SAL")) {
            Intent intent_print = new Intent(PaymentActivity.this, ViewPdfActivity.class);
            intent_print.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            intent_print.putExtra("ACTION", DataContract.ACTION_NEW);
            intent_print.putExtra("CUS_NAME", customerName);
            intent_print.putExtra("CUS_CODE", customerCode);
            intent_print.putExtra("SALESMAN_ID", salesmanId);
            intent_print.putExtra("DOC_NO", invoiceNo);
            intent_print.putExtra("DOC_DATE", invoiceDate);
            intent_print.putExtra("DISCOUNT", ParseDouble(editTextDiscount.getText().toString()));
            intent_print.putExtra("TOTAL", ParseDouble(textViewTotal.getText().toString()));
            intent_print.putExtra("OTHER", ParseDouble(editTextOtherAmount.getText().toString()));
            intent_print.putExtra("NET", ParseDouble(textViewGrantTotal.getText().toString()));
            intent_print.putExtra("PAY_MOD", paymentMode);
            intent_print.putExtra("SERVER_INV", serverInvoice);
            startActivity(intent_print);
        } else if (type.equals("ORD")) {
            Intent intent_print = new Intent(PaymentActivity.this, ViewOrderPdfActivity.class);
            intent_print.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            intent_print.putExtra("ACTION", DataContract.ACTION_NEW);
            intent_print.putExtra("CUS_NAME", customerName);
            intent_print.putExtra("CUS_CODE", customerCode);
            intent_print.putExtra("SALESMAN_ID", salesmanId);
            intent_print.putExtra("DOC_NO", invoiceNo);
            intent_print.putExtra("DOC_DATE", invoiceDate);
            intent_print.putExtra("DISCOUNT", ParseDouble(editTextDiscount.getText().toString()));
            intent_print.putExtra("TOTAL", ParseDouble(textViewTotal.getText().toString()));
            intent_print.putExtra("OTHER", ParseDouble(editTextOtherAmount.getText().toString()));
            intent_print.putExtra("NET", ParseDouble(textViewGrantTotal.getText().toString()));
            intent_print.putExtra("PAY_MOD", paymentMode);
            intent_print.putExtra("SERVER_INV", serverInvoice);
            startActivity(intent_print);
        } else {
            // finish all activity and go to home activity

            Cart.gCart.clear();
            SharedPreferences sharedPreferences = getSharedPreferences("Goods", 0);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.remove("Doc");
            editor.clear();
            editor.apply();
            Intent intent = new Intent(getApplicationContext(), ListDocActivity.class);
            intent.putExtra("Type", type);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
        }


    }

    private void calculateValues() {
        double totalValue = ParseDouble(textViewTotal.getText().toString());
        double discAmount = ParseDouble(editTextDiscount.getText().toString());
        //double discPers=ParseDouble(editTextDiscountPercentage.getText().toString());
        //double netTotal=ParseDouble(textViewNet.getText().toString());
        double otherCharge = ParseDouble(editTextOtherAmount.getText().toString());
        //double grandTotal=ParseDouble(textViewGrantTotal.getText().toString());

        double netTotal = totalValue - discAmount;
        double grandTotal = netTotal + otherCharge;

        //netTotal=Math.floor(netTotal*100)/100;
        //grandTotal=Math.floor(grandTotal * 100) / 100;
        textViewNet.setText(String.format("%.2f", netTotal));
        textViewGrantTotal.setText(String.format("%.2f", grandTotal));


    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }


    public long saveSales(int sync) {
        long id = 0;

        if (helper.checkInvoice(invoiceNo)) {
            id = helper.updateInvoice(invoiceNo, invoiceDate, salesmanId, customerCode, customerName,
                    ParseDouble(textViewTotal.getText().toString()), ParseDouble(editTextDiscount.getText().toString()),
                    ParseDouble(editTextDiscountPercentage.getText().toString()), ParseDouble(textViewNet.getText().toString()),
                    ParseDouble(editTextOtherAmount.getText().toString()), ParseDouble(textViewGrantTotal.getText().toString()),
                    paymentMode, mDate, serverInvoice, sync);


        } else {
            id = helper.saveInvoice(invoiceNo, invoiceDate, salesmanId, customerCode, customerName,
                    ParseDouble(textViewTotal.getText().toString()), ParseDouble(editTextDiscount.getText().toString()),
                    ParseDouble(editTextDiscountPercentage.getText().toString()), ParseDouble(textViewNet.getText().toString()),
                    ParseDouble(editTextOtherAmount.getText().toString()), ParseDouble(textViewGrantTotal.getText().toString()),
                    paymentMode, mDate, serverInvoice, sync);
            helper.incrementAutoId(DataContract.AutoIdGenerator.COL_INVOICE_TABLE);

        }

        //check inserted successfully
        return id;






      /*  SaleData saleData = new SaleData(invoiceNo, customerCode, customerName, invoiceDate,
                salesmanId, base_total, disc, disc_per, netAmount,otherAmount,grandTotal,
                paymentMode, mDate,serverInvoice, sync);
        SaveSales.TaskListener listener = new SaveSales.TaskListener() {
            @Override
            public void onFinished(String result) {
                if (result.equals("Saved")) {
                    Log.d(TAG, "onFinished: " + result);
                    // Toast.makeText(PaymentActivity.this, "Saved", Toast.LENGTH_SHORT).show();
                    gotoNext();
                }
            }
        };
        SaveSales saveSales = new SaveSales(this, listener);
        saveSales.execute(saleData);*/


    }

    public long saveOrders(int sync) {
        long id = 0;
        if (helper.checkOrder(invoiceNo)) {
            id = helper.updateOrder(invoiceNo, invoiceDate, salesmanId, customerCode, customerName,
                    ParseDouble(textViewTotal.getText().toString()), ParseDouble(editTextDiscount.getText().toString()),
                    ParseDouble(editTextDiscountPercentage.getText().toString()), ParseDouble(textViewNet.getText().toString()),
                    ParseDouble(editTextOtherAmount.getText().toString()), ParseDouble(textViewGrantTotal.getText().toString()),
                    paymentMode, mDate, serverInvoice, sync);

        } else {
            id = helper.saveOrder(invoiceNo, invoiceDate, salesmanId, customerCode, customerName,
                    ParseDouble(textViewTotal.getText().toString()), ParseDouble(editTextDiscount.getText().toString()),
                    ParseDouble(editTextDiscountPercentage.getText().toString()), ParseDouble(textViewNet.getText().toString()),
                    ParseDouble(editTextOtherAmount.getText().toString()), ParseDouble(textViewGrantTotal.getText().toString()),
                    paymentMode, mDate, serverInvoice, sync);
            helper.incrementAutoId(DataContract.AutoIdGenerator.COL_ORDER_TABLE);
        }

        return id;

    }

    public long saveGoods(int sync) {
        Log.d(TAG, "saveGoods: " + serverInvoice);
        long id = 0;
        if (helper.checkGoods(docNo)) {
            id = helper.updateGoods(docNo, orderNo, customerCode, customerName, invoiceNo, invoiceDate, salesmanId,
                    ParseDouble(editTextOtherAmount.getText().toString()),
                    ParseDouble(editTextDiscount.getText().toString()),
                    ParseDouble(textViewNet.getText().toString()),
                    ParseDouble(editTextOtherAmount.getText().toString()),
                    ParseDouble(textViewGrantTotal.getText().toString()), paymentMode, mDate, serverInvoice, sync);


        } else {
            id = helper.saveGoods(docNo, orderNo, customerCode, customerName, invoiceNo, invoiceDate, salesmanId,
                    ParseDouble(editTextOtherAmount.getText().toString()),
                    ParseDouble(editTextDiscount.getText().toString()),
                    ParseDouble(textViewNet.getText().toString()),
                    ParseDouble(editTextOtherAmount.getText().toString()),
                    ParseDouble(textViewGrantTotal.getText().toString()), paymentMode, mDate, serverInvoice, sync);
            helper.incrementAutoId(DataContract.AutoIdGenerator.COL_GOODS_TABLE);

        }

        return id;

    }

//        GoodsData data = new GoodsData(docNo, orderNo, customerCode, customerName,
//                invoiceNo, invoiceDate, salesmanId, base_total, disc, netAmount,otherAmount,grandTotal,
//                paymentMode,mDate,serverInvoice,sync);


//        SaveGoods.TaskListener listener = new SaveGoods.TaskListener() {
//            @Override
//            public void onFinished(String result) {
//                if (result.equals("Saved")) {
//                    Log.d(TAG, "onFinished: " + result);
//                    // Toast.makeText(PaymentActivity.this, "Saved", Toast.LENGTH_SHORT).show();
//                    gotoNext();
//                }
//
//            }
//        };
//        SaveGoods saveGoods = new SaveGoods(this, listener);
//        saveGoods.execute(data);


    public String currencyFormatter(double val) {
        NumberFormat format = NumberFormat.getCurrencyInstance();
        String pattern = ((DecimalFormat) format).toPattern();
        String newPattern = pattern.replace("\u00A4", "").trim();
        NumberFormat newFormat = new DecimalFormat(newPattern);
        return String.valueOf(newFormat.format(val));

    }


    public void onRadioButtonClicked(View view) {

        int radioId = radioGroup.getCheckedRadioButtonId();
        Log.d(TAG, "onRadioButtonClicked: " + radioId);
        radioButton = findViewById(radioId);
        paymentMode = radioButton.getText().toString();
        Log.d(TAG, "onRadioButtonClicked: " + paymentMode);
    }


    @OnClick(R.id.btn_delete)
    public void onBtnDeleteClicked() {

        new AlertDialog.Builder(PaymentActivity.this)
                .setTitle("Confirm")
                .setMessage("Do you want to cancel ?")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (type.equals("GDS")) {
                            cancelGoods();
                        } else if (type.equals("SAL")) {
                            cancelSale();
                        } else if (type.equals("ORD")) {
                            cancelOrder();
                        }
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override

                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                }).create().show();


    }

    private void cancelOrder() {
        if (Action.equals("EDIT")) {
            helper.deleteOrderByInvoiceNo(invoiceNo);
        }
        helper.deleteOrderDetailsByInvoiceNo(invoiceNo);
        Cart.mCart.clear();
        SharedPreferences sharedPreferences = getSharedPreferences("Invoice", 0);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.remove("Inv");
        editor.clear();
        editor.apply();
        Intent intent = new Intent(getApplicationContext(), ListSalesActivity.class);
        intent.putExtra("Type", "ORD");
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }

    private void cancelSale() {
        if (Action.equals("EDIT")) {
            helper.deleteInvoiceByInvoiceNo(invoiceNo);
        }
        helper.deleteInvoiceDetailsByInvoiceNo(invoiceNo);
        Cart.mCart.clear();
        SharedPreferences sharedPreferences = getSharedPreferences("Invoice", 0);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.remove("Inv");
        editor.clear();
        editor.apply();
        Intent intent = new Intent(getApplicationContext(), ListSalesActivity.class);
        intent.putExtra("Type", "SAL");
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }

    private void cancelGoods() {
        if (Action.equals("EDIT")) {
            helper.deleteGoodsById(docNo);
        }
        // finish all activity and go to home activity

        helper.deleteGoodsDetailsByDoc(docNo);
        Cart.gCart.clear();
        SharedPreferences sharedPreferences = getSharedPreferences("Goods", 0);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.remove("Doc");
        editor.clear();
        editor.apply();
        Intent intent = new Intent(getApplicationContext(), ListDocActivity.class);
        intent.putExtra("Type", "GDS");
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }


    @OnClick(R.id.btn_pay)
    public void onBtnPayClicked() {

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        String chooseOption=sharedPreferences.getString(getString(R.string.key_sync_type),"live_sync");
        if (chooseOption.equals("live_sync")) {
            sendToServer();
        } else {
            long id = 0;
            if (type.equals("GDS")) {
                serverInvoice = "NA";
                id = saveGoods(DataContract.SYNC_STATUS_FAILED);
            } else if (type.equals("SAL")) {
                serverInvoice = "NA";
                id = saveSales(DataContract.SYNC_STATUS_FAILED);
            } else if (type.equals("ORD")) {
                serverInvoice = "NA";
                id = saveOrders(DataContract.SYNC_STATUS_FAILED);
            }
            if (id > 0) {
                gotoNext();
            }
        }
    }

    private void sendToServer() {
        //send to volley
        if (type.equals("GDS")) {
            resultObject = generateGoodsJSON();
            url = "PriceChecker/goods_receive.php";
        } else if (type.equals("SAL")) {
            resultObject = generateSalesJSON();
            url = "PriceChecker/sales_invoice.php";
        } else if (type.equals("ORD")) {
            resultObject = generateSalesJSON();
            url = "PriceChecker/sales_order.php";
        }
        if (resultObject.length() > 0) {
            View view = this.getCurrentFocus();
            AppUtils.hideKeyboard(this, view);

            if (!AppUtils.isNetworkAvailable(this)) {
                Toast.makeText(this, "No Internet ", Toast.LENGTH_SHORT).show();

            } else {
                // if internet available send to server
                serviceGateway = new VolleyServiceGateway(mVolleyListener, this);
                serviceGateway.postDataVolley("POSTCALL", url, resultObject);
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.cart_toolbar, menu);
        itemCart = menu.findItem(R.id.action_cart);
        Log.d(TAG, "onCreateOptionsMenu: " + Converter.convertLayoutToImage(PaymentActivity.this, cart_count, R.drawable.ic_shopping_cart));
        itemCart.setIcon(Converter.convertLayoutToImage(PaymentActivity.this, cart_count, R.drawable.ic_shopping_cart));
        itemDelete = menu.findItem(R.id.action_delete);
        itemDelete.setVisible(false);
        itemAdd = menu.findItem(R.id.action_add);
        itemAdd.setVisible(false);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection

        int id = item.getItemId();
        if (id == R.id.action_delete) {
            //deleteCart();
        }

        return super.onOptionsItemSelected(item);
    }

    double ParseDouble(String strNumber) {
        if (strNumber != null && strNumber.length() > 0) {
            try {
                return Double.parseDouble(strNumber);
            } catch (Exception e) {
                return 0;   // or some value to mark this field is wrong. or make a function validates field first ...
            }
        } else return 0;
    }

    public void generateLog(String error) {
        Log.d(TAG, "generateLog: ");
        try {
            String path = FileUtils.getSubDirPath(this, DataContract.DIR_LOGS);
            String header = "/**PaymentActivity**/";
            String fileName = "Logs.txt";
            File file = new File(path, fileName);
            FileWriter writer = new FileWriter(file);
            writer.append(header);
            writer.append("\n");
            writer.append(AppUtils.getDateAndTime());
            writer.append("\n");
            writer.append("Request");
            writer.append("\n");
            writer.append("Response");
            writer.append("\n");
            writer.append(resultObject.toString());
            writer.append(error);
            writer.append("****************End*******");
            writer.flush();
            writer.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void otherAmountCustomDialog(String value) {

        LayoutInflater layoutInflater = LayoutInflater.from(this);
        View dialogView = layoutInflater.inflate(R.layout.custom_dialog, null);
        android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(this);
        builder.setView(dialogView);

        final EditText editTextUserInput = (EditText) dialogView.findViewById(R.id.editTextDialogUserInput);
        final TextView textView = (TextView) dialogView.findViewById(R.id.textViewTitle);
        editTextUserInput.setText(value);
        editTextUserInput.setSelection(editTextUserInput.getText().length());
        textView.setText("Enter the amount");

        builder.setCancelable(false)
                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int i) {
                        InputMethodManager im = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                        im.hideSoftInputFromWindow(editTextUserInput.getWindowToken(), 0);
                        //editTextOtherAmount.setText(editTextUserInput.getText().toString());
                        double otherAmt = ParseDouble(editTextUserInput.getText().toString());
                        if (otherAmt > 0) {
                            editTextOtherAmount.setText(editTextUserInput.getText().toString());
                        } else {
                            editTextOtherAmount.setText("0");
                        }
                        calculateValues();
                        //calculate total
                        //double total=(grandTotal+ParseDouble(editTextOtherAmount.getText().toString()));
                        // textViewGrantTotal.setText(String.valueOf(total));

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
        android.support.v7.app.AlertDialog alertDialog = builder.create();
        alertDialog.show();


    }
}
