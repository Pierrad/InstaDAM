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
    private LinearLayoutManager layoutManager;

    private static final int PAGE_SIZE = 10;

    public PaginationListener(@NonNull LinearLayoutManager layoutManager) {
        this.layoutManager = layoutManager;
    }

    /**
     * Called when the RecyclerView has been scrolled. This method checks if the user has scrolled to the bottom of the list and loads more posts if needed.
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
        if (!isLoading() && !isLastPage()) {
            if ((visibleItemCount + firstVisibleItemPosition) >= totalItemCount
                    && firstVisibleItemPosition >= 0
                    && totalItemCount >= PAGE_SIZE) {
                loadMoreItems();
            }
        }
    }
    protected abstract void loadMoreItems();
    public abstract boolean isLastPage();
    public abstract boolean isLoading();
}
