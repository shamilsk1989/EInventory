package com.alhikmahpro.www.e_inventory.View;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.alhikmahpro.www.e_inventory.AppUtils;
import com.alhikmahpro.www.e_inventory.Data.Cart;
import com.alhikmahpro.www.e_inventory.Data.CartModel;
import com.alhikmahpro.www.e_inventory.Data.DataContract;
import com.alhikmahpro.www.e_inventory.Data.ItemModel;
import com.alhikmahpro.www.e_inventory.Data.dbHelper;
import com.alhikmahpro.www.e_inventory.Interface.volleyListener;
import com.alhikmahpro.www.e_inventory.Network.VolleyServiceGateway;
import com.alhikmahpro.www.e_inventory.R;
import com.android.volley.VolleyError;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

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
    @BindView(R.id.textViewCustomer)
    TextView textViewCustomer;
    @BindView(R.id.textViewSalesman)
    TextView textViewSalesman;
    @BindView(R.id.editTextDiscount)
    EditText editTextDiscount;
    @BindView(R.id.textViewNet)
    TextView textViewNet;
    @BindView(R.id.btn_pay)
    Button btnPay;
    @BindView(R.id.radioGroup)
    RadioGroup radioGroup;

    RadioButton radioButton;
    String paymentMode;
    private static final String TAG = "PaymentActivity";

    String customerName, invoiceNo, total, invoiceDate, salesmanId, mDate, customerCode, type, orderNo;
    int docNo;
    double disc, netAmount, base_total;
    volleyListener mVolleyListener;
    VolleyServiceGateway serviceGateway;
    ProgressDialog progressDialog;
    dbHelper helper;
    @BindView(R.id.textViewTotalRow)
    TextView textViewTotalRow;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment);
        ButterKnife.bind(this);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle("Payment");
        Intent intent = getIntent();
        type = intent.getStringExtra("TYPE");
        customerName = intent.getStringExtra("CUS_NAME");
        customerCode = intent.getStringExtra("CUS_CODE");
        invoiceDate = intent.getStringExtra("INV_DATE");
        invoiceNo = intent.getStringExtra("INV_NO");
        salesmanId = intent.getStringExtra("SALESMAN_ID");
        int count=intent.getIntExtra("TOTAL_ROW",0);
        base_total = intent.getDoubleExtra("TOTAL", 0);
        netAmount = base_total;

        if (type.equals("GDS")) {
            orderNo = intent.getStringExtra("ORD_NO");
            docNo = intent.getIntExtra("DOC_NO", 0);
            textViewInvoice.setText(String.valueOf(docNo));
        } else {
            textViewInvoice.setText(invoiceNo);
        }

        textViewCustomer.setText(customerName);
        textViewSalesman.setText(salesmanId);
        textViewNet.setText(String.valueOf(netAmount));
        textViewTotal.setText(String.valueOf(base_total));
        textViewTotalRow.setText(String.valueOf(count));
        textViewDate.setText(invoiceDate);


        int radioId = radioGroup.getCheckedRadioButtonId();
        radioButton = findViewById(radioId);
        paymentMode = radioButton.getText().toString();
        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
        mDate = sdf.format(new Date());
        helper = new dbHelper(this);

        editTextDiscount.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {

                if (editTextDiscount.getText().toString().isEmpty()) {
                    editTextDiscount.setText("0");
                }

                calculateNetValue();

            }
        });
        initVolleyCallBack();

    }

    private void initVolleyCallBack() {

        mVolleyListener = new volleyListener() {
            @Override
            public void notifySuccess(String requestType, JSONObject response) {
                try {
                    Log.d(TAG, "notifySuccess: "+response);
                    String res = response.getString("Status");
                    if (res.equals("success")) {
                        if(type.equals("GDS")){

                            // update goods receive
                            helper.updateGoodsSync(docNo);
                        }else {// update invoice details
                            helper.updateInvoiceSync(invoiceNo);
                        }

                        Toast.makeText(PaymentActivity.this, "Sync successfully", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(PaymentActivity.this, "Sync failed", Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    Toast.makeText(PaymentActivity.this, "Unexpected response", Toast.LENGTH_SHORT).show();
                    e.printStackTrace();
                }
            }

            @Override
            public void notifyError(String requestType, VolleyError error) {
                Log.d(TAG, "notifyError: "+error);
                Toast.makeText(PaymentActivity.this, "Network error try again later!", Toast.LENGTH_SHORT).show();
            }
        };

    }

    // save goods receive and goods receive details in to local db
    private void saveGoods(int syncStatus) {
        boolean res = helper.saveGoods(docNo, orderNo, customerCode,customerName, invoiceNo, invoiceDate, salesmanId, base_total, disc, netAmount, paymentMode, mDate, syncStatus);
        if (res) {
            if (helper.saveGoodsDetails1(invoiceNo, syncStatus)) {
                generateGoodsJSON();
            }
        }
    }


    //save sales and sales details into local db

    private void saveSales(int syncStatus) {
        boolean res = helper.saveInvoice(invoiceNo, invoiceDate, salesmanId, customerCode, customerName, base_total, disc, netAmount, paymentMode, mDate, syncStatus);
        if (res) {
            if (helper.saveInvoiceDetails(invoiceNo, syncStatus)) {
                generateSalesJSON();
            }
        }
    }

    private void generateSalesJSON() {

        //create invoice json array

        JSONArray invoiceArray = new JSONArray();
        JSONObject invoiceObject = new JSONObject();
        try {
            invoiceObject.put(DataContract.Invoice.COL_INVOICE_NUMBER, invoiceNo);
            invoiceObject.put(DataContract.Invoice.COL_INVOICE_DATE, mDate);
            invoiceObject.put(DataContract.Invoice.COL_CUSTOMER_CODE, customerCode);
            invoiceObject.put(DataContract.Invoice.COL_CUSTOMER_NAME, customerName);
            invoiceObject.put(DataContract.Invoice.COL_SALESMAN_ID, salesmanId);
            invoiceObject.put(DataContract.Invoice.COL_TOTAL_AMOUNT,base_total);
            invoiceObject.put(DataContract.Invoice.COL_DISCOUNT_AMOUNT, disc);
            invoiceObject.put(DataContract.Invoice.COL_NET_AMOUNT, netAmount);
            invoiceObject.put(DataContract.Invoice.COL_PAYMENT_TYPE, paymentMode);

            invoiceArray.put(invoiceObject);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        //create invoiceDetails json array
        JSONArray invoiceDetailsArray = new JSONArray();
        int slNo=0;

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
                detailsObject.put(DataContract.InvoiceDetails.COL_NET_AMOUNT, cartModel.getNet());



                detailsObject.put(DataContract.InvoiceDetails.COL_NET_AMOUNT, cartModel.getNet());
                detailsObject.put(DataContract.InvoiceDetails.COL_NET_AMOUNT, cartModel.getNet());
                detailsObject.put(DataContract.InvoiceDetails.COL_SALE_TYPE, cartModel.getSaleType());




                invoiceDetailsArray.put(detailsObject);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        JSONObject result = new JSONObject();

        try {
            result.put("Invoice", invoiceArray);
            result.put("Details", invoiceDetailsArray);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        String url = "PriceChecker/sales_invoice.php";
        sendToVolley(result, url);

    }

    private void generateGoodsJSON() {

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
            object.put(DataContract.GoodsReceive.COL_TOTAL,base_total);
            object.put(DataContract.GoodsReceive.COL_DISCOUNT_AMOUNT, disc);
            object.put(DataContract.GoodsReceive.COL_NET_AMOUNT, netAmount);
            object.put(DataContract.GoodsReceive.COL_PAYMENT_TYPE, paymentMode);
            object.put(DataContract.GoodsReceive.COL_DATE_TIME, mDate);
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
        String url = "PriceChecker/goods_receive.php";
        sendToVolley(result, url);
    }


    private void sendToVolley(JSONObject object, String url) {

        //send to volley
        View view = this.getCurrentFocus();
        AppUtils.hideKeyboard(this, view);
        progressDialog = AppUtils.showProgressDialog(this, "Loading....");
        serviceGateway = new VolleyServiceGateway(mVolleyListener, this);
        serviceGateway.postDataVolley("POSTCALL", url, object);

    }


    private void gotoNext() {
        Log.d(TAG, "gotoNext: "+type);
        if (type.equals("SAL")) {
            Intent intent_print = new Intent(PaymentActivity.this, PrintViewActivity.class);
            intent_print.putExtra("TYPE", "new");
            intent_print.putExtra("CUS_NAME", customerName);
            intent_print.putExtra("CUS_CODE", customerCode);
            intent_print.putExtra("DISCOUNT", disc);
            intent_print.putExtra("SALESMAN_ID", salesmanId);
            intent_print.putExtra("DOC_NO", invoiceNo);
            intent_print.putExtra("DOC_DATE", invoiceDate);
            intent_print.putExtra("TOTAL", base_total);
            intent_print.putExtra("NET", netAmount);
            intent_print.putExtra("PAY_MOD", paymentMode);
            startActivity(intent_print);
        } else {
            // finish all activity and go to home activity
            Intent intent = new Intent(getApplicationContext(), InvoiceActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
        }


    }

    private void calculateNetValue() {
        try {
            disc = Double.valueOf(editTextDiscount.getText().toString());
        } catch (NumberFormatException e) {
            disc = 0;

        }
        netAmount = base_total - disc;
        Log.d(TAG, "calculateNetValue: " + netAmount);
        textViewNet.setText(currencyFormatter(netAmount));

    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @OnClick(R.id.btn_pay)
    public void onViewClicked() {
        if (type.equals("GDS"))
            saveGoods(DataContract.SYNC_STATUS_FAILED);
        else
            saveSales(DataContract.SYNC_STATUS_FAILED);

        gotoNext();

    }

    public String currencyFormatter(double val) {
        NumberFormat format = NumberFormat.getCurrencyInstance();
        String pattern = ((DecimalFormat) format).toPattern();
        String newPattern = pattern.replace("\u00A4", "").trim();
        NumberFormat newFormat = new DecimalFormat(newPattern);
        return String.valueOf(newFormat.format(val));

    }

    public void onRadioButtonClicked(View view) {
        paymentMode = radioButton.getText().toString();
        Log.d(TAG, "onRadioButtonClicked: " + paymentMode);
    }
}
