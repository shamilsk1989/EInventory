package com.alhikmahpro.www.e_inventory.View;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.alhikmahpro.www.e_inventory.Adapter.CartAdapter;
import com.alhikmahpro.www.e_inventory.AppUtils;
import com.alhikmahpro.www.e_inventory.Data.Cart;
import com.alhikmahpro.www.e_inventory.Data.DataContract;
import com.alhikmahpro.www.e_inventory.Data.ItemModel;
import com.alhikmahpro.www.e_inventory.Data.SessionHandler;
import com.alhikmahpro.www.e_inventory.Interface.volleyListener;
import com.alhikmahpro.www.e_inventory.Network.VolleyServiceGateway;
import com.alhikmahpro.www.e_inventory.Network.VolleySingleton;
import com.alhikmahpro.www.e_inventory.Data.dbHelper;
import com.alhikmahpro.www.e_inventory.Interface.OnAdapterClickListener;
import com.alhikmahpro.www.e_inventory.R;
import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkError;
import com.android.volley.NoConnectionError;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.ServerError;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class GoodsItemListActivity extends AppCompatActivity {

    @BindView(R.id.txtTotalCount)
    TextView txtTotalCount;
    @BindView(R.id.txtTotal)
    TextView txtTotal;
    @BindView(R.id.item_list_rv)
    RecyclerView itemListRv;
    @BindView(R.id.txtEmpty)
    TextView txtEmpty;
    @BindView(R.id.btnSave)
    Button btnSave;
    int docNo;
    String supplierName, supplierCode, invoiceNo, invoiceDate, user, orderNo,mDate;
    double totalAmount;
    ProgressDialog progressDialog;
    ConnectivityManager connectivityManager;
    RecyclerView.Adapter adapter;
    RecyclerView.LayoutManager layoutManager;
    List<ItemModel> listItem = new ArrayList<>();
    private static final String TAG = "GoodsItemListActivity";
    dbHelper helper;
    String companyCode, BASE_URL, deviceId, locationCode, periodCode, branchCode;
    volleyListener mVolleyListener;
    VolleyServiceGateway serviceGateway;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_goods_item_list);
        ButterKnife.bind(this);
        progressDialog = new ProgressDialog(this);
        connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        Intent mIntent = getIntent();
        docNo = mIntent.getIntExtra("DOC_NO", 0);
        orderNo = mIntent.getStringExtra("ORD_NO");
        supplierCode = mIntent.getStringExtra("SUPP_CODE");
        invoiceNo = mIntent.getStringExtra("INV_NO");
        user = mIntent.getStringExtra("USER");
        invoiceDate = mIntent.getStringExtra("INV_DATE");
        mDate=AppUtils.getDateAndTime();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle("Document  " + docNo);
        helper = new dbHelper(this);
        initView();
        initVolleyCallBack();

    }

    private void initVolleyCallBack() {

        mVolleyListener = new volleyListener() {
            @Override
            public void notifySuccess(String requestType, JSONObject response) {
                progressDialog.dismiss();
                String res = "";
                try {
                    res = response.getString("Status");
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                if (res.equals("success")) {
                    helper.updateGoodsSync(docNo);
                    showAlert("Sync Success");
                } else {
                    showAlert("Sync failed please try again later !");

                }


            }

            @Override
            public void notifyError(String requestType, VolleyError error) {

                showAlert("Network error please try again later !");

            }
        };
    }

    private void showAlert(String message) {

        AlertDialog.Builder builder = new AlertDialog.Builder(GoodsItemListActivity.this);
        builder.setTitle("Response");
        builder.setMessage(message);
        builder.setCancelable(false);
        builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                dialog.cancel();

                gotoNext();
            }
        }).create().show();


    }

    private void initView() {
//        SQLiteDatabase database = helper.getReadableDatabase();
//        Log.d(TAG, "initView: " + docNo);
//        Cursor cursor = helper.getGoodsDetailsByDoc(database, docNo);
//        Log.d(TAG, "initView: cursor " + cursor.getCount());
//        if (cursor.moveToFirst()) {
//            do {
//                ItemModel itemModel = new ItemModel();
//                itemModel.set_id(cursor.getInt(cursor.getColumnIndex(DataContract.GoodsReceiveDetails.COL_ID)));
//                itemModel.setDocNo(cursor.getInt(cursor.getColumnIndex(DataContract.GoodsReceiveDetails.COL_DOCUMENT_NUMBER)));
//                itemModel.setBarCode(cursor.getString(cursor.getColumnIndex(DataContract.GoodsReceiveDetails.COL_BAR_CODE)));
//                itemModel.setProductCode(cursor.getString(cursor.getColumnIndex(DataContract.GoodsReceiveDetails.COL_PRODUCT_CODE)));
//                itemModel.setProductName(cursor.getString(cursor.getColumnIndex(DataContract.GoodsReceiveDetails.COL_PRODUCT_NAME)));
//                itemModel.setSelectedPackage(cursor.getString(cursor.getColumnIndex(DataContract.GoodsReceiveDetails.COL_UNIT)));
//                itemModel.setSelectedFreePackage(cursor.getString(cursor.getColumnIndex(DataContract.GoodsReceiveDetails.COL_FREE_UNIT)));
//                itemModel.setQty(cursor.getDouble(cursor.getColumnIndex(DataContract.GoodsReceiveDetails.COL_QUANTITY)));
//                itemModel.setFreeQty(cursor.getDouble(cursor.getColumnIndex(DataContract.GoodsReceiveDetails.COL_FREE_QUANTITY)));
//                itemModel.setRate(cursor.getDouble(cursor.getColumnIndex(DataContract.GoodsReceiveDetails.COL_RATE)));
//                itemModel.setDiscount(cursor.getDouble(cursor.getColumnIndex(DataContract.GoodsReceiveDetails.COL_DISCOUNT)));
//                itemModel.setCostPrice(cursor.getDouble(cursor.getColumnIndex(DataContract.GoodsReceiveDetails.COL_COST_PRICE)));
//                itemModel.setSalePrice(cursor.getDouble(cursor.getColumnIndex(DataContract.GoodsReceiveDetails.COL_SALES_PRICE)));
//                itemModel.setStock(cursor.getString(cursor.getColumnIndex(DataContract.GoodsReceiveDetails.COL_STOCK)));
//                itemModel.setNet(cursor.getDouble(cursor.getColumnIndex(DataContract.GoodsReceiveDetails.COL_NET_VALUE)));
//                itemModel.setUnit1(cursor.getString(cursor.getColumnIndex(DataContract.GoodsReceiveDetails.COL_UNIT1)));
//                itemModel.setUnit2(cursor.getString(cursor.getColumnIndex(DataContract.GoodsReceiveDetails.COL_UNIT2)));
//                itemModel.setUnit3(cursor.getString(cursor.getColumnIndex(DataContract.GoodsReceiveDetails.COL_UNIT3)));
//                itemModel.setUnit1Qty(cursor.getInt(cursor.getColumnIndex(DataContract.GoodsReceiveDetails.COL_UN_QTY1)));
//                itemModel.setUnit2Qty(cursor.getInt(cursor.getColumnIndex(DataContract.GoodsReceiveDetails.COL_UN_QTY2)));
//                itemModel.setUnit3Qty(cursor.getInt(cursor.getColumnIndex(DataContract.GoodsReceiveDetails.COL_UN_QTY3)));
//
//                listItem.add(itemModel);
//            } while (cursor.moveToNext());
//        }
//        cursor.close();
//        database.close();
//
//        if (listItem.size() > 0) {
//            // hide empty text view
//            txtEmpty.setVisibility(View.GONE);
//            //calculate total number of items and amounts in cart
//
//            txtTotalCount.setText(String.valueOf(listItem.size()));
//            double sum = 0;
//            for (ItemModel itemModel : listItem) {
//                sum = sum + itemModel.getNet();
//
//            }
//            txtTotal.setText(String.valueOf(sum));
//
//            layoutManager = new LinearLayoutManager(this);
//            itemListRv.setLayoutManager(layoutManager);
//            itemListRv.setItemAnimator(new DefaultItemAnimator());
//            itemListRv.setHasFixedSize(true);
//            adapter = new CartAdapter(listItem, new OnAdapterClickListener() {
//                @Override
//                public void OnItemClicked(int position) {
//
//                }
//
//                @Override
//                public void OnDeleteClicked(int position) {
//
//                    ItemModel itemModel = listItem.get(position);
//                    int id = itemModel.get_id();
//                    double net = itemModel.getNet();
//                    if (helper.deleteGoodsDetailsById(id)) {
//                        listItem.remove(position);
//                        adapter.notifyItemRemoved(position);
//                        adapter.notifyItemRangeChanged(position, listItem.size());
//                        //set sum and count
//                        txtTotalCount.setText(String.valueOf(listItem.size()));
//                        double sum = Double.valueOf(txtTotal.getText().toString());
//                        sum = sum - net;
//                        txtTotal.setText(String.valueOf(sum));
//                        Toast.makeText(getApplication(), "Deleted", Toast.LENGTH_SHORT).show();
//                    } else {
//                        Toast.makeText(getApplication(), "Deletion Failed", Toast.LENGTH_SHORT).show();
//                    }
//                }
//            });
//            itemListRv.setAdapter(adapter);
//        } else {
//            txtTotal.setVisibility(View.GONE);
//            txtTotalCount.setVisibility(View.GONE);
//        }
        //**********************  updates*****************************

        if (Cart.gCart.size() > 0) {
            //if cart not empty hide empty textView

            txtEmpty.setVisibility(View.GONE);
            // set total count and total amount in cart
            txtTotalCount.setText(String.valueOf(Cart.gCart.size()));
            totalAmount = 0;
            for (ItemModel itemModel : Cart.gCart) {
                Log.d(TAG, "initView: cart amount "+itemModel.getNet());
                totalAmount = totalAmount + itemModel.getNet();
                Log.d(TAG, "initView: cart amount "+totalAmount);

            }
            txtTotal.setText(String.valueOf(totalAmount));
            //Load recycler view

            layoutManager = new LinearLayoutManager(this);
            itemListRv.setLayoutManager(layoutManager);
            itemListRv.setItemAnimator(new DefaultItemAnimator());
            itemListRv.setHasFixedSize(true);
            adapter = new CartAdapter(Cart.gCart, new OnAdapterClickListener() {
                @Override
                public void OnItemClicked(int position) {
                }

                @Override
                public void OnDeleteClicked(final int position) {

                    // Remove item from cart
                    new android.app.AlertDialog.Builder(GoodsItemListActivity.this)
                            .setTitle("Confirm")
                            .setMessage("Do you want to delete this item?")
                            .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {

                                    ItemModel itemModel = Cart.gCart.get(position);
                                    double amount = itemModel.getNet();
                                    Cart.gCart.remove(position);
                                    adapter.notifyItemRemoved(position);
                                    adapter.notifyItemRangeChanged(position, Cart.gCart.size());
                                    calculateNet(amount);

                                }
                            })
                            .setNegativeButton("No", new DialogInterface.OnClickListener() {
                                @Override

                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.cancel();
                                }
                            }).create().show();
                }
            });
            itemListRv.setAdapter(adapter);
        } else {
            txtTotal.setVisibility(View.GONE);
            txtTotalCount.setVisibility(View.GONE);
        }

        // ***************end updates*****************************

    }

    //Calculate total number of items and total amount of cart
    private void calculateNet(double amount){

        txtTotalCount.setText(String.valueOf(Cart.gCart.size()));
        totalAmount = totalAmount-amount;
        txtTotal.setText(String.valueOf(totalAmount));
    }

    @OnClick(R.id.btnSave)
    public void onViewClicked() {

        saveToLocalDataBase(DataContract.SYNC_STATUS_FAILED);

//        Log.d(TAG, "onViewClicked:  array size" + listItem.size());
//        JSONArray goodsDetailsArray = new JSONArray();
//        int slNo = 0;
//        for (ItemModel model : listItem) {
//            slNo++;
//            Log.d(TAG, "listitem: " + model.getUnit1());
//            JSONObject jsonObject = new JSONObject();
//            try {
//                jsonObject.put("serial_no", slNo);
//                jsonObject.put("doc_no", model.getDocNo());
//                jsonObject.put("bar_code", model.getBarCode());
//                jsonObject.put("product_code", model.getProductCode());
//                jsonObject.put("product_name", model.getProductName());
//                jsonObject.put("quantity", model.getQty());
//                jsonObject.put("free_quantity", model.getFreeQty());
//                jsonObject.put("rate", model.getRate());
//                jsonObject.put("discount", model.getDiscount());
//                jsonObject.put("net_value", model.getNet());
//                jsonObject.put("um1", model.getUnit1());
//                jsonObject.put("um2", model.getUnit2());
//                jsonObject.put("um3", model.getUnit3());
//                jsonObject.put("selected_unit", model.getSelectedPackage());
//                jsonObject.put("selected_free_unit", model.getSelectedFreePackage());
//                jsonObject.put("um1_qty", model.getUnit1Qty());
//                jsonObject.put("um2_qty", model.getUnit2Qty());
//                jsonObject.put("um3_qty", model.getUnit3Qty());
//                goodsDetailsArray.put(jsonObject);
//
//            } catch (JSONException e) {
//                e.printStackTrace();
//            }
//
//
//        }
//        // Log.d(TAG, "goodsDetailsArray: "+goodsDetailsArray);
//        JSONArray goodsArray = new JSONArray();
//        dbHelper helper = new dbHelper(GoodsItemListActivity.this);
//        SQLiteDatabase database = helper.getReadableDatabase();
//        Cursor cursor = helper.getGoodsByDoc(database, docNo);
//
//        if (cursor.moveToFirst()) {
//            do {
//                JSONObject object = new JSONObject();
//                try {
//                    object.put(DataContract.GoodsReceive.COL_DOCUMENT_NUMBER, cursor.getInt(cursor.getColumnIndex(DataContract.GoodsReceive.COL_DOCUMENT_NUMBER)));
//                    object.put(DataContract.GoodsReceive.COL_ORDER_NUMBER, cursor.getString(cursor.getColumnIndex(DataContract.GoodsReceive.COL_ORDER_NUMBER)));
//                    object.put(DataContract.GoodsReceive.COL_SUPPLIER_CODE, cursor.getString(cursor.getColumnIndex(DataContract.GoodsReceive.COL_SUPPLIER_CODE)));
//                    object.put(DataContract.GoodsReceive.COL_INVOICE_NUMBER, (cursor.getString(cursor.getColumnIndex(DataContract.GoodsReceive.COL_INVOICE_NUMBER))).toUpperCase());
//                    object.put(DataContract.GoodsReceive.COL_INVOICE_DATE, cursor.getString(cursor.getColumnIndex(DataContract.GoodsReceive.COL_INVOICE_DATE)));
//                    object.put(DataContract.GoodsReceive.COL_STAFF_NAME, cursor.getString(cursor.getColumnIndex(DataContract.GoodsReceive.COL_STAFF_NAME)));
//                    object.put(DataContract.GoodsReceive.COL_TOTAL, cursor.getDouble(cursor.getColumnIndex(DataContract.GoodsReceive.COL_TOTAL)));
//                    object.put(DataContract.GoodsReceive.COL_DATE_TIME, cursor.getString(cursor.getColumnIndex(DataContract.GoodsReceive.COL_DATE_TIME)));
//                    goodsArray.put(object);
//                } catch (JSONException e) {
//                    e.printStackTrace();
//                }
//            } while (cursor.moveToNext());
//        }
//        cursor.close();
//        database.close();
//        JSONObject result = new JSONObject();
//
//        try {
//            result.put("Goods", goodsArray);
//            result.put("Details", goodsDetailsArray);
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }
//
//        View view = this.getCurrentFocus();
//        AppUtils.hideKeyboard(this, view);
//        progressDialog = AppUtils.showProgressDialog(this, "Loading....");
//        serviceGateway = new VolleyServiceGateway(mVolleyListener, this);
//        serviceGateway.postDataVolley("POSTCALL", "PriceChecker/goods_receive.php", result);
    }

    private void saveToLocalDataBase(int syncStatus) {
         boolean res = helper.saveGoods(docNo, orderNo, supplierCode, invoiceNo, invoiceDate, user,totalAmount,mDate);
        if (res) {
            boolean res1 = helper.saveGoodsDetails1(invoiceNo, syncStatus);
            if (res1) {
                Log.d(TAG, "saved successfully: ");
                postDataToVolley();
                //gotoNext();
            }
        }
    }

    private void postDataToVolley(){
        //Add goodsItems into json array
        JSONArray goodsArray=new JSONArray();
        JSONObject object = new JSONObject();
        try {
            object.put(DataContract.GoodsReceive.COL_DOCUMENT_NUMBER, docNo);
            object.put(DataContract.GoodsReceive.COL_ORDER_NUMBER,orderNo);
            object.put(DataContract.GoodsReceive.COL_SUPPLIER_CODE,supplierCode);
            object.put(DataContract.GoodsReceive.COL_INVOICE_NUMBER,invoiceNo);
            object.put(DataContract.GoodsReceive.COL_INVOICE_DATE,invoiceDate);
            object.put(DataContract.GoodsReceive.COL_STAFF_NAME,user);
            object.put(DataContract.GoodsReceive.COL_TOTAL,totalAmount);
            object.put(DataContract.GoodsReceive.COL_DATE_TIME,mDate);
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

        // put json array ito json object
        JSONObject result = new JSONObject();
        try {
            result.put("Goods", goodsArray);
            result.put("Details", goodsDetailsArray);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        //send to volley
        View view = this.getCurrentFocus();
        AppUtils.hideKeyboard(this, view);
        progressDialog = AppUtils.showProgressDialog(this, "Loading....");
        serviceGateway = new VolleyServiceGateway(mVolleyListener, this);
        serviceGateway.postDataVolley("POSTCALL", "PriceChecker/goods_receive.php", result);


    }
    private void gotoNext() {


        Intent i = new Intent(GoodsItemListActivity.this, HomeActivity.class);
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(i);
    }


}
