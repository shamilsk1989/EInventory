package com.alhikmahpro.www.e_inventory.View;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.alhikmahpro.www.e_inventory.Data.dbHelper;
import com.alhikmahpro.www.e_inventory.R;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class ClearDataActivity extends AppCompatActivity {

    @BindView(R.id.textInvoice)
    TextView textInvoice;
    @BindView(R.id.textReceipt)
    TextView textReceipt;
    @BindView(R.id.paper_layout)
    LinearLayout paperLayout;
    @BindView(R.id.layoutReceipt)
    LinearLayout layoutReceipt;
    @BindView(R.id.toolbar)
    Toolbar toolbar;

//    AlertDialog alertDialog;
//    AlertDialog.Builder builder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_clear_data);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        toolbar.setTitle("Clear Data");
        ButterKnife.bind(this);
    }

    @OnClick({R.id.textInvoice, R.id.textReceipt, R.id.paper_layout, R.id.layoutReceipt})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.textInvoice:
                alertDialog("Do you want to delete all Invoices?", "inv");
                break;
            case R.id.textReceipt:
                alertDialog("Do you want to delete all Receipts?", "rec");
                break;
            case R.id.paper_layout:
                alertDialog("Do you want to delete all Invoices?", "inv");
                break;
            case R.id.layoutReceipt:
                alertDialog("Do you want to delete all Receipts?", "rec");
                break;
        }
    }

    private void alertDialog(String msg, String type) {

        new AlertDialog.Builder(ClearDataActivity.this)
                .setTitle("Warning")
                .setMessage(msg)
                .setPositiveButton("Yes", (dialog, which) -> {
                    ShowProgressDialog();
                    dbHelper helper = new dbHelper(this);
                    if (type.equals("inv")) {
                        helper.deleteInvoice();
                        helper.deleteInvoiceDetails();
                        //Toast.makeText(ClearDataActivity.this, "Deleted", Toast.LENGTH_SHORT).show();

                    } else if (type.equals("rec")) {
                        helper.deleteReceipt();
                        // Toast.makeText(ClearDataActivity.this, "Deleted", Toast.LENGTH_SHORT).show();
                    }


                })
                .setNegativeButton("No", (dialog, which) -> dialog.cancel()).create().show();

    }

    public void ShowProgressDialog() {

        final ProgressDialog dialog = ProgressDialog.show(this, "Deleting", "Please wait....", true);
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(5000);
                    dialog.dismiss();
                } catch (InterruptedException ex) {
                    ex.printStackTrace();
                }
            }
        }).start();


    }
    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }



//    public void HideProgressDialog() {
//
//        alertDialog.dismiss();
//    }

}
