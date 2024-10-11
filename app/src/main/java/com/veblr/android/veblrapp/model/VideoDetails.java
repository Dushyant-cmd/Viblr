package com.veblr.android.veblrapp.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class VideoDetails implements Serializable
{


    @SerializedName("app_user_id")
    @Expose
    private String appUserId;
    @SerializedName("channel_id")
    @Expose
    private String channelId;
    @SerializedName("video_temp_uploaded")
    @Expose
    private String videoTempUploaded;
    @SerializedName("video_duration_seconds")
    @Expose
    private Integer videoDurationSeconds;
    @SerializedName("video_title")
    @Expose
    private String videoTitle;
    @SerializedName("video_category")
    @Expose
    private String videoCategory;
    @SerializedName("video_description")
    @Expose
    private String videoDescription;
    @SerializedName("video_adult")
    @Expose
    private String videoAdult;
    @SerializedName("video_monetize")
    @Expose
    private String videoMonetize;
    @SerializedName("video_language")
    @Expose
    private String videoLanguage;
    @SerializedName("video_thumbnail")
    @Expose
    private String videoThumbnail;
    private final static long serialVersionUID = -8514553876916173847L;

    public String getAppUserId() {
        return appUserId;
    }

    public void setAppUserId(String appUserId) {
        this.appUserId = appUserId;
    }

    public String getChannelId() {
        return channelId;
    }

    public void setChannelId(String channelId) {
        this.channelId = channelId;
    }

    public String getVideoTempUploaded() {
        return videoTempUploaded;
    }

    public void setVideoTempUploaded(String videoTempUploaded) {
        this.videoTempUploaded = videoTempUploaded;
    }

    public Integer getVideoDurationSeconds() {
        return videoDurationSeconds;
    }

    public void setVideoDurationSeconds(Integer videoDurationSeconds) {
        this.videoDurationSeconds = videoDurationSeconds;
    }

    public String getVideoTitle() {
        return videoTitle;
    }

    public void setVideoTitle(String videoTitle) {
        this.videoTitle = videoTitle;
    }

    public String getVideoCategory() {
        return videoCategory;
    }

    public void setVideoCategory(String videoCategory) {
        this.videoCategory = videoCategory;
    }

    public String getVideoDescription() {
        return videoDescription;
    }

    public void setVideoDescription(String videoDescription) {
        this.videoDescription = videoDescription;
    }

    public String getVideoAdult() {
        return videoAdult;
    }

    public void setVideoAdult(String videoAdult) {
        this.videoAdult = videoAdult;
    }

    public String getVideoMonetize() {
        return videoMonetize;
    }

    public void setVideoMonetize(String videoMonetize) {
        this.videoMonetize = videoMonetize;
    }

    public String getVideoLanguage() {
        return videoLanguage;
    }

    public void setVideoLanguage(String videoLanguage) {
        this.videoLanguage = videoLanguage;
    }

    public String getVideoThumbnail() {
        return videoThumbnail;
    }

    public void setVideoThumbnail(String videoThumbnail) {
        this.videoThumbnail = videoThumbnail;
    }
}