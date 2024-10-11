package com.veblr.android.veblrapp.repositories;

import static com.veblr.android.veblrapp.util.Constants.SEARCHFROM_FOLLOWED_CHANNEL;
import static com.veblr.android.veblrapp.util.Constants.SEARCHFROM_LOCATION;
import static com.veblr.android.veblrapp.util.Constants.SEARCHFROM_RECOMMENDED;

import android.content.Context;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import com.veblr.android.veblrapp.datasource.ResponseHomeFeed;
import com.veblr.android.veblrapp.datasource.ResponseVideoList;
import com.veblr.android.veblrapp.model.ApiError;
import com.veblr.android.veblrapp.model.VIdeoItem;
import com.veblr.android.veblrapp.network.ApiInterface;
import com.veblr.android.veblrapp.network.ApiService;
import com.veblr.android.veblrapp.util.AppUtils;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HomeRepository {

    private final String TAG = HomeRepository.class.getSimpleName();
    private final Context context;
    private int choice;
    private List<VIdeoItem> videoItemArrayList = new ArrayList<>();
    private Call<ResponseVideoList> responseVideoListCall;

    public HomeRepository(Context context, int choice) {
        this.context = context;
        this.choice = choice;
    }

    public List<VIdeoItem> getSearchedHomeList() {

        switch (choice) {
            case SEARCHFROM_FOLLOWED_CHANNEL:
                if (AppUtils.getAppUserId(context) != null) {
                    responseVideoListCall = ApiService.getService(context).create(ApiInterface.class)
                            .getVideolistByFollowedChannel(AppUtils.getJSonOBJForRecommendate(context));
                } else {
                    responseVideoListCall = ApiService.getService(context).create(ApiInterface.class)
                            .getVideoList(AppUtils.getJSonOBJForHome());
                }
                break;

            case SEARCHFROM_LOCATION:
                if (AppUtils.getAppUserId(context) != null) {
                    responseVideoListCall = ApiService.getService(context).create(ApiInterface.class)
                            .getVideolistByFollowedChannel(AppUtils.getJSonOBJForRecommendate(context));
                } else {
                    responseVideoListCall = ApiService.getService(context).create(ApiInterface.class)
                            .getVideoList(AppUtils.getJSonOBJForHome());
                }
                break;

            case SEARCHFROM_RECOMMENDED:
                if (AppUtils.getAppUserId(context) != null) {
                    Log.e(TAG, "getSearchedHomeList: not null");
                    responseVideoListCall = ApiService.getService(context).create(ApiInterface.class)
                            .getVideolistByFollowedChannel(AppUtils.getJSonOBJForRecommendate(context));
                } else {
                    Log.e(TAG, "getSearchedHomeList: null");
                    responseVideoListCall = ApiService.getService(context).create(ApiInterface.class)
                            .getVideoList(AppUtils.getJSonOBJForHome());
                }
                break;
        }

        responseVideoListCall.enqueue(new Callback<ResponseVideoList>() {
            @Override
            public void onResponse(Call<ResponseVideoList> call, Response<ResponseVideoList> response) {
                Log.e(TAG, "getSearchedHomeList: onResponse Code:" + response.code());
                if (response.isSuccessful()) {
                    try {
                        videoItemArrayList = response.body().getResonse().getResult();
                        Log.e(TAG, "getSearchedHomeList: onResponse Size:" + videoItemArrayList.size()+", "+videoItemArrayList.get(0).getVideoTitle());
                        AppUtils.saveFavorites(context, videoItemArrayList);

                  /*  for (VIdeoItem v:vIdeoItemArrayList) {
                        new VideoRepository(context).insertVideo(v);
                    }*/
                    } catch (NullPointerException e) {
                        Log.d(TAG, "getSearchedHomeList: EXCEPTION:" + e.getMessage());
                        getIndexPage();
                    }
                } else {
                    getIndexPage();
                    //Gson gson = new Gson();
                    //Type type = new TypeToken<ApiError>() {
                    //}.getType();
              /*  ApiError errorResponse = gson.fromJson(response.errorBody().charStream(),type);
                Log.e("Error",errorResponse.getError().getMessage());*/
                }
            }

            @Override
            public void onFailure(Call<ResponseVideoList> call, Throwable t) {
                Log.e(TAG, "getSearchedHomeList: onFailure:" + t.getMessage());
                getIndexPage();
            }
        });
        return videoItemArrayList;
    }

    private void getIndexPage() {
        Log.e(TAG, "getSearchedHomeList: getIndexPage called");
        JsonObject obj = new JsonObject();
        JsonObject payerReg = new JsonObject();

        payerReg.addProperty("max_results", "100");
        payerReg.addProperty("cache_data_chk", false);
        obj.add("param", payerReg);

        Call<ResponseHomeFeed> responseVideoListCall = ApiService.getService(context).create(ApiInterface.class)
                .getVideoListForWebHomePage(obj);

        responseVideoListCall.enqueue(new Callback<ResponseHomeFeed>() {
            @Override
            public void onResponse(Call<ResponseHomeFeed> call, Response<ResponseHomeFeed> response) {
                Log.e(TAG, "getIndexPage: onResponse Code:" + response.code());
                if (response.isSuccessful()) {
                    try {
                        List<VIdeoItem> videoItemList = response.body().getResponse().getResult().getIndexTop().getVideos();
                        videoItemList.addAll(response.body().getResponse().getResult().getIndexRight().getVideos());
                        videoItemList.addAll(response.body().getResponse().getResult().getIndexMiddle1().getVideos());
                        videoItemList.addAll(response.body().getResponse().getResult().getIndexMiddle2().getVideos());
                        videoItemList.addAll(response.body().getResponse().getResult().getIndexLeft1().getVideos());
                        videoItemList.addAll(response.body().getResponse().getResult().getIndexLeft2().getVideos());
                        videoItemList.addAll(response.body().getResponse().getResult().getIndexBottom1().getVideos());
                        videoItemList.addAll(response.body().getResponse().getResult().getIndexBottom2().getVideos());
                        videoItemArrayList.addAll(videoItemList);

                        Log.e(TAG, "getIndexPage: onResponse Size:" + videoItemArrayList.size());
                        AppUtils.saveFavorites(context, videoItemArrayList);

                  /*  for (VIdeoItem v:vIdeoItemArrayList) {
                        new VideoRepository(context).insertVideo(v);
                    }*/
                    } catch (NullPointerException e) {
                        Log.d(TAG, "getIndexPage: EXCEPTION:" + e.getMessage());
                    }
                }

            }

            @Override
            public void onFailure(Call<ResponseHomeFeed> call, Throwable t) {
                Log.e(TAG, "getIndexPage: onFailure" + t.getMessage());
            }
        });
    }
}