package com.veblr.android.veblrapp.model;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "SearchHistory")
public class SearchHistory {

    @PrimaryKey(autoGenerate = true)
    private int stringId;

    private String queryString;



    public  SearchHistory(){}
    public  SearchHistory(String searchText){this.queryString = searchText;}


    public int getStringId(){return this.stringId;}
    public void setStringId(int id){this.stringId  = id;}
    public void setQueryString(String queryString){this.queryString = queryString;}
    public String getQueryString(){return  this.queryString;}
 }
