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

import com.moobasoft.yezna.R;
import com.moobasoft.yezna.rest.models.Question;
import com.moobasoft.yezna.ui.activities.base.BaseActivity;
import com.moobasoft.yezna.ui.fragments.RandomFragment;

import butterknife.Bind;
import butterknife.ButterKnife;

import static com.moobasoft.yezna.ui.SummaryAdapter.SummaryClickListener;

public class MainActivity extends BaseActivity implements SummaryClickListener {

    private FragmentManager fragmentManager;

    @Bind(R.id.container) ViewGroup container;
    @Bind(R.id.toolbar)   Toolbar toolbar;
    private DrawerLayout drawerLayout;

    @Override
    protected void onCreate(@Nullable Bundle state) {
        super.onCreate(state);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        getComponent().inject(this);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");
        fragmentManager = getFragmentManager();

        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);

        NavigationView view = (NavigationView) findViewById(R.id.navigation_view);
        view.setNavigationItemSelectedListener(item -> {

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
                    break;
            }
            Snackbar.make(container, item.getTitle() + " pressed", Snackbar.LENGTH_LONG).show();
            item.setChecked(true);
            drawerLayout.closeDrawers();
            return true;
        });


        // Initializing Drawer Layout and ActionBarToggle
        ActionBarDrawerToggle actionBarDrawerToggle = new ActionBarDrawerToggle(this,drawerLayout,toolbar,R.string.login, R.string.logout){
            @Override
            public void onDrawerClosed(View drawerView) {
                // Code here will be triggered once the drawer closes as we dont want anything to happen so we leave this blank
                super.onDrawerClosed(drawerView);
            }

            @Override
            public void onDrawerOpened(View drawerView) {
                // Code here will be triggered once the drawer open as we dont want anything to happen so we leave this blank

                super.onDrawerOpened(drawerView);
            }
        };

        //Setting the actionbarToggle to drawer layout
        drawerLayout.setDrawerListener(actionBarDrawerToggle);

        //calling sync state is necessay or else your hamburger icon wont show up
        actionBarDrawerToggle.syncState();



        if (state == null)
            fragmentManager.beginTransaction()
                    .add(container.getId(), new RandomFragment())
                    .commit();
    }

    @Override
    public void onSummaryClicked(Question question) {
        //manager.openShowFragment(question);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        boolean loggedIn = credentialStore.isLoggedIn();
        /*menu.findItem(R.id.action_logout).setVisible(loggedIn);
        menu.findItem(R.id.action_login).setVisible(!loggedIn);
        menu.findItem(R.id.action_register).setVisible(!loggedIn);*/
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
                break;
        }
        supportInvalidateOptionsMenu();
        return false;
    }
}