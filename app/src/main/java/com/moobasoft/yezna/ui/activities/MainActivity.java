package com.moobasoft.yezna.ui.activities;

import android.app.FragmentManager;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
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
import com.moobasoft.yezna.EventBus;
import com.moobasoft.yezna.R;
import com.moobasoft.yezna.rest.models.Question;
import com.moobasoft.yezna.rest.models.User;
import com.moobasoft.yezna.ui.activities.ConnectActivity.LoginEvent;
import com.moobasoft.yezna.ui.activities.base.BaseActivity;
import com.moobasoft.yezna.ui.fragments.PublicQuestionFragment;

import javax.inject.Inject;

import butterknife.Bind;
import butterknife.ButterKnife;
import rx.Subscription;
import rx.subscriptions.CompositeSubscription;

import static com.moobasoft.yezna.ui.SummaryAdapter.SummaryClickListener;

public class MainActivity extends BaseActivity implements SummaryClickListener {

    @Bind(R.id.drawer_layout) DrawerLayout drawerLayout;
    @Bind(R.id.navigation_view) NavigationView navigationView;
    @Bind(R.id.toolbar) Toolbar toolbar;
    @Bind(R.id.container) ViewGroup container;

    @Inject EventBus eventBus;

    private FragmentManager fragmentManager;
    private CompositeSubscription subscriptions;

    @Override
    protected void onCreate(@Nullable Bundle state) {
        super.onCreate(state);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        getComponent().inject(this);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");
        fragmentManager = getFragmentManager();

        initNavigationDrawer();

        if (state == null)
            fragmentManager.beginTransaction()
                    .add(container.getId(), new PublicQuestionFragment())
                    .commit();
    }

    @Override
    protected void onStart() {
        super.onStart();
        subscribeToEvents();
    }

    @Override
    protected void onStop() {
        super.onStop();
        subscriptions.clear();
    }

    private void subscribeToEvents() {
        subscriptions = new CompositeSubscription();

        Subscription loginSubscription = eventBus.listen()
                .ofType(LoginEvent.class)
                .subscribe(event -> {
                    setMenuItems();
                    loadUserDetails();
                });

        subscriptions.add(loginSubscription);
    }

    private void initNavigationDrawer() {
        navigationView.setNavigationItemSelectedListener(item -> {
            switch (item.getItemId()) {
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
                    setMenuItems();
                    eventBus.send(new LogOutEvent());
                    break;
            }
            drawerLayout.closeDrawers();
            item.setChecked(false);
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
        setMenuItems();
        loadUserDetails();
    }

    private void loadUserDetails() {
        User user = credentialStore.loadUser();

        if (user != null) {
            View header = navigationView.getHeaderView(0);

            TextView username = (TextView) header.findViewById(R.id.username);
            username.setText(user.getUsername());

            ImageView avatar = (ImageView) header.findViewById(R.id.avatar);
            if (user.getAvatar() != null)
                Glide.with(getApplicationContext())
                        .load(user.getAvatar())
                        .into(avatar);
        }
    }

    @Override
    public void onSummaryClicked(Question question) {
        //manager.openShowFragment(question);
    }

    private void setMenuItems() {
        Menu menu = navigationView.getMenu();
        boolean loggedIn = credentialStore.isLoggedIn();
        menu.setGroupVisible(R.id.unauthenticated_group, !loggedIn);
        menu.setGroupVisible(R.id.signed_in_group, loggedIn);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                drawerLayout.openDrawer(GravityCompat.START);
                return true;

            case R.id.action_create:
                promptForLogin(toolbar);
                return true;

            case R.id.action_list:
                promptForLogin(toolbar);
                return true;
        }
        return false;
    }

    public static class LogOutEvent {}
}