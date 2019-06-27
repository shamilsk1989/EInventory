package com.alhikmahpro.www.e_inventory.View;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.Button;
import android.widget.EditText;

import com.alhikmahpro.www.e_inventory.Data.SessionHandler;
import com.alhikmahpro.www.e_inventory.Data.dbHelper;
import com.alhikmahpro.www.e_inventory.R;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class LoginActivity extends AppCompatActivity {

    @BindView(R.id.editPassword)
    EditText editPassword;
    @BindView(R.id.buttonLogin)
    Button buttonLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);

    }

    @OnClick(R.id.buttonLogin)
    public void onViewClicked() {
        String password = editPassword.getText().toString();
        if (password.isEmpty()) {
            SessionHandler.getInstance(LoginActivity.this).setUser("User");
            gotoHome();
            finish();
        } else {
            dbHelper helper = new dbHelper(this);
            boolean res = helper.getLogin(password);
            if (res) {
                SessionHandler.getInstance(LoginActivity.this).setUser("Admin");
                gotoHome();
                finish();
            }
            else{
                editPassword.setError("Invalid Password");
            }
        }
    }

    private void gotoHome() {
        startActivity(new Intent(LoginActivity.this, HomeActivity.class));
    }
}
