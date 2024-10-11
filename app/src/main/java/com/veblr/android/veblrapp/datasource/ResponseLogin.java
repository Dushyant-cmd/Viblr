package com.veblr.android.veblrapp.datasource;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class ResponseLogin  implements Serializable
{

    @SerializedName("response")
    @Expose
    private Response response;
    private final static long serialVersionUID = -1632422059242951708L;

    public Response getResponse() {
        return response;
    }

    public void setResponse(Response response) {
        this.response = response;
    }

    public class Result implements Serializable
    {

        @SerializedName("user_id")
        @Expose
        private String userId;
        private final static long serialVersionUID = 5843280433885203774L;

        public String getUserId() {
            return userId;
        }

        public void setUserId(String userId) {
            this.userId = userId;
        }

    }
    public class Response implements Serializable
    {

        @SerializedName("status")
        @Expose
        private Integer status;
        @SerializedName("result")
        @Expose
        private Result result;
        private final static long serialVersionUID = -1210946883288194229L;

        public Integer getStatus() {
            return status;
        }

        public void setStatus(Integer status) {
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
