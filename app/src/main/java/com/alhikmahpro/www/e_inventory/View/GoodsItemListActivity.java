package com.alhikmahpro.www.e_inventory.View;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.alhikmahpro.www.e_inventory.Adapter.CartAdapter;
import com.alhikmahpro.www.e_inventory.Data.Cart;
import com.alhikmahpro.www.e_inventory.Data.Converter;
import com.alhikmahpro.www.e_inventory.Data.DataContract;
import com.alhikmahpro.www.e_inventory.Data.ItemModel;
import com.alhikmahpro.www.e_inventory.Data.dbHelper;
import com.alhikmahpro.www.e_inventory.Interface.OnAdapterClickListener;
import com.alhikmahpro.www.e_inventory.Interface.volleyListener;
import com.alhikmahpro.www.e_inventory.Network.VolleyServiceGateway;
import com.alhikmahpro.www.e_inventory.R;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class GoodsItemListActivity extends AppCompatActivity {

    @BindView(R.id.item_list_rv)
    RecyclerView itemListRv;
    @BindView(R.id.txtEmpty)
    TextView txtEmpty;
    @BindView(R.id.btnSave)
    Button btnSave;
    int docNo;
    String supplierName, supplierCode, invoiceNo, invoiceDate, user, orderNo, action;
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
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    private static int cart_count = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_goods_item_list);
        ButterKnife.bind(this);
        progressDialog = new ProgressDialog(this);
        connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        Intent mIntent = getIntent();
        action = mIntent.getStringExtra("ACTION");
        Log.d(TAG, "onCreate: " + action);

        docNo = mIntent.getIntExtra("DOC_NO", 0);
        orderNo = mIntent.getStringExtra("ORD_NO");
        supplierCode = mIntent.getStringExtra("SUPP_CODE");
        supplierName = mIntent.getStringExtra("SUPP_NAME");
        invoiceNo = mIntent.getStringExtra("INV_NO");
        user = mIntent.getStringExtra("USER");
        invoiceDate = mIntent.getStringExtra("INV_DATE");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle(supplierName);
        helper = new dbHelper(this);

        // check action is edit or not if edit then load item db to gCart;
        if (action.equals("Edit")) {
            // if edit then load goodsDetail from database by docNo.
            if (Cart.gCart.size() > 0) {
                Cart.gCart.clear();
            }
            SQLiteDatabase database = helper.getReadableDatabase();
            Cursor cursor = helper.getGoodsDetailsByDoc(database, docNo);
            if (cursor.moveToFirst()) {

                do {

                    ItemModel itemModel = new ItemModel();
                    itemModel.setDocNo(cursor.getInt(cursor.getColumnIndex(DataContract.GoodsReceiveDetails.COL_DOCUMENT_NUMBER)));
                    itemModel.setBarCode(cursor.getString(cursor.getColumnIndex(DataContract.GoodsReceiveDetails.COL_BAR_CODE)));
                    itemModel.setProductCode(cursor.getString(cursor.getColumnIndex(DataContract.GoodsReceiveDetails.COL_PRODUCT_CODE)));
                    itemModel.setProductName(cursor.getString(cursor.getColumnIndex(DataContract.GoodsReceiveDetails.COL_PRODUCT_NAME)));
                    itemModel.setQty(cursor.getDouble(cursor.getColumnIndex(DataContract.GoodsReceiveDetails.COL_QUANTITY)));
                    itemModel.setFreeQty(cursor.getDouble(cursor.getColumnIndex(DataContract.GoodsReceiveDetails.COL_FREE_QUANTITY)));
                    itemModel.setSelectedPackage(cursor.getString(cursor.getColumnIndex(DataContract.GoodsReceiveDetails.COL_UNIT)));
                    itemModel.setSelectedFreePackage(cursor.getString(cursor.getColumnIndex(DataContract.GoodsReceiveDetails.COL_FREE_UNIT)));
                    itemModel.setRate(cursor.getDouble(cursor.getColumnIndex(DataContract.GoodsReceiveDetails.COL_RATE)));
                    itemModel.setDiscount(cursor.getDouble(cursor.getColumnIndex(DataContract.GoodsReceiveDetails.COL_DISCOUNT)));
                    itemModel.setCostPrice(cursor.getDouble(cursor.getColumnIndex(DataContract.GoodsReceiveDetails.COL_COST_PRICE)));
                    itemModel.setSalePrice(cursor.getDouble(cursor.getColumnIndex(DataContract.GoodsReceiveDetails.COL_SALES_PRICE)));
                    itemModel.setStock(cursor.getString(cursor.getColumnIndex(DataContract.GoodsReceiveDetails.COL_STOCK)));
                    itemModel.setUnit1(cursor.getString(cursor.getColumnIndex(DataContract.GoodsReceiveDetails.COL_UNIT1)));
                    itemModel.setUnit2(cursor.getString(cursor.getColumnIndex(DataContract.GoodsReceiveDetails.COL_UNIT2)));
                    itemModel.setUnit3(cursor.getString(cursor.getColumnIndex(DataContract.GoodsReceiveDetails.COL_UNIT3)));
                    itemModel.setUnit1Qty(cursor.getInt(cursor.getColumnIndex(DataContract.GoodsReceiveDetails.COL_UN_QTY1)));
                    itemModel.setUnit2Qty(cursor.getInt(cursor.getColumnIndex(DataContract.GoodsReceiveDetails.COL_UN_QTY2)));
                    itemModel.setUnit3Qty(cursor.getInt(cursor.getColumnIndex(DataContract.GoodsReceiveDetails.COL_UN_QTY3)));
                    itemModel.setNet(cursor.getDouble(cursor.getColumnIndex(DataContract.GoodsReceiveDetails.COL_NET_VALUE)));
                    Cart.gCart.add(itemModel);

                } while (cursor.moveToNext());
            }

            cursor.close();
            database.close();

        }
        loadRecycler();
        calculateNet();
    }


    private void loadRecycler() {

        if (Cart.gCart.size() > 0) {
            //if cart not empty hide empty textView
            txtEmpty.setVisibility(View.GONE);
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
//                    if (action.equals("Edit")) {
//                        Toast.makeText(GoodsItemListActivity.this, "Not available", Toast.LENGTH_SHORT).show();
//                    } else {

                    // Remove item from cart
                    new AlertDialog.Builder(GoodsItemListActivity.this)
                            .setTitle("Confirm")
                            .setMessage("Do you want to delete this item?")
                            .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {

                                    ItemModel itemModel = Cart.gCart.get(position);
                                    double amount = itemModel.getNet();
                                    Cart.gCart.remove(position);
                                    adapter.notifyDataSetChanged();
                                    adapter.notifyItemRemoved(position);
                                    adapter.notifyItemRangeChanged(position, Cart.gCart.size());
                                    calculateNet();

                                }
                            })
                            .setNegativeButton("No", new DialogInterface.OnClickListener() {
                                @Override

                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.cancel();
                                }
                            }).create().show();
                    //}

                }
            });
            itemListRv.setAdapter(adapter);
        } else {
            //txtTotal.setVisibility(View.GONE);
            //txtTotalCount.setVisibility(View.GONE);
            btnSave.setVisibility(View.GONE);
        }


    }

    //Calculate total number of items and total amount of cart
    private void calculateNet() {
        // set total count and total amount in cart
        totalAmount = 0;
        for (ItemModel itemModel : Cart.gCart) {
            Log.d(TAG, "initView: cart amount " + itemModel.getNet());
            totalAmount = totalAmount + itemModel.getNet();
            Log.d(TAG, "initView: cart amount " + totalAmount);
        }
        cart_count=Cart.gCart.size();
        if(Cart.gCart.size()>0){
            btnSave.setText("Pay QR:"+currencyFormatter(totalAmount));
        }else {
            txtEmpty.setVisibility(View.VISIBLE);
            btnSave.setVisibility(View.GONE);
        }
        invalidateOptionsMenu();
        //txtTotal.setText(String.valueOf(totalAmount));


    }

    @OnClick(R.id.btnSave)
    public void onViewClicked() {

        Log.d(TAG, "onViewClicked: " + "doc :" + docNo + "suppl :" + supplierName);
        Log.d(TAG, "onViewClicked:gCart " + Cart.gCart.size());
        Intent intent_payment = new Intent(GoodsItemListActivity.this, PaymentActivity.class);
        intent_payment.putExtra("ACTION", "NEW");
        intent_payment.putExtra("TYPE", "GDS");
        intent_payment.putExtra("CUS_NAME", supplierName);
        intent_payment.putExtra("CUS_CODE", supplierCode);
        intent_payment.putExtra("SALESMAN_ID", user);
        intent_payment.putExtra("DOC_NO", docNo);
        intent_payment.putExtra("ORD_NO", orderNo);
        intent_payment.putExtra("INV_NO", invoiceNo);
        intent_payment.putExtra("INV_DATE", invoiceDate);
        intent_payment.putExtra("TOTAL_ROW", Cart.gCart.size());
        intent_payment.putExtra("TOTAL", totalAmount);
        startActivity(intent_payment);
    }


    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.cart_toolbar, menu);
        MenuItem menuItem = menu.findItem(R.id.action_cart);
        Log.d(TAG, "onCreateOptionsMenu: "+ Converter.convertLayoutToImage(GoodsItemListActivity.this,cart_count,R.drawable.ic_shopping_cart));
        menuItem.setIcon(Converter.convertLayoutToImage(GoodsItemListActivity.this,cart_count,R.drawable.ic_shopping_cart));
        MenuItem itemDelete = menu.findItem(R.id.action_delete);
        itemDelete.setVisible(false);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection

        int id = item.getItemId();
        if(id==R.id.action_delete){
            //deleteCart();
        }

        return super.onOptionsItemSelected(item);
    }
    public String currencyFormatter(double val) {

        NumberFormat format = NumberFormat.getCurrencyInstance();
        String pattern = ((DecimalFormat) format).toPattern();
        String newPattern = pattern.replace("\u00A4", "").trim();
        NumberFormat newFormat = new DecimalFormat(newPattern);
        return String.valueOf(newFormat.format(val));

    }
}
