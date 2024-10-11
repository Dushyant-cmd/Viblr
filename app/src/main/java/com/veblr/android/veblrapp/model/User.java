package com.veblr.android.veblrapp.model;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;
import androidx.room.TypeConverters;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.veblr.android.veblrapp.dao.Converters;

import java.io.Serializable;
import java.util.List;

@Entity(tableName = "USER_TABLE" )
 public class User implements Serializable {
 @NonNull
 @PrimaryKey(autoGenerate = true)
 @ColumnInfo(name = "id_no")
 private int id_no;

 @SerializedName("id")
 @Expose
 private  String userId;

 private String deviceId;
 @SerializedName("app_user_id")
 @Expose
 private String guestUserId;

 @SerializedName("name")
 @Expose
 private String name;
 @SerializedName("email_id")
 @Expose
 private String emailId;
 @SerializedName("mobile_no")
 @Expose
 private String mobileNo;
 @SerializedName("image")
 @Expose
 private String image;
 @SerializedName("country_code")
 @Expose
 private String countryCode;
 @SerializedName("country")
 @Expose
 private String country;
 @SerializedName("dob")
 @Expose
 private String dob;
 @SerializedName("gender")
 @Expose
 private String gender;
 @SerializedName("mobile_verify")
 @Expose
 private String mobileVerify;
 @SerializedName("email_verify")
 @Expose
 private String emailVerify;
 @SerializedName("newsletter")
 @Expose
 private String newsletter;
 @SerializedName("date")
 @Expose
 private String date;
 @SerializedName("user_upload_permission")
 @Expose
 private String userUploadPermission;
 @SerializedName("user_comment_permission")
 @Expose
 private String userCommentPermission;
 @SerializedName("user_playlist_permission")
 @Expose
 private String userPlaylistPermission;
 @SerializedName("user_monetization_permission")
 @Expose
 private String userMonetizationPermission;
 @SerializedName("channel")
 @Expose
 @TypeConverters(Converters.class)
 private List<Channel> channel = null;

 private final static long serialVersionUID = 5838170982802844274L;

 public User(String appuserID) {
 this.guestUserId = appuserID;
 }

 public User(){}

 public String getName() {
  return name;
 }

 public void setName(String name) {
  this.name = name;
 }
 public String getEmailId() {
  return emailId;
 }

 public void setEmailId(String emailId) {
  this.emailId = emailId;
 }

 public String getMobileNo() {
  return mobileNo;
 }

 public void setMobileNo(String mobileNo) {
  this.mobileNo = mobileNo;
 }

 public String getImage() {
  return image;
 }

 public void setImage(String image) {
  this.image = image;
 }

 public String getCountryCode() {
  return countryCode;
 }

 public void setCountryCode(String countryCode) {
  this.countryCode = countryCode;
 }

 public String getCountry() {
  return country;
 }

 public void setCountry(String country) {
  this.country = country;
 }

 public String getDob() {
  return dob;
 }

 public void setDob(String dob) {
  this.dob = dob;
 }

 public String getGender() {
  return gender;
 }

 public void setGender(String gender) {
  this.gender = gender;
 }

 public String getMobileVerify() {
  return mobileVerify;
 }

 public void setMobileVerify(String mobileVerify) {
  this.mobileVerify = mobileVerify;
 }

 public String getEmailVerify() {
  return emailVerify;
 }

 public void setEmailVerify(String emailVerify) {
  this.emailVerify = emailVerify;
 }

 public String getNewsletter() {
  return newsletter;
 }

 public void setNewsletter(String newsletter) {
  this.newsletter = newsletter;
 }

 public String getDate() {
  return date;
 }

 public void setDate(String date) {
  this.date = date;
 }

 public String getUserUploadPermission() {
  return userUploadPermission;
 }

 public void setUserUploadPermission(String userUploadPermission) {
  this.userUploadPermission = userUploadPermission;
 }

 public String getUserCommentPermission() {
  return userCommentPermission;
 }

 public void setUserCommentPermission(String userCommentPermission) {
  this.userCommentPermission = userCommentPermission;
 }

 public String getUserPlaylistPermission() {
  return userPlaylistPermission;
 }

 public void setUserPlaylistPermission(String userPlaylistPermission) {
  this.userPlaylistPermission = userPlaylistPermission;
 }

 public String getUserMonetizationPermission() {
  return userMonetizationPermission;
 }

 public void setUserMonetizationPermission(String userMonetizationPermission) {
  this.userMonetizationPermission = userMonetizationPermission;
 }

 public List<Channel> getChannel() {
  return channel;
 }

 public void setChannel(List<Channel> channel) {
  this.channel = channel;
 }

 public String getGuestUserId() {
  return guestUserId;
 }

 public void setGuestUserId(String guestUserId) {
  this.guestUserId = guestUserId;
 }

 public String getDeviceId() {
  return deviceId;
 }

 public void setDeviceId(String deviceId) {
  this.deviceId = deviceId;
 }

 public String getUserId() {
  return userId;
 }

 public void setUserId(String userId) {
  this.userId = userId;
 }

 public int getId_no() {
  return id_no;
 }

 public void setId_no(int id) {
  this.id_no = id;
 }


}
