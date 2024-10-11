package com.veblr.android.veblrapp.model;

import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.DiffUtil;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;
import androidx.room.TypeConverters;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.veblr.android.veblrapp.dao.Converters;

import java.io.Serializable;

import android.os.Build;
import android.util.Base64;
import android.util.Log;

import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.UUID;

@Entity(tableName = "VIDEO")

public class VIdeoItem implements Serializable
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
    @SerializedName("video_channel_link")
    @Expose
    private String videoChannelLink;
    @SerializedName("video_channel_image")
    @Expose
    private String videoChannelImage;
    @SerializedName("video_views")
    @Expose
    private String videoViews;
    @SerializedName("video_likes")
    @Expose
    private String videoLikes;
    @SerializedName("video_comments")
    @Expose
    private String videoComments;
    @SerializedName("video_uploaded_date")
    @Expose
    private String videoUploadedDate;
    @SerializedName("video_language_short")
    @Expose
    private String videoLanguageShort;
    @SerializedName("video_is_family_friendly")
    @Expose
    private String videoIsFamilyFriendly;
    @SerializedName("video_mp4")
    @Expose
    private String videoMp4;
    @SerializedName("vast_tag")
    @Expose
    private String vastTag;
    @SerializedName("video_keywords")
    @Expose
    private List<String> videoKeywords = null;
    @SerializedName("video_hashtags")
    @Expose
    private List<String> videoHashtags = null;

    private boolean likedbyME = false;
    private boolean followedByMe = false;

    private final static long serialVersionUID = 3280738260908111685L;


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

    public String getVideoChannelLink() {
        return videoChannelLink;
    }

    public void setVideoChannelLink(String videoChannelLink) {
        this.videoChannelLink = videoChannelLink;
    }

    public String getVideoChannelImage() {
        return videoChannelImage;
    }

    public void setVideoChannelImage(String videoChannelImage) {
        this.videoChannelImage = videoChannelImage;
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

    public String getVideoComments() {
        return videoComments;
    }

    public void setVideoComments(String videoComments) {
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

    public String getVideoMp4() {
        if(!videoMp4.contains(".mp4")){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            try{
                videoMp4 = new String(Base64.decode(videoMp4, Base64.DEFAULT), StandardCharsets.UTF_8);

            }catch (Exception e) {
                e.printStackTrace();
            }
        }
     else {
            try {
                videoMp4 = new String(Base64.decode(videoMp4, Base64.URL_SAFE ), StandardCharsets.UTF_8);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        }
        else
            return videoMp4;
        Log.e("mp4file is:",videoMp4);
        return videoMp4;
    }

    public void setVideoMp4(String videoMp4) {
        this.videoMp4 = videoMp4;
    }

    public String getVastTag() {
        return vastTag;
    }

    public void setVastTag(String vastTag) {
        this.vastTag = vastTag;
    }

    public List<String> getVideoKeywords() {
        return videoKeywords;
    }

    public void setVideoKeywords(List<String> videoKeywords) {
        this.videoKeywords = videoKeywords;
    }

    public List<String> getVideoHashtags() {
        return videoHashtags;
    }

    public void setVideoHashtags(List<String> videoHashtags) {
        this.videoHashtags = videoHashtags;
    }
    //delete
    public VIdeoItem(){}

    public VIdeoItem(String videoTitle, String videoPoster, String videoPosted, String videoWatchLink){
        this.videoTitle  = videoTitle;
        this.videoPoster =videoPoster;
        this.videoPosted = videoPosted;
        this.videoMp4 = videoWatchLink;

    }
    @PrimaryKey(autoGenerate = true)
    private Integer id;

    public Integer getId( ){return  this.id;}
    public void setId(Integer id){
        this.id = id;
    }

    public static final DiffUtil.ItemCallback<VIdeoItem> CALLBACK = new DiffUtil.ItemCallback<VIdeoItem>() {
        @Override
        public boolean areItemsTheSame(VIdeoItem oldItem, VIdeoItem newItem) {
            return oldItem.videoId == newItem.videoId;
        }

        @Override
        public boolean areContentsTheSame(VIdeoItem oldItem, VIdeoItem newItem) {
            return true;
        }
    };

    public boolean isLikedbyME() {
        return likedbyME;
    }

    public void setLikedbyME(boolean likedbyME) {
        this.likedbyME = likedbyME;
    }

    public boolean isFollowedByMe() {
        return followedByMe;
    }

    public void setFollowedByMe(boolean followedByMe) {
        this.followedByMe = followedByMe;
    }
}