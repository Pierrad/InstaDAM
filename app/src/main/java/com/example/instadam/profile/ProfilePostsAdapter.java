package com.example.instadam.profile;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.instadam.R;
import com.example.instadam.components.Post;

import java.util.List;

public class ProfilePostsAdapter extends BaseAdapter {

    private List<Post> listData;
    private LayoutInflater layoutInflater;
    private Context context;

    public ProfilePostsAdapter(Context aContext,  List<Post> listData) {
        this.context = aContext;
        this.listData = listData;
        layoutInflater = LayoutInflater.from(aContext);
    }

    @Override
    public int getCount() {
        return listData.size();
    }

    @Override
    public Object getItem(int position) {
        return listData.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;

        if (convertView == null) {
            convertView = layoutInflater.inflate(R.layout.grid_post_layout, null);
            holder = new ViewHolder();
            holder.flagView = convertView.findViewById(R.id.imageView_flag);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        Post post = this.listData.get(position);

        holder.flagView.setImageBitmap(post.getImage());

        return convertView;
    }

    static class ViewHolder {
        ImageView flagView;
    }
}
