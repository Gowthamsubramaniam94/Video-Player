package com.gowtham.videoplayer.webservice;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by Gowtham on 28-02-2019.
 */

public class ReturnValues {

    public VideoDetails getVideoDetailsInstance() {
        return new VideoDetails();
    }

    public static class VideoDetails implements Serializable {

        @SerializedName("description")
        public String description;

        @SerializedName("id")
        public String id;

        @SerializedName("thumb")
        public String thumb;

        @SerializedName("title")
        public String title;

        @SerializedName("url")
        public String url;

        @SerializedName("duration")
        public String duration;

        @SerializedName("isPlayed")
        public int isPlayed;

    }

}
