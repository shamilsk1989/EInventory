package com.alhikmahpro.www.e_inventory.Data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.widget.Toast;

public class SaveGoods extends AsyncTask<GoodsData,Void,String> {
    Context mContext;

    public SaveGoods(Context mContext) {
        this.mContext = mContext;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected String doInBackground(GoodsData... data) {
        dbHelper dbHelper=new dbHelper(mContext);
        int docNo=data[0].docNo;
        String orderNo=data[0].orderNo;
        String customerCode=data[0].customerCode;
        String customerName=data[0].customerName;
        String invoiceNo=data[0].invoiceNo;
        String invoiceDate=data[0].invoiceDate;
        String salesmanId=data[0].salesmanId;
        double base_total=data[0].base_total;
        double disc=data[0].disc;
        double netAmount=data[0].netAmount;
        String paymentMode=data[0].paymentMode;
        String mDate=data[0].mDate;
        int syncStatus=data[0].syncStatus;

        SQLiteDatabase database=dbHelper.getReadableDatabase();
        // check goods_receive table doc no already exist or not
        if(dbHelper.checkGoods(database,docNo)){
            database.close();
           return "Already Exists";
        }else {
            database.close();
            if(dbHelper.saveGoods(docNo,orderNo,customerCode,customerName,invoiceNo,invoiceDate,salesmanId,base_total,disc,netAmount,paymentMode,mDate,syncStatus))
                dbHelper.saveGoodsDetails1(docNo,syncStatus);
            return "Saved";
        }
    }


    @Override
    protected void onPostExecute(String result) {
        Toast.makeText(mContext, result, Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onProgressUpdate(Void... values) {
        super.onProgressUpdate(values);
    }
}
