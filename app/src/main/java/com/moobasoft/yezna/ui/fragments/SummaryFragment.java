package com.moobasoft.yezna.ui.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.moobasoft.yezna.R;
import com.moobasoft.yezna.rest.models.Question;
import com.moobasoft.yezna.ui.StaggeredScrollListener;
import com.moobasoft.yezna.ui.SummaryAdapter;
import com.moobasoft.yezna.ui.activities.MainActivity;
import com.moobasoft.yezna.ui.fragments.base.RxFragment;
import com.moobasoft.yezna.ui.presenters.SummaryPresenter;

import java.util.List;

import javax.inject.Inject;

import butterknife.Bind;
import butterknife.ButterKnife;

import static android.view.View.VISIBLE;

public class SummaryFragment extends RxFragment implements SummaryPresenter.View,
        SwipeRefreshLayout.OnRefreshListener, AppBarLayout.OnOffsetChangedListener {

    public static final String PAGE_KEY      = "page_key";
    public static final String SUMMARIES_KEY = "summaries_key";
    public static final String SCROLL_KEY    = "scroll_key";

    /** RecyclerView.Adapter implementation for {@code postsRecyclerView} */
    private SummaryAdapter summaryAdapter;

    /** OnScrollListener for {@code postsRecyclerView} */
    private StaggeredScrollListener scrollListener;

    /** A reference to the current AppBarLayout. */
    private AppBarLayout appBarLayout;

    /**
     * Manually keep track of {@code appBarLayout}'s expanded/collapsed state.
     * Makes use of {@code AppBarLayout.OnOffsetChangedListener}.
     */
    private boolean appBarIsExpanded = true;

    /** The current page of post data. Used as a query param. */
    private int currentPage = 1;

    @Inject SummaryPresenter presenter;

    @Bind(R.id.post_recycler) RecyclerView postsRecyclerView;
    @Bind(R.id.swipe_refresh) SwipeRefreshLayout refreshLayout;

    public SummaryFragment() {}

    @Override
    public void onCreate(@Nullable Bundle state) {
        super.onCreate(state);
        summaryAdapter = new SummaryAdapter((MainActivity)getActivity());
        scrollListener = new StaggeredScrollListener() {
            @Override public void onLoadMore() {
                loadPosts(false);
            }
            @Override public boolean isRefreshing() {
                setRefreshLayoutEnabled();
                return refreshLayout.isRefreshing();
            }
        };
        restoreState(state);
    }

    private void restoreState(@Nullable Bundle state) {
        if (state != null) {
            currentPage = state.getInt(PAGE_KEY);
            List<Question> questions = state.getParcelableArrayList(SUMMARIES_KEY);
            scrollListener.restoreState(state.getParcelable(SCROLL_KEY));
            if (scrollListener.isFinished())
                summaryAdapter.setFinished();
            summaryAdapter.loadQuestions(questions);
        }
    }

    @Nullable @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_summary, container, false);
        appBarLayout = (AppBarLayout) getActivity().findViewById(R.id.app_bar);
        ButterKnife.bind(this, view);
        initialiseRecyclerView();
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle state) {
        super.onActivityCreated(state);

        getComponent().inject(this);
        presenter.bindView(this);

        if (state == null) { // Add first-init check
            loadPosts(false);
        } else {
            if (summaryAdapter.getSummaryList().isEmpty())
                activateEmptyView(getString(R.string.no_questions_found));
            else
                activateContentView();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        appBarLayout.addOnOffsetChangedListener(this);
    }

    @Override
    public void onStop() {
        super.onStop();
        appBarLayout.removeOnOffsetChangedListener(this);
    }

    @Override
    public void onSaveInstanceState(Bundle state) {
        super.onSaveInstanceState(state);
        state.putInt(PAGE_KEY, currentPage);
        state.putParcelableArrayList(SUMMARIES_KEY, summaryAdapter.getSummaryList());
        state.putParcelable(SCROLL_KEY, scrollListener.getOutState());
    }

    @Override
    public void onDestroyView() {
        presenter.releaseView();
        ButterKnife.unbind(this);
        super.onDestroyView();
    }

    private void initialiseRecyclerView() {
        int columns = getResources().getInteger(R.integer.main_list_columns);
        StaggeredGridLayoutManager layoutManager = new StaggeredGridLayoutManager(
                columns, StaggeredGridLayoutManager.VERTICAL);

        postsRecyclerView.setLayoutManager(layoutManager);
        postsRecyclerView.addOnScrollListener(scrollListener);
        postsRecyclerView.setAdapter(summaryAdapter);
        scrollListener.setLayoutManager(layoutManager);

        refreshLayout.setOnRefreshListener(this);
        refreshLayout.setColorSchemeResources(
                R.color.colorPrimary, R.color.colorPrimaryDark);
    }

    private void setRefreshLayoutEnabled() {
        if (refreshLayout == null || postsRecyclerView == null)
            return;
        boolean canRefresh = appBarIsExpanded && !postsRecyclerView.canScrollVertically(-1);
        refreshLayout.setEnabled(canRefresh);
    }

    private void loadPosts(boolean refresh) {
        showLoadingIndicator();
        presenter.loadSummaries(refresh, currentPage);
    }

    @Override
    public void onPostsRetrieved(List<Question> questions) {
        refreshLayout.setRefreshing(false);

        currentPage++;

        if (summaryAdapter.isEmpty() && questions.isEmpty())
            activateEmptyView(getString(R.string.no_questions_found));
        else if (!summaryAdapter.isEmpty() && questions.isEmpty()) {
            scrollListener.setFinished();
            summaryAdapter.setFinished();
        } else {
            activateContentView();
            summaryAdapter.loadQuestions(questions);
        }
    }

    private void showLoadingIndicator() {
        if (currentPage == 1 || errorView.getVisibility() == VISIBLE || emptyView.getVisibility() == VISIBLE)
            activateLoadingView();
        else
            refreshLayout.setRefreshing(true);
    }

    @Override
    public void onError(int messageId) {
        super.onError(messageId);
        refreshLayout.setRefreshing(false);
    }

    @Override
    public void promptForLogin() {
        super.promptForLogin();
        activateErrorView(getString(R.string.error_unauthorized));
    }

    @Override
    public void onRefresh() {
        scrollListener.reset();
        summaryAdapter.clear();
        currentPage = 1;
        loadPosts(true);
    }

    @Override
    public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
        appBarIsExpanded = verticalOffset == 0;
        setRefreshLayoutEnabled();
    }
}