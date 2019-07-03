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

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
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
    String supplierName, supplierCode, invoiceNo, invoiceDate, user, orderNo,action;
    double totalAmount;
    ProgressDialog progressDialog;
    ConnectivityManager connectivityManager;
    RecyclerView.Adapter adapter;
    RecyclerView.LayoutManager layoutManager;
    List<ItemModel> listItem = new ArrayList<>();
    private static final String TAG = "GoodsItemListActivity";
    dbHelper helper;

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
        action=mIntent.getStringExtra("ACTION");
        docNo = mIntent.getIntExtra("DOC_NO", 0);
        orderNo = mIntent.getStringExtra("ORD_NO");
        supplierCode = mIntent.getStringExtra("SUPP_CODE");
        supplierName = mIntent.getStringExtra("SUPP_NAME");
        invoiceNo = mIntent.getStringExtra("INV_NO");
        user = mIntent.getStringExtra("USER");
        invoiceDate = mIntent.getStringExtra("INV_DATE");
//
//        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
//        mDate = sdf.format(new Date());
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle("Document  " + docNo);
        helper = new dbHelper(this);
        initView();
        //initVolleyCallBack();

    }



    private void initView() {
        if(action.equals("New")){
            btnSave.setText("Next");
        }
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
                    // check the action; if edit the delete not allowed
                    if(action.equals("Edit")){
                        Toast.makeText(GoodsItemListActivity.this, "Not available", Toast.LENGTH_SHORT).show();
                    }else {

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

                }
            });
            itemListRv.setAdapter(adapter);
        } else {
            txtTotal.setVisibility(View.GONE);
            txtTotalCount.setVisibility(View.GONE);
        }



    }

    //Calculate total number of items and total amount of cart
    private void calculateNet(double amount){

        txtTotalCount.setText(String.valueOf(Cart.gCart.size()));
        totalAmount = totalAmount-amount;
        txtTotal.setText(String.valueOf(totalAmount));
    }

    @OnClick(R.id.btnSave)
    public void onViewClicked() {

        Log.d(TAG, "onViewClicked: "+"doc :"+docNo+ "suppl :"+supplierName);
        Intent intent_payment = new Intent(GoodsItemListActivity.this, PaymentActivity.class);
        intent_payment.putExtra("TYPE", "GDS");
        intent_payment.putExtra("CUS_NAME", supplierName);
        intent_payment.putExtra("CUS_CODE", supplierCode);
        intent_payment.putExtra("SALESMAN_ID", user);
        intent_payment.putExtra("DOC_NO", docNo);
        intent_payment.putExtra("ORD_NO", orderNo);
        intent_payment.putExtra("INV_NO", invoiceNo);
        intent_payment.putExtra("INV_DATE", invoiceDate);
        intent_payment.putExtra("TOTAL_ROW", Cart.gCart.size());
        intent_payment.putExtra("TOTAL",totalAmount);
        startActivity(intent_payment);
    }


    private void gotoNext() {


        Intent i = new Intent(GoodsItemListActivity.this, HomeActivity.class);
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(i);
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}
