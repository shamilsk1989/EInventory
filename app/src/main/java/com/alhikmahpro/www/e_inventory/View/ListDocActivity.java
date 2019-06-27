package com.alhikmahpro.www.e_inventory.View;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.view.ViewCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.alhikmahpro.www.e_inventory.Adapter.DocAdapter;
import com.alhikmahpro.www.e_inventory.Data.DataContract;
import com.alhikmahpro.www.e_inventory.Data.ItemModel;
import com.alhikmahpro.www.e_inventory.Data.RuntimeData;
import com.alhikmahpro.www.e_inventory.Data.dbHelper;
import com.alhikmahpro.www.e_inventory.Interface.FragmentActionListener;
import com.alhikmahpro.www.e_inventory.Interface.OnAdapterClickListener;
import com.alhikmahpro.www.e_inventory.R;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class ListDocActivity extends AppCompatActivity {

    @BindView(R.id.doc_list_rv)
    RecyclerView docListRv;
    @BindView(R.id.txtEmpty)
    TextView txtEmpty;
    @BindView(R.id.fab)
    FloatingActionButton fab;
    private static final String TAG = "ListDocActivity";
    RecyclerView.Adapter adapter;
    RecyclerView.LayoutManager layoutManager;
    private String type;
    List<ItemModel> list;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_doc);
        ButterKnife.bind(this);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        Log.d(TAG, "onCreate: ");

        Intent mIntent = getIntent();
        type = mIntent.getStringExtra("Type");
        list= new ArrayList<>();
        if(type.equals("INV")){
            getSupportActionBar().setTitle("Inventory");
            loadInventory();

        }else if(type.equals("GDS")){
            getSupportActionBar().setTitle("Goods Receive");
            loadGoods();
        }
        else if(type.equals("SAL")){
            getSupportActionBar().setTitle("Sales");
            loadSales();
        }
        populateRecycler();
    }



    private void loadSales() {

        dbHelper helper = new dbHelper(ListDocActivity.this);
        SQLiteDatabase database = helper.getReadableDatabase();
        Cursor cursor = helper.getInvoice(database);
        if (cursor.moveToFirst()) {

            txtEmpty.setVisibility(View.GONE);
            do {
                ItemModel model = new ItemModel();
                model.setInvoiceNo(cursor.getString(cursor.getColumnIndex(DataContract.Invoice.COL_INVOICE_NUMBER)));
                model.setDate(cursor.getString(cursor.getColumnIndex(DataContract.Invoice.COL_INVOICE_DATE)));
                model.setStaffName(cursor.getString(cursor.getColumnIndex(DataContract.Invoice.COL_SALESMAN_ID)));
                model.setCustomerCode(cursor.getString(cursor.getColumnIndex(DataContract.Invoice.COL_CUSTOMER_CODE)));
                model.setTotal(cursor.getDouble(cursor.getColumnIndex(DataContract.Invoice.COL_TOTAL_AMOUNT)));
                model.setDiscount(cursor.getDouble(cursor.getColumnIndex(DataContract.Invoice.COL_DISCOUNT_AMOUNT)));
                model.setNet(cursor.getDouble(cursor.getColumnIndex(DataContract.Invoice.COL_NET_AMOUNT)));
                model.setPaymentType(cursor.getString(cursor.getColumnIndex(DataContract.Invoice.COL_PAYMENT_TYPE)));
                model.setIs_sync(cursor.getInt(cursor.getColumnIndex(DataContract.Invoice.COL_IS_SYNC)));

                list.add(model);
            } while (cursor.moveToNext());
        }else {
            txtEmpty.setVisibility(View.VISIBLE);
        }
        cursor.close();
        database.close();
    }




    private void loadInventory(){

        dbHelper helper = new dbHelper(ListDocActivity.this);
        SQLiteDatabase database = helper.getReadableDatabase();
        Cursor cursor = helper.getStocks(database);
        if (cursor.moveToFirst()) {

            txtEmpty.setVisibility(View.GONE);
            do {
                ItemModel model = new ItemModel();
                model.setInvoiceNo(cursor.getString(cursor.getColumnIndex(DataContract.Stocks.COL_DOCUMENT_NUMBER)));
                model.setStaffName(cursor.getString(cursor.getColumnIndex(DataContract.Stocks.COL_STAFF_NAME)));
                model.setTotal(cursor.getDouble(cursor.getColumnIndex(DataContract.Stocks.COL_TOTAL)));
                model.setDate(cursor.getString(cursor.getColumnIndex(DataContract.Stocks.COL_DATE_TIME)));
                model.setIs_sync(cursor.getInt(cursor.getColumnIndex(DataContract.Stocks.COL_IS_SYNC)));
                list.add(model);
            } while (cursor.moveToNext());
        }else {
            txtEmpty.setVisibility(View.VISIBLE);
        }
        cursor.close();
        database.close();
    }
    private void loadGoods() {
        dbHelper helper = new dbHelper(ListDocActivity.this);
        SQLiteDatabase database = helper.getReadableDatabase();
        Cursor cursor = helper.getGoods(database);
        if (cursor.moveToFirst()) {

            txtEmpty.setVisibility(View.GONE);
            do {
                ItemModel model = new ItemModel();
                model.setInvoiceNo(cursor.getString(cursor.getColumnIndex(DataContract.GoodsReceive.COL_DOCUMENT_NUMBER)));
                model.setStaffName(cursor.getString(cursor.getColumnIndex(DataContract.GoodsReceive.COL_STAFF_NAME)));
                model.setTotal(cursor.getDouble(cursor.getColumnIndex(DataContract.GoodsReceive.COL_TOTAL)));
                model.setDate(cursor.getString(cursor.getColumnIndex(DataContract.GoodsReceive.COL_DATE_TIME)));
                model.setIs_sync(cursor.getInt(cursor.getColumnIndex(DataContract.Stocks.COL_IS_SYNC)));
                list.add(model);
            } while (cursor.moveToNext());
        }else {
            txtEmpty.setVisibility(View.VISIBLE);
        }
        cursor.close();
        database.close();
    }


    private void populateRecycler(){

        layoutManager = new LinearLayoutManager(this);
        docListRv.setLayoutManager(layoutManager);
        docListRv.setItemAnimator(new DefaultItemAnimator());
        docListRv.setHasFixedSize(true);
        adapter=new DocAdapter(list, new OnAdapterClickListener() {
            @Override
            public void OnItemClicked(int position) {

                if(type.equals("SAL")){
                    Toast.makeText(ListDocActivity.this, "Edit option not available ", Toast.LENGTH_SHORT).show();
                }
                else{
                    ItemModel itemModel=list.get(position);
                    String docNo=itemModel.getInvoiceNo();
                   // int no=itemModel.getDocNo();
                    int no;
                    try {
                        no = Integer.parseInt(docNo);
                    }
                    catch (NumberFormatException e)
                    {
                        no = 0;
                    }
                    String user=itemModel.getStaffName();
                    editDoc(no,user);
                }

            }
            @Override
            public void OnDeleteClicked(int position) { }
        });

        docListRv.setAdapter(adapter);
        ViewCompat.setNestedScrollingEnabled(docListRv,false);
    }

    @Override
    protected void onResume() {
        super.onResume();
        RuntimeData.mCartData.clear();
    }
    private void editDoc(int no,String user) {

        if(type.equals("INV")){
            Intent intent=new Intent(this,ListItemActivity.class);
            intent.putExtra("Action","Edit");
            intent.putExtra("DocNo",no);
            intent.putExtra("User",user);
            startActivity(intent);
        }else if(type.equals("GDS")){
            Intent intent=new Intent(this,GoodsItemListActivity.class);
            intent.putExtra("DocNo",no);
            startActivity(intent);
        }



    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;

    }

    @OnClick(R.id.fab)
    public void onViewClicked() {
        if(type.equals("INV")){
            Intent intent=new Intent(ListDocActivity.this,InventoryActivity.class);
            startActivity(intent);
        }else if(type.equals("GDS")){
            Intent intent=new Intent(ListDocActivity.this,InvoiceActivity.class);
            startActivity(intent);
        }else if(type.equals("SAL")){
            Intent intent=new Intent(ListDocActivity.this,CheckCustomerActivity.class);
            startActivity(intent);
        }





    }

}
