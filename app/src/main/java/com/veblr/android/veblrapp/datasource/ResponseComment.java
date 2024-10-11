package com.veblr.android.veblrapp.datasource;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.veblr.android.veblrapp.model.Comment;

import java.io.Serializable;
import java.util.List;

public class ResponseComment implements  Serializable{


    @SerializedName("response")
    @Expose
    private Response resonse;
    private final static long serialVersionUID = -213431950999961875L;

    public Response getResonse() {
        return resonse;
    }

    public void setResonse(Response resonse) {
        this.resonse = resonse;
    }


    public class Response implements Serializable
    {

        @SerializedName("status")
        @Expose
        private Integer status;
        @SerializedName("result")
        @Expose
        private List<Comment> result = null;
        private final static long serialVersionUID = -7071883986853568739L;

        public Integer getStatus() {
            return status;
        }

        public void setStatus(Integer status) {
            this.status = status;
        }

        public List<Comment> getResult() {
            return result;
        }

        public void setResult(List<Comment> result) {
            this.result = result;
        }

    }


}
