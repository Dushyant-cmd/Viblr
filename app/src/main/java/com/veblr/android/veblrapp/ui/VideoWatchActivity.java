package com.veblr.android.veblrapp.ui;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.PowerManager;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.adcolony.sdk.AdColony;
import com.adcolony.sdk.AdColonyAdOptions;
import com.adcolony.sdk.AdColonyAppOptions;
import com.adcolony.sdk.AdColonyInterstitial;
import com.adcolony.sdk.AdColonyInterstitialListener;
import com.adcolony.sdk.AdColonyZone;
import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestManager;
import com.bumptech.glide.request.RequestOptions;
import com.facebook.ads.Ad;
import com.facebook.ads.AdError;
import com.facebook.ads.AdSettings;
import com.facebook.ads.AdSize;
import com.facebook.ads.AdView;
import com.facebook.ads.CacheFlag;
import com.facebook.ads.InterstitialAd;
import com.facebook.ads.InterstitialAdListener;
import com.google.android.exoplayer2.ui.PlayerView;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.FullScreenContentCallback;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback;
import com.google.gson.JsonObject;
import com.veblr.android.veblrapp.R;
import com.veblr.android.veblrapp.adapter.OnSwipeTouchListener;
import com.veblr.android.veblrapp.datasource.ResponseVideoItem;
import com.veblr.android.veblrapp.datasource.ResponseVideoList;
import com.veblr.android.veblrapp.model.VIdeoItem;
import com.veblr.android.veblrapp.network.ApiInterface;
import com.veblr.android.veblrapp.network.ApiService;
import com.veblr.android.veblrapp.network.InterstitialFragment;
import com.veblr.android.veblrapp.util.AppUtils;
import com.veblr.android.veblrapp.videoplayer.PlayerManager;

import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumSet;
import java.util.List;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.facebook.ads.AdSettings.IntegrationErrorMode.INTEGRATION_ERROR_CRASH_DEBUG_MODE;
import static com.veblr.android.veblrapp.util.Constants.ADCOLONY_APP_ID;
import static java.util.TimeZone.SHORT;

public class VideoWatchActivity extends AppCompatActivity
        implements VideoWatchFragment.OnFragmentInteractionListener, InterstitialAdListener {

    private final String TAG = "VideoWatchActivity";
    List<VIdeoItem> vIdeoItemList = new ArrayList<VIdeoItem>();
    View view;
    FragmentTransaction ft1, ft2;
    VideoWatchFragment fragment;
    int currentItemIndex = 0;
    boolean fromComments = false;
    protected PowerManager.WakeLock mWakeLock;
    String videoID;
    int countSwipe = 0;
    RelativeLayout relativeLayout;
    private InterstitialAd interstitialAd;
    AdColonyInterstitial ad;
    private AdColonyInterstitialListener listener;
    private AdColonyAdOptions adOptions;
    private final String ZONE_ID = "vz5576ea28e1d5421bb";
    private com.google.android.gms.ads.interstitial.InterstitialAd mInterstitialAd;
    AdView adView;

    @SuppressLint("InvalidWakeLockTag")
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_watch);
        NotificationManager notificationmanager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        // Construct optional app options object to be sent with configure
        AdColonyAppOptions appOptions = new AdColonyAppOptions()
                //.setUserID("unique_user_id")
                //.setTestModeEnabled(true)
                .setKeepScreenOn(true);
        //AdColony.configure(VideoWatchActivity.this, ADCOLONY_APP_ID, "vz5576ea28e1d5421bb9");//Commented By Rakesh
        // Configure AdColony in your launching Activity's onCreate() method so that cached ads can
        // be available as soon as possible.
        AdColony.configure(this, appOptions, ADCOLONY_APP_ID);
        // Ad specific options to be sent with request
        adOptions = new AdColonyAdOptions();

        relativeLayout = (RelativeLayout) findViewById(R.id.rlAd);
        // Example for setting the SDK to crash when in debug mode

        showFbAds();

        // Collections.shuffle(vIdeoItemList.subList(1,vIdeoItemList.size()-1));
        view = LayoutInflater.from(this).inflate(R.layout.fragment_video_watch, null);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);

            LayoutInflater inflater = (LayoutInflater) getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            assert inflater != null;
            View v = inflater.inflate(R.layout.video_watch_actionbar, null);
            ImageView btnBack = v.findViewById(R.id.btnBack);
            btnBack.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    VideoWatchActivity.this.finish();
                   /* VideoWatchActivity.super.onBackPressed();
                    startActivity(new Intent(VideoWatchActivity.this,MainActivity.class));*/
                }
            });
            ImageView btnSearch = v.findViewById(R.id.btnSearch);
            btnSearch.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivity(new Intent(VideoWatchActivity.this, SearchActivity.class));
                }
            });
            ImageView btnProfile = v.findViewById(R.id.btnUserIcon);
            btnProfile.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {

                    if (AppUtils.getRegisteredUserId(VideoWatchActivity.this) == null)
                        startActivity(new Intent(VideoWatchActivity.this, LoginActivity.class));
                    else {
                        startActivity(new Intent(VideoWatchActivity.this, UserProfileActivity.class));

                    }
                }
            });

            ActionBar.LayoutParams p = new ActionBar.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    Gravity.CENTER_VERTICAL);

            getSupportActionBar().setCustomView(v, p);
            //  getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        getWindow().setStatusBarColor(Color.BLACK);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_LAYOUT_FLAGS);
        }
        final PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
        assert pm != null;
        this.mWakeLock = pm.newWakeLock(PowerManager.SCREEN_DIM_WAKE_LOCK, "My Tag");
        this.mWakeLock.acquire(10000000);
    /*
       mRecyclerView = findViewById(R.id.rvHorizontalView);
      //  Intent i = new Intent();
      if(vIdeoItemList!=null)
            Log.d("intent",vIdeoItemList.get(0).getVideoId());
        mPaginVideoWatchFragmentationAdapter = new HorizontalRecyclerViewAdapter(vIdeoItemList,VideoWatchActivity.this);
        mRecyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(this);
        mLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        mRecyclerView.addItemDecoration(new EqualSpacingItemDecoration(10, EqualSpacingItemDecoration.HORIZONTAL));
        SnapHelper snapHelper  =new PagerSnapHelper();
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setHasFixedSize(true);
        snapHelper.attachToRecyclerView(mRecyclerView);

        mRecyclerView.setAdapter(mPaginVideoWatchFragmentationAdapter);
        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
               int i =  mLayoutManager.findFirstCompletelyVisibleItemPosition();
                Log.d("CURRENT POSITION",mLayoutManager.findFirstCompletelyVisibleItemPosition()+"");

            }
        });*/

        vIdeoItemList = (List<VIdeoItem>) getIntent().getSerializableExtra("videoList");
        if (getIntent().getStringExtra("video_id") != null)
            videoID = getIntent().getStringExtra("video_id");
        fromComments = getIntent().getBooleanExtra("fromComments", false);
        showVideo(videoID);

        adView = new AdView(this, getString(R.string.facebook_placement_id), AdSize.BANNER_HEIGHT_50);

// Find the Ad Container
        LinearLayout adContainer = (LinearLayout) findViewById(R.id.banner_container);

// Add the ad view to your activity layout
        adContainer.addView(adView);

// Request an ad
        adView.loadAd();


    }


    public static RequestManager initGlide(Context c) {
        RequestOptions options = new RequestOptions()
                .centerCrop()
                .placeholder(R.drawable.white_background)
                .error(R.drawable.white_background);

        return Glide.with(c)
                .setDefaultRequestOptions(options);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        handleIntent(intent);

    }

    @Override
    protected void onPause() {
        super.onPause();
      /*  if(mPaginVideoWatchFragmentationAdapter.getHorizontalRecyclerViewHolder()!=null)
            mPaginVideoWatchFragmentationAdapter.getHorizontalRecyclerViewHolder().getPlayer().getPlayer().release();
*/
    }

    @Override
    protected void onResume() {
        super.onResume();

        /*if(mPaginVideoWatchFragmentationAdapter.getHorizontalRecyclerViewHolder()!=null)
        mPaginVideoWatchFragmentationAdapter.getHorizontalRecyclerViewHolder().getPlayer().getPlayer().seekTo(0);
   */
    }

    @Override
    public void onBackPressed() {
        VideoWatchActivity.this.finish();
       /* VideoWatchActivity.super.onBackPressed();
        startActivity(new Intent(VideoWatchActivity.this,MainActivity.class));
*/
    }

    @Override
    public void onFragmentInteraction(Uri uri) {
    }

    public void swipeRight() {
        ft1.remove(fragment);
        ft2 = getSupportFragmentManager().beginTransaction();
        ft2.setCustomAnimations(R.animator.slide_in, R.animator.slide_out);
        if (currentItemIndex <= vIdeoItemList.size() - 1)
            currentItemIndex++;
        VideoWatchFragment fragment = VideoWatchFragment.newInstance(vIdeoItemList.get(currentItemIndex + 1), fromComments);
        ft2.replace(R.id.flFragment, fragment);
        ft2.commit();
    }

    //TODO:player slideview properly
    public void setSwipeListener(final View fromFragment) {
        OnSwipeTouchListener onSwipeTouchListener = new OnSwipeTouchListener(VideoWatchActivity.this) {
            @Override
            public void onSwipeRight() {
                super.onSwipeRight();//Wrong

                Log.e("SWIPE LEFT", "swiped left");
                if (ft2 != null) ft2.remove(fragment);
                ft1 = getSupportFragmentManager().beginTransaction();
                ft1.setCustomAnimations(R.anim.enter_from_left, R.anim.exit_to_right);//, R.anim.enter_from_right, R.anim.exit_to_left);

                //ft1.setCustomAnimations(R.animator.fragment_slide_left_enter,
                  //      R.animator.fragment_slide_left_exit);
                ft1.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
                if (currentItemIndex == 0) {
                    Toast toast = Toast.makeText(VideoWatchActivity.this, "No more videos on the Left",
                            Toast.LENGTH_LONG);
                    TextView toastMessage = (TextView) toast.getView().findViewById(android.R.id.message);
                    toastMessage.setTextColor(getResources().getColor(android.R.color.black));
                    toast.getView().getBackground().setColorFilter(Color.WHITE, PorterDuff.Mode.SRC_IN);
                    toast.show();
                } else {
                    currentItemIndex = currentItemIndex - 1;
                    fragment = VideoWatchFragment.newInstance(vIdeoItemList.get(currentItemIndex), fromComments);
                    ft1.replace(R.id.flFragment, fragment);
                    ft1.commit();
                }
                if (countSwipe == 3 || countSwipe % 3 == 0) {

                    AdColony.requestInterstitial(ZONE_ID, new AdColonyInterstitialListener() {
                        @Override
                        public void onRequestFilled(AdColonyInterstitial adColonyInterstitial) {
                            Log.d(TAG, "onRequestFilled");
                            // Ad passed back in request filled callback, ad can now be shown
                            ad = adColonyInterstitial;
                            ad.show();
                        }

                        @Override
                        public void onRequestNotFilled(AdColonyZone zone) {
                            // Ad request was not filled
                            Log.d(TAG, "onRequestNotFilled");
                            showFbAds();
                            super.onRequestNotFilled(zone);
                        }

                        @Override
                        public void onOpened(AdColonyInterstitial ad) {
                            // Ad opened, reset UI to reflect state change
                            Log.d(TAG, "onOpened");
                        }

                        @Override
                        public void onExpiring(AdColonyInterstitial ad) {
                            Log.d(TAG, "onExpiring");
                            // Request a new ad if ad is expiring
                            AdColony.requestInterstitial(ZONE_ID, this, adOptions);
                        }
                    }, adOptions);
                    //Comment By Rakesh
                    /*AdColony.requestInterstitial("vz5576ea28e1d5421bb9", new AdColonyInterstitialListener() {
                        @Override
                        public void onRequestFilled(AdColonyInterstitial adColonyInterstitial) {
                            ad = adColonyInterstitial;
                            ad.show();

                        }

                        @Override
                        public void onRequestNotFilled(AdColonyZone zone) {
                            if (interstitialAd != null) {
                                interstitialAd.destroy();
                                interstitialAd = null;
                            }
                            interstitialAd = new InterstitialAd(VideoWatchActivity.this,
                                    "424758711275495_731462617271768");
                            interstitialAd.setAdListener(VideoWatchActivity.this);
                            interstitialAd.loadAd(EnumSet.of(CacheFlag.VIDEO));
                            super.onRequestNotFilled(zone);
                        }
                    });*/


                }
                countSwipe++;
            }

            @Override
            public void onSwipeLeft() {
                Log.e("SWIPE RIGHT", "swiped RIGHT");

                super.onSwipeLeft();

                ft1.remove(fragment);
                ft2 = getSupportFragmentManager().beginTransaction();
                ft2.setCustomAnimations(R.animator.slide_in, R.animator.slide_out);
                if (currentItemIndex <= vIdeoItemList.size() - 1)
                    currentItemIndex++;
                VideoWatchFragment fragment = VideoWatchFragment.newInstance(vIdeoItemList.get(currentItemIndex + 1), fromComments);
                ft2.replace(R.id.flFragment, fragment);
                ft2.commit();

                if (countSwipe == 3 || countSwipe % 3 == 0) {

                    AdColony.requestInterstitial(ZONE_ID, new AdColonyInterstitialListener() {
                        @Override
                        public void onRequestFilled(AdColonyInterstitial adColonyInterstitial) {
                            Log.d(TAG, "onRequestFilled");
                            // Ad passed back in request filled callback, ad can now be shown
                            ad = adColonyInterstitial;
                            ad.show();
                        }

                        @Override
                        public void onRequestNotFilled(AdColonyZone zone) {
                            // Ad request was not filled
                            Log.e(TAG, "onRequestNotFilled");
                            showFbAds();
                            Log.e(TAG, "onRequestNotFilled 3");
                            super.onRequestNotFilled(zone);
                        }

                        @Override
                        public void onOpened(AdColonyInterstitial ad) {
                            // Ad opened, reset UI to reflect state change
                            Log.d(TAG, "onOpened");
                        }

                        @Override
                        public void onExpiring(AdColonyInterstitial ad) {
                            Log.d(TAG, "onExpiring");
                            // Request a new ad if ad is expiring
                            AdColony.requestInterstitial(ZONE_ID, this, adOptions);
                        }
                    }, adOptions);
                    /*AdColony.requestInterstitial("vz5576ea28e1d5421bb9", new AdColonyInterstitialListener() {
                        @Override
                        public void onRequestFilled(AdColonyInterstitial adColonyInterstitial) {
                            ad = adColonyInterstitial;
                            ad.show();

                        }

                        @Override
                        public void onRequestNotFilled(AdColonyZone zone) {
                            if (interstitialAd != null) {
                                interstitialAd.destroy();
                                interstitialAd = null;
                            }
                            interstitialAd = new InterstitialAd(VideoWatchActivity.this,
                                    "424758711275495_731462617271768");
                            interstitialAd.setAdListener(VideoWatchActivity.this);
                            interstitialAd.loadAd(EnumSet.of(CacheFlag.VIDEO));
                            super.onRequestNotFilled(zone);
                        }
                    });*/
                }
                countSwipe++;
            }
/*
        @Override
        public void onClick() {
            super.onClick();
            ((PlayerView)fromFragment.findViewById(R.id.player_view))
                    .setControllerVisibilityListener(new PlayerControlView.VisibilityListener() {
                                                                                                          @Override
                                                                                                          public void onVisibilityChange(int visibility) {
                                                                                                              if(visibility==View.VISIBLE)
                                                                                                                  ((PlayerView)fromFragment.findViewById(R.id.player_view)).hideController();
                                                                                                              else  ((PlayerView)fromFragment.findViewById(R.id.player_view)).showController();

                                                                                                          }
                                                                                                      });

        }*/
        };
        fromFragment.setOnTouchListener(onSwipeTouchListener);
        fromFragment.findViewById(R.id.ns).setOnTouchListener(onSwipeTouchListener);
        fromFragment.findViewById(R.id.rvCommentList).setOnTouchListener(onSwipeTouchListener);
    }

    @Override
    protected void onDestroy() {
        this.mWakeLock.release();
        if (interstitialAd != null) {
            interstitialAd.destroy();
        }
        if (adView != null) {
            adView.destroy();
        }
        super.onDestroy();
    }

    @Override
    public void onInterstitialDisplayed(Ad ad) {
        Log.e(TAG, "onRequestNotFilled 7");
    }

    @Override
    public void onInterstitialDismissed(Ad ad) {
        Log.e(TAG, "onRequestNotFilled 6");
    }

    @Override
    public void onError(Ad ad, AdError adError) {
        Log.e(TAG, "onRequestNotFilled 5" + adError.getErrorMessage() + "," + adError.getErrorCode());
        //No fill. We are not able to serve ads to this person
        //Ad was re-loaded too frequently,1002
        //https://developers.facebook.com/docs/audience-network/testing
        //https://developers.facebook.com/docs/audience-network/faq#a12
        //showAdMobs();
    }

    @Override
    public void onAdLoaded(Ad ad) {
        Log.e(TAG, "onRequestNotFilled 4");
        if (interstitialAd == null || !interstitialAd.isAdLoaded()) {
            // Ad not ready to show.
            relativeLayout.setVisibility(View.GONE);
        } else {
            // Ad was loaded, show it!
            interstitialAd.show();
            relativeLayout.setVisibility(View.VISIBLE);

        }
    }

    @Override
    public void onAdClicked(Ad ad) {
        Log.e(TAG, "onRequestNotFilled 8");
    }

    @Override
    public void onLoggingImpression(Ad ad) {
        Log.e(TAG, "onRequestNotFilled 9");
    }


    private void handleIntent(Intent intent) {
        String appLinkAction = intent.getAction();
        Uri appLinkData = intent.getData();
        if (Intent.ACTION_VIEW.equals(appLinkAction) && appLinkData != null) {
            String videoId = appLinkData.getLastPathSegment();
            Uri appData = Uri.parse("content://com.veblr.android.veblrapp.ui.VideoWatchActivity").buildUpon()
                    .appendPath(videoId).build();
            showVideo(videoId);
        }
    }

    private void showVideo(String videoId) {
        if (videoID != null) {
            JsonObject obj = new JsonObject();
            JsonObject payerReg = new JsonObject();
            payerReg.addProperty("video_id", videoID);
            payerReg.addProperty("auto_play_video", "true");
            payerReg.addProperty("auto_play_video_next", "true");
            payerReg.addProperty("recommended_channel_name", "default");
            payerReg.addProperty("recommended_playlist_id", "true");
            payerReg.addProperty("auto_play_video", "371d959e");
            payerReg.addProperty("cache_data_chk", false);
            obj.add("param", payerReg);
            Call<ResponseVideoItem> responseVideoItemCall = ApiService.getService(VideoWatchActivity.this)
                    .create(ApiInterface.class).getVideoDetails(obj);
            responseVideoItemCall.enqueue(new Callback<ResponseVideoItem>() {
                @Override
                public void onResponse(Call<ResponseVideoItem> call, Response<ResponseVideoItem> response) {
                    if (response.isSuccessful() && response.body().getResponse().getResult() != null) {
                        VIdeoItem vIdeoItem = response.body().getResponse().getResult();
                        List<VIdeoItem> videolist = new ArrayList<>();
                        videolist.add(0, vIdeoItem);

                        if (AppUtils.getFavorites(getApplicationContext()) != null) {
                            videolist.addAll(Objects.requireNonNull(AppUtils.getFavorites(getApplicationContext())));
                        }
                        vIdeoItemList = new ArrayList<>();
                        vIdeoItemList.addAll(videolist);
                        ft1 = getSupportFragmentManager().beginTransaction();
                        fragment = VideoWatchFragment.newInstance(vIdeoItemList.get(0), fromComments);
                        ft1.replace(R.id.flFragment, fragment);
                        ft1.commit();

                    }
                }

                @Override
                public void onFailure(Call<ResponseVideoItem> call, Throwable t) {

                }
            });

        } else {
            ft1 = getSupportFragmentManager().beginTransaction();
            if (vIdeoItemList != null) {
                fragment = VideoWatchFragment.newInstance(vIdeoItemList.get(0), fromComments);
                ft1.replace(R.id.flFragment, fragment);
                ft1.commit();
                countSwipe++;
            }
        }
        if (vIdeoItemList == null && videoId != null) {
            JsonObject obj = new JsonObject();
            JsonObject payerReg = new JsonObject();
            payerReg.addProperty("video_id", videoId);
            payerReg.addProperty("auto_play_video", "true");
            payerReg.addProperty("auto_play_video_next", "true");
            payerReg.addProperty("recommended_channel_name", "default");
            payerReg.addProperty("recommended_playlist_id", "true");
            payerReg.addProperty("auto_play_video", "371d959e");
            payerReg.addProperty("cache_data_chk", false);
            obj.add("param", payerReg);
            Call<ResponseVideoItem> responseVideoItemCall = ApiService.getService(VideoWatchActivity.this)
                    .create(ApiInterface.class).getVideoDetails(obj);
            responseVideoItemCall.enqueue(new Callback<ResponseVideoItem>() {
                @Override
                public void onResponse(Call<ResponseVideoItem> call, Response<ResponseVideoItem> response) {
                    if (response.isSuccessful() && response.body().getResponse().getResult() != null) {
                        VIdeoItem vIdeoItem = response.body().getResponse().getResult();
                        List<VIdeoItem> videolist = new ArrayList<>();
                        videolist.add(0, vIdeoItem);
                        if (AppUtils.getFavorites(getApplicationContext()) != null) {
                            videolist.addAll(Objects.requireNonNull(AppUtils.getFavorites(getApplicationContext())));
                        }
                        ft1 = getSupportFragmentManager().beginTransaction();
                        fragment = VideoWatchFragment.newInstance(videolist.get(0), fromComments);
                        ft1.replace(R.id.flFragment, fragment);
                        ft1.commit();
                    }
                }

                @Override
                public void onFailure(Call<ResponseVideoItem> call, Throwable t) {

                }
            });
        }

    }

    public void showAdMobs(){
        AdRequest adRequest = new AdRequest.Builder().build();

        com.google.android.gms.ads.interstitial.InterstitialAd.load(this, "ca-app-pub-3940256099942544/1033173712", adRequest,
                new InterstitialAdLoadCallback() {
                    @Override
                    public void onAdLoaded(@NonNull com.google.android.gms.ads.interstitial.InterstitialAd interstitialAd) {
                        super.onAdLoaded(interstitialAd);
                        // The mInterstitialAd reference will be null until
                        // an ad is loaded.
                        mInterstitialAd = interstitialAd;
                        Log.i(TAG, "onAdLoaded");
                    }
                    @Override
                    public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                        // Handle the error
                        Log.d(TAG, loadAdError.toString());
                        mInterstitialAd = null;
                    }
                });

        mInterstitialAd.setFullScreenContentCallback(new FullScreenContentCallback(){
            @Override
            public void onAdClicked() {
                // Called when a click is recorded for an ad.
                Log.e(TAG, "Ad was clicked.");
            }

            @Override
            public void onAdDismissedFullScreenContent() {
                // Called when ad is dismissed.
                // Set the ad reference to null so you don't show the ad a second time.
                Log.e(TAG, "Ad dismissed fullscreen content.");
                mInterstitialAd = null;
            }

            @Override
            public void onAdImpression() {
                // Called when an impression is recorded for an ad.
                Log.e(TAG, "Ad recorded an impression.");
            }

            @Override
            public void onAdShowedFullScreenContent() {
                // Called when ad is shown.
                Log.e(TAG, "Ad showed fullscreen content.");
                if (mInterstitialAd != null) {
                    mInterstitialAd.show(VideoWatchActivity.this);
                } else {
                    Log.d("TAG", "The interstitial ad wasn't ready yet.");
                }
            }
        });

    }

    public void showFbAds() {
        if (interstitialAd != null) {
            interstitialAd.destroy();
            interstitialAd = null;
        }
        interstitialAd = new InterstitialAd(this, "424758711275495_731462617271768");
        //interstitialAd.setAdListener(VideoWatchActivity.this);
        //interstitialAd.loadAd(EnumSet.of(CacheFlag.VIDEO));
        interstitialAd.loadAd(
                interstitialAd.buildLoadAdConfig()
                        .withAdListener(this)
                        .build());
        /*Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            public void run() {
                // Check if interstitialAd has been loaded successfully
                if(interstitialAd == null || !interstitialAd.isAdLoaded()) {
                    return;
                }
                // Check if ad is already expired or invalidated, and do not show ad if that is the case. You will not get paid to show an invalidated ad.
                if(interstitialAd.isAdInvalidated()) {
                    return;
                }
                // Show the ad
                interstitialAd.show();
            }
        }, 1000 * 60 * 1);*/

    }
}