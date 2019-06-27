package com.alhikmahpro.www.e_inventory.Network;

import android.content.Context;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

public class VolleySingleton {
    public static VolleySingleton mInstance;
    private RequestQueue requestQueue;
    public static Context ctx;

    private VolleySingleton(Context context){
        ctx=context;
        requestQueue=getRequestQueue();
    }


    public RequestQueue getRequestQueue(){
        if(requestQueue==null){
            requestQueue= Volley.newRequestQueue(ctx.getApplicationContext());
        }
        return requestQueue;

    }

    public static synchronized VolleySingleton getInstance(Context context){

        if(mInstance==null){
            mInstance=new VolleySingleton(context);
        }

        return mInstance;
    }

    public <T>void addToRequestQueue(Request<T> request){

        requestQueue.add(request);

    }
}
