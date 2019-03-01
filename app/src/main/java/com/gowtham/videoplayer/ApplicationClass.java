package com.gowtham.videoplayer;

import android.app.Application;
import android.arch.persistence.room.Room;
import android.content.Context;
import android.text.TextUtils;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.gowtham.videoplayer.data.AppDatabase;
import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;

/**
 * Created by Gowtham on 28-02-2019.
 */

public class ApplicationClass extends Application {

    public static final String TAG = ApplicationClass.class.getSimpleName();
    private RequestQueue myRequestQueue;
    private static ApplicationClass myInstance;


    @Override
    public void onCreate() {
        super.onCreate();
        myInstance = this;
        initImageLoader(getApplicationContext());

        Room.databaseBuilder(this, AppDatabase.class, "app")
                .fallbackToDestructiveMigration()
                .build();
    }

    /**
     * This method is used to initialize image loader
     *
     * @param aContext
     */
    public static void initImageLoader(Context aContext) {

        try {
            ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(
                    aContext).threadPriority(Thread.NORM_PRIORITY - 2)
                    .denyCacheImageMultipleSizesInMemory()
                    .discCacheFileNameGenerator(new Md5FileNameGenerator())
                    .tasksProcessingOrder(QueueProcessingType.LIFO).build();

            ImageLoader.getInstance().init(config);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static synchronized ApplicationClass getInstance() {
        return myInstance;
    }


    public RequestQueue getRequestQueue() {

        try {
            if (myRequestQueue == null)
                myRequestQueue = Volley.newRequestQueue(getApplicationContext());
        } catch (Exception e) {
            e.printStackTrace();
        }

        return myRequestQueue;
    }

    public <T> void addToRequestQueue(Request<T> req, String tag) {
        try {
            // set the default tag if tag is empty
            req.setTag(TextUtils.isEmpty(tag) ? TAG : tag);
            getRequestQueue().add(req);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
