package com.alhikmahpro.www.e_inventory.View;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;

import com.alhikmahpro.www.e_inventory.R;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class IdGeneratorActivity extends AppCompatActivity {

    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.editTextInventory)
    EditText editTextInventory;
    @BindView(R.id.editTextGoods)
    EditText editTextGoods;
    @BindView(R.id.editTextSales)
    EditText editTextSales;
    @BindView(R.id.editTextOrder)
    EditText editTextOrder;
    @BindView(R.id.btnSave)
    Button btnSave;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_auto_id_settings);
        ButterKnife.bind(this);

    }

    @OnClick(R.id.btnSave)
    public void onViewClicked() {
        if(TextUtils.isEmpty(editTextGoods.getText())){
            editTextGoods.setError("Invalid Number");
        }else if(TextUtils.isEmpty(editTextInventory.getText())){
            editTextInventory.setError("Invalid Number");
        }else if(TextUtils.isEmpty(editTextOrder.getText())){
            editTextOrder.setError("Invalid Number");
        }else if(TextUtils.isEmpty(editTextSales.getText())){
            editTextSales.setError("Invalid Number");
        }else{


        }

    }
}
