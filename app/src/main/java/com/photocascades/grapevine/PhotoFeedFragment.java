package com.photocascades.grapevine;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

/**
 * Created by Aditya on 4/22/2015.
 */
public class PhotoFeedFragment extends Fragment {

    private PhotoObj mPhoto = null;
    private TextView mAuthor, mDesc, mShares;
    private ImageView mImg;
    private ProgressBar mStatsBar;
    FeedManager mFeedManager;

    public PhotoFeedFragment() {}

    public void setPhoto(PhotoObj feedObj, FeedManager feedMng) {
        mPhoto = feedObj;
        mFeedManager = feedMng;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        View rootView = inflater.inflate(R.layout.fragment_photo_feed, container, false);

        // Set the tag of the view to be its position in the ViewPager.
        Bundle args = getArguments();
        int mPosition = args.getInt("position");
        rootView.setTag(mPosition);

        if (mPhoto != null) {
            mAuthor = (TextView) rootView.findViewById(R.id.photo_uploader);
            mDesc = (TextView) rootView.findViewById(R.id.photo_desc);
            mShares = (TextView) rootView.findViewById(R.id.photo_shares);
            mImg = (ImageView) rootView.findViewById(R.id.photo);
            mStatsBar = (ProgressBar) rootView.findViewById(R.id.stats_bar);

            mFeedManager.setAuthorView(mPhoto, mAuthor);

            mDesc.setText(mPhoto.getDescription());

            //TODO:Xander
            /*if (mPhoto.hasComments()) {
                mCommentButton.setImageResource(R.drawable.ic_comments_present);
            }*/

            mShares.setText(mPhoto.getShares() + " shares");

            mStatsBar.setProgress(100 * mPhoto.getShares() / mPhoto.getViews());

            mFeedManager.setImageView(mPhoto, mImg);
        }

        return rootView;
    }
}
