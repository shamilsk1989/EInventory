package com.alhikmahpro.www.e_inventory.View;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.AppCompatDelegate;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;

import com.alhikmahpro.www.e_inventory.Data.DataContract;
import com.alhikmahpro.www.e_inventory.Data.SessionHandler;
import com.alhikmahpro.www.e_inventory.R;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class DashBoardActivity extends AppCompatActivity {

    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.saleCard)
    CardView saleCard;
    @BindView(R.id.receiptCard)
    CardView receiptCard;
    @BindView(R.id.inventoryCard)
    CardView inventoryCard;
    @BindView(R.id.goodsCard)
    CardView goodsCard;
    @BindView(R.id.checkCard)
    CardView checkCard;

    @BindView(R.id.layBase)
    LinearLayout layBase;
    private static final String TAG = "DashBoardActivity";

    public static String PREF_KEY_GOODS = "key_module_goods";
    public static String PREF_KEY_SALE = "key_module_sales";
    public static String PREF_KEY_RECEIPT = "key_module_receipts";
    public static String PREF_KEY_INVENTORY = "key_module_inventory";
    @BindView(R.id.reportCard)
    CardView reportCard;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dash_board);
        ButterKnife.bind(this);
        setSupportActionBar(toolbar);
//        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
//        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle("Dash Board");
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);
        // readSettings();


    }

    private void readSettings() {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);

        if (sharedPreferences.getBoolean(PREF_KEY_GOODS, false))
            goodsCard.setVisibility(View.VISIBLE);
        else
            goodsCard.setVisibility(View.GONE);
        if (sharedPreferences.getBoolean(PREF_KEY_INVENTORY, false))
            inventoryCard.setVisibility(View.VISIBLE);
        else
            inventoryCard.setVisibility(View.GONE);
        if (sharedPreferences.getBoolean(PREF_KEY_SALE, false))
            saleCard.setVisibility(View.VISIBLE);
        else
            saleCard.setVisibility(View.GONE);
        if (sharedPreferences.getBoolean(PREF_KEY_RECEIPT, false))
            receiptCard.setVisibility(View.VISIBLE);
        else
            receiptCard.setVisibility(View.GONE);
    }

    @OnClick({R.id.saleCard, R.id.receiptCard, R.id.inventoryCard, R.id.goodsCard, R.id.checkCard,R.id.reportCard})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.saleCard:
                Intent intent_sale = new Intent(DashBoardActivity.this, ListSalesActivity.class);
                startActivity(intent_sale);
                break;
            case R.id.receiptCard:
                Intent intent_rec = new Intent(DashBoardActivity.this, ListReceiptActivity.class);
                startActivity(intent_rec);
                break;
            case R.id.inventoryCard:
                Intent intent_inv = new Intent(DashBoardActivity.this, ListDocActivity.class);
                intent_inv.putExtra("Type", "INV");
                startActivity(intent_inv);
                break;
            case R.id.goodsCard:
                Intent intent_gds = new Intent(DashBoardActivity.this, ListDocActivity.class);
                intent_gds.putExtra("Type", "GDS");
                startActivity(intent_gds);
                break;
            case R.id.checkCard:
                Intent checker = new Intent(DashBoardActivity.this, PriceCheckerActivity.class);
                startActivity(checker);
                break;
            case R.id.reportCard:
                Log.d(TAG, "onViewClicked: ");
                Intent report = new Intent(DashBoardActivity.this, ReportActivity.class);
                startActivity(report);
                break;


        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.home_toolbar, menu);

        MenuItem itemSettings = menu.findItem(R.id.action_settings);
        MenuItem itemLogout = menu.findItem(R.id.action_logout);
        if (SessionHandler.getInstance(DashBoardActivity.this).getUser().equals(DataContract.USER)) {
            itemSettings.setVisible(false);
        }


        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection

        int id = item.getItemId();
        if (id == R.id.action_logout) {
            SessionHandler.getInstance(DashBoardActivity.this).resetUser();
            startActivity(new Intent(DashBoardActivity.this, LoginActivity.class));
            finish();
        } else if (id == R.id.action_settings) {
            startActivity(new Intent(DashBoardActivity.this, PrefSettingsActivity.class));
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume() {
        super.onResume();
        readSettings();
    }
}
