package com.veblr.android.veblrapp.model;

import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.room.TypeConverter;

public class VideoHashTag {
    @PrimaryKey(autoGenerate = true)
    private int hashTagIds;

    private String videohashtag;
    private String video_id;


    public  VideoHashTag(){}
    public  VideoHashTag(String keyword){this.videohashtag = keyword;}


    public int getStringId(){return this.hashTagIds;}
    public void setStringId(int id){this.hashTagIds  = id;}
    public void setQueryString(String queryString){this.videohashtag = queryString;}
    public String getQueryString(){return  this.videohashtag;}
    public void setVideo_id(String queryString){this.video_id = queryString;}
    public String getVideo_id(){return  this.video_id;}


}
