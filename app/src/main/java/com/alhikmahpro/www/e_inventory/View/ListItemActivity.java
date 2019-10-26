package com.alhikmahpro.www.e_inventory.View;

import android.Manifest;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.alhikmahpro.www.e_inventory.Adapter.CartAdapter;
import com.alhikmahpro.www.e_inventory.Data.DataContract;
import com.alhikmahpro.www.e_inventory.Data.ItemModel;
import com.alhikmahpro.www.e_inventory.Data.RuntimeData;
import com.alhikmahpro.www.e_inventory.Data.csvWritter;
import com.alhikmahpro.www.e_inventory.Data.dbHelper;
import com.alhikmahpro.www.e_inventory.FileUtils;
import com.alhikmahpro.www.e_inventory.Interface.OnAdapterClickListener;
import com.alhikmahpro.www.e_inventory.R;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class ListItemActivity extends AppCompatActivity {

    @BindView(R.id.item_list_rv)
    RecyclerView itemListRv;
    @BindView(R.id.txtEmpty)
    TextView txtEmpty;
    @BindView(R.id.btnSave)
    Button btnSave;

    private static final int STORAGE_PERMISSION_CODE = 100;
    ProgressDialog progressDialog;
    String staffName = "";
    String action, mDate;
    int docNo;
    RecyclerView.Adapter adapter;
    RecyclerView.LayoutManager layoutManager;
    private static final String TAG = "ListItemActivity";
    dbHelper helper;
    List<ItemModel> listItem = new ArrayList<>();
    @BindView(R.id.txtCount)
    TextView txtCount;
    @BindView(R.id.toolbar)
    Toolbar toolbar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_item);
        ButterKnife.bind(this);
        progressDialog = new ProgressDialog(this);
        Intent mIntent = getIntent();
        action = mIntent.getStringExtra("ACTION");
        Log.d(TAG, "onCreate Action: " + action);
        docNo = mIntent.getIntExtra("DOC_NO", 0);
        staffName = mIntent.getStringExtra("USER");

        Log.d(TAG, "onCreate: document NO " + docNo + "user " + staffName);
        helper = new dbHelper(this);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        toolbar.setTitle("Document  " + docNo);
        initView();


    }

    @Override
    protected void onResume() {
        super.onResume();

        Log.d(TAG, "onResume: ");
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @OnClick(R.id.btnSave)
    public void onViewClicked() {

        Log.d(TAG, "onViewClicked: ");
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            //csvWritter writer=new csvWritter(getContext(),docNo);
            requestStoragePermission();

        } else {
            saveToDataBase();
        }
    }


    private void initView() {


        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
        mDate = sdf.format(new Date());
        Log.d(TAG, "initView: " + RuntimeData.mCartData.size());

        SQLiteDatabase database = helper.getReadableDatabase();
        Cursor cursor = helper.getStockDetailsByDoc(database, docNo);
        Log.d(TAG, "initView: cursor " + cursor.getCount());
        if (cursor.moveToFirst()) {
            do {
                ItemModel itemModel = new ItemModel();
                itemModel.set_id(cursor.getInt(cursor.getColumnIndex(DataContract.StocksDetails.COL_ID)));
                itemModel.setDocNo(cursor.getInt(cursor.getColumnIndex(DataContract.StocksDetails.COL_DOCUMENT_NUMBER)));
                itemModel.setBarCode(cursor.getString(cursor.getColumnIndex(DataContract.StocksDetails.COL_BAR_CODE)));
                itemModel.setProductCode(cursor.getString(cursor.getColumnIndex(DataContract.StocksDetails.COL_PRODUCT_CODE)));
                itemModel.setProductName(cursor.getString(cursor.getColumnIndex(DataContract.StocksDetails.COL_PRODUCT_NAME)));
                itemModel.setSelectedPackage(cursor.getString(cursor.getColumnIndex(DataContract.StocksDetails.COL_UNIT)));
                itemModel.setQty(cursor.getDouble(cursor.getColumnIndex(DataContract.StocksDetails.COL_QUANTITY)));
                itemModel.setSalePrice(cursor.getDouble(cursor.getColumnIndex(DataContract.StocksDetails.COL_SALES_PRICE)));
                itemModel.setCostPrice(cursor.getDouble(cursor.getColumnIndex(DataContract.StocksDetails.COL_COST_PRICE)));
                listItem.add(itemModel);
            } while (cursor.moveToNext());
        }
        if (listItem.size() > 0) {

            txtEmpty.setVisibility(View.GONE);
            txtCount.setText("Total Items :" + String.valueOf(listItem.size()));
            layoutManager = new LinearLayoutManager(this);
            itemListRv.setLayoutManager(layoutManager);
            itemListRv.setItemAnimator(new DefaultItemAnimator());
            itemListRv.setHasFixedSize(true);
            adapter = new CartAdapter(listItem, new OnAdapterClickListener() {
                @Override
                public void OnItemClicked(int position) {

                }

                @Override
                public void OnDeleteClicked(final int position) {

                    new android.support.v7.app.AlertDialog.Builder(ListItemActivity.this)
                            .setTitle(" Warning ")
                            .setMessage("Do you want to delete !")
                            .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    ItemModel itemModel = listItem.get(position);
                                    int id = itemModel.get_id();
                                    deleteItem(id, position);
                                }
                            })
                            .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                @Override

                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.cancel();
                                }
                            }).create().show();


                }
            });
            itemListRv.setAdapter(adapter);
        } else {
            txtEmpty.setVisibility(View.VISIBLE);
            txtCount.setVisibility(View.GONE);
        }
    }


    private void deleteItem(int id, int position) {


        Log.d(TAG, "deleteItem: ID" + position);

        boolean del = helper.deleteStockDetailsById(id);
        if (del) {

            listItem.remove(position);
            adapter.notifyItemRemoved(position);
            adapter.notifyItemRangeChanged(position, listItem.size());
            txtCount.setText("Total Items :" + String.valueOf(listItem.size()));
            Toast.makeText(this, "Deleted", Toast.LENGTH_SHORT).show();

        } else {
            Toast.makeText(this, "Failed", Toast.LENGTH_SHORT).show();
        }


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
                            saveToDataBase();
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

    }

    private void showSettingDialog() {
        android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(ListItemActivity.this);
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

    private void saveToDataBase() {

        if (listItem.size() > 0) {
            double total = 0;
            for (ItemModel itemModel : RuntimeData.mCartData) {
                total = total + itemModel.getQty() * itemModel.getCostPrice();
            }

            helper.updateStocks(docNo, total);
            writeToCSV();
            Intent i = new Intent(ListItemActivity.this, HomeActivity.class);
// set the new task and clear flags
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(i);
        } else {
            Toast.makeText(this, "No items", Toast.LENGTH_SHORT).show();
        }


    }


    private void alertMessage(String message) {

        new AlertDialog.Builder(ListItemActivity.this)
                .setTitle("Status")
                .setMessage(message)
                .setCancelable(false)
                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                        RuntimeData.mCartData.clear();
                        finish();

                    }
                }).create().show();

    }

    private void writeToCSV() {
        Log.d(TAG, "writeToCSV: ");
        csvWritter writer = new csvWritter(ListItemActivity.this, docNo, mDate);
        try {
            writer.exportToCSV(staffName);
            RuntimeData.mCartData.clear();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }



}
