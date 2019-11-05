package com.alhikmahpro.www.e_inventory.Data;

import android.content.Context;
import android.os.AsyncTask;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Toast;

import com.alhikmahpro.www.e_inventory.R;
import com.alhikmahpro.www.e_inventory.View.ClearDataActivity;

public class ClearData extends AsyncTask<String,Void,Void> {

    Context context;
    AlertDialog alertDialog;
    AlertDialog.Builder builder;
    private static final String TAG = "ClearData";


    public ClearData(Context context) {
        this.context = context;

    }

    @Override
    protected void onPreExecute() {

        ShowProgressDialog();
    }

    @Override
    protected Void doInBackground(String... strings) {

        String type=strings[0];
        dbHelper helper=new dbHelper(context);
        if (type.equals("inv")) {
            helper.deleteInvoice();
            helper.deleteInvoiceDetails();
           // Toast.makeText(context, "Deleted", Toast.LENGTH_SHORT).show();
            Log.d(TAG, "doInBackground: Invoice deleted");

        } else if (type.equals("rec")) {
            helper.deleteReceipt();
            Log.d(TAG, "doInBackground: Receipt deleted");
            //Toast.makeText(context, "Deleted", Toast.LENGTH_SHORT).show();
        }

        return null;
    }



    @Override
    protected void onPostExecute(Void aVoid) {

        HideProgressDialog();
    }

    private void ShowProgressDialog() {
        builder = new AlertDialog.Builder(context);
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View dialogView = inflater.inflate(R.layout.progress, null);
        builder.setView(dialogView);
        builder.setCancelable(false);
        alertDialog = builder.create();
        alertDialog.show();
    }

    private void HideProgressDialog() {

        alertDialog.dismiss();
    }
}
