package com.veblr.android.veblrapp.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.veblr.android.veblrapp.datasource.ResponseVideoList;

import org.intellij.lang.annotations.Language;

import java.io.Serializable;
import java.util.List;

public class Response {

    @SerializedName("response")
    @Expose
    private Resonse resonse;
    @SerializedName("error")
    @Expose
    private ResponseVideoList.Error error;
    private final static long serialVersionUID = 5205652523629300687L;

    public Resonse getResonse() {
        return resonse;
    }

    public ResponseVideoList.Error getError() {
        return error;
    }

    public void setError(ResponseVideoList.Error error) {
        this.error = error;
    }

    public void setResonse(Resonse resonse) {
        this.resonse = resonse;
    }
    public class Result implements Serializable
    {
        @SerializedName("app_user_id")
        @Expose
        private String appUserId;

        @SerializedName("liked_id")
        @Expose
        private String likedId;

        @SerializedName("comment_id")
        @Expose
        private String commentId;
        @SerializedName("followed_id")
        @Expose
        private String followId;

        @SerializedName("token")
        @Expose
        private String token;

        @SerializedName("email_id_status")
        @Expose
        private String email_id_status;

        @SerializedName("temp_user_id")
        @Expose
        private String temp_user_id;

        private final static long serialVersionUID = 8603108679064773753L;

        public Result() {
        }


        public String getToken() {
            return token;
        }

        public void setToken(String token) {
            this.token = token;
        }


        public String getAppUserId() {
            return appUserId;
        }

        public void setAppUserId(String appUserId) {
            this.appUserId = appUserId;
        }

        public String getFollowId() {
            return followId;
        }

        public void setFollowId(String followId) {
            this.followId = followId;
        }

        public String getEmail_id_status() {
            return email_id_status;
        }

        public void setEmail_id_status(String email_id_status) {
            this.email_id_status = email_id_status;
        }

        public String getTemp_user_id() {
            return temp_user_id;
        }

        public void setTemp_user_id(String temp_user_id) {
            this.temp_user_id = temp_user_id;
        }
    }


    public class Resonse implements Serializable
    {

        @SerializedName("status")
        @Expose
        private String status;
        @SerializedName("result")
        @Expose
        private Result result;

        private final static long serialVersionUID = -7786068689122995704L;

        public String getStatus() {
            return status;
        }

        public void setStatus(String status) {
            this.status = status;
        }

        public Result getResult() {
            return result;
        }

        public void setResult(Result result) {
            this.result = result;
        }



    }
}
