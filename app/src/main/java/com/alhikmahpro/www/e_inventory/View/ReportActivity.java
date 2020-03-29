package com.alhikmahpro.www.e_inventory.View;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.alhikmahpro.www.e_inventory.Data.CreatePdf;
import com.alhikmahpro.www.e_inventory.Data.DataContract;
import com.alhikmahpro.www.e_inventory.R;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class ReportActivity extends AppCompatActivity {

    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.txtFromDate)
    TextView txtFromDate;
    @BindView(R.id.imgCalenderFrom)
    ImageView imgCalenderFrom;
    @BindView(R.id.txtToDate)
    TextView txtToDate;
    @BindView(R.id.imgCalenderTo)
    ImageView imgCalenderTo;
    @BindView(R.id.radioInvoice)
    RadioButton radioInvoice;
    @BindView(R.id.radioReceipt)
    RadioButton radioReceipt;
    @BindView(R.id.radioGroup)
    RadioGroup radioGroup;
    @BindView(R.id.btn_generate)
    Button btnGenerate;
    RadioButton radioButton;
    Calendar calendar;
    
    String Type;
    private static final String TAG = "ReportActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report);
        ButterKnife.bind(this);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        getSupportActionBar().setTitle("Reports");

        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
        String mDate = sdf.format(new Date());
        txtToDate.setText(mDate);
        txtFromDate.setText(mDate);
        int radioId = radioGroup.getCheckedRadioButtonId();
        radioButton = findViewById(radioId);
        Type = radioButton.getText().toString();

    }
    private void updateFromDate() {
        String myFormat = "dd-MM-yyyy "; //In which you need put here
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);
        txtFromDate.setText(sdf.format(calendar.getTime()));
    }
    private void updateToDate() {
        String myFormat = "dd-MM-yyyy "; //In which you need put here
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);
        txtToDate.setText(sdf.format(calendar.getTime()));
    }

    @OnClick({R.id.imgCalenderFrom, R.id.imgCalenderTo, R.id.btn_generate})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.imgCalenderFrom:
                calendar = Calendar.getInstance();

                final DatePickerDialog.OnDateSetListener dateSetListener = new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                        calendar.set(Calendar.YEAR, year);
                        calendar.set(Calendar.MONTH, monthOfYear);
                        calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                        updateFromDate();
                    }
                };
                new DatePickerDialog(ReportActivity.this, dateSetListener, calendar
                        .get(Calendar.YEAR), calendar.get(Calendar.MONTH),
                        calendar.get(Calendar.DAY_OF_MONTH)).show();
                break;

            case R.id.imgCalenderTo:
                final DatePickerDialog.OnDateSetListener dateSetListener1 = new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                        calendar.set(Calendar.YEAR, year);
                        calendar.set(Calendar.MONTH, monthOfYear);
                        calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                        updateToDate();
                    }
                };
                new DatePickerDialog(ReportActivity.this, dateSetListener1, calendar
                        .get(Calendar.YEAR), calendar.get(Calendar.MONTH),
                        calendar.get(Calendar.DAY_OF_MONTH)).show();
                break;
            case R.id.btn_generate:
                createPdfWrapper();
                break;
        }
    }

    public void onRadioButtonClicked(View view) {

        int radioId = radioGroup.getCheckedRadioButtonId();
        Log.d(TAG, "onRadioButtonClicked: " + radioId);
        radioButton = findViewById(radioId);
        Type = radioButton.getText().toString();
        Log.d(TAG, "onRadioButtonClicked: " + Type);
    }

    private void createPdfWrapper() {
        Log.d(TAG, "createPdfWrapper: ");
        //CreatePdf createPdf = new CreatePdf(this,txtFromDate.getText().toString(),txtToDate.getText().toString(),Type );
//        createPdf.execute();
        Intent intent_rec = new Intent(ReportActivity.this, ReportViewActivity.class);
        intent_rec.putExtra("TYPE", Type);
        intent_rec.putExtra("FROM_DATE", txtFromDate.getText().toString());
        intent_rec.putExtra("TO_DATE", txtToDate.getText().toString());
        startActivity(intent_rec);

    }
    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}
