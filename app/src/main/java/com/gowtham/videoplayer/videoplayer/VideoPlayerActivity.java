package com.gowtham.videoplayer.videoplayer;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.AppCompatDelegate;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ProgressBar;

import com.google.android.exoplayer2.C;
import com.google.android.exoplayer2.DefaultLoadControl;
import com.google.android.exoplayer2.DefaultRenderersFactory;
import com.google.android.exoplayer2.ExoPlaybackException;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.LoadControl;
import com.google.android.exoplayer2.PlaybackParameters;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.Timeline;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.TrackGroupArray;
import com.google.android.exoplayer2.trackselection.AdaptiveTrackSelection;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.TrackSelection;
import com.google.android.exoplayer2.trackselection.TrackSelectionArray;
import com.google.android.exoplayer2.trackselection.TrackSelector;
import com.google.android.exoplayer2.ui.PlayerView;
import com.google.android.exoplayer2.upstream.BandwidthMeter;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DefaultAllocator;
import com.google.android.exoplayer2.upstream.DefaultBandwidthMeter;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.util.Util;
import com.gowtham.videoplayer.R;
import com.gowtham.videoplayer.adapter.RelatedVideoListAdapter;
import com.gowtham.videoplayer.data.AppDatabase;
import com.gowtham.videoplayer.data.entity.VideoDetail;
import com.gowtham.videoplayer.webservice.ReturnValues;

import java.util.ArrayList;

/**
 * Created by Gowtham on 28-02-2019.
 */

public class VideoPlayerActivity extends AppCompatActivity implements Player.EventListener {

    private static final String TAG = "VideoPlayerActivity";

    private static final String KEY_VIDEO_POSITION = "Video_List";
    private static final String KEY_VIDEO_ID = "Video_ID";
    private static final String KEY_VIDEO_ARRAY = "Video_Array";

    private PlayerView mVideoFullScreenPlayer;
    private ProgressBar mSpinnerVideoDetails;
    private AppCompatButton mPlayBTN;
    private RecyclerView mRelatedList;
    private RelatedVideoListAdapter mAdapter;
    private ArrayList<ReturnValues.VideoDetails> mVideoArrayList = new ArrayList<>();
    private ArrayList<VideoDetail> mVideoDetailList = new ArrayList<>();
    private ArrayList<ReturnValues.VideoDetails> mRemovedVideoArrayList = new ArrayList<>();
    private AppCompatTextView mTitleTXT, mDescriptionTXT;
    private Uri videoUri;
    private SimpleExoPlayer player;
    private Handler mHandler;
    private Runnable mRunnable;
    private int mPosition;
    private String mVideoID;
    AppDatabase mAppDatabase;
    View nextButton;
    Timeline.Window window;

    public static Intent getStartIntent(Context context, int aPosition, String id, ArrayList<ReturnValues.VideoDetails> mVideoDetailsList) {
        Intent intent = new Intent(context, VideoPlayerActivity.class);
        intent.putExtra(KEY_VIDEO_POSITION, aPosition);
        intent.putExtra(KEY_VIDEO_ID, id);
        intent.putExtra(KEY_VIDEO_ARRAY, mVideoDetailsList);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_video_detail);

        mAppDatabase = AppDatabase.getDatabase(this);
        mVideoFullScreenPlayer = findViewById(R.id.videoFullScreenPlayer);
        mSpinnerVideoDetails = findViewById(R.id.spinnerVideoDetails);
        mPlayBTN = findViewById(R.id.activity_video_detail_play_BTN);
        mRelatedList = findViewById(R.id.activity_video_detail_related_list);
        nextButton = findViewById(R.id.exo_next);

        mTitleTXT = findViewById(R.id.activity_video_detail_title);
        mDescriptionTXT = findViewById(R.id.activity_video_detail_description);

        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);
        getSupportActionBar().hide();

        if (getIntent().hasExtra(KEY_VIDEO_POSITION) && getIntent().hasExtra(KEY_VIDEO_ARRAY)) {
            mPosition = getIntent().getIntExtra(KEY_VIDEO_POSITION, 0);
            mVideoID = getIntent().getStringExtra(KEY_VIDEO_ID);
            mVideoArrayList = (ArrayList<ReturnValues.VideoDetails>) getIntent().getSerializableExtra(KEY_VIDEO_ARRAY);
            for (ReturnValues.VideoDetails aVideoDetails : mVideoArrayList) {
                if (aVideoDetails.id.equals(mVideoID))
                    videoUri = Uri.parse(aVideoDetails.url);
            }
            //videoUri = Uri.parse(mVideoArrayList.get(mPosition).url);
        }

        if (mAppDatabase.aVideoDao().getVideoDurationEmpty().size() == 0) {
            for (int i = 0; i < mVideoArrayList.size(); i++) {
                ReturnValues.VideoDetails aVideoDetailList = mVideoArrayList.get(i);

                VideoDetail aVideoDetail = new VideoDetail();

                aVideoDetail.id = aVideoDetailList.id;
                aVideoDetail.duration = "0";

                mVideoDetailList.add(aVideoDetail);
            }
            mAppDatabase.aVideoDao().insertVideoDuration(mVideoDetailList);
        }

        setUp();
        setupRecyclerView();
        loadValues();

        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                next();
            }
        });
    }

    private void setupRecyclerView() {

        LinearLayoutManager aLinearLayoutManager = new LinearLayoutManager(getApplicationContext());
        mRelatedList.setLayoutManager(aLinearLayoutManager);
        mRelatedList.setHasFixedSize(true);

        //remove current position
        mRemovedVideoArrayList.addAll(mVideoArrayList);
        for (ReturnValues.VideoDetails aVideoDetails : mVideoArrayList) {
            if (aVideoDetails.id.equals(mVideoID))
                mRemovedVideoArrayList.remove(aVideoDetails);
        }

        mAdapter = new RelatedVideoListAdapter(this, mVideoArrayList, mRemovedVideoArrayList);
        mRelatedList.setAdapter(mAdapter);

    }

    private void loadValues() {

        for (ReturnValues.VideoDetails aVideoDetails : mVideoArrayList) {
            if (aVideoDetails.id.equals(mVideoID)) {
                mTitleTXT.setText(aVideoDetails.title);
                mDescriptionTXT.setText(aVideoDetails.description);
            }
        }
    }

    private void setUp() {
        initializePlayer();
        if (videoUri == null) {
            return;
        }
        buildMediaSource(videoUri);
    }

    private void initializePlayer() {
        if (player == null) {
            // 1. Create a default TrackSelector
            LoadControl loadControl = new DefaultLoadControl(
                    new DefaultAllocator(true, 16),
                    VideoPlayerConfig.MIN_BUFFER_DURATION,
                    VideoPlayerConfig.MAX_BUFFER_DURATION,
                    VideoPlayerConfig.MIN_PLAYBACK_START_BUFFER,
                    VideoPlayerConfig.MIN_PLAYBACK_RESUME_BUFFER, -1, true);

            BandwidthMeter bandwidthMeter = new DefaultBandwidthMeter();
            TrackSelection.Factory videoTrackSelectionFactory =
                    new AdaptiveTrackSelection.Factory(bandwidthMeter);
            TrackSelector trackSelector =
                    new DefaultTrackSelector(videoTrackSelectionFactory);
            // 2. Create the player
            player = ExoPlayerFactory.newSimpleInstance(new DefaultRenderersFactory(this), trackSelector, loadControl);
            mVideoFullScreenPlayer.setPlayer(player);
        }
    }

    private void buildMediaSource(Uri mUri) {
        // Measures bandwidth during playback. Can be null if not required.
        DefaultBandwidthMeter bandwidthMeter = new DefaultBandwidthMeter();
        // Produces DataSource instances through which media data is loaded.
        DataSource.Factory dataSourceFactory = new DefaultDataSourceFactory(this,
                Util.getUserAgent(this, getString(R.string.app_name)), bandwidthMeter);
        // This is the MediaSource representing the media to be played.
        MediaSource videoSource = new ExtractorMediaSource.Factory(dataSourceFactory)
                .createMediaSource(mUri);

        // Prepare the player with the source.
        player.prepare(videoSource);
        player.setPlayWhenReady(true);
        player.addListener(this);


        VideoDetail sss = (mAppDatabase.aVideoDao().getVideoDuration(mVideoArrayList.get(mPosition).id));
        long along = Long.parseLong(sss.duration);
        player.seekTo(along);

    }

    private void releasePlayer() {
        if (player != null) {
            player.release();
            player = null;
        }
    }

    private void pausePlayer() {
        if (player != null) {
            player.setPlayWhenReady(false);
            player.getPlaybackState();
            if (player.getCurrentPosition() > 0) {
                VideoDetail aVideoDetail = new VideoDetail();
                aVideoDetail.id = mVideoArrayList.get(mPosition).id;
                aVideoDetail.duration = String.valueOf(player.getCurrentPosition());

                mAppDatabase.aVideoDao().update(mVideoArrayList.get(mPosition).id, String.valueOf(player.getCurrentPosition()));
            }
        }
    }

    private void resumePlayer() {
        if (player != null) {
            player.setPlayWhenReady(true);
            player.getPlaybackState();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        pausePlayer();
        if (mRunnable != null) {
            mHandler.removeCallbacks(mRunnable);
        }
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        resumePlayer();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        releasePlayer();
    }

    @Override
    public void onTimelineChanged(Timeline timeline, Object manifest, int reason) {

    }

    @Override
    public void onTracksChanged(TrackGroupArray trackGroups, TrackSelectionArray trackSelections) {

    }

    @Override
    public void onLoadingChanged(boolean isLoading) {

    }


    @Override
    public void onPlayerStateChanged(boolean playWhenReady, int playbackState) {
        switch (playbackState) {

            case Player.STATE_BUFFERING:
                mSpinnerVideoDetails.setVisibility(View.VISIBLE);
                break;
            case Player.STATE_ENDED:
                // Activate the force enable
                mAppDatabase.aVideoDao().update(mVideoID, "0");
                if (mPosition < mVideoArrayList.size() - 1) {
                    ((VideoPlayerActivity) this).finish();
                    startActivity(VideoPlayerActivity.getStartIntent(this, mPosition + 1, mVideoArrayList.get(mPosition + 1).id, mVideoArrayList));
                } else
                    finish();
                break;
            case Player.STATE_IDLE:

                break;
            case Player.STATE_READY:
                mSpinnerVideoDetails.setVisibility(View.GONE);
                break;

            default:
                // status = PlaybackStatus.IDLE;
                break;
        }
    }

    private void next() {
        Timeline timeline = player.getCurrentTimeline();
        if (timeline.isEmpty()) {
            return;
        }
        int windowIndex = player.getCurrentWindowIndex();
        if (windowIndex < timeline.getWindowCount() - 1) {
            player.seekTo(windowIndex + 1, C.TIME_UNSET);
        } else if (timeline.getWindow(windowIndex, window, false).isDynamic) {
            player.seekTo(windowIndex, C.TIME_UNSET);
        }
    }


    @Override
    public void onRepeatModeChanged(int repeatMode) {
    }

    @Override
    public void onShuffleModeEnabledChanged(boolean shuffleModeEnabled) {

    }

    @Override
    public void onPlayerError(ExoPlaybackException error) {

    }

    @Override
    public void onPositionDiscontinuity(int reason) {

    }

    @Override
    public void onPlaybackParametersChanged(PlaybackParameters playbackParameters) {

    }

    @Override
    public void onSeekProcessed() {

    }


}

