package com.alhikmahpro.www.e_inventory.View;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
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
import com.alhikmahpro.www.e_inventory.Data.GoodsData;
import com.alhikmahpro.www.e_inventory.Data.ItemModel;
import com.alhikmahpro.www.e_inventory.Data.SaleData;
import com.alhikmahpro.www.e_inventory.Data.SaveGoods;
import com.alhikmahpro.www.e_inventory.Data.SaveSales;
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

    String customerName, invoiceNo, total, invoiceDate, salesmanId, mDate, customerCode, type, orderNo, Action;
    int docNo;
    double disc, netAmount, base_total,disc_per;

    volleyListener mVolleyListener;
    VolleyServiceGateway serviceGateway;
    ProgressDialog progressDialog;
    dbHelper helper;
    @BindView(R.id.textViewTotalRow)
    TextView textViewTotalRow;
    @BindView(R.id.btn_delete)
    Button btnDelete;
    @BindView(R.id.toolbar)
    Toolbar toolbar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment);
        ButterKnife.bind(this);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle("Payment");
        Intent intent = getIntent();
        Action = intent.getStringExtra("ACTION");
        type = intent.getStringExtra("TYPE");
        customerName = intent.getStringExtra("CUS_NAME");
        customerCode = intent.getStringExtra("CUS_CODE");
        invoiceDate = intent.getStringExtra("INV_DATE");
        Log.d(TAG, " payment activity onCreate: invoice date" + invoiceDate);
        invoiceNo = intent.getStringExtra("INV_NO");
        salesmanId = intent.getStringExtra("SALESMAN_ID");
        int count = intent.getIntExtra("TOTAL_ROW", 0);
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
        //SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
        //mDate = sdf.format(new Date());
        mDate = AppUtils.getDateAndTime();
        helper = new dbHelper(this);
        editTextDiscount.addTextChangedListener(amountWatcher);
        editTextDiscountPercentage.addTextChangedListener(percentageWatcher);
        initVolleyCallBack();

    }

    TextWatcher amountWatcher=new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void onTextChanged(CharSequence s, int i, int i1, int i2) {
            editTextDiscountPercentage.removeTextChangedListener(percentageWatcher);
            if (!s.toString().equals("")) {
                double discountPercentage = Math.round(Double.parseDouble(s.toString()) / base_total * 100);

                editTextDiscountPercentage.setText(String.format("%.2f",discountPercentage));
            }else{
                editTextDiscountPercentage.setText("");
            }
            editTextDiscountPercentage.addTextChangedListener(percentageWatcher);
            calculateNetValue();

        }

        @Override
        public void afterTextChanged(Editable editable) {

        }
    };
    TextWatcher percentageWatcher=new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void onTextChanged(CharSequence s, int i, int i1, int i2) {
            editTextDiscount.removeTextChangedListener(amountWatcher);
            if (!s.toString().equals("")) {

                double discountPercentage = Double.parseDouble(s.toString());
                double percentageDecimal = discountPercentage / 100;
                double discountAmount = percentageDecimal * base_total;
                editTextDiscount.setText(String.format("%.2f",discountAmount));
            }else {
                editTextDiscount.setText("");
            }
            editTextDiscount.addTextChangedListener(amountWatcher);
            calculateNetValue();

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
                    if (res.equals("success")) {
                        if (type.equals("GDS")) {
                            saveGoods(DataContract.SYNC_STATUS_OK);
                        } else {
                            saveSales(DataContract.SYNC_STATUS_OK);
                        }
                    } else {
                        Toast.makeText(PaymentActivity.this, res, Toast.LENGTH_SHORT).show();
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void notifyError(String requestType, VolleyError error) {
                Log.d(TAG, "notifyError: " + error);
                Toast.makeText(PaymentActivity.this, "Connection Error try again !", Toast.LENGTH_SHORT).show();
//                if (type.equals("GDS")) {
//                    // save goods receive sync failed
//                    saveGoods(DataContract.SYNC_STATUS_FAILED);
//
//                } else {
//                    // save invoice syn failed
//                    saveSales(DataContract.SYNC_STATUS_FAILED);
//                }


            }
        };

    }


    private JSONObject generateSalesJSON() {

        //create invoice json array

        JSONArray invoiceArray = new JSONArray();
        JSONObject invoiceObject = new JSONObject();
        try {
            invoiceObject.put(DataContract.Invoice.COL_INVOICE_NUMBER, invoiceNo);
            invoiceObject.put(DataContract.Invoice.COL_INVOICE_DATE, invoiceDate.substring(0, 10));
            invoiceObject.put(DataContract.Invoice.COL_CUSTOMER_CODE, customerCode);
            invoiceObject.put(DataContract.Invoice.COL_CUSTOMER_NAME, customerName);
            invoiceObject.put(DataContract.Invoice.COL_SALESMAN_ID, salesmanId);
            invoiceObject.put(DataContract.Invoice.COL_TOTAL_AMOUNT, base_total);
            invoiceObject.put(DataContract.Invoice.COL_DISCOUNT_AMOUNT, disc);
            invoiceObject.put(DataContract.Invoice.COL_DISCOUNT_PERCENTAGE,disc_per);
            invoiceObject.put(DataContract.Invoice.COL_NET_AMOUNT, netAmount);
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
            object.put(DataContract.GoodsReceive.COL_INVOICE_DATE, invoiceDate.substring(0, 10));
            object.put(DataContract.GoodsReceive.COL_STAFF_NAME, salesmanId);
            object.put(DataContract.GoodsReceive.COL_TOTAL, base_total);
            object.put(DataContract.GoodsReceive.COL_DISCOUNT_AMOUNT, disc);
            object.put(DataContract.GoodsReceive.COL_DISCOUNT_PERCENTAGE, disc_per);
            object.put(DataContract.GoodsReceive.COL_NET_AMOUNT, netAmount);
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


    private void gotoNext() {
        Log.d(TAG, "gotoNext: " + type);
        if (type.equals("SAL")) {
            Intent intent_print = new Intent(PaymentActivity.this, ViewPdfActivity.class);
            intent_print.putExtra("ACTION", DataContract.ACTION_NEW);
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
            Cart.gCart.clear();
            Intent intent = new Intent(getApplicationContext(), ListDocActivity.class);
            intent.putExtra("Type", type);
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


    public void saveSales(int sync) {
        Log.d(TAG, "saveSales: disc per "+disc_per);
        SaleData saleData = new SaleData(invoiceNo, customerCode, customerName, invoiceDate, salesmanId, base_total, disc,disc_per,netAmount, paymentMode, mDate, sync);
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
        saveSales.execute(saleData);


    }

    public void saveGoods(int sync) {
        GoodsData data = new GoodsData(docNo, orderNo, customerCode, customerName, invoiceNo, invoiceDate, salesmanId, base_total, disc, netAmount, paymentMode, mDate, sync);

        SaveGoods.TaskListener listener = new SaveGoods.TaskListener() {
            @Override
            public void onFinished(String result) {
                if (result.equals("Saved")) {
                    Log.d(TAG, "onFinished: " + result);
                    // Toast.makeText(PaymentActivity.this, "Saved", Toast.LENGTH_SHORT).show();
                    gotoNext();
                }

            }
        };

        SaveGoods saveGoods = new SaveGoods(this, listener);
        saveGoods.execute(data);

    }


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
                .setMessage("Do you want to delete the cart ?")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        if (type.equals("GDS")) {
                            if (Action.equals("EDIT")) {
                                helper.deleteGoodsById(docNo);
                            }
                            // finish all activity and go to home activity
                            Cart.gCart.clear();
                            Intent intent = new Intent(getApplicationContext(), ListDocActivity.class);
                            intent.putExtra("Type", "GDS");
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            startActivity(intent);

                        } else if (type.equals("SAL")) {
                            if (Action.equals("EDIT")) {
                                helper.deleteInvoiceById(invoiceNo);
                            }
                            Cart.mCart.clear();
                            Intent intent = new Intent(getApplicationContext(), ListSalesActivity.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            startActivity(intent);
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


    @OnClick(R.id.btn_pay)
    public void onBtnPayClicked() {
        //saveSales(DataContract.SYNC_STATUS_OK);
        try {
            disc_per = Double.valueOf(editTextDiscountPercentage.getText().toString());
        } catch (NumberFormatException e) {
            disc_per = 0;

        }

        JSONObject resultObject = new JSONObject();
        String url = "";
        if (type.equals("GDS")) {
            resultObject = generateGoodsJSON();
            url = "PriceChecker/goods_receive.php";
        } else if (type.equals("SAL")) {
            resultObject = generateSalesJSON();
            url = "PriceChecker/sales_invoice.php";
        }
        if (resultObject.length() > 0) {

            //send to volley
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

}
