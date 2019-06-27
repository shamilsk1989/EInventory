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

    String customerName, invoiceNo, total, invoiceDate, salesmanId, mDate, customerCode;
    double disc, netAmount, base_total;
    volleyListener mVolleyListener;
    VolleyServiceGateway serviceGateway;
    ProgressDialog progressDialog;
    dbHelper helper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment);
        ButterKnife.bind(this);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle("Payment");
        Intent intent = getIntent();
        customerName = intent.getStringExtra("CUS_NAME");
        customerCode = intent.getStringExtra("CUS_CODE");
        invoiceNo = intent.getStringExtra("DOC_NO");
        invoiceDate = intent.getStringExtra("DOC_DATE");
        base_total = intent.getDoubleExtra("TOTAL", 0);
        netAmount=base_total;
        salesmanId = intent.getStringExtra("SALESMAN_ID");
        int radioId = radioGroup.getCheckedRadioButtonId();
        radioButton = findViewById(radioId);
        paymentMode = radioButton.getText().toString();

        Log.d(TAG, "onCreate: invoice number: " + invoiceNo);

        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
        //invoiceDate = sdf.format(new Date());


        textViewCustomer.setText(customerName);
        textViewSalesman.setText(salesmanId);
        textViewInvoice.setText(invoiceNo);
        textViewNet.setText(String.valueOf(netAmount));
        textViewTotal.setText(String.valueOf(base_total));
        textViewDate.setText(invoiceDate);
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
                    String res = response.getString("Status");
                    if (res.equals("Success")) {
                        helper.updateInvoiceSync(invoiceNo);
                        Toast.makeText(PaymentActivity.this, "Sync successfully", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(PaymentActivity.this, "Sync failed", Toast.LENGTH_SHORT).show();
                    }


                } catch (JSONException e) {
                    e.printStackTrace();
                }


            }

            @Override
            public void notifyError(String requestType, VolleyError error) {
                Toast.makeText(PaymentActivity.this, "Network error try again later!", Toast.LENGTH_SHORT).show();
            }
        };

    }

    private void saveToLocalDataBase(int syncStatus) {

        boolean res = helper.saveInvoice(invoiceNo, invoiceDate, salesmanId, customerCode,customerName, base_total, disc, netAmount, paymentMode, invoiceDate, syncStatus);
        if (res) {
            boolean res1 = helper.saveInvoiceDetails(invoiceNo, syncStatus);
            if (res1) {
                Toast.makeText(this, "Invoice Saved", Toast.LENGTH_SHORT).show();
                //postDataToServer();
                gotoNext();
            }
        }
    }

    private void gotoNext() {
        Log.d(TAG, "gotoNext: " + paymentMode);

        Intent intent_print = new Intent(PaymentActivity.this, PrintViewActivity.class);
        intent_print.putExtra("TYPE","new");
        intent_print.putExtra("CUS_NAME", customerName);
        intent_print.putExtra("CUS_CODE", customerCode);
        intent_print.putExtra("DISCOUNT", disc);
        intent_print.putExtra("SALESMAN_ID", salesmanId);
        intent_print.putExtra("DOC_NO", invoiceNo);
        intent_print.putExtra("DOC_DATE", invoiceDate);
        intent_print.putExtra("TOTAL",base_total);
        intent_print.putExtra("NET",netAmount);
        intent_print.putExtra("PAY_MOD", paymentMode);
        startActivity(intent_print);

        // finish all activity and go to home activity
//        Intent intent = new Intent(getApplicationContext(), HomeActivity.class);
//        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//        startActivity(intent);

    }

    private void calculateNetValue() {
        try {
            disc = Double.valueOf(editTextDiscount.getText().toString());
        } catch (NumberFormatException e) {
            disc = 0;

        }
        netAmount = base_total - disc;
        Log.d(TAG, "calculateNetValue: "+netAmount);
        textViewNet.setText(currencyFormatter(netAmount));

    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @OnClick(R.id.btn_pay)
    public void onViewClicked() {
        saveToLocalDataBase(DataContract.SYNC_STATUS_FAILED);
    }


    public void postDataToServer() {
        //send data to server

        //create invoice json array

        JSONArray invoiceArray = new JSONArray();
        JSONObject invoiceObject = new JSONObject();
        try {
            invoiceObject.put(DataContract.Invoice.COL_INVOICE_NUMBER, invoiceNo);
            invoiceObject.put(DataContract.Invoice.COL_INVOICE_DATE, mDate);
            invoiceObject.put(DataContract.Invoice.COL_CUSTOMER_CODE, customerName);
            invoiceObject.put(DataContract.Invoice.COL_SALESMAN_ID, salesmanId);
            invoiceObject.put(DataContract.Invoice.COL_TOTAL_AMOUNT, Double.valueOf(total));
            invoiceObject.put(DataContract.Invoice.COL_DISCOUNT_AMOUNT, disc);
            invoiceObject.put(DataContract.Invoice.COL_NET_AMOUNT, netAmount);
            invoiceObject.put(DataContract.Invoice.COL_PAYMENT_TYPE, paymentMode);

            invoiceArray.put(invoiceObject);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        //create invoiceDetails json array
        JSONArray invoiceDetailsArray = new JSONArray();

        for (CartModel cartModel : Cart.mCart) {

            JSONObject detailsObject = new JSONObject();
            try {
                detailsObject.put(DataContract.InvoiceDetails.COL_INVOICE_NUMBER, invoiceNo);
                detailsObject.put(DataContract.InvoiceDetails.COL_BAR_CODE, cartModel.getBarcode());
                detailsObject.put(DataContract.InvoiceDetails.COL_PRODUCT_CODE, cartModel.getProductCode());
                detailsObject.put(DataContract.InvoiceDetails.COL_PRODUCT_NAME, cartModel.getProductName());
                detailsObject.put(DataContract.InvoiceDetails.COL_QUANTITY, cartModel.getQty());
                detailsObject.put(DataContract.InvoiceDetails.COL_UNIT, cartModel.getSelectedUnit());
                detailsObject.put(DataContract.InvoiceDetails.COL_RATE, cartModel.getRate());
                detailsObject.put(DataContract.InvoiceDetails.COL_DISCOUNT, cartModel.getDiscount());
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
        View view = this.getCurrentFocus();
        AppUtils.hideKeyboard(this, view);
        progressDialog = AppUtils.showProgressDialog(this, "Loading....");
        serviceGateway = new VolleyServiceGateway(mVolleyListener, this);
        serviceGateway.postDataVolley("POSTCALL", "PriceChecker/goods_receive.php", result);
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
