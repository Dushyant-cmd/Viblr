package com.veblr.android.veblrapp.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import com.veblr.android.veblrapp.model.SearchHistory;
import com.veblr.android.veblrapp.model.VideoKeyWords;

import java.util.List;
@Dao
public interface VideoKeyWordDao {
    @Insert
    void insertKeyWordString(VideoKeyWords text);

    /*@Query("DELETE FROM SearchHistory")
    void deleteKeyWord ();


    @Query("SELECT * FROM VID")
    LiveData<List<SearchHistory>> getAllSearchString();*/
}
