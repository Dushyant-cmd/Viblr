package com.veblr.android.veblrapp.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.veblr.android.veblrapp.model.SearchHistory;
import com.veblr.android.veblrapp.model.VIdeoItem;

import java.util.List;

@Dao
public interface VIdeoDao {


    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertVideoItem(VIdeoItem text);

    @Query("DELETE FROM VIDEO")
    void deleteAllVideos ();


    @Query("SELECT * FROM VIDEO")
    List<VIdeoItem> getAllVideos ();

    @Update(onConflict = OnConflictStrategy.REPLACE)
    void updateVideoItem (VIdeoItem vIdeoItem);


    @Query("UPDATE VIDEO SET likedbyME= :isLiked WHERE videoId= :videoId ")
    void setLike(boolean isLiked,String videoId);

    @Query("SELECT likedbyME FROM VIDEO  WHERE videoId= :videoId ")
    boolean getLike(String videoId);


   }
