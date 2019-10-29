//package com.alhikmahpro.www.e_inventory.View;
//
//import android.content.Context;
//import android.content.Intent;
//import android.os.Vibrator;
//import android.support.v7.app.AppCompatActivity;
//import android.os.Bundle;
//import android.widget.Toast;
//
//import com.alhikmahpro.www.e_inventory.R;
//import com.google.android.gms.common.api.CommonStatusCodes;
//import com.google.zxing.Result;
//
//import me.dm7.barcodescanner.zxing.ZXingScannerView;
//
//public class ZXingBrcodeScanActivity extends AppCompatActivity implements ZXingScannerView.ResultHandler {
//
//
//
//    ZXingScannerView scannerView;
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        scannerView=new ZXingScannerView(this);
//        setContentView(scannerView);
//
//    }
//
//    @Override
//    public void handleResult(Result result) {
//
//
//        Vibrator vibrator=(Vibrator)getApplicationContext().getSystemService(Context.VIBRATOR_SERVICE);
//        vibrator.vibrate(500);
//
////          PriceCheckerActivity.txtBarcode.setText(result.getText());
////          onBackPressed();
//
//        Toast.makeText(this, "Barcode is"+result.getText(), Toast.LENGTH_SHORT).show();
////        Intent intent = new Intent();
////        intent.putExtra("barcode", result.getText());
////        setResult(RESULT_OK, intent);
////        finish();
//
//
//
//
//
//    }
//
//    @Override
//    protected void onStop() {
//        super.onStop();
//        scannerView.stopCamera();
//    }
//
//    @Override
//    protected void onResume() {
//        super.onResume();
//        scannerView.setResultHandler(this);
//        scannerView.startCamera();
//    }
//}
