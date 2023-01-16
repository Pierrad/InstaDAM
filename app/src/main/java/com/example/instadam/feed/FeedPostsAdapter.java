package com.example.instadam.feed;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.instadam.R;

import java.util.ArrayList;
import java.util.List;

/**
 * The FeedPostsAdapter is used to show the posts in the FeedActivity.
 * It extends the RecyclerView.Adapter class and implements the ViewHolder pattern to fill the RecyclerView with the posts.
 */
public class FeedPostsAdapter  extends RecyclerView.Adapter<FeedPostsAdapter.ViewHolder> {
    private ArrayList<FeedPost> feedPosts;
    private Context context;

    public FeedPostsAdapter(ArrayList<FeedPost> feedPosts, Context context) {
        this.feedPosts = feedPosts;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.feed_post_item, parent, false);
        return new ViewHolder(view);
    }

    /**
     * Bind the data to the ViewHolder.
     * @param holder The ViewHolder to bind the data to.
     * @param position The position of the data in the ArrayList.
     */
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        FeedPost feedPost = feedPosts.get(position);

        holder.nameTV.setText(feedPost.getName());
        holder.geolocationTV.setText(feedPost.getGeolocation().getAddress());
        holder.authorTV.setText(feedPost.getAuthor());
        holder.descriptionTV.setText(feedPost.getDescription());
        holder.imageTV.setImageBitmap(feedPost.getImage());
    }

    @Override
    public int getItemCount() {
        return feedPosts.size();
    }

    public void addItems(List<FeedPost> posts) {
        feedPosts.addAll(posts);
        notifyDataSetChanged();
    }

    /**
     * Retrieve the reference of the views in the layout and store them in the ViewHolder.
     */
    public class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView nameTV, geolocationTV, authorTV, descriptionTV;
        private final ImageView imageTV;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            nameTV = itemView.findViewById(R.id.feed_post_name);
            geolocationTV = itemView.findViewById(R.id.feed_post_geolocation);
            authorTV = itemView.findViewById(R.id.feed_post_author);
            descriptionTV = itemView.findViewById(R.id.feed_post_description);
            imageTV = itemView.findViewById(R.id.feed_post_image);
        }
    }
}
