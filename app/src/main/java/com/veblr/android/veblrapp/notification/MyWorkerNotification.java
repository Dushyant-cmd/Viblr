package com.veblr.android.veblrapp.notification;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.work.ListenableWorker;
import androidx.work.Worker;
import androidx.work.WorkerParameters;
import javax.xml.transform.Result;

import io.reactivex.Scheduler;


public class MyWorkerNotification extends Worker {
    private static final String TAG = "MyWorker";


    public MyWorkerNotification(@NonNull Context appContext, @NonNull WorkerParameters workerParams) {
        super(appContext, workerParams);
    }

    @NonNull
    @Override
    public Result doWork() {
        Log.d(TAG, "Performing long running task in scheduled job");
        // add long running task here.
        return Result.success();
    }

}