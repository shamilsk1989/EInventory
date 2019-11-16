package com.alhikmahpro.www.e_inventory.Data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;


public class SaveSales extends AsyncTask<SaleData, Void, String> {

    public static interface TaskListener {
        public abstract void onFinished(String result);
    }
    Context mContext;
    private TaskListener taskListener;
    private static final String TAG = "SaveSales";

    public SaveSales(Context mContext,TaskListener taskListener) {
        this.mContext = mContext;
        this.taskListener=taskListener;
    }

    @Override
    protected String doInBackground(SaleData... data) {

        dbHelper dbHelper = new dbHelper(mContext);
        SQLiteDatabase database = dbHelper.getReadableDatabase();

        String customerCode = data[0].customerCode;
        String customerName = data[0].customerName;
        String invoiceNo = data[0].invoiceNo;
        String invoiceDate = data[0].invoiceDate;
        String salesmanId = data[0].salesmanId;
        double base_total = data[0].base_total;
        double disc = data[0].disc;
        double disc_per = data[0].discPer;
        double netAmount = data[0].netAmount;
        String paymentMode = data[0].paymentMode;
        String mDate = data[0].mDate;
        Log.d(TAG, "doInBackground: "+disc_per);
        int syncStatus = data[0].syncStatus;
        if (dbHelper.checkInvoice(database, invoiceNo)) {
            database.close();
            dbHelper.updateInvoice(invoiceNo, invoiceDate, salesmanId, customerCode, customerName, base_total, disc,disc_per, netAmount, paymentMode, mDate, syncStatus);
            dbHelper.updateInvoiceDetails(invoiceNo, syncStatus);

            return "Saved";

        } else {
            database.close();
            dbHelper.saveInvoice(invoiceNo, invoiceDate, salesmanId, customerCode, customerName, base_total, disc,disc_per,netAmount, paymentMode, mDate, syncStatus);
            dbHelper.saveInvoiceDetails(invoiceNo, syncStatus);

            return "Saved";
        }


    }

    @Override
    protected void onPostExecute(String result) {
        if(this.taskListener != null) {

            // And if it is we call the callback function on it.
            this.taskListener.onFinished(result);
        }
    }
}
