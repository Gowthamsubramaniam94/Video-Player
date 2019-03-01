package com.gowtham.videoplayer.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.Toast;

import com.gowtham.videoplayer.R;
import com.gowtham.videoplayer.adapter.VideoListAdapter;
import com.gowtham.videoplayer.utils.NetworkManager;
import com.gowtham.videoplayer.webservice.CommonValues;
import com.gowtham.videoplayer.webservice.ReturnValues;
import com.gowtham.videoplayer.webservice.Webservice;
import com.gowtham.videoplayer.webservice.WebserviceCallback;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * Created by Gowtham on 28-02-2019.
 */

public class VideoListActivity extends AppCompatActivity implements CommonValues {

    private RecyclerView mVideoList;
    private VideoListAdapter mVideoAdapter;
    private NetworkManager mNetworkManager;
    private Webservice mWebservice;
    private ArrayList<ReturnValues.VideoDetails> mVideoDetailsList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        init();
    }

    //Initialization
    private void init() {
        mWebservice = new Webservice();
        mNetworkManager = new NetworkManager();
        mVideoList = findViewById(R.id.activity_main_RV);

        setupRecycler();
        loadValues();
    }

    //Setup RecyclerView
    private void setupRecycler() {
        LinearLayoutManager myLayoutManager = new LinearLayoutManager(getApplicationContext());
        mVideoList.setLayoutManager(myLayoutManager);
        mVideoList.setHasFixedSize(true);
        mVideoAdapter = new VideoListAdapter(getApplicationContext(), mVideoDetailsList);
        mVideoList.setAdapter(mVideoAdapter);
    }

    private void loadValues() {

        if (checkInternet()) {
            mWebservice.getVideoDetails(VideoListActivity.this, new WebserviceCallback() {
                @Override
                public void onSuccess(Object object) {
                    ReturnValues.VideoDetails[] aVideoDetails = (ReturnValues.VideoDetails[]) object;
                    if (aVideoDetails.length > 0) {
                        mVideoDetailsList.addAll(Arrays.asList(aVideoDetails));
                    }
                    mVideoAdapter.notifyDataSetChanged();
                }

                @Override
                public void onFailer(Object object) {
                    Toast.makeText(getApplicationContext(), SERVER_NOT_REACHABLE, Toast.LENGTH_LONG).show();
                }
            });
        }
    }

    //Check Internet Connection
    private boolean checkInternet() {
        return mNetworkManager.isInternetOn(getApplicationContext());
    }

}