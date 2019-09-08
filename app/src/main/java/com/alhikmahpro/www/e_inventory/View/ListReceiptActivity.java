package com.alhikmahpro.www.e_inventory.View;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.alhikmahpro.www.e_inventory.Adapter.ReceiptAdapter;
import com.alhikmahpro.www.e_inventory.Data.DataContract;
import com.alhikmahpro.www.e_inventory.Data.ItemModel;
import com.alhikmahpro.www.e_inventory.Data.ReceiptModel;
import com.alhikmahpro.www.e_inventory.Data.dbHelper;
import com.alhikmahpro.www.e_inventory.Interface.OnAdapterClickListener;
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
//        Intent intent = getIntent();
//        type= intent.getStringExtra("Type");
        getSupportActionBar().setTitle("Receipts");
        list = new ArrayList<>();

    }

    @Override
    protected void onResume() {
        super.onResume();
        loadReceipts();
    }

    private void loadReceipts() {

        list.clear();
        layoutManager = new LinearLayoutManager(this);
        docListRv.setLayoutManager(layoutManager);
        docListRv.setItemAnimator(new DefaultItemAnimator());
        docListRv.setHasFixedSize(true);
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
                model.setReceivedAmount(cursor.getString(cursor.getColumnIndex(DataContract.Receipts.COL_RECEIVED_AMOUNT)));
                model.setBalanceAmount(cursor.getString(cursor.getColumnIndex(DataContract.Receipts.COL_BALANCE_AMOUNT)));
                model.setPaymentType(cursor.getString(cursor.getColumnIndex(DataContract.Receipts.COL_PAYMENT_TYPE)));
                model.setChequeNumber(cursor.getString(cursor.getColumnIndex(DataContract.Receipts.COL_CHEQUE_NUMBER)));
                model.setChequeDate(cursor.getString(cursor.getColumnIndex(DataContract.Receipts.COL_CHEQUE_DATE)));
                model.setIs_sync(cursor.getInt(cursor.getColumnIndex(DataContract.Receipts.COL_IS_SYNC)));
                list.add(model);
            } while (cursor.moveToNext());


            adapter=new ReceiptAdapter(this, list, new OnAdapterClickListener() {
                @Override
                public void OnItemClicked(int position) {

                    ReceiptModel receiptModel=list.get(position);
                    String customerName=receiptModel.getCustomerName();
                    String customerCode=receiptModel.getCustomerCode();
                    String salesman=receiptModel.getSalesmanId();
                    String receiptNo=receiptModel.getReceiptNo();
                    String receiptDate=receiptModel.getReceiptDate();
                    String balanceAmount=receiptModel.getBalanceAmount();
                    String receiptAmount=receiptModel.getReceivedAmount();
                    String paymentType=receiptModel.getPaymentType();
                    String chqNumber=receiptModel.getChequeNumber();
                    String chqDate=receiptModel.getChequeDate();

                    Intent intent = new Intent(ListReceiptActivity.this, ReceiptActivity.class);
                    intent.putExtra("TYPE","EDIT");
                    intent.putExtra("CUS_NAME",customerName);
                    intent.putExtra("CUS_CODE",customerCode);
                    intent.putExtra("SALESMAN",salesman);
                    intent.putExtra("RECEIPT_NO",receiptNo);
                    intent.putExtra("RECEIPT_DATE",receiptDate);
                    intent.putExtra("BALANCE_AMOUNT",balanceAmount);
                    intent.putExtra("RECEIVED_AMOUNT",receiptAmount);
                    intent.putExtra("PAYMENT_TYPE",paymentType);
                    intent.putExtra("CHEQUE_NUMBER",chqNumber);
                    intent.putExtra("CHEQUE_DATE",chqDate);
                    startActivity(intent);
                }

                @Override
                public void OnDeleteClicked(int position) {

                }
            });

        } else {
            txtEmpty.setVisibility(View.VISIBLE);
        }
        cursor.close();
        database.close();
    }

    @OnClick(R.id.fab)
    public void onViewClicked() {

        Intent intent = new Intent(ListReceiptActivity.this, CheckCustomerActivity.class);
        startActivity(intent);
    }
}
