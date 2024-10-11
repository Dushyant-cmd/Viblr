package com.veblr.android.veblrapp.ui;

import static android.os.PowerManager.SCREEN_DIM_WAKE_LOCK;
import static android.view.View.VISIBLE;
import static com.veblr.android.veblrapp.record.FFmpegRecordActivity.REQUEST_PERMISSIONS;
import static com.veblr.android.veblrapp.ui.CategoryFragment.isBackPressed;
import static com.veblr.android.veblrapp.util.Constants.LOCATION_REQUEST_CODE;
import static com.veblr.android.veblrapp.util.Constants.RECORD_VIDEO_REQUEST;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.AssetManager;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.PowerManager;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.view.ViewCompat;
import androidx.core.widget.ImageViewCompat;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import com.amosyuen.videorecorder.activity.FFmpegRecorderActivity;
import com.amosyuen.videorecorder.activity.params.FFmpegRecorderActivityParams;
import com.amosyuen.videorecorder.camera.CameraControllerI;
import com.amosyuen.videorecorder.recorder.common.ImageFit;
import com.amosyuen.videorecorder.recorder.common.ImageScale;
import com.amosyuen.videorecorder.recorder.common.ImageSize;
import com.amosyuen.videorecorder.recorder.params.EncoderParamsI;
import com.getkeepsafe.relinker.ReLinker;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationMenuView;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.veblr.android.veblrapp.R;
import com.veblr.android.veblrapp.adapter.ViewPagerAdapter;
import com.veblr.android.veblrapp.location.GPSTracker;
import com.veblr.android.veblrapp.model.VIdeoItem;
import com.veblr.android.veblrapp.network.SaveCountryListTask;
import com.veblr.android.veblrapp.record.FFmpegRecordActivity;
import com.veblr.android.veblrapp.repositories.HomeRepository;
import com.veblr.android.veblrapp.util.AppUtils;
import com.veblr.android.veblrapp.util.Constants;
import com.veblr.android.veblrapp.util.RequestPermissionHandler;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URISyntaxException;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import VideoHandle.EpDraw;
import VideoHandle.EpEditor;
import VideoHandle.EpText;
import VideoHandle.EpVideo;
import VideoHandle.OnEditorListener;

public  class MainActivity extends AppCompatActivity {

    private final String TAG = MainActivity.class.getSimpleName();
    final private int REQUEST_CODE_ASK_MULTIPLE_PERMISSIONS = 124;
    BottomNavigationView navView;
    CustomViewPager viewPager;
    ViewPagerAdapter viewPagerAdapter;
    MenuItem prevMenuItem;
    ;
    PopupWindow popupWindow;
    ConstraintLayout constraintLayout;
    ImageView ivHome;
    private ViewCompat.OnUnhandledKeyEventListenerCompat mCompatListener;
    private static final int REQUEST_CODE_EMAIL = 5;

    boolean homeClicked = true;
    boolean catagoryClicked = true;
    boolean followClicked = true;
    boolean notiClicked = true;
    TextView btnRecord;
    private ReLinker.Logger logcatLogger = new ReLinker.Logger() {
        @Override
        public void log(String message) {
            Log.d("ReLinker", message);
        }
    };
    // private ArrayList<CustomBottomNavigationItem> bottomNavigationItems = new ArrayList<>();
    private boolean useMenuResource = true;
    private int[] tabColors;
    private Handler handler = new Handler();
    ProgressBar progressBar;
    protected PowerManager.WakeLock mWakeLock;
    String fileTitle = "";
    int duration = 0;
    File mVideoFile;
    File mThumbnailFile;
    static final String FILE_PREFIX = "recorder-";
    static final String THUMBNAIL_FILE_EXTENSION = "jpg";
    int count = 0;

    /*
    private CustomBottomNavigation bottomNavigation;
*/

    @SuppressLint("RestrictedApi")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
        assert pm != null;
        this.mWakeLock = pm.newWakeLock(SCREEN_DIM_WAKE_LOCK, "myapp:MainTAg");
        this.mWakeLock.acquire(10000000);

        viewPager = (CustomViewPager) findViewById(R.id.viewpager);
        ivHome = (ImageView) findViewById(R.id.ivHome);
        ((LinearLayout) findViewById(R.id.llPB)).setVisibility(VISIBLE);

        //Comment By Rakesh
        Log.e(TAG, "Calling HomeRepository getSearchedHomeList From Main");
        List<VIdeoItem> list = new HomeRepository(this, 3).getSearchedHomeList();
        if (list.size() != 0) {
            Log.e("RESPONSE_id", list.get(0).getVideoId() + "");
            AppUtils.saveFavorites(this, list);
           /* for (VIdeoItem v:list) {
                new VideoRepository(this).insertVideo(v);
             }*/
        }
        ((LinearLayout) findViewById(R.id.llPB)).setVisibility(VISIBLE);
        //Toast.makeText(getApplicationContext(), "Android id is " + AppUtils.getDeviceUniqueId(getApplicationContext()),
          //      Toast.LENGTH_LONG).show();
        viewPagerAdapter = new ViewPagerAdapter(getSupportFragmentManager(), this, getSupportActionBar(),
                null);
        viewPager.setAdapter(viewPagerAdapter);
        viewPager.setOffscreenPageLimit(5);
        viewPager.setPagingEnabled(false);
        // ((LinearLayout)findViewById(R.id.llPB)).setVisibility(View.GONE);

        checkMultiplePermissions();

        /*if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

            if (checkSelfPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    != PackageManager.PERMISSION_GRANTED) {
                Log.e(TAG, "Inside permission check1");
                ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
            } else {
                try {
                    Runtime.getRuntime().exec("logcat -f " + Environment.getExternalStorageDirectory()
                            + "/veblrAppData/logFile");
                    //Intent.ACTION_CREATE_DOCUMENT
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }*/

        LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
        View customView = inflater.inflate(R.layout.upload_dialog, null);
        popupWindow = new PopupWindow(customView,
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT);
        if (Build.VERSION.SDK_INT >= 21) {
            popupWindow.setElevation(5.0f);
        }
        popupWindow.setFocusable(true);
        btnRecord = (TextView) customView.findViewById(R.id.btnRecord);
        btnRecord.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // startActivity(new Intent(MainActivity.this, FFmpegRecordActivity.class));
                String[] neededPermissions = {
                        Manifest.permission.CAMERA,
                        Manifest.permission.RECORD_AUDIO,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.ACCESS_FINE_LOCATION};
                List<String> deniedPermissions = new ArrayList<>();
                for (String permission : neededPermissions) {
                    if (ContextCompat.checkSelfPermission(MainActivity.this, permission) != PackageManager.PERMISSION_GRANTED) {
                        deniedPermissions.add(permission);
                    }
                }
                if (deniedPermissions.isEmpty()) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            popupWindow.dismiss();
                        }
                    });
                    creatTempFile();
                    launchrecorder(mVideoFile, mThumbnailFile);
                } else {
                    String[] array = new String[deniedPermissions.size()];
                    array = deniedPermissions.toArray(array);
                    ActivityCompat.requestPermissions(MainActivity.this, array, REQUEST_PERMISSIONS);
                }
            }
               /*      if (!AppUtils.hasPermission(MainActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                     new RequestPermissionHandler().requestPermission(MainActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, EXTERNAL_STORAGE_WRITE_REQUEST_CODE,
                             new RequestPermissionHandler.RequestPermissionListener() {

                                 @Override
                                 public void onSuccess() {


                                 }

                                 @Override
                                 public void onFailed() {

                                 }
                             });
                 }

               else{  creatTempFile();
                     launchrecorder(mVideoFile,mThumbnailFile);

                 }
     */
        });
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setLogo(R.drawable.ic_veblr_logo);
            getSupportActionBar().setDisplayUseLogoEnabled(true);
            getSupportActionBar().setTitle("");
            //  getSupportActionBar().setHideOnContentScrollEnabled(true);
            //  getSupportActionBar().setElevation(20);
        }

       /* if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setStatusBarColor(Color.WHITE);
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        }*/
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                if (prevMenuItem != null)
                    prevMenuItem.setChecked(false);
              /*  else
                    navView.getMenu().getItem(0).setChecked(false);

                navView.getMenu().getItem(position).setChecked(true);
                prevMenuItem = navView.getMenu().getItem(position);*/

            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });


        //   if (viewPager.getVerticalScrollbarPosition() == 0)
        homeClicked = true;

        ImageViewCompat.setImageTintList((ImageView) findViewById(R.id.ivHome), ColorStateList.valueOf(Color.parseColor("#2C98FF")));
        ((TextView) findViewById(R.id.tvHome)).setTextColor(getResources().getColor(R.color.bottombarbackground));

        setnotificationReceiver();
        Log.e("LOACTION", AppUtils.getSavedLocation(this));

        /*if (!AppUtils.hasPermission(MainActivity.this, Manifest.permission.ACCESS_FINE_LOCATION)) {
            Log.e(TAG, "Inside permission check2");
            new RequestPermissionHandler().requestPermission(MainActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_REQUEST_CODE,
                    new RequestPermissionHandler.RequestPermissionListener() {

                        @Override
                        public void onSuccess() {
                            getAndSaveLocation();
                        }

                        @Override
                        public void onFailed() {

                        }
                    });
        } else getAndSaveLocation();*/

    }

    private void creatTempFile() {


        if (mVideoFile != null && mThumbnailFile != null) {
            return;
        }

        //  File dir = Environment.getExternalStorageDirectory()+"/veblrAppData";
        try {
            String videoExt = ".mp4";
            // ((EncoderParamsI.OutputFormat) mOutputFormatSpinner.getSelectedItem()).getFileExtension();
            while (true) {
                  /*  File dir = new File(Environment.getExternalStorageDirectory() + "/veblrAppData");
                    if(!dir.exists()) {
                        // do something here
                        dir.mkdir();
                    }*/
                int n = (int) (Math.random() * Integer.MAX_VALUE);
                String videoFileName = FILE_PREFIX + Integer.toString(n) + "." + videoExt;
                mVideoFile = new File(Environment.getExternalStorageDirectory() + "/veblrAppData", videoFileName);
                if (mVideoFile.exists() && mVideoFile.createNewFile()) {
                    String thumbnailFileName =
                            FILE_PREFIX + Integer.toString(n) + "." + THUMBNAIL_FILE_EXTENSION;
                    mThumbnailFile = new File(Environment.getExternalStorageDirectory() + "/veblrAppData", thumbnailFileName);
                    if (!mThumbnailFile.exists() && mThumbnailFile.createNewFile()) {
                        return;
                    }
                    mVideoFile.delete();
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    private void launchrecorder(File videoFile, File thumbnailFile) {
        FFmpegRecorderActivityParams.Builder paramsBuilder =
                FFmpegRecorderActivityParams.builder(MainActivity.this)
                        .setVideoOutputFileUri(videoFile)
                        .setVideoThumbnailOutputFileUri(thumbnailFile);

        paramsBuilder.recorderParamsBuilder()
                .setVideoSize(new ImageSize(720, 480))
                .setVideoCodec(EncoderParamsI.VideoCodec.H264)
                .setVideoBitrate(100000)
                .setVideoFrameRate(30)
                .setVideoImageFit(ImageFit.FIT)
                .setVideoImageScale(ImageScale.DOWNSCALE)
                .setShouldCropVideo(true)
                .setShouldPadVideo(true)
                .setVideoCameraFacing(CameraControllerI.Facing.BACK)
                .setAudioCodec(EncoderParamsI.AudioCodec.AAC)
                .setAudioSamplingRateHz(44100)
                .setAudioBitrate(100000)
                .setAudioChannelCount(2)
                .setOutputFormat(EncoderParamsI.OutputFormat.MP4);
        Intent intent = new Intent(this, FFmpegRecordActivity.class);
        intent.putExtra(FFmpegRecordActivity.REQUEST_PARAMS_KEY, paramsBuilder.build());
        startActivityForResult(intent, RECORD_VIDEO_REQUEST);
    }

    private void getAndSaveLocation() {
        final GPSTracker tracker = new GPSTracker(MainActivity.this);
        Thread thread = new Thread() {
            @Override
            public void run() {
                Geocoder geocoder = new Geocoder(MainActivity.this, Locale.getDefault());
                String result = null;
                try {
                    List<Address> addressList = geocoder.getFromLocation(tracker.getLatitude(), tracker.getLongitude(), 1);
                    //     List<Address> addressList = geocoder.getFromLocation(  20.838816, 83.583619,1);
                    if (addressList != null && addressList.size() > 0) {
                        Address address = addressList.get(0);
                        StringBuilder sb = new StringBuilder();
                        for (int i = 0; i < address.getMaxAddressLineIndex(); i++) {
                            sb.append(address.getAddressLine(i)).append(" ");
                        }
                        sb.append(address.getFeatureName()).append(" ");
                        sb.append(address.getLocality()).append(" ");
                        sb.append(address.getSubLocality()).append(" ");
                        sb.append(address.getAdminArea()).append(" ");
                        sb.append(address.getExtras()).append(" ");
                        sb.append(address.getPostalCode()).append(" ");
                        sb.append(address.getCountryCode());
                        result = sb.toString();
                        Log.e("LOCATION", result);
                        result = result.replace("null", "");
                        AppUtils.saveLocation(MainActivity.this, result);

                    }

                } catch (IOException e) {
                    Log.e("ERROR", "Unable connect to Geocoder", e);
                } finally {
                    Message message = Message.obtain();
                    message.setTarget(handler);
                    if (result != null) {
                        message.what = 1;
                        Bundle bundle = new Bundle();
                        result = "Latitude: " + tracker.getLatitude() + " Longitude: " + tracker.getLongitude() +
                                "\n\nAddress:\n" + result;
                        bundle.putString("address", result);
                        message.setData(bundle);
                    } else {
                        message.what = 1;
                        Bundle bundle = new Bundle();
                        result = "Latitude: " + tracker.getLatitude() + " Longitude: " + tracker.getLongitude() +
                                "\n Unable to get address for this lat-long.";
                        bundle.putString("address", result);
                        message.setData(bundle);
                    }
                    message.sendToTarget();
                }
            }
        };
        thread.start();


    }

    private void setnotificationReceiver() {

        FirebaseInstanceId.getInstance().getInstanceId()
                .addOnCompleteListener(new OnCompleteListener<InstanceIdResult>() {
                    @Override
                    public void onComplete(@NonNull Task<InstanceIdResult> task) {
                        if (!task.isSuccessful()) {
                            Log.w("MAIN UI", "getInstanceId failed", task.getException());
                            return;
                        }

                        // Get new Instance ID token
                        String token = task.getResult().getToken();
                        // Log and toast
                        Log.d("FCM_TOKEN", token + " ");
                    }
                });
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            // Create channel to show notifications.
            String channelId = getString(R.string.default_notification_channel_id);
            NotificationManager notificationManager =
                    getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(new NotificationChannel(channelId,
                    "NEWVIDEOS", NotificationManager.IMPORTANCE_LOW));
        }

    }


    private void setBottomNavViewiconsSize() {

        BottomNavigationMenuView menuView = (BottomNavigationMenuView) navView.getChildAt(0);

        // for (int i = 0; i < menuView.getChildCount(); i++) {
        final View iconView = menuView.getChildAt(2).findViewById(R.id.navigation_upload);
        final ViewGroup.LayoutParams layoutParams = iconView.getLayoutParams();
        final DisplayMetrics displayMetrics = getResources().getDisplayMetrics();
        layoutParams.height = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 32, displayMetrics);
        layoutParams.width = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 28, displayMetrics);
        iconView.setLayoutParams(layoutParams);
        //}
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.top_menu, menu);

       /*  if (Build.VERSION.SDK_INT >= 17) {






            SearchManager searchManager =
                    (SearchManager) getSystemService(Context.SEARCH_SERVICE);
            SearchView searchView =
                    (SearchView) menu.findItem(R.id.navigation_search).getActionView();
            searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
            searchView.setIconifiedByDefault(false);

        }*/
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {


        if (item.getItemId() == R.id.navigation_profile) {
            // Log.e("THUMBNAIL",AppUtils.getThumbnail(""));
            if (AppUtils.getRegisteredUserId(MainActivity.this) == null)
                startActivity(new Intent(MainActivity.this, LoginActivity.class));
            else {
                startActivity(new Intent(MainActivity.this, UserProfileActivity.class));

            }
            //AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);

        }

        if (item.getItemId() == R.id.navigation_search) {
            // Log.e("THUMBNAIL",AppUtils.getThumbnail(""));

            startActivity(new Intent(MainActivity.this, SearchActivity.class));
            //  getSupportActionBar().setIcon(null);
            /*item.setOnActionExpandListener(new MenuItem.OnActionExpandListener() {
                @Override
                public boolean onMenuItemActionExpand(MenuItem item) {

                    getSupportActionBar().setIcon(null);
                    return false;
                }

                @Override
                public boolean onMenuItemActionCollapse(MenuItem item) {
                    return false;
                }
            });*/
        }
        return true;
    }


    public void onGallerybuttonClicked(View view) {

        if (AppUtils.hasPermission(MainActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            Log.e(TAG, "Inside permission check3");
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    popupWindow.dismiss();
                }
            });
            Intent intent = new Intent();
            intent.setType("video/*");
            intent.setAction(Intent.ACTION_GET_CONTENT);
            startActivityForResult(Intent.createChooser(intent, "Select video to upload"),
                    Constants.GALLEY_OPEN_REQUEST_CODE);

        } else {
            Log.e(TAG, "Inside permission check4");
            new RequestPermissionHandler().requestPermission(MainActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 10,
                    new RequestPermissionHandler.RequestPermissionListener() {
                        @Override
                        public void onSuccess() {
                            Intent intent = new Intent();
                            intent.setType("video/*");
                            intent.setAction(Intent.ACTION_GET_CONTENT);
                            startActivityForResult(Intent.createChooser(intent, "Select video to upload"),
                                    Constants.GALLEY_OPEN_REQUEST_CODE);
                        }

                        @Override
                        public void onFailed() {

                        }
                    });
        }
    }

    public void OnHomeButtonClicked(View view) {
        //  if(viewPager.getVerticalScrollbarPosition()==0)
        //       homeClicked =true;

        if (homeClicked) {
            viewPager.setCurrentItem(0);
            AppUtils.enableButton((ImageView) findViewById(R.id.ivHome), (TextView) findViewById(R.id.tvHome), getBaseContext());
            homeClicked = false;

            AppUtils.disableButton((ImageView) findViewById(R.id.ivCata), (TextView) findViewById(R.id.tvCata), getBaseContext());
            AppUtils.disableButton((ImageView) findViewById(R.id.ivFollow), (TextView) findViewById(R.id.tvFollow), getBaseContext());
            AppUtils.disableButton((ImageView) findViewById(R.id.ivNoti), (TextView) findViewById(R.id.tvNoti), getBaseContext());
            catagoryClicked = true;
            followClicked = true;
            notiClicked = true;

        } else {
            AppUtils.disableButton((ImageView) findViewById(R.id.ivHome), (TextView) findViewById(R.id.tvHome), getBaseContext());
            homeClicked = true;

        }
    }

    public void OnCatagoryButtonClicked(View view) {
        viewPager.setCurrentItem(1);
        if (catagoryClicked) {
            AppUtils.enableButton((ImageView) findViewById(R.id.ivCata), (TextView) findViewById(R.id.tvCata), getBaseContext());
            catagoryClicked = false;
            AppUtils.disableButton((ImageView) findViewById(R.id.ivHome), (TextView) findViewById(R.id.tvHome), getBaseContext());
            AppUtils.disableButton((ImageView) findViewById(R.id.ivFollow), (TextView) findViewById(R.id.tvFollow), getBaseContext());
            AppUtils.disableButton((ImageView) findViewById(R.id.ivNoti), (TextView) findViewById(R.id.tvNoti), getBaseContext());
            homeClicked = true;
            followClicked = true;
            notiClicked = true;
        } else {
            AppUtils.disableButton((ImageView) findViewById(R.id.ivCata), (TextView) findViewById(R.id.tvCata), getBaseContext());
            catagoryClicked = true;
        }
    }

    public void OnUploadButtonClicked(View view) {

        if (AppUtils.checkForLoggedInUser(MainActivity.this)) {
            popupWindow.getContentView().getRootView().setVisibility(VISIBLE);
            popupWindow.showAtLocation(findViewById(R.id.container), Gravity.BOTTOM, 0, 0);
        } else {
            new Handler(Looper.getMainLooper()).post(new Runnable() {
                @Override
                public void run() {
                    startActivity(new Intent(MainActivity.this, LoginActivity.class));

                }
            });
        }

/*    this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
            }
        });*/
    }

    public void OnNotificationButtonClicked(View view) {
        viewPager.setCurrentItem(4);
        if (notiClicked) {
            AppUtils.enableButton((ImageView) findViewById(R.id.ivNoti), (TextView) findViewById(R.id.tvNoti), getBaseContext());
            notiClicked = false;
            AppUtils.disableButton((ImageView) findViewById(R.id.ivCata), (TextView) findViewById(R.id.tvCata), getBaseContext());
            AppUtils.disableButton((ImageView) findViewById(R.id.ivFollow), (TextView) findViewById(R.id.tvFollow), getBaseContext());
            AppUtils.disableButton((ImageView) findViewById(R.id.ivHome), (TextView) findViewById(R.id.tvHome), getBaseContext());
            catagoryClicked = true;
            followClicked = true;
            homeClicked = true;
        } else {
            AppUtils.disableButton((ImageView) findViewById(R.id.ivNoti), (TextView) findViewById(R.id.tvNoti), getBaseContext());
            notiClicked = true;
        }
    }

    public void OnFollowButtonClicked(View view) {
        viewPager.setCurrentItem(3);
        if (followClicked) {
            AppUtils.enableButton((ImageView) findViewById(R.id.ivFollow), (TextView) findViewById(R.id.tvFollow), getBaseContext());
            followClicked = false;
            AppUtils.disableButton((ImageView) findViewById(R.id.ivCata), (TextView) findViewById(R.id.tvCata), getBaseContext());
            AppUtils.disableButton((ImageView) findViewById(R.id.ivHome), (TextView) findViewById(R.id.tvHome), getBaseContext());
            AppUtils.disableButton((ImageView) findViewById(R.id.ivNoti), (TextView) findViewById(R.id.tvNoti), getBaseContext());
            catagoryClicked = true;
            homeClicked = true;
            notiClicked = true;
        } else {
            AppUtils.disableButton((ImageView) findViewById(R.id.ivFollow), (TextView) findViewById(R.id.tvFollow), getBaseContext());
            followClicked = true;
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (resultCode == RESULT_OK) {
            if (requestCode == 1) {

                assert data != null;
                Log.e("data video :", data.getData() + "");
                ContentResolver cr = this.getContentResolver();
                String s = AppUtils.getRealVideoPathFromURI(cr, data.getData());
                Log.e("data video :", s + "  ");
                try {
                    Log.e("data video :", AppUtils.getFilePath(MainActivity.this, data.getData()) + "  ");
                    s = AppUtils.getFilePath(MainActivity.this, data.getData());
                    editvideo(s);

                } catch (URISyntaxException e) {
                    e.printStackTrace();
                }

                Uri uri = data.getData();
                assert uri != null;
                File file = new File(uri.getPath());//create path from uri
                final String split = file.getPath();//split the path.
                Log.e("data video :", split + "  " + file.getAbsolutePath());

            }

        }

        if (requestCode == RECORD_VIDEO_REQUEST) {
            switch (resultCode) {
                case RESULT_OK:
                    assert data != null;
                    Uri videoUri = data.getData();
                    Uri thumbnailUri =
                            data.getParcelableExtra(FFmpegRecordActivity.RESULT_THUMBNAIL_URI_KEY);
                    Log.e("URI:", videoUri.toString() + " " + thumbnailUri);
                    Log.e("EDIT STATUS", "SUccessfull");
                    break;
                case Activity.RESULT_CANCELED:
                    break;
                case FFmpegRecorderActivity.RESULT_ERROR:
                    Exception error = null;
                    if (data != null) {
                        error = (Exception)
                                data.getSerializableExtra(FFmpegRecordActivity.RESULT_ERROR_PATH_KEY);
                        error.printStackTrace();
                    }
                    break;
            }
        }
        super.onActivityResult(requestCode, resultCode, data);

    }

    public void editvideo(String path) {
        // System.loadLibrary("EpMedia");
        ((LinearLayout) findViewById(R.id.llPB)).setVisibility(VISIBLE);
        final EpVideo epVideo = new EpVideo(path);
        MediaMetadataRetriever retriever = new MediaMetadataRetriever();
        retriever.setDataSource(path);
        int frames = Integer.valueOf(retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION));
        Log.e("Video Duration", epVideo.getClipDuration() + " " + frames);
        duration = frames;
        int width = Integer.valueOf(retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_WIDTH));
        int height = Integer.valueOf(retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_HEIGHT));
        fileTitle = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_TITLE);

        retriever.release();

        saveLogoIntoLocal();
        String username = AppUtils.getRegisteredUserId(MainActivity.this).getName();
        String location = AppUtils.getSavedLocation(MainActivity.this);
        if (location == null) {
            getAndSaveLocation();
        }
        String text = "@" + username + "\n" + AppUtils.getSavedLocation(MainActivity.this);
        int picWidth = width / 5;
        int picHeight = height / 20;
        int textSize = height / 27;
        epVideo.addDraw(new EpDraw(Environment.getExternalStorageDirectory()
                + "/veblrAppData/veblr-logo.png",
                width - 150, height - 50, picWidth, picHeight, false));
        epVideo.addText(new EpText(width - 160, height - (picHeight + (2 * textSize) + 50), textSize,
                EpText.Color.Yellow,
                Environment.getExternalStorageDirectory() + "/veblrAppData/msyh.ttf", text, new EpText.Time(0, frames)));
        String outPath;
        if (fileTitle == null)
            outPath = Environment.getExternalStorageDirectory() + "/veblrAppData/" + DateFormat.getDateTimeInstance().format(new Date()) + ".mp4";
        else {
            outPath = Environment.getExternalStorageDirectory() + "/veblrAppData/" + fileTitle + DateFormat.getDateTimeInstance().format(new Date()) + ".mp4";
        }

        sendCmd(outPath, epVideo);

    }

    @Override
    protected void onPostCreate(@Nullable Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
    }

    public void sendCmd(final String outPath, final EpVideo epVideo) {
        try {
            ReLinker.recursively().loadLibrary(this, "ffmpeg");

            EpEditor.exec(epVideo, new EpEditor.OutputOption
                            (outPath),
                    new OnEditorListener() {
                        @Override
                        public void onSuccess() {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    ((LinearLayout) findViewById(R.id.llPB)).setVisibility(View.GONE);

                                }
                            });

                            Log.e("EDIT STATUS", "SUccessfull");
                            Intent intent = new Intent(MainActivity.this, PreviewActivity.class);
                            intent.putExtra("fileUri", outPath);
                            intent.putExtra("duration", duration);

                            startActivity(intent);
                        }

                        @Override
                        public void onFailure() {

                        }

                        @Override
                        public void onProgress(final float progress) {


                        }
                    });
        } catch (UnsatisfiedLinkError e) {
            ReLinker.log(logcatLogger)
                    .force()
                    .recursively()
                    .loadLibrary(MainActivity.this, "ffmpeg",
                            new ReLinker.LoadListener() {
                                @Override
                                public void success() {
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            EpEditor.exec(epVideo, new EpEditor.OutputOption
                                                            (outPath),
                                                    new OnEditorListener() {
                                                        @Override
                                                        public void onSuccess() {
                                                            //popupWindow.dismiss();
                                                           /* new Handler(Looper.getMainLooper()).post(new Runnable() {
                                                                @Override
                                                                public void run() {
                                                                    //do stuff like remove view etc
                                                                    popupWindow.dismiss();                    }
                                                            });*/
                                                            Log.e("EDIT STATUS", "SUccessfull");
                                                            Intent intent = new Intent(MainActivity.this, PreviewActivity.class);
                                                            intent.putExtra("fileUri", outPath);
                                                            startActivity(intent);
                                                           /* new Handler(Looper.getMainLooper()).post(new Runnable() {
                                                                @Override
                                                                public void run() {
                                                                    //do stuff like remove view etc
                                                                    progressBar.setVisibility(View.GONE);
                                                                }
                                                            });*/
                                                        }

                                                        @Override
                                                        public void onFailure() {

                                                        }

                                                        @Override
                                                        public void onProgress(float progress) {

                                                        }

                                                    });

                                        }
                                    });
                                }

                                @Override
                                public void failure(Throwable t) {

                                }
                            });
        } catch (NoClassDefFoundError e) {
            ReLinker.log(logcatLogger)
                    .force()
                    .recursively()
                    .loadLibrary(MainActivity.this, "ffmpeg",
                            new ReLinker.LoadListener() {
                                @Override
                                public void success() {
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            EpEditor.exec(epVideo, new EpEditor.OutputOption
                                                            (outPath),
                                                    new OnEditorListener() {
                                                        @Override
                                                        public void onSuccess() {
                                                            //popupWindow.dismiss();
                                                           /* new Handler(Looper.getMainLooper()).post(new Runnable() {
                                                                @Override
                                                                public void run() {
                                                                    //do stuff like remove view etc
                                                                    popupWindow.dismiss();                    }
                                                            });*/
                                                            Log.e("EDIT STATUS", "SUccessfull");
                                                            Intent intent = new Intent(MainActivity.this, PreviewActivity.class);
                                                            intent.putExtra("fileUri", outPath);
                                                            startActivity(intent);
                                                           /* new Handler(Looper.getMainLooper()).post(new Runnable() {
                                                                @Override
                                                                public void run() {
                                                                    //do stuff like remove view etc
                                                                    progressBar.setVisibility(View.GONE);
                                                                }
                                                            });*/
                                                        }

                                                        @Override
                                                        public void onFailure() {

                                                        }

                                                        @Override
                                                        public void onProgress(float progress) {

                                                        }

                                                    });

                                        }
                                    });
                                }

                                @Override
                                public void failure(Throwable t) {
                                    t.printStackTrace();
                                }
                            });
        }
    }

    private void saveLogoIntoLocal() {

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
                Log.e("tag", "Failed to get asset file list.", e);
            }
            if (files != null) for (String filename : files) {
                InputStream in = null;
                OutputStream out = null;
                try {
                    in = assetManager.open(filename);
                    File outFile = new File(Environment.getExternalStorageDirectory() + "/veblrAppData/", filename);
                    out = new FileOutputStream(outFile);
                    copyFile(in, out);
                } catch (IOException e) {
                    Log.e("tag", "Failed to copy asset file: " + filename, e);
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

    public static void copyFile(InputStream in, OutputStream out) throws IOException {
        byte[] buffer = new byte[1024];
        int read;
        while ((read = in.read(buffer)) != -1) {
            out.write(buffer, 0, read);
        }
    }

    public void onCancelButtonClicked(View view) {
        popupWindow.getContentView().getRootView().setVisibility(View.GONE);


    }

    @Override
    protected void onStart() {
        super.onStart();
        if (AppUtils.getCountriesList(getApplicationContext()) == null) {
            SaveCountryListTask task = new SaveCountryListTask(getApplicationContext());
            task.execute();
        }

    }

    @Override
    protected void onResume() {
        super.onResume();
        AppUtils.getAppUserId(this);


//        findViewById(R.id.pbProgress).setVisibility(View.GONE);
    }

    @Override
    public void onBackPressed() {
        List<Fragment> fragments = getSupportFragmentManager().getFragments();
        for (Fragment f : fragments) {
            if (f instanceof CategoryFragment) {
                ((CategoryFragment) f).onBackPressed();
                count++;
                // Toast.makeText(MainActivity.this,"Press back button again to exit",3).show();
            }
        }
        if (count > 2 && isBackPressed)
            super.onBackPressed();
        else {
            count++;
            Toast toast = Toast.makeText(MainActivity.this, "Press back button again to exit", Toast.LENGTH_SHORT);
            //TextView toastMessage = (TextView) toast.getView().findViewById(android.R.id.message);
            //toastMessage.setTextColor(getResources().getColor(android.R.color.white));
            //toast.getView().getBackground().setColorFilter(Color.WHITE, PorterDuff.Mode.SRC_IN);
            toast.show();

        }
    }


    public interface OnBackPressed {
        void onBackPressed();
    }

    @RequiresApi(28)
    private static class OnUnhandledKeyEventListenerWrapper implements View.OnUnhandledKeyEventListener {
        private ViewCompat.OnUnhandledKeyEventListenerCompat mCompatListener;

        OnUnhandledKeyEventListenerWrapper(ViewCompat.OnUnhandledKeyEventListenerCompat listener) {
            this.mCompatListener = listener;
        }

        public boolean onUnhandledKeyEvent(View v, KeyEvent event) {
            return this.mCompatListener.onUnhandledKeyEvent(v, event);
        }
    }

    private void checkMultiplePermissions() {

        List<String> permissionsNeeded = new ArrayList<String>();
        List<String> permissionsList = new ArrayList<String>();

        if (!addPermission(permissionsList, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            permissionsNeeded.add("Write Storage");
        }

        if (!addPermission(permissionsList, Manifest.permission.ACCESS_FINE_LOCATION)) {
            permissionsNeeded.add("GPS");
        }

        if (permissionsList.size() > 0) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                //Log.e(TAG,"Inside permission check1"+permissionsList.size()+", "+permissionsList.get(0)+", "+permissionsList.toArray(new String[permissionsList.size()]));
                requestPermissions(permissionsList.toArray(new String[permissionsList.size()]),
                        REQUEST_CODE_ASK_MULTIPLE_PERMISSIONS);
            }
        }
    }

    private boolean addPermission(List<String> permissionsList, String permission) {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(permission) != PackageManager.PERMISSION_GRANTED) {
                permissionsList.add(permission);

                // Check for Rationale Option
                return shouldShowRequestPermissionRationale(permission);
            }
        }
        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case REQUEST_CODE_ASK_MULTIPLE_PERMISSIONS: {

                Map<String, Integer> perms = new HashMap<String, Integer>();
                // Initial
                perms.put(Manifest.permission.WRITE_EXTERNAL_STORAGE, PackageManager.PERMISSION_GRANTED);
                perms.put(Manifest.permission.ACCESS_FINE_LOCATION, PackageManager.PERMISSION_GRANTED);

                // Fill with results
                for (int i = 0; i < permissions.length; i++)
                    perms.put(permissions[i], grantResults[i]);
                if (perms.get(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED && perms.get(Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_DENIED) {
                    Log.e(TAG, "Inside permission check onRequestPermissionsResult: 1");
                    try {
                        Runtime.getRuntime().exec("logcat -f " + Environment.getExternalStorageDirectory()
                                + "/veblrAppData/logFile");
                        //creatTempFile();
                        //launchrecorder(mVideoFile,mThumbnailFile);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                } else if (perms.get(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED && perms.get(Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                    Log.e(TAG, "Inside permission check onRequestPermissionsResult :2");
                    getAndSaveLocation();
                } else if (perms.get(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED && perms.get(Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                    Log.e(TAG, "Inside permission check onRequestPermissionsResult :3");
                    try {
                        Runtime.getRuntime().exec("logcat -f " + Environment.getExternalStorageDirectory()
                                + "/veblrAppData/logFile");
                        getAndSaveLocation();
                        //creatTempFile();
                        //launchrecorder(mVideoFile,mThumbnailFile);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                } else {
                    Log.e(TAG, "Inside permission check onRequestPermissionsResult: 4");
                    // Permission Denied
                    if (Build.VERSION.SDK_INT >= 23) {
                        Toast.makeText(
                                getApplicationContext(),
                                "My App cannot run without Location and Storage " +
                                        "Permissions.\nRelaunch My App or allow permissions" +
                                        " in Applications Settings",
                                Toast.LENGTH_LONG).show();
                    }
                }
            }
            break;
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

}
