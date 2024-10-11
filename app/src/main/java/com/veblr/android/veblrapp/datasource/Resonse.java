package com.veblr.android.veblrapp.datasource;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.veblr.android.veblrapp.model.VIdeoItem;

import java.io.Serializable;
import java.util.List;

public class Resonse implements Serializable {

    @SerializedName("status")
    @Expose
    private Integer status;
    @SerializedName("result")
    @Expose
    private List<VIdeoItem> result = null;
    private final static long serialVersionUID = 3604737417113897610L;

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public List<VIdeoItem> getResult() {
        return result;
    }

    public void setResult(List<VIdeoItem> result) {
        this.result = result;
    }

}