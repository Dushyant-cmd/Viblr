package com.veblr.android.veblrapp.upload;

import android.content.Context;
import android.content.Intent;

import androidx.annotation.NonNull;
import androidx.core.app.JobIntentService;

public class UploadIntentService extends JobIntentService {
    static final int UPLOAD_JOB_ID = 1000;

    @Override
    protected void onHandleWork(@NonNull Intent intent) {

    }

    @Override
    public void onDestroy() {
        super.onDestroy();

    }

    static void enqueueWork(Context context, Intent work) {
        enqueueWork(context, UploadIntentService.class, UPLOAD_JOB_ID, work);
    }

}
