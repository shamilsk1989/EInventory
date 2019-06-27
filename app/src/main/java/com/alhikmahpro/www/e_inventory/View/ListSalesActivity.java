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
import com.alhikmahpro.www.e_inventory.Adapter.SalesListAdapter;
import com.alhikmahpro.www.e_inventory.Data.Cart;
import com.alhikmahpro.www.e_inventory.Data.CartModel;
import com.alhikmahpro.www.e_inventory.Data.DataContract;
import com.alhikmahpro.www.e_inventory.Data.ItemModel;
import com.alhikmahpro.www.e_inventory.Data.dbHelper;
import com.alhikmahpro.www.e_inventory.Interface.OnAdapterClickListener;
import com.alhikmahpro.www.e_inventory.R;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class ListSalesActivity extends AppCompatActivity {

    @BindView(R.id.doc_list_rv)
    RecyclerView docListRv;
    @BindView(R.id.txtEmpty)
    TextView txtEmpty;
    @BindView(R.id.fab)
    FloatingActionButton fab;
    List<ItemModel> list=new ArrayList<>();
    RecyclerView.LayoutManager layoutManager;
    SalesListAdapter adapter;
    private static final String TAG = "ListSalesActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_sales);
        ButterKnife.bind(this);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle("Sales");
        populateRecycler();
    }

    private void populateRecycler() {
        if(list.size()>0){
            list.clear();
        }
        dbHelper helper = new dbHelper(ListSalesActivity.this);
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
                model.setCustomerCode(cursor.getString(cursor.getColumnIndex(DataContract.Invoice.COL_CUSTOMER_NAME)));
                model.setTotal(cursor.getDouble(cursor.getColumnIndex(DataContract.Invoice.COL_TOTAL_AMOUNT)));
                model.setDiscount(cursor.getDouble(cursor.getColumnIndex(DataContract.Invoice.COL_DISCOUNT_AMOUNT)));
                model.setNet(cursor.getDouble(cursor.getColumnIndex(DataContract.Invoice.COL_NET_AMOUNT)));
                model.setPaymentType(cursor.getString(cursor.getColumnIndex(DataContract.Invoice.COL_PAYMENT_TYPE)));
                model.setIs_sync(cursor.getInt(cursor.getColumnIndex(DataContract.Invoice.COL_IS_SYNC)));
                list.add(model);
            } while (cursor.moveToNext());
        } else {
            txtEmpty.setVisibility(View.VISIBLE);
        }
        cursor.close();
        database.close();


        layoutManager = new LinearLayoutManager(this);
        docListRv.setLayoutManager(layoutManager);
        docListRv.setItemAnimator(new DefaultItemAnimator());
        docListRv.setHasFixedSize(true);
        adapter = new SalesListAdapter(this, list, new OnAdapterClickListener() {
            @Override
            public void OnItemClicked(int position) {

                goToNext(position);

            }

            @Override
            public void OnDeleteClicked(int position) {

            }
        });

        docListRv.setAdapter(adapter);
        ViewCompat.setNestedScrollingEnabled(docListRv, false);
    }

    private void goToNext(int position) {
        ItemModel itemModel=list.get(position);
        String invoiceNo=itemModel.getInvoiceNo();
        dbHelper helper=new dbHelper(this);

        //get invoice details and add to the cart
        helper.getInvoiceDetailsById(invoiceNo);

        Log.d(TAG, "goToNext:cart size "+Cart.mCart.size());

        Intent intent = new Intent(ListSalesActivity.this,PrintViewActivity.class);
        intent.putExtra("TYPE","edit");
        intent.putExtra("CUS_NAME", itemModel.getCustomerName());
        intent.putExtra("CUS_CODE", itemModel.getCustomerCode());
        intent.putExtra("DISCOUNT", itemModel.getDiscount());
        intent.putExtra("SALESMAN_ID", itemModel.getStaffName());
        intent.putExtra("DOC_NO", itemModel.getInvoiceNo());
        intent.putExtra("DOC_DATE", itemModel.getInvoiceDate());
        intent.putExtra("TOTAL",itemModel.getTotal());
        intent.putExtra("NET",itemModel.getNet());
        intent.putExtra("PAY_MOD", itemModel.getPaymentType());
        startActivity(intent);
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
        Intent intent=new Intent(ListSalesActivity.this,CheckCustomerActivity.class);
        startActivity(intent);
    }
}
