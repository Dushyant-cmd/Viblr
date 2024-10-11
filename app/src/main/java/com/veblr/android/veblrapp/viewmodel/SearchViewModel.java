package com.veblr.android.veblrapp.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.paging.LivePagedListBuilder;
import androidx.paging.PagedList;

import com.veblr.android.veblrapp.datasource.SearchDataSOurceFactory;
import com.veblr.android.veblrapp.model.SearchHistory;
import com.veblr.android.veblrapp.model.VIdeoItem;
import com.veblr.android.veblrapp.network.ApiInterface;
import com.veblr.android.veblrapp.network.ApiService;
import com.veblr.android.veblrapp.repositories.SearchRepository;

import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class SearchViewModel extends AndroidViewModel{

    SearchRepository searchRepository;
    private LiveData<List<SearchHistory>> allMovies;
    String searchText;
    private Executor executor;
    private LiveData<PagedList<VIdeoItem>> videosPagedList;
    public SearchViewModel(@NonNull Application application) {
        super(application);
       // this.searchText = searchText;
        searchRepository =new SearchRepository(application);
        allMovies = searchRepository.getAllSearchText();
        ApiInterface apiInterface = ApiService.getService(application).create(ApiInterface.class);
        SearchDataSOurceFactory factory = new SearchDataSOurceFactory(application,apiInterface,"modi");

        PagedList.Config config = (new PagedList.Config.Builder())
                .setEnablePlaceholders(true)
                .setInitialLoadSizeHint(2)
                .setPageSize(4)
                .setPrefetchDistance(4)
                .build();

        executor = Executors.newFixedThreadPool(5);

        videosPagedList = (new LivePagedListBuilder<Long, VIdeoItem>(factory, config))
                .setFetchExecutor(executor)
                .build();
    }

    public void insertText(SearchHistory  searchText) {
        searchRepository.insertText(searchText);
        this.searchText = searchText.getQueryString();

    }

 /*   public void updateText(String searchText) {
        searchRepository.updateText(searchText);
    }*/


    public void deleteAllText() {
        searchRepository.deleteAllText();
    }

    public LiveData<List<SearchHistory>> getAllSearchText() {
        return allMovies;
    }

    public LiveData<PagedList<VIdeoItem>> getAllSearchedVideo(){
        return videosPagedList;
    }

}
