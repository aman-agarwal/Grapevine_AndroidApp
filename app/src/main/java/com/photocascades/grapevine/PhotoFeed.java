package com.photocascades.grapevine;

import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.List;


public class PhotoFeed extends ActionBarActivity {
    /**
     * Number of pages maintained in the ViewPager. The current photo occupies index 1, while the
     * next photo occupies indices 0 and 2.
     */
    public static final int NUM_PAGES = 10;

    /**
     * Index of the left page in the ViewPager.
     */
    public static final int LEFT_PAGE = 0;

    /**
     * Index of the middle page in the ViewPager.
     */
    public static final int MIDDLE_PAGE = 1;

    /**
     * Index of the right page in the ViewPager.
     */
    public static final int RIGHT_PAGE = 2;

    /**
     * The pager widget that handles swiping and displaying the photo feed.
     */
    public ViewPager mPager;

    /**
     * The pager adapter that provides the pages to the ViewPager widget.
     */
    private PhotoFeedAdapter mPagerAdapter;

    private FeedManager mFeedManager = new FeedManager();

    // For the current feed, whether the user has starred this feed
    private boolean fav = false;

    // For the current feed, the direction of the user swiping
    private int swipeDir = FeedManager.UNSET;

    // Comment fragment displaying comments to a particular photo.
    private CommentFragment mCommentFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_photo_feed);

        ParseQuery<PhotoObj> query = ParseQuery.getQuery("PhotoObj");
        query.findInBackground(new FindCallback<PhotoObj>() {
            @Override
            public void done(List<PhotoObj> photoList, ParseException e) {
                if (e == null) {
                    Log.d("RetrieveObj", Integer.toString(photoList.size()));
                    // Instantiate a ViewPager and a PhotoFeedAdapter, then connect them.
                    mPager = (ViewPager) findViewById(R.id.photo_feed);

                    mPagerAdapter = new PhotoFeedAdapter(getSupportFragmentManager());
                    mPager.setPageTransformer(true, new DepthPageTransformer());
                    mPager.setAdapter(mPagerAdapter);
                    mPager.setOnPageChangeListener(new OnPageChangeListener());

                    // Initialize ViewPager to the middle element.
                    mPager.setCurrentItem(MIDDLE_PAGE, false);
                } else {
                    Log.e("RetrieveObj", e.toString());
                }
            }
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_photo_feed, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        } else if (id == R.id.action_logout) {
            ParseUser.logOut();

            // FLAG_ACTIVITY_CLEAR_TASK only works on API 11, so if the user
            // logs out on older devices, we'll just exit.
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
                Intent intent = new Intent(PhotoFeed.this,
                        LoginDispatchActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK
                        | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            } else {
                finish();
            }
        } else if (id == R.id.camera) {
            Intent photoIntent = new Intent(this, CameraActivity.class);
            startActivity(photoIntent);
            return false;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * Handle back button to return from comments fragment to ViewPager.
     */
    @Override
    public void onBackPressed()
    {
        if (getFragmentManager().getBackStackEntryCount() > 0) {
            getFragmentManager().popBackStack();
        } else {
            super.onBackPressed();
        }
    }

    /**
     * Click handler for "heart" button to favorite image.
     */
    public void favPhoto(View view) {
        ImageButton btn = (ImageButton) findViewById(R.id.fav_button);

        if (fav == false) {
            btn.setImageResource(R.drawable.ic_fav_select);
            fav = true;
        }
        else {
            btn.setImageResource(R.drawable.ic_fav);
            fav = false;
        }
    }

    /**
     * Click handler to read comments.
     * @param view
     */
    public void readComments(View view) {
        //TODO:Xander
        //if (mFeedManager.getCur().hasComments())

        // Set up comment fragment for current photo.
        mCommentFragment = new CommentFragment();
        Bundle bundle = new Bundle();
        bundle.putString("photoId", mFeedManager.getCur().getObjectId());
        mCommentFragment.setArguments(bundle);

        // Set up fragment transaction and add to back stack.
        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        transaction.add(R.id.fragment_container, mCommentFragment);
        transaction.addToBackStack(null);

        // Commit transaction.
        transaction.commit();
    }

    /**
     * Click handler for "like" button to share page.
     * @param view
     */
    public void likePage(View view) {
        mPager.setCurrentItem(LEFT_PAGE, true);
    }

    /**
     * Click handler for "dislike" button to contain page.
     * @param view
     */
    public void dislikePage(View view) {
        mPager.setCurrentItem(RIGHT_PAGE, true);
    }

    /**
     * Click handler to upload user comment.
     * @param view
     */
    public void sendComment(View view) {
        EditText user_comment = (EditText) findViewById(R.id.user_comment);
        mFeedManager.createCurComment(user_comment.getText().toString());

        // Reset comment field and close keyboard.
        user_comment.setText("");
        InputMethodManager imm = (InputMethodManager) getSystemService(
                Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(user_comment.getWindowToken(), 0);

        // Update list of comments.
        mCommentFragment.update();
    }

    /**
     * Click listener to like a comment.
     * @param v
     */
    public void likeComment(View v) {
        mCommentFragment.likeComment(v);
    }

    /**
     * An adapter class to supply pages to the photo feed ViewPager.
     *
     * We maintain the sense of a vertical stack of photos, rather than a horizontal slideshow, by
     * instantiating the ViewPager in the middle of a symmetric set of photos. After swiping in
     * either direction, we mutate the pages in the ViewPager to again make the current photo be
     * the middle of a symmetric set of photos. This will create the illusion that swiping left or
     * right always proceeds to the “next” photo, even though it actually takes you to the next
     * and previous photo in the list, respectively.
     */
    private class PhotoFeedAdapter extends FragmentStatePagerAdapter {

        public PhotoFeedAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            Bundle bundle = new Bundle();
            bundle.putInt("position", position);

            PhotoFeedFragment photoFrag = new PhotoFeedFragment();
            photoFrag.setArguments(bundle);
            photoFrag.setPhoto((position == MIDDLE_PAGE) ? mFeedManager.getCur() : mFeedManager.getNext(), mFeedManager);
            return photoFrag;
        }

        /**
         * Update current and next photos.
         */
        private void updatePages() {
            mFeedManager.moveNext(swipeDir, fav);
            swipeDir = FeedManager.UNSET;
            fav = false;
        }

        /**
         * Tell the ViewPager to remove and reload all the pages.
         */
        @Override
        public int getItemPosition(Object object) {
            return POSITION_NONE;
        }

        @Override
        public int getCount() {
            return NUM_PAGES;
        }
    }

    /**
     * A listener that manipulates the ViewPager upon a page change. Simulates the functionality of
     * an infinitely scrollable deck of photos.
     */
    private class OnPageChangeListener implements ViewPager.OnPageChangeListener {

        // Specifies whether ViewPager needs to reload and reset to middle element.
        private Boolean refresh = false;

        /**
         * Detect when the user has swiped on to the next photo.
         * @param position
         */
        @Override
        public void onPageSelected(int position) {
            if (position != MIDDLE_PAGE) {
                if (position < MIDDLE_PAGE) {
                    swipeDir = FeedManager.LIKE;
                    Log.d("SWIPED", "Green");
                } else {
                    swipeDir = FeedManager.UNLIKE;
                    Log.d("SWIPED", "Red");
                }
                refresh = true;
            }
        }

        /**
         * Reload ViewPager and set it to the MIDDLE_PAGE when the user swipes.
         * @param state
         */
        @Override
        public void onPageScrollStateChanged(int state) {
            if (state == ViewPager.SCROLL_STATE_IDLE && refresh) {
                mPagerAdapter.updatePages();
                mPagerAdapter.notifyDataSetChanged();
                mPager.setCurrentItem(MIDDLE_PAGE, false);

                refresh = false;
            }
        }

        /**
         * Update ViewPager background color to be green or red based on whether the user is sharing
         * the photo (swiping right) or not (swiping left).
         */
        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            // User is swiping left to not share photo.
            if (position == MIDDLE_PAGE) {
                mPager.setBackgroundColor(getResources().getColor(R.color.red));
            }
            // User is swiping right to share photo.
            else {
                mPager.setBackgroundColor(getResources().getColor(R.color.green));
            }
        }
    }
}
