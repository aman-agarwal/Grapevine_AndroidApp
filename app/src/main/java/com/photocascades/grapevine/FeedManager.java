package com.photocascades.grapevine;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.parse.FindCallback;
import com.parse.FunctionCallback;
import com.parse.GetCallback;
import com.parse.GetDataCallback;
import com.parse.ParseCloud;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by sdhpzhtk on 5/1/15.
 */
public class FeedManager {
    // Number of feeds fetching each time.
    final static int num_feed = 5;

    // Status for each feed
    final static int LIKE = FeedObj.LIKE;
    final static int UNLIKE = FeedObj.UNLIKE;
    final static int UNSET = FeedObj.UNSET;

    // The array of photos to show in feed
    private final List<PhotoObj> photos = Collections.synchronizedList(new ArrayList<PhotoObj>());
    // The map of the actual images to show in the feed
    private ConcurrentHashMap<PhotoObj, Bitmap> imgs = new ConcurrentHashMap<>();
    // The map of the authors of the photos to show in the feed
    private ConcurrentHashMap<PhotoObj, ParseUser> authors = new ConcurrentHashMap<>();
    // The map of the text view of the authors of the photos for the feeds
    private ConcurrentHashMap<PhotoObj, TextView> aViews = new ConcurrentHashMap<>();
    // The map of the image view of the photos for the feeds
    private ConcurrentHashMap<PhotoObj, ImageView> iViews = new ConcurrentHashMap<>();
    // Stores the comment for the current feed
    private final List<CommentObj> comments = Collections.synchronizedList(new ArrayList<CommentObj>());
    // Stores the replies for each comment
    private ConcurrentHashMap<CommentObj, List<CommentObj>> replies = new ConcurrentHashMap<>();

    public FeedManager() {
        Log.d("FeedManager", ParseUser.getCurrentUser().getUsername());
        fill();
    }

    // Replenishing the photo array. Currently grabbing the first num_feed photos from cloud
    private void fill() {
        HashMap<String, Object> params = new HashMap<>();
        params.put("num_feed", num_feed);
        ParseCloud.callFunctionInBackground("fetch", params,
                new FunctionCallback<List<PhotoObj>>() {
            @Override
            public void done(List<PhotoObj> photoList, ParseException e) {
                if (e == null) {
                    Log.d("RetrieveObj", Integer.toString(photoList.size()));

                    for (PhotoObj photo: photoList) {
                        fetchImg(photo);
                        fetchAuthor(photo);
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
        Log.i("FeedManager_fetchIMG", photo.getObjectId() + " start fetching");
        photo.getImg().getDataInBackground(new GetDataCallback() {
            @Override
            public void done(byte[] bytes, ParseException e) {
                Log.i("FeedManager_fetchIMG", photo.getObjectId() + " DONE");
                Bitmap imgBM;
                if (e == null) {
                    imgBM = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);

                } else {
                    Log.e("FeedManager_fetchIMG", e.toString());
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

    // Fetching the author ParseUser of the photo in the background
    private void fetchAuthor(final PhotoObj photo){
        Log.i("FeedManager_fetchAUT", photo.getObjectId() + " start fetching");
        photo.getAuthor().fetchInBackground(new GetCallback<ParseUser>() {
            @Override
            public void done(ParseUser parseObject, ParseException e) {
                Log.i("FeedManager_fetchAUT", photo.getObjectId() + " DONE");
                ParseUser ret;
                if (e == null) {
                    ret = parseObject;
                } else {
                    Log.e("FeedManager_fetchAUT", e.toString());
                    ret = null;
                }

                if (aViews.containsKey(photo)) {
                    aViews.get(photo).setText((ret == null) ? "" : ret.getUsername());
                }
                authors.put(photo, ret);


            }
        });
    }

    // Update the status of the current feed
    private void updateViewLike(PhotoObj photo, int status, boolean fav) {

        photo.incViews();
        if (status == LIKE) {
            photo.incShares();
            Log.d("SwipeUpdate", "Green");
        } else
            Log.d("SwipeUpdate", "Red");

        FeedObj feed = new FeedObj(photo, ParseUser.getCurrentUser(), status, fav);
        feed.saveEventually();
        photo.saveEventually();
    }

    // Fetch the comments of the current feed from DB
    private void fetchCurComment() {
        ParseQuery<CommentObj> query = ParseQuery.getQuery("CommentObj");
        query.whereEqualTo(CommentObj.PHOTO, getCur());
        query.whereEqualTo(CommentObj.IS_REPLY, false);
        query.findInBackground(new FindCallback<CommentObj>() {
            @Override
            public void done(List<CommentObj> commentObjs, ParseException e) {
                if (e == null) {
                    if (commentObjs.size() > 0 &&
                        commentObjs.get(0).getPhoto().getObjectId() == getCur().getObjectId()) {

                        synchronized (comments) {
                            comments.addAll(commentObjs);
                        }

                    }
                }
            }
        });
    }

    // Fetch the replies of a comment from DB
    private void fetchReply(final CommentObj comment) {
        ParseQuery<CommentObj> query = ParseQuery.getQuery("CommentObj");
        query.whereEqualTo(CommentObj.PARENT, comment);
        query.findInBackground(new FindCallback<CommentObj>() {
            @Override
            public void done(List<CommentObj> commentObjs, ParseException e) {
                if (e == null) {
                    if (replies.containsKey(comment))
                        replies.get(comment).addAll(commentObjs);
                    else
                        replies.put(comment, commentObjs);
                }
            }
        });
    }

    // Get the current photo
    public PhotoObj getCur() {
        synchronized (photos) {
            if (photos.size() > 0)
                return photos.get(0);
            else
                return null;
        }
    }

    public String getCurUserID() {
        return authors.get(getCur()).getObjectId();
    }

    // Get the next photo
    public PhotoObj getNext() {
        synchronized (photos) {
            if (photos.size() > 1)
                return photos.get(1);
            else
                return null;
        }
    }

    // Update and delete the first photo in the array.
    public void moveNext(int status, boolean fav) {
        PhotoObj photo;
        synchronized (photos) {
            try {
                photo = photos.remove(0);
            } catch (IndexOutOfBoundsException e) {
                photo = null;
            }
        }

        if (photo != null) {
            if (isReady(photo))
                updateViewLike(photo, status, fav);
            synchronized (comments) {
                comments.clear();
            }
            replies = new ConcurrentHashMap<>();
            imgs.remove(photo);
            authors.remove(photo);
            iViews.remove(photo);
            aViews.remove(photo);
        }

        synchronized (photos) {
            if (photos.size() <= num_feed / 2)
                fill();
        }
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

    /* Set the author textView of the current photo. If not fetched yet, store the textView for
       later.
    */
    public void setAuthorView(PhotoObj photo, TextView view) {
        if (authors.containsKey(photo)) {
            ParseUser author = authors.get(photo);
            view.setText((author == null) ? "" : author.getUsername());
        } else {
            view.setText("");
            aViews.put(photo, view);
        }
    }

    // Check for the photo object whether we have retrieved the actual image yet.
    public boolean isReady(PhotoObj photo) {
        return imgs.containsKey(photo) && imgs.get(photo) != null;
    }

    // Create a comment for current photo, assuming commentReady!
    public void createCurComment(String comment) {

        PhotoObj photo = getCur();
        CommentObj commentObj = new CommentObj(photo, ParseUser.getCurrentUser(),
            comment, false, null);
        commentObj.saveEventually();
        synchronized (comments) {
            comments.add(commentObj);
        }
    }

    // Create a reply to a comment, assuming the replies have been loaded {
    public void CreateReply(String comment, CommentObj parent) {
        PhotoObj photo = getCur();
        CommentObj commentObj = new CommentObj(photo, ParseUser.getCurrentUser(),
                comment, true, parent);
        commentObj.saveEventually();

        if (!replies.containsKey(parent)) {
            replies.put(parent, new ArrayList<CommentObj>());
        }
        replies.get(parent).add(commentObj);

        if (!parent.getHasReply()) {
            parent.setHasReply(true);
            parent.saveEventually();
        }
    }


}
