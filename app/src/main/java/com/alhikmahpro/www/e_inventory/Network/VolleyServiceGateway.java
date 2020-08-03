package com.alhikmahpro.www.e_inventory.Network;

import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.preference.PreferenceManager;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Toast;

import com.alhikmahpro.www.e_inventory.AppUtils;
import com.alhikmahpro.www.e_inventory.Data.DataContract;
import com.alhikmahpro.www.e_inventory.Data.SessionHandler;
import com.alhikmahpro.www.e_inventory.Data.dbHelper;
import com.alhikmahpro.www.e_inventory.Interface.volleyListener;
import com.alhikmahpro.www.e_inventory.R;
import com.alhikmahpro.www.e_inventory.View.PaymentActivity;
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
    AlertDialog alertDialog;
    AlertDialog.Builder builder;
    private static final String TAG = "VolleyServiceGateway";
    public static String PREF_KEY_URL = "key_server_url";
    public static String PREF_KEY_CCODE = "key_company_code";
    public static String PREF_KEY_CNAME = "key_company_name";
    public static String PREF_KEY_BCODE = "key_branch_code";
    public static String PREF_KEY_PCODE= "key_period";
    public static String PREF_KEY_DEVICE = "key_employee";
    public static String PREF_KEY_LOCATION = "key_location";
    //String companyCode,companyName,deviceId,branchCode,periodCode,locationCode,BASE_URL;

    public VolleyServiceGateway(volleyListener mListener, Context context) {
        this.mListener = mListener;
        this.mContext = context;
    }

    public void postDataVolley(final String requestType, String url, JSONObject postObj){

        ShowProgressDialog();
        SharedPreferences sharedPreferences= PreferenceManager.getDefaultSharedPreferences(mContext);
        String BASE_URL=sharedPreferences.getString(PREF_KEY_URL,"0");

        //String BASE_URL = SessionHandler.getInstance(mContext).getHost();
        BASE_URL=BASE_URL.trim();
        Log.d(TAG, "getDataVolley Base url: "+BASE_URL);
        String postUrl="http://" + BASE_URL + "/" + url;
                //"http://" + BASE_URL + "/" + url;
        Log.d(TAG, "getDataVolley: postUrl"+postUrl);
        Log.d(TAG, "getDataVolley: postData:"+postObj);

        try{
            JsonObjectRequest request=new JsonObjectRequest(Request.Method.POST, postUrl, postObj,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            HideProgressDialog();
                            Log.d(TAG, "onResponse in Volley Service: "+response);
                            if(mListener!=null){
                                mListener.notifySuccess(requestType,response);
                            }

                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.d(TAG, "onErrorResponse: "+error);
                    HideProgressDialog();
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
        ShowProgressDialog();

        SharedPreferences sharedPreferences= PreferenceManager.getDefaultSharedPreferences(mContext);
        String BASE_URL=sharedPreferences.getString(PREF_KEY_URL,"0");
        BASE_URL=BASE_URL.trim();
        Log.d(TAG, "getDataVolley Base url: "+BASE_URL);
        String postUrl="http://" + BASE_URL + "/" + url;
        Log.d(TAG, "getDataVolley: postUrl"+postUrl);

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, postUrl, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                       HideProgressDialog();
                        Log.d(TAG, "onResponse: Test"+response);
                        if(mListener!=null)
                            mListener.notifySuccess(requestType,response);
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                HideProgressDialog();
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
        SharedPreferences sharedPreferences= PreferenceManager.getDefaultSharedPreferences(mContext);
        Log.d(TAG, "makeHeader: Device ID "+sharedPreferences.getString("key_employee","0"));
        HashMap<String, String> headers = new HashMap<String, String>();
        headers.put("Content-Type", "application/json");
        headers.put("CompanyCode",sharedPreferences.getString(PREF_KEY_CCODE,"0"));
        headers.put("CompanyName",sharedPreferences.getString(PREF_KEY_CNAME,"0"));
        headers.put("DeviceId", sharedPreferences.getString(PREF_KEY_DEVICE,"0"));
        headers.put("BranchCode", sharedPreferences.getString(PREF_KEY_BCODE,"0"));
        headers.put("PeriodCode", sharedPreferences.getString(PREF_KEY_PCODE,"0"));
        headers.put("LocationCode",sharedPreferences.getString(PREF_KEY_LOCATION,"0"));


//        dbHelper helper = new dbHelper(mContext);
//        SQLiteDatabase sqLiteDatabase = helper.getReadableDatabase();
//        Cursor cursor = helper.getSettings(sqLiteDatabase);
//        if (cursor.moveToFirst()) {
//
//            headers.put("Content-Type", "application/json");
//            headers.put("CompanyCode", cursor.getString(cursor.getColumnIndex(DataContract.Settings.COL_COMPANY_CODE)));
//            headers.put("CompanyName", cursor.getString(cursor.getColumnIndex(DataContract.Settings.COL_COMPANY_NAME)));
//            headers.put("DeviceId", cursor.getString(cursor.getColumnIndex(DataContract.Settings.COL_DEVICE_ID)));
//            headers.put("BranchCode", cursor.getString(cursor.getColumnIndex(DataContract.Settings.COL_BRANCH_CODE)));
//            headers.put("PeriodCode", cursor.getString(cursor.getColumnIndex(DataContract.Settings.COL_PERIOD_CODE)));
//            headers.put("LocationCode", cursor.getString(cursor.getColumnIndex(DataContract.Settings.COL_LOCATION_CODE)));
//        }
//        cursor.close();
//        sqLiteDatabase.close();
        Log.d(TAG, "makeHeader: "+headers);

        return headers;

    }

    private void HandlingVolleyError(VolleyError error) {
        if (error instanceof TimeoutError) {

            Log.d("Volley", "Network timeout: ");
        } else if (error instanceof NoConnectionError) {

            Log.d(TAG, "NoConnectionError: " + error.getMessage());

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

    private void ShowProgressDialog() {
        builder = new AlertDialog.Builder(mContext);
        LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
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
