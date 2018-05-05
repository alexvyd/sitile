package ru.parvenu.sitile;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;
import android.support.v4.util.LruCache;
import android.util.Log;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class PicLoader<T> extends HandlerThread {
    private static final String TAG = "PicLoader";
    private static final int MESSAGE_DOWNLOAD = 0;
    private boolean mHasQuit = false;
    private Handler mRequestHandler;
    private ConcurrentMap<T,String> mRequestMap = new ConcurrentHashMap<>();

    private Handler mResponseHandler;
    private PicLoadListener<T> mPicLoadListener;
    private LruCache<String, Bitmap> mMemoryCache;



    public PicLoader(Handler responseHandler) {
        super(TAG);
        mResponseHandler = responseHandler;

        //Настраиваем кеш в памяти
        final int maxMemory = (int) (Runtime.getRuntime().maxMemory() / 1024);
        final int cacheSize = maxMemory / 8;
        mMemoryCache = new LruCache<String, Bitmap>(cacheSize) {
            @Override
            protected int sizeOf(String key, Bitmap bitmap) {
                return bitmap.getByteCount() / 1024;
            }
        };


    }

    public void addBitmapToMemoryCache(String key, Bitmap bitmap) {
        if (getBitmapFromMemCache(key) == null) {
            mMemoryCache.put(key, bitmap);
        }
    }

    public Bitmap getBitmapFromMemCache(String key) {
        return mMemoryCache.get(key);
    }

    public interface PicLoadListener<T> {
        void onPicLoaded(T target, Bitmap thumbnail);
    }

    public void setPicLoadListener(PicLoadListener<T> listener) {
        mPicLoadListener = listener;
    }

    @Override
    protected void onLooperPrepared() {
        mRequestHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                if (msg.what == MESSAGE_DOWNLOAD) {
                    T target = (T) msg.obj;
                    //Log.i(TAG, "Got a request for URL: " + mRequestMap.get(target));
                    handleRequest(target);
                }
            }
        };
    }

    @Override
    public boolean quit() {
        mHasQuit = true;
        return super.quit();
    }
    public void loadPic(T target, String url) {
        //Log.i(TAG, "Got a URL: " + url);
        if (url == null) {
            mRequestMap.remove(target);
        } else {
            mRequestMap.put(target, url);
            mRequestHandler.obtainMessage(MESSAGE_DOWNLOAD, target)
                    .sendToTarget();
        }
    }

    public void clearQueue() {
        mRequestHandler.removeMessages(MESSAGE_DOWNLOAD);
        mRequestMap.clear();
    }

    //выполнение загрузки по необходимости
    private void handleRequest(final T target) {
        try {
            final String url = mRequestMap.get(target);
            if (url == null) {
                return;
            }

            //проверка наличия в кеше
            final String imageKey = url;
            final Bitmap[] bitmap = {getBitmapFromMemCache(imageKey)};
            if (bitmap[0] == null) {
                byte[] bitmapBytes = new FlickrFetchr().getUrlBytes(url);
                bitmap[0] = BitmapFactory
                        .decodeByteArray(bitmapBytes, 0, bitmapBytes.length);
                //Log.i(TAG, "Bitmap created");
                addBitmapToMemoryCache(url, bitmap[0]);
            }

            mResponseHandler.post(new Runnable() {
                public void run() {
                    if (mRequestMap.get(target) != url ||
                            mHasQuit) {
                        return;
                    }
                    mRequestMap.remove(target);
                    mPicLoadListener.onPicLoaded(target,
                            bitmap[0]);
                }
            });

        } catch (IOException ioe) {
            Log.e(TAG, "Error downloading image", ioe);
        }
    }


}

