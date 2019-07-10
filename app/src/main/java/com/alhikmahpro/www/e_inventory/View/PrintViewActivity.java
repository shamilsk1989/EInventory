package com.alhikmahpro.www.e_inventory.View;

import android.Manifest;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.alhikmahpro.www.e_inventory.Adapter.PrintViewAdapter;
import com.alhikmahpro.www.e_inventory.AppUtils;
import com.alhikmahpro.www.e_inventory.Data.Cart;
import com.alhikmahpro.www.e_inventory.Data.CartModel;
import com.alhikmahpro.www.e_inventory.Data.DataContract;
import com.alhikmahpro.www.e_inventory.Data.PrinterCommands;
import com.alhikmahpro.www.e_inventory.Data.SessionHandler;
import com.alhikmahpro.www.e_inventory.Data.dbHelper;
import com.alhikmahpro.www.e_inventory.Interface.volleyListener;
import com.alhikmahpro.www.e_inventory.Network.VolleyServiceGateway;
import com.alhikmahpro.www.e_inventory.R;
import com.android.volley.VolleyError;
import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Chunk;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.Image;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.pdf.BaseFont;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import com.itextpdf.text.pdf.draw.LineSeparator;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class PrintViewActivity extends AppCompatActivity {

    @BindView(R.id.txtInvoice)
    TextView txtInvoice;
    @BindView(R.id.txtDate)
    TextView txtDate;
    @BindView(R.id.txtCustomer)
    TextView txtCustomer;
    @BindView(R.id.txtCustomerName)
    TextView txtCustomerName;
    @BindView(R.id.txtSalesman)
    TextView txtSalesman;
    @BindView(R.id.txtSalesmanId)
    TextView txtSalesmanId;
    @BindView(R.id.rv_items)
    RecyclerView rvViewPrint;
    @BindView(R.id.txtTotalName)
    TextView txtTotalName;
    @BindView(R.id.txtTotalAmount)
    TextView txtTotalAmount;
    @BindView(R.id.txtDiscName)
    TextView txtDiscName;
    @BindView(R.id.txtDiscAmount)
    TextView txtDiscAmount;
    @BindView(R.id.txtNetName)
    TextView txtNetName;
    @BindView(R.id.txtNetAmount)
    TextView txtNetAmount;
    @BindView(R.id.txtPaymentMode)
    TextView txtPaymentMode;
    @BindView(R.id.discLayout)
    RelativeLayout discLayout;
    @BindView(R.id.btn_print)
    Button btnPrint;
    @BindView(R.id.btn_new)
    Button btnNew;
    @BindView(R.id.txtEmpty)
    TextView txtEmpty;
    @BindView(R.id.txtPayment)
    TextView txtPayment;
//    @BindView(R.id.btn_sync)
//    Button btnSync;
    @BindView(R.id.btn_pdf)
    Button btnPdf;

//    @BindView(R.id.toolbar)
//    Toolbar toolbar;

    private ProgressDialog dialog;
    volleyListener mVolleyListener;
    VolleyServiceGateway serviceGateway;
    BluetoothAdapter bluetoothAdapter;
    BluetoothSocket bluetoothSocket;
    BluetoothDevice bluetoothDevice;

    OutputStream outputStream;
    InputStream inputStream;
    Thread thread;

    byte[] readBuffer;
    int readBufferPosition;
    volatile boolean stopWorker;

    private static final int REQUEST_CODE_ENABLING_BT = 1;
    String mDeviceAddress, mDeviceName, mDate;
    private UUID applicationUUID = UUID
            .fromString("00001101-0000-1000-8000-00805F9B34FB");
    BluetoothDevice mBluetoothDevice;
    boolean billStatus;

    RecyclerView.LayoutManager layoutManager;
    PrintViewAdapter adapter;
    private static final String TAG = "PrintViewActivity";
    String customerName, invoiceNo, total, invoiceDate, salesmanId, customerCode, paymentMode, Type;
    String companyName, companyAddress, companyPhone, footer;
    double netAmount, discountAmount, base_total;
    private static final int PERMISSION_CODE = 100;
    private static final int STORAGE_PERMISSION_CODE = 101;
    private String path;
    private File pdfFile;
    private File dir;
    dbHelper helper;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_print);
        ButterKnife.bind(this);
        getSupportActionBar().setTitle("Payment");
        //setSupportActionBar(toolbar);
        Intent intent = getIntent();
        Type = intent.getStringExtra("TYPE");
        customerName = intent.getStringExtra("CUS_NAME");
        customerCode = intent.getStringExtra("CUS_CODE");
        salesmanId = intent.getStringExtra("SALESMAN_ID");
        invoiceNo = intent.getStringExtra("DOC_NO");
        base_total = intent.getDoubleExtra("TOTAL", 0);
        discountAmount = intent.getDoubleExtra("DISCOUNT", 0);
        netAmount = intent.getDoubleExtra("NET", 0);
        paymentMode = intent.getStringExtra("PAY_MOD");
        Log.d(TAG, "onCreate: " + customerName);
        invoiceDate = AppUtils.getDateAndTime();
        helper = new dbHelper(this);
        initView();
        //initVolleyCallBack();
    }

//    private void initVolleyCallBack() {
//        mVolleyListener=new volleyListener() {
//            @Override
//            public void notifySuccess(String requestType, JSONObject response) {
//                dialog.cancel();
//                Log.d(TAG, "notifySuccess: "+response);
//                String res ="";
//                try {
//                    res = response.getString("Status");
//                } catch (JSONException e) {
//                    e.printStackTrace();
//                }
//                if (res.equals("success")) {
//                    helper.updateInvoiceSync(invoiceNo);
//
//                }else {
//                    Toast.makeText(PrintViewActivity.this, "something went wrong pls try again later", Toast.LENGTH_SHORT).show();
//                }
//
//            }
//
//            @Override
//            public void notifyError(String requestType, VolleyError error) {
//                dialog.cancel();
//                Toast.makeText(PrintViewActivity.this, "server error", Toast.LENGTH_SHORT).show();
//
//            }
//        };
//    }

    private void initView() {


        SQLiteDatabase database = helper.getReadableDatabase();
        Cursor cursor = helper.getPaperSettings(database);

        if (cursor.moveToFirst()) {
            companyName = cursor.getString(cursor.getColumnIndex(DataContract.PaperSettings.COL_COMPANY_NAME));
            companyAddress = cursor.getString(cursor.getColumnIndex(DataContract.PaperSettings.COL_COMPANY_ADDRESS));
            companyPhone = cursor.getString(cursor.getColumnIndex(DataContract.PaperSettings.COL_COMPANY_PHONE));
            footer = cursor.getString(cursor.getColumnIndex(DataContract.PaperSettings.COL_FOOTER));
        }
        Log.d(TAG, "initView: " + companyName);

        cursor.close();
        database.close();


        txtInvoice.setText(invoiceNo);
        txtDate.setText(invoiceDate);
        txtCustomerName.setText(customerName);
        txtSalesmanId.setText(salesmanId);
        txtPaymentMode.setText(paymentMode);
        txtTotalAmount.setText(currencyFormatter(base_total));
        txtDiscAmount.setText(currencyFormatter(discountAmount));
        txtNetAmount.setText(currencyFormatter(netAmount));

        layoutManager = new LinearLayoutManager(this);
        rvViewPrint.setLayoutManager(layoutManager);
        rvViewPrint.setHasFixedSize(true);
        adapter = new PrintViewAdapter(PrintViewActivity.this);
        if (Cart.mCart.size() > 0) {
            rvViewPrint.setAdapter(adapter);
            txtEmpty.setVisibility(View.GONE);
        }


    }

    public String currencyFormatter(double val) {
        NumberFormat format = NumberFormat.getCurrencyInstance();
        String pattern = ((DecimalFormat) format).toPattern();
        String newPattern = pattern.replace("\u00A4", "").trim();
        NumberFormat newFormat = new DecimalFormat(newPattern);
        return String.valueOf(newFormat.format(val));

    }

    @OnClick(R.id.btn_print)
    public void onBtnPrintClicked() {
        try {
            closePrinter();
            FindBluetoothDevice();
            openBluetoothPrinter();
            printData();

        } catch (Exception ex) {
            ex.printStackTrace();
        }

    }


    private void closePrinter() {
        try {
            stopWorker = true;
            if (outputStream != null && inputStream != null && bluetoothSocket != null) {
                outputStream.close();
                inputStream.close();
                bluetoothSocket.close();
                Log.d(TAG, "Printer Disconnected.");
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public boolean onNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        clearActivity();
        finish();
    }

    private void clearActivity() {
        closePrinter();
        Cart.mCart.clear();
        // finish all activity and go to home activity
        Intent intent = new Intent(getApplicationContext(), ListSalesActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }

    private void FindBluetoothDevice() {
        try {
            String spDeviceAddress = SessionHandler.getInstance(this).getPrinterName();
            Log.d(TAG, "spDeviceAddress" + spDeviceAddress);

            bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
            if (bluetoothAdapter == null) {
                Log.d(TAG, "Bluetooth Adapter not found");
            }
            if (!bluetoothAdapter.isEnabled()) {
                Intent enableBT = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(enableBT, REQUEST_CODE_ENABLING_BT);
            }

            Set<BluetoothDevice> pairedDevice = bluetoothAdapter.getBondedDevices();

            if (pairedDevice.size() > 0) {
                for (BluetoothDevice pairedDev : pairedDevice) {

                    Log.d(TAG, "printer name:" + pairedDev.getName());
                    Log.d(TAG, "printer address:" + pairedDev.getAddress());
                    if (pairedDev.getAddress().equals(spDeviceAddress)) {
                        bluetoothDevice = pairedDev;
                        Log.d(TAG, "Printer Found " + pairedDev.getName());
                        break;
                    }
                }
            }

        } catch (Exception ex) {
            ex.printStackTrace();
        }

    }

    void openBluetoothPrinter() throws IOException {
        try {

            //Standard uuid from string //
            UUID uuidSting = UUID.fromString("00001101-0000-1000-8000-00805f9b34fb");
            bluetoothSocket = bluetoothDevice.createRfcommSocketToServiceRecord(uuidSting);
            bluetoothSocket.connect();
            outputStream = bluetoothSocket.getOutputStream();
            inputStream = bluetoothSocket.getInputStream();

            Log.d(TAG, "printer connected:");

            beginListenData();

        } catch (Exception ex) {
            ex.printStackTrace();

        }
    }

    void beginListenData() {
        try {

            final byte delimiter = 10;
            stopWorker = false;
            readBufferPosition = 0;
            readBuffer = new byte[1024];

            thread = new Thread(new Runnable() {
                @Override
                public void run() {

                    while (!Thread.currentThread().isInterrupted() && !stopWorker) {
                        try {
                            int byteAvailable = inputStream.available();
                            if (byteAvailable > 0) {
                                byte[] packetByte = new byte[byteAvailable];
                                inputStream.read(packetByte);

                                for (int i = 0; i < byteAvailable; i++) {
                                    byte b = packetByte[i];
                                    if (b == delimiter) {
                                        byte[] encodedByte = new byte[readBufferPosition];
                                        System.arraycopy(
                                                readBuffer, 0,
                                                encodedByte, 0,
                                                encodedByte.length
                                        );
                                        final String data = new String(encodedByte, "US-ASCII");
                                        readBufferPosition = 0;
                                    } else {
                                        readBuffer[readBufferPosition++] = b;
                                    }
                                }
                            }
                        } catch (Exception ex) {
                            stopWorker = true;
                        }
                    }

                }
            });

            thread.start();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void printData() {

        final DecimalFormat decimalFormat = new DecimalFormat("0.00");
        Log.d(TAG, "printBill");
        Thread thread = new Thread() {

            public void run() {

                try {
                    // byte[] printformat = new byte[]{0x1B, 0x21, 0x03};
                    // byte[] printformat = new byte[]{30,35,0};
                    //outputStream.write(printformat);

                    printCustom(companyName, 2, 1);
                    printCustom(companyAddress, 1, 1);
                    printCustom("Tel:" + companyPhone, 1, 1);
                    printNewLine();


                    //printText(leftRightAlign(date,"        "));
                    // printCustom(mDate, 1, 0);
                    printCustom("Invoice #" + invoiceNo, 1, 0);
                    printCustom("Date :" + invoiceDate, 1, 0);
                    printCustom("Customer :" + customerName, 1, 0);
                    printCustom("Salesman :" + salesmanId, 1, 0);
                    printCustom(new String(new char[32]).replace("\0", "."), 1, 1);

                    for (CartModel mm : Cart.mCart) {
                        String item = mm.getProductName();
                        String qty = String.valueOf(mm.getQty());
                        String price = String.valueOf(decimalFormat.format(mm.getRate()));
                        String sub_total = String.valueOf(decimalFormat.format(mm.getNet()));
                        String item_format = price + " X " + mm.getQty();
                        printText(leftRightAlign(item, sub_total));
                        printNewLine();
                        printText(leftRightAlign(item_format, " "));
                        printNewLine();
                    }
                    printCustom(new String(new char[32]).replace("\0", "."), 1, 1);

                    printText(leftRightAlign("Total", " " + decimalFormat.format(base_total)));
                    printNewLine();

                    printText(leftRightAlign("Dis ", "-" + decimalFormat.format(discountAmount)));
                    printNewLine();

                    leftRightAlignLarge("Net", String.valueOf(decimalFormat.format(netAmount)));
                    printNewLine();
                    printCustom(new String(new char[32]).replace("\0", " "), 1, 1);

                    printCustom(new String(new char[32]).replace("\0", "."), 1, 1);
                    printCustom(footer, 1, 1);

                    printNewLine();
                    printNewLine();
                    outputStream.flush();

                } catch (Exception e) {
                    Log.e(TAG, "Exe ", e);
                }

            }
        };
        thread.start();
    }

    @OnClick(R.id.btn_new)
    public void onBtnNewClicked() {
        clearActivity();

    }

    private void printCustom(String msg, int size, int align) {
        //Print config "mode"
        byte[] cc = new byte[]{0x1B, 0x21, 0x03};  // 0- normal size text
        //byte[] cc1 = new byte[]{0x1B,0x21,0x00};  // 0- normal size text
        byte[] bb = new byte[]{0x1B, 0x21, 0x08};  // 1- only bold text
        byte[] bb2 = new byte[]{0x1B, 0x21, 0x20}; // 2- bold with medium text
        byte[] bb3 = new byte[]{0x1B, 0x21, 0x10}; // 3- bold with large text
        try {
            switch (size) {
                case 0:
                    outputStream.write(cc);
                    break;
                case 1:
                    outputStream.write(bb);
                    break;
                case 2:
                    outputStream.write(bb2);
                    break;
                case 3:
                    outputStream.write(bb3);
                    break;
            }

            switch (align) {
                case 0:
                    //left align
                    outputStream.write(PrinterCommands.ESC_ALIGN_LEFT);
                    break;
                case 1:
                    //center align
                    outputStream.write(PrinterCommands.ESC_ALIGN_CENTER);
                    break;
                case 2:
                    //right align
                    outputStream.write(PrinterCommands.ESC_ALIGN_RIGHT);
                    break;
            }
            outputStream.write(msg.getBytes());
            outputStream.write(PrinterCommands.LF);
            //outputStream.write(cc);
            //printNewLine();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    //print text
    private void printText(String msg) {
        try {
            // Print normal text
            outputStream.write(msg.getBytes());
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    //print text
    private void printLargeText(String msg) {
        byte[] bb = new byte[]{0x1B, 0x21, 0x08};
        try {
            // Print Large text

            outputStream.write(bb);
            outputStream.write(msg.getBytes());
        } catch (IOException e) {
            e.printStackTrace();
        }

    }


    private String leftRightAlign(String str1, String str2) {

        Log.d(TAG, "leftRightAlign called");
        if (str1.length() > 12) {
            str1 = str1.substring(0, Math.min(str1.length(), 12));
        } else {

            int no_space = 12 - str1.length();
            String space = addspace(no_space);
            str1 = str1 + space;

        }
        Log.d(TAG, "first str with space:" + str1 + "#");
        Log.d(TAG, "first str with space count:" + str1.length());
        if (str2.length() < 8) {
            int no_space = 7 - str2.length();
            String space = addspace(no_space);
            str2 = space + str2;

        }
        Log.d(TAG, "second str with space:" + str2);
        Log.d(TAG, "second str with space count:" + str2.length());
        String ans = str1 + str2;
        Log.d(TAG, "final:" + ans);

        if (ans.length() < 33) {
            int n = (32 - (str1.length() + str2.length()));
            Log.d(TAG, "n count:" + n);
            ans = str1 + new String(new char[n]).replace("\0", " ") + str2;
            Log.d(TAG, "final str with space count:" + ans.length());
        }

        return ans;
    }


    String addspace(int i) {
        String temp = " ";
        StringBuilder str1 = new StringBuilder();
        for (int j = 0; j < i; j++) {
            str1.append(" ");
        }
        //  str1.append(temp);
        return str1.toString();
    }


    //print new line
    private void printNewLine() {
        try {
            outputStream.write(PrinterCommands.FEED_LINE);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private void leftRightAlignLarge(String str1, String str2) {

        byte[] bb3 = new byte[]{0x1B, 0x21, 0x10};


        Log.d(TAG, "leftRightAlign called");
        if (str1.length() > 12) {
            str1 = str1.substring(0, Math.min(str1.length(), 12));
        } else {

            int no_space = 12 - str1.length();
            String space = addspace(no_space);
            str1 = str1 + space;

        }
        Log.d(TAG, "first str with space:" + str1 + "#");
        Log.d(TAG, "first str with space count:" + str1.length());
        if (str2.length() < 8) {
            int no_space = 7 - str2.length();
            String space = addspace(no_space);
            str2 = space + str2;

        }
        Log.d(TAG, "second str with space:" + str2);
        Log.d(TAG, "second str with space count:" + str2.length());
        String ans = str1 + str2;
        Log.d(TAG, "final:" + ans);

        if (ans.length() < 33) {
            int n = (32 - (str1.length() + str2.length()));
            Log.d(TAG, "n count:" + n);
            ans = str1 + new String(new char[n]).replace("\0", " ") + str2;
            Log.d(TAG, "final str with space count:" + ans.length());
        }


        try {
            outputStream.write(bb3);
            outputStream.write(ans.getBytes());

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

//    @OnClick(R.id.btn_sync)
//    public void onBtnSyncClicked() {
//        //create invoice json array
//
//        JSONArray invoiceArray = new JSONArray();
//        JSONObject invoiceObject = new JSONObject();
//        try {
//            invoiceObject.put(DataContract.Invoice.COL_INVOICE_NUMBER, invoiceNo);
//            invoiceObject.put(DataContract.Invoice.COL_INVOICE_DATE, mDate);
//            invoiceObject.put(DataContract.Invoice.COL_CUSTOMER_CODE, customerCode);
//            invoiceObject.put(DataContract.Invoice.COL_CUSTOMER_NAME, customerName);
//            invoiceObject.put(DataContract.Invoice.COL_SALESMAN_ID, salesmanId);
//            invoiceObject.put(DataContract.Invoice.COL_TOTAL_AMOUNT,base_total);
//            invoiceObject.put(DataContract.Invoice.COL_DISCOUNT_AMOUNT,discountAmount);
//            invoiceObject.put(DataContract.Invoice.COL_NET_AMOUNT, netAmount);
//            invoiceObject.put(DataContract.Invoice.COL_PAYMENT_TYPE, paymentMode);
//
//            invoiceArray.put(invoiceObject);
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }
//
//        //create invoiceDetails json array
//        JSONArray invoiceDetailsArray = new JSONArray();
//        int slNo=0;
//
//        for (CartModel cartModel : Cart.mCart) {
//            slNo++;
//
//            JSONObject detailsObject = new JSONObject();
//            try {
//                detailsObject.put("serial_no", slNo);
//                detailsObject.put(DataContract.InvoiceDetails.COL_INVOICE_NUMBER, invoiceNo);
//                detailsObject.put(DataContract.InvoiceDetails.COL_BAR_CODE, cartModel.getBarcode());
//                detailsObject.put(DataContract.InvoiceDetails.COL_PRODUCT_CODE, cartModel.getProductCode());
//                detailsObject.put(DataContract.InvoiceDetails.COL_PRODUCT_NAME, cartModel.getProductName());
//                detailsObject.put(DataContract.InvoiceDetails.COL_QUANTITY, cartModel.getQty());
//                detailsObject.put(DataContract.InvoiceDetails.COL_UNIT1, cartModel.getUnit1());
//                detailsObject.put(DataContract.InvoiceDetails.COL_UNIT2, cartModel.getUnit2());
//                detailsObject.put(DataContract.InvoiceDetails.COL_UNIT3, cartModel.getUnit3());
//                detailsObject.put(DataContract.InvoiceDetails.COL_UNIT, cartModel.getSelectedUnit());
//                detailsObject.put(DataContract.InvoiceDetails.COL_UN_QTY1, cartModel.getUnit1Qty());
//                detailsObject.put(DataContract.InvoiceDetails.COL_UN_QTY2, cartModel.getUnit2Qty());
//                detailsObject.put(DataContract.InvoiceDetails.COL_UN_QTY3, cartModel.getUnit3Qty());
//                detailsObject.put(DataContract.InvoiceDetails.COL_RATE, cartModel.getRate());
//                detailsObject.put(DataContract.InvoiceDetails.COL_DISCOUNT, cartModel.getDiscount());
//                detailsObject.put(DataContract.InvoiceDetails.COL_NET_AMOUNT, cartModel.getNet());
//
//
//
//                detailsObject.put(DataContract.InvoiceDetails.COL_NET_AMOUNT, cartModel.getNet());
//                detailsObject.put(DataContract.InvoiceDetails.COL_NET_AMOUNT, cartModel.getNet());
//                detailsObject.put(DataContract.InvoiceDetails.COL_SALE_TYPE, cartModel.getSaleType());
//
//
//
//
//                invoiceDetailsArray.put(detailsObject);
//            } catch (JSONException e) {
//                e.printStackTrace();
//            }
//        }
//
//        JSONObject result = new JSONObject();
//
//        try {
//            result.put("Invoice", invoiceArray);
//            result.put("Details", invoiceDetailsArray);
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }
//
//        //send to volley
//        View view = this.getCurrentFocus();
//        AppUtils.hideKeyboard(this, view);
//        dialog = AppUtils.showProgressDialog(this, "Loading....");
//        serviceGateway = new VolleyServiceGateway(mVolleyListener, this);
//        serviceGateway.postDataVolley("POSTCALL", "PriceChecker/sales_invoice.php", result);
//    }

    @OnClick(R.id.btn_pdf)
    public void onBtnPdfClicked() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//            if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
//                Log.d(TAG, "Permission already granted: ");
//                createPdfWrapper();
//            } else {
//                requestStoragePermission();
//            }
            if (requestStoragePermission()) {
                createPdfWrapper();
            }
        } else {
            createPdfWrapper();
        }

    }

    private boolean requestStoragePermission() {

        int writePermission = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        int readPermission = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE);
        List<String> listPermissions = new ArrayList<>();
        if (writePermission != PackageManager.PERMISSION_GRANTED) {
            listPermissions.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
        }
        if (readPermission != PackageManager.PERMISSION_GRANTED) {
            listPermissions.add(Manifest.permission.READ_EXTERNAL_STORAGE);
        }
        if (!listPermissions.isEmpty()) {
            ActivityCompat.requestPermissions(this, listPermissions.toArray(new String[listPermissions.size()]), PERMISSION_CODE);
            return false;
        }
        return true;
//        if (ActivityCompat.shouldShowRequestPermissionRationale(PrintViewActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
//
//            new AlertDialog.Builder(PrintViewActivity.this)
//                    .setTitle("Permission needed")
//                    .setMessage("To continue please allow the permission ")
//                    .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
//                        @Override
//                        public void onClick(DialogInterface dialog, int which) {
//                            ActivityCompat.requestPermissions(PrintViewActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, STORAGE_PERMISSION_CODE);
//
//                        }
//                    })
//                    .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
//                        @Override
//
//                        public void onClick(DialogInterface dialog, int which) {
//                            dialog.cancel();
//                        }
//                    }).create().show();
//
//
//        } else {
//            ActivityCompat.requestPermissions(PrintViewActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, STORAGE_PERMISSION_CODE);
//        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case PERMISSION_CODE:
                Map<String, Integer> perms = new HashMap<>();
                perms.put(Manifest.permission.READ_EXTERNAL_STORAGE, PackageManager.PERMISSION_GRANTED);
                perms.put(Manifest.permission.WRITE_EXTERNAL_STORAGE, PackageManager.PERMISSION_GRANTED);

                if (grantResults.length > 0) {
                    for (int i = 0; i < permissions.length; i++)
                        perms.put(permissions[i], grantResults[i]);

                    //check all permissions
                    if (perms.get(Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED &&
                            perms.get(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {

                        Log.d(TAG, "permission granted ");
                        createPdfWrapper();
                    } else {
                        if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) ||

                                ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.READ_EXTERNAL_STORAGE)) {

                            new AlertDialog.Builder(PrintViewActivity.this)
                                    .setTitle("Permission needed")
                                    .setMessage("To continue please allow the permission ")
                                    .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            requestStoragePermission();
                                        }
                                    })
                                    .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                        @Override

                                        public void onClick(DialogInterface dialog, int which) {
                                            dialog.cancel();
                                        }
                                    }).create().show();

                        } else {
                            Toast.makeText(this, "permission denied", Toast.LENGTH_SHORT).show();
                        }
                    }
                }

        }


    }

    private void createPdfWrapper() {

        final DecimalFormat decimalFormat = new DecimalFormat("0.00");

        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
        String mDate = sdf.format(new Date());
        Document document = new Document();
        try {

            //create directory
            String directoryPath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/Invoice";
            dir = new File(directoryPath);
            if (!dir.exists()) {
                dir.mkdir();
            }

            String fileName = invoiceNo + "-" + invoiceDate+".pdf";
            pdfFile = new File(dir, fileName);

            //PdfWriter.getInstance(document,new FileOutputStream(mFilePath));
            FileOutputStream stream = new FileOutputStream(pdfFile);
            //PdfWriter pdfWriter=PdfWriter.getInstance(document,stream);
            PdfWriter.getInstance(document, stream);
            //open document

            document.open();
            //document settings

            document.setPageSize(PageSize.A4);
            document.addCreationDate();
            BaseColor mColorAccent = new BaseColor(0, 153, 204, 255);
            float mHeadingFontSize = 13.0f;
            float mValueFontSize = 15.0f;
            float mItemFontSize = 10.0f;
            Paragraph paragraph = new Paragraph("");

            //adding title image
//            Image image=Image.getInstance("src/resource/logo.png");
//            image.scaleAbsolute(540f,75f);


            //font
            BaseFont urName = BaseFont.createFont("assets/fonts/brandon_medium.otf", "UTF-8", BaseFont.EMBEDDED);

            Font heading = new Font(Font.FontFamily.TIMES_ROMAN, 12, Font.BOLD, new BaseColor(0, 0, 0));
            Font normal = new Font(Font.FontFamily.TIMES_ROMAN, 12);

            // LINE SEPARATOR
            LineSeparator lineSeparator = new LineSeparator();
            lineSeparator.setLineColor(new BaseColor(0, 0, 0, 68));

            // Title Order Details...
            // Adding Title....

            Font mOrderDetailsTitleFont = new Font(urName, 25.0f, Font.NORMAL, BaseColor.BLACK);
            Chunk mOrderDetailsTitleChunk = new Chunk(companyName, mOrderDetailsTitleFont);
            Paragraph mOrderDetailsTitleParagraph = new Paragraph(mOrderDetailsTitleChunk);
            mOrderDetailsTitleParagraph.setAlignment(Element.ALIGN_CENTER);
            document.add(mOrderDetailsTitleParagraph);

            Font mOrderDetailsTitle2Font = new Font(urName, 22.0f, Font.NORMAL, BaseColor.BLACK);
            Chunk mOrderDetailsTitle2Chunk = new Chunk(companyAddress, mOrderDetailsTitle2Font);
            Paragraph mOrderDetailsTitle2Paragraph = new Paragraph(mOrderDetailsTitle2Chunk);
            mOrderDetailsTitle2Paragraph.setAlignment(Element.ALIGN_CENTER);
            document.add(mOrderDetailsTitle2Paragraph);

            Font mOrderDetailsTitle3Font = new Font(urName, 20.0f, Font.NORMAL, BaseColor.BLACK);
            Chunk mOrderDetailsTitle3Chunk = new Chunk(companyPhone, mOrderDetailsTitle3Font);
            Paragraph mOrderDetailsTitle3Paragraph = new Paragraph(mOrderDetailsTitle3Chunk);
            mOrderDetailsTitle3Paragraph.setAlignment(Element.ALIGN_CENTER);
            document.add(mOrderDetailsTitle3Paragraph);


            // Fields of Order Details...
            // Adding Chunks for Title and value

            Font mOrderIdFont = new Font(urName, mHeadingFontSize, Font.NORMAL, mColorAccent);
            Chunk mOrderIdChunk = new Chunk("Invoice No:", mOrderIdFont);
            Paragraph mOrderIdParagraph = new Paragraph(mOrderIdChunk);
            document.add(mOrderIdParagraph);

            Font mOrderIdValueFont = new Font(urName, mValueFontSize, Font.NORMAL, BaseColor.BLACK);
            Chunk mOrderIdValueChunk = new Chunk("#" + invoiceNo, mOrderIdValueFont);
            Paragraph mOrderIdValueParagraph = new Paragraph(mOrderIdValueChunk);
            document.add(mOrderIdValueParagraph);

            // Adding Line Breakable Space....
            document.add(new Paragraph(""));
            // Adding Horizontal Line...
            //  document.add(new Chunk(lineSeparator));
            // Adding Line Breakable Space....
            document.add(new Paragraph(""));

            // Fields of Order Details...
            Font mOrderDateFont = new Font(urName, mHeadingFontSize, Font.NORMAL, mColorAccent);
            Chunk mOrderDateChunk = new Chunk("Order Date:", mOrderDateFont);
            Paragraph mOrderDateParagraph = new Paragraph(mOrderDateChunk);
            document.add(mOrderDateParagraph);

            Font mOrderDateValueFont = new Font(urName, mValueFontSize, Font.NORMAL, BaseColor.BLACK);
            Chunk mOrderDateValueChunk = new Chunk(invoiceDate, mOrderDateValueFont);
            Paragraph mOrderDateValueParagraph = new Paragraph(mOrderDateValueChunk);
            document.add(mOrderDateValueParagraph);

            document.add(new Paragraph(""));
            //document.add(new Chunk(lineSeparator));
            document.add(new Paragraph(""));

            // Fields of Order Details...
            Font mOrderAcNameFont = new Font(urName, mHeadingFontSize, Font.NORMAL, mColorAccent);
            Chunk mOrderAcNameChunk = new Chunk("Customer Name:", mOrderAcNameFont);
            Paragraph mOrderAcNameParagraph = new Paragraph(mOrderAcNameChunk);
            document.add(mOrderAcNameParagraph);

            Font mOrderAcNameValueFont = new Font(urName, mValueFontSize, Font.NORMAL, BaseColor.BLACK);
            Chunk mOrderAcNameValueChunk = new Chunk(customerName, mOrderAcNameValueFont);
            Paragraph mOrderAcNameValueParagraph = new Paragraph(mOrderAcNameValueChunk);
            document.add(mOrderAcNameValueParagraph);

            document.add(new Paragraph(""));
            //document.add(new Chunk(lineSeparator));
            document.add(new Paragraph(""));

            // Fields of Order Details...
            Font mOrderSalesmanFont = new Font(urName, mHeadingFontSize, Font.NORMAL, mColorAccent);
            Chunk mOrderSalesmanChunk = new Chunk("Salesman:", mOrderSalesmanFont);
            Paragraph mOrderSalesmanParagraph = new Paragraph(mOrderSalesmanChunk);
            document.add(mOrderSalesmanParagraph);

            Font mOrderSalesmanValueFont = new Font(urName, mValueFontSize, Font.NORMAL, BaseColor.BLACK);
            Chunk mOrderSalesmanValueChunk = new Chunk(salesmanId, mOrderSalesmanValueFont);
            Paragraph mOrderSalesmanValueParagraph = new Paragraph(mOrderSalesmanValueChunk);
            document.add(mOrderSalesmanValueParagraph);

            document.add(new Paragraph(""));
            document.add(new Chunk(lineSeparator));
            document.add(new Paragraph(""));

            //float[]columnWidth={}
            PdfPTable pTable = new PdfPTable(3);
            pTable.setWidthPercentage(100);
            //insert heading

            insertCell(pTable, " Item Name", Element.ALIGN_RIGHT, 1, heading);
            insertCell(pTable, " Quantity", Element.ALIGN_LEFT, 1, heading);
            insertCell(pTable, " Total", Element.ALIGN_LEFT, 1, heading);
            pTable.setHeaderRows(1);

            for (CartModel mm : Cart.mCart) {

                String item = mm.getProductName();
                String qty = String.valueOf(mm.getQty());
                String price = String.valueOf(decimalFormat.format(mm.getRate()));
                String sub_total = String.valueOf(decimalFormat.format(mm.getNet()));
                String item_format = price + " X " + mm.getQty();

                insertCell(pTable, item, Element.ALIGN_RIGHT, 1, normal);
                insertCell(pTable, item_format, Element.ALIGN_LEFT, 1, normal);
                insertCell(pTable, price, Element.ALIGN_LEFT, 1, normal);

            }
            insertCell(pTable, "", Element.ALIGN_LEFT, 3, heading);
            insertCell(pTable, "Total ", Element.ALIGN_RIGHT, 2, normal);
            insertCell(pTable, decimalFormat.format(base_total), Element.ALIGN_RIGHT, 1, normal);
            insertCell(pTable, "Discount ", Element.ALIGN_RIGHT, 2, normal);
            insertCell(pTable, decimalFormat.format(discountAmount), Element.ALIGN_RIGHT, 1, normal);
            insertCell(pTable, "Net :", Element.ALIGN_RIGHT, 2, normal);
            insertCell(pTable, decimalFormat.format(netAmount), Element.ALIGN_RIGHT, 1, normal);
            paragraph.add(pTable);
            document.add(paragraph);
            Toast.makeText(this, "Pdf Generated", Toast.LENGTH_SHORT).show();


            //previewPDF(fileName,dir);
            document.close();

        } catch (DocumentException de) {
            Toast.makeText(this, "Error " + de.getMessage(), Toast.LENGTH_SHORT).show();
            Log.d(TAG, "createPdfWrapper: " + de.getMessage());
            //Log.d(TAG, "createPdfWrapper: "+ex.;
            de.printStackTrace();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }


    private void insertCell(PdfPTable pTable, String text, int align, int colspan, Font font) {

        PdfPCell cell = new PdfPCell(new Phrase(text.trim(), font));
        cell.setHorizontalAlignment(align);
        cell.setColspan(colspan);
        if (text.trim().equalsIgnoreCase("")) {
            cell.setMinimumHeight(10f);
        }
        pTable.addCell(cell);

    }

    private void previewPDF(String fileName, File dir) {
        String path="/sdcard/pdffromlayout.pdf";
                //Environment.getExternalStorageDirectory().getAbsolutePath()+"/"+dir+"/"+fileName;
        Log.d(TAG, "previewPDF: path "+path);
        File file=new File(path);
        if(file.exists()){
            Intent pdfIntent=new Intent(Intent.ACTION_VIEW);
            Uri uri=Uri.fromFile(file);
            pdfIntent.setDataAndType(uri,"application/pdf");
            pdfIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            try {
                startActivity(pdfIntent);
            } catch (ActivityNotFoundException ae) {
                ae.printStackTrace();
                Toast.makeText(this, "Can't read file", Toast.LENGTH_SHORT).show();
            }
        }else {
            Log.d(TAG, "previewPDF: no file ");
        }

    }


    static class createPdf extends AsyncTask<Void,Void,Void>{

        @Override
        protected Void doInBackground(Void... voids) {
            return null;
        }
    }


}
