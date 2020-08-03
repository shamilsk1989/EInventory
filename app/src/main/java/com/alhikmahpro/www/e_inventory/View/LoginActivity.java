package com.alhikmahpro.www.e_inventory.View;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.widget.EditText;
import android.widget.TextView;

import com.alhikmahpro.www.e_inventory.Data.DataContract;
import com.alhikmahpro.www.e_inventory.Data.SessionHandler;
import com.alhikmahpro.www.e_inventory.Data.dbHelper;
import com.alhikmahpro.www.e_inventory.R;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class LoginActivity extends AppCompatActivity {

    @BindView(R.id.editText)
    EditText editText;
    @BindView(R.id.editText2)
    EditText editText2;
    @BindView(R.id.textView)
    TextView textView;
    @BindView(R.id.cardView)
    CardView cardView;
    @BindView(R.id.textView2)
    TextView textView2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);


    }

//    @OnClick(R.id.buttonLogin)
//    public void onViewClicked() {
//        String password = editPassword.getText().toString();
//        if (password.isEmpty()) {
//            SessionHandler.getInstance(LoginActivity.this).setUser(DataContract.USER);
//            gotoHome();
//            finish();
//        } else {
//            dbHelper helper = new dbHelper(this);
//            boolean res = helper.getLogin(password);
//            if (res) {
//                SessionHandler.getInstance(LoginActivity.this).setUser(DataContract.ADMIN);
//                gotoHome();
//                finish();
//            }
//            else{
//                editPassword.setError("Invalid Password");
//            }
//        }
//    }

    private void gotoHome() {
        startActivity(new Intent(LoginActivity.this, DashBoardActivity.class));
    }

    @OnClick(R.id.cardView)
    public void onViewClicked() {
        String user = editText.getText().toString();
        String password = editText2.getText().toString();
        if (user.equals("Admin")) {
            dbHelper helper = new dbHelper(this);
            boolean res = helper.getLogin(password);
            if (res) {
                SessionHandler.getInstance(LoginActivity.this).setUser(DataContract.ADMIN);
                gotoHome();
                finish();
            } else {
                editText2.setError("Invalid Password");
            }
        }else{
            editText.setError("Invalid User Name");
        }
    }

    @OnClick(R.id.textView2)
    public void textView2Clicked() {
        SessionHandler.getInstance(LoginActivity.this).setUser(DataContract.USER);
        gotoHome();
        finish();
    }


}
