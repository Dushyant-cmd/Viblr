package com.veblr.android.veblrapp.model;

import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.PrimaryKey;
import androidx.room.TypeConverter;
@Entity(tableName = "VideoKeyWords")
public class VideoKeyWords {

    @PrimaryKey(autoGenerate = true)
    private int keyWordId;

    private String keyword;
    private String video_id;


    @TypeConverter
    public static String toString(VideoKeyWords section) {
        return section == null ? null : section.getQueryString();
    }
    public  VideoKeyWords(){}
    public  VideoKeyWords(String keyword){this.keyword = keyword;}


    public int getStringId(){return this.keyWordId;}
    public void setStringId(int id){this.keyWordId  = id;}
    public void setQueryString(String queryString){this.keyword = queryString;}
    public String getQueryString(){return  this.keyword;}
    public void setVideo_id(String queryString){this.video_id = queryString;}
    public String getVideo_id(){return  this.video_id;}
}
