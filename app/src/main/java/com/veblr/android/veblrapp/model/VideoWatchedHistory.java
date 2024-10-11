package com.veblr.android.veblrapp.model;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "VideoWatchHistory")
public class VideoWatchedHistory {

    @PrimaryKey(autoGenerate = true)
    private int historyId;
    private String videoId;

    public  VideoWatchedHistory(){}
    public  VideoWatchedHistory(String videoId){}

    public int getStringId(){return this.historyId;}
    public void setStringId(int id){this.historyId  = id;}
    public void setWatchedVideoId(String videoId){this.videoId = videoId;}
    public String getWatchedVideoId(){return  this.videoId;}

}
