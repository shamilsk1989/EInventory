package com.alhikmahpro.www.e_inventory.View;

import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
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
import com.alhikmahpro.www.e_inventory.Data.Utils;
import com.alhikmahpro.www.e_inventory.Data.dbHelper;
import com.alhikmahpro.www.e_inventory.Interface.volleyListener;
import com.alhikmahpro.www.e_inventory.Network.VolleyServiceGateway;
import com.alhikmahpro.www.e_inventory.R;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.BitSet;
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
    @BindView(R.id.toolbar)
    Toolbar toolbar;
//    @BindView(R.id.btn_sync)
//    Button btnSync;
//    @BindView(R.id.btn_pdf)
//    Button btnPdf;

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
    BitSet dots;

    RecyclerView.LayoutManager layoutManager;
    PrintViewAdapter adapter;
    private static final String TAG = "PrintViewActivity";
    String customerName, invoiceNo, total, invoiceDate, salesmanId, customerCode, paymentMode, Action;
    String companyName = "", companyAddress = "", companyPhone = "", footer = "";
    double netAmount, discountAmount, base_total;
    Bitmap thumbnail;
    private static final int PERMISSION_CODE = 100;
    private static final int STORAGE_PERMISSION_CODE = 101;
    private String path;
    private File pdfFile;
    private File dir;
    AlertDialog alertDialog;
    AlertDialog.Builder builder;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_print);
        ButterKnife.bind(this);
        setSupportActionBar(toolbar);
        toolbar.setTitle("Payment");
        Intent intent = getIntent();
        Action = intent.getStringExtra("ACTION");
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
        initView();

    }


    private void initView() {
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
            printData();
            //clearActivity();

            // printPhoto(R.drawable.img);


        } catch (Exception ex) {
            ex.printStackTrace();
        }

    }
    private void gotoPdfView(){
        Intent intent_print = new Intent(PrintViewActivity.this,ViewPdfActivity.class);
        intent_print.putExtra("ACTION", DataContract.ACTION_NEW);
        intent_print.putExtra("CUS_NAME", customerName);
        intent_print.putExtra("CUS_CODE", customerCode);
        intent_print.putExtra("DISCOUNT", discountAmount);
        intent_print.putExtra("SALESMAN_ID", salesmanId);
        intent_print.putExtra("DOC_NO", invoiceNo);
        intent_print.putExtra("DOC_DATE", invoiceDate);
        intent_print.putExtra("TOTAL", base_total);
        intent_print.putExtra("NET", netAmount);
        intent_print.putExtra("PAY_MOD", paymentMode);
        startActivity(intent_print);
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
                        openBluetoothPrinter();
                        break;
                    } else {
                        Toast.makeText(this, "Printer not found", Toast.LENGTH_SHORT).show();
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

    //print photo
    public void printPhoto(int img) {
        try {
            Bitmap bmp = BitmapFactory.decodeResource(getResources(), img);
            if (bmp != null) {
                byte[] command = Utils.decodeBitmap(bmp);
                outputStream.write(PrinterCommands.ESC_ALIGN_CENTER);
                printText(command);
            } else {
                Log.e("Print Photo error", "the file isn't exists");
            }
        } catch (Exception e) {
            e.printStackTrace();
            Log.e("PrintTools", "the file isn't exists");
        }
    }


    private void printData() throws IOException {

        try {
            ShowProgressDialog();
            final DecimalFormat decimalFormat = new DecimalFormat("0.00");
            byte[] printformat = new byte[]{0x1B, 0x21, 0x03};
            //   byte[] printformat = new byte[]{30,35,0};
            outputStream.write(printformat);

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
            e.printStackTrace();
        } finally {
            HideProgressDialog();
            clearActivity();
        }
    }


    private void printData1() {

        final DecimalFormat decimalFormat = new DecimalFormat("0.00");
        Log.d(TAG, "printBill");
        Thread thread = new Thread() {

            public void run() {

                try {
//                    if(thumbnail!=null){
//                        byte[] command = Utils.decodeBitmap(thumbnail);
//                        outputStream.write(PrinterCommands.ESC_ALIGN_CENTER);
//                        printText(command);
//                    }
                    byte[] printformat = new byte[]{0x1B, 0x21, 0x03};
                    //   byte[] printformat = new byte[]{30,35,0};
                    outputStream.write(printformat);

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

    //print byte[]
    private void printText(byte[] msg) {
        Log.d(TAG, "printText: " + msg);
        try {
            // Print normal text
            outputStream.write(msg);
            printNewLine();
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

    private void ShowProgressDialog() {
        builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
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
