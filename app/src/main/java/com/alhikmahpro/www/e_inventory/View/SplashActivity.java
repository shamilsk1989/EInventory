package com.alhikmahpro.www.e_inventory.View;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import com.alhikmahpro.www.e_inventory.Data.AutoIdModel;
import com.alhikmahpro.www.e_inventory.Data.DataContract;
import com.alhikmahpro.www.e_inventory.Data.SessionHandler;
import com.alhikmahpro.www.e_inventory.Data.dbHelper;
import com.alhikmahpro.www.e_inventory.R;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import butterknife.BindView;
import butterknife.ButterKnife;

public class SplashActivity extends AppCompatActivity {

    private static final String DEFAULT_ADMIN_PASSWORD = "sysadmin";
    private static final String TAG = "SplashActivity";
    @BindView(R.id.image)
    ImageView image;
    @BindView(R.id.txtView)
    TextView txtView;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_splash);
        ButterKnife.bind(this);

        Animation animation= AnimationUtils.loadAnimation(this,R.anim.splashanim);
        image.setAnimation(animation);
        txtView.setAnimation(animation);
        ArrayList<AutoIdModel>idModels=new ArrayList<>();
        String[]tableName={DataContract.Stocks.TABLE_NAME,DataContract.Order.TABLE_NAME,DataContract.Invoice.TABLE_NAME,DataContract.GoodsReceive.TABLE_NAME};
        dbHelper helper=new dbHelper(this);
        if(!SessionHandler.getInstance(SplashActivity.this).isAppFirstTime()){
            Log.d(TAG, "App running first time: ");
            helper.saveLogin(DEFAULT_ADMIN_PASSWORD);
//            for(int i=0;i<tableName.length;i++){
//                AutoIdModel model=new AutoIdModel();
//                model.setTableName(tableName[i]);
//                model.setId(0);
//                idModels.add(model);
//            }
            helper.saveAutoGeneratorId(0,0,0,0);
            SessionHandler.getInstance(SplashActivity.this).setAppFirstTime(true);
        }
        moveToNextScreen();

    }


    public void moveToNextScreen() {
        final Timer timer = new Timer();
        TimerTask splashTask = new TimerTask() {
            @Override
            public void run() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                        if (SessionHandler.getInstance(SplashActivity.this).isUserLoggedIn()) {
                            gotoHome();
                        } else {
                            gotoLoginScreen();
                        }
                        finish();
                    }
                });
            }
        };

        timer.schedule(splashTask, 2600);
    }

    private void gotoHome() {
        startActivity(new Intent(SplashActivity.this, DashBoardActivity.class));
    }

    private void gotoLoginScreen() {

        startActivity(new Intent(SplashActivity.this, LoginActivity.class));
    }


}
