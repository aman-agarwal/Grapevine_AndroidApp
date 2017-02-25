package com.photocascades.grapevine;

import com.parse.ParseClassName;
import com.parse.ParseObject;
import com.parse.ParseUser;

/**
 * Created by sdhpzhtk on 5/9/15.
 */
@ParseClassName("FeedObj")
public class FeedObj extends ParseObject {
    /*
    The object class for Feed:

    photo: PhotoObj, the photo of the feed.
    viewer: ParseUser, the view of this feed
    status: Int, LIKE, UNLIKE or something else.
    fav: Boolean, whether this photo is starred by the user

    The followings are the ParseObject default fields.
    objectID: string
    createdAt: string
    updatedAt: string
     */

    final static int LIKE = 1;
    final static int UNLIKE = -1;
    final static int UNSET = 0;
    final static String PHOTO = "photo";
    final static String VIEWER = "viewer";
    final static String STATUS = "status";
    final static String FAV = "fav";

    public FeedObj() {
        // A default constructor is required.
    }

    public FeedObj(PhotoObj photo, ParseUser viewer, int status, boolean fav) {
        setPhoto(photo);
        setViewer(viewer);
        setStatus(status);
        setFav(fav);
    }

    // Setters and Getters.
    public PhotoObj getPhoto() { return (PhotoObj) getParseObject(PHOTO); }

    public ParseUser getViewer() {
        return getParseUser(VIEWER);
    }

    public int getStatus() { return getInt(STATUS); }

    public Boolean getFav() { return getBoolean(FAV); }

    public void setPhoto(PhotoObj photo) {
        put(PHOTO, photo);
    }

    public void setViewer(ParseUser viewer) { put(VIEWER, viewer); }

    public void setStatus(int status) { put(STATUS, status); }

    public void setFav(boolean fav) { put(FAV, fav); }
}
