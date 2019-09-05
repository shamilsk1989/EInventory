package com.alhikmahpro.www.e_inventory.View;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.alhikmahpro.www.e_inventory.Data.DataContract;
import com.alhikmahpro.www.e_inventory.Data.ItemModel;
import com.alhikmahpro.www.e_inventory.Data.ReceiptModel;
import com.alhikmahpro.www.e_inventory.Data.dbHelper;
import com.alhikmahpro.www.e_inventory.R;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class ListReceiptActivity extends AppCompatActivity {

    @BindView(R.id.doc_list_rv)
    RecyclerView docListRv;
    @BindView(R.id.txtEmpty)
    TextView txtEmpty;
    @BindView(R.id.fab)
    FloatingActionButton fab;
    RecyclerView.Adapter adapter;
    RecyclerView.LayoutManager layoutManager;
    private String type;
    List<ReceiptModel> list;
    private static final String TAG = "ListReceiptActivity";



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_receipt);
        ButterKnife.bind(this);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle("Receipts");
        list=new ArrayList<>();

    }

    @Override
    protected void onResume() {
        super.onResume();
        loadReceipts();
    }

    private void loadReceipts() {

        list.clear();
        dbHelper helper = new dbHelper(ListReceiptActivity.this);
        SQLiteDatabase database = helper.getReadableDatabase();
        Cursor cursor = helper.getAllReceipts(database);
        if (cursor.moveToFirst()) {

            txtEmpty.setVisibility(View.GONE);
            do {
                //Log.d(TAG, "loadReceipts: "+cursor.getInt(cursor.getColumnIndex(DataContract.Stocks.COL_DOCUMENT_NUMBER)));
                ReceiptModel model = new ReceiptModel();
                model.setReceiptNo(cursor.getString(cursor.getColumnIndex(DataContract.Receipts.COL_RECEIPT_NUMBER)));
                model.setReceiptDate(cursor.getString(cursor.getColumnIndex(DataContract.Receipts.COL_RECEIPT_DATE)));
                model.setSalesmanId(cursor.getString(cursor.getColumnIndex(DataContract.Receipts.COL_SALESMAN_ID)));
                model.setReceivedAmount(cursor.getDouble(cursor.getColumnIndex(DataContract.Receipts.COL_RECEIVED_AMOUNT)));
                model.setPaymentType(cursor.getString(cursor.getColumnIndex(DataContract.Receipts.COL_PAYMENT_TYPE)));
                model.setChequeNumber(cursor.getString(cursor.getColumnIndex(DataContract.Receipts.COL_CHEQUE_NUMBER)));
                model.setChequeDate(cursor.getString(cursor.getColumnIndex(DataContract.Receipts.COL_CHEQUE_DATE)));
                model.setIs_sync(cursor.getInt(cursor.getColumnIndex(DataContract.Receipts.COL_IS_SYNC)));
                list.add(model);
            } while (cursor.moveToNext());
        } else {
            txtEmpty.setVisibility(View.VISIBLE);
        }
        cursor.close();
        database.close();
    }

    @OnClick(R.id.fab)
    public void onViewClicked() {

        Intent intent = new Intent(ListReceiptActivity.this, ReceiptActivity.class);
            startActivity(intent);
    }
}
