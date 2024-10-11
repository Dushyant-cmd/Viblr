package com.veblr.android.veblrapp.datasource;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.veblr.android.veblrapp.model.Country;

import java.io.Serializable;
import java.util.List;

public class ResponseCountry implements Serializable {

    @SerializedName("response")
    @Expose
    private Response response;
    private final static long serialVersionUID = 5172481405676563274L;

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
        private List<Country> result = null;
        private final static long serialVersionUID = -7071883986853568739L;

        public Integer getStatus() {
            return status;
        }

        public void setStatus(Integer status) {
            this.status = status;
        }

        public List<Country> getResult() {
            return result;
        }

        public void setResult(List<Country> result) {
            this.result = result;
        }

    }



}