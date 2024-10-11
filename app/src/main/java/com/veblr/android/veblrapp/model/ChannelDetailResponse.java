package com.veblr.android.veblrapp.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

public class ChannelDetailResponse implements Serializable{

    @SerializedName("response")
    @Expose
    private Resonse resonse;
    private final static long serialVersionUID = 5205652523629300687L;

    public Resonse getUSERDetailResponse() {
        return resonse;
    }

    public void setUSERDetailResponse(Resonse resonse) {
        this.resonse = resonse;
    }

    public class Resonse implements Serializable
    {

        @SerializedName("status")
        @Expose
        private String status;
        @SerializedName("result")
        @Expose
        private List<Channel> result = null;
        private final static long serialVersionUID = -7071883986853568739L;



        public List<Channel> getResultList() {
            return result;
        }

        public void setResultList(List<Channel> result) {
            this.result = result;

        }
        public String getStatus() {
            return status;
        }

        public void setStatus(String status) {
            this.status = status;
        }

    }
}
