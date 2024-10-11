package com.veblr.android.veblrapp.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
public class Comment implements Serializable {

    @SerializedName("comment_id")
    @Expose
    private String commentId;
    @SerializedName("video_id")
    @Expose
    private String videoId;
    @SerializedName("comment")
    @Expose
    private String commentText;

    private String userPosted;

    private String userPostedImage;
    @SerializedName("date")
    @Expose
    private String dateOfPosted;
    private String comment_no;
    @SerializedName("appUser")
    private AppUser user;

    @SerializedName("channel")
    private Channel channel;

    public  Comment (){}

   public Comment(String commentText,String userPosted,String userPostedImage,String dateOfPosted,String videoId){

       this.commentText =commentText;
       this.userPosted = userPosted;
       this.userPostedImage =userPostedImage;
       this.dateOfPosted  = dateOfPosted;
       this.videoId = videoId;
   }


    public String getCommentText() {
        return commentText;
    }

    public void setCommentText(String commentText) {
        this.commentText = commentText;
    }

    public String getUserPosted() {
        return userPosted;
    }

    public void setUserPosted(String userPosted) {
        this.userPosted = userPosted;
    }

    public String getUserPostedImage() {
        return userPostedImage;
    }

    public void setUserPostedImage(String userPostedImage) {
        this.userPostedImage = userPostedImage;
    }

    public String getDateOfPosted() {
        return dateOfPosted;
    }

    public void setDateOfPosted(String dateOfPosted) {
        this.dateOfPosted = dateOfPosted;
    }

    public String getVideoId() {
        return videoId;
    }

    public void setVideoId(String videoId) {
        this.videoId = videoId;
    }

    public String getComment_no() {
        return comment_no;
    }

    public void setComment_no(String videoId) {
        this.comment_no = comment_no;
    }

    public AppUser getUser() {
        return user;
    }

    public void setUser(AppUser user) {
        this.user = user;
    }

    public Channel getChannel() {
        return channel;
    }

    public void setChannel(Channel channel) {
        this.channel = channel;
    }

    public class AppUser implements Serializable{
        @SerializedName("app_user_id")
        @Expose
        private String appUserId;
        @SerializedName("app_user_name")
        @Expose
        private String appUserName;
        @SerializedName("app_user_image")
        @Expose
        private String appUserImage;
        private final static long serialVersionUID = -5951901033541442539L;

        public String getAppUserId() {
            return appUserId;
        }

        public void setAppUserId(String appUserId) {
            this.appUserId = appUserId;
        }

        public String getAppUserName() {
            return appUserName;
        }

        public void setAppUserName(String appUserName) {
            this.appUserName = appUserName;
        }

        public String getAppUserImage() {
            return appUserImage;
        }

        public void setAppUserImage(String appUserImage) {
            this.appUserImage = appUserImage;
        }
    }
}
