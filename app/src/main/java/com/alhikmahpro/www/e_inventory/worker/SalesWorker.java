package com.alhikmahpro.www.e_inventory.worker;

import android.arch.persistence.room.Database;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.annotation.NonNull;
import android.util.Log;

import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.alhikmahpro.www.e_inventory.Data.Cart;
import com.alhikmahpro.www.e_inventory.Data.CartModel;
import com.alhikmahpro.www.e_inventory.Data.DataContract;
import com.alhikmahpro.www.e_inventory.Data.SessionHandler;
import com.alhikmahpro.www.e_inventory.Data.dbHelper;
import com.alhikmahpro.www.e_inventory.Network.VolleySingleton;
import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkError;
import com.android.volley.NoConnectionError;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.ServerError;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class SalesWorker extends Worker {

    private static final String TAG = "SalesWorker";
    dbHelper helper;

    public SalesWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
    }

    @NonNull
    @Override
    public Result doWork() {

//        helper=new dbHelper(getApplicationContext());
//        JSONObject jsonObjectResult = generateJson();
//
//        if(jsonObjectResult.length()>0){
//            syncData(jsonObjectResult);
//        }
//
//
//        Log.d(TAG, "doWork " + jsonObjectResult);
        return Result.SUCCESS;
    }


//    private JSONObject generateJson() {
//
//        JSONArray invoiceArray = new JSONArray();
//        JSONArray invoiceDetailsArray = new JSONArray();
//        JSONArray receiptArray = new JSONArray();
//        JSONObject result = new JSONObject();
//        String invoiceNo;
//        SQLiteDatabase database = helper.getReadableDatabase();
//
//        // select all un syc invoice
//
//        Cursor cursor = helper.getAllUnSyncInvoice(database);
//        Log.d(TAG, "generateSalesJson: " + cursor.getCount());
//        if (cursor.moveToFirst()) {
//            invoiceNo = cursor.getString(cursor.getColumnIndex(DataContract.Invoice.COL_INVOICE_NUMBER));
//
//            JSONObject invoiceObject = new JSONObject();
//            try {
//                invoiceObject.put(DataContract.Invoice.COL_INVOICE_NUMBER, invoiceNo);
//                invoiceObject.put(DataContract.Invoice.COL_INVOICE_DATE, cursor.getString(cursor.getColumnIndex(DataContract.Invoice.COL_INVOICE_DATE)).substring(0, 10));
//                invoiceObject.put(DataContract.Invoice.COL_CUSTOMER_CODE, cursor.getString(cursor.getColumnIndex(DataContract.Invoice.COL_CUSTOMER_CODE)));
//                invoiceObject.put(DataContract.Invoice.COL_CUSTOMER_NAME, cursor.getString(cursor.getColumnIndex(DataContract.Invoice.COL_CUSTOMER_NAME)));
//                invoiceObject.put(DataContract.Invoice.COL_SALESMAN_ID, cursor.getString(cursor.getColumnIndex(DataContract.Invoice.COL_SALESMAN_ID)));
//                invoiceObject.put(DataContract.Invoice.COL_TOTAL_AMOUNT, cursor.getString(cursor.getColumnIndex(DataContract.Invoice.COL_TOTAL_AMOUNT)));
//                invoiceObject.put(DataContract.Invoice.COL_DISCOUNT_AMOUNT, cursor.getString(cursor.getColumnIndex(DataContract.Invoice.COL_DISCOUNT_AMOUNT)));
//                invoiceObject.put(DataContract.Invoice.COL_NET_AMOUNT, cursor.getString(cursor.getColumnIndex(DataContract.Invoice.COL_NET_AMOUNT)));
//                invoiceObject.put(DataContract.Invoice.COL_PAYMENT_TYPE, cursor.getString(cursor.getColumnIndex(DataContract.Invoice.COL_PAYMENT_TYPE)));
//                invoiceArray.put(invoiceObject);
//            } catch (JSONException e) {
//                e.printStackTrace();
//            }
//
//
//            // select invoice details by invoice id and save to mCart
//            helper.getInvoiceDetailsById(invoiceNo);
//            if (Cart.mCart.size() > 0) {
//                int slNo = 0;
//                for (CartModel cartModel : Cart.mCart) {
//                    slNo++;
//
//                    JSONObject detailsObject = new JSONObject();
//                    try {
//                        detailsObject.put("serial_no", slNo);
//                        detailsObject.put(DataContract.InvoiceDetails.COL_INVOICE_NUMBER, invoiceNo);
//                        detailsObject.put(DataContract.InvoiceDetails.COL_BAR_CODE, cartModel.getBarcode());
//                        detailsObject.put(DataContract.InvoiceDetails.COL_PRODUCT_CODE, cartModel.getProductCode());
//                        detailsObject.put(DataContract.InvoiceDetails.COL_PRODUCT_NAME, cartModel.getProductName());
//                        detailsObject.put(DataContract.InvoiceDetails.COL_QUANTITY, cartModel.getQty());
//                        detailsObject.put(DataContract.InvoiceDetails.COL_UNIT1, cartModel.getUnit1());
//                        detailsObject.put(DataContract.InvoiceDetails.COL_UNIT2, cartModel.getUnit2());
//                        detailsObject.put(DataContract.InvoiceDetails.COL_UNIT3, cartModel.getUnit3());
//                        detailsObject.put(DataContract.InvoiceDetails.COL_UNIT, cartModel.getSelectedUnit());
//                        detailsObject.put(DataContract.InvoiceDetails.COL_UN_QTY1, cartModel.getUnit1Qty());
//                        detailsObject.put(DataContract.InvoiceDetails.COL_UN_QTY2, cartModel.getUnit2Qty());
//                        detailsObject.put(DataContract.InvoiceDetails.COL_UN_QTY3, cartModel.getUnit3Qty());
//                        detailsObject.put(DataContract.InvoiceDetails.COL_RATE, cartModel.getRate());
//                        detailsObject.put(DataContract.InvoiceDetails.COL_DISCOUNT, cartModel.getDiscount());
//                        detailsObject.put(DataContract.InvoiceDetails.COL_NET_AMOUNT, cartModel.getNet());
//                        detailsObject.put(DataContract.InvoiceDetails.COL_NET_AMOUNT, cartModel.getNet());
//                        detailsObject.put(DataContract.InvoiceDetails.COL_NET_AMOUNT, cartModel.getNet());
//                        detailsObject.put(DataContract.InvoiceDetails.COL_SALE_TYPE, cartModel.getSaleType());
//                        invoiceDetailsArray.put(detailsObject);
//                    } catch (JSONException e) {
//                        e.printStackTrace();
//                    }
//                }
//            }
//        }
//        cursor.close();
//        database.close();
//
//        // select all un sync Receipts
//
//        SQLiteDatabase database1=helper.getReadableDatabase();
//        Cursor cursorReceipts = helper.getAllUnSyncReceipts(database1);
//        if (cursorReceipts.moveToFirst()) {
//            JSONObject receiptObject = new JSONObject();
//            try {
//                receiptObject.put(DataContract.Receipts.COL_RECEIPT_NUMBER,cursorReceipts.getString(cursorReceipts.getColumnIndex(DataContract.Receipts.COL_RECEIPT_NUMBER)));
//                receiptObject.put(DataContract.Receipts.COL_RECEIPT_DATE, cursorReceipts.getString(cursorReceipts.getColumnIndex(DataContract.Receipts.COL_RECEIPT_DATE)));
//                receiptObject.put(DataContract.Receipts.COL_CUSTOMER_CODE, cursorReceipts.getString(cursorReceipts.getColumnIndex(DataContract.Receipts.COL_CUSTOMER_CODE)));
//                receiptObject.put(DataContract.Receipts.COL_CUSTOMER_NAME, cursorReceipts.getString(cursorReceipts.getColumnIndex(DataContract.Receipts.COL_CUSTOMER_NAME)));
//                receiptObject.put(DataContract.Receipts.COL_RECEIVED_AMOUNT, cursorReceipts.getString(cursorReceipts.getColumnIndex(DataContract.Receipts.COL_RECEIVED_AMOUNT)));
//                receiptObject.put(DataContract.Receipts.COL_CHEQUE_NUMBER, cursorReceipts.getString(cursorReceipts.getColumnIndex(DataContract.Receipts.COL_CHEQUE_NUMBER)));
//                receiptObject.put(DataContract.Receipts.COL_CHEQUE_DATE, cursorReceipts.getString(cursorReceipts.getColumnIndex(DataContract.Receipts.COL_CHEQUE_DATE)));
//                receiptObject.put(DataContract.Receipts.COL_REMARK,cursorReceipts.getString(cursorReceipts.getColumnIndex(DataContract.Receipts.COL_REMARK)));
//                receiptArray.put(receiptObject);
//
//            } catch (JSONException e) {
//                e.printStackTrace();
//            }
//        }
//
//        cursorReceipts.close();
//        database1.close();
//
//        // add all into single result json object
//        try {
//            result.put("Invoice", invoiceArray);
//            result.put("Details", invoiceDetailsArray);
//            result.put("Receipts",receiptArray);
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }
//        return result;
//    }

//    private void syncData(JSONObject jsonObject){
//
//        String BASE_URL = SessionHandler.getInstance(getApplicationContext()).getHost();
//        Log.d(TAG, "syncData: Base url :"+BASE_URL);
//        Log.d(TAG, "syncData:posting  "+jsonObject);
//        String Url="http://"+BASE_URL+"/PriceChecker/sync_data.php";//BASE_URL+"/PriceChecker/sync_data.php";
//
//        JsonObjectRequest request=new JsonObjectRequest(Request.Method.POST, Url, jsonObject, new Response.Listener<JSONObject>() {
//            @Override
//            public void onResponse(JSONObject response) {
//                Log.d(TAG, "onResponse: "+response);
//                try {
//                    String res=response.getString("Status");
//
//                    if(res.equals("success")){
//                        helper.updateAllInvoiceSync();
//                        helper.updateAllReceiptSync();
//                    }
//                } catch (JSONException e) {
//                    e.printStackTrace();
//                }
//
//
//            }
//        }, error -> {
//            Log.d(TAG, "onErrorResponse: "+error);
//            VolleyErrorHandler(error);
//
//        }){
//            @Override
//            public Map<String, String> getHeaders() throws AuthFailureError {
//
//                SQLiteDatabase sqLiteDatabase = helper.getReadableDatabase();
//                Cursor cursor = helper.getSettings(sqLiteDatabase);
//                HashMap<String,String> headers=new HashMap<>();
//                if (cursor.moveToFirst()) {
//
//                    headers.put("Content-Type", "application/json");
//                    headers.put("CompanyCode", cursor.getString(cursor.getColumnIndex(DataContract.Settings.COL_COMPANY_CODE)));
//                    headers.put("CompanyName", cursor.getString(cursor.getColumnIndex(DataContract.Settings.COL_COMPANY_NAME)));
//                    headers.put("DeviceId", cursor.getString(cursor.getColumnIndex(DataContract.Settings.COL_DEVICE_ID)));
//                    headers.put("BranchCode", cursor.getString(cursor.getColumnIndex(DataContract.Settings.COL_BRANCH_CODE)));
//                    headers.put("PeriodCode", cursor.getString(cursor.getColumnIndex(DataContract.Settings.COL_PERIOD_CODE)));
//                    headers.put("LocationCode", cursor.getString(cursor.getColumnIndex(DataContract.Settings.COL_LOCATION_CODE)));
//
//
//                }
//                cursor.close();
//                sqLiteDatabase.close();
//                return headers;
//            }
//        };
//
//
//        request.setRetryPolicy(new DefaultRetryPolicy(20*1000,2,DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
//        VolleySingleton.getInstance(getApplicationContext()).addToRequestQueue(request);
//
//
//
//    }

    private void VolleyErrorHandler(VolleyError error) {
        if(error instanceof TimeoutError){
            Log.d(TAG, "time out : "+error.getMessage());
        }
        if(error instanceof NoConnectionError){
            Log.d(TAG, "NoConnectionError : "+error.getMessage());
        }
        if(error instanceof AuthFailureError){
            Log.d(TAG, "AuthFailureError: "+error.getMessage());
        }
        if(error instanceof ServerError){
            Log.d(TAG, "ServerError: "+error.getMessage());
        }if(error instanceof NetworkError){
            Log.d(TAG, "NetworkError : "+error.getMessage());
        }
        if(error instanceof ParseError){
            Log.d(TAG, "ParseError : "+error.getMessage());
        }


    }
}
