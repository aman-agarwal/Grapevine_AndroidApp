package com.photocascades.grapevine;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import com.parse.FunctionCallback;
import com.parse.GetDataCallback;
import com.parse.ParseCloud;
import com.parse.ParseException;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by sdhpzhtk on 5/11/15.
 */
public class CollectionManager {
    // The array of photos to show in feed
    final List<PhotoObj> photos = Collections.synchronizedList(new ArrayList<PhotoObj>());
    // The map of the actual images to show in the feed
    private ConcurrentHashMap<PhotoObj, Bitmap> imgs = new ConcurrentHashMap<>();
    // The map of the image view of the photos for the feeds
    private ConcurrentHashMap<PhotoObj, ImageView> iViews = new ConcurrentHashMap<>();

    String mUserId = null;

    public CollectionManager(String userId) {
        Log.d("ColManager", mUserId);
        mUserId = userId;
        fill();
    }

    // Replenishing the photo array. Currently grabbing the first num_feed photos from cloud
    private void fill() {
        HashMap<String, Object> params = new HashMap<>();
        params.put("mUserId", mUserId);
        ParseCloud.callFunctionInBackground("fetch_grid", params,
                new FunctionCallback<List<PhotoObj>>() {
                    @Override
                    public void done(List<PhotoObj> photoList, ParseException e) {
                        if (e == null) {
                            Log.d("RetrieveObj", Integer.toString(photoList.size()));

                            for (PhotoObj photo : photoList) {
                                fetchImg(photo);
                            }
                            synchronized (photos) {
                                photos.addAll(photoList);
                            }
                        } else {
                            Log.e("RetrieveObj", e.toString());
                        }
                    }
                });
    }

    // Fetching the image of a photo object in the background
    private void fetchImg(final PhotoObj photo){
        Log.i("ColManager_fetchIMG", photo.getObjectId() + " start fetching");
        photo.getImg().getDataInBackground(new GetDataCallback() {
            @Override
            public void done(byte[] bytes, ParseException e) {
                Log.i("ColManager_fetchIMG", photo.getObjectId() + " DONE");
                Bitmap imgBM;
                if (e == null) {
                    imgBM = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);

                } else {
                    Log.e("ColManager_fetchIMG", e.toString());
                    imgBM = null;
                }

                if (iViews.containsKey(photo)) {
                    iViews.get(photo).setVisibility(View.VISIBLE);
                    iViews.get(photo).setImageBitmap(imgBM);
                }
                imgs.put(photo, imgBM);

            }
        });
    }

    // Get n_grid photos from index
    public List<PhotoObj> getList(int index, int n_grid) {
        return photos.subList(index, n_grid);
    }

    /* Set the imageView of the current photo. If not fetched yet, store the imageView for
       later.
    */
    public void setImageView(PhotoObj photo, ImageView view) {
        if (imgs.containsKey(photo))
            view.setImageBitmap(imgs.get(photo));
        else {
            view.setVisibility(View.INVISIBLE);
            iViews.put(photo, view);
        }
    }

    // Check for the photo object whether we have retrieved the actual image yet.
    public boolean isReady(PhotoObj photo) {
        return imgs.containsKey(photo) && imgs.get(photo) != null;
    }
}
