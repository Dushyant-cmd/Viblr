package com.veblr.android.veblrapp.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class Channel implements Serializable {

    @SerializedName("ch_id")
    @Expose
    private String chId;
    @SerializedName("ch_name_disp")
    @Expose
    private String chNameDisp;
    @SerializedName("ch_image")
    @Expose
    private String chImage;
    @SerializedName("ch_category")
    @Expose
    private String chCategory;
    @SerializedName("ch_language")
    @Expose
    private String chLanguage;
    @SerializedName("ch_country")
    @Expose
    private String chCountry;
    @SerializedName("user_id")
    @Expose
    private String userId;
    @SerializedName("ch_banner")
    @Expose
    private String chBanner;
    @SerializedName("ch_about")
    @Expose
    private String chAbout;
    @SerializedName("ch_def_video")
    @Expose
    private String chDefVideo;
    @SerializedName("website")
    @Expose
    private String website;
    @SerializedName("facebook")
    @Expose
    private String facebook;
    @SerializedName("twitter")
    @Expose
    private String twitter;
    @SerializedName("gplus")
    @Expose
    private String gplus;
    @SerializedName("blogger")
    @Expose
    private String blogger;
    @SerializedName("pinterest")
    @Expose
    private String pinterest;
    @SerializedName("youtube")
    @Expose
    private String youtube;
    @SerializedName("dailymotion")
    @Expose
    private String dailymotion;
    @SerializedName("ch_verify")
    @Expose
    private String chVerify;
    @SerializedName("name")
    @Expose
    private String name;
    @SerializedName("image")
    @Expose
    private String image;
    @SerializedName("ch_category_name")
    @Expose
    private String chCategoryName;
    @SerializedName("ch_language_name")
    @Expose
    private String chLanguageName;
    @SerializedName("ch_language_name_short")
    @Expose
    private String chLanguageNameShort;
    @SerializedName("ch_country_name")
    @Expose
    private String chCountryName;
    @SerializedName("ch_total_videos")
    @Expose
    private String chTotalVideos;
    @SerializedName("ch_total_video_views")
    @Expose
    private String chTotalVideoViews;
    @SerializedName("ch_total_playlist")
    @Expose
    private String chTotalPlaylist;
    @SerializedName("ch_total_follower")
    @Expose
    private String chTotalFollower;
    @SerializedName("ch_total_following")
    @Expose
    private String chTotalFollowing;
    @SerializedName("ch_name")
    @Expose
    private String chName;

    @SerializedName("ch_upload_permission")
    @Expose
    private String chUploadPermission;
    @SerializedName("ch_comment_permission")
    @Expose
    private String chCommentPermission;
    @SerializedName("ch_playlist_permission")
    @Expose
    private String chPlaylistPermission;
    @SerializedName("ch_monetization_permission")
    @Expose
    private String chMonetizationPermission;
    private final static long serialVersionUID = -7626142695724065565L;


    public  Channel(){}
    public  Channel(String chid){this.chId = chid;}


    public String getChUploadPermission() {
        return chUploadPermission;
    }

    public void setChUploadPermission(String chUploadPermission) {
        this.chUploadPermission = chUploadPermission;
    }

    public String getChCommentPermission() {
        return chCommentPermission;
    }

    public void setChCommentPermission(String chCommentPermission) {
        this.chCommentPermission = chCommentPermission;
    }

    public String getChPlaylistPermission() {
        return chPlaylistPermission;
    }

    public void setChPlaylistPermission(String chPlaylistPermission) {
        this.chPlaylistPermission = chPlaylistPermission;
    }

    public String getChMonetizationPermission() {
        return chMonetizationPermission;
    }

    public void setChMonetizationPermission(String chMonetizationPermission) {
        this.chMonetizationPermission = chMonetizationPermission;
    }


    private boolean followedByMe = false;

    public String getChId() {
        return chId;
    }

    public void setChId(String chId) {
        this.chId = chId;
    }

    public String getChNameDisp() {
        return chNameDisp;
    }

    public void setChNameDisp(String chNameDisp) {
        this.chNameDisp = chNameDisp;
    }

    public String getChImage() {
        return chImage;
    }

    public void setChImage(String chImage) {
        this.chImage = chImage;
    }

    public String getChCategory() {
        return chCategory;
    }

    public void setChCategory(String chCategory) {
        this.chCategory = chCategory;
    }

    public String getChLanguage() {
        return chLanguage;
    }

    public void setChLanguage(String chLanguage) {
        this.chLanguage = chLanguage;
    }

    public String getChCountry() {
        return chCountry;
    }

    public void setChCountry(String chCountry) {
        this.chCountry = chCountry;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getChBanner() {
        return chBanner;
    }

    public void setChBanner(String chBanner) {
        this.chBanner = chBanner;
    }

    public String getChAbout() {
        return chAbout;
    }

    public void setChAbout(String chAbout) {
        this.chAbout = chAbout;
    }

    public String getChDefVideo() {
        return chDefVideo;
    }

    public void setChDefVideo(String chDefVideo) {
        this.chDefVideo = chDefVideo;
    }

    public String getWebsite() {
        return website;
    }

    public void setWebsite(String website) {
        this.website = website;
    }

    public String getFacebook() {
        return facebook;
    }

    public void setFacebook(String facebook) {
        this.facebook = facebook;
    }

    public String getTwitter() {
        return twitter;
    }

    public void setTwitter(String twitter) {
        this.twitter = twitter;
    }

    public String getGplus() {
        return gplus;
    }

    public void setGplus(String gplus) {
        this.gplus = gplus;
    }

    public String getBlogger() {
        return blogger;
    }

    public void setBlogger(String blogger) {
        this.blogger = blogger;
    }

    public String getPinterest() {
        return pinterest;
    }

    public void setPinterest(String pinterest) {
        this.pinterest = pinterest;
    }

    public String getYoutube() {
        return youtube;
    }

    public void setYoutube(String youtube) {
        this.youtube = youtube;
    }

    public String getDailymotion() {
        return dailymotion;
    }

    public void setDailymotion(String dailymotion) {
        this.dailymotion = dailymotion;
    }

    public String getChVerify() {
        return chVerify;
    }

    public void setChVerify(String chVerify) {
        this.chVerify = chVerify;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getChCategoryName() {
        return chCategoryName;
    }

    public void setChCategoryName(String chCategoryName) {
        this.chCategoryName = chCategoryName;
    }

    public String getChLanguageName() {
        return chLanguageName;
    }

    public void setChLanguageName(String chLanguageName) {
        this.chLanguageName = chLanguageName;
    }

    public String getChLanguageNameShort() {
        return chLanguageNameShort;
    }

    public void setChLanguageNameShort(String chLanguageNameShort) {
        this.chLanguageNameShort = chLanguageNameShort;
    }

    public String getChCountryName() {
        return chCountryName;
    }

    public void setChCountryName(String chCountryName) {
        this.chCountryName = chCountryName;
    }

    public String getChTotalVideos() {
        return chTotalVideos;
    }

    public void setChTotalVideos(String chTotalVideos) {
        this.chTotalVideos = chTotalVideos;
    }

    public String getChTotalVideoViews() {
        return chTotalVideoViews;
    }

    public void setChTotalVideoViews(String chTotalVideoViews) {
        this.chTotalVideoViews = chTotalVideoViews;
    }

    public String getChTotalPlaylist() {
        return chTotalPlaylist;
    }

    public void setChTotalPlaylist(String chTotalPlaylist) {
        this.chTotalPlaylist = chTotalPlaylist;
    }

    public String getChTotalFollower() {
        return chTotalFollower;
    }

    public void setChTotalFollower(String chTotalFollower) {
        this.chTotalFollower = chTotalFollower;
    }

    public String getChTotalFollowing() {
        return chTotalFollowing;
    }

    public void setChTotalFollowing(String chTotalFollowing) {
        this.chTotalFollowing = chTotalFollowing;
    }

    public String getChName() {
        return chName;
    }

    public void setChName(String chName) {
        this.chName = chName;
    }

    public boolean isFollowedByMe() {
        return followedByMe;
    }

    public void setFollowedByMe(boolean followedByMe) {
        this.followedByMe = followedByMe;
    }
}
