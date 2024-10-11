package com.veblr.android.veblrapp.datasource;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.veblr.android.veblrapp.model.VIdeoItem;

import java.io.Serializable;

public class ResponseVideoItem implements Serializable
{

    @SerializedName("response")
    @Expose
    private Response response;
    private final static long serialVersionUID = -443270657371048086L;

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
        private VIdeoItem result;
        private final static long serialVersionUID = -1210946883288194229L;

        public Integer getStatus() {
            return status;
        }

        public void setStatus(Integer status) {
            this.status = status;
        }

        public VIdeoItem getResult() {
            return result;
        }

        public void setResult(VIdeoItem result) {
            this.result = result;
        }

    }
}