package com.photocascades.grapevine;

import com.parse.ui.ParseLoginDispatchActivity;

/**
 * Created by sdhpzhtk on 4/22/15.
 */
public class LoginDispatchActivity extends ParseLoginDispatchActivity{

    @Override
    protected Class<?> getTargetClass() {
        return PhotoFeed.class;
    }
}
