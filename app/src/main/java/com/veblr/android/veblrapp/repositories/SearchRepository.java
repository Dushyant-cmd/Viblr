package com.veblr.android.veblrapp.repositories;

import android.app.Application;
import android.os.AsyncTask;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.veblr.android.veblrapp.dao.SearchHistoryDao;
import com.veblr.android.veblrapp.model.SearchHistory;
import com.veblr.android.veblrapp.room.AppDataBase;

import java.util.List;

public class SearchRepository extends AndroidViewModel {

   private SearchHistoryDao searchHistoryDao;
    private AppDataBase appDataBaseInstance;
    LiveData<List<SearchHistory>> listLiveData;
    public SearchRepository(@NonNull Application application) {
        super(application);
        appDataBaseInstance = AppDataBase.getDatabaseInstance(application);
        searchHistoryDao = appDataBaseInstance.searchHistoryDao();
    }



    public LiveData<List<SearchHistory>> getAllSearchText() {
       //new GetAllSearchText(searchHistoryDao).execute();
       return  searchHistoryDao.getAllSearchString();
    }

    public void insertText(SearchHistory searchText) {
        new InsertSearchTextAsyncTask(searchHistoryDao).execute(searchText);

  }


    public void deleteAllText() {
        searchHistoryDao.deleteAllSearchString();
    }


 /*   private static class GetAllSearchText extends AsyncTask<Void, Void,  LiveData<List<SearchHistory>> > {
        private SearchHistoryDao searchHistoryDao;

        private GetAllSearchText(SearchHistoryDao movieDao) {
            this.searchHistoryDao = movieDao;
        }

        @Override
        protected LiveData<List<SearchHistory>> doInBackground(Void... voids) {
            return searchHistoryDao.getAllSearchString();
        }
    }*/
    private static class InsertSearchTextAsyncTask extends AsyncTask<SearchHistory, Void, Void> {
        private SearchHistoryDao searchHistoryDao;

        private InsertSearchTextAsyncTask(SearchHistoryDao movieDao) {
            this.searchHistoryDao = movieDao;
        }

        @Override
        protected Void doInBackground(SearchHistory... searchtext) {
            searchHistoryDao.insertSearchString(searchtext[0]);
            return null;
        }
    }

}