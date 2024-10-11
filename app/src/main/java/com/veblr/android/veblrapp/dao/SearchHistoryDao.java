package com.veblr.android.veblrapp.dao;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.room.Dao;
import androidx.room.Entity;
import androidx.room.Insert;
import androidx.room.Query;

import com.veblr.android.veblrapp.model.SearchHistory;

import java.util.List;
@Dao
public interface SearchHistoryDao {
    @Insert
    void insertSearchString(SearchHistory text);

    @Query("DELETE FROM SearchHistory")
    void deleteAllSearchString ();


    @Query("SELECT * FROM SearchHistory")
    LiveData<List<SearchHistory>> getAllSearchString();

  /*  @Query("SELECT * FROM SearchHistory WHERE queryString LIKE :searchText")
    List<String> getDesiredSearchText(String searchText);*/
}
