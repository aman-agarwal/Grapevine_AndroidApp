package com.photocascades.grapevine;

import android.app.Application;

import com.parse.Parse;
import com.parse.ParseFacebookUtils;
import com.parse.ParseObject;

/**
 * Created by Aditya on 4/15/2015.
 */
public class GrapevineApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        ParseObject.registerSubclass(PhotoObj.class);
        ParseObject.registerSubclass(FeedObj.class);
        ParseObject.registerSubclass(CommentObj.class);
        // Enable Local Datastore.
        Parse.enableLocalDatastore(this);

        Parse.initialize(this);

        Parse.setLogLevel(Parse.LOG_LEVEL_DEBUG);
        ParseFacebookUtils.initialize(this);
    }
}
