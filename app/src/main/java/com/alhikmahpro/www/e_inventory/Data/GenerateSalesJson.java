package com.alhikmahpro.www.e_inventory.Data;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;

import com.alhikmahpro.www.e_inventory.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

public class GenerateSalesJson extends AsyncTask<Void,Void, JSONObject> {
    public  interface TaskListener {
          void onFinished(JSONObject result);
    }
    private Context context;
    private TaskListener taskListener;
    private AlertDialog alertDialog;
    private AlertDialog.Builder builder;
    public GenerateSalesJson(Context context, TaskListener taskListener) {
        this.context = context;
        this.taskListener = taskListener;
    }



    @Override
    protected void onPreExecute() {
        ShowProgressDialog();
        super.onPreExecute();
    }


    @Override
    protected JSONObject doInBackground(Void... voids) {
        dbHelper helper=new dbHelper(context);
        JSONObject result = new JSONObject();
        List<ItemModel> unSyncSaleList;
        int slNo = 0;
        SharedPreferences sharedPreferences= PreferenceManager.getDefaultSharedPreferences(context);
        String  salesmanId=sharedPreferences.getString("key_employee","0");

        unSyncSaleList = helper.getAllUnSyncInvoice();
        if (unSyncSaleList.size() > 0) {
            JSONArray invoiceArray = new JSONArray();
            JSONArray detailsArray = new JSONArray();
            for (ItemModel model : unSyncSaleList) {
                JSONObject invoiceObject = new JSONObject();
                try {
                    invoiceObject.put(DataContract.Invoice.COL_INVOICE_NUMBER, model.getInvoiceNo());
                    invoiceObject.put(DataContract.Invoice.COL_INVOICE_DATE, model.getInvoiceDate());
                    invoiceObject.put(DataContract.Invoice.COL_CUSTOMER_CODE, model.getCustomerCode());
                    invoiceObject.put(DataContract.Invoice.COL_CUSTOMER_NAME, model.getCustomerName());
                    invoiceObject.put(DataContract.Invoice.COL_SALESMAN_ID, salesmanId);
                    invoiceObject.put(DataContract.Invoice.COL_TOTAL_AMOUNT, model.getTotal());
                    invoiceObject.put(DataContract.Invoice.COL_DISCOUNT_AMOUNT, model.getDiscount());
                    invoiceObject.put(DataContract.Invoice.COL_DISCOUNT_PERCENTAGE, model.getDiscountPercentage());
                    invoiceObject.put(DataContract.Invoice.COL_OTHER_AMOUNT, model.getOtherAmount());
                    invoiceObject.put(DataContract.Invoice.COL_NET_AMOUNT, model.getNet());
                    invoiceObject.put(DataContract.Invoice.COL_PAYMENT_TYPE, model.getPaymentType());
                    invoiceArray.put(invoiceObject);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            for (ItemModel model : unSyncSaleList) {
                String inv = model.getInvoiceNo();
                helper.getInvoiceDetailsById(inv);
                if (Cart.mCart.size() > 0) {
                    for (CartModel cartModel : Cart.mCart) {
                        slNo++;
                        JSONObject detailsObject = new JSONObject();
                        try {
                            detailsObject.put("serial_no", slNo);
                            detailsObject.put(DataContract.InvoiceDetails.COL_INVOICE_NUMBER, inv);
                            detailsObject.put(DataContract.InvoiceDetails.COL_BAR_CODE, cartModel.getBarcode());
                            detailsObject.put(DataContract.InvoiceDetails.COL_PRODUCT_CODE, cartModel.getProductCode());
                            detailsObject.put(DataContract.InvoiceDetails.COL_PRODUCT_NAME, cartModel.getProductName());
                            detailsObject.put(DataContract.InvoiceDetails.COL_QUANTITY, cartModel.getQty());
                            detailsObject.put(DataContract.InvoiceDetails.COL_UNIT1, cartModel.getUnit1());
                            detailsObject.put(DataContract.InvoiceDetails.COL_UNIT2, cartModel.getUnit2());
                            detailsObject.put(DataContract.InvoiceDetails.COL_UNIT3, cartModel.getUnit3());
                            detailsObject.put(DataContract.InvoiceDetails.COL_UNIT, cartModel.getSelectedUnit());
                            detailsObject.put(DataContract.InvoiceDetails.COL_UN_QTY1, cartModel.getUnit1Qty());
                            detailsObject.put(DataContract.InvoiceDetails.COL_UN_QTY2, cartModel.getUnit2Qty());
                            detailsObject.put(DataContract.InvoiceDetails.COL_UN_QTY3, cartModel.getUnit3Qty());
                            detailsObject.put(DataContract.InvoiceDetails.COL_RATE, cartModel.getRate());
                            detailsObject.put(DataContract.InvoiceDetails.COL_DISCOUNT, cartModel.getDiscount());
                            detailsObject.put(DataContract.InvoiceDetails.COL_NET_AMOUNT, cartModel.getNet());
                            detailsObject.put(DataContract.InvoiceDetails.COL_NET_AMOUNT, cartModel.getNet());
                            detailsObject.put(DataContract.InvoiceDetails.COL_NET_AMOUNT, cartModel.getNet());
                            detailsObject.put(DataContract.InvoiceDetails.COL_SALE_TYPE, cartModel.getSaleType());
                            detailsArray.put(detailsObject);

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }

                try {
                    result.put("Invoice", invoiceArray);
                    result.put("Details", detailsArray);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }


        return result;
    }


    @Override
    protected void onPostExecute(JSONObject result) {
        HideProgressDialog();
        if(this.taskListener!=null){
            this.taskListener.onFinished(result);
        }
        super.onPostExecute(result);
    }

    @Override
    protected void onProgressUpdate(Void... values) {
        super.onProgressUpdate(values);
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
