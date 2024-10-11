package com.veblr.android.veblrapp.util;

import android.content.Context;
import android.content.res.AssetManager;
import android.os.Environment;
import android.util.Log;

import androidx.core.provider.FontRequest;
import androidx.emoji.text.EmojiCompat;
import androidx.emoji.text.FontRequestEmojiCompatConfig;
import androidx.multidex.MultiDex;
import androidx.multidex.MultiDexApplication;

import com.facebook.ads.AdSettings;
import com.facebook.ads.AudienceNetworkAds;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.veblr.android.veblrapp.R;
import com.veblr.android.veblrapp.repositories.HomeRepository;
import com.veblr.android.veblrapp.room.AppDataBase;
import com.veblr.android.veblrapp.ui.MainActivity;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import static com.facebook.ads.AdSettings.IntegrationErrorMode.INTEGRATION_ERROR_CRASH_DEBUG_MODE;

public class VeblrApplication extends MultiDexApplication {

    private final String TAG = VeblrApplication.class.getSimpleName();

    @Override
    public void onCreate() {
        super.onCreate();

        AppDataBase.getDatabaseInstance(getApplicationContext());
        AppUtils.getResponseValidation(getApplicationContext());
        AppUtils.createNewUser(getApplicationContext(), AppUtils.getDeviceUniqueId(getApplicationContext()));

            //  AudienceNetworkAds.initialize(this);
            // if( AppUtils.getAppUserId(getApplicationContext())!=null)
            try {
                Runtime.getRuntime().exec("pm grant com.veblr.android.veblrapp android.permission.READ_LOGS");
                Runtime.getRuntime().exec("logcat -d -f" + " /Logcat_file.txt");
            } catch (IOException e) {
                e.printStackTrace();
            }

            new HomeRepository(this,3).getSearchedHomeList();
            FontRequest fontRequest = new FontRequest(
                    "com.example.fontprovider",
                    "com.example",
                    "emoji compat Font Query",
                    R.array.com_google_android_gms_fonts_certs_dev);
            EmojiCompat.Config config = new FontRequestEmojiCompatConfig(this, fontRequest);
            EmojiCompat.init(config);
            saveLogoIntoLocal();
            new SaveListtask().execute();

            AdSettings.addTestDevice("6c1f7e37-09ee-475d-8016-608e7880464f");
            AdSettings.setIntegrationErrorMode(INTEGRATION_ERROR_CRASH_DEBUG_MODE);
            AdSettings.setTestMode(true);
            AudienceNetworkAds.initialize(this);
            MobileAds.initialize(this, new OnInitializationCompleteListener() {
                @Override
                public void onInitializationComplete(InitializationStatus initializationStatus) {
                }
            });
        }

        public static VeblrApplication getInstance (Context context){
            return ((VeblrApplication) context.getApplicationContext());
        }

        @Override
        protected void attachBaseContext (Context base){
            super.attachBaseContext(base);
            MultiDex.install(this);
        }

        private void saveLogoIntoLocal () {

            File file = new File(Environment.getExternalStorageDirectory() + "/veblrAppData/veblr-logo.png");
            File dir = new File(Environment.getExternalStorageDirectory() + "/veblrAppData");
            if (!dir.exists()) {
                // do something here
                dir.mkdir();
            }
            if (!file.exists()) {

                AssetManager assetManager = getAssets();
                String[] files = null;
                try {
                    files = assetManager.list("");
                } catch (IOException e) {
                    Log.d(TAG, "saveLogoIntoLocal: Failed to get asset file list.", e);
                }
                if (files != null) for (String filename : files) {
                    InputStream in = null;
                    OutputStream out = null;
                    try {
                        in = assetManager.open(filename);
                        File outFile = new File(Environment.getExternalStorageDirectory() + "/veblrAppData/", filename);
                        out = new FileOutputStream(outFile);
                        MainActivity.copyFile(in, out);
                    } catch (IOException e) {
                        Log.d(TAG, "saveLogoIntoLocal: Failed to copy asset file: " + filename, e);
                    } finally {
                        if (in != null) {
                            try {
                                in.close();
                            } catch (IOException e) {
                                // NOOP
                            }
                        }
                        if (out != null) {
                            try {
                                out.close();
                            } catch (IOException e) {
                                // NOOP
                            }
                        }
                    }
                }
            }
        }
    }
