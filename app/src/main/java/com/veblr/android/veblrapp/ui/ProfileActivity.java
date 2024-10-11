package com.veblr.android.veblrapp.ui;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatToggleButton;
import androidx.core.view.ViewCompat;
import androidx.core.widget.NestedScrollView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestManager;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.facebook.ads.AdError;
import com.facebook.ads.NativeAdsManager;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import com.veblr.android.veblrapp.R;
import com.veblr.android.veblrapp.adapter.ListOfVideosAdapter;
import com.veblr.android.veblrapp.datasource.ResponseHomeFeed;
import com.veblr.android.veblrapp.datasource.ResponseVideoList;
import com.veblr.android.veblrapp.model.ApiError;
import com.veblr.android.veblrapp.model.Channel;
import com.veblr.android.veblrapp.model.ChannelDetailResponse;
import com.veblr.android.veblrapp.model.VIdeoItem;
import com.veblr.android.veblrapp.network.ApiInterface;
import com.veblr.android.veblrapp.network.ApiService;
import com.veblr.android.veblrapp.util.AppUtils;
import com.veblr.android.veblrapp.videoplayer.AutoVideoPlayerRecyclerView;
import com.veblr.android.veblrapp.videoplayer.AutoVideoPlayerRecyclerViewAdapter;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.graphics.Typeface.BOLD;

public class ProfileActivity extends AppCompatActivity {

    RecyclerView rvListOfUserVideos;
    TextView tvUserAbout;
    Button btnMore;
    androidx.appcompat.widget.AppCompatToggleButton followButton;
    boolean moreButtonVisible = true;
    NestedScrollView scrollView;
    AutoVideoPlayerRecyclerView rvVideoListOfUser;
    Channel channel;
    private NativeAdsManager mNativeAdsManager;
    AutoVideoPlayerRecyclerViewAdapter adapter;
    List<VIdeoItem> vIdeoItemArrayList =new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        rvListOfUserVideos = (RecyclerView)findViewById(R.id.rvListOfUserVodeos);
        rvVideoListOfUser = (AutoVideoPlayerRecyclerView)findViewById(R.id.rvListOfUserVideos);
        rvListOfUserVideos.setVisibility(View.GONE);
        rvVideoListOfUser.setVisibility(View.VISIBLE);
        scrollView = (NestedScrollView) findViewById(R.id.scrollView);
//        (NestedScrollView)findViewById(R.id.scrollView)
        setTitleBar();
        tvUserAbout = (TextView)findViewById(R.id.tvAboutUser);
        btnMore = (Button)findViewById(R.id.btnMore);
        followButton = (AppCompatToggleButton)findViewById(R.id.btnFollowChannel);
        if(moreButtonVisible)
        tvUserAbout.setSingleLine(true);
        findViewById(R.id.collapsingLayout).requestFocus();


        if(getIntent().getSerializableExtra("channel")!=null) {
            channel = (Channel) getIntent().getSerializableExtra("channel");
            setUserData(channel);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                setfollowButton();
            }
        }
        String placement_id = "424758711275495_730941363990560";

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            mNativeAdsManager = new NativeAdsManager(this, placement_id, 90);
        }
        mNativeAdsManager.loadAds();
        mNativeAdsManager.setListener(new NativeAdsManager.Listener() {
            @Override
            public void onAdsLoaded() {

            }

            @Override
            public void onAdError(AdError adError) {

            }
        });
        initRecyclerview();
       // getFollowingORNot(getIntent().getStringExtra("channelLink"));

        followButton.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
            @Override
            public void onClick(View view) {

                boolean b= true;
                for (Channel c: Objects.requireNonNull(AppUtils.getFavoriteUser(ProfileActivity.this))) {
                    if(Objects.equals(c.getChId(),channel.getChId()))
                    {
                        b=false;
                        break;
                    }
                }
                if(b){
                    followButton.setChecked(true);
                    followButton.setTextColor(Color.WHITE);
                   Toast toast  =  Toast.makeText(ProfileActivity.this, "You now follow "
                           + channel.getChNameDisp() + "", Toast.LENGTH_LONG);
                    TextView toastMessage=(TextView) toast.getView().findViewById(android.R.id.message);
                    toastMessage.setTextColor(getResources().getColor(android.R.color.white));
                    toast.show();
                }
                else{
                    followButton.setChecked(false);
                    followButton.setTextColor(Color.parseColor("#0a90ed"));
                   Toast toast= Toast.makeText(ProfileActivity.this,
                           channel.getChNameDisp()+" is unfollowed by you",Toast.LENGTH_LONG);
                    TextView toastMessage=(TextView) toast.getView().findViewById(android.R.id.message);
                    toastMessage.setTextColor(getResources().getColor(android.R.color.white));                    toast.getView().getBackground().setColorFilter(Color.WHITE, PorterDuff.Mode.SRC_IN);
                    toast.show();

                }
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                    addittofollowingList(channel,b);
                }
            }
        });



        rvListOfUserVideos.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
            }

            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (!recyclerView.canScrollVertically(1)) {
                        if (AppUtils.getFavorites(getApplicationContext()) != null) {
                            List<VIdeoItem> vIdeoItemList = AppUtils.getFavorites(getApplicationContext());
                            vIdeoItemArrayList.addAll(vIdeoItemArrayList.size() - 1, vIdeoItemList);
                            rvVideoListOfUser.addMediaObjects(vIdeoItemArrayList);
                            adapter.notifyDataSetChanged();
                        } else {

                            JsonObject obj = new JsonObject();
                            JsonObject payerReg = new JsonObject();
                            payerReg.addProperty("max_results", "100");
                            payerReg.addProperty("cache_data_chk", false);
                            obj.add("param", payerReg);
                            Call<ResponseHomeFeed> responseVideoListCall =
                                    ApiService.getService(getApplicationContext()).create(ApiInterface.class)
                                            .getVideoListForWebHomePage(obj);
                            responseVideoListCall.enqueue(new Callback<ResponseHomeFeed>() {
                                @Override
                                public void onResponse(Call<ResponseHomeFeed> call,
                                                       Response<ResponseHomeFeed> response) {
                                    if (response.isSuccessful()) {
                                        try {
                                            Log.e("RESPONSE", response.body().getResponse().getStatus() + "");

                                            if (response.body().getResponse().getResult() != null) {
                                                List<VIdeoItem> vIdeoItemList = response.body().getResponse().getResult().getIndexTop().getVideos();
                                                vIdeoItemList.addAll( response.body().getResponse().getResult().getIndexRight().getVideos());
                                                vIdeoItemList.addAll( response.body().getResponse().getResult().getIndexMiddle1().getVideos());
                                                vIdeoItemList.addAll( response.body().getResponse().getResult().getIndexMiddle2().getVideos());
                                                vIdeoItemList.addAll( response.body().getResponse().getResult().getIndexLeft1().getVideos());
                                                vIdeoItemList.addAll( response.body().getResponse().getResult().getIndexLeft2().getVideos());
                                                vIdeoItemList.addAll( response.body().getResponse().getResult().getIndexBottom1().getVideos());
                                                vIdeoItemList.addAll( response.body().getResponse().getResult().getIndexBottom2().getVideos());
                                                vIdeoItemArrayList.addAll(vIdeoItemArrayList.size()-1,vIdeoItemList);
                                                rvVideoListOfUser.addMediaObjects(vIdeoItemArrayList);
                                                adapter.notifyDataSetChanged();

                                            }

                                        }catch (NullPointerException e){e.printStackTrace();}
                                    }
                                }

                                @Override
                                public void onFailure(Call<ResponseHomeFeed> call, Throwable t) {

                                }
                            });
                        }
                    }

            }});

    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private void addittofollowingList(Channel channel, boolean b) {


            Call<com.veblr.android.veblrapp.model.Response> responseCall;
            if(b) {  if(AppUtils.getRegisteredUserId(ProfileActivity.this)!=null){
                responseCall =
                        ApiService.getService(ProfileActivity.this).create(ApiInterface.class)
                                .followaChannel (AppUtils.getJSonOBJForFolloworUnfollowReg(channel.getChId(),
                                        Objects.requireNonNull(AppUtils.getAppUserId(ProfileActivity.this)).getGuestUserId(),Objects.requireNonNull(AppUtils.getRegisteredUserId(ProfileActivity.this).getUserId()),AppUtils.getRegisteredUserId(ProfileActivity.this).getChannel().get(0).getChId()));

            }
            else {
                responseCall =
                        ApiService.getService(ProfileActivity.this).create(ApiInterface.class)
                                .followaChannel(AppUtils.getJSonOBJForFolloworUnfollow(channel.getChId(), Objects.requireNonNull(AppUtils.getAppUserId(ProfileActivity.this)).getGuestUserId()));
            }


                AppUtils.addFavoriteUser(ProfileActivity.this,channel);

            }
            else {
                if(AppUtils.getRegisteredUserId(ProfileActivity.this)!=null){
                    responseCall =
                            ApiService.getService(ProfileActivity.this).create(ApiInterface.class)
                                    .unFollowaChannel (AppUtils.getJSonOBJForFolloworUnfollowReg(channel.getChId(),
                                            Objects.requireNonNull(AppUtils.getAppUserId(ProfileActivity.this)).getGuestUserId(),Objects.requireNonNull(AppUtils.getRegisteredUserId(ProfileActivity.this).getUserId()),AppUtils.getRegisteredUserId(ProfileActivity.this).getChannel().get(0).getChId()));

                }
                else {
                    responseCall =
                            ApiService.getService(ProfileActivity.this).create(ApiInterface.class)
                                    .unFollowaChannel(AppUtils.getJSonOBJForFolloworUnfollow(channel.getChId(), Objects.requireNonNull(AppUtils.getAppUserId(ProfileActivity.this)).getGuestUserId()));
                }
                AppUtils.removeFavoriteUser(ProfileActivity.this,channel.getChId());

            }
            responseCall.enqueue(new Callback<com.veblr.android.veblrapp.model.Response>() {
                @Override
                public void onResponse(Call<com.veblr.android.veblrapp.model.Response> call, Response<com.veblr.android.veblrapp.model.Response> response) {
                    if(response.isSuccessful()){

                    }
                }
                @Override
                public void onFailure(Call<com.veblr.android.veblrapp.model.Response> call, Throwable t) {

                }
            });
        }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private void setfollowButton() {
        if(AppUtils.getFavoriteUser(ProfileActivity.this)!=null) {
            for (Channel c : Objects.requireNonNull(AppUtils.getFavoriteUser(ProfileActivity.this))) {
                if (Objects.equals(c.getChId(), channel.getChId())) {
                    followButton.setChecked(true);
                    followButton.setTextColor(Color.WHITE);
                    break;
                } else {
                    followButton.setChecked(false);
                    followButton.setTextColor(Color.parseColor("#0a90ed"));
                }
            }


        }
    }

    private void setTitleBar() {


        if(getSupportActionBar()!=null)
        {
            getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);

            LayoutInflater inflater = (LayoutInflater) getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            assert inflater != null;
            View v = inflater.inflate(R.layout.register_actionabar, null);
            v.findViewById(R.id.btnBack).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ProfileActivity.super.onBackPressed();

                }
            });
            ActionBar.LayoutParams p = new ActionBar.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    Gravity.CENTER);
            ((TextView) v.findViewById(R.id.tvActionBarTitle)).setTextSize(14);
            ((TextView) v.findViewById(R.id.tvActionBarTitle)).setTextColor(getResources().getColor(R.color.profile_title_textcolor));
            ((TextView) v.findViewById(R.id.tvActionBarTitle)).setTypeface(Typeface.defaultFromStyle(BOLD));
            ((TextView) v.findViewById(R.id.tvActionBarTitle)).setText("");

         //   ((ImageView)v.findViewById(R.id.btnBack)).setImageDrawable(getResources().getDrawable(R.drawable.ic_icon_back_black));
            getSupportActionBar().setCustomView(v, p);
            getSupportActionBar().setDisplayShowTitleEnabled(true);
            //  getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setStatusBarColor(Color.WHITE);
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        }

    }

    private void setUserData(Channel channel) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            tvUserAbout.setText(Html.fromHtml(channel.getChAbout()+"", Html.FROM_HTML_MODE_LEGACY));
        } else {
            tvUserAbout.setText(Html.fromHtml(channel.getChAbout()+"").toString());
        }
        //tvUserAbout.setText(channel.getChAbout());
        Glide.with(this).asBitmap()
                .load(channel.getImage())
                .into(new SimpleTarget<Bitmap>() {
                    @Override
                    public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {

                        ((ImageView)findViewById(R.id.ivChannelIcon)).setImageBitmap(resource);
                    }
                });
        /*LayoutInflater inflater = (LayoutInflater) getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View v = inflater.inflate(R.layout.register_actionabar, null);
       ((TextView) v.findViewById(R.id.tvActionBarTitle)).setText("");*/
        ((TextView)findViewById(R.id.tvUserID)).setText(channel.getChNameDisp());

        LayoutInflater inflater = (LayoutInflater) getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View v = inflater.inflate(R.layout.register_actionabar, null);
        v.findViewById(R.id.btnBack).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ProfileActivity.super.onBackPressed();

            }
        });
        ActionBar.LayoutParams p = new ActionBar.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT,
                Gravity.CENTER);
        v.setBackgroundColor(getResources().getColor(android.R.color.white));
        ((TextView) v.findViewById(R.id.tvActionBarTitle)).setTextSize(14);
        ((TextView) v.findViewById(R.id.tvActionBarTitle)).setTextColor(getResources().getColor(R.color.profile_title_textcolor));
        ((TextView) v.findViewById(R.id.tvActionBarTitle)).setTypeface(Typeface.defaultFromStyle(BOLD));
        ((TextView) v.findViewById(R.id.tvActionBarTitle)).setText(channel.getChNameDisp());

        //   ((TextView)findViewById(R.id.tvJoinedDate)).setText(channel.get());
        if(channel.getChTotalVideos()==null) ( (TextView)findViewById(R.id.tvNoOfVideos)).setText("0");
            else ( (TextView)findViewById(R.id.tvNoOfVideos)).setText(channel.getChTotalVideos()+"  videos");

        if(channel.getChTotalVideoViews()==null) ( (TextView)findViewById(R.id.tvNoOfViews)).setText("0");
        else ( (TextView)findViewById(R.id.tvNoOfViews)).setText(channel.getChTotalVideoViews()+"  views");

        if(channel.getChTotalFollowing()==null) ( (TextView)findViewById(R.id.tvNoOfFollowing)).setText("0");
        else ( (TextView)findViewById(R.id.tvNoOfFollowing)).setText(channel.getChTotalFollowing());

        if(channel.getChTotalFollower()==null) ( (TextView)findViewById(R.id.tvNoOfFollowers)).setText("0");
        else ( (TextView)findViewById(R.id.tvNoOfFollowers)).setText(channel.getChTotalFollower());


    }

    private void initRecyclerview() {
        rvListOfUserVideos.setFocusable(false);

      /*  ScrollerCompat mScroller = ScrollerCompat.create(ProfileActivity.this, null);
        scrollView.setFocusable(true);
        scrollView.setDescendantFocusability(FOCUS_AFTER_DESCENDANTS);
        scrollView.setWillNotDraw(false);
        final ViewConfiguration configuration = ViewConfiguration.get(ProfileActivity.this);
       int mTouchSlop = configuration.getScaledTouchSlop();
       int mMinimumVelocity = configuration.getScaledMinimumFlingVelocity();
       int  mMaximumVelocity = configuration.getScaledMaximumFlingVelocity();*/

        LinearLayoutManager layoutManager = new LinearLayoutManager(ProfileActivity.this,RecyclerView.VERTICAL,false);
        /*rvListOfUserVideos.setLayoutManager(new LinearLayoutManager(ProfileActivity.this,RecyclerView.VERTICAL,false){
            @Override
            public boolean canScrollHorizontally() {
                return false;
            }

            @Override
            public boolean canScrollVertically() {
                return false;
            }
        });

        rvListOfUserVideos.setLayoutManager(layoutManager);
        ArrayList<VIdeoItem> mediaObjects = new ArrayList<VIdeoItem>(Arrays.asList(AppUtils.MEDIA_OBJECTS));
        // rvListOfUserVideos.smoothScrollBy(0,0);
        ListOfVideosAdapter listOfVideosAdapter = new ListOfVideosAdapter(ProfileActivity.this,initGlide());
        rvListOfUserVideos.setAdapter(listOfVideosAdapter);
        ViewCompat.setNestedScrollingEnabled(rvListOfUserVideos, false);*/
        rvVideoListOfUser.setLayoutManager(layoutManager);
        ViewCompat.setNestedScrollingEnabled(rvVideoListOfUser, true);
        if(getIntent().getStringExtra("channelLink")!=null){
        Call<ResponseVideoList>  responseVideoListCall = ApiService.getService(ProfileActivity.this).create(ApiInterface.class)
              .getVideoListForChannels(AppUtils.getJSonOBJForFollwedChannels(getIntent()
                      .getStringExtra("channelLink") ));
        responseVideoListCall.enqueue(new Callback<ResponseVideoList>() {
            @Override
            public void onResponse(Call<ResponseVideoList> call, Response<ResponseVideoList> response) {

                if(response.isSuccessful()) {
                    try {
                         vIdeoItemArrayList = response.body().getResonse().getResult();
                        Log.e("RESPONSE From REpo",response.body().getResonse().getStatus()+"");
                        rvVideoListOfUser.setMediaObjects(vIdeoItemArrayList);
                        ArrayList<VIdeoItem> arrayList = new ArrayList<VIdeoItem>(vIdeoItemArrayList);
                       adapter = new AutoVideoPlayerRecyclerViewAdapter
                                (mNativeAdsManager,ProfileActivity.this,vIdeoItemArrayList,
                                        AppUtils.initGlide(getApplicationContext()),false);
                        rvVideoListOfUser.setAdapter(adapter);
                        ((ProgressBar)findViewById(R.id.pbProgress)).setVisibility(View.GONE);
                    } catch (NullPointerException e) {
                        Log.e("EXCEPTION", e.getMessage());
                    }
                }
                else{
                    Gson gson = new Gson();
                    Type type = new TypeToken<ApiError>() {}.getType();
                    ApiError errorResponse = gson.fromJson(response.errorBody().charStream(),type);
                    Log.e("Error",errorResponse.getError().getMessage());
                }
            }
            @Override
            public void onFailure(Call<ResponseVideoList> call, Throwable t) {

            }

        });
        }
    }

    private RequestManager initGlide() {
        RequestOptions options =
            new RequestOptions()
            .centerCrop()
            .placeholder(R.drawable.white_background)
            .error(R.drawable.white_background);

        return Glide.with(this)
                .setDefaultRequestOptions(options);
    }

    public void OnMoreButtonClicked(View view)
    {
        moreButtonVisible=false;
        btnMore.setVisibility(View.GONE);
        tvUserAbout.setSingleLine(false);
        tvUserAbout.setMaxLines(18);
        tvUserAbout.setWidth(AppUtils.getDeviceWidth());

    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    void getFollowingORNot(final String videoChannelLink){
        final boolean[] following = {false};
        if(AppUtils.getAppUserId(this)!=null) {
            Call<ChannelDetailResponse> channelDetailResponseCall;
            if(AppUtils.getRegisteredUserId(ProfileActivity.this)!=null){
                channelDetailResponseCall = ApiService.getService(ProfileActivity.this)
                        .create(ApiInterface.class).getaUsersFollowingListBYChannel
                                (AppUtils.getJSonOBJForFollowingListReg
                                        (Objects.requireNonNull(AppUtils.getAppUserId(ProfileActivity.this)).getGuestUserId(),Objects.requireNonNull(AppUtils.getRegisteredUserId(ProfileActivity.this).getUserId()),AppUtils.getRegisteredUserId(ProfileActivity.this).getChannel().get(0).getChId()));

            }
            else {
                channelDetailResponseCall = ApiService.getService(ProfileActivity.this)
                        .create(ApiInterface.class).getaUsersFollowingList(AppUtils.getJSonOBJForFollowingList(Objects.requireNonNull(AppUtils.getAppUserId(ProfileActivity.this)).getGuestUserId(), ""));
            }

            channelDetailResponseCall.enqueue(new Callback<ChannelDetailResponse>() {
                @Override
                public void onResponse(Call<ChannelDetailResponse> call, Response<ChannelDetailResponse> response) {
                    if (response.isSuccessful() && response.body().getUSERDetailResponse() != null) {
                        List<Channel> channels = response.body().getUSERDetailResponse().getResultList();
                        Log.e("chlink", channels.get(0).getChName() + "");

                        for (Channel c : channels) {
                            Log.e("chlink", c.getChName() + " " + videoChannelLink);
                            if (c.getChName().equals(videoChannelLink)) {
                                following[0] = true;
                                followButton.setBackgroundResource(R.drawable.followed_bg);
                                followButton.setTextColor(Color.WHITE);
                                followButton.setText("Following");
                            }
                        }
                    }
                }
                @Override
                public void onFailure(Call<ChannelDetailResponse> call, Throwable t) {

                }
            });
        }
    }
}
