package com.moobasoft.yezna.ui.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.RotateAnimation;
import android.view.animation.TranslateAnimation;

import com.moobasoft.yezna.R;
import com.moobasoft.yezna.rest.models.Question;
import com.moobasoft.yezna.ui.fragments.base.RxFragment;
import com.moobasoft.yezna.ui.presenters.RandomPresenter;
import com.moobasoft.yezna.ui.views.QuestionView;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import butterknife.Bind;
import butterknife.ButterKnife;

import static android.view.View.VISIBLE;

public class RandomFragment extends RxFragment implements RandomPresenter.View, QuestionView.QuestionClickListener {

    /** Bundle keys for saving/restoring instance state. */
    public static final String PAGE_KEY      = "page_key";
    public static final String QUESTIONS_KEY = "summaries_key";

    /** The number of questions returned per page. */
    public static final int PER_PAGE = 10;

    /** The current page of question data. Used as a query param. */
    private int currentPage = 1;

    /** A data structure to store question instances. */
    private ArrayList<Question> questions;

    /** A container ViewGroup for the question card views. */
    @Bind(R.id.content) ViewGroup cardDeck;

    @Inject RandomPresenter presenter;

    public RandomFragment() {}

    @Override
    public void onCreate(@Nullable Bundle state) {
        super.onCreate(state);
        getComponent().inject(this);
        presenter.bindView(this);
        questions = new ArrayList<>();

        if (state != null) {
            currentPage = state.getInt(PAGE_KEY);
            questions = state.getParcelableArrayList(QUESTIONS_KEY);
        }
    }

    @Nullable @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle state) {
        View view = inflater.inflate(R.layout.fragment_question, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle state) {
        super.onActivityCreated(state);

        if (state == null) // Add first-init check
            loadPosts(false);
        else if (questions.isEmpty())
            activateEmptyView(getString(R.string.no_questions_found));
        else
            activateContentView();
    }

    @Override
    public void onSaveInstanceState(Bundle state) {
        super.onSaveInstanceState(state);
        state.putInt(PAGE_KEY, currentPage);
        state.putParcelableArrayList(QUESTIONS_KEY, questions);
    }

    @Override
    public void onDestroyView() {
        presenter.releaseView();
        ButterKnife.unbind(this);
        super.onDestroyView();
    }

    private void loadPosts(boolean refresh) {
        boolean errorViewVisible = errorView.getVisibility() == VISIBLE;
        boolean emptyViewVisible = emptyView.getVisibility() == VISIBLE;
        if (currentPage == 1 || errorViewVisible || emptyViewVisible)
            activateLoadingView();
        presenter.loadSummaries(refresh, currentPage);
    }

    @Override
    public void onPostsRetrieved(List<Question> questions) {
        if (questions.isEmpty())
            activateEmptyView(getString(R.string.no_questions_found));
        else {
            activateContentView();
            this.questions.addAll(questions);

            if (currentPage == 1) {
                if (questions.size() > 2)
                    pushCard(questions.get(2), 1F, -2F);
                if (questions.size() > 1)
                    pushCard(questions.get(1), 2F, -1F);
                if (questions.size() > 0)
                    pushCard(questions.get(0), 3F, 0F);
            }
        }
        currentPage++;
    }

    @Override
    public void promptForLogin() {
        super.promptForLogin();
        activateErrorView(getString(R.string.error_unauthorized));
    }

    @Override
    public void onRefresh() {
        currentPage = 1;
        cardDeck.removeAllViews();
        loadPosts(true);
    }

    @Override
    public void onAnswerQuestion(Question question, final View view, boolean yes) {
        //if (questions.size() == Math.ceil(PER_PAGE / 2))
          //  loadPosts(false);

        presenter.answerQuestion(question.getId(), yes);

        int angle = yes ? 45 : -45;
        int deltaX = yes ? 2000 : -1400;
        int deltaY = yes ? -1800 : -1100;

        Animation rotate = new RotateAnimation(0, angle);
        Animation translate = new TranslateAnimation(0, deltaX, 0, deltaY);

        AnimationSet set = new AnimationSet(true);
        set.addAnimation(rotate);
        set.addAnimation(translate);
        set.setDuration(350);
        set.setFillAfter(true);
        set.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                for (int i = 0; i < cardDeck.getChildCount(); i++)
                    cardDeck.getChildAt(i).setLayerType(View.LAYER_TYPE_HARDWARE, null);

                questions.remove(0);
                ViewCompat.setTranslationZ(view, 4F);
                cardDeck.removeView(view);

                if (questions.size() == 2)
                    pushCard(questions.get(1), 2F, -1F);
                else if (questions.size() > 2)
                    pushCard(questions.get(2), 1F, -2F);

                if (cardDeck.getChildCount() >= 1) {
                    View topCard = cardDeck.getChildAt(cardDeck.getChildCount() - 1);
                    ViewCompat.setTranslationZ(topCard, 3F);
                    topCard.animate().rotation(0F).start();
                }
                if (cardDeck.getChildCount() >= 2) {
                    View secondCard = cardDeck.getChildAt(cardDeck.getChildCount() - 2);
                    ViewCompat.setTranslationZ(secondCard, 2F);
                    secondCard.animate().rotation(-1F).start();
                }
                if (cardDeck.getChildCount() >= 3) {
                    View thirdCard = cardDeck.getChildAt(cardDeck.getChildCount() - 3);
                    ViewCompat.setTranslationZ(thirdCard, 1F);
                    thirdCard.animate().rotation(-2F).start();
                }
            }
            @Override public void onAnimationEnd(Animation animation) {
                view.setLayerType(View.LAYER_TYPE_NONE, null);
                for (int i = 0; i < cardDeck.getChildCount(); i++)
                    cardDeck.getChildAt(i).setLayerType(View.LAYER_TYPE_NONE, null);
            }
            @Override public void onAnimationRepeat(Animation animation) {}
        });

        view.setLayerType(View.LAYER_TYPE_HARDWARE, null);
        view.startAnimation(set);
    }

    private void pushCard(Question question, float zIndex, float rotation) {
        QuestionView view = (QuestionView) LayoutInflater.from(getActivity())
                .inflate(R.layout.view_question, cardDeck, false);
        view.bindTo(question, this);

        view.setRotation(rotation);

        cardDeck.addView(view, 0);
        ViewCompat.setTranslationZ(cardDeck.getChildAt(0), zIndex);
    }
}