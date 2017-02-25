package com.photocascades.grapevine;

import com.parse.ParseClassName;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseUser;

/**
 * Created by sdhpzhtk on 4/24/15.
 */

@ParseClassName("PhotoObj")
public class PhotoObj extends ParseObject {
    /*
    The object class for Photo:

    author: ParseUser, the author of the photo.
    img: ParseFile, the actual image file of the photo
    title: string, the title of the photo
    description: string, the description of the photo
    views: int, the number of views of this photo
    shares: int, the number of shares of this photo

    The followings are the ParseObject default fields.
    objectID: string
    createdAt: string
    updatedAt: string
     */

    public PhotoObj() {
        // A default constructor is required.
    }

    public PhotoObj(ParseUser author, ParseFile img, String title, String description) {
        setAuthor(author);
        setImg(img);
        setTitle(title);
        setDescription(description);
        setViews(0);
        setShares(0);
    }

    // Setters and Getters.
    public ParseUser getAuthor() {
        return getParseUser("author");
    }

    public ParseFile getImg() {
        return getParseFile("img");
    }

    public String getTitle() {
        return getString("title");
    }

    public String getDescription() {
        return getString("description");
    }

    public int getViews() {
        return getInt("views");
    }

    public int getShares() {
        return getInt("shares");
    }

    public void setAuthor(ParseUser user) {
        put("author", user);
    }

    public void setImg(ParseFile img) {
        put("img", img);
    }

    public void setTitle(String title) {
        put("title", title);
    }

    public void setDescription(String description) {
        put("description", description);
    }

    public void setViews(int views) {
        put("views", views);
    }

    public void setShares(int shares) {
        put("shares", shares);
    }

    // Atomic increment for views
    public void incViews() {
        increment("views");
    }

    public void incViews(int amount) {
        increment("views", amount);
    }

    // Atomic increment for shares
    public void incShares() {
        increment("shares");
    }

    public void incShares(int amount) {
        increment("shares", amount);
    }

}
