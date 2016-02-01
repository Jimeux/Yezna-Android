package com.moobasoft.yezna.ui.activities;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
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
import com.moobasoft.yezna.events.ask.QuestionCreatedEvent;
import com.moobasoft.yezna.events.auth.LogOutEvent;
import com.moobasoft.yezna.events.auth.LoginEvent;
import com.moobasoft.yezna.rest.models.Question;
import com.moobasoft.yezna.rest.models.User;
import com.moobasoft.yezna.ui.activities.base.BaseActivity;
import com.moobasoft.yezna.ui.fragments.MyQuestionsFragment;
import com.moobasoft.yezna.ui.fragments.ProfileFragment;
import com.moobasoft.yezna.ui.fragments.PublicQuestionsFragment;

import butterknife.Bind;
import butterknife.ButterKnife;
import icepick.Icicle;

import static com.moobasoft.yezna.ui.MyQuestionsAdapter.SummaryClickListener;

public class MainActivity extends BaseActivity implements SummaryClickListener {

    @Bind(R.id.drawer_layout) DrawerLayout drawerLayout;
    @Bind(R.id.navigation_view) NavigationView navigationView;
    @Bind(R.id.toolbar) Toolbar toolbar;
    @Bind(R.id.container) ViewGroup container;

    @Icicle Tag currentTag = Tag.PUBLIC_QUESTIONS;

    enum Tag {PUBLIC_QUESTIONS, MY_QUESTIONS, PROFILE}
    private FragmentManager fragmentManager;

    @Override public void onCreate(@Nullable Bundle state) {
        super.onCreate(state);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        getComponent().inject(this);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");
        fragmentManager = getFragmentManager();
        initNavigationDrawer();
    }

    @Override protected void onPostCreate(@Nullable Bundle state) {
        super.onPostCreate(state);
        showFragment(currentTag);
    }

    @Override
    public void subscribeToEvents() {
        super.subscribeToEvents();
        eventSubscriptions.add(eventBus.listenFor(LoginEvent.class)
                .subscribe(event -> {
                    setDrawerMenuItems();
                    loadUserDetails();
                }));
        eventSubscriptions.add(eventBus.listenFor(LogOutEvent.class)
                .subscribe(this::handleLogOut));
        eventSubscriptions.add(eventBus.listenFor(QuestionCreatedEvent.class)
                .subscribe(questionCreatedEvent -> showFragment(Tag.MY_QUESTIONS)));
    }

    private void showFragment(Tag tag) {
        if (!tag.equals(Tag.PUBLIC_QUESTIONS) && !credentialStore.isLoggedIn())
            promptForLogin(null);
        else {
            FragmentTransaction transaction = fragmentManager.beginTransaction(); //.setCustomAnimations(android.R.animator.fade_in, android.R.animator.fade_out);

            for (Tag t : Tag.values()) {
                Fragment fragment = fragmentManager.findFragmentByTag(t.name());

                if (t.equals(tag) && fragment == null) {
                    fragment = createFragment(tag);
                    /*fragment.setExitTransition(new Fade(Fade.OUT));
                    fragment.setEnterTransition(new Fade(Fade.IN));
                    fragment.setReturnTransition(new Fade(Fade.IN));*/
                    transaction.add(container.getId(), fragment, tag.name());
                } else if (t.equals(tag) && fragment != null)
                    transaction.show(fragment);
                else if (fragment != null)
                    transaction.hide(fragment);
            }
            transaction.commit();
            currentTag = tag;
            invalidateOptionsMenu();
        }
    }

    private Fragment createFragment(@NonNull Tag tag) {
        switch (tag) {
            case PUBLIC_QUESTIONS:
                return new PublicQuestionsFragment();
            case MY_QUESTIONS:
                return new MyQuestionsFragment();
            case PROFILE:
                return new ProfileFragment();
            default:
                return null;
        }
    }

    private void initNavigationDrawer() {
        navigationView.setNavigationItemSelectedListener(item -> {
            drawerLayout.closeDrawers();
            item.setChecked(false);
            if (credentialStore.isLoggedIn() && !(item.getItemId() == R.id.action_ask_question))
                handleMenuItemClick(item.getItemId());
            else
                // Wait for drawer to close for smoother animation
                drawerLayout.postDelayed(() -> handleMenuItemClick(item.getItemId()), 220);
            return false; // False to avoid checking items
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

    private void handleLogOut(LogOutEvent event) {
        showFragment(Tag.PUBLIC_QUESTIONS);
        View header = navigationView.getHeaderView(0);
        TextView username = (TextView) header.findViewById(R.id.signed_in_username);
        username.setText(getString(R.string.not_signed_in));
        ImageView avatar = (ImageView) header.findViewById(R.id.avatar);
        avatar.setImageDrawable(null);
        invalidateOptionsMenu();
    }

    private void loadUserDetails() {
        User user = credentialStore.loadUser();

        if (user != null) {
            View header = navigationView.getHeaderView(0);

            TextView username = (TextView) header.findViewById(R.id.signed_in_username);
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
        menu.findItem(R.id.action_ask_question).setVisible(true);
        menu.findItem(R.id.action_public_questions)
                .setVisible(!currentTag.equals(Tag.PUBLIC_QUESTIONS));
        menu.findItem(R.id.action_my_questions)
                .setVisible(!currentTag.equals(Tag.MY_QUESTIONS));
        return true;
    }

    @Override public boolean onOptionsItemSelected(MenuItem item) {
        return handleMenuItemClick(item.getItemId());
    }

    private boolean handleMenuItemClick(int id) {
        switch (id) {
            case R.id.action_public_questions:
                showFragment(Tag.PUBLIC_QUESTIONS);
                break;

            case R.id.action_my_questions:
                showFragment(Tag.MY_QUESTIONS);
                break;

            case R.id.action_profile:
                showFragment(Tag.PROFILE);
                break;

            case R.id.action_ask_question:
                if (credentialStore.isLoggedIn()) {
                    Intent intent = new Intent(MainActivity.this, AskQuestionActivity.class);
                    ActivityOptionsCompat options = ActivityOptionsCompat
                            .makeSceneTransitionAnimation(this);
                    ActivityCompat.startActivity(this, intent, options.toBundle());
                } else {
                    promptForLogin(null);
                }
                break;

            /** Auth-related items */
            case R.id.action_login:
                startConnectActivity(false);
                break;

            case R.id.action_register:
                startConnectActivity(true);
                break;

            case R.id.action_logout:
                credentialStore.delete();
                Snackbar.make(container, getString(R.string.logout_success), Snackbar.LENGTH_SHORT).show();
                setDrawerMenuItems();
                eventBus.send(new LogOutEvent());
                break;
        }
        return true;
    }

}