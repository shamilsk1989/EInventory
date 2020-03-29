package com.alhikmahpro.www.e_inventory.View;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
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
import com.alhikmahpro.www.e_inventory.Data.Cart;
import com.alhikmahpro.www.e_inventory.Data.ClearData;
import com.alhikmahpro.www.e_inventory.Data.CreatePdf;
import com.alhikmahpro.www.e_inventory.Data.DataContract;
import com.alhikmahpro.www.e_inventory.Data.ItemModel;
import com.alhikmahpro.www.e_inventory.Data.dbHelper;
import com.alhikmahpro.www.e_inventory.FileUtils;
import com.alhikmahpro.www.e_inventory.Interface.OnListAdapterClickListener;
import com.alhikmahpro.www.e_inventory.R;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;

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
    String type;
    private static final int PERMISSION_CODE = 100;
    String invoiceNo = "";
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    MenuItem itemShare, itemClear, itemPrint, itemSync;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_sales);
        ButterKnife.bind(this);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        Intent intent = getIntent();
        type= intent.getStringExtra("Type");
        if(type.equals("SAL")){
            getSupportActionBar().setTitle("Sales");
        }else if(type.equals("ORD")){
            getSupportActionBar().setTitle("Order");
        }

        populateRecycler();


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
        if (list.size() > 0) {
            list.clear();
        }
        layoutManager = new LinearLayoutManager(this);
        docListRv.setLayoutManager(layoutManager);
        docListRv.setItemAnimator(new DefaultItemAnimator());
        docListRv.setHasFixedSize(true);
        dbHelper helper = new dbHelper(ListSalesActivity.this);
        SQLiteDatabase database = helper.getReadableDatabase();


        //select all invoice from invoice table

        Cursor cursor = helper.getInvoice(database);
        if (cursor.moveToFirst()) {

            //txtEmpty.setVisibility(View.GONE);
            do {
                ItemModel model = new ItemModel();
                model.setInvoiceNo(cursor.getString(cursor.getColumnIndex(DataContract.Invoice.COL_INVOICE_NUMBER)));
                model.setInvoiceDate(cursor.getString(cursor.getColumnIndex(DataContract.Invoice.COL_INVOICE_DATE)));
                Log.d(TAG, "populateRecycler: Date " + cursor.getString(cursor.getColumnIndex(DataContract.Invoice.COL_INVOICE_DATE)));
                model.setStaffName(cursor.getString(cursor.getColumnIndex(DataContract.Invoice.COL_SALESMAN_ID)));
                model.setCustomerCode(cursor.getString(cursor.getColumnIndex(DataContract.Invoice.COL_CUSTOMER_CODE)));
                model.setCustomerName(cursor.getString(cursor.getColumnIndex(DataContract.Invoice.COL_CUSTOMER_NAME)));
                model.setTotal(cursor.getDouble(cursor.getColumnIndex(DataContract.Invoice.COL_TOTAL_AMOUNT)));
                model.setDiscount(cursor.getDouble(cursor.getColumnIndex(DataContract.Invoice.COL_DISCOUNT_AMOUNT)));
                model.setNet(cursor.getDouble(cursor.getColumnIndex(DataContract.Invoice.COL_NET_AMOUNT)));
                model.setOtherAmount(cursor.getDouble(cursor.getColumnIndex(DataContract.Invoice.COL_OTHER_AMOUNT)));
                model.setGrandTotal(cursor.getDouble(cursor.getColumnIndex(DataContract.Invoice.COL_GRAND_TOTAL_AMOUNT)));
                model.setServerInvoice(cursor.getString(cursor.getColumnIndex(DataContract.Invoice.COL_SERVER_INVOICE_NUMBER)));
                model.setPaymentType(cursor.getString(cursor.getColumnIndex(DataContract.Invoice.COL_PAYMENT_TYPE)));
                model.setIs_sync(cursor.getInt(cursor.getColumnIndex(DataContract.Invoice.COL_IS_SYNC)));
                list.add(model);
            } while (cursor.moveToNext());


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

//                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//
//                        requestStoragePermission();
//
//                    } else {
//                        //createPdfWrapper();
//                        sharePdf();
//                    }


                }

                @Override
                public void OnDeleteClicked(int position) {

                }
            });

            docListRv.setAdapter(adapter);
            ViewCompat.setNestedScrollingEnabled(docListRv, false);
        } else {
            //txtEmpty.setVisibility(View.VISIBLE);
        }
        cursor.close();
        database.close();
    }

    private void createPdfWrapper() {
//        Log.d(TAG, "createPdfWrapper: ");
//        CreatePdf createPdf = new CreatePdf(this, invoiceNo);
//        createPdf.execute();


    }

    private void goToNext(int position) {
        Cart.mCart.clear();
        ItemModel itemModel = list.get(position);
        invoiceNo = itemModel.getInvoiceNo();

        Log.d(TAG, "goToNext: " + itemModel.getCustomerName());
        dbHelper helper = new dbHelper(this);

        //get invoice details and add to the cart
        helper.getInvoiceDetailsById(invoiceNo);
        Log.d(TAG, "goToNext:cart size " + Cart.mCart.size());
        // Intent intent;

        // syc successfully then goto PrintViewActivity
//        if (itemModel.getIs_sync() == DataContract.SYNC_STATUS_FAILED) {
//            intent = new Intent(ListSalesActivity.this, ViewCartActivity.class);

//        else {
//            intent = new Intent(ListSalesActivity.this, ViewCartActivity.class);
//        }

        Intent intent = new Intent(ListSalesActivity.this, ViewPdfActivity.class);
        intent.putExtra("ACTION", DataContract.ACTION_EDIT);
        intent.putExtra("TYPE", "SAL");
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



    private void requestStoragePermission() {

        Dexter.withActivity(this)
                .withPermissions(
                        Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.READ_EXTERNAL_STORAGE
                )
                .withListener(new MultiplePermissionsListener() {
                    @Override
                    public void onPermissionsChecked(MultiplePermissionsReport report) {
                        //check all permission granted
                        if (report.areAllPermissionsGranted()) {
                            //createPdfWrapper();
                            sharePdf();
                        }
                        //check any permission permanent denied
                        if (report.isAnyPermissionPermanentlyDenied()) {
                            showSettingDialog();
                        }
                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token) {
                        token.continuePermissionRequest();

                    }
                }).check();


//        int writePermission = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
//        int readPermission = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE);
//        List<String> listPermissions = new ArrayList<>();
//        if (writePermission != PackageManager.PERMISSION_GRANTED) {
//            listPermissions.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
//        }
//        if (readPermission != PackageManager.PERMISSION_GRANTED) {
//            listPermissions.add(Manifest.permission.READ_EXTERNAL_STORAGE);
//        }
//        if (!listPermissions.isEmpty()) {
//            ActivityCompat.requestPermissions(this, listPermissions.toArray(new String[listPermissions.size()]), PERMISSION_CODE);
//            return false;
//        }
//        return true;

    }


    private void showSettingDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(ListSalesActivity.this);
        builder.setTitle("Need Permissions");
        builder.setMessage("This app needs permission to use this feature. You can grant them in app settings.");
        builder.setPositiveButton("GOTO SETTINGS", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
                openSettings();
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        builder.show();
    }

    private void openSettings() {
        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        Uri uri = Uri.fromParts("package", getPackageName(), null);
        intent.setData(uri);
        startActivityForResult(intent, 101);
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
        clearData.execute("inv");
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
        intent.putExtra("TYPE", "SAL");

        startActivity(intent);
    }


    private void sharePdf() {
        String fileName = null;
        fileName = FileUtils.getSubDirPath(this, DataContract.DIR_INVOICE) + invoiceNo + ".pdf";
        File outputFile = new File(fileName);
        if (outputFile.exists()) {
            Uri uri = FileProvider.getUriForFile(ListSalesActivity.this, ListSalesActivity.this.getPackageName() + ".provider", outputFile);
            Intent share = new Intent();
            share.setAction(Intent.ACTION_SEND);
            share.setType("application/pdf");
            share.putExtra(Intent.EXTRA_STREAM, uri);
            startActivity(Intent.createChooser(share, "Share to :"));
        } else {
            Toast.makeText(this, "File not found", Toast.LENGTH_SHORT).show();
        }
    }


}


