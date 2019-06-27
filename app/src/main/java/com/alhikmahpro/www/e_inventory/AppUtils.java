package com.alhikmahpro.www.e_inventory;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.drawable.ColorDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v7.app.AlertDialog;
import android.text.format.DateFormat;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ProgressBar;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class AppUtils {


    private static ProgressDialog sProgressDialog;

    public static ProgressDialog showProgressDialog(Context context,String Message) {
        sProgressDialog = new ProgressDialog(context);
        sProgressDialog.setCancelable(true);
        sProgressDialog.setCanceledOnTouchOutside(true);
        sProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        sProgressDialog.setMessage(Message);
        sProgressDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        return sProgressDialog;
    }

    public static void hideProgressDialog() {
        if ((sProgressDialog != null) && sProgressDialog.isShowing()) {
            sProgressDialog.cancel();
            sProgressDialog = null;
        }
    }

    public static String getDateTime() {
        SimpleDateFormat dateFormat = new SimpleDateFormat(
                "yyyy-MM-dd", Locale.getDefault());
        Date date = new Date();
        return dateFormat.format(date);

    }
    public static void hideKeyboard(Context context,View view) {
        if (view != null) {
            InputMethodManager methodManager = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
            methodManager.hideSoftInputFromWindow(view.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }



    public static String getMonth() {

        Date date=new Date();
        String monthNumber  = (String) DateFormat.format("MM",   date); // 06
        return monthNumber;
    }

    public static String getYear() {

        Date date=new Date();
        String yearNumber  = (String) DateFormat.format("yyyy",   date); // 06
        return yearNumber;
    }

    public static String getDate() {

        Date date=new Date();
        String dateNumber  = (String) DateFormat.format("dd",   date); // 06
        return dateNumber;
    }



    public static String getFormattedDate() {
        SimpleDateFormat dateFormat = new SimpleDateFormat(
                "dd-MM-yyyy - hh:mm:ss", Locale.getDefault());
        Date date = new Date();
        return dateFormat.format(date);
    }

    public static long getShortDate(){
        String date = new SimpleDateFormat("ddmmyy", Locale.getDefault()).format(new Date());
        return Long.parseLong(date);
    }

    public static String getDateAndTime()
    {
        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
        String str = sdf.format(new Date());
        return str;
    }



    public static boolean isNetworkAvailable(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager
                .getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnectedOrConnecting();
    }


    public static void handleTimeOutError(final Activity context) {
        AppUtils.hideProgressDialog();
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setMessage("Server error")
                .setCancelable(false)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        context.finish();
                        dialog.dismiss();
                    }
                });
        AlertDialog alert = builder.create();
        alert.show();

    }


}
