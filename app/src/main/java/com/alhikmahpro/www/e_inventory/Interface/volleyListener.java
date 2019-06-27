package com.alhikmahpro.www.e_inventory.Interface;

import com.android.volley.VolleyError;

import org.json.JSONArray;
import org.json.JSONObject;

public interface volleyListener {

    public void notifySuccess(String requestType, JSONObject response);
    public void notifyError(String requestType, VolleyError error);

}
