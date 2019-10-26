package com.alhikmahpro.www.e_inventory.View;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.Toast;

import com.alhikmahpro.www.e_inventory.AppUtils;
import com.alhikmahpro.www.e_inventory.Data.DataContract;
import com.alhikmahpro.www.e_inventory.Data.SessionHandler;
import com.alhikmahpro.www.e_inventory.Data.dbHelper;
import com.alhikmahpro.www.e_inventory.R;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import de.hdodenhof.circleimageview.CircleImageView;

public class SettingsActivity extends AppCompatActivity {

    @BindView(R.id.txtCompanyName)
    EditText txtCompanyName;
    @BindView(R.id.txtCompanyCode)
    EditText txtCompanyCode;
    @BindView(R.id.txtBranchCode)
    EditText txtBranchCode;
    @BindView(R.id.txtLocationCode)
    EditText txtLocationCode;
    @BindView(R.id.txtPeriodCode)
    EditText txtPeriodCode;
    @BindView(R.id.txtDeviceId)
    EditText txtDeviceId;
    @BindView(R.id.txtHost)
    EditText txtHost;


    @BindView(R.id.switch_inventory)
    Switch switchInventory;

    @BindView(R.id.switch_goods)
    Switch switchGoods;

    @BindView(R.id.switch_receipt)
    Switch switchReceipt;
    @BindView(R.id.imgLogo)
    CircleImageView imgLogo;
    @BindView(R.id.btnSave)
    Button btnSave;


    private static final String TAG = "SettingsActivity";
    boolean goods = false, inv = false, sale = false;
    @BindView(R.id.switch_sale)
    Switch switchSale;

    dbHelper helper;
    int is_sale = 0, is_gds = 0, is_inv = 0, is_rec = 0;
    public static final int REQUEST_ID_MULTIPLE_PERMISSIONS = 1;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    private int GALLERY = 1, CAMERA = 2;
    Bitmap thumbnail;
    int _id = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        ButterKnife.bind(this);
        helper = new dbHelper(this);
        setSupportActionBar(toolbar);
        toolbar.setTitle("Settings");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);


        SQLiteDatabase sqLiteDatabase = helper.getReadableDatabase();
        Cursor cursor = helper.getSettings(sqLiteDatabase);
        if (cursor.moveToFirst()) {

            _id = cursor.getInt(cursor.getColumnIndex(DataContract.Settings.COL_ID));
            txtCompanyName.setText(cursor.getString(cursor.getColumnIndex(DataContract.Settings.COL_COMPANY_NAME)));
            txtCompanyCode.setText(cursor.getString(cursor.getColumnIndex(DataContract.Settings.COL_COMPANY_CODE)));
            txtBranchCode.setText(cursor.getString(cursor.getColumnIndex(DataContract.Settings.COL_BRANCH_CODE)));
            txtLocationCode.setText(cursor.getString(cursor.getColumnIndex(DataContract.Settings.COL_LOCATION_CODE)));
            txtPeriodCode.setText(cursor.getString(cursor.getColumnIndex(DataContract.Settings.COL_PERIOD_CODE)));
            txtDeviceId.setText(cursor.getString(cursor.getColumnIndex(DataContract.Settings.COL_DEVICE_ID)));
            is_inv = cursor.getInt(cursor.getColumnIndex(DataContract.Settings.COL_IS_INV));
            is_gds = cursor.getInt(cursor.getColumnIndex(DataContract.Settings.COL_IS_GDS));
            is_sale = cursor.getInt(cursor.getColumnIndex(DataContract.Settings.COL_IS_SALE));
            is_rec = cursor.getInt(cursor.getColumnIndex(DataContract.Settings.COL_IS_REC));
            byte[] img = cursor.getBlob(cursor.getColumnIndex(DataContract.Settings.COL_LOGO));

            //get url from shared prefernce
            txtHost.setText(SessionHandler.getInstance(SettingsActivity.this).getHost());

            Log.d(TAG, "Logo" + img.toString());

            try {
                Bitmap bitmap = BitmapFactory.decodeByteArray(img, 0, img.length);
                imgLogo.setImageBitmap(Bitmap.createScaledBitmap(bitmap, 60, 60, false));
            } catch (Exception e) {
                e.printStackTrace();
            }

            if (is_sale == DataContract.SALE_ON) {
                switchSale.setChecked(true);
            }
            if (is_gds == DataContract.GDS_ON) {
                switchGoods.setChecked(true);
            }
            if (is_inv == DataContract.INV_ON) {
                switchInventory.setChecked(true);
            }
            if (is_rec == DataContract.REC_ON) {
                switchReceipt.setChecked(true);
            }


        }
        cursor.close();
        sqLiteDatabase.close();


        switchInventory.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (switchInventory.isChecked()) {
                    is_inv = DataContract.INV_ON;

                } else {
                    is_inv = DataContract.INV_OFF;
                }

                Log.d(TAG, "Inventory onCheckedChanged: " + is_inv);
            }
        });


        switchGoods.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (switchGoods.isChecked()) {

                    is_gds = DataContract.GDS_ON;

                } else {
                    is_gds = DataContract.GDS_OFF;
                }

                Log.d(TAG, "Goods onCheckedChanged: " + is_gds);

            }
        });
        switchSale.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (switchSale.isChecked()) {

                    is_sale = DataContract.SALE_ON;
                } else {

                    is_sale = DataContract.SALE_OFF;
                }
                Log.d(TAG, "Sale onCheckedChanged: " + is_sale);

            }
        });

        switchReceipt.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (switchReceipt.isChecked()) {
                    is_rec = DataContract.REC_ON;
                } else {
                    is_rec = DataContract.REC_OFF;
                }
                Log.d(TAG, "Receipt onCheckedChanged: " + is_rec);

            }
        });

    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    private void closeKey() {
        View view = this.getCurrentFocus();
        AppUtils.hideKeyboard(this, view);

    }


    private void initValues() {


    }


    private boolean validate(String code, String ip) {


        if (TextUtils.isEmpty(code)) {
            txtBranchCode.setError("Invalid name");
            return false;

        } else if (TextUtils.isEmpty(ip)) {
            txtHost.setError("Invalid ip");
            return false;

        }
        return true;

    }


    @OnClick(R.id.imgLogo)
    public void onImgLogoClicked() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkAndRequestPermissions()) {
                showOptions();
            }
        } else {
            showOptions();
        }
    }

    @OnClick(R.id.btnSave)
    public void onBtnSaveClicked() {
        Log.d(TAG, "onBtnSaveClicked: Sale:" + is_sale + "inventory:" + is_inv + "goods: " + is_gds);
        closeKey();
        String c_name = txtCompanyName.getText().toString();
        String c_code = txtCompanyCode.getText().toString();
        String b_code = txtBranchCode.getText().toString();
        String l_code = txtLocationCode.getText().toString();
        String p_code = txtPeriodCode.getText().toString();
        String device = txtDeviceId.getText().toString();
        String url = txtHost.getText().toString();
        imgLogo.setDrawingCacheEnabled(true);
        imgLogo.buildDrawingCache();
        Bitmap bitmap = imgLogo.getDrawingCache();
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
        byte[] img_data = stream.toByteArray();
        if (validate(c_code, url)) {
            //save url to shared preference

            SessionHandler.getInstance(this).setHost(url);
            // check settings table if data is there; update else save setting details to DB
            if (_id > 0) {
                helper.updateSettings(_id, c_code, c_name, b_code, l_code, p_code, device, img_data, is_sale, is_inv, is_gds, is_rec);
            } else {
                helper.saveSettings(c_code, c_name, b_code, l_code, p_code, device, img_data, is_sale, is_inv, is_gds, is_rec);
            }
            Toast.makeText(this, "Saved", Toast.LENGTH_SHORT).show();
            finish();
        }

    }


    private boolean checkAndRequestPermissions() {

        int cameraPermission = ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA);
        int writePermission = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        int readPermission = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE);
        List<String> listPermissionsNeeded = new ArrayList<>();
        if (cameraPermission != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.CAMERA);
        }
        if (writePermission != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
        }
        if (readPermission != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.READ_EXTERNAL_STORAGE);
        }
        if (!listPermissionsNeeded.isEmpty()) {
            ActivityCompat.requestPermissions(this, listPermissionsNeeded.toArray(new String[listPermissionsNeeded.size()]), REQUEST_ID_MULTIPLE_PERMISSIONS);
            return false;
        }

        return true;

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case REQUEST_ID_MULTIPLE_PERMISSIONS:
                Map<String, Integer> perms = new HashMap<>();
                perms.put(Manifest.permission.READ_EXTERNAL_STORAGE, PackageManager.PERMISSION_GRANTED);
                perms.put(Manifest.permission.WRITE_EXTERNAL_STORAGE, PackageManager.PERMISSION_GRANTED);

                if (grantResults.length > 0) {
                    for (int i = 0; i < permissions.length; i++)
                        perms.put(permissions[i], grantResults[i]);

                    //check all permissions
                    if (perms.get(Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED &&
                            perms.get(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {

                        Log.d(TAG, "permission granted ");
                        showOptions();
                    } else {
                        if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) ||

                                ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.READ_EXTERNAL_STORAGE)) {

                            new AlertDialog.Builder(SettingsActivity.this)
                                    .setTitle("Permission needed")
                                    .setMessage("To continue please allow the permission ")
                                    .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            checkAndRequestPermissions();
                                        }
                                    })
                                    .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                        @Override

                                        public void onClick(DialogInterface dialog, int which) {
                                            dialog.cancel();
                                        }
                                    }).create().show();

                        } else {
                            Toast.makeText(this, "permission denied", Toast.LENGTH_SHORT).show();
                        }
                    }
                }

        }


    }

    private void showOptions() {
        AlertDialog.Builder pictureDialog = new AlertDialog.Builder(this);
        pictureDialog.setTitle("Select Action");
        String[] pictureDialogItems = {
                "Select photo from gallery",
                "Capture photo from camera"};
        pictureDialog.setItems(pictureDialogItems,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which) {
                            case 0:
                                choosePhotoFromGallery();
                                break;
                            case 1:
                                takePhotoFromCamera();
                                break;
                        }
                    }
                });
        pictureDialog.show();
    }

    private void takePhotoFromCamera() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(intent, CAMERA);

    }

    private void choosePhotoFromGallery() {
        Intent galleryIntent = new Intent(Intent.ACTION_PICK,
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(galleryIntent, GALLERY);


    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_CANCELED) {
            return;
        }
        if (requestCode == GALLERY) {
            if (data != null) {
                final Uri contentURI = data.getData();

                if (contentURI != null) {

                    try {
                        final InputStream inputStream = this.getContentResolver().openInputStream(contentURI);
                        final Bitmap selectedImage = BitmapFactory.decodeStream(inputStream);
                        imgLogo.setImageBitmap(selectedImage);

                    } catch (IOException e) {
                        e.printStackTrace();
                        Toast.makeText(this, "Failed!", Toast.LENGTH_SHORT).show();
                    }
                }

            }

        } else if (requestCode == CAMERA) {

            thumbnail = (Bitmap) data.getExtras().get("data");
            imgLogo.setMaxWidth(100);
            imgLogo.setImageBitmap(thumbnail);

        }
    }

}
