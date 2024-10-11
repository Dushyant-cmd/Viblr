package com.veblr.android.veblrapp.services;

import android.app.AlertDialog;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Build;
import android.util.Log;
import android.view.KeyEvent;

import androidx.core.app.NotificationCompat;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.veblr.android.veblrapp.R;

public class FileUploadNotification {
    public static NotificationManager mNotificationManager;
    static NotificationCompat.Builder builder;
    static Context context;
    static int NOTIFICATION_ID = 111;
    static  String CHANNEL_ID = "upload_channel_id";
    static FileUploadNotification fileUploadNotification;
/*
    public static FileUploadNotification createInsance(Context context) {
        if(fileUploadNotification == null)
            fileUploadNotification = new FileUploadNotification(context);

        return fileUploadNotification;
    }*/
    public FileUploadNotification(Context context) {
        mNotificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = context.getResources().getString(R.string.channel_name_upload);
            String description = context.getResources().getString(R.string.channel_description_upload);
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            mNotificationManager.createNotificationChannel(channel);
        }
        builder = new NotificationCompat.Builder(context);
        builder.setContentTitle("start uploading...")
                .setContentText("Video ")
                .setSmallIcon(android.R.drawable.stat_sys_upload)
                .setProgress(100, 0, false)
                .setAutoCancel(false);

    }

    public static void updateNotification(String percent, String fileName, String contentText,Context context) {
        try {
            builder.setContentText(contentText)
                    .setContentTitle("Video File is uploaded...")
                    .setSmallIcon(android.R.drawable.stat_sys_upload)
                    .setColor(context.getResources().getColor(R.color.bg_followButton))
                    .setOngoing(true)
                    .setChannelId(CHANNEL_ID)
                    .setContentInfo(percent + "%")
                    .setProgress(100, Integer.parseInt(percent), false);

            mNotificationManager.notify(NOTIFICATION_ID, builder.build());
            if (Integer.parseInt(percent) == 100)
                deleteNotification(context);

        } catch (Exception e) {
            // TODO Auto-generated catch block
            Log.e("Error...Notification.", e.getMessage() + ".....");
            e.printStackTrace();
        }
    }

    public static void failUploadNotification(/*int percentage, String fileName*/) {
        Log.e("downloadsize", "failed notification...");

        if (builder != null) {
            /* if (percentage < 100) {*/
            builder.setContentText("Uploading Failed")
                    //.setContentTitle(fileName)
                    .setSmallIcon(android.R.drawable.stat_sys_upload_done)
                    .setOngoing(false);
            mNotificationManager.notify(NOTIFICATION_ID, builder.build());
        /*} else {
            mNotificationManager.cancel(NOTIFICATION_ID);
            builder = null;
        }*/
        } else {
            mNotificationManager.cancel(NOTIFICATION_ID);
        }
    }

    public static void deleteNotification(Context context) {
        mNotificationManager.cancel(NOTIFICATION_ID);
        builder = null;
       final AlertDialog alertDialog  = new AlertDialog.Builder(context).create();
       alertDialog.setMessage("Upload is completed...");
       alertDialog.show();
       alertDialog.setOnKeyListener(new DialogInterface.OnKeyListener() {
           @Override
           public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
               alertDialog.dismiss();
               return true;
           }
       });

    }
}
