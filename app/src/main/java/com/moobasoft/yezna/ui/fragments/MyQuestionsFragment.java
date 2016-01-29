package com.moobasoft.yezna.ui.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v4.widget.SwipeRefreshLayout.OnRefreshListener;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.moobasoft.yezna.R;
import com.moobasoft.yezna.events.ask.QuestionCreatedEvent;
import com.moobasoft.yezna.events.auth.LogOutEvent;
import com.moobasoft.yezna.events.auth.LoginEvent;
import com.moobasoft.yezna.rest.models.Question;
import com.moobasoft.yezna.ui.MyQuestionsAdapter;
import com.moobasoft.yezna.ui.StaggeredScrollListener;
import com.moobasoft.yezna.ui.activities.MainActivity;
import com.moobasoft.yezna.ui.fragments.base.RxFragment;
import com.moobasoft.yezna.ui.presenters.MyQuestionsPresenter;

import java.util.List;

import javax.inject.Inject;

import butterknife.Bind;
import butterknife.ButterKnife;
import rx.Subscription;
import rx.subscriptions.CompositeSubscription;

import static android.view.View.VISIBLE;

public class MyQuestionsFragment extends RxFragment implements MyQuestionsPresenter.View, OnRefreshListener {

    public static final String PAGE_KEY = "page_key";
    public static final String QUESTIONS_KEY = "summaries_key";
    public static final String SCROLL_KEY = "scroll_key";

    /**
     * RecyclerView.Adapter implementation for {@code recyclerView}
     */
    private MyQuestionsAdapter questionAdapter;

    /**
     * OnScrollListener for {@code recyclerView}
     */
    private StaggeredScrollListener scrollListener;

    /**
     * The current page of post data. Used as a query param.
     */
    private int currentPage = 1;

    @Inject MyQuestionsPresenter presenter;

    @Bind(R.id.recycler_view) RecyclerView recyclerView;
    @Bind(R.id.swipe_refresh) SwipeRefreshLayout refreshLayout;

    public MyQuestionsFragment() {
    }

    @Override public void onCreate(@Nullable Bundle state) {
        super.onCreate(state);
        questionAdapter = new MyQuestionsAdapter((MainActivity) getActivity());
        scrollListener = new StaggeredScrollListener() {
            @Override public void onLoadMore() {
                loadQuestions(false);
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
            List<Question> questions = state.getParcelableArrayList(QUESTIONS_KEY);
            scrollListener.restoreState(state.getParcelable(SCROLL_KEY));
            if (scrollListener.isFinished())
                questionAdapter.setFinished();
            questionAdapter.loadQuestions(questions);
        }
    }

    @Override public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle state) {
        View view = inflater.inflate(R.layout.fragment_my_questions, container, false);
        ButterKnife.bind(this, view);
        initialiseRecyclerView();
        return view;
    }

    @Override public void onActivityCreated(@Nullable Bundle state) {
        super.onActivityCreated(state);
        getComponent().inject(this);
        presenter.bindView(this);

        if (state == null && questionAdapter.isEmpty()) {
            loadQuestions(false);
        } else {
            if (questionAdapter.getQuestionsList().isEmpty())
                activateEmptyView(getString(R.string.no_my_questions));
            else
                activateContentView();
        }
    }

    @Override public void onSaveInstanceState(Bundle state) {
        super.onSaveInstanceState(state);
        state.putInt(PAGE_KEY, currentPage);
        state.putParcelableArrayList(QUESTIONS_KEY, questionAdapter.getQuestionsList());
        state.putParcelable(SCROLL_KEY, scrollListener.getOutState());
    }

    @Override public void onDestroyView() {
        presenter.releaseView();
        ButterKnife.unbind(this);
        super.onDestroyView();
    }

    @Override protected void subscribeToEvents() {
        Subscription loginEvent =
                eventBus.listenFor(LoginEvent.class)
                        .subscribe(event -> loadQuestions(true));

        Subscription logOutEvent =
                eventBus.listenFor(LogOutEvent.class)
                        .subscribe(event -> {
                            activateEmptyView(getString(R.string.unauthorized_my_questions));
                            reset();
                        });

        Subscription createdEvent = eventBus
                .listenFor(QuestionCreatedEvent.class)
                .map(QuestionCreatedEvent::getQuestion)
                .subscribe(questionAdapter::loadQuestion);

        eventSubscriptions = new CompositeSubscription(
                loginEvent, logOutEvent, createdEvent);
    }

    private void initialiseRecyclerView() {
        int columns = getResources().getInteger(R.integer.main_list_columns);
        StaggeredGridLayoutManager layoutManager = new StaggeredGridLayoutManager(
                columns, StaggeredGridLayoutManager.VERTICAL);

        recyclerView.setLayoutManager(layoutManager);
        recyclerView.addOnScrollListener(scrollListener);
        //recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(questionAdapter);
        scrollListener.setLayoutManager(layoutManager);

        refreshLayout.setOnRefreshListener(this);
        refreshLayout.setColorSchemeResources(
                R.color.colorPrimary, R.color.colorPrimaryDark);
    }

    private void loadQuestions(boolean refresh) {
        showLoadingIndicator();
        presenter.loadQuestions(refresh, currentPage);
    }

    @Override public void onQuestionsRetrieved(List<Question> questions) {
        refreshLayout.setRefreshing(false);
        currentPage++;

        if (questionAdapter.isEmpty() && questions.isEmpty())
            activateEmptyView(getString(R.string.no_my_questions));
        else if (!questionAdapter.isEmpty() && questions.isEmpty()) {
            scrollListener.setFinished();
            questionAdapter.setFinished();
        } else {
            activateContentView();
            questionAdapter.loadQuestions(questions);
        }
    }

    @Override public void onError(int messageId) {
        super.onError(messageId);
        refreshLayout.setRefreshing(false);
    }

    @Override public void promptForLogin() {
        super.promptForLogin();
        activateErrorView(getString(R.string.unauthorized_my_questions));
    }

    @Override public void onRefresh() {
        reset();
        loadQuestions(true);
    }

    private void setRefreshLayoutEnabled() {
        if (refreshLayout == null || recyclerView == null) return;
        boolean canRefresh = !recyclerView.canScrollVertically(-1);
        refreshLayout.setEnabled(canRefresh);
    }

    private void showLoadingIndicator() {
        if (currentPage == 1 || errorView.getVisibility() == VISIBLE || emptyView.getVisibility() == VISIBLE)
            activateLoadingView();
        else
            refreshLayout.setRefreshing(true);
    }

    private void reset() {
        scrollListener.reset();
        questionAdapter.clear();
        currentPage = 1;
    }

}