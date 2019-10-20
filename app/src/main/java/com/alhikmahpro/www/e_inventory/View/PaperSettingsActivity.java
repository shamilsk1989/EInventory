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
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
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
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;

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
    int _id=0;
    ArrayAdapter<CharSequence> adapter;
    public static final int REQUEST_ID_MULTIPLE_PERMISSIONS = 1;
    private int GALLERY = 1, CAMERA = 2;
    Bitmap thumbnail;
    @BindView(R.id.imgLogo)
    CircleImageView imgLogo;
    dbHelper helper;
    private static final String TAG = "PaperSettingsActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_paper_settings);
        ButterKnife.bind(this);
        helper= new dbHelper(this);
        getSupportActionBar().setTitle("Clear Data");

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        initView();
    }

    private void initView() {
        SQLiteDatabase database=helper.getReadableDatabase();
        Cursor cursor=helper.getPaperSettings(database);
        if(cursor.moveToFirst()){
            do{
                //_id=cursor.getInt(cursor.getColumnIndex(DataContract.PaperSettings.COL_ID));
                txtCompanyName.setText(cursor.getString(cursor.getColumnIndex(DataContract.PaperSettings.COL_COMPANY_NAME)));
                txtCompanyAddress.setText(cursor.getString(cursor.getColumnIndex(DataContract.PaperSettings.COL_COMPANY_ADDRESS)));
                txtCompanyPhone.setText(cursor.getString(cursor.getColumnIndex(DataContract.PaperSettings.COL_COMPANY_PHONE)));
                txtFooter.setText(cursor.getString(cursor.getColumnIndex(DataContract.PaperSettings.COL_FOOTER)));
                byte[] img = cursor.getBlob(cursor.getColumnIndex(DataContract.Settings.COL_LOGO));
                try {
                    Bitmap bitmap = BitmapFactory.decodeByteArray(img, 0, img.length);
                    imgLogo.setImageBitmap(Bitmap.createScaledBitmap(bitmap, 60, 60, false));
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }while (cursor.moveToNext());
        }
        adapter = ArrayAdapter.createFromResource(this, R.array.paper_size, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(this);

    }
    @OnClick(R.id.imgLogo)
    public void onImgLogoClicked() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//            if (checkAndRequestPermissions()) {
//                showOptions();
//            }
            requestStoragePermission();
        } else {
            showOptions();
        }
    }


    @OnClick(R.id.btnNext)
    public void onViewClicked() {

        companyName = txtCompanyName.getText().toString();
        companyAddress = txtCompanyAddress.getText().toString();
        companyPhone = txtCompanyPhone.getText().toString();
        footer = txtFooter.getText().toString();
        imgLogo.setDrawingCacheEnabled(true);
        imgLogo.buildDrawingCache();
        Bitmap bitmap = imgLogo.getDrawingCache();
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
        byte[] img_data = stream.toByteArray();

        if (Validate(companyPhone, companyAddress, companyName)) {
            // delete previous records  from paper settings table before inserting new record
            helper.deletePaperSettings();
            if(helper.savePaperSettings(companyName, companyAddress, companyPhone, footer, img_data, selectedPaper)){
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
    private void requestStoragePermission() {

        Dexter.withActivity(this)
                .withPermissions(
                        Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.READ_EXTERNAL_STORAGE
                )
                .withListener(new MultiplePermissionsListener() {
                    @Override
                    public void onPermissionsChecked(MultiplePermissionsReport report) {
                        //check all permission granted
                        if (report.areAllPermissionsGranted()) {
                            showOptions();

                        }
                        //check any permission permanent denied
                        if (report.isAnyPermissionPermanentlyDenied()) {
                            showSettingDialog();
                        }
                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token) {
                        token.continuePermissionRequest();

                    }
                }).check();
    }

    private void showSettingDialog() {
        android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(PaperSettingsActivity.this);
        builder.setTitle("Need Permissions");
        builder.setMessage("This app needs permission to use this feature. You can grant them in app settings.");
        builder.setPositiveButton("GOTO SETTINGS", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
                openSettings();
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        builder.show();
    }

    private void openSettings() {
        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        Uri uri = Uri.fromParts("package", getPackageName(), null);
        intent.setData(uri);
        startActivityForResult(intent, 101);
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
    private void showOptions() {
        android.app.AlertDialog.Builder pictureDialog = new android.app.AlertDialog.Builder(this);
        pictureDialog.setTitle("Select Action");
        String[] pictureDialogItems = {
                "Select photo from gallery",
                "Capture photo from camera"};
        pictureDialog.setItems(pictureDialogItems,
                (dialog, which) -> {
                    switch (which) {
                        case 0:
                            choosePhotoFromGallery();
                            break;
                        case 1:
                            takePhotoFromCamera();
                            break;
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

//    @Override
//    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
//        switch (requestCode) {
//            case REQUEST_ID_MULTIPLE_PERMISSIONS:
//                Map<String, Integer> perms = new HashMap<>();
//                perms.put(Manifest.permission.READ_EXTERNAL_STORAGE, PackageManager.PERMISSION_GRANTED);
//                perms.put(Manifest.permission.WRITE_EXTERNAL_STORAGE, PackageManager.PERMISSION_GRANTED);
//
//                if (grantResults.length > 0) {
//                    for (int i = 0; i < permissions.length; i++)
//                        perms.put(permissions[i], grantResults[i]);
//
//                    //check all permissions
//                    if (perms.get(Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED &&
//                            perms.get(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
//
//                        Log.d(TAG, "permission granted ");
//                        showOptions();
//                    } else {
//                        if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) ||
//
//                                ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.READ_EXTERNAL_STORAGE)) {
//
//                            new AlertDialog.Builder(PaperSettingsActivity.this)
//                                    .setTitle("Permission needed")
//                                    .setMessage("To continue please allow the permission ")
//                                    .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
//                                        @Override
//                                        public void onClick(DialogInterface dialog, int which) {
//                                            checkAndRequestPermissions();
//                                        }
//                                    })
//                                    .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
//                                        @Override
//
//                                        public void onClick(DialogInterface dialog, int which) {
//                                            dialog.cancel();
//                                        }
//                                    }).create().show();
//
//                        } else {
//                            Toast.makeText(this, "permission denied", Toast.LENGTH_SHORT).show();
//                        }
//                    }
//                }
//
//        }
//
//
//    }
}
