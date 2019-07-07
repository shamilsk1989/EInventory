package com.alhikmahpro.www.e_inventory.View;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.alhikmahpro.www.e_inventory.AppUtils;
import com.alhikmahpro.www.e_inventory.Data.DataContract;
import com.alhikmahpro.www.e_inventory.Data.SessionHandler;
import com.alhikmahpro.www.e_inventory.Interface.volleyListener;
import com.alhikmahpro.www.e_inventory.Network.VolleyServiceGateway;
import com.alhikmahpro.www.e_inventory.Network.VolleySingleton;
import com.alhikmahpro.www.e_inventory.Data.dbHelper;
import com.alhikmahpro.www.e_inventory.R;
import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkError;
import com.android.volley.NoConnectionError;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.ServerError;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class InvoiceActivity extends AppCompatActivity implements ListSupplierFragment.OnCompleteListener {

    @BindView(R.id.editTextUser)
    EditText editTextUser;
    @BindView(R.id.editTextSupplierCode)
    EditText editTextSupplierCode;
    @BindView(R.id.editTextInvoiceNo)
    EditText editTextInvoiceNo;
    @BindView(R.id.txtInvoiceDate)
    TextView txtInvoiceDate;
    @BindView(R.id.btnCheck)
    Button btnCheck;
    @BindView(R.id.editTextOrderNo)
    EditText editTextOrderNo;
    @BindView(R.id.imgSearch)
    ImageView imgSearch;
    @BindView(R.id.imgCalender)
    ImageView imgCalender;

    String companyCode, companyName, deviceId, User, invoiceNo,
            supplierCode, invoiceDate, orderNo, branchCode, periodCode;
    String supplierName = "";
    String BASE_URL = "";
    ProgressDialog progressDialog;
    //ConnectivityManager connectivityManager;
    private static final String TAG = "InvoiceActivity";
    Calendar calendar;

    volleyListener mVolleyListener;
    VolleyServiceGateway serviceGateway;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_invoice);
        ButterKnife.bind(this);



        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle("Check Supplier");

        txtInvoiceDate.setEnabled(false);



        initView();
        initVolleyCallBack();
    }

    private void initVolleyCallBack() {
        mVolleyListener=new volleyListener() {
            @Override
            public void notifySuccess(String requestType, JSONObject response) {

                progressDialog.dismiss();
                boolean status;
                if (response.length() > 0) {

                    try {
                        supplierName = response.getString("SupplierName");
                        btnCheck.setEnabled(true);
                        status=true;
                        showAlert(supplierName,status);

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else {
                    status=false;
                    showAlert("Not Found",status);

                }

            }

            @Override
            public void notifyError(String requestType, VolleyError error) {

                progressDialog.dismiss();
                boolean status=false;
                showAlert("Network error",status);
            }
        };
    }

    private void updateLabel() {
        String myFormat = "dd-MM-yyyy "; //In which you need put here
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);
        txtInvoiceDate.setText(sdf.format(calendar.getTime()));
    }

    private void initView() {

        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
        String mDate = sdf.format(new Date());
        txtInvoiceDate.setText(mDate);
    }


    void getDataFromVolley() {
        Log.d(TAG, "getDataFromVolley: ");
        View view = this.getCurrentFocus();
        AppUtils.hideKeyboard(this, view);
        JSONObject postParam = new JSONObject();
        try {
            postParam.put("SupplierCode", supplierCode);
            postParam.put("InvoiceNo", invoiceNo.toUpperCase());
            postParam.put("OrderNo", orderNo);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        progressDialog = AppUtils.showProgressDialog(this, "Loading....");
        serviceGateway = new VolleyServiceGateway(mVolleyListener, this);
        serviceGateway.postDataVolley("POSTCALL", "PriceChecker/check_supp.php", postParam);
    }


    private void gotoNext() {

        Log.d(TAG, "gotoNext: ");
        Intent intent = new Intent(this, GoodsReceiverActivity.class);
        intent.putExtra("InvoiceNo", invoiceNo);
        intent.putExtra("User", User);
        intent.putExtra("SupplierCode", supplierCode);
        intent.putExtra("SupplierName", supplierName);
        intent.putExtra("Date", invoiceDate);
        intent.putExtra("OrderNo", orderNo);
        startActivity(intent);

    }


    private void showAlert(String Message, final boolean status) {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Response");
        builder.setMessage(Message);
        builder.setCancelable(false);
        builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if(status){
                    gotoNext();
                }
                dialog.cancel();
            }
        }).create().show();
    }

    private boolean validate() {
        if (TextUtils.isEmpty(User)) {
            editTextUser.setError("Invalid User");
            return false;
        } else if (TextUtils.isEmpty(supplierCode)) {
            editTextSupplierCode.setError("Invalid Supplier Code");
            return false;
        } else if (TextUtils.isEmpty(invoiceNo)) {
            editTextInvoiceNo.setError("Invalid Invoice");
            return false;
        }


        return true;
    }


    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }


    @OnClick({R.id.imgSearch, R.id.btnCheck,R.id.imgCalender})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.imgSearch:
                FragmentManager fragmentManager = getSupportFragmentManager();
                ListSupplierFragment listSupplierFragment = new ListSupplierFragment();
                listSupplierFragment.show(fragmentManager, "Supplier");
                break;
            case R.id.btnCheck:
                supplierCode = editTextSupplierCode.getText().toString();
                User = editTextUser.getText().toString();
                invoiceNo = editTextInvoiceNo.getText().toString();
                orderNo = editTextOrderNo.getText().toString();
                invoiceDate = txtInvoiceDate.getText().toString();
                if (validate()) {
                    getDataFromVolley();
                }

                break;
            case R.id.imgCalender:

                calendar=Calendar.getInstance();

                final DatePickerDialog.OnDateSetListener dateSetListener = new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                        calendar.set(Calendar.YEAR, year);
                        calendar.set(Calendar.MONTH, monthOfYear);
                        calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                        updateLabel();

                    }
                };
                new DatePickerDialog(InvoiceActivity.this, dateSetListener, calendar
                        .get(Calendar.YEAR), calendar.get(Calendar.MONTH),
                        calendar.get(Calendar.DAY_OF_MONTH)).show();
                break;

        }
    }

    @Override
    public void onComplete(String code) {

        editTextSupplierCode.setText(code);

    }
}
