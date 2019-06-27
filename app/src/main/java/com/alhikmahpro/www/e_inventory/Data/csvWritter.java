package com.alhikmahpro.www.e_inventory.Data;

import android.app.ProgressDialog;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;

import com.alhikmahpro.www.e_inventory.R;
import com.opencsv.CSVWriter;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class csvWritter {


    private final Context mContext;
    //private final File dirpath;
    private boolean isFirst = false;
    private static final String TAG = "csvWritter";
    private String folder_path,mDate;
    private int docNo;
    private static String folder_name="PriceChecker";


    public csvWritter(Context mContext,int doc,String dt) {
        this.mContext = mContext;
        this.docNo=doc;
        this.mDate=dt;
        folder_path = createDirectory(getPath(null), folder_name);
        Log.d(TAG, "folder path: "+folder_path);

    }


    public String createDirectory(String path, String folder_name) {

        Log.d(TAG, "createDirectory: in csvWritter");
        boolean success = true;
        File folder = new File(path, folder_name);
        if (!folder.exists()) {
            folder.mkdir();
        }
        return folder.getPath();


    }

    public String getPath(String folder_name) {
        return (folder_name != null) ? Environment.getExternalStorageDirectory() + "/" + folder_name + "/" : Environment.getExternalStorageDirectory().toString();
    }


    public void exportToCSV(final String user) throws Exception {
        Log.d(TAG, "exportToCSV: UserName: "+user);

        //final ArrayList<ItemModel> tempList;
        //tempList = new ArrayList<ItemModel>(RuntimeData.mCartData);
       //Log.d(TAG, "inside exportToCSV-Runtime"+tempList.size());
//
//
       // Log.d(TAG, "inside exportToCSV-temp"+tempList.size());



        String file_path = getPath(folder_name);
        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy-HH:mm:ss");
        final String dateTime = sdf.format(new Date());
        String filename = docNo+"-"+ dateTime;
        Log.d(TAG, "fille is : "+filename);
        File file = new File(file_path + "/" + filename + ".csv");
        final String csvFilename = file_path + filename + ".csv";
        final ProgressDialog progressDialog=new ProgressDialog(mContext);
        progressDialog.setTitle("Exporting...");
        progressDialog.setMessage("Please wait");
        progressDialog.setIndeterminate(true);
        progressDialog.setCancelable(true);
        progressDialog.show();

        final Handler handler = new Handler(Looper.getMainLooper()) {
            @Override
            public void handleMessage(Message msg) {

            }
        };
        new Thread() {

            dbHelper helper=new dbHelper(mContext);
            SQLiteDatabase database = helper.getReadableDatabase();
            Cursor cursor = helper.getStockDetailsByDoc(database, docNo);
            public void run() {

                try {

                    Log.d(TAG, "run: cursor " + cursor.getCount());


                    Log.d(TAG, "run: +creating csv");

                    FileWriter fileWriter = new FileWriter(csvFilename);
                    fileWriter.append("Barcode");
                    fileWriter.append(',');
                    fileWriter.append("quantity");
                    fileWriter.append(',');
                    fileWriter.append("Unit");
                    fileWriter.append(',');
                    fileWriter.append("Product Code");
                    fileWriter.append(',');
                    fileWriter.append("Product Name");
                    fileWriter.append(',');
                    fileWriter.append("Sale price");
                    fileWriter.append(',');
                    fileWriter.append("Cost price");
                    fileWriter.append(',');
                    fileWriter.append("User Name");
                    fileWriter.append(',');
                    fileWriter.append("Date");
                    fileWriter.append('\n');
                    if (cursor.moveToFirst()){

                        do{
                            fileWriter.append(cursor.getString(cursor.getColumnIndex(DataContract.StocksDetails.COL_BAR_CODE)));
                            fileWriter.append(',');
                            fileWriter.append(String.valueOf(cursor.getDouble(cursor.getColumnIndex(DataContract.StocksDetails.COL_QUANTITY))));
                            fileWriter.append(',');
                            fileWriter.append(cursor.getString(cursor.getColumnIndex(DataContract.StocksDetails.COL_UNIT)));
                            fileWriter.append(',');
                            fileWriter.append(cursor.getString(cursor.getColumnIndex(DataContract.StocksDetails.COL_PRODUCT_CODE)));
                            fileWriter.append(',');
                            fileWriter.append(cursor.getString(cursor.getColumnIndex(DataContract.StocksDetails.COL_PRODUCT_NAME)));
                            fileWriter.append(',');
                            fileWriter.append(String.valueOf(cursor.getDouble(cursor.getColumnIndex(DataContract.StocksDetails.COL_SALES_PRICE))));
                            fileWriter.append(',');
                            fileWriter.append(String.valueOf(cursor.getDouble(cursor.getColumnIndex(DataContract.StocksDetails.COL_COST_PRICE))));
                            fileWriter.append(',');
                            fileWriter.append(user);
                            fileWriter.append(',');
                            fileWriter.append(mDate);
                            fileWriter.append('\n');

                        }while (cursor.moveToNext());

                    }





//                    for (ItemModel model : tempList) {
//                        fileWriter.append(model.getBarCode());
//                        fileWriter.append(',');
//                        fileWriter.append(String.valueOf(model.getQty()));
//                        fileWriter.append(',');
//                        fileWriter.append(model.getSelectedPackage());
//                        fileWriter.append(',');
//                        fileWriter.append(model.getProductCode());
//                        fileWriter.append(',');
//                        fileWriter.append(model.getProductName());
//                        fileWriter.append(',');
//                        fileWriter.append(String.valueOf(model.getSalePrice()));
//                        fileWriter.append(',');
//                        fileWriter.append(String.valueOf(model.getCostPrice()));
//                        fileWriter.append(',');
//                        fileWriter.append(user);
//                        fileWriter.append(',');
//                        fileWriter.append(mDate);
//                        fileWriter.append('\n');
//                    }


                    fileWriter.close();

                } catch (Exception e) {
                    //e.printStackTrace();
                }
                handler.sendEmptyMessage(0);
                progressDialog.dismiss();
                cursor.close();
                database.close();


            }
        }.start();

        //Snackbar.make(findViewById(R.id.loginActivity), "Imported", Snackbar.LENGTH_LONG).show();
        Toast.makeText(mContext,"Exported",Toast.LENGTH_LONG).show();
        //tempList.clear();
        //  showSnackbar();


    }


}


