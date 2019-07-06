package com.alhikmahpro.www.e_inventory.Data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.widget.Toast;


public class SaveSales extends AsyncTask<SaleData,Void,String> {
    Context mContext;

    public SaveSales(Context mContext) {
        this.mContext = mContext;
    }

    @Override
    protected String doInBackground(SaleData... data) {

        dbHelper dbHelper=new dbHelper(mContext);
        SQLiteDatabase database=dbHelper.getReadableDatabase();

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

        if(dbHelper.checkSale(database,invoiceNo))
        {
            database.close();
            return "Already exists";
        }else {
            database.close();
            if(dbHelper.saveInvoice(invoiceNo,invoiceDate,salesmanId,customerCode,customerName,base_total,disc,netAmount,paymentMode,mDate,syncStatus))
                dbHelper.saveInvoiceDetails(invoiceNo,syncStatus);
            database.close();
            return "Saved";
        }

    }

    @Override
    protected void onPostExecute(String result) {
        Toast.makeText(mContext, result, Toast.LENGTH_SHORT).show();
    }
}
