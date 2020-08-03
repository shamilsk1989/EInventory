package com.alhikmahpro.www.e_inventory.View;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
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
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class AddLogoActivity extends AppCompatActivity {

    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.imgLogo)
    ImageView imgLogo;
    @BindView(R.id.btnAdd)
    Button btnAdd;
    private int GALLERY = 1, CAMERA = 2;
    Bitmap thumbnail;
    dbHelper helper;
    int id=0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_logo);
        ButterKnife.bind(this);
        helper=new dbHelper(this);
        SQLiteDatabase sqLiteDatabase = helper.getReadableDatabase();
        Bitmap bitmap = helper.getLogo(sqLiteDatabase);
        try {
             imgLogo.setImageBitmap(Bitmap.createScaledBitmap(bitmap, 60, 60, false));
        } catch (Exception e) {
                e.printStackTrace();
        }
    }

    private void requestPermissions() {
        Dexter.withActivity(this)
                .withPermissions(Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.CAMERA)
                .withListener(new MultiplePermissionsListener() {
                    @Override
                    public void onPermissionsChecked(MultiplePermissionsReport report) {
                        if (report.areAllPermissionsGranted()) {
                            showOptions();

                        }
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

    private void showSettingDialog() {
        android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(AddLogoActivity.this);
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


    @OnClick({R.id.imgLogo, R.id.btnAdd})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.imgLogo:
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
                    requestPermissions();
                else
                    showOptions();
                break;
            case R.id.btnAdd:
                imgLogo.setDrawingCacheEnabled(true);
                imgLogo.buildDrawingCache();
                Bitmap bitmap = imgLogo.getDrawingCache();
                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
                byte[] img_data = stream.toByteArray();
                long id=helper.saveLogo(img_data);
                if(id>0){
                    Toast.makeText(this, "Saved", Toast.LENGTH_SHORT).show();
                    finish();
                    break;
                }

        }
    }
}
