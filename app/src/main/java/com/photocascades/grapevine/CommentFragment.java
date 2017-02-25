package com.photocascades.grapevine;

import android.app.Fragment;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

/**
 * Display the comments associated with a particular photo.
 */
public class CommentFragment extends Fragment {

    private String mPhotoId;
    private ListView mListView;
    private EditText mComment;
    private ImageButton mSendButton;

    private CommentAdapter mCommentAdapter;

    public CommentFragment() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_comment, container, false);

        Bundle args = getArguments();
        mPhotoId = args.getString("photoId");

        // Set up ParseQueryAdapter.
        mCommentAdapter = new CommentAdapter(getActivity(), mPhotoId);

        // Attach CommentAdapter to ListView.
        mListView = (ListView) rootView.findViewById(R.id.comment_section);
        mListView.setAdapter(mCommentAdapter);

        // Set up user comment feature.
        mComment = (EditText) rootView.findViewById(R.id.user_comment);
        mSendButton = (ImageButton) rootView.findViewById(R.id.ic_send);
        mComment.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.length() > 0) {
                    mSendButton.setImageResource(R.drawable.ic_send_select);
                } else {
                    mSendButton.setImageResource(R.drawable.ic_send);
                }
            }
        });

        return rootView;
    }

    // Update comments after user uploads comments.
    public void update() {
        if (mCommentAdapter != null) {
            mCommentAdapter.notifyDataSetChanged();
        }
    }

    /**
     * Click listener to view likes to a comment.
     * @param v
     */
    public void viewLikes(View v) {}

    /**
     * Click listener to read replies to a comment.
     * @param v
     */

    public void readReplies(View v) {}
    /**
     * Click listener to like a comment.
     * @param v
     */

    public void likeComment(View v) {
        // Increment like counter before updating cloud.
        LinearLayout parent = (LinearLayout) v.getParent();
        TextView commentNumLikes = (TextView) parent.findViewById(R.id.comment_num_likes);
        int curNumLikes = Integer.parseInt(commentNumLikes.getText().toString());
        commentNumLikes.setText(Integer.toString(curNumLikes + 1));

        // Get position of comment.
        RelativeLayout parentRow = (RelativeLayout) parent.getParent();
        ListView listView = (ListView) parentRow.getParent();
        int index = listView.getPositionForView(parentRow);

        // Update cloud.
        CommentObj commentObj = mCommentAdapter.getItem(index);
        commentObj.incNumLikes();
    }

    /**
     * Click listener to reply to a comment.
     * @param v
     */
    public void replyToComment(View v) {}
}
