package com.alhikmahpro.www.e_inventory.Data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

public class SaveGoods extends AsyncTask<GoodsData,Void,String> {
    public SaveGoods() {
    }

    public static interface TaskListener {
        public abstract void onFinished(String result);
    }
    private Context mContext;
    private static final String TAG = "SaveGoods";
    private TaskListener taskListener;
    public SaveGoods(Context mContext, TaskListener taskListener) {
        this.mContext = mContext;
        this.taskListener = taskListener;
}






    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected String doInBackground(GoodsData... data) {
        Log.d(TAG, "doInBackground: ");
        //dbHelper dbHelper=new dbHelper(mContext);
        int docNo = data[0].docNo;
        String orderNo = data[0].orderNo;
        String customerCode = data[0].customerCode;
        String customerName = data[0].customerName;
        String invoiceNo = data[0].invoiceNo;
        String invoiceDate = data[0].invoiceDate;
        String salesmanId = data[0].salesmanId;
        double base_total = data[0].base_total;
        double disc = data[0].disc;
        double netAmount = data[0].netAmount;
        double otherAmount = data[0].otherAmount;
        double gTotal = data[0].gTotal;
        String paymentMode = data[0].paymentMode;
        String mDate = data[0].mDate;
        String serverInvoice = data[0].serverInvoice;
        int syncStatus = data[0].syncStatus;

//        SQLiteDatabase database=dbHelper.getReadableDatabase();
//        // check goods_receive table doc no already exist or not
//        if(dbHelper.checkGoods(docNo)){
//            database.close();
//            Log.d(TAG, "doInBackground: "+serverInvoice);
//
//           dbHelper.updateGoods(docNo,orderNo,customerCode,customerName,invoiceNo,invoiceDate,salesmanId,base_total,disc,netAmount,otherAmount,gTotal,paymentMode,mDate,serverInvoice, syncStatus);
//           dbHelper.updateGoodsDetails1(docNo,syncStatus);
//           return "Saved";
//        }else {
//            Log.d(TAG, "doInBackground: "+serverInvoice);
//            database.close();
//            dbHelper.saveGoods(docNo,orderNo,customerCode,customerName,invoiceNo,invoiceDate,salesmanId,base_total,disc,netAmount,otherAmount,gTotal,paymentMode,mDate,serverInvoice,syncStatus);
//            dbHelper.saveGoodsDetails1(docNo,syncStatus);
//            return "Saved";
//        }
    return null;
    }


    @Override
    protected void onPostExecute(String result) {
        if(this.taskListener != null) {

            // And if it is we call the callback function on it.
            this.taskListener.onFinished(result);
        }
        //Toast.makeText(mContext, result, Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onProgressUpdate(Void... values) {
        super.onProgressUpdate(values);
    }
}
