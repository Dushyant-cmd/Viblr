package com.veblr.android.veblrapp.datasource;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class ResponseVideoList implements  Serializable {

    @SerializedName("error")
    @Expose
    private Error error;
    @SerializedName("response")
    @Expose
    private Resonse response;
    private final static long serialVersionUID = 915493456214401102L;

    public Resonse getResonse() {
        return response;
    }

    public void setResonse(Resonse response) {
        this.response = response;
    }

    public Error getError() {
        return error;
    }

    public void setError(Error error) {
        this.error = error;
    }

    public class Error{
        @SerializedName("status")
        @Expose
        private int status;
        @SerializedName("message")
        @Expose
        private String message;

        public int getStatus() {
            return status;
        }

        public void setStatus(int status) {
            this.status = status;
        }

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }
    }
}

