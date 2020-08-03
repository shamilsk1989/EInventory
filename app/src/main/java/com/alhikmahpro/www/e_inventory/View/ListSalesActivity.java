package com.alhikmahpro.www.e_inventory.View;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.FileProvider;
import android.support.v4.view.ViewCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.alhikmahpro.www.e_inventory.Adapter.SalesListAdapter;
import com.alhikmahpro.www.e_inventory.AppUtils;
import com.alhikmahpro.www.e_inventory.Data.Cart;
import com.alhikmahpro.www.e_inventory.Data.CartModel;
import com.alhikmahpro.www.e_inventory.Data.ClearData;
import com.alhikmahpro.www.e_inventory.Data.CreatePdf;
import com.alhikmahpro.www.e_inventory.Data.DataContract;
import com.alhikmahpro.www.e_inventory.Data.GenerateSalesJson;
import com.alhikmahpro.www.e_inventory.Data.ItemModel;
import com.alhikmahpro.www.e_inventory.Data.dbHelper;
import com.alhikmahpro.www.e_inventory.FileUtils;
import com.alhikmahpro.www.e_inventory.Interface.OnListAdapterClickListener;
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
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class ListSalesActivity extends AppCompatActivity {

    @BindView(R.id.doc_list_rv)
    RecyclerView docListRv;
    //    @BindView(R.id.txtEmpty)
//    TextView txtEmpty;
    @BindView(R.id.fab)
    FloatingActionButton fab;
    List<ItemModel> list = new ArrayList<>();
    RecyclerView.LayoutManager layoutManager;
    SalesListAdapter adapter;
    private static final String TAG = "ListSalesActivity";
    String type,salesmanId;
    private static final int PERMISSION_CODE = 100;
    String invoiceNo = "";
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    MenuItem itemShare, itemClear, itemPrint, itemSync;
    dbHelper helper;
    volleyListener mVolleyListener;
    VolleyServiceGateway serviceGateway;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_sales);
        ButterKnife.bind(this);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        Intent intent = getIntent();
        helper = new dbHelper(ListSalesActivity.this);
        type = intent.getStringExtra("Type");
        if (type.equals("SAL")) {
            getSupportActionBar().setTitle("Sales");
        } else if (type.equals("ORD")) {
            getSupportActionBar().setTitle("Order");
        }
        SharedPreferences sharedPreferences= PreferenceManager.getDefaultSharedPreferences(this);
        salesmanId=sharedPreferences.getString("key_employee","0");
        populateRecycler();
        initVolleyCallBack();

        docListRv.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (dy > 0 && fab.getVisibility() == View.VISIBLE) {
                    fab.hide();
                } else if (dy < 0 && fab.getVisibility() != View.VISIBLE) {
                    fab.show();
                }
            }
        });
    }

    private void populateRecycler() {
        Log.d(TAG, "populateRecycler: " + list.size());
        if (list.size() > 0) {
            list.clear();
        }

        if (type.equals("SAL")) {
            list = loadSales();
        } else if (type.equals("ORD")) {
            list = loadOrders();
        }

        if (list.size() > 0) {

            layoutManager = new LinearLayoutManager(this);
            docListRv.setLayoutManager(layoutManager);
            docListRv.setItemAnimator(new DefaultItemAnimator());
            docListRv.setHasFixedSize(true);
            adapter = new SalesListAdapter(this, list, new OnListAdapterClickListener() {
                @Override
                public void OnEditClicked(int position) {
                    goToNext(position);
                }

                @Override
                public void OnSyncClicked(int position) {

                }

                @Override
                public void OnShareClicked(int position) {
                    Log.d(TAG, "OnShareClicked: ");
                    ItemModel itemModel = list.get(position);
                    invoiceNo = itemModel.getInvoiceNo();
                    goToNext(position);
                }

                @Override
                public void OnDeleteClicked(int position) {

                }
            });

            docListRv.setAdapter(adapter);
            ViewCompat.setNestedScrollingEnabled(docListRv, false);
        } else {
            // display recycler empty
        }
    }


    private List<ItemModel> loadSales() {
        List<ItemModel> saleList;
        saleList = helper.getAllInvoice();
        return saleList;
    }

    private List<ItemModel> loadOrders() {
        List<ItemModel> orderList;
        orderList = helper.getAllOrders();
        return orderList;
    }


    private void goToNext(int position) {
        Cart.mCart.clear();
        ItemModel itemModel = list.get(position);
        invoiceNo = itemModel.getInvoiceNo();
        Log.d(TAG, "goToNext: invoice no" + invoiceNo);
        if (type.equals("SAL")) {
            //get invoice details and add to the cart
            helper.getInvoiceDetailsById(invoiceNo);
            Log.d(TAG, "goToNext:cart size " + Cart.mCart.size());

            Intent intent = new Intent(ListSalesActivity.this, ViewPdfActivity.class);
            intent.putExtra("ACTION", DataContract.ACTION_EDIT);
            intent.putExtra("TYPE", type);
            intent.putExtra("CUS_NAME", itemModel.getCustomerName());
            intent.putExtra("CUS_CODE", itemModel.getCustomerCode());
            intent.putExtra("DISCOUNT", itemModel.getDiscount());
            intent.putExtra("SALESMAN_ID", itemModel.getStaffName());
            intent.putExtra("DOC_NO", itemModel.getInvoiceNo());
            intent.putExtra("DOC_DATE", itemModel.getInvoiceDate());
            intent.putExtra("TOTAL", itemModel.getTotal());
            intent.putExtra("OTHER", itemModel.getOtherAmount());
            intent.putExtra("NET", itemModel.getNet());
            intent.putExtra("PAY_MOD", itemModel.getPaymentType());
            intent.putExtra("SERVER_INV", itemModel.getServerInvoice());
            startActivity(intent);
        } else if (type.equals("ORD")) {

            Intent intent = new Intent(ListSalesActivity.this, ViewOrderPdfActivity.class);
            intent.putExtra("ACTION", DataContract.ACTION_EDIT);
            intent.putExtra("TYPE", type);
            intent.putExtra("CUS_NAME", itemModel.getCustomerName());
            intent.putExtra("CUS_CODE", itemModel.getCustomerCode());
            intent.putExtra("DISCOUNT", itemModel.getDiscount());
            intent.putExtra("SALESMAN_ID", itemModel.getStaffName());
            intent.putExtra("DOC_NO", itemModel.getInvoiceNo());
            intent.putExtra("DOC_DATE", itemModel.getInvoiceDate());
            intent.putExtra("TOTAL", itemModel.getTotal());
            intent.putExtra("OTHER", itemModel.getOtherAmount());
            intent.putExtra("NET", itemModel.getNet());
            intent.putExtra("PAY_MOD", itemModel.getPaymentType());
            intent.putExtra("SERVER_INV", itemModel.getServerInvoice());
            startActivity(intent);
        }


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.tool_bar, menu);
        itemPrint = menu.findItem(R.id.action_print);
        itemSync = menu.findItem(R.id.action_sync);
        itemShare = menu.findItem(R.id.action_share);
        itemClear = menu.findItem(R.id.action_clear);

         itemSync.setVisible(false);
        itemPrint.setVisible(false);
        itemShare.setVisible(false);


        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection

        int id = item.getItemId();
        if (id == R.id.action_clear) {
            alertDialog();
        } else if (id == R.id.action_sync) {
            if(type.equals("SAL")){
                String url="PriceChecker/sales_invoice.php";
                Log.d(TAG, "onOptionsItemSelected: sync called");
                GenerateSalesJson.TaskListener taskListener= result -> {
                    if(result.length()>0){
                        Log.d(TAG, "onOptionsItemSelected: "+result);
                        if (!AppUtils.isNetworkAvailable(this)) {
                            Toast.makeText(this, "No Internet ", Toast.LENGTH_SHORT).show();

                        } else {
                            // if internet available send to server
                            serviceGateway = new VolleyServiceGateway(mVolleyListener, this);
                            serviceGateway.postDataVolley("POSTCALL", url, result);
                        }
                    }

                };
                GenerateSalesJson salesJson=new GenerateSalesJson(this,taskListener);
                salesJson.execute();
            }

        }
        return super.onOptionsItemSelected(item);
    }

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

                        } else if (type.equals("SAL")) {
                            id=helper.updateAllInvoiceSync(DataContract.SYNC_STATUS_OK,res);

                        } else if (type.equals("ORD")) {

                        }
                        populateRecycler();
                    } else {
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
                    errorDescription = "Network Time Out";
                } else if (error instanceof AuthFailureError) {
                    errorDescription = "AuthFailure Error";
                } else if (error instanceof ServerError) {
                    errorDescription = "Server Error";
                } else if (error instanceof NetworkError) {
                    errorDescription = "NetWork Error";
                } else if (error instanceof ParseError) {
                    errorDescription = "Parse Error";
                }
            }
        };

    }
    private JSONObject generateSyncSaleData() {
        JSONObject result = new JSONObject();
        List<ItemModel> unSyncSaleList;
        int slNo = 0;
        unSyncSaleList = helper.getAllUnSyncInvoice();
        if (unSyncSaleList.size() > 0) {
            JSONArray invoiceArray = new JSONArray();
            JSONArray detailsArray = new JSONArray();
            for (ItemModel model : unSyncSaleList) {
                JSONObject invoiceObject = new JSONObject();
                try {
                    invoiceObject.put(DataContract.Invoice.COL_INVOICE_NUMBER, model.getInvoiceNo());
                    invoiceObject.put(DataContract.Invoice.COL_INVOICE_DATE, model.getInvoiceDate());
                    invoiceObject.put(DataContract.Invoice.COL_CUSTOMER_CODE, model.getCustomerCode());
                    invoiceObject.put(DataContract.Invoice.COL_CUSTOMER_NAME, model.getCustomerName());
                    invoiceObject.put(DataContract.Invoice.COL_SALESMAN_ID, salesmanId);
                    invoiceObject.put(DataContract.Invoice.COL_TOTAL_AMOUNT, model.getTotal());
                    invoiceObject.put(DataContract.Invoice.COL_DISCOUNT_AMOUNT, model.getDiscount());
                    invoiceObject.put(DataContract.Invoice.COL_DISCOUNT_PERCENTAGE, model.getDiscountPercentage());
                    invoiceObject.put(DataContract.Invoice.COL_OTHER_AMOUNT, model.getOtherAmount());
                    invoiceObject.put(DataContract.Invoice.COL_NET_AMOUNT,model.getNet());
                    invoiceObject.put(DataContract.Invoice.COL_PAYMENT_TYPE, model.getPaymentType());
                    invoiceArray.put(invoiceObject);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            for (ItemModel model : unSyncSaleList) {
                String inv = model.getInvoiceNo();
                helper.getInvoiceDetailsById(inv);
                if (Cart.mCart.size() > 0) {
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
                            detailsArray.put(detailsObject);

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }

                try {
                    result.put("Invoice", invoiceArray);
                    result.put("Details", detailsArray);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
        return result;

    }


    private void alertDialog() {
        new android.app.AlertDialog.Builder(ListSalesActivity.this)
                .setTitle("Warning")
                .setMessage("Do you want to delete all items!")
                .setPositiveButton("Yes", (dialog, which) -> {
                    clearSales();
                })
                .setNegativeButton("No", (dialog, which) -> dialog.cancel()).create().show();
    }

    private void clearSales() {

        ClearData clearData = new ClearData(ListSalesActivity.this);
        clearData.execute(type);
        populateRecycler();

    }

    @Override
    protected void onResume() {
        super.onResume();
        populateRecycler();

    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @OnClick(R.id.fab)
    public void onViewClicked() {
        Intent intent = new Intent(ListSalesActivity.this, CheckCustomerActivity.class);
        intent.putExtra("TYPE", type);
        startActivity(intent);
    }


}


