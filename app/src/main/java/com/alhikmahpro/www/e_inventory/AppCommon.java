package com.alhikmahpro.www.e_inventory;

import android.app.Activity;
import android.content.Context;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;

public class AppCommon {

    Context mContext;
    AlertDialog dialog;
    AlertDialog.Builder builder;

    public AppCommon(Context mContext) {
        this.mContext = mContext;
    }


    public void ShowProgress(String message){

        builder=new AlertDialog.Builder(mContext);
        LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View dialogView = inflater.inflate(R.layout.progress, null);
        builder.setView(dialogView);
        builder.setCancelable(false);
        dialog = builder.create();
        dialog.show();
    }

    public void HideProgressDialog(){

        dialog.dismiss();
    }



}
