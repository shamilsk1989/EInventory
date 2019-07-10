package com.alhikmahpro.www.e_inventory.Data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.ContactsContract;
import android.util.Log;

public class dbHelper extends SQLiteOpenHelper {
    public static final String DATABASE_NAME = "m-inv";
    public static final int DATABASE_VERSION = 1;
    private static final String TAG = "dbHelper";

    private final String SQL_CREATE_SETTINGS_TABLE = "CREATE TABLE IF NOT EXISTS " + DataContract.Settings.TABLE_NAME + " (" +

            DataContract.Settings.COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
            DataContract.Settings.COL_COMPANY_CODE + " TEXT," +
            DataContract.Settings.COL_COMPANY_NAME + " TEXT," +
            DataContract.Settings.COL_BRANCH_CODE + " TEXT," +
            DataContract.Settings.COL_LOCATION_CODE + " TEXT," +
            DataContract.Settings.COL_PERIOD_CODE + " TEXT," +
            DataContract.Settings.COL_LOGO + " BLOB, " +
            DataContract.Settings.COL_IS_SALE + " INTEGER DEFAULT 0, " +
            DataContract.Settings.COL_IS_GDS + " INTEGER DEFAULT 0, " +
            DataContract.Settings.COL_IS_INV + " INTEGER DEFAULT 0, " +
            DataContract.Settings.COL_DEVICE_ID + " INTEGER DEFAULT 0 " + ");";

    private final String SQL_CREATE_LOGIN_TABLE = "CREATE TABLE IF NOT EXISTS " + DataContract.Login.TABLE_NAME + " (" +
            DataContract.Login.COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
            DataContract.Login.COL_PASSWORD + " TEXT " + ");";

    private final String SQL_CREATE_STOCK_TABLE = "CREATE TABLE IF NOT EXISTS " + DataContract.Stocks.TABLE_NAME + " (" +
            DataContract.Stocks.COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
            DataContract.Stocks.COL_DOCUMENT_NUMBER + " INTEGER ," +
            DataContract.Stocks.COL_TOTAL + " REAL ," +
            DataContract.Stocks.COL_STAFF_NAME + " TEXT ," +
            DataContract.Stocks.COL_DATE_TIME + " TEXT ," +
            DataContract.Stocks.COL_IS_SYNC + " INTEGER DEFAULT 0 " + ");";

    private final String SQL_CREATE_STOCK_DETAILS_TABLE = "CREATE TABLE IF NOT EXISTS " + DataContract.StocksDetails.TABLE_NAME + " (" +
            DataContract.StocksDetails.COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +

            DataContract.StocksDetails.COL_DOCUMENT_NUMBER + " INTEGER ," +
            DataContract.StocksDetails.COL_BAR_CODE + " TEXT ," +
            DataContract.StocksDetails.COL_PRODUCT_CODE + " TEXT ," +
            DataContract.StocksDetails.COL_PRODUCT_NAME + " TEXT ," +
            DataContract.StocksDetails.COL_UNIT + " TEXT ," +
            DataContract.StocksDetails.COL_QUANTITY + " REAL ," +
            DataContract.StocksDetails.COL_SALES_PRICE + " REAL ," +
            DataContract.StocksDetails.COL_COST_PRICE + " REAL ," +
            DataContract.StocksDetails.COL_IS_SYNC + " INTEGER DEFAULT 0 " + ");";


    private final String SQL_CREATE_GOODS_TABLE = "CREATE TABLE IF NOT EXISTS " + DataContract.GoodsReceive.TABLE_NAME + " (" +
            DataContract.GoodsReceive.COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
            DataContract.GoodsReceive.COL_DOCUMENT_NUMBER + " INTEGER ," +
            DataContract.GoodsReceive.COL_ORDER_NUMBER + " TEXT ," +
            DataContract.GoodsReceive.COL_SUPPLIER_CODE + " TEXT ," +
            DataContract.GoodsReceive.COL_SUPPLIER_NAME + " TEXT ," +
            DataContract.GoodsReceive.COL_INVOICE_NUMBER + " TEXT ," +
            DataContract.GoodsReceive.COL_INVOICE_DATE + " TEXT ," +
            DataContract.GoodsReceive.COL_STAFF_NAME + " TEXT ," +
            DataContract.GoodsReceive.COL_TOTAL + " REAL ," +
            DataContract.GoodsReceive.COL_DISCOUNT_AMOUNT + " REAL ," +
            DataContract.GoodsReceive.COL_NET_AMOUNT + " REAL ," +
            DataContract.GoodsReceive.COL_PAYMENT_TYPE + " TEXT ," +
            DataContract.GoodsReceive.COL_DATE_TIME + " TEXT ," +
            DataContract.GoodsReceive.COL_IS_SYNC + " INTEGER DEFAULT 0 " + ");";


    private final String SQL_CREATE_GOODS_DETAILS_TABLE = "CREATE TABLE IF NOT EXISTS " + DataContract.GoodsReceiveDetails.TABLE_NAME + " (" +
            DataContract.GoodsReceiveDetails.COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +

            DataContract.GoodsReceiveDetails.COL_DOCUMENT_NUMBER + " INTEGER ," +
            DataContract.GoodsReceiveDetails.COL_BAR_CODE + " TEXT ," +
            DataContract.GoodsReceiveDetails.COL_PRODUCT_CODE + " TEXT ," +
            DataContract.GoodsReceiveDetails.COL_PRODUCT_NAME + " TEXT ," +
            DataContract.GoodsReceiveDetails.COL_UNIT + " TEXT ," +
            DataContract.GoodsReceiveDetails.COL_FREE_UNIT + " TEXT ," +
            DataContract.GoodsReceiveDetails.COL_QUANTITY + " REAL ," +
            DataContract.GoodsReceiveDetails.COL_FREE_QUANTITY + " REAL ," +
            DataContract.GoodsReceiveDetails.COL_RATE + " REAL ," +
            DataContract.GoodsReceiveDetails.COL_DISCOUNT + " REAL ," +
            DataContract.GoodsReceiveDetails.COL_SALES_PRICE + " REAL ," +
            DataContract.GoodsReceiveDetails.COL_COST_PRICE + " REAL ," +
            DataContract.GoodsReceiveDetails.COL_STOCK + " TEXT ," +
            DataContract.GoodsReceiveDetails.COL_NET_VALUE + " REAL ," +
            DataContract.GoodsReceiveDetails.COL_UNIT1 + " TEXT ," +
            DataContract.GoodsReceiveDetails.COL_UNIT2 + " TEXT ," +
            DataContract.GoodsReceiveDetails.COL_UNIT3 + " TEXT ," +
            DataContract.GoodsReceiveDetails.COL_UN_QTY1 + " INTEGER ," +
            DataContract.GoodsReceiveDetails.COL_UN_QTY2 + " INTEGER ," +
            DataContract.GoodsReceiveDetails.COL_UN_QTY3 + " INTEGER ," +
            DataContract.GoodsReceiveDetails.COL_IS_SYNC + " INTEGER DEFAULT 0 " + ");";


    private final String SQL_CREATE_INVOICE_TABLE = "CREATE TABLE IF NOT EXISTS " + DataContract.Invoice.TABLE_NAME + " (" +
            DataContract.Invoice.COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
            DataContract.Invoice.COL_INVOICE_NUMBER + " TEXT ," +
            DataContract.Invoice.COL_INVOICE_DATE + " TEXT ," +
            DataContract.Invoice.COL_SALESMAN_ID + " TEXT ," +
            DataContract.Invoice.COL_CUSTOMER_CODE + " TEXT ," +
            DataContract.Invoice.COL_CUSTOMER_NAME + " TEXT ," +
            DataContract.Invoice.COL_TOTAL_AMOUNT + " REAL ," +
            DataContract.Invoice.COL_DISCOUNT_AMOUNT + " REAL ," +
            DataContract.Invoice.COL_PAYMENT_TYPE + " TEXT ," +
            DataContract.Invoice.COL_NET_AMOUNT + " REAL ," +
            DataContract.Invoice.COL_DATE_TIME + " TEXT ," +
            DataContract.Invoice.COL_IS_SYNC + " INTEGER DEFAULT 0 " + ");";


    private final String SQL_CREATE_INVOICE_DETAILS_TABLE = "CREATE TABLE IF NOT EXISTS " + DataContract.InvoiceDetails.TABLE_NAME + " (" +
            DataContract.InvoiceDetails.COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +

            DataContract.InvoiceDetails.COL_INVOICE_NUMBER + " TEXT ," +
            DataContract.InvoiceDetails.COL_BAR_CODE + " TEXT ," +
            DataContract.InvoiceDetails.COL_PRODUCT_CODE + " TEXT ," +
            DataContract.InvoiceDetails.COL_PRODUCT_NAME + " TEXT ," +
            DataContract.InvoiceDetails.COL_UNIT + " TEXT ," +
            DataContract.InvoiceDetails.COL_QUANTITY + " REAL ," +
            DataContract.InvoiceDetails.COL_UNIT1 + " TEXT ," +
            DataContract.InvoiceDetails.COL_UNIT2 + " TEXT ," +
            DataContract.InvoiceDetails.COL_UNIT3 + " TEXT ," +
            DataContract.InvoiceDetails.COL_UN_QTY1 + " INTEGER ," +
            DataContract.InvoiceDetails.COL_UN_QTY2 + " INTEGER ," +
            DataContract.InvoiceDetails.COL_UN_QTY3 + " INTEGER ," +
            DataContract.InvoiceDetails.COL_RATE + " REAL ," +
            DataContract.InvoiceDetails.COL_DISCOUNT + " REAL ," +
            DataContract.InvoiceDetails.COL_NET_AMOUNT + " REAL ," +
            DataContract.InvoiceDetails.COL_SALE_TYPE + " TEXT ," +
            DataContract.InvoiceDetails.COL_IS_SYNC + " INTEGER DEFAULT 0 " + ");";

    private final String SQL_CREATE_PAPER_SETTINGS_TABLE = "CREATE TABLE IF NOT EXISTS " + DataContract.PaperSettings.TABLE_NAME + " (" +

            DataContract.PaperSettings.COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
            DataContract.PaperSettings.COL_COMPANY_NAME + " TEXT," +
            DataContract.PaperSettings.COL_COMPANY_ADDRESS + " TEXT," +
            DataContract.PaperSettings.COL_COMPANY_PHONE + " TEXT," +
            DataContract.PaperSettings.COL_FOOTER + " TEXT," +
            DataContract.PaperSettings.COL_PAPER_SIZE + " TEXT " + ");";


    public dbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        Log.d(TAG, "Database created......: ");
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_SETTINGS_TABLE);
        db.execSQL(SQL_CREATE_STOCK_TABLE);
        db.execSQL(SQL_CREATE_STOCK_DETAILS_TABLE);
        db.execSQL(SQL_CREATE_LOGIN_TABLE);
        db.execSQL(SQL_CREATE_GOODS_TABLE);
        db.execSQL(SQL_CREATE_GOODS_DETAILS_TABLE);
        db.execSQL(SQL_CREATE_INVOICE_TABLE);
        db.execSQL(SQL_CREATE_INVOICE_DETAILS_TABLE);
        db.execSQL(SQL_CREATE_PAPER_SETTINGS_TABLE);
        Log.d(TAG, "Table created......: ");

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(" DROP TABLE IF EXISTS " + DataContract.Settings.TABLE_NAME);
        db.execSQL(" DROP TABLE IF EXISTS " + DataContract.Stocks.TABLE_NAME);
        db.execSQL(" DROP TABLE IF EXISTS " + DataContract.Login.TABLE_NAME);
        db.execSQL(" DROP TABLE IF EXISTS " + DataContract.StocksDetails.TABLE_NAME);
        db.execSQL(" DROP TABLE IF EXISTS " + DataContract.GoodsReceive.TABLE_NAME);
        db.execSQL(" DROP TABLE IF EXISTS " + DataContract.GoodsReceiveDetails.TABLE_NAME);
        db.execSQL(" DROP TABLE IF EXISTS " + DataContract.Invoice.TABLE_NAME);
        db.execSQL(" DROP TABLE IF EXISTS " + DataContract.InvoiceDetails.TABLE_NAME);
        db.execSQL(" DROP TABLE IF EXISTS " + DataContract.PaperSettings.TABLE_NAME);
        onCreate(db);

    }

    //*****************************settings table********************
    public boolean saveSettings(String CCode, String CCName, String BCode, String LCode, String PCode, String deviceId,
                                byte[] logo, int saleStat, int invStat, int gdsStat) {

        SQLiteDatabase database = getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        try {

            contentValues.put(DataContract.Settings.COL_COMPANY_CODE, CCode);
            contentValues.put(DataContract.Settings.COL_COMPANY_NAME, CCName);
            contentValues.put(DataContract.Settings.COL_BRANCH_CODE, BCode);
            contentValues.put(DataContract.Settings.COL_LOCATION_CODE, LCode);
            contentValues.put(DataContract.Settings.COL_PERIOD_CODE, PCode);
            contentValues.put(DataContract.Settings.COL_DEVICE_ID, deviceId);
            contentValues.put(DataContract.Settings.COL_LOGO, logo);
            contentValues.put(DataContract.Settings.COL_IS_SALE, saleStat);
            contentValues.put(DataContract.Settings.COL_IS_INV, invStat);
            contentValues.put(DataContract.Settings.COL_IS_GDS, gdsStat);
            database.insert(DataContract.Settings.TABLE_NAME, null, contentValues);
            database.close();
            Log.d(TAG, "one row inserted in Settings table ......... ");
            return true;
        } catch (Exception e) {
            return false;
        }


    }

    public boolean updateSettings(int _id, String CCode, String CCName, String BCode, String LCode, String PCode, String deviceId,
                                  byte[] logo, int saleStat, int invStat, int gdsStat) {

        SQLiteDatabase database = getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        String selection = DataContract.Settings.COL_ID + " = " + _id;
        try {

            contentValues.put(DataContract.Settings.COL_COMPANY_CODE, CCode);
            contentValues.put(DataContract.Settings.COL_COMPANY_NAME, CCName);
            contentValues.put(DataContract.Settings.COL_BRANCH_CODE, BCode);
            contentValues.put(DataContract.Settings.COL_LOCATION_CODE, LCode);
            contentValues.put(DataContract.Settings.COL_PERIOD_CODE, PCode);
            contentValues.put(DataContract.Settings.COL_DEVICE_ID, deviceId);
            contentValues.put(DataContract.Settings.COL_LOGO, logo);
            contentValues.put(DataContract.Settings.COL_IS_SALE, saleStat);
            contentValues.put(DataContract.Settings.COL_IS_INV, invStat);
            contentValues.put(DataContract.Settings.COL_IS_GDS, gdsStat);
            database.update(DataContract.Settings.TABLE_NAME, contentValues, selection, null);
            database.close();
            Log.d(TAG, " updated Settings table ......... ");
            return true;
        } catch (Exception e) {
            return false;
        }
    }
    public Cursor getSettings(SQLiteDatabase sqLiteDatabase) {
        String[] projections = {
                DataContract.Settings.COL_ID, DataContract.Settings.COL_ID,
                DataContract.Settings.COL_COMPANY_CODE, DataContract.Settings.COL_COMPANY_NAME,
                DataContract.Settings.COL_BRANCH_CODE, DataContract.Settings.COL_LOCATION_CODE,
                DataContract.Settings.COL_PERIOD_CODE, DataContract.Settings.COL_DEVICE_ID,
                DataContract.Settings.COL_PERIOD_CODE, DataContract.Settings.COL_LOGO,
                DataContract.Settings.COL_PERIOD_CODE, DataContract.Settings.COL_IS_SALE,
                DataContract.Settings.COL_PERIOD_CODE, DataContract.Settings.COL_IS_GDS,
                DataContract.Settings.COL_PERIOD_CODE, DataContract.Settings.COL_IS_INV,
        };
        Cursor cursor = sqLiteDatabase.query(DataContract.Settings.TABLE_NAME, projections, null,
                null, null, null, DataContract.Settings.COL_ID + " DESC ", "1 ");

        // database.close();DataContract.BillTitles.COL_TITLE_ID +" DESC ","1"
        return cursor;

    }
    /*******************************end settings table***********************************/

    /******************************* login table***********************************/

    public boolean saveLogin(String password) {
        SQLiteDatabase database = getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(DataContract.Login.COL_PASSWORD, password);
        database.insert(DataContract.Login.TABLE_NAME, null, contentValues);
        database.close();
        Log.d(TAG, "one row inserted in login table ......... ");
        return true;
    }
    /******************************* end login table***********************************/


    /******************************* stock table***********************************/
    public boolean saveStocks(int docNo, double total, String staffName, String date) {

        SQLiteDatabase database = getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        try {
            contentValues.put(DataContract.Stocks.COL_DOCUMENT_NUMBER, docNo);
            contentValues.put(DataContract.Stocks.COL_TOTAL, total);
            contentValues.put(DataContract.Stocks.COL_STAFF_NAME, staffName);
            contentValues.put(DataContract.Stocks.COL_DATE_TIME, date);
            database.insert(DataContract.Stocks.TABLE_NAME, null, contentValues);
            database.close();
            Log.d(TAG, "one row inserted in Stocks table ......... ");
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public boolean saveStocksDetails(int docNo, String barCode, String proCode, String proName, String unit,
                                     double qty, double price, double cost) {
        //String barCode, String pCode, String pName, String unit,double qty, double sPrice, double cPrice) {

        boolean res = false;
        SQLiteDatabase database = getWritableDatabase();

        try {
            ContentValues contentValues = new ContentValues();


            contentValues.put(DataContract.StocksDetails.COL_DOCUMENT_NUMBER, docNo);
            contentValues.put(DataContract.StocksDetails.COL_BAR_CODE, barCode);
            contentValues.put(DataContract.StocksDetails.COL_PRODUCT_CODE, proCode);
            contentValues.put(DataContract.StocksDetails.COL_PRODUCT_NAME, proName);
            contentValues.put(DataContract.StocksDetails.COL_UNIT, unit);
            contentValues.put(DataContract.StocksDetails.COL_QUANTITY, qty);
            contentValues.put(DataContract.StocksDetails.COL_SALES_PRICE, price);
            contentValues.put(DataContract.StocksDetails.COL_COST_PRICE, cost);

            database.insert(DataContract.StocksDetails.TABLE_NAME, null, contentValues);

            Log.d(TAG, "one row inserted in StocksDetails table ......... ");

            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public void updateStocks(int doc_no, double amount) {

        SQLiteDatabase database = getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(DataContract.Stocks.COL_TOTAL, amount);
        String selection = DataContract.Stocks.COL_DOCUMENT_NUMBER + " = " + doc_no;
        database.update(DataContract.Stocks.TABLE_NAME, contentValues, selection, null);
        database.close();

    }
    public Cursor getStocks(SQLiteDatabase database) {
        Log.d(TAG, "getStocks: ");
        String[] projection = {DataContract.Stocks.COL_STAFF_NAME, DataContract.Stocks.COL_TOTAL, DataContract.Stocks.COL_DOCUMENT_NUMBER
                , DataContract.Stocks.COL_DATE_TIME, DataContract.Stocks.COL_IS_SYNC};
        String orderBy = DataContract.Stocks.COL_ID + " DESC ";
        Cursor cursor = database.query(DataContract.Stocks.TABLE_NAME, projection, null, null, null, null, orderBy);
        return cursor;
    }


    public Cursor getStockDetailsByDoc(SQLiteDatabase database, int doc_no) {

        String[] projections = {DataContract.StocksDetails.COL_ID, DataContract.StocksDetails.COL_DOCUMENT_NUMBER, DataContract.StocksDetails.COL_BAR_CODE, DataContract.StocksDetails.COL_PRODUCT_CODE,
                DataContract.StocksDetails.COL_PRODUCT_NAME, DataContract.StocksDetails.COL_UNIT, DataContract.StocksDetails.COL_QUANTITY,
                DataContract.StocksDetails.COL_SALES_PRICE, DataContract.StocksDetails.COL_COST_PRICE};
        String selection = DataContract.StocksDetails.COL_DOCUMENT_NUMBER + " = " + doc_no;
        Cursor cursor = database.query(DataContract.StocksDetails.TABLE_NAME, projections, selection, null, null, null, null);
        return cursor;
    }

    public boolean deleteStockDetailsById(int _id) {
        SQLiteDatabase database = getReadableDatabase();
        return database.delete(DataContract.StocksDetails.TABLE_NAME, DataContract.StocksDetails.COL_ID + "= ?", new String[]{String.valueOf(_id)}) > 0;
    }

    public int getLastDocNo() {
        SQLiteDatabase database = getReadableDatabase();
        String[] projection = {DataContract.Stocks.COL_DOCUMENT_NUMBER};
        Cursor cursor = database.query(DataContract.Stocks.TABLE_NAME, projection, null, null, null, null, DataContract.Stocks.COL_DOCUMENT_NUMBER + " DESC ", "1");


        //Cursor cursor=database.query(DataContract.Invoice.TABLE_NAME,projections,null,null,null,null,DataContract.Invoice.COL_INVOICE_ID +" DESC ","1");
        int id = 0;
        if (cursor.moveToFirst()) {
            id = cursor.getInt(cursor.getColumnIndex(DataContract.Stocks.COL_DOCUMENT_NUMBER));

        }
        cursor.close();
        database.close();
        return id;
    }

    /******************************* end stock table***********************************/


    /******************************* Goods table***********************************/

    public boolean saveGoods(int docNo, String orderNo, String supplierCode, String supplierName, String invoiceNo, String invoiceDate,
                             String staffName, double total, double disc, double net, String payment, String date, int status) {

        SQLiteDatabase database = getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        try {
            contentValues.put(DataContract.GoodsReceive.COL_DOCUMENT_NUMBER, docNo);
            contentValues.put(DataContract.GoodsReceive.COL_ORDER_NUMBER, orderNo);
            contentValues.put(DataContract.GoodsReceive.COL_SUPPLIER_CODE, supplierCode);
            contentValues.put(DataContract.GoodsReceive.COL_SUPPLIER_CODE, supplierName);
            contentValues.put(DataContract.GoodsReceive.COL_INVOICE_NUMBER, invoiceNo);
            contentValues.put(DataContract.GoodsReceive.COL_INVOICE_DATE, invoiceDate);
            contentValues.put(DataContract.GoodsReceive.COL_STAFF_NAME, staffName);
            contentValues.put(DataContract.GoodsReceive.COL_TOTAL, total);
            contentValues.put(DataContract.GoodsReceive.COL_DISCOUNT_AMOUNT, disc);
            contentValues.put(DataContract.GoodsReceive.COL_NET_AMOUNT, net);
            contentValues.put(DataContract.GoodsReceive.COL_TOTAL, net);
            contentValues.put(DataContract.GoodsReceive.COL_PAYMENT_TYPE, payment);
            contentValues.put(DataContract.GoodsReceive.COL_DATE_TIME, date);
            contentValues.put(DataContract.GoodsReceive.COL_IS_SYNC, status);
            database.insert(DataContract.GoodsReceive.TABLE_NAME, null, contentValues);
            database.close();
            Log.d(TAG, "one row inserted in goods table ......... ");
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public boolean saveGoodsDetails1(int docNo, int status) {

        boolean result;
        SQLiteDatabase database = getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        try {
            for (ItemModel model : Cart.gCart) {
                contentValues.put(DataContract.GoodsReceiveDetails.COL_DOCUMENT_NUMBER, docNo);
                contentValues.put(DataContract.GoodsReceiveDetails.COL_BAR_CODE, model.getBarCode());
                contentValues.put(DataContract.GoodsReceiveDetails.COL_PRODUCT_CODE, model.getProductCode());
                contentValues.put(DataContract.GoodsReceiveDetails.COL_PRODUCT_NAME, model.getProductName());
                contentValues.put(DataContract.GoodsReceiveDetails.COL_UNIT, model.getSelectedPackage());
                contentValues.put(DataContract.GoodsReceiveDetails.COL_FREE_UNIT, model.getSelectedFreePackage());
                contentValues.put(DataContract.GoodsReceiveDetails.COL_QUANTITY, model.getQty());
                contentValues.put(DataContract.GoodsReceiveDetails.COL_FREE_QUANTITY, model.getFreeQty());
                contentValues.put(DataContract.GoodsReceiveDetails.COL_RATE, model.getRate());
                contentValues.put(DataContract.GoodsReceiveDetails.COL_DISCOUNT, model.getDiscount());
                contentValues.put(DataContract.GoodsReceiveDetails.COL_SALES_PRICE, model.getSalePrice());
                contentValues.put(DataContract.GoodsReceiveDetails.COL_COST_PRICE, model.getCostPrice());
                contentValues.put(DataContract.GoodsReceiveDetails.COL_STOCK, model.getStock());
                contentValues.put(DataContract.GoodsReceiveDetails.COL_NET_VALUE, model.getNet());
                contentValues.put(DataContract.GoodsReceiveDetails.COL_UNIT1, model.getUnit1());
                contentValues.put(DataContract.GoodsReceiveDetails.COL_UNIT2, model.getUnit2());
                contentValues.put(DataContract.GoodsReceiveDetails.COL_UNIT3, model.getUnit3());
                contentValues.put(DataContract.GoodsReceiveDetails.COL_UN_QTY1, model.getUnit1Qty());
                contentValues.put(DataContract.GoodsReceiveDetails.COL_UN_QTY2, model.getUnit2Qty());
                contentValues.put(DataContract.GoodsReceiveDetails.COL_UN_QTY3, model.getUnit3Qty());
                contentValues.put(DataContract.GoodsReceiveDetails.COL_IS_SYNC, status);
                database.insert(DataContract.GoodsReceiveDetails.TABLE_NAME, null, contentValues);
                Log.d(TAG, "one row inserted in GoodsReceiveDetails table ......... ");

            }
            result = true;

        } catch (SQLException e) {
            result = false;
        }

        return result;
    }


    public void updateGoods(int docNo, String orderNo, String supplierCode, String supplierName, String invoiceNo, String invoiceDate,
                            String staffName, double total, double disc, double net, String payment, String date, int status) {
        SQLiteDatabase database = getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(DataContract.GoodsReceive.COL_DOCUMENT_NUMBER, docNo);
        contentValues.put(DataContract.GoodsReceive.COL_ORDER_NUMBER, orderNo);
        contentValues.put(DataContract.GoodsReceive.COL_SUPPLIER_CODE, supplierCode);
        contentValues.put(DataContract.GoodsReceive.COL_SUPPLIER_CODE, supplierName);
        contentValues.put(DataContract.GoodsReceive.COL_INVOICE_NUMBER, invoiceNo);
        contentValues.put(DataContract.GoodsReceive.COL_INVOICE_DATE, invoiceDate);
        contentValues.put(DataContract.GoodsReceive.COL_STAFF_NAME, staffName);
        contentValues.put(DataContract.GoodsReceive.COL_TOTAL, total);
        contentValues.put(DataContract.GoodsReceive.COL_DISCOUNT_AMOUNT, disc);
        contentValues.put(DataContract.GoodsReceive.COL_NET_AMOUNT, net);
        contentValues.put(DataContract.GoodsReceive.COL_TOTAL, net);
        contentValues.put(DataContract.GoodsReceive.COL_PAYMENT_TYPE, payment);
        contentValues.put(DataContract.GoodsReceive.COL_DATE_TIME, date);
        contentValues.put(DataContract.GoodsReceive.COL_IS_SYNC, status);
        database.update(DataContract.GoodsReceive.TABLE_NAME, contentValues, DataContract.GoodsReceive.COL_DOCUMENT_NUMBER + "=?", new String[]{String.valueOf(docNo)});
        Log.d(TAG, "one row update in GoodsReceiveDetails table ......... ");
        database.close();
    }

    public boolean updateGoodsDetails1(int docNo, int status) {

        boolean result;
        SQLiteDatabase database = getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        try {
            for (ItemModel model : Cart.gCart) {
                contentValues.put(DataContract.GoodsReceiveDetails.COL_DOCUMENT_NUMBER, docNo);
                contentValues.put(DataContract.GoodsReceiveDetails.COL_BAR_CODE, model.getBarCode());
                contentValues.put(DataContract.GoodsReceiveDetails.COL_PRODUCT_CODE, model.getProductCode());
                contentValues.put(DataContract.GoodsReceiveDetails.COL_PRODUCT_NAME, model.getProductName());
                contentValues.put(DataContract.GoodsReceiveDetails.COL_UNIT, model.getSelectedPackage());
                contentValues.put(DataContract.GoodsReceiveDetails.COL_FREE_UNIT, model.getSelectedFreePackage());
                contentValues.put(DataContract.GoodsReceiveDetails.COL_QUANTITY, model.getQty());
                contentValues.put(DataContract.GoodsReceiveDetails.COL_FREE_QUANTITY, model.getFreeQty());
                contentValues.put(DataContract.GoodsReceiveDetails.COL_RATE, model.getRate());
                contentValues.put(DataContract.GoodsReceiveDetails.COL_DISCOUNT, model.getDiscount());
                contentValues.put(DataContract.GoodsReceiveDetails.COL_SALES_PRICE, model.getSalePrice());
                contentValues.put(DataContract.GoodsReceiveDetails.COL_COST_PRICE, model.getCostPrice());
                contentValues.put(DataContract.GoodsReceiveDetails.COL_STOCK, model.getStock());
                contentValues.put(DataContract.GoodsReceiveDetails.COL_NET_VALUE, model.getNet());
                contentValues.put(DataContract.GoodsReceiveDetails.COL_UNIT1, model.getUnit1());
                contentValues.put(DataContract.GoodsReceiveDetails.COL_UNIT2, model.getUnit2());
                contentValues.put(DataContract.GoodsReceiveDetails.COL_UNIT3, model.getUnit3());
                contentValues.put(DataContract.GoodsReceiveDetails.COL_UN_QTY1, model.getUnit1Qty());
                contentValues.put(DataContract.GoodsReceiveDetails.COL_UN_QTY2, model.getUnit2Qty());
                contentValues.put(DataContract.GoodsReceiveDetails.COL_UN_QTY3, model.getUnit3Qty());
                contentValues.put(DataContract.GoodsReceiveDetails.COL_IS_SYNC, status);
                database.update(DataContract.GoodsReceive.TABLE_NAME, contentValues, DataContract.GoodsReceive.COL_DOCUMENT_NUMBER + "=?", new String[]{String.valueOf(docNo)});
                Log.d(TAG, "one row update in GoodsReceiveDetails table ......... ");

            }
            result = true;

        } catch (SQLException e) {
            result = false;
        }

        return result;
    }



    public int getGoodsLastDocNo() {
        SQLiteDatabase database = getReadableDatabase();
        String[] projection = {DataContract.GoodsReceive.COL_DOCUMENT_NUMBER};
        Cursor cursor = database.query(DataContract.GoodsReceive.TABLE_NAME, projection, null, null, null, null, DataContract.Stocks.COL_DOCUMENT_NUMBER + " DESC ", "1");


        //Cursor cursor=database.query(DataContract.Invoice.TABLE_NAME,projections,null,null,null,null,DataContract.Invoice.COL_INVOICE_ID +" DESC ","1");
        int id = 0;
        if (cursor.moveToFirst()) {
            id = cursor.getInt(cursor.getColumnIndex(DataContract.GoodsReceive.COL_DOCUMENT_NUMBER));

        }
        cursor.close();
        database.close();
        return id;

    }

    public Cursor getGoodsDetailsByDoc(SQLiteDatabase database, int doc_no) {

        Cursor cursor = database.rawQuery("SELECT * FROM " + DataContract.GoodsReceiveDetails.TABLE_NAME + " WHERE " +
                        DataContract.GoodsReceiveDetails.COL_DOCUMENT_NUMBER + "= ? AND " + DataContract.GoodsReceiveDetails.COL_IS_SYNC + " = ? ",
                new String[]{String.valueOf(doc_no), String.valueOf(DataContract.SYNC_STATUS_FAILED)});

        return cursor;


    }

    public boolean checkGoods(SQLiteDatabase database, int doc_no) {

        Cursor cursor = database.rawQuery("SELECT * FROM " + DataContract.GoodsReceive.TABLE_NAME + " WHERE " +
                        DataContract.GoodsReceive.COL_DOCUMENT_NUMBER + "= ? ",
                new String[]{String.valueOf(doc_no)});

        if (cursor.getCount() > 0)
            return true;
        else
            return false;
    }



    public Cursor getGoods(SQLiteDatabase database) {
        Log.d(TAG, "getGoods: ");
        String[] projection = {DataContract.GoodsReceive.COL_DOCUMENT_NUMBER,
                DataContract.GoodsReceive.COL_ORDER_NUMBER,
                DataContract.GoodsReceive.COL_SUPPLIER_CODE,
                DataContract.GoodsReceive.COL_SUPPLIER_NAME,
                DataContract.GoodsReceive.COL_INVOICE_NUMBER,
                DataContract.GoodsReceive.COL_INVOICE_DATE,
                DataContract.GoodsReceive.COL_STAFF_NAME,
                DataContract.GoodsReceive.COL_TOTAL,
                DataContract.GoodsReceive.COL_DISCOUNT_AMOUNT,
                DataContract.GoodsReceive.COL_NET_AMOUNT,
                DataContract.GoodsReceive.COL_PAYMENT_TYPE,
                DataContract.GoodsReceive.COL_DATE_TIME,
                DataContract.GoodsReceive.COL_IS_SYNC



        };
        String orderBy = DataContract.GoodsReceive.COL_ID + " DESC ";
        Cursor cursor = database.query(DataContract.GoodsReceive.TABLE_NAME, projection, null, null, null, null, orderBy);
        return cursor;
    }


    public boolean deleteGoodsById(int _id) {
        SQLiteDatabase database = getReadableDatabase();
        return database.delete(DataContract.GoodsReceive.TABLE_NAME, DataContract.GoodsReceive.COL_ID + "= ?", new String[]{String.valueOf(_id)}) > 0;
    }

    public void updateGoodsSync(int doc) {
        try {
            SQLiteDatabase database = getWritableDatabase();
            ContentValues values = new ContentValues();
            values.put(DataContract.GoodsReceive.COL_IS_SYNC, DataContract.SYNC_STATUS_OK);
            database.update(DataContract.GoodsReceive.TABLE_NAME, values, DataContract.GoodsReceive.COL_DOCUMENT_NUMBER + "=?", new String[]{String.valueOf(doc)});

            Log.v(TAG, "invoice sync updated");
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }


    /******************************* end Goods table***********************************/

    /******************************* invoice table***********************************/

    public boolean saveInvoice(String invoiceNo, String invoiceDate, String salesmanId, String customerCode, String customerName,
                               double total, double discount, double net, String paymentMode, String date, int status) {

        SQLiteDatabase database = getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        try {
            contentValues.put(DataContract.Invoice.COL_INVOICE_NUMBER, invoiceNo);
            contentValues.put(DataContract.Invoice.COL_INVOICE_DATE, invoiceDate);
            contentValues.put(DataContract.Invoice.COL_SALESMAN_ID, salesmanId);
            contentValues.put(DataContract.Invoice.COL_CUSTOMER_CODE, customerCode);
            contentValues.put(DataContract.Invoice.COL_CUSTOMER_NAME, customerName);
            contentValues.put(DataContract.Invoice.COL_TOTAL_AMOUNT, total);
            contentValues.put(DataContract.Invoice.COL_DISCOUNT_AMOUNT, discount);
            contentValues.put(DataContract.Invoice.COL_NET_AMOUNT, net);
            contentValues.put(DataContract.Invoice.COL_PAYMENT_TYPE, paymentMode);
            contentValues.put(DataContract.Invoice.COL_DATE_TIME, date);
            contentValues.put(DataContract.Invoice.COL_IS_SYNC, status);
            database.insert(DataContract.Invoice.TABLE_NAME, null, contentValues);
            database.close();
            Log.d(TAG, "one row inserted in sales table ......... ");
            return true;
        } catch (Exception e) {
            return false;
        }


    }

    public boolean saveInvoiceDetails(String invoiceNo, int status) {

        boolean result = false;
        SQLiteDatabase database = getWritableDatabase();
        ContentValues contentValues = new ContentValues();


        try {
            for (CartModel model : Cart.mCart) {
                contentValues.put(DataContract.InvoiceDetails.COL_INVOICE_NUMBER, invoiceNo);
                contentValues.put(DataContract.InvoiceDetails.COL_BAR_CODE, model.getBarcode());
                contentValues.put(DataContract.InvoiceDetails.COL_PRODUCT_CODE, model.getProductCode());
                contentValues.put(DataContract.InvoiceDetails.COL_PRODUCT_NAME, model.getProductName());
                contentValues.put(DataContract.InvoiceDetails.COL_QUANTITY, model.getQty());
                contentValues.put(DataContract.InvoiceDetails.COL_UNIT, model.getSelectedUnit());
                contentValues.put(DataContract.InvoiceDetails.COL_UNIT1, model.getUnit1());
                contentValues.put(DataContract.InvoiceDetails.COL_UNIT2, model.getUnit2());
                contentValues.put(DataContract.InvoiceDetails.COL_UNIT3, model.getUnit3());
                contentValues.put(DataContract.InvoiceDetails.COL_UN_QTY1, model.getUnit1Qty());
                contentValues.put(DataContract.InvoiceDetails.COL_UN_QTY2, model.getUnit2Qty());
                contentValues.put(DataContract.InvoiceDetails.COL_UN_QTY3, model.getUnit3Qty());
                contentValues.put(DataContract.InvoiceDetails.COL_RATE, model.getRate());
                contentValues.put(DataContract.InvoiceDetails.COL_DISCOUNT, model.getDiscount());
                contentValues.put(DataContract.InvoiceDetails.COL_NET_AMOUNT, model.getNet());
                contentValues.put(DataContract.InvoiceDetails.COL_NET_AMOUNT, model.getNet());
                contentValues.put(DataContract.InvoiceDetails.COL_SALE_TYPE, model.getSaleType());
                contentValues.put(DataContract.InvoiceDetails.COL_IS_SYNC, status);
                database.insertOrThrow(DataContract.InvoiceDetails.TABLE_NAME, null, contentValues);
                Log.d(TAG, "one row inserted in invoiceDetails table ......... ");
                result = true;
            }


        } catch (SQLException e) {
            result = false;
        }

        return result;
    }

    public boolean checkInvoice(SQLiteDatabase database, String invoice_no) {

        Cursor cursor = database.rawQuery("SELECT * FROM " + DataContract.Invoice.TABLE_NAME + " WHERE " +
                        DataContract.Invoice.COL_INVOICE_NUMBER + "= ? ",
                new String[]{String.valueOf(invoice_no)});

        if (cursor.getCount() > 0)
            return true;
        else
            return false;
    }

    public boolean updateInvoice(String invoiceNo, String invoiceDate, String salesmanId, String customerCode, String customerName,
                               double total, double discount, double net, String paymentMode, String date, int status) {

        SQLiteDatabase database = getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        try {
            contentValues.put(DataContract.Invoice.COL_INVOICE_NUMBER, invoiceNo);
            contentValues.put(DataContract.Invoice.COL_INVOICE_DATE, invoiceDate);
            contentValues.put(DataContract.Invoice.COL_SALESMAN_ID, salesmanId);
            contentValues.put(DataContract.Invoice.COL_CUSTOMER_CODE, customerCode);
            contentValues.put(DataContract.Invoice.COL_CUSTOMER_NAME, customerName);
            contentValues.put(DataContract.Invoice.COL_TOTAL_AMOUNT, total);
            contentValues.put(DataContract.Invoice.COL_DISCOUNT_AMOUNT, discount);
            contentValues.put(DataContract.Invoice.COL_NET_AMOUNT, net);
            contentValues.put(DataContract.Invoice.COL_PAYMENT_TYPE, paymentMode);
            contentValues.put(DataContract.Invoice.COL_DATE_TIME, date);
            contentValues.put(DataContract.Invoice.COL_IS_SYNC, status);
            database.update(DataContract.Invoice.TABLE_NAME, contentValues, DataContract.Invoice.COL_INVOICE_NUMBER + "=?", new String[]{invoiceNo});
            database.close();
            Log.d(TAG, "one row updated in invoice table ......... ");
            return true;
        } catch (Exception e) {
            return false;
        }


    }

    public boolean updateInvoiceDetails(String invoiceNo, int status) {

        boolean result = false;
        SQLiteDatabase database = getWritableDatabase();
        ContentValues contentValues = new ContentValues();


        try {
            for (CartModel model : Cart.mCart) {
                contentValues.put(DataContract.InvoiceDetails.COL_INVOICE_NUMBER, invoiceNo);
                contentValues.put(DataContract.InvoiceDetails.COL_BAR_CODE, model.getBarcode());
                contentValues.put(DataContract.InvoiceDetails.COL_PRODUCT_CODE, model.getProductCode());
                contentValues.put(DataContract.InvoiceDetails.COL_PRODUCT_NAME, model.getProductName());
                contentValues.put(DataContract.InvoiceDetails.COL_QUANTITY, model.getQty());
                contentValues.put(DataContract.InvoiceDetails.COL_UNIT, model.getSelectedUnit());
                contentValues.put(DataContract.InvoiceDetails.COL_UNIT1, model.getUnit1());
                contentValues.put(DataContract.InvoiceDetails.COL_UNIT2, model.getUnit2());
                contentValues.put(DataContract.InvoiceDetails.COL_UNIT3, model.getUnit3());
                contentValues.put(DataContract.InvoiceDetails.COL_UN_QTY1, model.getUnit1Qty());
                contentValues.put(DataContract.InvoiceDetails.COL_UN_QTY2, model.getUnit2Qty());
                contentValues.put(DataContract.InvoiceDetails.COL_UN_QTY3, model.getUnit3Qty());
                contentValues.put(DataContract.InvoiceDetails.COL_RATE, model.getRate());
                contentValues.put(DataContract.InvoiceDetails.COL_DISCOUNT, model.getDiscount());
                contentValues.put(DataContract.InvoiceDetails.COL_NET_AMOUNT, model.getNet());
                contentValues.put(DataContract.InvoiceDetails.COL_NET_AMOUNT, model.getNet());
                contentValues.put(DataContract.InvoiceDetails.COL_SALE_TYPE, model.getSaleType());
                contentValues.put(DataContract.InvoiceDetails.COL_IS_SYNC, status);
                database.update(DataContract.InvoiceDetails.TABLE_NAME, contentValues, DataContract.InvoiceDetails.COL_INVOICE_NUMBER + "=?", new String[]{invoiceNo});
                Log.d(TAG, "one row updated in invoiceDetails table ......... ");
                result = true;
            }


        } catch (SQLException e) {
            result = false;
        }

        return result;
    }

    public Cursor getInvoice(SQLiteDatabase database) {
        Log.d(TAG, "getInvoice: ");
        String[] projection = {
                DataContract.Invoice.COL_INVOICE_NUMBER,
                DataContract.Invoice.COL_INVOICE_DATE,
                DataContract.Invoice.COL_SALESMAN_ID,
                DataContract.Invoice.COL_CUSTOMER_CODE,
                DataContract.Invoice.COL_CUSTOMER_NAME,
                DataContract.Invoice.COL_TOTAL_AMOUNT,
                DataContract.Invoice.COL_DISCOUNT_AMOUNT,
                DataContract.Invoice.COL_NET_AMOUNT,
                DataContract.Invoice.COL_PAYMENT_TYPE,
                DataContract.Invoice.COL_IS_SYNC,
        };
        String orderBy = DataContract.Invoice.COL_ID + " DESC ";
        Cursor cursor = database.query(DataContract.Invoice.TABLE_NAME, projection, null, null, null, null, orderBy);
        return cursor;
    }

    public void getInvoiceDetailsById(String invoiceId) {
        Log.d(TAG, "getInvoice: ");
        SQLiteDatabase database = getReadableDatabase();
        String[] projection = {
                DataContract.InvoiceDetails.COL_INVOICE_NUMBER,
                DataContract.InvoiceDetails.COL_BAR_CODE,
                DataContract.InvoiceDetails.COL_PRODUCT_CODE,
                DataContract.InvoiceDetails.COL_PRODUCT_NAME,
                DataContract.InvoiceDetails.COL_SALE_TYPE,
                DataContract.InvoiceDetails.COL_QUANTITY,
                DataContract.InvoiceDetails.COL_UNIT,
                DataContract.InvoiceDetails.COL_RATE,
                DataContract.InvoiceDetails.COL_DISCOUNT,
                DataContract.InvoiceDetails.COL_NET_AMOUNT,

        };
        String selection = DataContract.Invoice.COL_INVOICE_NUMBER + " LIKE ?";
        String[] selection_ars = {invoiceId};
        Cursor cursor = database.query(DataContract.InvoiceDetails.TABLE_NAME, projection, selection, selection_ars, null, null, null);
        Log.d(TAG, "Refund details cursor size" + cursor.getCount());
        if (cursor.moveToFirst()) {
            do {

                CartModel cartModel = new CartModel();

                cartModel.setBarcode(cursor.getString(cursor.getColumnIndex(DataContract.InvoiceDetails.COL_BAR_CODE)));
                cartModel.setProductCode(cursor.getString(cursor.getColumnIndex(DataContract.InvoiceDetails.COL_PRODUCT_CODE)));
                cartModel.setProductName(cursor.getString(cursor.getColumnIndex(DataContract.InvoiceDetails.COL_PRODUCT_NAME)));
                cartModel.setSaleType(cursor.getString(cursor.getColumnIndex(DataContract.InvoiceDetails.COL_SALE_TYPE)));
                cartModel.setQty(cursor.getDouble(cursor.getColumnIndex(DataContract.InvoiceDetails.COL_QUANTITY)));
                cartModel.setSelectedUnit(cursor.getString(cursor.getColumnIndex(DataContract.InvoiceDetails.COL_UNIT)));
                cartModel.setRate(cursor.getDouble(cursor.getColumnIndex(DataContract.InvoiceDetails.COL_RATE)));
                cartModel.setDiscount(cursor.getDouble(cursor.getColumnIndex(DataContract.InvoiceDetails.COL_DISCOUNT)));
                cartModel.setNet(cursor.getDouble(cursor.getColumnIndex(DataContract.InvoiceDetails.COL_NET_AMOUNT)));
                Cart.mCart.add(cartModel);
            } while (cursor.moveToNext());

        }
        cursor.close();
        database.close();
    }

    public void updateInvoiceSync(String doc) {
        try {
            SQLiteDatabase database = getWritableDatabase();
            ContentValues values = new ContentValues();
            values.put(DataContract.Invoice.COL_IS_SYNC, DataContract.SYNC_STATUS_OK);
            database.update(DataContract.Invoice.TABLE_NAME, values, DataContract.Invoice.COL_INVOICE_NUMBER + "=?", new String[]{String.valueOf(doc)});

            Log.v(TAG, "invoice sync updated");
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }


    public int getLastInvoiceNo() {
        SQLiteDatabase database = getReadableDatabase();
        String[] projection = {DataContract.Invoice.COL_ID};
        Cursor cursor = database.query(DataContract.Invoice.TABLE_NAME, projection, null, null, null, null, DataContract.Invoice.COL_ID + " DESC ", "1");


        //Cursor cursor=database.query(DataContract.Invoice.TABLE_NAME,projections,null,null,null,null,DataContract.Invoice.COL_INVOICE_ID +" DESC ","1");
        int id = 0;
        if (cursor.moveToFirst()) {
            id = cursor.getInt(cursor.getColumnIndex(DataContract.Invoice.COL_ID));

        }
        cursor.close();
        database.close();
        return id;

    }

    public boolean deleteInvoiceById(String invoice_no) {
        SQLiteDatabase database = getReadableDatabase();
        return database.delete(DataContract.Invoice.TABLE_NAME, DataContract.Invoice.COL_INVOICE_NUMBER + "= ?", new String[]{invoice_no}) > 0;
    }




    /************************ end invoice*******************************************************/


    /************************************paper settings ********************************/
    public boolean savePaperSettings(String Name, String Address, String Phone, String Footer, String Size) {

        SQLiteDatabase database = getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        try {
            contentValues.put(DataContract.PaperSettings.COL_COMPANY_NAME, Name);
            contentValues.put(DataContract.PaperSettings.COL_COMPANY_ADDRESS, Address);
            contentValues.put(DataContract.PaperSettings.COL_COMPANY_PHONE, Phone);
            contentValues.put(DataContract.PaperSettings.COL_FOOTER, Footer);
            contentValues.put(DataContract.PaperSettings.COL_PAPER_SIZE, Size);
            database.insert(DataContract.PaperSettings.TABLE_NAME, null, contentValues);
            database.close();
            Log.d(TAG, "one row inserted in Paper Settings table ......... ");
            return true;
        } catch (Exception e) {
            return false;
        }
    }


    public Cursor getPaperSettings(SQLiteDatabase sqLiteDatabase) {
        String[] projections = {DataContract.PaperSettings.COL_COMPANY_NAME, DataContract.PaperSettings.COL_COMPANY_ADDRESS,
                DataContract.PaperSettings.COL_COMPANY_PHONE, DataContract.PaperSettings.COL_FOOTER,
                DataContract.PaperSettings.COL_PAPER_SIZE};
        Cursor cursor = sqLiteDatabase.query(DataContract.PaperSettings.TABLE_NAME, projections, null, null, null, null,
                DataContract.PaperSettings.COL_ID + " DESC ", "1");
        return cursor;
    }
    /************************************end paper settings********************************/

    public boolean getLogin(String password) {
        SQLiteDatabase database = getReadableDatabase();
        boolean res = false;

        Cursor cursor = database.query(DataContract.Login.TABLE_NAME,
                new String[]{DataContract.Login.COL_PASSWORD},
                DataContract.Login.COL_PASSWORD + "=?",
                new String[]{password}, null, null, null, null);
        if (cursor.moveToFirst()) {
            res = true;
        }
        cursor.close();
        database.close();
        return res;
    }

    public boolean checkLogin() {
        SQLiteDatabase database = getReadableDatabase();
        String[] projection = {DataContract.Login.COL_PASSWORD};
        Cursor cursor = database.query(DataContract.Login.TABLE_NAME, projection, null, null, null, null, null);
        if (cursor.moveToFirst()) {
            return true;
        } else {
            return false;

        }
    }






}
