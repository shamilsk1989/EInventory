package com.alhikmahpro.www.e_inventory.View;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.LinearLayout;

import com.alhikmahpro.www.e_inventory.R;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class PrinterSettingsActivity extends AppCompatActivity {


    @BindView(R.id.paper_layout)
    LinearLayout paperLayout;
    @BindView(R.id.printer_layout)
    LinearLayout printerLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_printer_settings);
        ButterKnife.bind(this);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle("Printer settings");

    }

    @OnClick(R.id.paper_layout)
    public void onPaperLayoutClicked() {
        Intent intent_paper=new Intent(PrinterSettingsActivity.this,PaperSettingsActivity.class);
        startActivity(intent_paper);

    }

    @OnClick(R.id.printer_layout)
    public void onPrinterLayoutClicked() {
        Intent intent_printer=new Intent(PrinterSettingsActivity.this,AddPrinterActivity.class);
        startActivity(intent_printer);
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}
