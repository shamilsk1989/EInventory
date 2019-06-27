package com.alhikmahpro.www.e_inventory.Network;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import android.widget.Toast;

import com.alhikmahpro.www.e_inventory.AppUtils;
import com.alhikmahpro.www.e_inventory.Data.DataContract;
import com.alhikmahpro.www.e_inventory.Data.SessionHandler;
import com.alhikmahpro.www.e_inventory.Data.dbHelper;
import com.alhikmahpro.www.e_inventory.Interface.volleyListener;
import com.alhikmahpro.www.e_inventory.View.PriceCheckerActivity;
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
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class VolleyServiceGateway {

    volleyListener mListener=null;
    Context mContext;
    private static final String TAG = "VolleyServiceGateway";
    //String companyCode,companyName,deviceId,branchCode,periodCode,locationCode,BASE_URL;

    public VolleyServiceGateway(volleyListener mListener, Context context) {
        this.mListener = mListener;
        this.mContext = context;
    }

    public void postDataVolley(final String requestType, String url, JSONObject postObj){

        if(!AppUtils.isNetworkAvailable(mContext)){
            Toast.makeText(mContext, "No Internet ", Toast.LENGTH_SHORT).show();
            return;

        }

        String BASE_URL = SessionHandler.getInstance(mContext).getHost();
        BASE_URL=BASE_URL.trim();
        Log.d(TAG, "getDataVolley Base url: "+BASE_URL);
        String postUrl="http://" + BASE_URL + "/" + url;
        Log.d(TAG, "getDataVolley: postUrl"+postUrl);
        Log.d(TAG, "getDataVolley: postData:"+postObj);

        try{
            JsonObjectRequest request=new JsonObjectRequest(Request.Method.POST, postUrl, postObj,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {

                            Log.d(TAG, "onResponse in Volley Service: "+response);
                            if(mListener!=null){
                                mListener.notifySuccess(requestType,response);
                            }

                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {

                    HandlingVolleyError(error);
                    if(mListener!=null){
                        mListener.notifyError(requestType,error);
                    }
                }
            }){

                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    //HashMap<String, String> headers = makeHeader();
                    return makeHeader();
                }
            };

            request.setRetryPolicy(new DefaultRetryPolicy(20 * 1000, 2, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
            VolleySingleton.getInstance(mContext).addToRequestQueue(request);


        }catch (Exception e){

        }
    }








    public void getDataVolley(final String requestType, String url){
//        final ProgressDialog progressDialog = new ProgressDialog(this);
//        progressDialog.setMessage("Loading...");
//        progressDialog.show();

        String BASE_URL = SessionHandler.getInstance(mContext).getHost();
        BASE_URL=BASE_URL.trim();
        Log.d(TAG, "getDataVolley Base url: "+BASE_URL);
        String postUrl="http://" + BASE_URL + "/" + url;
        Log.d(TAG, "getDataVolley: postUrl"+postUrl);

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, postUrl, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                       // progressDialog.dismiss();
                        //closeKey();
                        Log.d(TAG, "onResponse: Test"+response);
                        if(mListener!=null)
                            mListener.notifySuccess(requestType,response);
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                HandlingVolleyError(error);
                if(mListener!=null)
                    mListener.notifyError(requestType,error);
            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
//                               HashMap<String, String> headers = new HashMap<String,String>();
//
//                headers.put("Content-Type", "application/json");
//                headers.put("CompanyCode","C1");

                return makeHeader();
            }
        };


        request.setRetryPolicy(new DefaultRetryPolicy(20 * 1000, 2, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        VolleySingleton.getInstance(mContext).addToRequestQueue(request);




    }

    public HashMap<String,String> makeHeader(){

        dbHelper helper = new dbHelper(mContext);
        SQLiteDatabase sqLiteDatabase = helper.getReadableDatabase();
        Cursor cursor = helper.getSettings(sqLiteDatabase);
        HashMap<String, String> headers = new HashMap<String, String>();
        if (cursor.moveToFirst()) {

            headers.put("Content-Type", "application/json");
            headers.put("CompanyCode", cursor.getString(cursor.getColumnIndex(DataContract.Settings.COL_COMPANY_CODE)));
            headers.put("CompanyName", cursor.getString(cursor.getColumnIndex(DataContract.Settings.COL_COMPANY_NAME)));
            headers.put("DeviceId", cursor.getString(cursor.getColumnIndex(DataContract.Settings.COL_DEVICE_ID)));
            headers.put("BranchCode", cursor.getString(cursor.getColumnIndex(DataContract.Settings.COL_BRANCH_CODE)));
            headers.put("PeriodCode", cursor.getString(cursor.getColumnIndex(DataContract.Settings.COL_PERIOD_CODE)));
            headers.put("LocationCode", cursor.getString(cursor.getColumnIndex(DataContract.Settings.COL_LOCATION_CODE)));


        }
        cursor.close();
        sqLiteDatabase.close();

        return headers;

    }

    private void HandlingVolleyError(VolleyError error) {
        if (error instanceof TimeoutError) {

            Log.d("Volley", "Network timeout: ");
        } else if (error instanceof NoConnectionError) {

            Log.d(TAG, "HandlingVolleyError: " + error.getMessage());

        } else if (error instanceof AuthFailureError) {
            Log.d("Volley", "AuthFailureError: ");


        } else if (error instanceof ServerError) {
            Log.d("Volley", "ServerError: ");

        } else if (error instanceof NetworkError) {
            Log.d("Volley", "NetworkError: ");


        } else if (error instanceof ParseError) {
            Log.d("Volley", "ParseError: ");


        }
    }



}
