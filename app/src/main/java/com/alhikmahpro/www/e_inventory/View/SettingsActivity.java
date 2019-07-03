package com.alhikmahpro.www.e_inventory.View;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.alhikmahpro.www.e_inventory.AppUtils;
import com.alhikmahpro.www.e_inventory.Data.DataContract;
import com.alhikmahpro.www.e_inventory.Data.SessionHandler;
import com.alhikmahpro.www.e_inventory.Data.dbHelper;
import com.alhikmahpro.www.e_inventory.R;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class SettingsActivity extends AppCompatActivity {

    @BindView(R.id.txtCompanyName)
    EditText txtCompanyName;
    @BindView(R.id.txtCompanyCode)
    EditText txtCompanyCode;
    @BindView(R.id.txtBranchCode)
    EditText txtBranchCode;
    @BindView(R.id.txtLocationCode)
    EditText txtLocationCode;
    @BindView(R.id.txtPeriodCode)
    EditText txtPeriodCode;
    @BindView(R.id.txtDeviceId)
    EditText txtDeviceId;
    @BindView(R.id.txtHost)
    EditText txtHost;
    @BindView(R.id.content_layout)
    LinearLayout contentLayout;
//    @BindView(R.id.txt_inventory)
//    TextView txtInventory;
    @BindView(R.id.switch_inventory)
    Switch switchInventory;
//    @BindView(R.id.txt_goods)
//    TextView txtGoods;
    @BindView(R.id.switch_goods)
    Switch switchGoods;
//    @BindView(R.id.txt_empty)
//    TextView txtEmpty;
    @BindView(R.id.btnSearch)
    Button btnSearch;
    dbHelper helper;

    private static final String TAG = "SettingsActivity";
    boolean goods = false, inv = false,sale=false;
    @BindView(R.id.switch_sale)
    Switch switchSale;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        ButterKnife.bind(this);
        helper = new dbHelper(this);
        getSupportActionBar().setTitle("Settings");

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);


        if (SessionHandler.getInstance(SettingsActivity.this).isSetInventory()) {
            Log.i("menu", "inventory on");
            switchInventory.setChecked(true);
            inv = true;

        }

        if (SessionHandler.getInstance(SettingsActivity.this).isSetGoodsReceive()) {
            Log.i("menu", "goods receive  on");
            switchGoods.setChecked(true);
            goods = true;

        }
        if (SessionHandler.getInstance(SettingsActivity.this).isSetSale()) {
            Log.i("menu", "sale  on");
            switchSale.setChecked(true);
            goods = true;

        }


        switchInventory.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (switchInventory.isChecked()) {
                    // Toast.makeText(SettingsActivity.this, "inventory activated", Toast.LENGTH_SHORT).show();
                    //SessionHandler.getInstance(SettingsActivity.this).setInventory(true);
                    inv = true;

                } else {
                    // SessionHandler.getInstance(SettingsActivity.this).resetInventory();
                    inv = false;
                    //Toast.makeText(SettingsActivity.this, "inventory disabled", Toast.LENGTH_SHORT).show();
                }
            }
        });


        switchGoods.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (switchInventory.isChecked()) {
                    Toast.makeText(SettingsActivity.this, "goods receive activated", Toast.LENGTH_SHORT).show();
                    //SessionHandler.getInstance(SettingsActivity.this).setGoodsReceive(true);
                    goods = true;

                } else {
                    //SessionHandler.getInstance(SettingsActivity.this).resetGoodsReceive();
                    goods = false;
                    //  Toast.makeText(SettingsActivity.this, "goods receive disabled", Toast.LENGTH_SHORT).show();
                }

            }
        });
        switchSale.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(switchSale.isChecked()){
                    sale=true;
                }else {
                    sale=false;
                }

            }
        });
        initValues();
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @OnClick(R.id.btnSearch)
    public void onViewClicked() {
        closeKey();
        String c_name = txtCompanyName.getText().toString();
        String c_code = txtCompanyCode.getText().toString();
        String b_code = txtBranchCode.getText().toString();
        String l_code = txtLocationCode.getText().toString();
        String p_code = txtPeriodCode.getText().toString();
        String device = txtDeviceId.getText().toString();
        String ip = txtHost.getText().toString();
        if (validate(c_code, ip)) {

            helper.saveSettings(c_code, c_name, b_code, l_code, p_code, device);
            SessionHandler.getInstance(this).setHost(ip);
            if (goods) {
                SessionHandler.getInstance(SettingsActivity.this).setGoodsReceive(true);
                // Toast.makeText(SettingsActivity.this, "Goods Receive activated", Toast.LENGTH_SHORT).show();
                Log.i("menu", "Goods Receive on");
            } else {
                SessionHandler.getInstance(SettingsActivity.this).resetGoodsReceive();
                //Toast.makeText(SettingsActivity.this, "Goods Receive disabled", Toast.LENGTH_SHORT).show();
                Log.i("menu", "Goods Receive off");
            }

            if (inv) {
                SessionHandler.getInstance(SettingsActivity.this).setInventory(true);
                //Toast.makeText(SettingsActivity.this, "inventory activated", Toast.LENGTH_SHORT).show();
                Log.i("menu", "Inventory on");
            } else {
                SessionHandler.getInstance(SettingsActivity.this).resetInventory();
                //Toast.makeText(SettingsActivity.this, "inventory disabled", Toast.LENGTH_SHORT).show();
                Log.i("menu", "Inventory off");
            }
            if (sale) {
                SessionHandler.getInstance(SettingsActivity.this).setSale(true);
                //Toast.makeText(SettingsActivity.this, "inventory activated", Toast.LENGTH_SHORT).show();
                Log.i("menu", "Sale on");
            } else {
                SessionHandler.getInstance(SettingsActivity.this).resetSale();
                //Toast.makeText(SettingsActivity.this, "inventory disabled", Toast.LENGTH_SHORT).show();
                Log.i("menu", "Sale off");
            }
            Toast.makeText(this, "Saved", Toast.LENGTH_SHORT).show();
            finish();
        }
    }


    private void closeKey() {
        View view = this.getCurrentFocus();
        AppUtils.hideKeyboard(this,view);
//        if (view != null) {
//            InputMethodManager methodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
//            methodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
//        }
    }


    private void initValues() {


        Log.d(TAG, "initView: ");
        SQLiteDatabase sqLiteDatabase = helper.getReadableDatabase();
        Cursor cursor = helper.getSettings(sqLiteDatabase);
        if (cursor.moveToFirst()) {


            txtCompanyName.setText(cursor.getString(cursor.getColumnIndex(DataContract.Settings.COL_COMPANY_NAME)));
            txtCompanyCode.setText(cursor.getString(cursor.getColumnIndex(DataContract.Settings.COL_COMPANY_CODE)));
            txtBranchCode.setText(cursor.getString(cursor.getColumnIndex(DataContract.Settings.COL_BRANCH_CODE)));
            txtLocationCode.setText(cursor.getString(cursor.getColumnIndex(DataContract.Settings.COL_LOCATION_CODE)));
            txtPeriodCode.setText(cursor.getString(cursor.getColumnIndex(DataContract.Settings.COL_PERIOD_CODE)));
            txtDeviceId.setText(cursor.getString(cursor.getColumnIndex(DataContract.Settings.COL_DEVICE_ID)));
            txtHost.setText(SessionHandler.getInstance(SettingsActivity.this).getHost());


        }
        cursor.close();
        sqLiteDatabase.close();
    }


    private boolean validate(String code, String ip) {


        if (TextUtils.isEmpty(code)) {
            txtBranchCode.setError("Invalid name");
            return false;

        } else if (TextUtils.isEmpty(ip)) {
            txtHost.setError("Invalid ip");
            return false;

        }
        return true;

    }
}
