package com.alhikmahpro.www.e_inventory.View;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
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

import com.alhikmahpro.www.e_inventory.Adapter.ReceiptAdapter;
import com.alhikmahpro.www.e_inventory.AppUtils;
import com.alhikmahpro.www.e_inventory.Data.ClearData;
import com.alhikmahpro.www.e_inventory.Data.DataContract;
import com.alhikmahpro.www.e_inventory.Data.ReceiptModel;
import com.alhikmahpro.www.e_inventory.Data.dbHelper;
import com.alhikmahpro.www.e_inventory.FileUtils;
import com.alhikmahpro.www.e_inventory.Interface.OnListAdapterClickListener;
import com.alhikmahpro.www.e_inventory.R;
import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Chunk;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.FontFactory;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.BaseFont;
import com.itextpdf.text.pdf.PdfWriter;
import com.itextpdf.text.pdf.draw.LineSeparator;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class ListReceiptActivity extends AppCompatActivity {

    @BindView(R.id.doc_list_rv)
    RecyclerView docListRv;
    //    @BindView(R.id.txtEmpty)
//    TextView txtEmpty;
    @BindView(R.id.fab)
    FloatingActionButton fab;
    RecyclerView.Adapter adapter;
    RecyclerView.LayoutManager layoutManager;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    private String type;
    List<ReceiptModel> list;
    private static final String TAG = "ListReceiptActivity";
    String companyName = "xxxx", companyAddress = "xxxx", companyPhone = "xxxx", footer = "xxxx";
    String customerName, customerCode, receiptNo, receiptDate;
    String chqNumber, chqDate, paymentType, salesman, remark;
    String fileName;
    double balanceAmount, receiptAmount;
    AlertDialog alertDialog;
    AlertDialog.Builder builder;
    private static final int PERMISSION_CODE = 100;

    MenuItem itemShare, itemClear, itemPrint, itemSync;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_receipt);
        ButterKnife.bind(this);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle("Receipts");

//        Intent intent = getIntent();
//        type= intent.getStringExtra("Type");

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

    @Override
    protected void onResume() {
        super.onResume();
        loadReceipts();
    }

    private void loadReceipts() {
        list.clear();
        dbHelper helper = new dbHelper(this);
        list = helper.getAllReceipts();
        if (list.size() > 0) {
            layoutManager = new LinearLayoutManager(this);
            docListRv.setLayoutManager(layoutManager);
            docListRv.setItemAnimator(new DefaultItemAnimator());
            docListRv.setHasFixedSize(true);


            adapter = new ReceiptAdapter(this, list, new OnListAdapterClickListener() {
                @Override
                public void OnEditClicked(int position) {

                    // ReceiptModel receiptModel = list.get(position);
//                    customerName = receiptModel.getCustomerName();
//                    customerCode = receiptModel.getCustomerCode();
//                    salesman = receiptModel.getSalesmanId();
//                    receiptNo = receiptModel.getReceiptNo();
//                    receiptDate = receiptModel.getReceiptDate();
//                    balanceAmount = receiptModel.getBalanceAmount();
//                    receiptAmount = receiptModel.getReceivedAmount();
//                    paymentType = receiptModel.getPaymentType();
//                    chqNumber = receiptModel.getChequeNumber();
//                    chqDate = receiptModel.getChequeDate();
//                    remark = receiptModel.getRemark();
//
//                    Log.d(TAG, "OnEditClicked: date" + receiptDate + "number" + receiptNo);
//
//                    Intent intent = new Intent(ListReceiptActivity.this, ReceiptActivity.class);
//                    intent.putExtra("ACTION", DataContract.ACTION_EDIT);
//                    intent.putExtra("CUS_NAME", customerName);
//                    intent.putExtra("CUS_CODE", customerCode);
//                    intent.putExtra("SALESMAN", salesman);
//                    intent.putExtra("RECEIPT_NO", receiptNo);
//                    intent.putExtra("RECEIPT_DATE", receiptDate);
//                    intent.putExtra("BALANCE_AMOUNT", balanceAmount);
//                    intent.putExtra("RECEIVED_AMOUNT", receiptAmount);
//                    intent.putExtra("PAYMENT_TYPE", paymentType);
//                    intent.putExtra("CHEQUE_NUMBER", chqNumber);
//                    intent.putExtra("CHEQUE_DATE", chqDate);
//                    intent.putExtra("REMARK", remark);
//                    startActivity(intent);
                }

                @Override
                public void OnSyncClicked(int position) {

                }

                @Override
                public void OnShareClicked(int position) {
                    Log.d(TAG, "OnShareClicked: ");
//                    ReceiptModel receiptModel = list.get(position);
//                    sharePdf(receiptModel.getReceiptNo());
                    gotoViewPdf(position);

                }

                @Override
                public void OnDeleteClicked(int position) {

                }
            });

            docListRv.setAdapter(adapter);
            ViewCompat.setNestedScrollingEnabled(docListRv, false);

        }
    }

    private void gotoViewPdf(int position) {
        Intent view_pdf = new Intent(this, ReceiptPdfViewActivity.class);
        ReceiptModel receiptModel = list.get(position);
        view_pdf.putExtra("CUSTOMER_NAME", receiptModel.getCustomerName());
        view_pdf.putExtra("CUSTOMER_CODE", receiptModel.getCustomerCode());
        view_pdf.putExtra("SALESMAN_ID", receiptModel.getSalesmanId());
        view_pdf.putExtra("RECEIPT_NO", receiptModel.getReceiptNo());
        view_pdf.putExtra("RECEIPT_DATE", receiptModel.getReceiptDate());
        //view_pdf.putExtra("BAL_AMOUNT",receiptModel.getBalanceAmount());
        view_pdf.putExtra("REC_AMOUNT", receiptModel.getReceivedAmount());
//        view_pdf.putExtra("PAY_TYPE",receiptModel.getPaymentType());
//        view_pdf.putExtra("CHQ_NUMBER",receiptModel.getChequeNumber());
//        view_pdf.putExtra("CHQ_DATE",receiptModel.getChequeDate());
//        view_pdf.putExtra("REMARK",receiptModel.getRemark());
        startActivity(view_pdf);

    }


    @OnClick(R.id.fab)
    public void onViewClicked() {

        Intent intent = new Intent(ListReceiptActivity.this, CheckCustomerActivity.class);
        intent.putExtra("TYPE", "REC");
        startActivity(intent);
    }

    private void sharePdf(String no) {
        String fileName = null;
        fileName = FileUtils.getSubDirPath(this, DataContract.DIR_RECEIPT) + no + ".pdf";
        File outputFile = new File(fileName);
        if (outputFile.exists()) {
            Uri uri = FileProvider.getUriForFile(ListReceiptActivity.this, ListReceiptActivity.this.getPackageName() + ".provider", outputFile);
            Intent share = new Intent();
            share.setAction(Intent.ACTION_SEND);
            share.setType("application/pdf");
            share.putExtra(Intent.EXTRA_STREAM, uri);
            startActivity(Intent.createChooser(share, "Share to :"));
        } else {
            Toast.makeText(this, "File not found", Toast.LENGTH_SHORT).show();
        }


    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
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
        }
        return super.onOptionsItemSelected(item);
    }

    private void alertDialog() {
        new android.app.AlertDialog.Builder(ListReceiptActivity.this)
                .setTitle("Warning")
                .setMessage("Do you want to delete all items!")
                .setPositiveButton("Yes", (dialog, which) -> {
                    clearReceipt();
                })
                .setNegativeButton("No", (dialog, which) -> dialog.cancel()).create().show();
    }

    private void clearReceipt() {

        ClearData clearData = new ClearData(ListReceiptActivity.this);
        clearData.execute("REC");
        loadReceipts();
    }

}
