package com.gowtham.videoplayer.helper;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.ImageView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;

/**
 * Created by Gowtham on 28-02-2019.
 */

public class Helper {

    public static void loadImage(Context aContext, final ImageView aImageView, String aImageUrlStr, int aDefaultImage, int aErrorImage) {
        try {
            ImageLoader imageLoader = ImageLoader.getInstance();
            imageLoader.init(ImageLoaderConfiguration.createDefault(aContext));
            DisplayImageOptions options = new DisplayImageOptions.Builder()
                    .cacheInMemory(true)
                    .cacheOnDisk(true)
                    .imageScaleType(ImageScaleType.EXACTLY)
                    .resetViewBeforeLoading(true)
                    .showImageForEmptyUri(ContextCompat.getDrawable(aContext, aDefaultImage))
                    .showImageOnFail(ContextCompat.getDrawable(aContext, aErrorImage))
                    .showImageOnLoading(ContextCompat.getDrawable(aContext, aDefaultImage))
                    .build();

            //download and display image from url
            imageLoader.displayImage(aImageUrlStr, aImageView, options, new ImageLoadingListener() {
                @Override
                public void onLoadingStarted(String s, View view) {

                }

                @Override
                public void onLoadingFailed(String s, View view, FailReason failReason) {

                }

                @Override
                public void onLoadingComplete(String s, View view, Bitmap bitmap) {
                    //   Log.d(TAG, "onLoadingComplete: " + s);

                }


                @Override
                public void onLoadingCancelled(String s, View view) {

                }
            });
        } catch (OutOfMemoryError e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
