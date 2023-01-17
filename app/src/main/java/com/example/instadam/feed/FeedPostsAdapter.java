package com.example.instadam.feed;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.instadam.R;
import com.example.instadam.components.Post;

import java.util.ArrayList;
import java.util.List;

public class FeedPostsAdapter extends RecyclerView.Adapter<FeedPostsAdapter.ViewHolder> {
    private ArrayList<Post> feedPosts;
    private Context context;

    public FeedPostsAdapter(ArrayList<Post> feedPosts, Context context) {
        this.feedPosts = feedPosts;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.feed_post_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Post feedPost = feedPosts.get(position);

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

    public void addItems(List<Post> posts) {
        feedPosts.addAll(posts);
        notifyDataSetChanged();
    }


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
