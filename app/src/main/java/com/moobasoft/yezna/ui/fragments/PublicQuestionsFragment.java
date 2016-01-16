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
import com.moobasoft.yezna.events.auth.LogOutEvent;
import com.moobasoft.yezna.events.auth.LoginEvent;
import com.moobasoft.yezna.rest.models.Question;
import com.moobasoft.yezna.ui.fragments.base.RxFragment;
import com.moobasoft.yezna.ui.presenters.PublicQuestionPresenter;
import com.moobasoft.yezna.ui.views.PublicQuestionView;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import butterknife.ButterKnife;
import rx.Subscription;
import rx.subscriptions.CompositeSubscription;

import static android.view.View.VISIBLE;
import static com.moobasoft.yezna.ui.views.PublicQuestionView.QuestionClickListener;

public class PublicQuestionsFragment extends RxFragment
        implements PublicQuestionPresenter.View, QuestionClickListener {

    @Inject PublicQuestionPresenter presenter;

    public static final String QUESTIONS_KEY = "questions_key";
    public static final int PER_PAGE = 6;

    private ArrayList<Question> questions;
    /**
     * Manages question views as an animated deck of cards.
     */
    private CardDeck cardDeck;
    /**
     * Track whether we're awaiting on a callback.
     */
    private boolean loading;

    public PublicQuestionsFragment() {
    }

    @Override public void onCreate(@Nullable Bundle state) {
        super.onCreate(state);
        getComponent().inject(this);
        presenter.bindView(this);

        if (state != null)
            questions = state.getParcelableArrayList(QUESTIONS_KEY);
        if (questions == null)
            questions = new ArrayList<>();
    }

    @Override public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle state) {
        View view = inflater.inflate(R.layout.fragment_public_questions, container, false);
        ButterKnife.bind(this, view);
        cardDeck = new CardDeck(contentView, questions, this);
        cardDeck.initialise();
        return view;
    }

    @Override public void onActivityCreated(@Nullable Bundle state) {
        super.onActivityCreated(state);

        if (state == null) // Add first-init check
            loadPosts(false);
        else if (questions.isEmpty())
            activateEmptyView(getString(R.string.no_questions_found));
        else
            activateContentView();
    }

    @Override public void onSaveInstanceState(Bundle state) {
        super.onSaveInstanceState(state);
        state.putParcelableArrayList(QUESTIONS_KEY, questions);
    }

    @Override public void onDestroyView() {
        presenter.releaseView();
        ButterKnife.unbind(this);
        super.onDestroyView();
    }

    @Override protected void subscribeToEvents() {
        Subscription loginEventSubscription =
                eventBus.listenFor(LoginEvent.class)
                        .subscribe(event -> onRefresh()); //TODO: Error handling?

        Subscription logOutEventSubscription =
                eventBus.listenFor(LogOutEvent.class)
                        .subscribe(event -> onRefresh());

        eventSubscriptions = new CompositeSubscription(
                loginEventSubscription, logOutEventSubscription);
    }

    private void loadPosts(boolean refresh) {
        loading = true;
        boolean errorViewVisible = errorView.getVisibility() == VISIBLE;
        boolean emptyViewVisible = emptyView.getVisibility() == VISIBLE;

        if (questions.isEmpty() || errorViewVisible || emptyViewVisible)
            activateLoadingView();

        int fromId = (questions.isEmpty()) ?
                0 : questions.get(questions.size() - 1).getId();
        presenter.loadSummaries(refresh, fromId);
    }

    @Override public void onQuestionsRetrieved(List<Question> newQuestions) {
        loading = false;

        if (newQuestions.isEmpty() && questions.isEmpty())
            activateEmptyView(getString(R.string.no_more_questions));
        else if (!newQuestions.isEmpty()) {
            activateContentView();
            boolean wasEmptyOrDepleted = questions.isEmpty() || questions.size() < 3;
            questions.addAll(newQuestions);
            if (wasEmptyOrDepleted)
                cardDeck.initialise();
        }
    }

    @Override public void onAnswerQuestion(Question question, View view, boolean yes) {
        if (!credentialStore.isLoggedIn())
            promptForLogin(getString(R.string.sign_in_to_answer));
        else {
            presenter.answerQuestion(question.getId(), yes);
            cardDeck.pop();
            if (questions.size() == Math.ceil(PER_PAGE / 2.0))
                loadPosts(false);
        }
    }

    @Override public void onRefresh() {
        questions.clear();
        contentView.removeAllViews();
        loadPosts(true);
    }

    class CardDeck {
        /**
         * Container for the views to be pushed and popped into/from.
         */
        private final ViewGroup container;
        /**
         * List of question objects to be bound to the views.
         */
        private final ArrayList<Question> questions;
        /**
         * Listener for yes/no clicks.
         */
        private final QuestionClickListener clickListener;

        public CardDeck(ViewGroup container, ArrayList<Question> questions,
                        QuestionClickListener clickListener) {
            this.container = container;
            this.questions = questions;
            this.clickListener = clickListener;
        }

        /**
         * Try to add 3 initial views to {@link #container}.
         */
        public void initialise() {
            container.removeAllViews();
            for (int i = 0; i < 3 && i < questions.size(); i++)
                push(questions.get(i), true);
            resetCards();
        }

        /**
         * Animate the views in {@link #container} to their initial
         * state by changing the z-index and rotation.
         */
        private void resetCards() {
            for (int position = 0; position < container.getChildCount(); position++) {
                View card = container.getChildAt(position);
                ViewCompat.setTranslationZ(card, 4.0F - (position + 1));
                card.animate().setDuration(220).rotation(1.0F - (position + 1)).start();

                boolean isOnTop = (position == 0);
                card.findViewById(R.id.yes_btn).setEnabled(isOnTop);
                card.findViewById(R.id.no_btn).setEnabled(isOnTop);
            }
        }

        /**
         * Add a view to the bottom of {@link #container}.
         *
         * @param question the question object to bind the view to
         */
        private void push(Question question, boolean upright) {
            PublicQuestionView card = (PublicQuestionView) LayoutInflater.from(container.getContext())
                    .inflate(R.layout.view_public_question, container, false);
            card.bindTo(question, clickListener);
            card.setRotation(upright ? 0F : -2F);
            container.addView(card, container.getChildCount());
        }

        /**
         * Animate the removal of the top view from {@link #container}.
         */
        public void pop() {
            View view = container.getChildAt(0);

            Animation rotate = new RotateAnimation(0, 45);
            Animation translate = new TranslateAnimation(0, 1800, 0, -1900);

            AnimationSet set = new AnimationSet(true);
            set.addAnimation(rotate);
            set.addAnimation(translate);
            set.setDuration(450);
            set.setAnimationListener(new Animation.AnimationListener() {
                @Override public void onAnimationStart(Animation animation) {
                    setCardHardwareAcceleration(true);
                    ViewCompat.setTranslationZ(view, 4F);
                    popAndPushNext();
                }

                @Override public void onAnimationEnd(Animation animation) {
                    setCardHardwareAcceleration(false);

                    //FIXME: Ties this class to the fragment - REMOVE
                    if (questions.isEmpty() && loading)
                        activateLoadingView();
                    else if (questions.isEmpty() && !loading)
                        activateEmptyView(getString(R.string.no_more_questions));
                }

                @Override public void onAnimationRepeat(Animation animation) {
                }
            });
            view.startAnimation(set);
        }

        /**
         * Remove the top view and add a new view to the bottom if available.
         */
        private void popAndPushNext() {
            questions.remove(0);
            container.post(() -> {
                container.removeViewAt(0);
                if (questions.size() > 2)
                    push(questions.get(2), false);

                resetCards();
            });
        }

        /**
         * Convenience method to toggle hardware acceleration (HA) at view-level for
         * each view in {@link #container}, which allows for smoother animations.
         *
         * @param activate decide whether HA is to be used or not
         */
        private void setCardHardwareAcceleration(boolean activate) {
            int layerType = activate ? View.LAYER_TYPE_HARDWARE : View.LAYER_TYPE_NONE;
            for (int i = 0; i < container.getChildCount(); i++)
                container.getChildAt(i).setLayerType(layerType, null);
        }
    }
}