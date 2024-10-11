package com.veblr.android.veblrapp.datasource;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.text.HtmlCompat;
import androidx.paging.PageKeyedDataSource;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;
import com.veblr.android.veblrapp.model.ApiError;
import com.veblr.android.veblrapp.model.Body;
import com.veblr.android.veblrapp.model.JSONBody;
import com.veblr.android.veblrapp.model.JSONBody.*;
import com.veblr.android.veblrapp.model.User;
import com.veblr.android.veblrapp.model.VIdeoItem;
import com.veblr.android.veblrapp.network.ApiInterface;
import com.veblr.android.veblrapp.network.ApiService;
import com.veblr.android.veblrapp.util.AppUtils;

import org.json.JSONObject;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;

public class SearchDataSource extends PageKeyedDataSource<Long, VIdeoItem> {

    ApiInterface apiService;
    String searchText;
    Context context;
    SearchDataSource(Context context,ApiInterface apiService,String searchText){
        this.context = context;
        this.apiService = apiService;
        this.searchText = searchText;
    }
    @Override
    public void loadInitial(@NonNull LoadInitialParams<Long> params, @NonNull final LoadInitialCallback<Long, VIdeoItem> callback) {

      Call<ResponseVideoList> responseVideoListCall = apiService.getVideoListFromSearchText(AppUtils.getJSonOBJForSearch(searchText));

            responseVideoListCall.enqueue(new Callback<ResponseVideoList>() {
                @Override
                public void onResponse(Call<ResponseVideoList> call, retrofit2.Response<ResponseVideoList> response) {
                    if(response.isSuccessful()){
                        if (response.body() != null) {
                       //     Log.e("RESPONSE", response.body().getResonse().getResult().get(0).getVideoId());
                            ResponseVideoList responseVideoList = response.body();
                            ArrayList<VIdeoItem> vIdeoItemArrayList = new ArrayList<>();
                            if (responseVideoList != null && responseVideoList.getResonse().getResult() != null) {
                                vIdeoItemArrayList = (ArrayList<VIdeoItem>) responseVideoList.getResonse().getResult();
                                callback.onResult(vIdeoItemArrayList, null, (long) 2);
                            }
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
                    Log.e("FAiled",t.getMessage());
                }
            });
    }

    @Override
    public void loadBefore(@NonNull LoadParams<Long> params, @NonNull LoadCallback<Long, VIdeoItem> callback) {

    }

    @Override
    public void loadAfter(@NonNull final LoadParams<Long> params, @NonNull final LoadCallback<Long, VIdeoItem> callback) {

      Call<ResponseVideoList> responseVideoListCall = apiService.getVideoListFromSearchText(AppUtils.getJSonOBJForSearch(searchText));

         responseVideoListCall.enqueue(new Callback<ResponseVideoList>() {
            @Override
            public void onResponse(Call<ResponseVideoList> call, retrofit2.Response<ResponseVideoList> response) {
                if(response.isSuccessful()){
                    ResponseVideoList responseVideoList = response.body();
                    ArrayList<VIdeoItem> vIdeoItemArrayList = new ArrayList<>();

                    if (responseVideoList != null && responseVideoList.getResonse().getResult() != null) {
                        vIdeoItemArrayList = (ArrayList<VIdeoItem>) responseVideoList.getResonse().getResult();

                        callback.onResult(vIdeoItemArrayList, params.key + 1);
                    }
                }
                else {
                    AppUtils.getResponseValidation(context);
                    apiService = ApiService.getService(context).create(ApiInterface.class);
                }
            }
            @Override
            public void onFailure(Call<ResponseVideoList> call, Throwable t) {

                Log.e("Failed",t.getMessage());
            }

        });
    }
}
