package com.veblr.android.veblrapp.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class Body implements Serializable {
    public static class Param implements Serializable
    {

        @SerializedName("api_key")
        @Expose
        private String apiKey;
        @SerializedName("username")
        @Expose
        private String username;
        @SerializedName("password")
        @Expose
        private String password;
        private final static long serialVersionUID = 3882203784427451523L;

        public String getApiKey() {
            return apiKey;
        }

        public void setApiKey(String apiKey) {
            this.apiKey = apiKey;
        }

        public String getUsername() {
            return username;
        }

        public void setUsername(String username) {
            this.username = username;
        }

        public String getPassword() {
            return password;
        }

        public void setPassword(String password) {
            this.password = password;
        }

    }
    @SerializedName("param")
    @Expose
    private Param param;
    private final static long serialVersionUID = -887176780592942056L;

    public Param getParam() {
        return param;
    }

    public void setParam(Param param) {
        this.param = param;
    }

}
