package com.gowtham.videoplayer.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.gowtham.videoplayer.R;
import com.gowtham.videoplayer.helper.Helper;
import com.gowtham.videoplayer.videoplayer.VideoPlayerActivity;
import com.gowtham.videoplayer.webservice.ReturnValues;

import java.util.ArrayList;

/**
 * Created by Gowtham on 28-02-2019.
 */

public class VideoListAdapter extends RecyclerView.Adapter<VideoListAdapter.ViewHolder> {

    private Context mContext;
    private ArrayList<ReturnValues.VideoDetails> mVideoDetailsList = new ArrayList<>();

    public VideoListAdapter(Context aContext, ArrayList<ReturnValues.VideoDetails> aVideoDetailsList) {
        mContext = aContext;
        mVideoDetailsList = aVideoDetailsList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View aView = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.layout_inflate_video_list, viewGroup, false);
        return new ViewHolder(aView);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, final int i) {

        final ReturnValues.VideoDetails aVideoDetails = mVideoDetailsList.get(i);

        Helper.loadImage(mContext, viewHolder.mVideoThumbIMG, aVideoDetails.thumb, R.drawable.ic_launcher_background, R.drawable.ic_launcher_background);

        viewHolder.mVideoTitleTXT.setText(aVideoDetails.title);
        viewHolder.mVideoDescTXT.setText(aVideoDetails.description);

        viewHolder.mMainLAY.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (aVideoDetails.url != null && !aVideoDetails.url.equals(""))
                    mContext.startActivity(VideoPlayerActivity.getStartIntent(mContext, i, aVideoDetails.id, mVideoDetailsList));
            }
        });

    }

    @Override
    public int getItemCount() {
        return mVideoDetailsList.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        private AppCompatImageView mVideoThumbIMG;
        private AppCompatTextView mVideoTitleTXT, mVideoDescTXT;
        private RelativeLayout mMainLAY;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            mVideoThumbIMG = itemView.findViewById(R.id.video_thumb_IMG);
            mVideoTitleTXT = itemView.findViewById(R.id.video_title_TXt);
            mVideoDescTXT = itemView.findViewById(R.id.video_description_TXt);
            mMainLAY = itemView.findViewById(R.id.video_main_lay);
        }
    }
}
