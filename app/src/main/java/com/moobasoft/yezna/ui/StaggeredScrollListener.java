package com.moobasoft.yezna.ui;

import android.os.Parcelable;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;

/**
 * Adapted from this gist: https://gist.github.com/ssinss/e06f12ef66c51252563e
 */
public abstract class StaggeredScrollListener extends RecyclerView.OnScrollListener {

    private StaggeredGridLayoutManager layoutManager;

    /**
     * The minimum amount of items to have below your current scroll position before loading more.
     */
    private static final int VISIBLE_THRESHOLD = 1;

    /**
     * The total number of items in the dataset after the last load
     */
    private int previousTotal = 0;

    /**
     * Should be true when no more data is left to load
     */
    private boolean isFinished = false;

    public boolean isFinished() { return isFinished; }

    /**
     * Needs to be set manually because of fragment lifecycle issues
     */
    public void setLayoutManager(StaggeredGridLayoutManager layoutManager) {
        this.layoutManager = layoutManager;
    }

    @Override
    public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
        if (layoutManager == null || isRefreshing() || isFinished || dy == 0)
            return;

        super.onScrolled(recyclerView, dx, dy);

        int visibleItemCount = recyclerView.getChildCount();
        int totalItemCount   = layoutManager.getItemCount();
        int firstVisibleItem = findFirstVisibleItem();

        if (firstVisibleItem < 0) return;

        if (totalItemCount > previousTotal)
            previousTotal = totalItemCount;

        boolean endReached = (totalItemCount - visibleItemCount)
                <= (firstVisibleItem + VISIBLE_THRESHOLD);

        if (endReached) {
            onLoadMore();
        }
    }

    private int findFirstVisibleItem() {
        int[] firstCompletelyVisibleItemPositions = layoutManager
                .findFirstCompletelyVisibleItemPositions(null);

        if (firstCompletelyVisibleItemPositions.length > 0)
            return firstCompletelyVisibleItemPositions[0];
        else
            return -1;
    }

    /**
     * Reset to first page. Use in onRefresh()
     */
    public void reset() {
        previousTotal = 0;
        isFinished = false;
    }

    /**
     * Set flag to show that no more data should be loaded
     */
    public void setFinished() {
        isFinished = true;
    }

    /**
     * Restore state from a previous instance.
     * Use in onRestoreInstanceState()
     */
    public void restoreState(ScrollOutState state) {
        this.previousTotal = state.previousTotal;
        this.isFinished    = state.isFinished;
    }

    /**
     * The necessary state to restore this instance.
     * Use in onSaveInstanceState()
     */
    public ScrollOutState getOutState() {
        return new ScrollOutState(previousTotal, isFinished);
    }

    /**
     * Allow client to define behaviour when next page should be loaded.
     */
    public abstract void onLoadMore();

    /**
     * Is previous request still loading?
     */
    public abstract boolean isRefreshing();

    public static class ScrollOutState implements Parcelable {
        public int previousTotal;
        public boolean isFinished;

        public ScrollOutState() {}

        public ScrollOutState(int previousTotal, boolean isFinished) {
            this.previousTotal = previousTotal;
            this.isFinished = isFinished;
        }

        /**
         *  Code to implement Parcelable
         */

        public ScrollOutState(android.os.Parcel source) {
            previousTotal = source.readInt();
            isFinished    = (source.readByte() == 1);
        }

        @Override
        public int describeContents() { return previousTotal; }

        @Override
        public void writeToParcel(android.os.Parcel dest, int flags) {
            dest.writeInt(previousTotal);
            dest.writeByte((byte) (this.isFinished ? 1 : 0));
        }

        public static final Creator<ScrollOutState> CREATOR = new Creator<ScrollOutState>() {

            public ScrollOutState createFromParcel(android.os.Parcel in) {
                return new ScrollOutState(in);
            }

            public ScrollOutState[] newArray(int size) {
                return new ScrollOutState[size];
            }
        };
    }
}