package com.veblr.android.veblrapp.datasource;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;
import androidx.paging.DataSource;

import com.veblr.android.veblrapp.network.ApiInterface;
import com.veblr.android.veblrapp.network.ApiService;

public class SearchDataSOurceFactory extends DataSource.Factory {

    private SearchDataSource userDataSource;
    private ApiInterface apiService;
    private MutableLiveData<SearchDataSource> mutableLiveData;
    String searchText;
    Context context;
    public SearchDataSOurceFactory(Context context, ApiInterface apiService, String searchText) {
        this.context  = context;
        this.apiService = apiService;
        mutableLiveData = new MutableLiveData<>();
        this.searchText = searchText;
        mutableLiveData  = getMutableLiveData();
    }

    public MutableLiveData<SearchDataSource> getMutableLiveData() {
        return mutableLiveData;
    }

    @NonNull
    @Override
    public DataSource create() {
        userDataSource = new SearchDataSource(context,apiService,searchText);
        mutableLiveData.postValue(userDataSource);
        return userDataSource;
    }
}
