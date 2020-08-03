package com.alhikmahpro.www.e_inventory.View;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.view.ViewCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.alhikmahpro.www.e_inventory.Adapter.DocAdapter;
import com.alhikmahpro.www.e_inventory.Data.DataContract;
import com.alhikmahpro.www.e_inventory.Data.ItemModel;
import com.alhikmahpro.www.e_inventory.Data.RuntimeData;
import com.alhikmahpro.www.e_inventory.Data.dbHelper;
import com.alhikmahpro.www.e_inventory.Interface.OnAdapterClickListener;
import com.alhikmahpro.www.e_inventory.R;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class ListDocActivity extends AppCompatActivity {

    @BindView(R.id.doc_list_rv)
    RecyclerView docListRv;
//    @BindView(R.id.txtEmpty)
//    TextView txtEmpty;
    @BindView(R.id.fab)
    FloatingActionButton fab;
    private static final String TAG = "ListDocActivity";
    RecyclerView.Adapter adapter;
    RecyclerView.LayoutManager layoutManager;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    private String type;
    List<ItemModel> list;
    dbHelper helper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_doc);
        ButterKnife.bind(this);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        helper = new dbHelper(ListDocActivity.this);
        Log.d(TAG, "onCreate: ");

        Intent mIntent = getIntent();
        type = mIntent.getStringExtra("Type");
        list = new ArrayList<>();

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

    private List<ItemModel> loadInventory() {

        List<ItemModel>list;
        list = helper.getStocks();
        return list;

    }

    private List<ItemModel> loadGoods() {
        List<ItemModel>list;
        list = helper.getGoods();
        return list;
    }


    private void populateRecycler() {
        if(list.size()>0){
            list.clear();
        }
        RuntimeData.mCartData.clear();
        if (type.equals("INV")) {
            toolbar.setTitle("Inventory");
            list=loadInventory();
        } else if (type.equals("GDS")) {
            toolbar.setTitle("Goods Receive");
            list=loadGoods();
        }
        if(list.size()>0){
            layoutManager = new LinearLayoutManager(this);
            docListRv.setLayoutManager(layoutManager);
            docListRv.setItemAnimator(new DefaultItemAnimator());
            docListRv.setHasFixedSize(true);

            adapter = new DocAdapter(list, new OnAdapterClickListener() {
                @Override
                public void OnItemClicked(int position) {
                    editDoc(position);
                }

                @Override
                public void OnDeleteClicked(int position) {
                    //use as share button click event
                }
            });
            docListRv.setAdapter(adapter);
            ViewCompat.setNestedScrollingEnabled(docListRv, false);
        }
    }

    private void editDoc(int position) {

        ItemModel itemModel = list.get(position);
        if (type.equals("INV")) {
            Intent intent = new Intent(this, ListItemActivity.class);
            intent.putExtra("ACTION", "Edit");
            intent.putExtra("DOC_NO", itemModel.getDocNo());
            intent.putExtra("USER", itemModel.getStaffName());
            startActivity(intent);
        } else if (type.equals("GDS")) {

            Log.d(TAG, "editDoc:supp code " + itemModel.getSupplierCode());
            Intent intent = new Intent(this, GoodsItemListActivity.class);
            intent.putExtra("ACTION", "Edit");
            intent.putExtra("DOC_NO", itemModel.getDocNo());
            intent.putExtra("ORD_NO", itemModel.getOrderNo());
            intent.putExtra("SUPP_CODE", itemModel.getSupplierCode());
            intent.putExtra("SUPP_NAME", itemModel.getSupplierName());
            intent.putExtra("INV_NO", itemModel.getInvoiceNo());
            intent.putExtra("INV_DATE", itemModel.getInvoiceDate());
            intent.putExtra("USER", itemModel.getStaffName());
            startActivity(intent);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        populateRecycler();
    }


//    private void editDoc(int no,String user) {
//
//        if(type.equals("INV")){
//            Intent intent=new Intent(this,ListItemActivity.class);
//            intent.putExtra("Action","Edit");
//            intent.putExtra("DocNo",no);
//            intent.putExtra("User",user);
//            startActivity(intent);
//        }else if(type.equals("GDS")){
//
//            //Toast.makeText(this, "edit option not available", Toast.LENGTH_SHORT).show();
//            Intent intent=new Intent(this,GoodsItemListActivity.class);
//            intent.putExtra("TYPE","edit");
//            int docNo = mIntent.getIntExtra("DOC_NO", 0);
//            String orderNo = mIntent.getStringExtra("ORD_NO");
//            String supplierCode = mIntent.getStringExtra("SUPP_CODE");
//            String supplierName = mIntent.getStringExtra("SUPP_NAME");
//            String invoiceNo = mIntent.getStringExtra("INV_NO");
//            String user = mIntent.getStringExtra("USER");
//            String invoiceDate = mIntent.getStringExtra("INV_DATE");
//            startActivity(intent);
//        }


    //  }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;

    }

    @OnClick(R.id.fab)
    public void onViewClicked() {
        if (type.equals("INV")) {
            Intent intent = new Intent(ListDocActivity.this, InventoryActivity.class);
            startActivity(intent);
        } else if (type.equals("GDS")) {
            Intent intent = new Intent(ListDocActivity.this, InvoiceActivity.class);
            startActivity(intent);
        } else if (type.equals("SAL")) {
            Intent intent = new Intent(ListDocActivity.this, CheckCustomerActivity.class);
            startActivity(intent);
        }
//        else if (type.equals("REC")) {
//            Intent intent = new Intent(ListDocActivity.this, ReceiptActivity.class);
//            startActivity(intent);
//        }


    }

}
