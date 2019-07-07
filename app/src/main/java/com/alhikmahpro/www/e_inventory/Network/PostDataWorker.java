package com.alhikmahpro.www.e_inventory.Network;

import android.content.Context;
import android.support.annotation.NonNull;

import androidx.work.ListenableWorker;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

public class PostDataWorker extends Worker {

    public PostDataWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
    }

    @NonNull
    @Override
    public Result doWork() {
        return null;
    }
}
