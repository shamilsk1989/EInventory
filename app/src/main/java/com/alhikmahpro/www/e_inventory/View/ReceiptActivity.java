package com.alhikmahpro.www.e_inventory.View;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.alhikmahpro.www.e_inventory.Data.Cart;
import com.alhikmahpro.www.e_inventory.Data.DataContract;
import com.alhikmahpro.www.e_inventory.Data.PrinterCommands;
import com.alhikmahpro.www.e_inventory.Data.SessionHandler;
import com.alhikmahpro.www.e_inventory.Data.dbHelper;
import com.alhikmahpro.www.e_inventory.Interface.volleyListener;
import com.alhikmahpro.www.e_inventory.Network.VolleyServiceGateway;
import com.alhikmahpro.www.e_inventory.R;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.Set;
import java.util.UUID;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class ReceiptActivity extends AppCompatActivity {

    @BindView(R.id.txt_date)
    TextView txtDate;
    @BindView(R.id.txt_receipt)
    TextView txtReceipt;
    @BindView(R.id.textViewCustomerName)
    TextView textViewCustomerName;
    @BindView(R.id.editTextBalance)
    EditText editTextBalance;
    @BindView(R.id.editTextAmount)
    EditText editTextAmount;
    @BindView(R.id.radioCash)
    RadioButton radioCash;
    @BindView(R.id.radioCheque)
    RadioButton radioCheque;
    @BindView(R.id.radioGroup)
    RadioGroup radioGroup;
    @BindView(R.id.editTextChequeNumber)
    EditText editTextChequeNumber;
    @BindView(R.id.editTextChequeDate)
    EditText editTextChequeDate;
    @BindView(R.id.cheque_layout)
    LinearLayout chequeLayout;
    @BindView(R.id.txt_remark)
    TextView txtRemark;
    @BindView(R.id.editTextRemark)
    EditText editTextRemark;
    @BindView(R.id.btnPrint)
    Button btnPrint;
    @BindView(R.id.btnNew)
    Button btnNew;
    @BindView(R.id.btnPdf)
    Button btnPdf;
    String mDate;
    int mDoc;
    String type,paymentType;
    String customerName,customerCode,balanceAmount;
    RadioButton radioButton;
    Calendar myCalendar;

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
    String mDeviceAddress, mDeviceName;
    private UUID applicationUUID = UUID
            .fromString("00001101-0000-1000-8000-00805F9B34FB");
    BluetoothDevice mBluetoothDevice;
    boolean billStatus;

    String companyName, companyAddress, companyPhone, footer;
    private static final String TAG = "ReceiptActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_receipt);
        ButterKnife.bind(this);
        editTextBalance.setEnabled(false);
        Intent intent=getIntent();
        type=intent.getStringExtra("TYPE");
        customerName=intent.getStringExtra("CUS_NAME");
        customerCode=intent.getStringExtra("CUS_CODE");
        balanceAmount=intent.getStringExtra("BALANCE_AMOUNT");
        paymentType=intent.getStringExtra("PAYMENT_TYPE");

        Log.d(TAG, "onCreate: balance :"+balanceAmount);
        myCalendar=Calendar.getInstance();

        initView();

    }

    DatePickerDialog.OnDateSetListener date=new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker datePicker, int year, int monthOfYear, int dayOfMonth) {
            // TODO Auto-generated method stub
            myCalendar.set(Calendar.YEAR, year);
            myCalendar.set(Calendar.MONTH, monthOfYear);
            myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
            updateLabel();
        }
    };

    private void updateLabel() {
        String myFormat = "dd/MM/yy"; //In which you need put here
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);
        editTextChequeDate.setText(sdf.format(myCalendar.getTime()));
    }

    private void initView() {

        if(paymentType!=null && paymentType.equals("Cash")){
            radioCash.setChecked(true);
            chequeLayout.setVisibility(View.GONE);
        }else {
            radioCheque.setChecked(true);
            chequeLayout.setVisibility(View.VISIBLE);
        }


        if(type!=null && type.equals("NEW")){
            SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
            dbHelper helper = new dbHelper(this);
            mDate = sdf.format(new Date());
            int last_no = helper.getLastReceiptNo();
            Log.d(TAG, "setDoc: " + last_no);
            mDoc = last_no + 1;
        }else {
            Intent intent=getIntent();
            mDate=intent.getStringExtra("receiptDate");
            mDoc=Integer.valueOf(intent.getStringExtra("receiptNumber"));
            String recAmount=intent.getStringExtra("RECEIVED_AMOUNT");
            double amount=ParseDouble(recAmount);
            editTextAmount.setText(currencyFormatter(amount));
            editTextChequeDate.setText(intent.getStringExtra("CHEQUE_DATE"));
            editTextChequeNumber.setText(intent.getStringExtra("CHEQUE_NUMBER"));



        }

        double balAmount=ParseDouble(balanceAmount);
        editTextBalance.setText(currencyFormatter(balAmount ));
        textViewCustomerName.setText(customerName);
        txtDate.setText(mDate);
        txtReceipt.setText(String.valueOf(mDoc));
    }

    @OnClick({R.id.editTextChequeDate, R.id.btnPrint,R.id.btnNew,R.id.btnPdf})
    public void onViewClicked(View view) {
        switch (view.getId()) {

            case R.id.editTextChequeDate:
                new DatePickerDialog(this,date,myCalendar.get(Calendar.YEAR),myCalendar.get(Calendar.MONTH),myCalendar.get(Calendar.DAY_OF_MONTH)).show();
                break;
            case R.id.btnPrint:

                // save data to local data base
                dbHelper helper=new dbHelper(this);
                if(helper.saveReceipts(txtReceipt.getText().toString(),mDate,"Tab",customerCode,customerName,balanceAmount,editTextAmount.getText().toString(),
                        paymentType,editTextChequeDate.getText().toString(),editTextChequeNumber.getText().toString(),DataContract.SYNC_STATUS_FAILED)){
                    printingJob();
                }
                else {
                    Toast.makeText(this, "Saving Failed", Toast.LENGTH_SHORT).show();
                }

                break;
        }
    }

    public void onRadioButtonClicked(View view) {
        int radioId = radioGroup.getCheckedRadioButtonId();
        Log.d(TAG, "onRadioButtonClicked: " + radioId);
        radioButton = findViewById(radioId);
        String paymentType = radioButton.getText().toString();
        if(paymentType.equals("Cash")){
            chequeLayout.setVisibility(View.GONE);
        }else {
            chequeLayout.setVisibility(View.VISIBLE);
        }

        Toast.makeText(this, "selected type"+paymentType, Toast.LENGTH_SHORT).show();
    }


    public String currencyFormatter(double val) {
        NumberFormat format = NumberFormat.getCurrencyInstance();
        String pattern = ((DecimalFormat) format).toPattern();
        String newPattern = pattern.replace("\u00A4", "").trim();
        NumberFormat newFormat = new DecimalFormat(newPattern);
        return String.valueOf(newFormat.format(val));

    }
    double ParseDouble(String strNumber) {
        if (strNumber != null && strNumber.length() > 0) {
            try {
                return Double.parseDouble(strNumber);
            } catch (Exception e) {
                return -1;   // or some value to mark this field is wrong. or make a function validates field first ...
            }
        } else return 0;
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
                    printCustom("Invoice #" + txtReceipt.getText().toString(), 1, 0);
                    printCustom("Date :" + mDate, 1, 0);
                    printCustom("Customer :" + customerName, 1, 0);
                    //printCustom("Salesman :" + salesmanId, 1, 0);
                    printCustom(new String(new char[32]).replace("\0", "."), 1, 1);

//                    for (CartModel mm : Cart.mCart) {
//                        String item = mm.getProductName();
//                        String qty = String.valueOf(mm.getQty());
//                        String price = String.valueOf(decimalFormat.format(mm.getRate()));
//                        String sub_total = String.valueOf(decimalFormat.format(mm.getNet()));
//                        String item_format = price + " X " + mm.getQty();
//                        printText(leftRightAlign(item, sub_total));
//                        printNewLine();
//                        printText(leftRightAlign(item_format, " "));
//                        printNewLine();
//                    }
                    double recAmount=ParseDouble(editTextAmount.getText().toString());

                   // printCustom(new String(new char[32]).replace("\0", "."), 1, 1);

//                    printText(leftRightAlign("Received Amount", " " + currencyFormatter(recAmount)));
//                    printNewLine();

//                    printText(leftRightAlign("Dis ", "-" + decimalFormat.format(discountAmount)));
//                    printNewLine();
//
                    leftRightAlignLarge("Amount",decimalFormat.format(recAmount) );//currencyFormatter(recAmount)
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

   private void printingJob(){
       dbHelper helper=new dbHelper(this);
       SQLiteDatabase database = helper.getReadableDatabase();
       Cursor cursor = helper.getPaperSettings(database);

       if (cursor.moveToFirst()) {
           companyName = cursor.getString(cursor.getColumnIndex(DataContract.PaperSettings.COL_COMPANY_NAME));
           companyAddress = cursor.getString(cursor.getColumnIndex(DataContract.PaperSettings.COL_COMPANY_ADDRESS));
           companyPhone = cursor.getString(cursor.getColumnIndex(DataContract.PaperSettings.COL_COMPANY_PHONE));
           footer = cursor.getString(cursor.getColumnIndex(DataContract.PaperSettings.COL_FOOTER));


           try {
               closePrinter();
               FindBluetoothDevice();
               openBluetoothPrinter();
               printData();

           } catch (Exception ex) {
               ex.printStackTrace();
           }
       }else {
           Toast.makeText(this, "Settings Error", Toast.LENGTH_SHORT).show();
       }
       Log.d(TAG, "initView: " + companyName);
       cursor.close();
       database.close();
   }

}
