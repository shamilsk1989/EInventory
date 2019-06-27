package com.alhikmahpro.www.e_inventory.View;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.alhikmahpro.www.e_inventory.Data.DataContract;
import com.alhikmahpro.www.e_inventory.Data.dbHelper;
import com.alhikmahpro.www.e_inventory.R;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class PaperSettingsActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    @BindView(R.id.txtCompanyName)
    EditText txtCompanyName;
    @BindView(R.id.txtCompanyAddress)
    EditText txtCompanyAddress;
    @BindView(R.id.txtCompanyPhone)
    EditText txtCompanyPhone;
    @BindView(R.id.spinner)
    Spinner spinner;
    @BindView(R.id.btnNext)
    Button btnNext;
    @BindView(R.id.txtFooter)
    EditText txtFooter;
    String companyName, companyAddress, companyPhone, selectedPaper, footer;
    ArrayAdapter<CharSequence> adapter;
    dbHelper helper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_paper_settings);
        ButterKnife.bind(this);
        helper= new dbHelper(this);
        initView();
    }

    private void initView() {
        SQLiteDatabase database=helper.getReadableDatabase();
        Cursor cursor=helper.getPaperSettings(database);
        if(cursor.moveToFirst()){
            do{
                txtCompanyName.setText(cursor.getString(cursor.getColumnIndex(DataContract.PaperSettings.COL_COMPANY_NAME)));
                txtCompanyAddress.setText(cursor.getString(cursor.getColumnIndex(DataContract.PaperSettings.COL_COMPANY_ADDRESS)));
                txtCompanyPhone.setText(cursor.getString(cursor.getColumnIndex(DataContract.PaperSettings.COL_COMPANY_PHONE)));
                txtFooter.setText(cursor.getString(cursor.getColumnIndex(DataContract.PaperSettings.COL_FOOTER)));

            }while (cursor.moveToNext());
        }
        adapter = ArrayAdapter.createFromResource(this, R.array.paper_size, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(this);

    }

    @OnClick(R.id.btnNext)
    public void onViewClicked() {

        companyName = txtCompanyName.getText().toString();
        companyAddress = txtCompanyAddress.getText().toString();
        companyPhone = txtCompanyPhone.getText().toString();
        footer = txtFooter.getText().toString();

        if (Validate(companyPhone, companyAddress, companyName)) {
            if(helper.savePaperSettings(companyName, companyAddress, companyPhone, footer, selectedPaper)){
                Toast.makeText(this, "Inserted", Toast.LENGTH_SHORT).show();
                finish();
            }


        }

    }

    private boolean Validate(String companyPhone, String companyAddress, String companyName) {
        if (TextUtils.isEmpty(companyName)) {
            txtCompanyName.setError("invalid name");
            return false;
        } else if (TextUtils.isEmpty(companyAddress)) {
            txtCompanyAddress.setError("invalid address");
            return false;
        } else if (TextUtils.isEmpty(companyPhone)) {
            txtCompanyPhone.setError("invalid phone number");
            return false;
        }
        return true;
    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

        selectedPaper = adapterView.getItemAtPosition(i).toString();

    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }
}
