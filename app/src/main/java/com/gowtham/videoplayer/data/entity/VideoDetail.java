package com.gowtham.videoplayer.data.entity;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

@Entity(tableName = "VideoDetail")
public class VideoDetail implements Serializable {

    @PrimaryKey()
    @NonNull
    @ColumnInfo(name = "id")
    @SerializedName("id")
    public String id;

    @ColumnInfo(name = "duration")
    @SerializedName("duration")
    public String duration;


}
