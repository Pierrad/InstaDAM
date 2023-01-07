package com.example.instadam.feed;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.instadam.R;

import java.util.ArrayList;

public class FeedPostsAdapter  extends ArrayAdapter<FeedPost> {

    public FeedPostsAdapter(Context context, ArrayList<FeedPost> posts) {
        super(context, 0, posts);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        FeedPost entry = getItem(position);

        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.feed_post_item, parent, false);
        }

        TextView name = convertView.findViewById(R.id.feed_post_name);
        TextView geolocation = convertView.findViewById(R.id.feed_post_geolocation);
        TextView author = convertView.findViewById(R.id.feed_post_author);
        TextView description = convertView.findViewById(R.id.feed_post_description);
        ImageView image = convertView.findViewById(R.id.feed_post_image);

        name.setText(entry.getName());
        geolocation.setText(entry.getGeolocation().toString());
        author.setText(entry.getAuthor());
        description.setText(entry.getDescription());
        image.setImageBitmap(entry.getImage());

        return convertView;
    }
}
