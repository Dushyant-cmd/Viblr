package com.veblr.android.veblrapp.ui;

import android.annotation.SuppressLint;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.UiThread;
import androidx.appcompat.app.ActionBar;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestManager;
import com.bumptech.glide.request.RequestOptions;
import com.facebook.ads.AdError;
import com.facebook.ads.NativeAdsManager;
import com.google.gson.JsonObject;
import com.veblr.android.veblrapp.R;
import com.veblr.android.veblrapp.datasource.ResponseHomeFeed;
import com.veblr.android.veblrapp.datasource.ResponseVideoList;
import com.veblr.android.veblrapp.model.Channel;
import com.veblr.android.veblrapp.model.ChannelDetailResponse;
import com.veblr.android.veblrapp.model.VIdeoItem;
import com.veblr.android.veblrapp.network.ApiInterface;
import com.veblr.android.veblrapp.network.ApiService;
import com.veblr.android.veblrapp.util.AppUtils;
import com.veblr.android.veblrapp.util.VerticalSpacingItemDecorator;
import com.veblr.android.veblrapp.videoplayer.AutoVideoPlayerRecyclerView;
import com.veblr.android.veblrapp.videoplayer.AutoVideoPlayerRecyclerViewAdapter;
import com.veblr.android.veblrapp.viewmodel.HomeViewModel;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HomeFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener {

    private static final String TAG = HomeFragment.class.getSimpleName();
    //private static ActionBar actionBar1;
    private static List<VIdeoItem> mediaObjects1 = new ArrayList<>();
    private AutoVideoPlayerRecyclerView mRecyclerView;
    private SwipeRefreshLayout swipeRefreshLayout;
    private AutoVideoPlayerRecyclerViewAdapter adapter;
    private NativeAdsManager mNativeAdsManager;

    public static HomeFragment newInstance(ActionBar actionbar, List<VIdeoItem> mediaObjects) {
        //actionBar1 = actionbar;
        mediaObjects1 = mediaObjects;
        return new HomeFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        final View view = inflater.inflate(R.layout.home_fragment, container, false);
        mRecyclerView = view.findViewById(R.id.rvHomelist);
        swipeRefreshLayout = view.findViewById(R.id.swipe);
        swipeRefreshLayout.setColorSchemeResources(R.color.light_blue_600);
        //Hide From Layout By Rakesh
        //ProgressBar pb = view.findViewById(R.id.pb);
        int colorCodeDark = Color.parseColor("#FFFFFF");
        //pb.setIndeterminateTintList(ColorStateList.valueOf(colorCodeDark));
        swipeRefreshLayout.setOnRefreshListener(this);
        //swipeRefreshLayout.setRefreshing(true);

        if (getActivity() != null)
            ((LinearLayout) getActivity().findViewById(R.id.llPB)).setVisibility(View.VISIBLE);

        mNativeAdsManager = new NativeAdsManager(getActivity(), getString(R.string.facebook_placement_id), 90);
        mNativeAdsManager.loadAds();
        mNativeAdsManager.setListener(new NativeAdsManager.Listener() {
            @Override
            public void onAdsLoaded() {

            }

            @Override
            public void onAdError(AdError adError) {

            }
        });

        /*if (AppUtils.getAppUserId(getActivity()) == null)
            Toast.makeText(getActivity(), "Android id is: " + AppUtils.getDeviceUniqueId(getActivity()),
                    Toast.LENGTH_LONG).show();*/

       /* ((NestedScrollView)view.findViewById(R.id.scrollCustom)).setSmoothScrollingEnabled(true);
        ((NestedScrollView)view.findViewById(R.id.scrollCustom)).setOnScrollChangeListener(new NestedScrollView.OnScrollChangeListener() {
            @Override
            public void onScrollChange(NestedScrollView v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
                if (scrollY == (v.getChildAt(0).getMeasuredHeight() - v.getMeasuredHeight()))
                {
                    getIndexPage(true);
                }
            }
        });*/
        initRecyclerView();
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        HomeViewModel mViewModel = ViewModelProviders.of(this).get(HomeViewModel.class);
    }

    public void initRecyclerView() {
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        mRecyclerView.setLayoutManager(layoutManager);
        VerticalSpacingItemDecorator itemDecorator = new VerticalSpacingItemDecorator(5);
        mRecyclerView.addItemDecoration(itemDecorator);
        // ArrayList<VIdeoItem> mediaObjects = new ArrayList<VIdeoItem>(Arrays.asList(AppUtils.MEDIA_OBJECTS));

        if (AppUtils.getFavorites(requireActivity()) != null) {
            //Log.e(TAG, "calling getFeaturedChannels: getFavorites not null inside initRecyclerView mediaObjects1: "+mediaObjects1.size());
            mediaObjects1 = AppUtils.getFavorites(requireActivity());
            Log.e(TAG, "inside initRecyclerView: mediaObjects1 size: :"+mediaObjects1.size());

            mRecyclerView.setMediaObjects(mediaObjects1);
            adapter = new AutoVideoPlayerRecyclerViewAdapter(mNativeAdsManager, getContext(), mediaObjects1, initGlide(), true);
            mRecyclerView.setAdapter(adapter);
            //Log.e(TAG, "calling getFeaturedChannels: First inside initRecyclerView");
            getFeaturedChannels();

            if (getActivity() != null)
                ((LinearLayout) getActivity().findViewById(R.id.llPB)).setVisibility(View.GONE);
        }
        Log.e(TAG, "calling getList: First inside initRecyclerView");
        getList();

        //final boolean[] scroll_down = new boolean[1];
        mRecyclerView.setOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                //if (actionBar1 != null) {
                  /*  if (scroll_down[0]) {
                     actionBar.hide();
                            } else {
                        actionBar.show();
                    }*/
                //}
                if (!recyclerView.canScrollVertically(1))
                    getIndexPage(true);
            }

            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                /*if (dy > 10) {
                    //scroll down
                    scroll_down[0] = true;
                } else if (dy < -5) {
                    //scroll ucap
                    scroll_down[0] = false;
                }
                else if(dy==0){
                            mRecyclerView.resumePlayer();
                }*/
            }
        });
        /*mRecyclerView.getViewTreeObserver()
                .addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                    @Override
                    public void onGlobalLayout() {
                        //At this point the layout is complete and the
                        //dimensions of recyclerView and any child views are known.
                        //Remove listener after changed RecyclerView's height to prevent infinite loop
                        mRecyclerView.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                    }
                });*/
        swipeRefreshLayout.setRefreshing(false);
        final Handler handler = new Handler(Looper.getMainLooper());
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (AppUtils.getAppUserId(requireActivity()) == null) {
                    Log.e(TAG, "Create New User From onCreateView");
                    AppUtils.createNewUser(getContext(), AppUtils.getDeviceUniqueId(requireActivity()));
                }
            }
        }, 15000);
    }

    private RequestManager initGlide() {
        RequestOptions options = new RequestOptions()
                .override(AppUtils.getDeviceWidth(), (int) (AppUtils.getDeviceWidth() * 56) / 100)
                .centerCrop()
                .placeholder(R.drawable.white_background)
                .error(R.drawable.white_background);

        return Glide.with(this)
                .setDefaultRequestOptions(options);
    }

    @Override
    public void onDestroy() {
        if (mRecyclerView != null)
            mRecyclerView.releasePlayer();
        super.onDestroy();
    }

    @Override
    public void onResume() {
        super.onResume();
        if (mRecyclerView != null)
            mRecyclerView.resumePlayer();
        if (mRecyclerView != null)
            mRecyclerView.resetVideoView();
        //List<String> likedvideos = AppUtils.getLikedVideos(requireActivity());
        if (AppUtils.getAppUserId(requireActivity()) == null) {
            Log.e(TAG, "Create New User From onResume");
            AppUtils.createNewUser(getContext(), AppUtils.getDeviceUniqueId(requireActivity()));
        }
        //if (likedvideos == null) {
            try {
                Call<ResponseVideoList> responseVideoListCall;
                if (AppUtils.getRegisteredUserId(requireActivity()) != null) {
                    Log.e(TAG, "Inside onResume likedVideo is null if user not null:");
                    responseVideoListCall =
                            ApiService.getService(getContext()).create(ApiInterface.class)
                                    .getLikedVideosListBYChannel(AppUtils.getJSonOBJForLikedVideoListReg(Objects.requireNonNull(AppUtils.getAppUserId(requireActivity())).getGuestUserId(), Objects.requireNonNull(AppUtils.getRegisteredUserId(requireActivity())).getUserId(),
                                            Objects.requireNonNull(AppUtils.getRegisteredUserId(requireActivity())).getChannel().get(0).getChId()));
                } else {
                    Log.e(TAG, "Inside onResume likedVideo is null:"+AppUtils.getAppUserId(requireActivity()).getGuestUserId());
                    responseVideoListCall =
                            ApiService.getService(getContext()).create(ApiInterface.class)
                                    .getLikedVideosList(AppUtils.getJSonOBJForLikedVideoList
                                            (AppUtils.getAppUserId(requireActivity()).getGuestUserId(), ""));
                }
                responseVideoListCall.enqueue(new Callback<ResponseVideoList>() {
                    @SuppressLint("NotifyDataSetChanged")
                    @Override
                    public void onResponse(Call<ResponseVideoList> call, Response<ResponseVideoList> response) {
                        Log.e(TAG, "onResume liked is null onResponse:"+response.code());
                        if (response.isSuccessful() && response.body().getResonse() != null) {
                            for (VIdeoItem v : response.body().getResonse().getResult()) {
                                List<String> likedVideoList = new ArrayList<>();
                                likedVideoList.add(v.getVideoId());
                                //Log.e(TAG, "onResume liked is null onResponse LikedVideo Size:"+likedVideoList.size()+", "+likedVideoList.get(1));
                                AppUtils.saveLikedVideo(requireActivity(), likedVideoList);
                                if (adapter != null) {
                                    adapter.notifyDataSetChanged();
                                }
                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<ResponseVideoList> call, Throwable t) {

                    }
                });
            } catch (NullPointerException e) {
                e.printStackTrace();
            }
        /*}else{
            Log.e(TAG, "Inside onResume likedVideo else:"+likedvideos.get(0));
        }*/
    }

    @Override
    public void onPause() {
        if (mRecyclerView != null)
            mRecyclerView.releasePlayer();
        super.onPause();
    }

    @Override
    public void onRefresh() {
        if(swipeRefreshLayout!=null && !swipeRefreshLayout.isRefreshing()) {
            swipeRefreshLayout.post(new Runnable() {
                @Override
                public void run() {
                    swipeRefreshLayout.setEnabled(false);
                    swipeRefreshLayout.setRefreshing(true);
                }
            });
        }
        if (AppUtils.getAppUserId(requireActivity()) == null) {
            Log.e(TAG, "onRefresh: Create New User From onCreateView");
            AppUtils.createNewUser(getContext(), AppUtils.getDeviceUniqueId(requireActivity()));
            final Handler handler = new Handler(Looper.getMainLooper());
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    if (AppUtils.getAppUserId(requireActivity()) == null) {
                        Log.e(TAG, "calling getList: Inside OnRefresh");
                        getList();
                    }
                }
            }, 12000);
        }else {
            Log.e(TAG, "calling getList: Inside OnRefresh");
            getList();
        }
        /*if (getContext() != null)
            Toast.makeText(getContext(), "Android id is: " + AppUtils.getDeviceUniqueId(getContext()),
                    Toast.LENGTH_LONG).show();*/
        //getFeaturedChannels();
    }

    private void getList() {
        Call<ResponseVideoList> responseVideoListCall;
        if (AppUtils.getAppUserId(requireActivity()) != null &&
                AppUtils.getFavoriteUser(requireActivity()) != null) {
            Log.e(TAG, "getList: Calling getVideolistByFollowedChannel");
            responseVideoListCall = ApiService.getService(getContext()).create(ApiInterface.class)
                    .getVideolistByFollowedChannel(AppUtils.getJSonOBJForRecommendate(getContext()));
        }else if (AppUtils.getAppUserId(requireActivity()) != null){
            responseVideoListCall = ApiService.getService(getContext()).create(ApiInterface.class)
                    .getVideolistByFollowedChannel(AppUtils.getJSonOBJForRecommendate(getContext()));
        }
        else {
            Log.e(TAG, "getList: Calling getVideoList");
            responseVideoListCall = ApiService.getService(getContext()).create(ApiInterface.class)
                    .getVideoList(AppUtils.getJSonOBJForHome());
        }
        responseVideoListCall.enqueue(new Callback<ResponseVideoList>() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onResponse(Call<ResponseVideoList> call, Response<ResponseVideoList> response) {
                Log.e(TAG, "getList: onResponse Code:" + response.code());
                swipeRefreshLayout.setRefreshing(false);
                if (response.isSuccessful()) {
                    //   Toast.makeText(getContext(),"No new Videos",Toast.LENGTH_LONG).show();
                    try {
                        if (response.body().getResonse().getResult() != null) {
                            //mediaObjects1.clear();
                            mediaObjects1 = response.body().getResonse().getResult();
                            // Collections.shuffle(mediaObjects);
                            AppUtils.saveFavorites(getContext(), mediaObjects1);
                            Log.e(TAG, "getList: onResponse mediaObjects1 size:" + mediaObjects1.size());
                            mRecyclerView.setMediaObjects(mediaObjects1);
                            Log.e(TAG, "calling getFeaturedChannels: From Inside getList Response");
                            getFeaturedChannels();

                            LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
                            mRecyclerView.setLayoutManager(layoutManager);
                            adapter = new AutoVideoPlayerRecyclerViewAdapter(mNativeAdsManager, getContext(), mediaObjects1, initGlide(), true);
                            mRecyclerView.setAdapter(adapter);
                            adapter.notifyDataSetChanged();

                            if (getActivity() != null)
                                ((LinearLayout) getActivity().findViewById(R.id.llPB)).setVisibility(View.GONE);
                           /* for (VIdeoItem v:mediaObjects) {
                                new VideoRepository(getContext()).insertVideo(v);
                            }*/
                        }
                    } catch (NullPointerException e) {
                        Log.e(TAG,"getList: EXCEPTION: "+ e.getMessage());
                        if (AppUtils.getFavorites(requireActivity()) != null) {
                            mediaObjects1 = AppUtils.getFavorites(requireActivity());
                            mRecyclerView.setMediaObjects(AppUtils.getFavorites(requireActivity()));
                        }
                    }
                } else {
                    getIndexPage(false);
                    //Gson gson = new Gson();
                    //Type type = new TypeToken<ApiError>() {
                    //}.getType();
                    //ApiError errorResponse = gson.fromJson(response.errorBody().charStream(),type);
                    //Log.e("Error",errorResponse.getError().getMessage());
                }
            }

            @Override
            public void onFailure(Call<ResponseVideoList> call, Throwable t) {
                Log.e(TAG, "getList: onFailure: "+t.getMessage());
                getIndexPage(false);
            }
        });
    }

    @UiThread
    public void getIndexPage(final boolean frombottom) {

        Log.e(TAG, "getList: getIndexPage called");
        JsonObject obj = new JsonObject();
        JsonObject payerReg = new JsonObject();
        payerReg.addProperty("max_results", "100");
        payerReg.addProperty("cache_data_chk", false);
        obj.add("param", payerReg);

        Call<ResponseHomeFeed> responseVideoListCall =
                ApiService.getService(getContext()).create(ApiInterface.class).getVideoListForWebHomePage(obj);

        responseVideoListCall.enqueue(new Callback<ResponseHomeFeed>() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onResponse(Call<ResponseHomeFeed> call, Response<ResponseHomeFeed> response) {
                Log.e(TAG, "getIndexPage: onResponse Code:" + response.code());
                if (response.isSuccessful()) {
                    try {
                        //Log.d(TAG,"getIndexPage: RESPONSE: "+ response.body().getResponse().getStatus());

                        if (response.body().getResponse().getResult() != null) {
                            if (frombottom) {
                                List<VIdeoItem> vIdeoItemList = response.body().getResponse().getResult().getIndexTop().getVideos();
                                vIdeoItemList.addAll(response.body().getResponse().getResult().getIndexRight().getVideos());
                                vIdeoItemList.addAll(response.body().getResponse().getResult().getIndexMiddle1().getVideos());
                                vIdeoItemList.addAll(response.body().getResponse().getResult().getIndexMiddle2().getVideos());
                                vIdeoItemList.addAll(response.body().getResponse().getResult().getIndexLeft1().getVideos());
                                vIdeoItemList.addAll(response.body().getResponse().getResult().getIndexLeft2().getVideos());
                                vIdeoItemList.addAll(response.body().getResponse().getResult().getIndexBottom1().getVideos());
                                vIdeoItemList.addAll(response.body().getResponse().getResult().getIndexBottom2().getVideos());
                                mediaObjects1.clear();
                                mediaObjects1.addAll(mediaObjects1.size() - 1, vIdeoItemList);
                                Log.e(TAG, "getIndexPage: onResponse mediaObject1 Size:" + mediaObjects1.size());//+", "+mediaObjects1.get(1).getVideoTitle());
                                mRecyclerView.addMediaObjects(mediaObjects1);
                                adapter.notifyDataSetChanged();
                                  /* adapter.notifyItemInserted(mediaObjects1.size() - 1);
                                    adapter.notifyItemRangeChanged(0,
                                          mediaObjects1.size()+1+response.body().getResonse().getResult().size());
                             */
                            } else {
                                //Log.e(TAG,"getIndexPage: Title:"+mediaObjects1.size()+", "+response.body().getResponse().getResult().getIndexTop().getVideos().get(1).getVideoTitle());
                                List<VIdeoItem> vIdeoItemList = new ArrayList<>();
                                vIdeoItemList.addAll(response.body().getResponse().getResult().getIndexTop().getVideos());
                                vIdeoItemList.addAll(response.body().getResponse().getResult().getIndexRight().getVideos());
                                vIdeoItemList.addAll(response.body().getResponse().getResult().getIndexMiddle1().getVideos());
                                vIdeoItemList.addAll(response.body().getResponse().getResult().getIndexMiddle2().getVideos());
                                vIdeoItemList.addAll(response.body().getResponse().getResult().getIndexLeft1().getVideos());
                                vIdeoItemList.addAll(response.body().getResponse().getResult().getIndexLeft2().getVideos());
                                vIdeoItemList.addAll(response.body().getResponse().getResult().getIndexBottom1().getVideos());
                                vIdeoItemList.addAll(response.body().getResponse().getResult().getIndexBottom2().getVideos());
mediaObjects1.clear();
                                //if(!mediaObjects1.contains(vIdeoItemList)){
                                    mediaObjects1.addAll(vIdeoItemList);
                                //}

                                Log.e(TAG, "getIndexPage onResponse mediaObject1 Size:"+mediaObjects1.size());
                                // Collections.shuffle(mediaObjects);
                                AppUtils.saveFavorites(getContext(), mediaObjects1);
                                mRecyclerView.setMediaObjects(mediaObjects1);
                                Log.e(TAG, "getFeaturedChannels calling: getIndexPage onResponse");
                                getFeaturedChannels();
                                LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
                                mRecyclerView.setLayoutManager(layoutManager);
                                adapter.notifyDataSetChanged();
                                mRecyclerView.setAdapter(adapter);
                                if (getActivity() != null)
                                    ((LinearLayout) getActivity().findViewById(R.id.llPB)).setVisibility(View.GONE);
                           /* for (VIdeoItem v:mediaObjects) {
                                new VideoRepository(getContext()).insertVideo(v);
                            }*/
                            }
                        }
                    } catch (NullPointerException e) {
                        Log.e(TAG,"getIndexPage: EXCEPTION:"+ e.getMessage());
                        if (AppUtils.getFavorites(requireActivity()) != null) {
                            mediaObjects1 = AppUtils.getFavorites(requireActivity());
                            mRecyclerView.setMediaObjects(mediaObjects1);
                        }
                    }
                }
            }

            @Override
            public void onFailure(Call<ResponseHomeFeed> call, Throwable t) {
Log.e(TAG, "getIndexPage: onFailure: "+t.getMessage());
            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();
        Log.e(TAG,"getFeaturedChannels: called in onStart");
        getFeaturedChannels();
    }

    //Comment By Rakesh
    /*public static ProgressBar getPB() {
        return pb;
    }*/

    public void getFeaturedChannels() {
        if (AppUtils.getFeaturedUser(requireActivity()) == null) {
            Log.e(TAG,"getFeaturedChannels: inside getFeaturedChannels");
            Call<ChannelDetailResponse> channelsCall = ApiService.getService(getContext()).create(ApiInterface.class)
                    .getFeaturedChannels(AppUtils.getJSonOBJForFeaturedChannels("10"));
            channelsCall.enqueue(new Callback<ChannelDetailResponse>() {
                @Override
                public void onResponse(Call<ChannelDetailResponse> call, Response<ChannelDetailResponse> response) {
                    Log.e(TAG,"getFeaturedChannels: inside getFeaturedChannels onResponse:"+response.code());
                    if (response.isSuccessful()) {
                        List<Channel> userList = response.body().getUSERDetailResponse().getResultList();
                        AppUtils.saveFeatured(requireActivity(), userList);
                        Log.e(TAG,"getFeaturedChannels: inside getFeaturedChannels onResponse userList:"+userList.size()+", "+userList.get(1).getChNameDisp());
                        adapter.setUserList(userList);
                    }
                }

                @Override
                public void onFailure(Call<ChannelDetailResponse> call, Throwable t) {
                    Log.e(TAG,"getFeaturedChannels: onFailure: "+ t.getMessage());
                }
            });
        }else {
           // Log.e(TAG,"getFeaturedChannels: inside else:"+AppUtils.getFeaturedUser(requireActivity()).get(1).getChImage());
        }
        // else adapter.setUserList(AppUtils.getFeaturedUser(Objects.requireNonNull(getContext())));
    }
}
