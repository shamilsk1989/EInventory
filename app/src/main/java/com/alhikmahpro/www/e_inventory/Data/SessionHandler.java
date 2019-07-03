package com.alhikmahpro.www.e_inventory.Data;

import android.content.Context;
import android.content.SharedPreferences;

public class SessionHandler {

    private static final String PREF_NAME = "EInventorySession";
    private static final String USER_TYPE = "user_type";
    private static final String INVENTORY="inventory_module";
    private static final String GOODS_RECEIVE="goods_receive_module";
    private static final String SALE="sale_module";
    private static final String APP_FIRST_TIME="app_first";
    private static final String HOST = "host";
    private static final String PRINTER_NAME="printer_name";
    private static SessionHandler sInstance;
    private final SharedPreferences mPref;


    public SessionHandler(Context context) {
        mPref = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
    }

    public static synchronized SessionHandler getInstance(Context context) {
        if (sInstance == null) {
            sInstance = new SessionHandler(context);
        }
        return sInstance;
    }

    public void setHost(String ip){

        SharedPreferences.Editor editor = mPref.edit();
        editor.putString(HOST,ip);
        editor.apply();

    }
    public String getHost(){
        return mPref.getString(HOST,"");
    }

    public void setUser(String type){

        SharedPreferences.Editor editor = mPref.edit();
        editor.putString(USER_TYPE,type);

        editor.apply();

    }
    public String getUser(){
        return mPref.getString(USER_TYPE,"");
    }
    public boolean isUserLoggedIn(){
        return mPref.contains(USER_TYPE);
    }

    public void setInventory(boolean set){
        SharedPreferences.Editor editor=mPref.edit();
        editor.putBoolean(INVENTORY,set);
        editor.apply();

    }
    public boolean isSetInventory(){
        return mPref.getBoolean(INVENTORY,false);

    }
    public void resetInventory(){
        SharedPreferences.Editor editor=mPref.edit();
        editor.remove(INVENTORY);
        editor.apply();

    }
    public void setGoodsReceive(boolean set){
        SharedPreferences.Editor editor=mPref.edit();
        editor.putBoolean(GOODS_RECEIVE,set);
        editor.apply();

    }
    public boolean isSetGoodsReceive() {
        return mPref.getBoolean(GOODS_RECEIVE, false);
    }
    public void resetGoodsReceive(){
        SharedPreferences.Editor editor=mPref.edit();
        editor.remove(GOODS_RECEIVE);
        editor.apply();

    }
    public void setSale(boolean set){
        SharedPreferences.Editor editor=mPref.edit();
        editor.putBoolean(SALE,set);
        editor.apply();

    }
    public boolean isSetSale() {
        return mPref.getBoolean(SALE, false);
    }
    public void resetSale(){
        SharedPreferences.Editor editor=mPref.edit();
        editor.remove(SALE);
        editor.apply();

    }



    public void setAppFirstTime(boolean first){
        SharedPreferences.Editor editor=mPref.edit();
        editor.putBoolean(APP_FIRST_TIME,first);
        editor.apply();
    }
    public boolean isAppFirstTime(){
        return mPref.getBoolean(APP_FIRST_TIME,false);

    }

    public void resetUser(){
        SharedPreferences.Editor editor=mPref.edit();
        editor.remove(USER_TYPE);
        editor.apply();
    }


    public void setPrinterName(String name){
        SharedPreferences.Editor editor = mPref.edit();
        editor.putString(PRINTER_NAME,name);
        editor.apply();
    }


    public String getPrinterName(){
        return mPref.getString(PRINTER_NAME,"");
    }
    public void removePrinter(){
        SharedPreferences.Editor editor = mPref.edit();
        editor.remove(PRINTER_NAME);
        editor.apply();
    }



}
