package com.veblr.android.veblrapp.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class Result implements Serializable
{

    @SerializedName("video_id")
    @Expose
    private String videoId;
    @SerializedName("video_title")
    @Expose
    private String videoTitle;
    @SerializedName("video_description")
    @Expose
    private String videoDescription;
    @SerializedName("video_poster")
    @Expose
    private String videoPoster;
    @SerializedName("video_duration")
    @Expose
    private String videoDuration;
    @SerializedName("video_category")
    @Expose
    private String videoCategory;
    @SerializedName("video_category_link")
    @Expose
    private String videoCategoryLink;
    @SerializedName("video_posted")
    @Expose
    private String videoPosted;
    @SerializedName("video_views")
    @Expose
    private String videoViews;
    @SerializedName("video_likes")
    @Expose
    private String videoLikes;
    @SerializedName("video_comments")
    @Expose
    private Object videoComments;
    @SerializedName("video_uploaded_date")
    @Expose
    private String videoUploadedDate;
    @SerializedName("video_language_short")
    @Expose
    private String videoLanguageShort;
    @SerializedName("video_is_family_friendly")
    @Expose
    private String videoIsFamilyFriendly;
    private final static long serialVersionUID = 3886039548739865978L;

    public String getVideoId() {
        return videoId;
    }

    public void setVideoId(String videoId) {
        this.videoId = videoId;
    }

    public String getVideoTitle() {
        return videoTitle;
    }

    public void setVideoTitle(String videoTitle) {
        this.videoTitle = videoTitle;
    }

    public String getVideoDescription() {
        return videoDescription;
    }

    public void setVideoDescription(String videoDescription) {
        this.videoDescription = videoDescription;
    }

    public String getVideoPoster() {
        return videoPoster;
    }

    public void setVideoPoster(String videoPoster) {
        this.videoPoster = videoPoster;
    }

    public String getVideoDuration() {
        return videoDuration;
    }

    public void setVideoDuration(String videoDuration) {
        this.videoDuration = videoDuration;
    }

    public String getVideoCategory() {
        return videoCategory;
    }

    public void setVideoCategory(String videoCategory) {
        this.videoCategory = videoCategory;
    }

    public String getVideoCategoryLink() {
        return videoCategoryLink;
    }

    public void setVideoCategoryLink(String videoCategoryLink) {
        this.videoCategoryLink = videoCategoryLink;
    }

    public String getVideoPosted() {
        return videoPosted;
    }

    public void setVideoPosted(String videoPosted) {
        this.videoPosted = videoPosted;
    }

    public String getVideoViews() {
        return videoViews;
    }

    public void setVideoViews(String videoViews) {
        this.videoViews = videoViews;
    }

    public String getVideoLikes() {
        return videoLikes;
    }

    public void setVideoLikes(String videoLikes) {
        this.videoLikes = videoLikes;
    }

    public Object getVideoComments() {
        return videoComments;
    }

    public void setVideoComments(Object videoComments) {
        this.videoComments = videoComments;
    }

    public String getVideoUploadedDate() {
        return videoUploadedDate;
    }

    public void setVideoUploadedDate(String videoUploadedDate) {
        this.videoUploadedDate = videoUploadedDate;
    }

    public String getVideoLanguageShort() {
        return videoLanguageShort;
    }

    public void setVideoLanguageShort(String videoLanguageShort) {
        this.videoLanguageShort = videoLanguageShort;
    }

    public String getVideoIsFamilyFriendly() {
        return videoIsFamilyFriendly;
    }

    public void setVideoIsFamilyFriendly(String videoIsFamilyFriendly) {
        this.videoIsFamilyFriendly = videoIsFamilyFriendly;
    }

}
