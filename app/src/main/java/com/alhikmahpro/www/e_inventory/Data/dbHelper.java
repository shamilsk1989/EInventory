package com.alhikmahpro.www.e_inventory.Data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.provider.ContactsContract;
import android.util.Log;
import android.widget.Toast;

import com.amitshekhar.utils.DatabaseHelper;

import java.util.ArrayList;
import java.util.List;

public class dbHelper extends SQLiteOpenHelper {
    public static final String DATABASE_NAME = "m-inv";
    public static final int DATABASE_VERSION = 7;
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
            DataContract.Settings.COL_IS_REC + " INTEGER DEFAULT 0, " +
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

    private final String SQL_CREATE_AUTO_ID_GENERATOR_TABLE = "CREATE TABLE IF NOT EXISTS " +
            DataContract.AutoIdGenerator.TABLE_NAME + " (" +
            DataContract.AutoIdGenerator.COL_INVOICE_TABLE + " INTEGER," +
            DataContract.AutoIdGenerator.COL_ORDER_TABLE + " INTEGER," +
            DataContract.AutoIdGenerator.COL_GOODS_TABLE+ " INTEGER," +
            DataContract.AutoIdGenerator.COL_STOCK_TABLE + " INTEGER  " + ");";



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
            DataContract.GoodsReceive.COL_DISCOUNT_PERCENTAGE + " REAL ," +
            DataContract.GoodsReceive.COL_NET_AMOUNT + " REAL ," +
            DataContract.GoodsReceive.COL_OTHER_AMOUNT + " REAL ," +
            DataContract.GoodsReceive.COL_GRAND_TOTAL_AMOUNT + " REAL ," +
            DataContract.GoodsReceive.COL_SERVER_INVOICE_NUMBER + " TEXT ," +
            DataContract.GoodsReceive.COL_PAYMENT_TYPE + " TEXT ," +
            DataContract.GoodsReceive.COL_DATE_TIME + " TEXT ," +
            DataContract.GoodsReceive.COL_IS_SYNC + " INTEGER DEFAULT 0 " + ");";


    private final String SQL_CREATE_GOODS_DETAILS_TABLE = "CREATE TABLE IF NOT EXISTS " + DataContract.GoodsReceiveDetails.TABLE_NAME + " (" +
            DataContract.GoodsReceiveDetails.COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +

            DataContract.GoodsReceiveDetails.COL_DOCUMENT_NUMBER + " INTEGER ," +
            DataContract.GoodsReceiveDetails.COL_BAR_CODE + " TEXT ," +
            DataContract.GoodsReceiveDetails.COL_PRODUCT_CODE + " TEXT ," +
            DataContract.GoodsReceiveDetails.COL_PRODUCT_NAME + " TEXT ," +
            DataContract.GoodsReceiveDetails.COL_PRODUCT_NAME_AR + " TEXT ," +
            DataContract.GoodsReceiveDetails.COL_UNIT + " TEXT ," +
            DataContract.GoodsReceiveDetails.COL_FREE_UNIT + " TEXT ," +
            DataContract.GoodsReceiveDetails.COL_QUANTITY + " REAL ," +
            DataContract.GoodsReceiveDetails.COL_FREE_QUANTITY + " REAL ," +
            DataContract.GoodsReceiveDetails.COL_RATE + " REAL ," +
            DataContract.GoodsReceiveDetails.COL_DISCOUNT + " REAL ," +
            DataContract.GoodsReceiveDetails.COL_DISCOUNT_PERCENTAGE + " REAL ," +
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
            DataContract.Invoice.COL_DISCOUNT_PERCENTAGE + " REAL ," +
            DataContract.Invoice.COL_PAYMENT_TYPE + " TEXT ," +
            DataContract.Invoice.COL_NET_AMOUNT + " REAL ," +
            DataContract.Invoice.COL_OTHER_AMOUNT + " REAL ," +
            DataContract.Invoice.COL_GRAND_TOTAL_AMOUNT + " REAL ," +
            DataContract.Invoice.COL_SERVER_INVOICE_NUMBER + " TEXT ," +
            DataContract.Invoice.COL_DATE_TIME + " TEXT ," +
            DataContract.Invoice.COL_IS_SYNC + " INTEGER DEFAULT 0 " + ");";


    private final String SQL_CREATE_INVOICE_DETAILS_TABLE = "CREATE TABLE IF NOT EXISTS " + DataContract.InvoiceDetails.TABLE_NAME + " (" +
            DataContract.InvoiceDetails.COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +

            DataContract.InvoiceDetails.COL_INVOICE_NUMBER + " TEXT ," +
            DataContract.InvoiceDetails.COL_BAR_CODE + " TEXT ," +
            DataContract.InvoiceDetails.COL_PRODUCT_CODE + " TEXT ," +
            DataContract.InvoiceDetails.COL_PRODUCT_NAME + " TEXT ," +
            DataContract.InvoiceDetails.COL_PRODUCT_NAME_AR + " TEXT ," +
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
            DataContract.InvoiceDetails.COL_DISCOUNT_PERCENTAGE + " REAL ," +
            DataContract.InvoiceDetails.COL_NET_AMOUNT + " REAL ," +
            DataContract.InvoiceDetails.COL_SALE_TYPE + " TEXT ," +
            DataContract.InvoiceDetails.COL_IS_SYNC + " INTEGER DEFAULT 0 " + ");";



    private final String SQL_CREATE_ORDER_TABLE = " CREATE TABLE IF NOT EXISTS " +DataContract.Order.TABLE_NAME+ " (" +

            DataContract.Order.COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
            DataContract.Order.COL_INVOICE_NUMBER + " TEXT ," +
            DataContract.Order.COL_INVOICE_DATE + " TEXT ," +
            DataContract.Order.COL_SALESMAN_ID + " TEXT ," +
            DataContract.Order.COL_CUSTOMER_CODE + " TEXT ," +
            DataContract.Order.COL_CUSTOMER_NAME + " TEXT ," +
            DataContract.Order.COL_TOTAL_AMOUNT + " REAL ," +
            DataContract.Order.COL_DISCOUNT_AMOUNT + " REAL ," +
            DataContract.Order.COL_DISCOUNT_PERCENTAGE + " REAL ," +
            DataContract.Order.COL_PAYMENT_TYPE + " TEXT ," +
            DataContract.Order.COL_NET_AMOUNT + " REAL ," +
            DataContract.Order.COL_OTHER_AMOUNT + " REAL ," +
            DataContract.Order.COL_GRAND_TOTAL_AMOUNT + " REAL ," +
            DataContract.Order.COL_SERVER_INVOICE_NUMBER + " TEXT ," +
            DataContract.Order.COL_DATE_TIME + " TEXT ," +
            DataContract.Order.COL_IS_SYNC + " INTEGER DEFAULT 0 " + ");";


    private final String SQL_CREATE_ORDER_DETAILS_TABLE = "CREATE TABLE IF NOT EXISTS " + DataContract.OrderDetails.TABLE_NAME + " (" +
            DataContract.InvoiceDetails.COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +

            DataContract.InvoiceDetails.COL_INVOICE_NUMBER + " TEXT ," +
            DataContract.InvoiceDetails.COL_BAR_CODE + " TEXT ," +
            DataContract.InvoiceDetails.COL_PRODUCT_CODE + " TEXT ," +
            DataContract.InvoiceDetails.COL_PRODUCT_NAME + " TEXT ," +
            DataContract.InvoiceDetails.COL_PRODUCT_NAME_AR + " TEXT ," +
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
            DataContract.InvoiceDetails.COL_DISCOUNT_PERCENTAGE + " REAL ," +
            DataContract.InvoiceDetails.COL_NET_AMOUNT + " REAL ," +
            DataContract.InvoiceDetails.COL_SALE_TYPE + " TEXT ," +
            DataContract.InvoiceDetails.COL_IS_SYNC + " INTEGER DEFAULT 0 " + ");";

    private final String SQL_CREATE_PAPER_SETTINGS_TABLE = "CREATE TABLE IF NOT EXISTS " + DataContract.PaperSettings.TABLE_NAME + " (" +

            DataContract.PaperSettings.COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
            DataContract.PaperSettings.COL_COMPANY_NAME + " TEXT," +
            DataContract.PaperSettings.COL_COMPANY_ADDRESS + " TEXT," +
            DataContract.PaperSettings.COL_COMPANY_PHONE + " TEXT," +
            DataContract.PaperSettings.COL_FOOTER + " TEXT," +
            DataContract.Settings.COL_LOGO + " BLOB, " +
            DataContract.PaperSettings.COL_PAPER_SIZE + " TEXT " + ");";


    private final String SQL_CREATE_RECEIPT_TABLE = "CREATE TABLE IF NOT EXISTS " + DataContract.Receipts.TABLE_NAME + " (" +
            DataContract.Receipts.COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
            DataContract.Receipts.COL_RECEIPT_NUMBER + " TEXT ," +
            DataContract.Receipts.COL_RECEIPT_DATE + " TEXT ," +
            DataContract.Receipts.COL_SALESMAN_ID + " TEXT ," +
            DataContract.Receipts.COL_CUSTOMER_CODE + " TEXT ," +
            DataContract.Receipts.COL_CUSTOMER_NAME + " TEXT ," +
            DataContract.Receipts.COL_BALANCE_AMOUNT + " REAL ," +
            DataContract.Receipts.COL_RECEIVED_AMOUNT + " REAL ," +
            DataContract.Receipts.COL_PAYMENT_TYPE + " TEXT ," +
            DataContract.Receipts.COL_CHEQUE_DATE + " TEXT ," +
            DataContract.Receipts.COL_CHEQUE_NUMBER + " TEXT ," +
            DataContract.Receipts.COL_REMARK + " TEXT ," +
            DataContract.Receipts.COL_IS_SYNC + " INTEGER DEFAULT 0 ," +
            DataContract.Receipts.COL_SERVER_INVOICE_NUMBER + " TEXT " + ");";


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
        db.execSQL(SQL_CREATE_ORDER_TABLE);
        db.execSQL(SQL_CREATE_ORDER_DETAILS_TABLE);
        db.execSQL(SQL_CREATE_PAPER_SETTINGS_TABLE);
        db.execSQL(SQL_CREATE_RECEIPT_TABLE);
        db.execSQL(SQL_CREATE_AUTO_ID_GENERATOR_TABLE);

        Log.d(TAG, "Table created......: ");

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.d(TAG, "onUpgrade: "+"old version"+oldVersion+"/"+"new version"+newVersion);
        db.execSQL("DROP TABLE IF EXISTS " + DataContract.Settings.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + DataContract.Stocks.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + DataContract.StocksDetails.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + DataContract.Login.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + DataContract.GoodsReceive.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + DataContract.GoodsReceiveDetails.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + DataContract.Invoice.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + DataContract.InvoiceDetails.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + DataContract.Order.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + DataContract.OrderDetails.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + DataContract.PaperSettings.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + DataContract.Receipts.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + DataContract.AutoIdGenerator.TABLE_NAME);
        onCreate(db);
    }

    //*****************************settings table********************

    public long saveLogo(byte[] logo) {
        SQLiteDatabase database = getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        long id=0;
        contentValues.put(DataContract.Settings.COL_LOGO, logo);
        id=database.insert(DataContract.Settings.TABLE_NAME, null, contentValues);
        database.close();
        return id;
    }

//    public long saveSettings(String CCode, String CCName, String BCode, String LCode, String PCode, String deviceId,
//                                byte[] logo, int saleStat, int invStat, int gdsStat, int recStat) {
//
//        SQLiteDatabase database = getWritableDatabase();
//        ContentValues contentValues = new ContentValues();
//        long id=0;
//        try {
//            contentValues.put(DataContract.Settings.COL_COMPANY_CODE, CCode);
//            contentValues.put(DataContract.Settings.COL_COMPANY_NAME, CCName);
//            contentValues.put(DataContract.Settings.COL_BRANCH_CODE, BCode);
//            contentValues.put(DataContract.Settings.COL_LOCATION_CODE, LCode);
//            contentValues.put(DataContract.Settings.COL_PERIOD_CODE, PCode);
//            contentValues.put(DataContract.Settings.COL_DEVICE_ID, deviceId);
//            contentValues.put(DataContract.Settings.COL_LOGO, logo);
//            contentValues.put(DataContract.Settings.COL_IS_SALE, saleStat);
//            contentValues.put(DataContract.Settings.COL_IS_INV, invStat);
//            contentValues.put(DataContract.Settings.COL_IS_GDS, gdsStat);
//            contentValues.put(DataContract.Settings.COL_IS_REC, recStat);
//            id=database.insert(DataContract.Settings.TABLE_NAME, null, contentValues);
//            Log.d(TAG, "one row inserted in Settings table ......... ");
//
//        } catch (Exception e) {
//           id=0;
//        }
//        database.close();
//        return id;
//    }

//    public boolean updateSettings(int _id, String CCode, String CCName, String BCode, String LCode, String PCode, String deviceId,
//                                  byte[] logo, int saleStat, int invStat, int gdsStat, int recStat) {
//
//        SQLiteDatabase database = getWritableDatabase();
//        ContentValues contentValues = new ContentValues();
//        String selection = DataContract.Settings.COL_ID + " = " + _id;
//        try {
//
//            contentValues.put(DataContract.Settings.COL_COMPANY_CODE, CCode);
//            contentValues.put(DataContract.Settings.COL_COMPANY_NAME, CCName);
//            contentValues.put(DataContract.Settings.COL_BRANCH_CODE, BCode);
//            contentValues.put(DataContract.Settings.COL_LOCATION_CODE, LCode);
//            contentValues.put(DataContract.Settings.COL_PERIOD_CODE, PCode);
//            contentValues.put(DataContract.Settings.COL_DEVICE_ID, deviceId);
//            contentValues.put(DataContract.Settings.COL_LOGO, logo);
//            contentValues.put(DataContract.Settings.COL_IS_SALE, saleStat);
//            contentValues.put(DataContract.Settings.COL_IS_INV, invStat);
//            contentValues.put(DataContract.Settings.COL_IS_GDS, gdsStat);
//            contentValues.put(DataContract.Settings.COL_IS_REC, recStat);
//            database.update(DataContract.Settings.TABLE_NAME, contentValues, selection, null);
//            database.close();
//            Log.d(TAG, " updated Settings table ......... ");
//            return true;
//        } catch (Exception e) {
//            return false;
//        }
//    }

//    public Cursor getSettings(SQLiteDatabase sqLiteDatabase) {
//        String[] projections = {
//                DataContract.Settings.COL_ID, DataContract.Settings.COL_ID,
//                DataContract.Settings.COL_COMPANY_CODE, DataContract.Settings.COL_COMPANY_NAME,
//                DataContract.Settings.COL_BRANCH_CODE, DataContract.Settings.COL_LOCATION_CODE,
//                DataContract.Settings.COL_DEVICE_ID, DataContract.Settings.COL_DEVICE_ID,
//                DataContract.Settings.COL_LOGO, DataContract.Settings.COL_LOGO,
//                DataContract.Settings.COL_PERIOD_CODE, DataContract.Settings.COL_PERIOD_CODE,
//                DataContract.Settings.COL_IS_SALE, DataContract.Settings.COL_IS_SALE,
//                DataContract.Settings.COL_IS_GDS, DataContract.Settings.COL_IS_GDS,
//                DataContract.Settings.COL_IS_INV, DataContract.Settings.COL_IS_INV,
//                DataContract.Settings.COL_IS_REC, DataContract.Settings.COL_IS_REC,
//
//        };
//        Cursor cursor = sqLiteDatabase.query(DataContract.Settings.TABLE_NAME, projections, null,
//                null, null, null, DataContract.Settings.COL_ID + " DESC ", "1 ");
//
//        // database.close();DataContract.BillTitles.COL_TITLE_ID +" DESC ","1"
//        return cursor;
//
//    }

    public Bitmap getLogo(SQLiteDatabase sqLiteDatabase) {
        String[] projections = {
                DataContract.Settings.COL_ID, DataContract.Settings.COL_LOGO,
        };

        Bitmap bitmap=null;
        Cursor cursor = sqLiteDatabase.query(DataContract.Settings.TABLE_NAME, projections, null,
                null, null, null, DataContract.Settings.COL_ID + " DESC ", "1 ");
        if (cursor.moveToFirst()) {

            byte[] img = cursor.getBlob(cursor.getColumnIndex(DataContract.Settings.COL_LOGO));
            try {
                bitmap = BitmapFactory.decodeByteArray(img, 0, img.length);

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        cursor.close();
        return bitmap;
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

    /************** insert  value into auto id tables *************************************************/

    public long saveAutoGeneratorId(int invId,int ordId,int gdsId,int stkId) {

        SQLiteDatabase database = getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(DataContract.AutoIdGenerator.COL_INVOICE_TABLE,invId);
        contentValues.put(DataContract.AutoIdGenerator.COL_ORDER_TABLE,ordId);
        contentValues.put(DataContract.AutoIdGenerator.COL_GOODS_TABLE,gdsId);
        contentValues.put(DataContract.AutoIdGenerator.COL_STOCK_TABLE,stkId);
        long insert=database.insert(DataContract.AutoIdGenerator.TABLE_NAME,null,contentValues);
        database.close();
        return insert;
    }

    public boolean deleteAllAutoId()
    {
        SQLiteDatabase database = getReadableDatabase();
        boolean res = database.delete(DataContract.AutoIdGenerator.TABLE_NAME, null,null) > 0;
        database.close();
        return res;
    }

    public Cursor getAllAutoId(SQLiteDatabase database){
        //SQLiteDatabase database=getReadableDatabase();
        String[] projection = {
                DataContract.AutoIdGenerator.COL_ORDER_TABLE,
                DataContract.AutoIdGenerator.COL_STOCK_TABLE,
                DataContract.AutoIdGenerator.COL_GOODS_TABLE,
                DataContract.AutoIdGenerator.COL_INVOICE_TABLE
        };
        Cursor cursor = database.query(DataContract.AutoIdGenerator.TABLE_NAME, projection,
                null, null, null, null, null);
        //database.close();
        return cursor;
    }





//    public boolean updateAutoGeneratorId(String tableName, int id) {
//
//        SQLiteDatabase database = getWritableDatabase();
//        ContentValues contentValues = new ContentValues();
//        contentValues.put(DataContract.AutoIdGenerator.COL_TABLE_NAME, tableName);
//        contentValues.put(DataContract.AutoIdGenerator.COL_TABLE_NAME, tableName);
//        String selection = DataContract.Stocks.COL_DOCUMENT_NUMBER + " = " + doc_no;
//        database.update(DataContract.Stocks.TABLE_NAME, contentValues, selection, null);
//        database.close();
//
//    }


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
            database.close();

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

    public List<ItemModel> getStocks() {
        SQLiteDatabase database=getReadableDatabase();
        Log.d(TAG, "getStocks: ");
        List<ItemModel>list=new ArrayList<>();
        String[] projection = {DataContract.Stocks.COL_STAFF_NAME, DataContract.Stocks.COL_TOTAL, DataContract.Stocks.COL_DOCUMENT_NUMBER
                , DataContract.Stocks.COL_DATE_TIME, DataContract.Stocks.COL_IS_SYNC};
        String orderBy = DataContract.Stocks.COL_ID + " DESC ";
        Cursor cursor = database.query(DataContract.Stocks.TABLE_NAME, projection, null, null, null, null, orderBy);
        if (cursor.moveToFirst()) {
            do {
                ItemModel model = new ItemModel();
                Log.d(TAG, "initView: "+cursor.getInt(cursor.getColumnIndex(DataContract.Stocks.COL_DOCUMENT_NUMBER)));
                model.setDocNo(cursor.getInt(cursor.getColumnIndex(DataContract.Stocks.COL_DOCUMENT_NUMBER)));
                model.setStaffName(cursor.getString(cursor.getColumnIndex(DataContract.Stocks.COL_STAFF_NAME)));
                model.setTotal(cursor.getDouble(cursor.getColumnIndex(DataContract.Stocks.COL_TOTAL)));
                model.setDate(cursor.getString(cursor.getColumnIndex(DataContract.Stocks.COL_DATE_TIME)));
                list.add(model);
            } while (cursor.moveToNext());
        }

        cursor.close();
        database.close();
        return list;
    }


    public List<ItemModel> getStockDetailsByDoc(int doc_no) {
        List<ItemModel> listItem = new ArrayList<>();
        SQLiteDatabase database=getReadableDatabase();

        String[] projections = {DataContract.StocksDetails.COL_ID, DataContract.StocksDetails.COL_DOCUMENT_NUMBER, DataContract.StocksDetails.COL_BAR_CODE, DataContract.StocksDetails.COL_PRODUCT_CODE,
                DataContract.StocksDetails.COL_PRODUCT_NAME, DataContract.StocksDetails.COL_UNIT, DataContract.StocksDetails.COL_QUANTITY,
                DataContract.StocksDetails.COL_SALES_PRICE, DataContract.StocksDetails.COL_COST_PRICE};
        String selection = DataContract.StocksDetails.COL_DOCUMENT_NUMBER + " = " + doc_no;
        Cursor cursor = database.query(DataContract.StocksDetails.TABLE_NAME, projections, selection, null, null, null, null);
        if (cursor.moveToFirst()) {
            do {
                ItemModel itemModel = new ItemModel();
                itemModel.set_id(cursor.getInt(cursor.getColumnIndex(DataContract.StocksDetails.COL_ID)));
                itemModel.setDocNo(cursor.getInt(cursor.getColumnIndex(DataContract.StocksDetails.COL_DOCUMENT_NUMBER)));
                itemModel.setBarCode(cursor.getString(cursor.getColumnIndex(DataContract.StocksDetails.COL_BAR_CODE)));
                itemModel.setProductCode(cursor.getString(cursor.getColumnIndex(DataContract.StocksDetails.COL_PRODUCT_CODE)));
                itemModel.setProductName(cursor.getString(cursor.getColumnIndex(DataContract.StocksDetails.COL_PRODUCT_NAME)));
                itemModel.setSelectedPackage(cursor.getString(cursor.getColumnIndex(DataContract.StocksDetails.COL_UNIT)));
                itemModel.setQty(cursor.getDouble(cursor.getColumnIndex(DataContract.StocksDetails.COL_QUANTITY)));
                itemModel.setSalePrice(cursor.getDouble(cursor.getColumnIndex(DataContract.StocksDetails.COL_SALES_PRICE)));
                itemModel.setCostPrice(cursor.getDouble(cursor.getColumnIndex(DataContract.StocksDetails.COL_COST_PRICE)));
                listItem.add(itemModel);
            } while (cursor.moveToNext());
        }
        return listItem;
    }

    public boolean deleteStockDetailsById(int _id) {
        SQLiteDatabase database = getReadableDatabase();
        boolean res= database.delete(DataContract.StocksDetails.TABLE_NAME, DataContract.StocksDetails.COL_ID + "= ?", new String[]{String.valueOf(_id)}) > 0;
        database.close();
        return res;
    }

//    public int getLastDocNo() {
//        SQLiteDatabase database = getReadableDatabase();
//        String[] projection = {DataContract.Stocks.COL_DOCUMENT_NUMBER};
//        Cursor cursor = database.query(DataContract.Stocks.TABLE_NAME, projection, null, null, null, null, DataContract.Stocks.COL_DOCUMENT_NUMBER + " DESC ", "1");
//
//
//        //Cursor cursor=database.query(DataContract.Invoice.TABLE_NAME,projections,null,null,null,null,DataContract.Invoice.COL_INVOICE_ID +" DESC ","1");
//        int id = 0;
//        if (cursor.moveToFirst()) {
//            id = cursor.getInt(cursor.getColumnIndex(DataContract.Stocks.COL_DOCUMENT_NUMBER));
//
//        }
//        cursor.close();
//        database.close();
//        return id;
//    }

    /******************************* end stock table***********************************/


    /******************************* Goods table***********************************/

    public long saveGoods(int docNo, String orderNo, String supplierCode, String supplierName, String invoiceNo, String invoiceDate,
                             String staffName, double total, double disc, double net, double other, double gTotal,
                             String payment, String date, String serverInvoice, int status) {

        SQLiteDatabase database = getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        Log.d(TAG, "saveGoods: " + serverInvoice);

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
            contentValues.put(DataContract.GoodsReceive.COL_OTHER_AMOUNT, other);
            contentValues.put(DataContract.GoodsReceive.COL_GRAND_TOTAL_AMOUNT, gTotal);
            contentValues.put(DataContract.GoodsReceive.COL_PAYMENT_TYPE, payment);
            contentValues.put(DataContract.GoodsReceive.COL_DATE_TIME, date);
            contentValues.put(DataContract.GoodsReceive.COL_SERVER_INVOICE_NUMBER, serverInvoice);
            contentValues.put(DataContract.GoodsReceive.COL_IS_SYNC, status);
            long id=database.insert(DataContract.GoodsReceive.TABLE_NAME, null, contentValues);
            database.close();
            Log.d(TAG, "one row inserted in goods table ......... ");
            return id;

    }



    public long saveGoodsDetails(int docNo, String bCode,String pCode,String pName,String pNameAr,String unit,
                                    String freeUnit,double qty,double freeQty,double rate,double disc,double disPerc,
                                    double salePrice,double costPrice,String stock,double netVal,String unit1,String unit2,
                                    String unit3,int qty1,int qty2,int qty3,int status) {


        SQLiteDatabase database = getWritableDatabase();
        ContentValues contentValues = new ContentValues();

        contentValues.put(DataContract.GoodsReceiveDetails.COL_DOCUMENT_NUMBER, docNo);
        contentValues.put(DataContract.GoodsReceiveDetails.COL_BAR_CODE, bCode);
        contentValues.put(DataContract.GoodsReceiveDetails.COL_PRODUCT_CODE, pCode);
        contentValues.put(DataContract.GoodsReceiveDetails.COL_PRODUCT_NAME,pName);
        contentValues.put(DataContract.GoodsReceiveDetails.COL_PRODUCT_NAME_AR,pNameAr);
        contentValues.put(DataContract.GoodsReceiveDetails.COL_UNIT, unit);
        contentValues.put(DataContract.GoodsReceiveDetails.COL_FREE_UNIT, freeUnit);
        contentValues.put(DataContract.GoodsReceiveDetails.COL_QUANTITY, qty);
        contentValues.put(DataContract.GoodsReceiveDetails.COL_FREE_QUANTITY, freeQty);
        contentValues.put(DataContract.GoodsReceiveDetails.COL_RATE,rate);
        contentValues.put(DataContract.GoodsReceiveDetails.COL_DISCOUNT,disc);
        contentValues.put(DataContract.GoodsReceiveDetails.COL_DISCOUNT_PERCENTAGE,disPerc);
        contentValues.put(DataContract.GoodsReceiveDetails.COL_SALES_PRICE, salePrice);
        contentValues.put(DataContract.GoodsReceiveDetails.COL_COST_PRICE, costPrice);
        contentValues.put(DataContract.GoodsReceiveDetails.COL_STOCK, stock);
        contentValues.put(DataContract.GoodsReceiveDetails.COL_NET_VALUE, netVal);
        contentValues.put(DataContract.GoodsReceiveDetails.COL_UNIT1,unit1);
        contentValues.put(DataContract.GoodsReceiveDetails.COL_UNIT2, unit2);
        contentValues.put(DataContract.GoodsReceiveDetails.COL_UNIT3, unit3);
        contentValues.put(DataContract.GoodsReceiveDetails.COL_UN_QTY1,qty1);
        contentValues.put(DataContract.GoodsReceiveDetails.COL_UN_QTY2, qty2);
        contentValues.put(DataContract.GoodsReceiveDetails.COL_UN_QTY3,qty3);
        contentValues.put(DataContract.GoodsReceiveDetails.COL_IS_SYNC, status);
        long last=database.insert(DataContract.GoodsReceiveDetails.TABLE_NAME, null, contentValues);
        database.close();
        Log.d(TAG, "one row inserted in GoodsReceiveDetails table ......... ");
        return last;
    }
//    public boolean saveGoodsDetails1(int docNo, int status) {
//
//        boolean result;
//        SQLiteDatabase database = getWritableDatabase();
//        ContentValues contentValues = new ContentValues();
//        try {
//            for (ItemModel model : Cart.gCart) {
//                contentValues.put(DataContract.GoodsReceiveDetails.COL_DOCUMENT_NUMBER, docNo);
//                contentValues.put(DataContract.GoodsReceiveDetails.COL_BAR_CODE, model.getBarCode());
//                contentValues.put(DataContract.GoodsReceiveDetails.COL_PRODUCT_CODE, model.getProductCode());
//                contentValues.put(DataContract.GoodsReceiveDetails.COL_PRODUCT_NAME, model.getProductName());
//                contentValues.put(DataContract.GoodsReceiveDetails.COL_UNIT, model.getSelectedPackage());
//                contentValues.put(DataContract.GoodsReceiveDetails.COL_FREE_UNIT, model.getSelectedFreePackage());
//                contentValues.put(DataContract.GoodsReceiveDetails.COL_QUANTITY, model.getQty());
//                contentValues.put(DataContract.GoodsReceiveDetails.COL_FREE_QUANTITY, model.getFreeQty());
//                contentValues.put(DataContract.GoodsReceiveDetails.COL_RATE, model.getRate());
//                contentValues.put(DataContract.GoodsReceiveDetails.COL_DISCOUNT, model.getDiscount());
//                contentValues.put(DataContract.GoodsReceiveDetails.COL_SALES_PRICE, model.getSalePrice());
//                contentValues.put(DataContract.GoodsReceiveDetails.COL_COST_PRICE, model.getCostPrice());
//                contentValues.put(DataContract.GoodsReceiveDetails.COL_STOCK, model.getStock());
//                contentValues.put(DataContract.GoodsReceiveDetails.COL_NET_VALUE, model.getNet());
//                contentValues.put(DataContract.GoodsReceiveDetails.COL_UNIT1, model.getUnit1());
//                contentValues.put(DataContract.GoodsReceiveDetails.COL_UNIT2, model.getUnit2());
//                contentValues.put(DataContract.GoodsReceiveDetails.COL_UNIT3, model.getUnit3());
//                contentValues.put(DataContract.GoodsReceiveDetails.COL_UN_QTY1, model.getUnit1Qty());
//                contentValues.put(DataContract.GoodsReceiveDetails.COL_UN_QTY2, model.getUnit2Qty());
//                contentValues.put(DataContract.GoodsReceiveDetails.COL_UN_QTY3, model.getUnit3Qty());
//                contentValues.put(DataContract.GoodsReceiveDetails.COL_IS_SYNC, status);
//                database.insert(DataContract.GoodsReceiveDetails.TABLE_NAME, null, contentValues);
//                database.close();
//                Log.d(TAG, "one row inserted in GoodsReceiveDetails table ......... ");
//
//            }
//            result = true;
//
//        } catch (SQLException e) {
//            result = false;
//        }
//
//        return result;
//    }


    public long updateGoods(int docNo, String orderNo, String supplierCode, String supplierName, String invoiceNo, String invoiceDate,
                            String staffName, double total, double disc, double net, double other, double gTotal, String payment, String date, String serverInvoice, int status) {
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
        contentValues.put(DataContract.GoodsReceive.COL_OTHER_AMOUNT, other);
        contentValues.put(DataContract.GoodsReceive.COL_GRAND_TOTAL_AMOUNT, gTotal);
        contentValues.put(DataContract.GoodsReceive.COL_PAYMENT_TYPE, payment);

        contentValues.put(DataContract.GoodsReceive.COL_DATE_TIME, date);
        contentValues.put(DataContract.GoodsReceive.COL_SERVER_INVOICE_NUMBER, serverInvoice);
        contentValues.put(DataContract.GoodsReceive.COL_IS_SYNC, status);
        long id=database.update(DataContract.GoodsReceive.TABLE_NAME, contentValues, DataContract.GoodsReceive.COL_DOCUMENT_NUMBER + "=?", new String[]{String.valueOf(docNo)});
        Log.d(TAG, "one row update in GoodsReceiveDetails table ......... ");
        database.close();
        return id;

    }

//    public boolean updateGoodsDetails1(int docNo, int status) {
//
//        boolean result;
//        SQLiteDatabase database = getWritableDatabase();
//        ContentValues contentValues = new ContentValues();
//        try {
//            for (ItemModel model : Cart.gCart) {
//                contentValues.put(DataContract.GoodsReceiveDetails.COL_DOCUMENT_NUMBER, docNo);
//                contentValues.put(DataContract.GoodsReceiveDetails.COL_BAR_CODE, model.getBarCode());
//                contentValues.put(DataContract.GoodsReceiveDetails.COL_PRODUCT_CODE, model.getProductCode());
//                contentValues.put(DataContract.GoodsReceiveDetails.COL_PRODUCT_NAME, model.getProductName());
//                contentValues.put(DataContract.GoodsReceiveDetails.COL_UNIT, model.getSelectedPackage());
//                contentValues.put(DataContract.GoodsReceiveDetails.COL_FREE_UNIT, model.getSelectedFreePackage());
//                contentValues.put(DataContract.GoodsReceiveDetails.COL_QUANTITY, model.getQty());
//                contentValues.put(DataContract.GoodsReceiveDetails.COL_FREE_QUANTITY, model.getFreeQty());
//                contentValues.put(DataContract.GoodsReceiveDetails.COL_RATE, model.getRate());
//                contentValues.put(DataContract.GoodsReceiveDetails.COL_DISCOUNT, model.getDiscount());
//                contentValues.put(DataContract.GoodsReceiveDetails.COL_SALES_PRICE, model.getSalePrice());
//                contentValues.put(DataContract.GoodsReceiveDetails.COL_COST_PRICE, model.getCostPrice());
//                contentValues.put(DataContract.GoodsReceiveDetails.COL_STOCK, model.getStock());
//                contentValues.put(DataContract.GoodsReceiveDetails.COL_NET_VALUE, model.getNet());
//                contentValues.put(DataContract.GoodsReceiveDetails.COL_UNIT1, model.getUnit1());
//                contentValues.put(DataContract.GoodsReceiveDetails.COL_UNIT2, model.getUnit2());
//                contentValues.put(DataContract.GoodsReceiveDetails.COL_UNIT3, model.getUnit3());
//                contentValues.put(DataContract.GoodsReceiveDetails.COL_UN_QTY1, model.getUnit1Qty());
//                contentValues.put(DataContract.GoodsReceiveDetails.COL_UN_QTY2, model.getUnit2Qty());
//                contentValues.put(DataContract.GoodsReceiveDetails.COL_UN_QTY3, model.getUnit3Qty());
//                contentValues.put(DataContract.GoodsReceiveDetails.COL_IS_SYNC, status);
//                database.update(DataContract.GoodsReceive.TABLE_NAME, contentValues, DataContract.GoodsReceive.COL_DOCUMENT_NUMBER + "=?", new String[]{String.valueOf(docNo)});
//                database.close();
//                Log.d(TAG, "one row update in GoodsReceiveDetails table ......... ");
//
//            }
//            result = true;
//
//        } catch (SQLException e) {
//            result = false;
//        }
//
//        return result;
//    }


//    public int getGoodsLastDocNo() {
//        SQLiteDatabase database = getReadableDatabase();
//        String[] projection = {DataContract.GoodsReceive.COL_DOCUMENT_NUMBER};
//        Cursor cursor = database.query(DataContract.GoodsReceive.TABLE_NAME, projection, null, null, null, null, DataContract.Stocks.COL_DOCUMENT_NUMBER + " DESC ", "1");
//
//
//        //Cursor cursor=database.query(DataContract.Invoice.TABLE_NAME,projections,null,null,null,null,DataContract.Invoice.COL_INVOICE_ID +" DESC ","1");
//        int id = 0;
//        if (cursor.moveToFirst()) {
//            id = cursor.getInt(cursor.getColumnIndex(DataContract.GoodsReceive.COL_DOCUMENT_NUMBER));
//
//        }
//        cursor.close();
//        database.close();
//        return id;
//
//    }

    public void getGoodsDetailsByDoc(int doc_no) {
        SQLiteDatabase database=getReadableDatabase();
        Cursor cursor = database.rawQuery("SELECT * FROM " + DataContract.GoodsReceiveDetails.TABLE_NAME + " WHERE " +
                        DataContract.GoodsReceiveDetails.COL_DOCUMENT_NUMBER + "= ? AND " + DataContract.GoodsReceiveDetails.COL_IS_SYNC + " = ? ",
                new String[]{String.valueOf(doc_no), String.valueOf(DataContract.SYNC_STATUS_FAILED)});

        if (cursor.moveToFirst()) {

            do {

                ItemModel itemModel = new ItemModel();
                itemModel.setDocNo(cursor.getInt(cursor.getColumnIndex(DataContract.GoodsReceiveDetails.COL_DOCUMENT_NUMBER)));
                itemModel.setBarCode(cursor.getString(cursor.getColumnIndex(DataContract.GoodsReceiveDetails.COL_BAR_CODE)));
                itemModel.setProductCode(cursor.getString(cursor.getColumnIndex(DataContract.GoodsReceiveDetails.COL_PRODUCT_CODE)));
                itemModel.setProductName(cursor.getString(cursor.getColumnIndex(DataContract.GoodsReceiveDetails.COL_PRODUCT_NAME)));
                itemModel.setQty(cursor.getDouble(cursor.getColumnIndex(DataContract.GoodsReceiveDetails.COL_QUANTITY)));
                itemModel.setFreeQty(cursor.getDouble(cursor.getColumnIndex(DataContract.GoodsReceiveDetails.COL_FREE_QUANTITY)));
                itemModel.setSelectedPackage(cursor.getString(cursor.getColumnIndex(DataContract.GoodsReceiveDetails.COL_UNIT)));
                itemModel.setSelectedFreePackage(cursor.getString(cursor.getColumnIndex(DataContract.GoodsReceiveDetails.COL_FREE_UNIT)));
                itemModel.setRate(cursor.getDouble(cursor.getColumnIndex(DataContract.GoodsReceiveDetails.COL_RATE)));
                itemModel.setDiscount(cursor.getDouble(cursor.getColumnIndex(DataContract.GoodsReceiveDetails.COL_DISCOUNT)));
                itemModel.setCostPrice(cursor.getDouble(cursor.getColumnIndex(DataContract.GoodsReceiveDetails.COL_COST_PRICE)));
                itemModel.setSalePrice(cursor.getDouble(cursor.getColumnIndex(DataContract.GoodsReceiveDetails.COL_SALES_PRICE)));
                itemModel.setStock(cursor.getString(cursor.getColumnIndex(DataContract.GoodsReceiveDetails.COL_STOCK)));
                itemModel.setUnit1(cursor.getString(cursor.getColumnIndex(DataContract.GoodsReceiveDetails.COL_UNIT1)));
                itemModel.setUnit2(cursor.getString(cursor.getColumnIndex(DataContract.GoodsReceiveDetails.COL_UNIT2)));
                itemModel.setUnit3(cursor.getString(cursor.getColumnIndex(DataContract.GoodsReceiveDetails.COL_UNIT3)));
                itemModel.setUnit1Qty(cursor.getInt(cursor.getColumnIndex(DataContract.GoodsReceiveDetails.COL_UN_QTY1)));
                itemModel.setUnit2Qty(cursor.getInt(cursor.getColumnIndex(DataContract.GoodsReceiveDetails.COL_UN_QTY2)));
                itemModel.setUnit3Qty(cursor.getInt(cursor.getColumnIndex(DataContract.GoodsReceiveDetails.COL_UN_QTY3)));
                itemModel.setNet(cursor.getDouble(cursor.getColumnIndex(DataContract.GoodsReceiveDetails.COL_NET_VALUE)));
                Cart.gCart.add(itemModel);

            } while (cursor.moveToNext());
        }
    }

    public boolean checkGoods(int doc_no) {
        SQLiteDatabase database=getReadableDatabase();

        Cursor cursor = database.rawQuery("SELECT * FROM " + DataContract.GoodsReceive.TABLE_NAME + " WHERE " +
                        DataContract.GoodsReceive.COL_DOCUMENT_NUMBER + "= ? ",
                new String[]{String.valueOf(doc_no)});
        boolean res=cursor.moveToFirst();
        cursor.close();
        database.close();
        return res;
    }

    public List<ItemModel> getGoods() {
        List<ItemModel>list=new ArrayList<>();
        SQLiteDatabase database=getReadableDatabase();
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
                DataContract.GoodsReceive.COL_SERVER_INVOICE_NUMBER,
                DataContract.GoodsReceive.COL_PAYMENT_TYPE,
                DataContract.GoodsReceive.COL_DATE_TIME,
                DataContract.GoodsReceive.COL_IS_SYNC


        };
        String orderBy = DataContract.GoodsReceive.COL_ID + " DESC ";
        Cursor cursor = database.query(DataContract.GoodsReceive.TABLE_NAME, projection, null, null, null, null, orderBy);
        if (cursor.moveToFirst()) {
            do {
                ItemModel model = new ItemModel();
                model.setDocNo(cursor.getInt(cursor.getColumnIndex(DataContract.GoodsReceive.COL_DOCUMENT_NUMBER)));
                model.setOrderNo(cursor.getString(cursor.getColumnIndex(DataContract.GoodsReceive.COL_ORDER_NUMBER)));
                model.setSupplierCode(cursor.getString(cursor.getColumnIndex(DataContract.GoodsReceive.COL_SUPPLIER_CODE)));
                model.setSupplierName(cursor.getString(cursor.getColumnIndex(DataContract.GoodsReceive.COL_SUPPLIER_NAME)));
                model.setInvoiceNo(cursor.getString(cursor.getColumnIndex(DataContract.GoodsReceive.COL_INVOICE_NUMBER)));
                model.setInvoiceDate(cursor.getString(cursor.getColumnIndex(DataContract.GoodsReceive.COL_INVOICE_DATE)));
                model.setStaffName(cursor.getString(cursor.getColumnIndex(DataContract.GoodsReceive.COL_STAFF_NAME)));
                model.setTotal(cursor.getDouble(cursor.getColumnIndex(DataContract.GoodsReceive.COL_TOTAL)));
                model.setDiscount(cursor.getDouble(cursor.getColumnIndex(DataContract.GoodsReceive.COL_DISCOUNT_AMOUNT)));
                model.setNet(cursor.getDouble(cursor.getColumnIndex(DataContract.GoodsReceive.COL_NET_AMOUNT)));
                model.setServerInvoice(cursor.getString(cursor.getColumnIndex(DataContract.GoodsReceive.COL_SERVER_INVOICE_NUMBER)));
                model.setDate(cursor.getString(cursor.getColumnIndex(DataContract.GoodsReceive.COL_DATE_TIME)));
                model.setPaymentType(cursor.getString(cursor.getColumnIndex(DataContract.GoodsReceive.COL_PAYMENT_TYPE)));
                model.setIs_sync(cursor.getInt(cursor.getColumnIndex(DataContract.GoodsReceive.COL_IS_SYNC)));
                list.add(model);
            } while (cursor.moveToNext());
        }
        cursor.close();
        database.close();
        return list;
    }


    public boolean deleteGoodsById(int _id) {
        SQLiteDatabase database = getReadableDatabase();
        boolean res=database.delete(DataContract.GoodsReceive.TABLE_NAME, DataContract.GoodsReceive.COL_ID + "= ?", new String[]{String.valueOf(_id)}) > 0;
        database.close();
        return res;
    }

    public boolean deleteGoodsDetailsByDoc(int docNo) {
        SQLiteDatabase database = getReadableDatabase();
        boolean res=database.delete(DataContract.GoodsReceiveDetails.TABLE_NAME, DataContract.GoodsReceiveDetails.COL_DOCUMENT_NUMBER + "= ?", new String[]{String.valueOf(docNo)}) > 0;
        database.close();
        return res;
    }

    public boolean deleteGoodsDetailsById(int _id) {
        SQLiteDatabase database = getReadableDatabase();
        boolean res = database.delete(DataContract.GoodsReceiveDetails.TABLE_NAME, DataContract.GoodsReceiveDetails.COL_ID + "= ?", new String[]{String.valueOf(_id)}) > 0;
        database.close();
        return res;
    }


    public void updateGoodsSync(int doc) {
        try {
            SQLiteDatabase database = getWritableDatabase();
            ContentValues values = new ContentValues();
            values.put(DataContract.GoodsReceive.COL_IS_SYNC, DataContract.SYNC_STATUS_OK);
            database.update(DataContract.GoodsReceive.TABLE_NAME, values, DataContract.GoodsReceive.COL_DOCUMENT_NUMBER + "=?", new String[]{String.valueOf(doc)});
            database.close();

            Log.v(TAG, "invoice sync updated");
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }


    /******************************* end Goods table***********************************/

    /******************************* invoice table***********************************/

    public long saveInvoice(String invoiceNo, String invoiceDate, String salesmanId, String customerCode, String customerName,
                            double total, double discount, double dis_per, double net, double other_amount, double gTotal,
                            String paymentMode, String date, String serverInvoice, int status) {

        SQLiteDatabase database = getWritableDatabase();
        ContentValues contentValues = new ContentValues();

        contentValues.put(DataContract.Invoice.COL_INVOICE_NUMBER, invoiceNo);
        contentValues.put(DataContract.Invoice.COL_INVOICE_DATE, invoiceDate);
        contentValues.put(DataContract.Invoice.COL_SALESMAN_ID, salesmanId);
        contentValues.put(DataContract.Invoice.COL_CUSTOMER_CODE, customerCode);
        contentValues.put(DataContract.Invoice.COL_CUSTOMER_NAME, customerName);
        contentValues.put(DataContract.Invoice.COL_TOTAL_AMOUNT, total);
        contentValues.put(DataContract.Invoice.COL_DISCOUNT_AMOUNT, discount);
        contentValues.put(DataContract.Invoice.COL_DISCOUNT_PERCENTAGE, dis_per);
        contentValues.put(DataContract.Invoice.COL_NET_AMOUNT, net);
        contentValues.put(DataContract.Invoice.COL_OTHER_AMOUNT, other_amount);
        contentValues.put(DataContract.Invoice.COL_GRAND_TOTAL_AMOUNT, gTotal);
        contentValues.put(DataContract.Invoice.COL_PAYMENT_TYPE, paymentMode);
        contentValues.put(DataContract.Invoice.COL_DATE_TIME, date);
        contentValues.put(DataContract.Invoice.COL_SERVER_INVOICE_NUMBER, serverInvoice);

        contentValues.put(DataContract.Invoice.COL_IS_SYNC, status);
        long id = database.insert(DataContract.Invoice.TABLE_NAME, null, contentValues);
        database.close();
        Log.d(TAG, "one row inserted in sales table ......... ");
        return id;


    }
//insert one by one

    public long saveInvoiceDetails(String invoiceNo, String barCode, String pCode, String pName, String pNameAr,
                                   double qty, String unit, String unit1, String unit2, String unit3, int qty1,
                                   int qty2, int qty3, double rate, double discount, double disPerc, double netAmt,
                                   String saleType, int status) {

        SQLiteDatabase database = getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(DataContract.InvoiceDetails.COL_INVOICE_NUMBER, invoiceNo);
        contentValues.put(DataContract.InvoiceDetails.COL_BAR_CODE, barCode);
        contentValues.put(DataContract.InvoiceDetails.COL_PRODUCT_CODE, pCode);
        contentValues.put(DataContract.InvoiceDetails.COL_PRODUCT_NAME, pName);
        contentValues.put(DataContract.InvoiceDetails.COL_PRODUCT_NAME_AR, pNameAr);
        contentValues.put(DataContract.InvoiceDetails.COL_QUANTITY, qty);
        contentValues.put(DataContract.InvoiceDetails.COL_UNIT, unit);
        contentValues.put(DataContract.InvoiceDetails.COL_UNIT1, unit1);
        contentValues.put(DataContract.InvoiceDetails.COL_UNIT2, unit2);
        contentValues.put(DataContract.InvoiceDetails.COL_UNIT3, unit3);
        contentValues.put(DataContract.InvoiceDetails.COL_UN_QTY1, qty1);
        contentValues.put(DataContract.InvoiceDetails.COL_UN_QTY2, qty2);
        contentValues.put(DataContract.InvoiceDetails.COL_UN_QTY3, qty3);
        contentValues.put(DataContract.InvoiceDetails.COL_RATE, rate);
        contentValues.put(DataContract.InvoiceDetails.COL_DISCOUNT, discount);
        contentValues.put(DataContract.InvoiceDetails.COL_DISCOUNT_PERCENTAGE, disPerc);
        contentValues.put(DataContract.InvoiceDetails.COL_NET_AMOUNT, netAmt);
        contentValues.put(DataContract.InvoiceDetails.COL_SALE_TYPE, saleType);
        contentValues.put(DataContract.InvoiceDetails.COL_IS_SYNC, status);
        long id = database.insert(DataContract.InvoiceDetails.TABLE_NAME, null, contentValues);
        Log.d(TAG, "one row inserted in invoiceDetails table ......... ");
        database.close();
        return id;
    }
// insert using cart
//    public boolean saveInvoiceDetails(String invoiceNo, int status) {
//
//        boolean result = false;
//        SQLiteDatabase database = getWritableDatabase();
//        ContentValues contentValues = new ContentValues();
//
//
//        try {
//            for (CartModel model : Cart.mCart) {
//                contentValues.put(DataContract.InvoiceDetails.COL_INVOICE_NUMBER, invoiceNo);
//                contentValues.put(DataContract.InvoiceDetails.COL_BAR_CODE, model.getBarcode());
//                contentValues.put(DataContract.InvoiceDetails.COL_PRODUCT_CODE, model.getProductCode());
//                contentValues.put(DataContract.InvoiceDetails.COL_PRODUCT_NAME, model.getProductName());
//                contentValues.put(DataContract.InvoiceDetails.COL_QUANTITY, model.getQty());
//                contentValues.put(DataContract.InvoiceDetails.COL_UNIT, model.getSelectedUnit());
//                contentValues.put(DataContract.InvoiceDetails.COL_UNIT1, model.getUnit1());
//                contentValues.put(DataContract.InvoiceDetails.COL_UNIT2, model.getUnit2());
//                contentValues.put(DataContract.InvoiceDetails.COL_UNIT3, model.getUnit3());
//                contentValues.put(DataContract.InvoiceDetails.COL_UN_QTY1, model.getUnit1Qty());
//                contentValues.put(DataContract.InvoiceDetails.COL_UN_QTY2, model.getUnit2Qty());
//                contentValues.put(DataContract.InvoiceDetails.COL_UN_QTY3, model.getUnit3Qty());
//                contentValues.put(DataContract.InvoiceDetails.COL_RATE, model.getRate());
//                contentValues.put(DataContract.InvoiceDetails.COL_DISCOUNT, model.getDiscount());
//                contentValues.put(DataContract.InvoiceDetails.COL_NET_AMOUNT, model.getNet());
//                contentValues.put(DataContract.InvoiceDetails.COL_NET_AMOUNT, model.getNet());
//                contentValues.put(DataContract.InvoiceDetails.COL_SALE_TYPE, model.getSaleType());
//                contentValues.put(DataContract.InvoiceDetails.COL_IS_SYNC, status);
//                database.insertOrThrow(DataContract.InvoiceDetails.TABLE_NAME, null, contentValues);
//                Log.d(TAG, "one row inserted in invoiceDetails table ......... ");
//                result = true;
//            }
//
//
//        } catch (SQLException e) {
//            result = false;
//        }
//
//        return result;
//    }

    public boolean checkInvoice( String invoice_no) {
        SQLiteDatabase database=getReadableDatabase();
//        Cursor cursor = database.rawQuery("SELECT * FROM " + DataContract.Invoice.TABLE_NAME + " WHERE " +
//                        DataContract.Invoice.COL_INVOICE_NUMBER + "= ? ",
//                new String[]{String.valueOf(invoice_no)});

        String[] projection = {
                DataContract.Invoice.COL_INVOICE_NUMBER,
        };
        String selection = DataContract.Invoice.COL_INVOICE_NUMBER + " LIKE ?";
        String[] selection_ars = {invoice_no};
        Cursor cursor = database.query(DataContract.Invoice.TABLE_NAME, projection, selection, selection_ars, null, null, null);
        boolean res= cursor.moveToNext();
        cursor.close();
        database.close();
        return res;


    }

    public long updateInvoice(String invoiceNo, String invoiceDate, String salesmanId, String customerCode, String customerName,
                                 double total, double discount, double dis_per, double net, double other_amount, double gTotal,
                                 String paymentMode, String date, String serverInvoice,int status) {

        SQLiteDatabase database = getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        long id=0;
        try {

            contentValues.put(DataContract.Invoice.COL_INVOICE_NUMBER, invoiceNo);
            contentValues.put(DataContract.Invoice.COL_INVOICE_DATE, invoiceDate);
            contentValues.put(DataContract.Invoice.COL_SALESMAN_ID, salesmanId);
            contentValues.put(DataContract.Invoice.COL_CUSTOMER_CODE, customerCode);
            contentValues.put(DataContract.Invoice.COL_CUSTOMER_NAME, customerName);
            contentValues.put(DataContract.Invoice.COL_TOTAL_AMOUNT, total);
            contentValues.put(DataContract.Invoice.COL_DISCOUNT_AMOUNT, discount);
            contentValues.put(DataContract.Invoice.COL_DISCOUNT_PERCENTAGE, dis_per);
            contentValues.put(DataContract.Invoice.COL_NET_AMOUNT, net);
            contentValues.put(DataContract.Invoice.COL_OTHER_AMOUNT, other_amount);
            contentValues.put(DataContract.Invoice.COL_GRAND_TOTAL_AMOUNT, gTotal);
            contentValues.put(DataContract.Invoice.COL_PAYMENT_TYPE, paymentMode);
            contentValues.put(DataContract.Invoice.COL_DATE_TIME, date);
            contentValues.put(DataContract.Invoice.COL_SERVER_INVOICE_NUMBER, serverInvoice);

            contentValues.put(DataContract.Invoice.COL_IS_SYNC, status);
            id=database.update(DataContract.Invoice.TABLE_NAME, contentValues, DataContract.Invoice.COL_INVOICE_NUMBER + "=?", new String[]{invoiceNo});
            database.close();
            Log.d(TAG, "one row updated in invoice table ......... ");
            return id;
        } catch (Exception e) {
            return id;
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
                contentValues.put(DataContract.InvoiceDetails.COL_SALE_TYPE, model.getSaleType());
                contentValues.put(DataContract.InvoiceDetails.COL_IS_SYNC, status);
                database.update(DataContract.InvoiceDetails.TABLE_NAME, contentValues, DataContract.InvoiceDetails.COL_INVOICE_NUMBER + "=?", new String[]{invoiceNo});
                database.close();
                Log.d(TAG, "one row updated in invoiceDetails table ......... ");
                result = true;
            }


        } catch (SQLException e) {
            result = false;
        }

        return result;
    }

    public List<ItemModel>getAllInvoice() {
        SQLiteDatabase database=getReadableDatabase();
        List<ItemModel>saleList=new ArrayList<>();
        Log.d(TAG, "get all Invoice: ");
        String[] projection = {
                DataContract.Invoice.COL_INVOICE_NUMBER,
                DataContract.Invoice.COL_INVOICE_DATE,
                DataContract.Invoice.COL_SALESMAN_ID,
                DataContract.Invoice.COL_CUSTOMER_CODE,
                DataContract.Invoice.COL_CUSTOMER_NAME,
                DataContract.Invoice.COL_TOTAL_AMOUNT,
                DataContract.Invoice.COL_DISCOUNT_AMOUNT,
                DataContract.Invoice.COL_NET_AMOUNT,
                DataContract.Invoice.COL_OTHER_AMOUNT,
                DataContract.Invoice.COL_GRAND_TOTAL_AMOUNT,
                DataContract.Invoice.COL_SERVER_INVOICE_NUMBER,
                DataContract.Invoice.COL_PAYMENT_TYPE,
                DataContract.Invoice.COL_IS_SYNC,
        };
        String orderBy = DataContract.Invoice.COL_ID + " DESC ";
        Cursor cursor = database.query(DataContract.Invoice.TABLE_NAME, projection, null, null, null, null, orderBy);
        if (cursor.moveToFirst()) {
            do {
                ItemModel model = new ItemModel();
                model.setInvoiceNo(cursor.getString(cursor.getColumnIndex(DataContract.Invoice.COL_INVOICE_NUMBER)));
                model.setInvoiceDate(cursor.getString(cursor.getColumnIndex(DataContract.Invoice.COL_INVOICE_DATE)));
                Log.d(TAG, "populateRecycler: Date " + cursor.getString(cursor.getColumnIndex(DataContract.Invoice.COL_INVOICE_DATE)));
                model.setStaffName(cursor.getString(cursor.getColumnIndex(DataContract.Invoice.COL_SALESMAN_ID)));
                model.setCustomerCode(cursor.getString(cursor.getColumnIndex(DataContract.Invoice.COL_CUSTOMER_CODE)));
                model.setCustomerName(cursor.getString(cursor.getColumnIndex(DataContract.Invoice.COL_CUSTOMER_NAME)));
                model.setTotal(cursor.getDouble(cursor.getColumnIndex(DataContract.Invoice.COL_TOTAL_AMOUNT)));
                model.setDiscount(cursor.getDouble(cursor.getColumnIndex(DataContract.Invoice.COL_DISCOUNT_AMOUNT)));
                model.setNet(cursor.getDouble(cursor.getColumnIndex(DataContract.Invoice.COL_NET_AMOUNT)));
                model.setOtherAmount(cursor.getDouble(cursor.getColumnIndex(DataContract.Invoice.COL_OTHER_AMOUNT)));
                model.setGrandTotal(cursor.getDouble(cursor.getColumnIndex(DataContract.Invoice.COL_GRAND_TOTAL_AMOUNT)));
                model.setServerInvoice(cursor.getString(cursor.getColumnIndex(DataContract.Invoice.COL_SERVER_INVOICE_NUMBER)));
                model.setPaymentType(cursor.getString(cursor.getColumnIndex(DataContract.Invoice.COL_PAYMENT_TYPE)));
                model.setIs_sync(cursor.getInt(cursor.getColumnIndex(DataContract.Invoice.COL_IS_SYNC)));
                saleList.add(model);
            } while (cursor.moveToNext());
        }
        cursor.close();
        database.close();

        return saleList;
    }


    public Cursor getAllInvoiceByDate(String from, String to) {
        SQLiteDatabase database = this.getReadableDatabase();

        Cursor c = database.rawQuery("select invoice_number,customer_name,net_amount,payment_type,invoice_date from " + DataContract.Invoice.TABLE_NAME
                + " where invoice_date BETWEEN '" + from + "' AND '" + to + "' ", null);
        database.close();
        return c;
    }

    public  List<ItemModel> getAllUnSyncInvoice() {
        List<ItemModel>saleList=new ArrayList<>();
        SQLiteDatabase database=getReadableDatabase();
        Log.d(TAG, "get Un sync Invoice : ");
        String[] projection = {
                DataContract.Invoice.COL_INVOICE_NUMBER,
                DataContract.Invoice.COL_INVOICE_DATE,
                DataContract.Invoice.COL_CUSTOMER_CODE,
                DataContract.Invoice.COL_CUSTOMER_NAME,
                DataContract.Invoice.COL_SALESMAN_ID,
                DataContract.Invoice.COL_TOTAL_AMOUNT,
                DataContract.Invoice.COL_DISCOUNT_AMOUNT,
                DataContract.Invoice.COL_DISCOUNT_PERCENTAGE,
                DataContract.Invoice.COL_NET_AMOUNT,
                DataContract.Invoice.COL_OTHER_AMOUNT,
                DataContract.Invoice.COL_PAYMENT_TYPE,

        };
        String selection = DataContract.Invoice.COL_IS_SYNC + " LIKE ?";
        String[] selection_ars = {String.valueOf(DataContract.SYNC_STATUS_FAILED)};
        //Cursor cursor = database.rawQuery("select"+ DataContract.Invoice.COL_INVOICE_NUMBER+ "from " + DataContract.Invoice.TABLE_NAME + " where " + DataContract.Invoice.COL_IS_SYNC + "="+DataContract.SYNC_STATUS_FAILED , null);
        Cursor cursor = database.query(DataContract.Invoice.TABLE_NAME, projection, selection, selection_ars, null, null, null);
        if (cursor.moveToFirst()) {
            do {
                ItemModel model = new ItemModel();
                model.setInvoiceNo(cursor.getString(cursor.getColumnIndex(DataContract.Invoice.COL_INVOICE_NUMBER)));
                model.setInvoiceDate(cursor.getString(cursor.getColumnIndex(DataContract.Invoice.COL_INVOICE_DATE)));
                model.setStaffName(cursor.getString(cursor.getColumnIndex(DataContract.Invoice.COL_SALESMAN_ID)));
                model.setCustomerCode(cursor.getString(cursor.getColumnIndex(DataContract.Invoice.COL_CUSTOMER_CODE)));
                model.setCustomerName(cursor.getString(cursor.getColumnIndex(DataContract.Invoice.COL_CUSTOMER_NAME)));
                model.setTotal(cursor.getDouble(cursor.getColumnIndex(DataContract.Invoice.COL_TOTAL_AMOUNT)));
                model.setDiscount(cursor.getDouble(cursor.getColumnIndex(DataContract.Invoice.COL_DISCOUNT_AMOUNT)));
                model.setDiscountPercentage(cursor.getDouble(cursor.getColumnIndex(DataContract.Invoice.COL_DISCOUNT_PERCENTAGE)));
                model.setNet(cursor.getDouble(cursor.getColumnIndex(DataContract.Invoice.COL_NET_AMOUNT)));
                model.setOtherAmount(cursor.getDouble(cursor.getColumnIndex(DataContract.Invoice.COL_OTHER_AMOUNT)));
                model.setPaymentType(cursor.getString(cursor.getColumnIndex(DataContract.Invoice.COL_PAYMENT_TYPE)));
                saleList.add(model);
            } while (cursor.moveToNext());
        }
        cursor.close();
        database.close();
        return saleList;
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
                DataContract.InvoiceDetails.COL_UNIT1,
                DataContract.InvoiceDetails.COL_UNIT2,
                DataContract.InvoiceDetails.COL_UNIT3,
                DataContract.InvoiceDetails.COL_UN_QTY1,
                DataContract.InvoiceDetails.COL_UN_QTY2,
                DataContract.InvoiceDetails.COL_UN_QTY3,
                DataContract.InvoiceDetails.COL_RATE,
                DataContract.InvoiceDetails.COL_DISCOUNT,
                DataContract.InvoiceDetails.COL_NET_AMOUNT


        };
        String selection = DataContract.InvoiceDetails.COL_INVOICE_NUMBER + " LIKE ?";
        String[] selection_ars = {invoiceId};
        Cursor cursor = database.query(DataContract.InvoiceDetails.TABLE_NAME, projection, selection, selection_ars, null, null, null);
        //Log.d(TAG, "Refund details cursor size" + cursor.getCount());
        if (cursor.moveToFirst()) {
            do {

                CartModel cartModel = new CartModel();

                cartModel.setBarcode(cursor.getString(cursor.getColumnIndex(DataContract.InvoiceDetails.COL_BAR_CODE)));
                cartModel.setProductCode(cursor.getString(cursor.getColumnIndex(DataContract.InvoiceDetails.COL_PRODUCT_CODE)));
                cartModel.setProductName(cursor.getString(cursor.getColumnIndex(DataContract.InvoiceDetails.COL_PRODUCT_NAME)));
                cartModel.setQty(cursor.getDouble(cursor.getColumnIndex(DataContract.InvoiceDetails.COL_QUANTITY)));
                cartModel.setSelectedUnit(cursor.getString(cursor.getColumnIndex(DataContract.InvoiceDetails.COL_UNIT)));
                cartModel.setUnit1(cursor.getString(cursor.getColumnIndex(DataContract.InvoiceDetails.COL_UNIT1)));
                cartModel.setUnit2(cursor.getString(cursor.getColumnIndex(DataContract.InvoiceDetails.COL_UNIT2)));
                cartModel.setUnit3(cursor.getString(cursor.getColumnIndex(DataContract.InvoiceDetails.COL_UNIT3)));
                cartModel.setUnit1Qty(cursor.getInt(cursor.getColumnIndex(DataContract.InvoiceDetails.COL_UN_QTY1)));
                cartModel.setUnit2Qty(cursor.getInt(cursor.getColumnIndex(DataContract.InvoiceDetails.COL_UN_QTY2)));
                cartModel.setUnit3Qty(cursor.getInt(cursor.getColumnIndex(DataContract.InvoiceDetails.COL_UN_QTY3)));
                cartModel.setRate(cursor.getDouble(cursor.getColumnIndex(DataContract.InvoiceDetails.COL_RATE)));
                cartModel.setDiscount(cursor.getDouble(cursor.getColumnIndex(DataContract.InvoiceDetails.COL_DISCOUNT)));
                cartModel.setNet(cursor.getDouble(cursor.getColumnIndex(DataContract.InvoiceDetails.COL_NET_AMOUNT)));
                cartModel.setSaleType(cursor.getString(cursor.getColumnIndex(DataContract.InvoiceDetails.COL_SALE_TYPE)));
                Cart.mCart.add(cartModel);
            } while (cursor.moveToNext());

        }
        cursor.close();
        database.close();
    }


    public long updateAllInvoiceSync(int status,String serverNo) {
        long id=0;
        try {
            SQLiteDatabase database = getWritableDatabase();
            ContentValues values = new ContentValues();
            values.put(DataContract.Invoice.COL_IS_SYNC, status);
            values.put(DataContract.Invoice.COL_SERVER_INVOICE_NUMBER,serverNo);
            id=database.update(DataContract.Invoice.TABLE_NAME, values, DataContract.Invoice.COL_IS_SYNC + "=?", new String[]{String.valueOf(DataContract.SYNC_STATUS_FAILED)});
            database.close();
            Log.v(TAG, "invoice sync updated");
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return id;
    }


//    public int getLastInvoiceID() {
//        SQLiteDatabase database = getReadableDatabase();
//        Cursor cursor = database.rawQuery("SELECT MAX("+DataContract.Invoice.COL_ID+") FROM "+DataContract.Invoice.TABLE_NAME,
//                null);
//        int maxid = (cursor.moveToFirst() ? cursor.getInt(0) : 0);
//        return maxid;
//
//    }

    public boolean deleteInvoiceByInvoiceNo(String invoice_no) {
        SQLiteDatabase database = getReadableDatabase();
        boolean res = database.delete(DataContract.Invoice.TABLE_NAME, DataContract.Invoice.COL_INVOICE_NUMBER + "= ?", new String[]{invoice_no}) > 0;
        database.close();
        return res;
    }

    public boolean deleteInvoiceDetailsById(int _id) {
        SQLiteDatabase database = getReadableDatabase();
        boolean res = database.delete(DataContract.InvoiceDetails.TABLE_NAME, DataContract.InvoiceDetails.COL_ID + "= ?", new String[]{String.valueOf(_id)}) > 0;
        database.close();
        return res;
    }

    public boolean deleteInvoiceDetailsByInvoiceNo(String invoice_no) {
        SQLiteDatabase database = getReadableDatabase();
        boolean res = database.delete(DataContract.InvoiceDetails.TABLE_NAME, DataContract.InvoiceDetails.COL_INVOICE_NUMBER + "= ?", new String[]{invoice_no}) > 0;
        database.close();
        return res;
    }

    public void deleteAllInvoice() {
        SQLiteDatabase database = getReadableDatabase();
        database.execSQL("delete from " + DataContract.Invoice.TABLE_NAME);
        database.close();
    }

    public void deleteAllInvoiceDetails() {
        SQLiteDatabase database = getReadableDatabase();
        database.execSQL("delete from " + DataContract.InvoiceDetails.TABLE_NAME);
        database.close();
    }
    public long updateOrder(String invoiceNo, String invoiceDate, String salesmanId, String customerCode, String customerName,
                              double total, double discount, double dis_per, double net, double other_amount, double gTotal,
                              String paymentMode, String date, String serverInvoice,int status) {

        SQLiteDatabase database = getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        long id=0;
        try {

            contentValues.put(DataContract.Order.COL_INVOICE_NUMBER, invoiceNo);
            contentValues.put(DataContract.Order.COL_INVOICE_DATE, invoiceDate);
            contentValues.put(DataContract.Order.COL_SALESMAN_ID, salesmanId);
            contentValues.put(DataContract.Order.COL_CUSTOMER_CODE, customerCode);
            contentValues.put(DataContract.Order.COL_CUSTOMER_NAME, customerName);
            contentValues.put(DataContract.Order.COL_TOTAL_AMOUNT, total);
            contentValues.put(DataContract.Order.COL_DISCOUNT_AMOUNT, discount);
            contentValues.put(DataContract.Order.COL_DISCOUNT_PERCENTAGE, dis_per);
            contentValues.put(DataContract.Order.COL_NET_AMOUNT, net);
            contentValues.put(DataContract.Order.COL_OTHER_AMOUNT, other_amount);
            contentValues.put(DataContract.Order.COL_GRAND_TOTAL_AMOUNT, gTotal);
            contentValues.put(DataContract.Order.COL_PAYMENT_TYPE, paymentMode);
            contentValues.put(DataContract.Order.COL_DATE_TIME, date);
            contentValues.put(DataContract.Order.COL_SERVER_INVOICE_NUMBER, serverInvoice);

            contentValues.put(DataContract.Order.COL_IS_SYNC, status);
            id=database.update(DataContract.Order.TABLE_NAME, contentValues, DataContract.Order.COL_INVOICE_NUMBER + "=?", new String[]{invoiceNo});
            database.close();
            Log.d(TAG, "one row updated in Order table ......... ");
            return id;
        } catch (Exception e) {
            return id;
        }


    }


    /************************ end invoice*******************************************************/

    /************************ Start order*******************************************************/
    public boolean checkOrder(String invoice_no) {
        SQLiteDatabase database=getReadableDatabase();
        String[] projection = {
                DataContract.Order.COL_INVOICE_NUMBER,
        };
        String selection = DataContract.Order.COL_INVOICE_NUMBER + " LIKE ?";
        String[] selection_ars = {invoice_no};
        Cursor cursor = database.query(DataContract.Order.TABLE_NAME, projection, selection, selection_ars, null, null, null);
        boolean res= cursor.moveToNext();
        cursor.close();
        database.close();
        return res;
    }

    public long saveOrder(String invoiceNo, String invoiceDate, String salesmanId, String customerCode, String customerName,
                            double total, double discount, double dis_per, double net, double other_amount, double gTotal,
                            String paymentMode, String date, String serverInvoice, int status) {

        SQLiteDatabase database = getWritableDatabase();
        ContentValues contentValues = new ContentValues();

        contentValues.put(DataContract.Order.COL_INVOICE_NUMBER, invoiceNo);
        contentValues.put(DataContract.Order.COL_INVOICE_DATE, invoiceDate);
        contentValues.put(DataContract.Order.COL_SALESMAN_ID, salesmanId);
        contentValues.put(DataContract.Order.COL_CUSTOMER_CODE, customerCode);
        contentValues.put(DataContract.Order.COL_CUSTOMER_NAME, customerName);
        contentValues.put(DataContract.Order.COL_TOTAL_AMOUNT, total);
        contentValues.put(DataContract.Order.COL_DISCOUNT_AMOUNT, discount);
        contentValues.put(DataContract.Order.COL_DISCOUNT_PERCENTAGE, dis_per);
        contentValues.put(DataContract.Order.COL_NET_AMOUNT, net);
        contentValues.put(DataContract.Order.COL_OTHER_AMOUNT, other_amount);
        contentValues.put(DataContract.Order.COL_GRAND_TOTAL_AMOUNT, gTotal);
        contentValues.put(DataContract.Order.COL_PAYMENT_TYPE, paymentMode);
        contentValues.put(DataContract.Order.COL_DATE_TIME, date);
        contentValues.put(DataContract.Order.COL_SERVER_INVOICE_NUMBER, serverInvoice);

        contentValues.put(DataContract.Order.COL_IS_SYNC, status);
        long id = database.insert(DataContract.Order.TABLE_NAME, null, contentValues);
        database.close();
        Log.d(TAG, "one row inserted in Order table ......... ");
        return id;


    }

    public long saveOrderDetails(String invoiceNo, String barCode, String pCode, String pName, String pNameAr,
                                   double qty, String unit, String unit1, String unit2, String unit3, int qty1,
                                   int qty2, int qty3, double rate, double discount, double disPerc, double netAmt,
                                   String saleType, int status) {

        SQLiteDatabase database = getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(DataContract.OrderDetails.COL_INVOICE_NUMBER, invoiceNo);
        contentValues.put(DataContract.OrderDetails.COL_BAR_CODE, barCode);
        contentValues.put(DataContract.OrderDetails.COL_PRODUCT_CODE, pCode);
        contentValues.put(DataContract.OrderDetails.COL_PRODUCT_NAME, pName);
        contentValues.put(DataContract.OrderDetails.COL_PRODUCT_NAME_AR, pNameAr);
        contentValues.put(DataContract.OrderDetails.COL_QUANTITY, qty);
        contentValues.put(DataContract.OrderDetails.COL_UNIT, unit);
        contentValues.put(DataContract.OrderDetails.COL_UNIT1, unit1);
        contentValues.put(DataContract.OrderDetails.COL_UNIT2, unit2);
        contentValues.put(DataContract.OrderDetails.COL_UNIT3, unit3);
        contentValues.put(DataContract.OrderDetails.COL_UN_QTY1, qty1);
        contentValues.put(DataContract.OrderDetails.COL_UN_QTY2, qty2);
        contentValues.put(DataContract.OrderDetails.COL_UN_QTY3, qty3);
        contentValues.put(DataContract.OrderDetails.COL_RATE, rate);
        contentValues.put(DataContract.OrderDetails.COL_DISCOUNT, discount);
        contentValues.put(DataContract.OrderDetails.COL_DISCOUNT_PERCENTAGE, disPerc);
        contentValues.put(DataContract.OrderDetails.COL_NET_AMOUNT, netAmt);
        contentValues.put(DataContract.OrderDetails.COL_SALE_TYPE, saleType);
        contentValues.put(DataContract.OrderDetails.COL_IS_SYNC, status);

        long id = database.insert(DataContract.OrderDetails.TABLE_NAME, null, contentValues);
        Log.d(TAG, "one row inserted in OrderDetails table ......... ");
        database.close();
        return id;
    }


    public int getLastOrderID() {
        SQLiteDatabase database = getReadableDatabase();
        Cursor cursor = database.rawQuery("SELECT MAX(" + DataContract.Order.COL_ID + ") FROM  " + DataContract.Order.TABLE_NAME,
               null);


        int maxid = (cursor.moveToFirst() ? cursor.getInt(0) : 0);
        return maxid;

    }

    public List<ItemModel> getAllOrders( ) {
        List<ItemModel>orderList=new ArrayList<>();
        SQLiteDatabase database=getReadableDatabase();
        Log.d(TAG, "get all Orders: ");
        String[] projection = {
                DataContract.Order.COL_INVOICE_NUMBER,
                DataContract.Order.COL_INVOICE_DATE,
                DataContract.Order.COL_SALESMAN_ID,
                DataContract.Order.COL_CUSTOMER_CODE,
                DataContract.Order.COL_CUSTOMER_NAME,
                DataContract.Order.COL_TOTAL_AMOUNT,
                DataContract.Order.COL_DISCOUNT_AMOUNT,
                DataContract.Order.COL_NET_AMOUNT,
                DataContract.Order.COL_OTHER_AMOUNT,
                DataContract.Order.COL_GRAND_TOTAL_AMOUNT,
                DataContract.Order.COL_SERVER_INVOICE_NUMBER,
                DataContract.Order.COL_PAYMENT_TYPE,
                DataContract.Order.COL_IS_SYNC,
        };
        String orderBy = DataContract.Order.COL_ID + " DESC ";
        Cursor cursor = database.query(DataContract.Order.TABLE_NAME, projection, null, null, null, null, orderBy);
        if (cursor.moveToFirst()) {
            do {
                ItemModel model = new ItemModel();
                model.setInvoiceNo(cursor.getString(cursor.getColumnIndex(DataContract.Invoice.COL_INVOICE_NUMBER)));
                model.setInvoiceDate(cursor.getString(cursor.getColumnIndex(DataContract.Invoice.COL_INVOICE_DATE)));
                Log.d(TAG, "populateRecycler: Date " + cursor.getString(cursor.getColumnIndex(DataContract.Invoice.COL_INVOICE_DATE)));
                model.setStaffName(cursor.getString(cursor.getColumnIndex(DataContract.Invoice.COL_SALESMAN_ID)));
                model.setCustomerCode(cursor.getString(cursor.getColumnIndex(DataContract.Invoice.COL_CUSTOMER_CODE)));
                model.setCustomerName(cursor.getString(cursor.getColumnIndex(DataContract.Invoice.COL_CUSTOMER_NAME)));
                model.setTotal(cursor.getDouble(cursor.getColumnIndex(DataContract.Invoice.COL_TOTAL_AMOUNT)));
                model.setDiscount(cursor.getDouble(cursor.getColumnIndex(DataContract.Invoice.COL_DISCOUNT_AMOUNT)));
                model.setNet(cursor.getDouble(cursor.getColumnIndex(DataContract.Invoice.COL_NET_AMOUNT)));
                model.setOtherAmount(cursor.getDouble(cursor.getColumnIndex(DataContract.Invoice.COL_OTHER_AMOUNT)));
                model.setGrandTotal(cursor.getDouble(cursor.getColumnIndex(DataContract.Invoice.COL_GRAND_TOTAL_AMOUNT)));
                model.setServerInvoice(cursor.getString(cursor.getColumnIndex(DataContract.Invoice.COL_SERVER_INVOICE_NUMBER)));
                model.setPaymentType(cursor.getString(cursor.getColumnIndex(DataContract.Invoice.COL_PAYMENT_TYPE)));
                model.setIs_sync(cursor.getInt(cursor.getColumnIndex(DataContract.Invoice.COL_IS_SYNC)));
                orderList.add(model);
            } while (cursor.moveToNext());
        }
        cursor.close();
        database.close();



        return orderList;
    }

    public boolean deleteOrderByInvoiceNo(String invoice_no) {
        SQLiteDatabase database = getReadableDatabase();
        boolean res = database.delete(DataContract.Order.TABLE_NAME, DataContract.Order.COL_INVOICE_NUMBER + "= ?", new String[]{invoice_no}) > 0;
        database.close();
        return res;
    }

    public boolean deleteOrderDetailsById(int _id) {
        SQLiteDatabase database = getReadableDatabase();
        boolean res = database.delete(DataContract.OrderDetails.TABLE_NAME, DataContract.OrderDetails.COL_ID + "= ?", new String[]{String.valueOf(_id)}) > 0;
        database.close();
        return res;
    }

    public boolean deleteOrderDetailsByInvoiceNo(String invoice_no) {
        SQLiteDatabase database = getReadableDatabase();
        boolean res = database.delete(DataContract.OrderDetails.TABLE_NAME, DataContract.OrderDetails.COL_INVOICE_NUMBER + "= ?", new String[]{invoice_no}) > 0;
        database.close();
        return res;
    }

    public void deleteAllOrder() {
        SQLiteDatabase database = getReadableDatabase();
        database.execSQL(" delete from " + DataContract.Order.TABLE_NAME);
        database.close();
    }

    public void deleteAllOrderDetails() {
        SQLiteDatabase database = getReadableDatabase();
        database.execSQL("delete from " + DataContract.OrderDetails.TABLE_NAME);
        database.close();
    }




    /************************ end Order*******************************************************/

    /************************ Receipt **********************************************************/
    public boolean saveReceipts(String receiptNo, String receiptDate, String salesmanId, String customerCode, String customerName,
                                double balanceAmount, double receivedAmount, String paymentMode, String chqDate, String chqNumber, String remark, int status,String serverNo) {

        SQLiteDatabase database = getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        try {
            contentValues.put(DataContract.Receipts.COL_RECEIPT_NUMBER, receiptNo);
            contentValues.put(DataContract.Receipts.COL_RECEIPT_DATE, receiptDate);
            contentValues.put(DataContract.Receipts.COL_SALESMAN_ID, salesmanId);
            contentValues.put(DataContract.Receipts.COL_CUSTOMER_CODE, customerCode);
            contentValues.put(DataContract.Receipts.COL_CUSTOMER_NAME, customerName);
            contentValues.put(DataContract.Receipts.COL_BALANCE_AMOUNT, balanceAmount);
            contentValues.put(DataContract.Receipts.COL_RECEIVED_AMOUNT, receivedAmount);
            contentValues.put(DataContract.Receipts.COL_PAYMENT_TYPE, paymentMode);
            contentValues.put(DataContract.Receipts.COL_CHEQUE_DATE, chqDate);
            contentValues.put(DataContract.Receipts.COL_CHEQUE_NUMBER, chqNumber);
            contentValues.put(DataContract.Receipts.COL_REMARK, remark);
            contentValues.put(DataContract.Receipts.COL_IS_SYNC, status);
            contentValues.put(DataContract.Receipts.COL_SERVER_INVOICE_NUMBER, serverNo);
            database.insert(DataContract.Receipts.TABLE_NAME, null, contentValues);
            database.close();
            Log.d(TAG, "one row inserted in receipt table ......... ");
            return true;
        } catch (Exception e) {
            return false;
        }


    }

    public boolean updateReceipt(String receiptNo, String receiptDate, String salesmanId, String customerCode, String customerName,
                                 double balanceAmount, double receivedAmount, String paymentMode, String chqDate, String chqNumber, String remark, int status,String serverNo) {

        SQLiteDatabase database = getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        try {
            contentValues.put(DataContract.Receipts.COL_RECEIPT_NUMBER, receiptNo);
            contentValues.put(DataContract.Receipts.COL_RECEIPT_DATE, receiptDate);
            contentValues.put(DataContract.Receipts.COL_SALESMAN_ID, salesmanId);
            contentValues.put(DataContract.Receipts.COL_CUSTOMER_CODE, customerCode);
            contentValues.put(DataContract.Receipts.COL_CUSTOMER_NAME, customerName);
            contentValues.put(DataContract.Receipts.COL_BALANCE_AMOUNT, balanceAmount);
            contentValues.put(DataContract.Receipts.COL_RECEIVED_AMOUNT, receivedAmount);
            contentValues.put(DataContract.Receipts.COL_PAYMENT_TYPE, paymentMode);
            contentValues.put(DataContract.Receipts.COL_CHEQUE_DATE, chqDate);
            contentValues.put(DataContract.Receipts.COL_CHEQUE_NUMBER, chqNumber);
            contentValues.put(DataContract.Receipts.COL_REMARK, remark);
            contentValues.put(DataContract.Receipts.COL_IS_SYNC, status);
            contentValues.put(DataContract.Receipts.COL_SERVER_INVOICE_NUMBER, serverNo);

            database.update(DataContract.Receipts.TABLE_NAME, contentValues, DataContract.Receipts.COL_RECEIPT_NUMBER + "=?", new String[]{receiptNo});
            database.close();
            Log.d(TAG, "one row updated in receipt table ......... ");
            return true;
        } catch (Exception e) {
            return false;
        }


    }

    public void updateAllReceiptSync() {
        try {
            SQLiteDatabase database = getWritableDatabase();
            ContentValues values = new ContentValues();
            values.put(DataContract.Receipts.COL_IS_SYNC, DataContract.SYNC_STATUS_OK);
            database.update(DataContract.Invoice.TABLE_NAME, values, DataContract.Receipts.COL_IS_SYNC + "=?", new String[]{String.valueOf(DataContract.SYNC_STATUS_FAILED)});
            database.close();
            Log.v(TAG, "receipts sync updated");
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public int getLastReceiptNo() {
        SQLiteDatabase database = getReadableDatabase();
        String[] projection = {DataContract.Receipts.COL_ID};
        Cursor cursor = database.query(DataContract.Receipts.TABLE_NAME, projection, null, null, null, null, DataContract.Receipts.COL_ID + " DESC ", "1");


        //Cursor cursor=database.query(DataContract.Invoice.TABLE_NAME,projections,null,null,null,null,DataContract.Invoice.COL_INVOICE_ID +" DESC ","1");
        int id = 0;
        if (cursor.moveToFirst()) {
            id = cursor.getInt(cursor.getColumnIndex(DataContract.Receipts.COL_ID));

        }
        cursor.close();
        database.close();
        return id;

    }
    public List<ReceiptModel>  getAllReceipts() {
        Log.d(TAG, "get all receipts: ");
        List<ReceiptModel> list=new ArrayList<>();
        SQLiteDatabase database=getReadableDatabase();
        String[] projection = {
                DataContract.Receipts.COL_RECEIPT_NUMBER,
                DataContract.Receipts.COL_RECEIPT_DATE,

                DataContract.Receipts.COL_SALESMAN_ID,
                DataContract.Receipts.COL_CUSTOMER_CODE,
                DataContract.Receipts.COL_CUSTOMER_NAME,
                DataContract.Receipts.COL_RECEIVED_AMOUNT,
                DataContract.Receipts.COL_BALANCE_AMOUNT,
                DataContract.Receipts.COL_PAYMENT_TYPE,
                DataContract.Receipts.COL_CHEQUE_DATE,
                DataContract.Receipts.COL_CHEQUE_NUMBER,
                DataContract.Receipts.COL_REMARK,
                DataContract.Receipts.COL_IS_SYNC
        };
        String orderBy = DataContract.Receipts.COL_ID + " DESC ";
        Cursor cursor = database.query(DataContract.Receipts.TABLE_NAME, projection, null, null, null, null, orderBy);
        if (cursor.moveToFirst()) {
            do {
                //Log.d(TAG, "loadReceipts: "+cursor.getInt(cursor.getColumnIndex(DataContract.Stocks.COL_DOCUMENT_NUMBER)));
                ReceiptModel model = new ReceiptModel();
                model.setReceiptNo(cursor.getString(cursor.getColumnIndex(DataContract.Receipts.COL_RECEIPT_NUMBER)));
                model.setReceiptDate(cursor.getString(cursor.getColumnIndex(DataContract.Receipts.COL_RECEIPT_DATE)));
                model.setSalesmanId(cursor.getString(cursor.getColumnIndex(DataContract.Receipts.COL_SALESMAN_ID)));
                model.setCustomerCode(cursor.getString(cursor.getColumnIndex(DataContract.Receipts.COL_CUSTOMER_CODE)));
                model.setCustomerName(cursor.getString(cursor.getColumnIndex(DataContract.Receipts.COL_CUSTOMER_NAME)));
                model.setReceivedAmount(cursor.getDouble(cursor.getColumnIndex(DataContract.Receipts.COL_RECEIVED_AMOUNT)));
                model.setBalanceAmount(cursor.getDouble(cursor.getColumnIndex(DataContract.Receipts.COL_BALANCE_AMOUNT)));
                model.setPaymentType(cursor.getString(cursor.getColumnIndex(DataContract.Receipts.COL_PAYMENT_TYPE)));
                model.setChequeNumber(cursor.getString(cursor.getColumnIndex(DataContract.Receipts.COL_CHEQUE_NUMBER)));
                model.setChequeDate(cursor.getString(cursor.getColumnIndex(DataContract.Receipts.COL_CHEQUE_DATE)));
                model.setRemark(cursor.getString(cursor.getColumnIndex(DataContract.Receipts.COL_REMARK)));
                model.setIs_sync(cursor.getInt(cursor.getColumnIndex(DataContract.Receipts.COL_IS_SYNC)));
                list.add(model);
            } while (cursor.moveToNext());
        }
        return list;
    }

//    public Cursor getAllUnSyncReceipts(SQLiteDatabase database) {
//
//        Log.d(TAG, "get Un sync Receipts : ");
//        String[] projection = {
//                DataContract.Receipts.COL_RECEIPT_NUMBER,
//                DataContract.Receipts.COL_RECEIPT_DATE,
//                DataContract.Receipts.COL_SALESMAN_ID,
//                DataContract.Receipts.COL_CUSTOMER_CODE,
//                DataContract.Receipts.COL_CUSTOMER_NAME,
//                DataContract.Receipts.COL_RECEIVED_AMOUNT,
//                DataContract.Receipts.COL_BALANCE_AMOUNT,
//                DataContract.Receipts.COL_PAYMENT_TYPE,
//                DataContract.Receipts.COL_CHEQUE_DATE,
//                DataContract.Receipts.COL_CHEQUE_NUMBER,
//                DataContract.Receipts.COL_REMARK,
//                DataContract.Receipts.COL_IS_SYNC
//        };
//        String selection = DataContract.Receipts.COL_IS_SYNC + " LIKE ?";
//        String[] selection_ars = {String.valueOf(DataContract.SYNC_STATUS_FAILED)};
//        //Cursor cursor = database.rawQuery("select"+ DataContract.Invoice.COL_INVOICE_NUMBER+ "from " + DataContract.Invoice.TABLE_NAME + " where " + DataContract.Invoice.COL_IS_SYNC + "="+DataContract.SYNC_STATUS_FAILED , null);
//        Cursor cursor = database.query(DataContract.Receipts.TABLE_NAME, projection, selection, selection_ars, null, null, null);
//        return cursor;
//    }

    public void deleteReceipt() {
        SQLiteDatabase database = getReadableDatabase();
        database.execSQL("delete from " + DataContract.Receipts.TABLE_NAME);
        database.close();
    }

    public Cursor getAllReceiptByDate(String from, String to) {
        SQLiteDatabase database = this.getReadableDatabase();

        Cursor c = database.rawQuery("select receipts_number,customer_name,received_amount,payment_type,receipts_date from " + DataContract.Receipts.TABLE_NAME
                + " where receipts_date BETWEEN '" + from + "' AND '" + to + "' ", null);
        database.close();
        return c;
    }


    /************************ end receipts*******************************************************/
    /************************************paper settings ********************************/


    public void deletePaperSettings() {
        SQLiteDatabase database = getReadableDatabase();
        database.execSQL("delete from " + DataContract.PaperSettings.TABLE_NAME);
        database.close();
    }

    public boolean savePaperSettings(String Name, String Address, String Phone, String Footer, byte[] logo, String Size) {

        SQLiteDatabase database = getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        try {
            contentValues.put(DataContract.PaperSettings.COL_COMPANY_NAME, Name);
            contentValues.put(DataContract.PaperSettings.COL_COMPANY_ADDRESS, Address);
            contentValues.put(DataContract.PaperSettings.COL_COMPANY_PHONE, Phone);
            contentValues.put(DataContract.PaperSettings.COL_FOOTER, Footer);
            contentValues.put(DataContract.PaperSettings.COL_LOGO, logo);
            contentValues.put(DataContract.PaperSettings.COL_PAPER_SIZE, Size);
            database.insert(DataContract.PaperSettings.TABLE_NAME, null, contentValues);
            database.close();
            Log.d(TAG, "one row inserted in Paper Settings table ......... ");
            return true;
        } catch (Exception e) {
            return false;
        }
    }


//    public Cursor getPaperSettings(SQLiteDatabase sqLiteDatabase) {
//        String[] projections = {DataContract.PaperSettings.COL_COMPANY_NAME, DataContract.PaperSettings.COL_COMPANY_ADDRESS,
//                DataContract.PaperSettings.COL_COMPANY_PHONE, DataContract.PaperSettings.COL_FOOTER,
//                DataContract.PaperSettings.COL_PAPER_SIZE, DataContract.PaperSettings.COL_LOGO};
//        Cursor cursor = sqLiteDatabase.query(DataContract.PaperSettings.TABLE_NAME, projections, null, null, null, null,
//                DataContract.PaperSettings.COL_ID + " DESC ", "1");
//        return cursor;
//    }

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

    public int getLastID(String table){

        SQLiteDatabase database = getReadableDatabase();
        Cursor cursor = database.rawQuery("SELECT seq FROM " + "SQLITE_SEQUENCE" + " where " + "name" + "='" + table + "'", null);
        int lastID = (cursor.moveToFirst() ? cursor.getInt(0) : 0);
        cursor.close();
        database.close();
        return lastID;
    }

    public int getAutoId(String tblName){
        SQLiteDatabase database=getReadableDatabase();
        Cursor cursor =database.rawQuery(" SELECT "+tblName+" FROM "+
                DataContract.AutoIdGenerator.TABLE_NAME,null);
        int lastID = (cursor.moveToFirst() ? cursor.getInt(0) : 0);
        cursor.close();
        database.close();
        return lastID;

    }

    public void incrementAutoId(String tblName){
        int id=getAutoId(tblName);
        Log.d(TAG, "incrementAutoId: "+id);
        int newId=++id;
        Log.d(TAG, "new incrementAutoId: "+newId);
        SQLiteDatabase database=getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(tblName,newId);
        database.update(DataContract.AutoIdGenerator.TABLE_NAME, values, null,null);
        database.close();


    }



}
