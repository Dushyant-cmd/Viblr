package com.veblr.android.veblrapp.model;

public class Notification {
    private String message;
    private String payload;
    private String videoId;
    private String date;
    public Notification(){}
    public  Notification(String message,String date){
        this.message = message;
        this.date = date;
    }
    public  Notification(String message,String payload,String date){
        this.message = message;
        this.payload = payload;
        this.date = date;

    }
    public  Notification(String message,String videoId,String payload,String date){
        this.message = message;
        this.payload = payload;
        this.videoId =videoId;
        this.date = date;

    }


    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getPayload() {
        return payload;
    }

    public void setPayload(String payload) {
        this.payload = payload;
    }

    public String getVideoId() {
        return videoId;
    }

    public void setVideoId(String videoId) {
        this.videoId = videoId;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }
}
