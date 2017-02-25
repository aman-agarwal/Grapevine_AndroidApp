package com.photocascades.grapevine;

import android.support.v4.view.ViewPager;
import android.view.View;

/**
 * Animate photo feed as a stack of photos, rather than a horizontal slideshow, by providing a sense
 * of depth as a new photo emerges after swiping off the current photo.
 */
public class DepthPageTransformer implements ViewPager.PageTransformer {
    private static final float MIN_SCALE = 0.75f;

    public void transformPage(View view, float position) {
        int pageWidth = view.getWidth();

        if (position < -1) { // [-Infinity,-1)
            // This page is way off-screen to the left.
            view.setAlpha(0);

        } else if (position <= 1) { // [-1 ,1]
            if (view.getTag() == PhotoFeed.MIDDLE_PAGE) {
                // Use default transition
                view.setAlpha(1);
                view.setTranslationX(0);
                view.setScaleX(1);
                view.setScaleY(1);
            }
            else {
                // Fade the page.
                view.setAlpha(1 - Math.abs(position));

                // Counteract the default slide transition
                view.setTranslationX(pageWidth * -position);

                // Scale the page down (between MIN_SCALE and 1)
                float scaleFactor = MIN_SCALE
                        + (1 - MIN_SCALE) * (1 - Math.abs(position));
                view.setScaleX(scaleFactor);
                view.setScaleY(scaleFactor);
            }

        } else { // (1,+Infinity]
            // This page is way off-screen to the right.
            view.setAlpha(0);
        }
    }
}