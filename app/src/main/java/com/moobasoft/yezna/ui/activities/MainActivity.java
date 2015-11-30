package com.moobasoft.yezna.ui.activities;

import android.app.FragmentManager;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;

import com.moobasoft.yezna.R;
import com.moobasoft.yezna.rest.models.Question;
import com.moobasoft.yezna.ui.activities.base.BaseActivity;
import com.moobasoft.yezna.ui.fragments.SummaryFragment;
import com.moobasoft.yezna.ui.presenters.base.Presenter;

import java.util.UUID;

import butterknife.Bind;
import butterknife.ButterKnife;

import static com.moobasoft.yezna.ui.SummaryAdapter.SummaryClickListener;
import static com.moobasoft.yezna.ui.fragments.PresenterRetainer.PresenterHost;
import static com.moobasoft.yezna.ui.fragments.PresenterRetainer.get;
import static com.moobasoft.yezna.ui.fragments.PresenterRetainer.put;

public class MainActivity extends BaseActivity implements SummaryClickListener, PresenterHost {

    private FragmentManager fragmentManager;

    @Bind(R.id.container) ViewGroup container;
    @Bind(R.id.toolbar)   Toolbar toolbar;

    @Override
    public void putPresenter(@NonNull UUID key, @NonNull Presenter presenter) {
        put(fragmentManager, key, presenter);
    }

    @Override
    public Presenter getPresenter(@NonNull UUID key) {
        return get(fragmentManager, key);
    }

    @Override
    protected void onCreate(@Nullable Bundle state) {
        super.onCreate(state);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        getComponent().inject(this);
        setSupportActionBar(toolbar);
        fragmentManager = getFragmentManager();

        if (state == null)
            fragmentManager.beginTransaction()
                    .add(container.getId(), new SummaryFragment())
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
        menu.findItem(R.id.action_logout).setVisible(loggedIn);
        menu.findItem(R.id.action_login).setVisible(!loggedIn);
        menu.findItem(R.id.action_register).setVisible(!loggedIn);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
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
        supportInvalidateOptionsMenu();
        return false;
    }
}