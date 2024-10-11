package com.veblr.android.veblrapp.datasource;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

public class ResponseTag implements Serializable
{

    @SerializedName("response")
    @Expose
    private Response response;
    private final static long serialVersionUID = 6977815467116287297L;

    public Response getResponse() {
        return response;
    }

    public void setResponse(Response response) {
        this.response = response;
    }
    public class Response implements Serializable
    {

        @SerializedName("status")
        @Expose
        private Integer status;
        @SerializedName("result")
        @Expose
        private List<String> result = null;
        private final static long serialVersionUID = 8929405697109684779L;

        public Integer getStatus() {
            return status;
        }

        public void setStatus(Integer status) {
            this.status = status;
        }

        public List<String> getResult() {
            return result;
        }

        public void setResult(List<String> result) {
            this.result = result;
        }

    }
}
