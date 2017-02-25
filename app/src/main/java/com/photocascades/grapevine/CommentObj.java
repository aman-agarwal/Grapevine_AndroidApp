package com.photocascades.grapevine;

import com.parse.ParseClassName;
import com.parse.ParseObject;
import com.parse.ParseUser;

/**
 * Created by sdhpzhtk on 5/12/15.
 */
@ParseClassName("CommentObj")
public class CommentObj extends ParseObject {
    /*
    The object class for Feed:

    photo: PhotoObj, the photo of the comment.
    author: ParseUser, the author of this comment
    comment: String, the actual comment content
    hasReply: boolean, whether this comment has reply
    isReply: boolean, whether this comment is a reply
    parent: CommentObj, the parent comment if isReply, and null otherwise.
    numLikes: Int, number of likes to this comment

    The followings are the ParseObject default fields.
    objectID: string
    createdAt: string
    updatedAt: string
     */

    final static String PHOTO = "photo";
    final static String AUTHOR = "author";
    final static String COMMENT = "comment";
    final static String HAS_REPLY = "hasReply";
    final static String IS_REPLY = "isReply";
    final static String PARENT = "parent";
    final static String NUMLIKES = "numLikes";


    public CommentObj() {
        // A default constructor is required.
    }

    public CommentObj(PhotoObj photo, ParseUser author, String comment, Boolean isReply,
                      CommentObj parent) {
        setPhoto(photo);
        setAuthor(author);
        setComment(comment);
        setHasReply(false);
        setIsReply(isReply);
        setParent(parent);
        setNumLikes(0);
    }

    // Setters and Getters.
    public PhotoObj getPhoto() { return (PhotoObj) getParseObject(PHOTO); }

    public ParseUser getAuthor() {
        return getParseUser(AUTHOR);
    }

    public String getComment() { return getString(COMMENT); }

    public Boolean getHasReply() { return getBoolean(HAS_REPLY); }

    public Boolean getIsReply() { return getBoolean(IS_REPLY); }

    public CommentObj getParent() { return (CommentObj) get(PARENT); }

    public int getNumLikes() { return getInt(NUMLIKES); }

    public void setPhoto(PhotoObj photo) {
        put(PHOTO, photo);
    }

    public void setAuthor(ParseUser author) { put(AUTHOR, author); }

    public void setComment(String comment) { put(COMMENT, comment); }

    public void setHasReply(boolean hasReply) { put(HAS_REPLY, hasReply); }

    public void setIsReply(boolean isReply) { put(IS_REPLY, isReply); }

    public void setParent(CommentObj parent) { if (parent != null) put(PARENT, parent); }

    public void setNumLikes(int n) { put(NUMLIKES, n); }

    // Atomic increment for views
    public void incNumLikes() {
        increment(NUMLIKES);
    }

    public void incNumLikes(int amount) {
        increment(NUMLIKES, amount);
    }
}

