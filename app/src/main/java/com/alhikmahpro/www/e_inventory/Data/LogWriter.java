package com.alhikmahpro.www.e_inventory.Data;

import android.Manifest;
import android.content.Context;

import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;

import java.util.List;

public class LogWriter {
    Context mContext;
    String filePath;
    public LogWriter(Context context, String filePath) {
        this.mContext = context;
        this.filePath = filePath;
        requestPermission();
    }

    private void requestPermission() {

    }

    public void Write(String activityName,String error){

    }


}
