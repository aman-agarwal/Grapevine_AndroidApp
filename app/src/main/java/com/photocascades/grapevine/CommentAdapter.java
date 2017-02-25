package com.photocascades.grapevine;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseQueryAdapter;
import com.parse.ParseUser;

/**
 * Created by sdhpzhtk on 5/18/15.
 */
public class CommentAdapter extends ParseQueryAdapter<CommentObj> {

    public CommentAdapter(Context context, final String photoID) {
        super(context, new ParseQueryAdapter.QueryFactory<CommentObj>() {
            public ParseQuery create() {
                ParseQuery query = new ParseQuery("CommentObj");
                query.whereEqualTo(CommentObj.IS_REPLY, false);
                query.whereEqualTo(CommentObj.PHOTO,
                                   ParseObject.createWithoutData("PhotoObj", photoID));
                return query;
            }
        });
    }

    @Override
    public View getItemView(CommentObj object, View v, ViewGroup parent) {
        if (v == null) {
            v = View.inflate(getContext(), R.layout.list_item_comment, null);
        }

        super.getItemView(object, v, parent);
        final TextView user = (TextView) v.findViewById(R.id.username);
        TextView time = (TextView) v.findViewById(R.id.timestamp);
        TextView numLikes = (TextView) v.findViewById(R.id.comment_num_likes);
        TextView numReplies = (TextView) v.findViewById(R.id.comment_num_replies);
        TextView comment = (TextView) v.findViewById(R.id.comment);

        time.setText(object.getCreatedAt().toString());
        numLikes.setText(Integer.toString(object.getNumLikes()));
        numReplies.setText("0");
        comment.setText(object.getComment());
        object.getAuthor().fetchInBackground(new GetCallback<ParseUser>() {
            @Override
            public void done(ParseUser obj, ParseException e) {
                if (e == null) {
                    user.setText(obj.getUsername());
                }
            }
        });
        return v;
    }
}
