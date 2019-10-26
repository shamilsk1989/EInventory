package com.alhikmahpro.www.e_inventory.View;

import android.app.Activity;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.alhikmahpro.www.e_inventory.Data.SessionHandler;
import com.alhikmahpro.www.e_inventory.R;

import java.io.IOException;
import java.util.Set;
import java.util.UUID;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class AddPrinterActivity extends AppCompatActivity implements Runnable {

    @BindView(R.id.textpaper)
    TextView textpaper;
    @BindView(R.id.add_layout)
    LinearLayout addLayout;
    @BindView(R.id.textprinter)
    TextView textprinter;
    @BindView(R.id.remove_layout)
    LinearLayout removeLayout;
    private static final int REQUEST_CONNECT_DEVICE = 1;
    private static final int REQUEST_ENABLE_BT = 2;
    private static final String TAG = "AddPrinterActivity";
    public static final String MY_PREFS_NAME = "BluetoothDevice";
    SharedPreferences pref;
    SharedPreferences.Editor editor;
    BluetoothAdapter mBluetoothAdapter;
    BluetoothSocket mBluetoothSocket;
    BluetoothDevice mBluetoothDevice;
    String deviceAddress, deviceName;
    ProgressDialog progressDialog;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    private UUID applicationUUID = UUID
            .fromString("00001101-0000-1000-8000-00805F9B34FB");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_printer);
        ButterKnife.bind(this);

        progressDialog = new ProgressDialog(this);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        toolbar.setTitle("Printer");

        pref = getApplicationContext().getSharedPreferences(MY_PREFS_NAME, 0);
        editor = pref.edit();
        editor.apply();
    }

    @OnClick(R.id.add_layout)
    public void onAddLayoutClicked() {
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (mBluetoothAdapter == null) {
            Toast.makeText(this, "Bluetooth not support", Toast.LENGTH_SHORT).show();
        } else {
            if (!mBluetoothAdapter.isEnabled()) {
                Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
            } else {
                ListPairedDevices();
                Intent connectIntent = new Intent(this, DeviceListActivity.class);
                startActivityForResult(connectIntent,
                        REQUEST_CONNECT_DEVICE);
            }
        }
    }

    private void ListPairedDevices() {

        Set<BluetoothDevice> mPairedDevices = mBluetoothAdapter.getBondedDevices();
        if (mPairedDevices.size() > 0) {
            for (BluetoothDevice mDevice : mPairedDevices) {
                Log.v(TAG, "PairedDevices: " + mDevice.getName() + "  "
                        + mDevice.getAddress());
            }
        }

    }

    @OnClick(R.id.remove_layout)
    public void onRemoveLayoutClicked() {
        if (mBluetoothAdapter != null)
            mBluetoothAdapter.disable();
        SessionHandler.getInstance(getApplicationContext()).removePrinter();
        Toast.makeText(getApplication(), "Printer disconnected", Toast.LENGTH_LONG).show();
    }


    @Override
    protected void onActivityResult(int mRequestCode, int mResultCode, Intent data) {
        super.onActivityResult(mRequestCode, mResultCode, data);

        switch (mRequestCode) {
            case REQUEST_CONNECT_DEVICE:
                if (mResultCode == Activity.RESULT_OK) {
                    Bundle mExtra = data.getExtras();
                    deviceAddress = mExtra.getString("DeviceAddress");
                    //deviceName=mExtra.getString("DeviceName");

                    Log.v(TAG, "Coming incoming address + name" + deviceAddress);
                    mBluetoothDevice = mBluetoothAdapter.getRemoteDevice(deviceAddress);
                    Log.d(TAG, "printer " + SessionHandler.getInstance(getApplicationContext()).getPrinterName());
                    if (SessionHandler.getInstance(getApplicationContext()).getPrinterName().isEmpty()) {
                        Log.d(TAG, "inside printer");

                        //dialog.show(this,"Connecting...", mBluetoothDevice.getName() + " : " + mBluetoothDevice.getAddress(), true, true);
                        progressDialog.setTitle("Connecting..");
                        progressDialog.setMessage(mBluetoothDevice.getName());
                        progressDialog.setIndeterminate(true);
                        progressDialog.setCancelable(true);
                        progressDialog.show();

                        Thread mBlutoothConnectThread = new Thread(this);
                        mBlutoothConnectThread.start();
                    } else {
                        Toast.makeText(this, "printer already connected", Toast.LENGTH_LONG).show();

                    }
                }
                break;

            case REQUEST_ENABLE_BT:
                if (mResultCode == Activity.RESULT_OK) {
                    ListPairedDevices();
                    Intent connectIntent = new Intent(AddPrinterActivity.this, DeviceListActivity.class);
                    startActivityForResult(connectIntent, REQUEST_CONNECT_DEVICE);
                } else {
                    Toast.makeText(this, "Message", Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }

    @Override
    public void run() {
        try {
            mBluetoothSocket = mBluetoothDevice.createRfcommSocketToServiceRecord(applicationUUID);
            mBluetoothAdapter.cancelDiscovery();
            mBluetoothSocket.connect();
            mHandler.sendEmptyMessage(0);
        } catch (IOException eConnectException) {
            Log.d(TAG, "CouldNotConnectToSocket", eConnectException);
            closeSocket(mBluetoothSocket);
            return;
        }

    }

    private Handler mHandler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message msg) {

            if (progressDialog.isShowing()) {
                progressDialog.dismiss();
            }

            SessionHandler.getInstance(getApplicationContext()).setPrinterName(deviceAddress);
            Toast.makeText(AddPrinterActivity.this, "DeviceConnected" + SessionHandler.getInstance(getApplicationContext()).getPrinterName(), Toast.LENGTH_SHORT).show();
        }
    };

    private void closeSocket(BluetoothSocket nOpenSocket) {
        try {
            nOpenSocket.close();
            Log.d(TAG, "SocketClosed");
        } catch (IOException ex) {
            Log.d(TAG, "CouldNotCloseSocket");
        }
    }

    @Override
    public void onBackPressed() {
        try {
            if (mBluetoothSocket != null)
                mBluetoothSocket.close();
        } catch (Exception e) {
            Log.e("Tag", "Exe ", e);
        }
        setResult(RESULT_CANCELED);
        finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        try {
            if (mBluetoothSocket != null)
                mBluetoothSocket.close();
        } catch (Exception e) {
            Log.e("Tag", "Exe ", e);
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        Log.d(TAG, "onSupportNavigateUp");

        //fragmentManager.popBackStack();
        onBackPressed();
        return true;
    }
}
