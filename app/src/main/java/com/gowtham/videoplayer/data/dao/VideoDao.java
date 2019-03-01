package com.gowtham.videoplayer.data.dao;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;

import com.gowtham.videoplayer.data.entity.VideoDetail;

import java.util.ArrayList;
import java.util.List;

@Dao
public interface VideoDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(VideoDetail aVideoDetail);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertVideoDuration(ArrayList<VideoDetail> aVideoDetail);

    @Query("UPDATE VideoDetail SET duration = :aDuration WHERE id = :aID")
    void update(String aID, String aDuration);

    @Delete
    void delete(VideoDetail... aVideoDetail);

    @Query("DELETE FROM VideoDetail")
    void deleteAll();

    @Query("SELECT * FROM VideoDetail where  id = :aID and duration = :aDuration")
    List<VideoDetail> getVideoDuration(String aID, String aDuration);


    @Query("SELECT * FROM VideoDetail where  id = :aID")
    VideoDetail getVideoDuration(String aID);

    @Query("SELECT * FROM VideoDetail")
    List<VideoDetail> getVideoDurationEmpty();
}
