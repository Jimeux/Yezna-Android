package com.moobasoft.yezna.ui.activities;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.moobasoft.yezna.R;
import com.moobasoft.yezna.events.EventBus;
import com.moobasoft.yezna.events.auth.LogOutEvent;
import com.moobasoft.yezna.events.auth.LoginEvent;
import com.moobasoft.yezna.events.auth.LoginPromptEvent;
import com.moobasoft.yezna.rest.models.Question;
import com.moobasoft.yezna.rest.models.User;
import com.moobasoft.yezna.ui.activities.base.BaseActivity;
import com.moobasoft.yezna.ui.fragments.MyQuestionsFragment;
import com.moobasoft.yezna.ui.fragments.PublicQuestionFragment;

import javax.inject.Inject;

import butterknife.Bind;
import butterknife.ButterKnife;
import rx.Subscription;
import rx.subscriptions.CompositeSubscription;

import static com.moobasoft.yezna.ui.MyQuestionsAdapter.SummaryClickListener;

public class MainActivity extends BaseActivity implements SummaryClickListener {

    @Bind(R.id.app_bar) AppBarLayout appBarLayout;
    @Bind(R.id.drawer_layout) DrawerLayout drawerLayout;
    @Bind(R.id.navigation_view) NavigationView navigationView;
    @Bind(R.id.toolbar) Toolbar toolbar;
    @Bind(R.id.container) ViewGroup container;

    @Inject EventBus eventBus;

    public static final String CURRENT_TAG_KEY = "current_tag_key";

    enum Tag {PUBLIC_QUESTIONS, MY_QUESTIONS, CREATE_QUESTION, PROFILE}

    private Tag currentTag = Tag.PUBLIC_QUESTIONS;
    private FragmentManager fragmentManager;
    private CompositeSubscription eventSubscriptions;

    @Override protected void onCreate(@Nullable Bundle state) {
        super.onCreate(state);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        getComponent().inject(this);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");
        fragmentManager = getFragmentManager();
        initNavigationDrawer();
    }

    @Override protected void onSaveInstanceState(Bundle state) {
        super.onSaveInstanceState(state);
        state.putSerializable(CURRENT_TAG_KEY, currentTag);
    }

    @Override protected void onPostCreate(@Nullable Bundle state) {
        super.onPostCreate(state);
        if (state != null) {
            Tag savedTag = (Tag) state.getSerializable(CURRENT_TAG_KEY);
            if (savedTag != null) currentTag = savedTag;
        }
        showFragment(currentTag);
    }

    @Override protected void onStart() {
        super.onStart();
        subscribeToEvents();
    }

    @Override protected void onStop() {
        super.onStop();
        eventSubscriptions.clear();
    }

    private void subscribeToEvents() {
        Subscription loginEvent = eventBus
                .listenFor(LoginEvent.class)
                .subscribe(event -> {
                    setDrawerMenuItems();
                    loadUserDetails();
                });

        Subscription loginPromptEvent = eventBus
                .listenFor(LoginPromptEvent.class)
                .subscribe(this::promptForLogin);

        Subscription logoutEvent = eventBus
                .listenFor(LogOutEvent.class)
                .subscribe(this::clearUserDetails);

        eventSubscriptions = new CompositeSubscription(
                loginEvent, loginPromptEvent, logoutEvent);
    }

    private void showFragment(Tag tag) {
        FragmentTransaction transaction = fragmentManager.beginTransaction(); //.setCustomAnimations(android.R.animator.fade_in, android.R.animator.fade_out);

        for (Tag t : Tag.values()) {
            Fragment fragment = fragmentManager.findFragmentByTag(t.name());

            if (t.equals(tag) && fragment == null)
                transaction.add(container.getId(), createFragment(tag), tag.name());
            else if (t.equals(tag) && fragment != null)
                transaction.show(fragment);
            else if (fragment != null)
                transaction.hide(fragment);
        }
        transaction.commit();
        currentTag = tag;
    }

    private Fragment createFragment(Tag tag) {
        switch (tag) {
            case PUBLIC_QUESTIONS:
                return new PublicQuestionFragment();
            case MY_QUESTIONS:
                return new MyQuestionsFragment();
            default:
                return null;
        }
    }

    private void initNavigationDrawer() {
        navigationView.setNavigationItemSelectedListener(item -> {
            switch (item.getItemId()) {
                case R.id.action_public_questions:
                    showFragment(Tag.PUBLIC_QUESTIONS);
                    break;

                case R.id.action_my_questions:
                    showFragment(Tag.MY_QUESTIONS);
                    break;

                /** Auth-related items */
                case R.id.action_login:
                    Intent loginIntent = new Intent(this, ConnectActivity.class);
                    loginIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    loginIntent.putExtra(ConnectActivity.REGISTER, false);
                    startActivity(loginIntent);
                    break;

                case R.id.action_register:
                    Intent registerIntent = new Intent(this, ConnectActivity.class);
                    registerIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    registerIntent.putExtra(ConnectActivity.REGISTER, true);
                    startActivity(registerIntent);
                    break;

                case R.id.action_logout:
                    credentialStore.delete();
                    Snackbar.make(container, getString(R.string.logout_success), Snackbar.LENGTH_SHORT).show();
                    setDrawerMenuItems();
                    eventBus.send(new LogOutEvent());
                    break;
            }
            drawerLayout.closeDrawers();
            item.setChecked(false);
            invalidateOptionsMenu();
            return false;
        });

        ActionBarDrawerToggle actionBarDrawerToggle = new ActionBarDrawerToggle(
                this, drawerLayout, toolbar, R.string.login, R.string.logout) {
            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
            }

            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
            }
        };

        drawerLayout.setDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.syncState();
        setDrawerMenuItems();
        loadUserDetails();
    }

    private void clearUserDetails(LogOutEvent event) {
        View header = navigationView.getHeaderView(0);
        TextView username = (TextView) header.findViewById(R.id.username);
        username.setText(getString(R.string.not_signed_in));
        ImageView avatar = (ImageView) header.findViewById(R.id.avatar);
        avatar.setImageDrawable(null);
    }

    private void loadUserDetails() {
        User user = credentialStore.loadUser();

        if (user != null) {
            View header = navigationView.getHeaderView(0);

            TextView username = (TextView) header.findViewById(R.id.username);
            username.setText(getString(R.string.signed_in_as, user.getUsername()));

            ImageView avatar = (ImageView) header.findViewById(R.id.avatar);
            if (user.getAvatar() != null)
                Glide.with(getApplicationContext())
                        .load(user.getAvatar())
                        .into(avatar);
        }
    }

    @Override public void onSummaryClicked(Question question) {
        //manager.openShowFragment(question);
    }

    private void setDrawerMenuItems() {
        Menu menu = navigationView.getMenu();
        boolean loggedIn = credentialStore.isLoggedIn();
        menu.setGroupVisible(R.id.unauthenticated_group, !loggedIn);
        menu.setGroupVisible(R.id.public_group, loggedIn);
        menu.setGroupVisible(R.id.sign_out_group, loggedIn);
        menu.setGroupVisible(R.id.signed_in_group, loggedIn);
    }

    @Override public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        menu.findItem(R.id.action_public_questions).setVisible(
                currentTag.equals(Tag.MY_QUESTIONS));
        menu.findItem(R.id.action_my_questions).setVisible(
                currentTag.equals(Tag.PUBLIC_QUESTIONS));
        return true;
    }

    @Override public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                drawerLayout.openDrawer(GravityCompat.START);
                return true;

            case R.id.action_create_question:
                promptForLogin(null);
                break;

            case R.id.action_my_questions:
                showFragment(Tag.MY_QUESTIONS);
                break;

            case R.id.action_public_questions:
                showFragment(Tag.PUBLIC_QUESTIONS);
                break;
        }
        invalidateOptionsMenu();
        return true;
    }

    public void promptForLogin(LoginPromptEvent event) {
        String message = (event != null && event.getMessage() != null) ?
                event.getMessage() : getString(R.string.error_unauthorized);

        Snackbar.make(toolbar, message, Snackbar.LENGTH_LONG)
                .setActionTextColor(getResources().getColor(R.color.green400))
                .setAction(getString(R.string.login), v -> {
                    Intent intent = new Intent(MainActivity.this, ConnectActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    intent.putExtra(ConnectActivity.REGISTER, false);
                    startActivity(intent);
                })
                .show();
    }
}