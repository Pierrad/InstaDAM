package com.example.instadam.feed;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

/**
 * The FeedPostsScrollListener is used to load more posts when the user scrolls to the bottom of the RecyclerView.
 * It extends the RecyclerView.OnScrollListener class and implements the pagination logic.
 */
public abstract class PaginationListener extends RecyclerView.OnScrollListener {
    @NonNull
    private final LinearLayoutManager layoutManager;

    public PaginationListener(@NonNull LinearLayoutManager layoutManager) {
        this.layoutManager = layoutManager;
    }

    /**
     * Called when the RecyclerView has been scrolled. This method checks if the user has scrolled 80% of the list and loads more posts if needed.
     * @param recyclerView The RecyclerView which has been scrolled.
     * @param dx The amount of horizontal scroll.
     * @param dy The amount of vertical scroll.
     */
    @Override
    public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
        super.onScrolled(recyclerView, dx, dy);
        int visibleItemCount = layoutManager.getChildCount();
        int totalItemCount = layoutManager.getItemCount();
        int firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition();
        double visiblePercentage = (double)(visibleItemCount + firstVisibleItemPosition) / totalItemCount;
        if (!isLoading() && !isLastPage()) {
            if (visiblePercentage >= 0.8) {
                loadMoreItems();
            }
        }
    }
    protected abstract void loadMoreItems();
    public abstract boolean isLastPage();
    public abstract boolean isLoading();
}
