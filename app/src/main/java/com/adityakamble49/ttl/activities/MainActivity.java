package com.adityakamble49.ttl.activities;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.adityakamble49.ttl.R;
import com.adityakamble49.ttl.fragments.LogFragment;
import com.adityakamble49.ttl.fragments.TimerFragment;
import com.adityakamble49.ttl.network.NetworkKeys;
import com.adityakamble49.ttl.utils.Constants;
import com.adityakamble49.ttl.utils.SharedPrefUtils;

import me.pushy.sdk.Pushy;

public class MainActivity extends AppCompatActivity implements BottomNavigationView
        .OnNavigationItemSelectedListener {

    private static final String TAG = "MainActivity";
    private final int ELEVATION = 6;


    private BottomNavigationView mBottomNavigationView;
    private Toolbar mToolBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (isValidUser()) {
            setupUI();
            setupPushy();
        }
    }

    private void setupUI() {
        mToolBar = (Toolbar) findViewById(R.id.v_tb_toolbar_main);
        setSupportActionBar(mToolBar);
        ViewCompat.setElevation(mToolBar, ELEVATION);
        loadDefaultFragment();
        mBottomNavigationView = (BottomNavigationView) findViewById(R.id.v_bnv_bottom_nav);
        mBottomNavigationView.setOnNavigationItemSelectedListener(this);
    }

    private boolean isValidUser() {
        String userToken = SharedPrefUtils.getStringFromPreferences(this, NetworkKeys.KEY_TOKEN,
                "");
        if (userToken.isEmpty() || userToken.equals("")) {
            Intent loginActivityIntent = new Intent(this, LoginActivity.class);
            startActivity(loginActivityIntent);
            finish();
        } else {
            return true;
        }
        return false;
    }

    private void setupPushy() {
        Pushy.listen(this);
        // Check whether the user has granted us the READ/WRITE_EXTERNAL_STORAGE permissions
        if (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission
                .WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            // Request both READ_EXTERNAL_STORAGE and WRITE_EXTERNAL_STORAGE so that the
            // Pushy SDK will be able to persist the device token in the external storage
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission
                        .READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE}, 0);
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_about:
                Intent aboutActivityIntent = new Intent(MainActivity.this, AboutActivity.class);
                startActivity(aboutActivityIntent);
                break;
        }
        return true;
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        loadSelectedFragment(item);
        return true;
    }

    private void loadDefaultFragment() {
        Fragment selectedFragment = TimerFragment.newInstance();
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.v_fl_main_container, selectedFragment, selectedFragment
                .getTag());
        fragmentTransaction.commitAllowingStateLoss();
    }

    private void loadSelectedFragment(MenuItem item) {
        Fragment selectedFragment = getSelectedFragment(item);
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.v_fl_main_container, selectedFragment, selectedFragment
                .getTag());
        fragmentTransaction.commitAllowingStateLoss();
    }

    private Fragment getSelectedFragment(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.nav_timer:
                return TimerFragment.newInstance();
            case R.id.nav_logs:
                return LogFragment.newInstance();
        }

        return null;
    }
}
