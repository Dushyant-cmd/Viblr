package com.veblr.android.veblrapp.model;


import android.content.Context;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

public class Channels implements Serializable {

    @SerializedName("channel")
    @Expose
    private List<User> channel = null;
    private final static long serialVersionUID = -1839190813578586838L;

    public Channels() {
    }

    /**
     *
     * @param channel
     */
    public Channels(List<User> channel) {
        super();
        this.channel = channel;
    }
    public Channels(Context context){

    }
    public List<User> getVideo() {
        return channel;
    }

    public void setVideo(List<User> channel) {
        this.channel = channel;
    }

    public Channels withVideo(List<User> channel) {
        this.channel = channel;
        return this;
    }

}
