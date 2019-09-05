package com.alhikmahpro.www.e_inventory.View;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.alhikmahpro.www.e_inventory.Data.dbHelper;
import com.alhikmahpro.www.e_inventory.R;

import java.text.SimpleDateFormat;
import java.util.Date;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class ReceiptActivity extends AppCompatActivity {

    @BindView(R.id.txt_date)
    TextView txtDate;
    @BindView(R.id.txt_salesman)
    TextView txtSalesman;
    @BindView(R.id.editTextBarcode)
    EditText editTextBarcode;
    @BindView(R.id.imgBarcode)
    ImageView imgBarcode;
    @BindView(R.id.imgSearch)
    ImageView imgSearch;
    @BindView(R.id.imgSubmit)
    ImageView imgSubmit;
    @BindView(R.id.textViewCustomerName)
    TextView textViewCustomerName;
    @BindView(R.id.editTextBalance)
    EditText editTextBalance;
    @BindView(R.id.editTextAmount)
    EditText editTextAmount;
    @BindView(R.id.radioSale)
    RadioButton radioSale;
    @BindView(R.id.radioReturn)
    RadioButton radioReturn;
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
    @BindView(R.id.btnNext)
    Button btnNext;

    String mDate;
    int mDoc;
    private static final String TAG = "ReceiptActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_receipt);
        ButterKnife.bind(this);
        editTextBalance.setEnabled(false);

        initView();
    }

    private void initView() {
        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
        dbHelper helper = new dbHelper(this);
        mDate = sdf.format(new Date());
        int last_no = helper.getLastReceiptNo();
        Log.d(TAG, "setDoc: " + last_no);
        mDoc = last_no + 1;
        txtDate.setText(mDate);
        txtSalesman.setText(String.valueOf(mDoc));
    }

    @OnClick({R.id.imgBarcode, R.id.imgSearch, R.id.imgSubmit, R.id.editTextChequeDate, R.id.btnNext})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.imgBarcode:
                break;
            case R.id.imgSearch:
                break;
            case R.id.imgSubmit:
                break;
            case R.id.editTextChequeDate:
                break;
            case R.id.btnNext:
                break;
        }
    }
}
