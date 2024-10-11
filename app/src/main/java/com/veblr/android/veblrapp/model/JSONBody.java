package com.veblr.android.veblrapp.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

public class JSONBody  implements Serializable {

    @SerializedName("param")
    @Expose
    private Param param;
    private final static long serialVersionUID = 5725380348373051952L;

    public Param getParam() {
        return param;
    }

    public void setParam(Param param) {
        this.param = param;
    }
    public JSONBody(){}
   public static class Param implements Serializable {

        @SerializedName("max_results")
        @Expose
        private Integer maxResults;
        @SerializedName("sort")
        @Expose
        private String sort;
        @SerializedName("channel_name")
        @Expose
        private String channelName;
        @SerializedName("search1")
        @Expose
        private String search;
        @SerializedName("page_number")
        @Expose
        private Integer pageNumber;
        @SerializedName("language")
        @Expose
        private List<String> language = null;
        @SerializedName("category")
        @Expose
        private List<String> category = null;
        @SerializedName("country")
        @Expose
        private List<Integer> country = null;
        @SerializedName("exclude_video_id1")
        @Expose
        private List<String> excludeVideoId1 = null;
        @SerializedName("cache_data_chk")
        @Expose
        private Boolean cacheDataChk;
        private final static long serialVersionUID = -2005891538969430179L;

        public Integer getMaxResults() {
            return maxResults;
        }

        public void setMaxResults(Integer maxResults) {
            this.maxResults = maxResults;
        }

        public String getSort() {
            return sort;
        }

        public void setSort(String sort) {
            this.sort = sort;
        }

        public String getChannelName() {
            return channelName;
        }

        public void setChannelName(String channelName) {
            this.channelName = channelName;
        }

        public String getSearch() {
            return search;
        }

        public void setSearch(String search) {
            this.search = search;
        }

        public Integer getPageNumber() {
            return pageNumber;
        }

        public void setPageNumber(Integer pageNumber) {
            this.pageNumber = pageNumber;
        }

        public List<String> getLanguage() {
            return language;
        }

        public void setLanguage(List<String> language) {
            this.language = language;
        }

        public List<String> getCategory() {
            return category;
        }

        public void setCategory(List<String> category) {
            this.category = category;
        }

        public List<Integer> getCountry() {
            return country;
        }

        public void setCountry(List<Integer> country) {
            this.country = country;
        }

        public List<String> getExcludeVideoId1() {
            return excludeVideoId1;
        }

        public void setExcludeVideoId1(List<String> excludeVideoId1) {
            this.excludeVideoId1 = excludeVideoId1;
        }

        public Boolean getCacheDataChk() {
            return cacheDataChk;
        }

        public void setCacheDataChk(Boolean cacheDataChk) {
            this.cacheDataChk = cacheDataChk;
        }

        public Param() {
        }

        public Param(Integer maxResults, String search) {

            this.maxResults = maxResults;
            this.search = search;
        }
    }

}
