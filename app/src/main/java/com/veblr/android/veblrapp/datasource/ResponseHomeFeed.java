package com.veblr.android.veblrapp.datasource;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.veblr.android.veblrapp.model.VIdeoItem;

import java.io.Serializable;
import java.util.List;

public class ResponseHomeFeed implements Serializable {

    @SerializedName("response")
    @Expose
    private Response response;
    private final static long serialVersionUID = -7412841726803375457L;

    /**
     * No args constructor for use in serialization
     */
    public ResponseHomeFeed() {
    }

    /**
     * @param response
     */
    public ResponseHomeFeed(Response response) {
        super();
        this.response = response;
    }

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
        private Result result;
        private final static long serialVersionUID = 3343150950840156470L;

        /**
         * No args constructor for use in serialization
         *
         */
        public Response() {
        }

        /**
         *
         * @param result
         * @param status
         */
        public Response(Integer status, Result result) {
            super();
            this.status = status;
            this.result = result;
        }

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

    public class Result implements Serializable
    {

        @SerializedName("index_top")
        @Expose
        private IndexTop indexTop;
        @SerializedName("index_right")
        @Expose
        private IndexRight indexRight;
        @SerializedName("index_middle2")
        @Expose
        private IndexMiddle2 indexMiddle2;
        @SerializedName("index_middle1")
        @Expose
        private IndexMiddle1 indexMiddle1;
        @SerializedName("index_left2")
        @Expose
        private IndexLeft2 indexLeft2;
        @SerializedName("index_left1")
        @Expose
        private IndexLeft1 indexLeft1;
        @SerializedName("index_bottom2")
        @Expose
        private IndexBottom2 indexBottom2;
        @SerializedName("index_bottom1")
        @Expose
        private IndexBottom1 indexBottom1;
        private final static long serialVersionUID = 5416264946662565671L;

        /**
         * No args constructor for use in serialization
         *
         */
        public Result() {
        }

        /**
         *
         * @param indexMiddle2
         * @param indexMiddle1
         * @param indexRight
         * @param indexTop
         * @param indexBottom1
         * @param indexBottom2
         * @param indexLeft2
         * @param indexLeft1
         */
        public Result(IndexTop indexTop, IndexRight indexRight, IndexMiddle2 indexMiddle2,
                      IndexMiddle1 indexMiddle1, IndexLeft2 indexLeft2, IndexLeft1 indexLeft1,
                      IndexBottom2 indexBottom2, IndexBottom1 indexBottom1) {
            super();
            this.indexTop = indexTop;
            this.indexRight = indexRight;
            this.indexMiddle2 = indexMiddle2;
            this.indexMiddle1 = indexMiddle1;
            this.indexLeft2 = indexLeft2;
            this.indexLeft1 = indexLeft1;
            this.indexBottom2 = indexBottom2;
            this.indexBottom1 = indexBottom1;
        }

        public IndexTop getIndexTop() {
            return indexTop;
        }

        public void setIndexTop(IndexTop indexTop) {
            this.indexTop = indexTop;
        }

        public IndexRight getIndexRight() {
            return indexRight;
        }

        public void setIndexRight(IndexRight indexRight) {
            this.indexRight = indexRight;
        }

        public IndexMiddle2 getIndexMiddle2() {
            return indexMiddle2;
        }

        public void setIndexMiddle2(IndexMiddle2 indexMiddle2) {
            this.indexMiddle2 = indexMiddle2;
        }

        public IndexMiddle1 getIndexMiddle1() {
            return indexMiddle1;
        }

        public void setIndexMiddle1(IndexMiddle1 indexMiddle1) {
            this.indexMiddle1 = indexMiddle1;
        }

        public IndexLeft2 getIndexLeft2() {
            return indexLeft2;
        }

        public void setIndexLeft2(IndexLeft2 indexLeft2) {
            this.indexLeft2 = indexLeft2;
        }

        public IndexLeft1 getIndexLeft1() {
            return indexLeft1;
        }

        public void setIndexLeft1(IndexLeft1 indexLeft1) {
            this.indexLeft1 = indexLeft1;
        }

        public IndexBottom2 getIndexBottom2() {
            return indexBottom2;
        }

        public void setIndexBottom2(IndexBottom2 indexBottom2) {
            this.indexBottom2 = indexBottom2;
        }

        public IndexBottom1 getIndexBottom1() {
            return indexBottom1;
        }

        public void setIndexBottom1(IndexBottom1 indexBottom1) {
            this.indexBottom1 = indexBottom1;
        }

    }



public class IndexBottom1 implements Serializable
{

    @SerializedName("section_name")
    @Expose
    private String sectionName;
    @SerializedName("tag_name")
    @Expose
    private String tagName;
    @SerializedName("videos")
    @Expose
    private List<VIdeoItem> videos = null;
    private final static long serialVersionUID = 5428115707315630339L;

    /**
     * No args constructor for use in serialization
     *
     */
    public IndexBottom1() {
    }

    /**
     *
     * @param sectionName
     * @param videos
     * @param tagName
     */
    public IndexBottom1(String sectionName, String tagName, List<VIdeoItem> videos) {
        super();
        this.sectionName = sectionName;
        this.tagName = tagName;
        this.videos = videos;
    }

    public String getSectionName() {
        return sectionName;
    }

    public void setSectionName(String sectionName) {
        this.sectionName = sectionName;
    }

    public String getTagName() {
        return tagName;
    }

    public void setTagName(String tagName) {
        this.tagName = tagName;
    }

    public List<VIdeoItem> getVideos() {
        return videos;
    }

    public void setVideos(List<VIdeoItem> videos) {
        this.videos = videos;
    }

}
public class IndexBottom2 implements Serializable
{

    @SerializedName("section_name")
    @Expose
    private String sectionName;
    @SerializedName("tag_name")
    @Expose
    private String tagName;
    @SerializedName("videos")
    @Expose
    private List<VIdeoItem> videos = null;
    private final static long serialVersionUID = 6372023131425624220L;

    /**
     * No args constructor for use in serialization
     *
     */
    public IndexBottom2() {
    }

    /**
     *
     * @param sectionName
     * @param videos
     * @param tagName
     */
    public IndexBottom2(String sectionName, String tagName, List<VIdeoItem> videos) {
        super();
        this.sectionName = sectionName;
        this.tagName = tagName;
        this.videos = videos;
    }

    public String getSectionName() {
        return sectionName;
    }

    public void setSectionName(String sectionName) {
        this.sectionName = sectionName;
    }

    public String getTagName() {
        return tagName;
    }

    public void setTagName(String tagName) {
        this.tagName = tagName;
    }

    public List<VIdeoItem> getVideos() {
        return videos;
    }

    public void setVideos(List<VIdeoItem> videos) {
        this.videos = videos;
    }

}
    public class IndexTop implements Serializable
    {

        @SerializedName("section_name")
        @Expose
        private String sectionName;
        @SerializedName("tag_name")
        @Expose
        private String tagName;
        @SerializedName("videos")
        @Expose
        private List<VIdeoItem> videos = null;
        private final static long serialVersionUID = 315496563528150170L;

        /**
         * No args constructor for use in serialization
         *
         */
        public IndexTop() {
        }

        /**
         *
         * @param sectionName
         * @param videos
         * @param tagName
         */
        public IndexTop(String sectionName, String tagName, List<VIdeoItem> videos) {
            super();
            this.sectionName = sectionName;
            this.tagName = tagName;
            this.videos = videos;
        }

        public String getSectionName() {
            return sectionName;
        }

        public void setSectionName(String sectionName) {
            this.sectionName = sectionName;
        }

        public String getTagName() {
            return tagName;
        }

        public void setTagName(String tagName) {
            this.tagName = tagName;
        }

        public List<VIdeoItem> getVideos() {
            return videos;
        }

        public void setVideos(List<VIdeoItem> videos) {
            this.videos = videos;
        }

    }
    public class IndexRight implements Serializable
    {

        @SerializedName("section_name")
        @Expose
        private String sectionName;
        @SerializedName("tag_name")
        @Expose
        private String tagName;
        @SerializedName("videos")
        @Expose
        private List<VIdeoItem> videos = null;
        private final static long serialVersionUID = -675990961822402019L;

        /**
         * No args constructor for use in serialization
         *
         */
        public IndexRight() {
        }

        /**
         *
         * @param sectionName
         * @param videos
         * @param tagName
         */
        public IndexRight(String sectionName, String tagName, List<VIdeoItem> videos) {
            super();
            this.sectionName = sectionName;
            this.tagName = tagName;
            this.videos = videos;
        }

        public String getSectionName() {
            return sectionName;
        }

        public void setSectionName(String sectionName) {
            this.sectionName = sectionName;
        }

        public String getTagName() {
            return tagName;
        }

        public void setTagName(String tagName) {
            this.tagName = tagName;
        }

        public List<VIdeoItem> getVideos() {
            return videos;
        }

        public void setVideos(List<VIdeoItem> videos) {
            this.videos = videos;
        }

    }
    public class IndexMiddle2 implements Serializable
    {

        @SerializedName("section_name")
        @Expose
        private String sectionName;
        @SerializedName("tag_name")
        @Expose
        private String tagName;
        @SerializedName("videos")
        @Expose
        private List<VIdeoItem> videos = null;
        private final static long serialVersionUID = 8481652217340211523L;

        /**
         * No args constructor for use in serialization
         *
         */
        public IndexMiddle2() {
        }

        /**
         *
         * @param sectionName
         * @param videos
         * @param tagName
         */
        public IndexMiddle2(String sectionName, String tagName, List<VIdeoItem> videos) {
            super();
            this.sectionName = sectionName;
            this.tagName = tagName;
            this.videos = videos;
        }

        public String getSectionName() {
            return sectionName;
        }

        public void setSectionName(String sectionName) {
            this.sectionName = sectionName;
        }

        public String getTagName() {
            return tagName;
        }

        public void setTagName(String tagName) {
            this.tagName = tagName;
        }

        public List<VIdeoItem> getVideos() {
            return videos;
        }

        public void setVideos(List<VIdeoItem> videos) {
            this.videos = videos;
        }

    }
    public class IndexMiddle1 implements Serializable
    {

        @SerializedName("section_name")
        @Expose
        private String sectionName;
        @SerializedName("tag_name")
        @Expose
        private String tagName;
        @SerializedName("videos")
        @Expose
        private List<VIdeoItem> videos = null;
        private final static long serialVersionUID = 6505951904271980052L;

        /**
         * No args constructor for use in serialization
         *
         */
        public IndexMiddle1() {
        }

        /**
         *
         * @param sectionName
         * @param videos
         * @param tagName
         */
        public IndexMiddle1(String sectionName, String tagName, List<VIdeoItem> videos) {
            super();
            this.sectionName = sectionName;
            this.tagName = tagName;
            this.videos = videos;
        }

        public String getSectionName() {
            return sectionName;
        }

        public void setSectionName(String sectionName) {
            this.sectionName = sectionName;
        }

        public String getTagName() {
            return tagName;
        }

        public void setTagName(String tagName) {
            this.tagName = tagName;
        }

        public List<VIdeoItem> getVideos() {
            return videos;
        }

        public void setVideos(List<VIdeoItem> videos) {
            this.videos = videos;
        }

    }
    public class IndexLeft2 implements Serializable
    {

        @SerializedName("section_name")
        @Expose
        private String sectionName;
        @SerializedName("tag_name")
        @Expose
        private String tagName;
        @SerializedName("videos")
        @Expose
        private List<VIdeoItem> videos = null;
        private final static long serialVersionUID = -6525273953389251928L;

        /**
         * No args constructor for use in serialization
         *
         */
        public IndexLeft2() {
        }

        /**
         *
         * @param sectionName
         * @param videos
         * @param tagName
         */
        public IndexLeft2(String sectionName, String tagName, List<VIdeoItem> videos) {
            super();
            this.sectionName = sectionName;
            this.tagName = tagName;
            this.videos = videos;
        }

        public String getSectionName() {
            return sectionName;
        }

        public void setSectionName(String sectionName) {
            this.sectionName = sectionName;
        }

        public String getTagName() {
            return tagName;
        }

        public void setTagName(String tagName) {
            this.tagName = tagName;
        }

        public List<VIdeoItem> getVideos() {
            return videos;
        }

        public void setVideos(List<VIdeoItem> videos) {
            this.videos = videos;
        }

    }
    public class IndexLeft1 implements Serializable
    {

        @SerializedName("section_name")
        @Expose
        private String sectionName;
        @SerializedName("tag_name")
        @Expose
        private String tagName;
        @SerializedName("videos")
        @Expose
        private List<VIdeoItem> videos = null;
        private final static long serialVersionUID = 931812586809750150L;

        /**
         * No args constructor for use in serialization
         *
         */
        public IndexLeft1() {
        }

        /**
         *
         * @param sectionName
         * @param videos
         * @param tagName
         */
        public IndexLeft1(String sectionName, String tagName, List<VIdeoItem> videos) {
            super();
            this.sectionName = sectionName;
            this.tagName = tagName;
            this.videos = videos;
        }

        public String getSectionName() {
            return sectionName;
        }

        public void setSectionName(String sectionName) {
            this.sectionName = sectionName;
        }

        public String getTagName() {
            return tagName;
        }

        public void setTagName(String tagName) {
            this.tagName = tagName;
        }

        public List<VIdeoItem> getVideos() {
            return videos;
        }

        public void setVideos(List<VIdeoItem> videos) {
            this.videos = videos;
        }

    }
}
