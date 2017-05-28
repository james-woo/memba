package com.james.memba.home;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInApi;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.GoogleApiClient;
import com.james.memba.R;
import com.james.memba.model.source.BerryRepository;
import com.james.memba.model.source.remote.BerryRemoteDataSource;
import com.james.memba.utils.ActivityUtils;

public class HomeActivity extends AppCompatActivity {

    private HomePresenter mHomePresenter;

    private String mClientId;
    private GoogleApiClient mGoogleApiClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home_activity);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        //toolbar.setTitleTextColor(Color.BLACK);
        setSupportActionBar(toolbar);

        HomeFragment homeFragment = (HomeFragment) getSupportFragmentManager().findFragmentById(R.id.contentFrame);
        if (homeFragment == null) {
            homeFragment = HomeFragment.newInstance();
            ActivityUtils.addFragmentToActivity(
                    getSupportFragmentManager(), homeFragment, R.id.contentFrame);
        }

        // Create the presenter

        // TODO: Fix this temporary patch
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        mHomePresenter = new HomePresenter(
                BerryRepository.getInstance(BerryRemoteDataSource.getInstance()), homeFragment);

        mClientId = getIntent().getStringExtra("SIGNIN_CLIENTID");
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestProfile()
                .requestEmail()
                .requestIdToken(mClientId)
                .build();

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();

        mGoogleApiClient.connect();

        // Load previously saved state, if available.
        if (savedInstanceState != null) {

        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        switch(id) {
            case R.id.action_settings:
                return true;
            case R.id.action_sign_out:
                Auth.GoogleSignInApi.signOut(mGoogleApiClient);
                Toast.makeText(this, "Logged out.", Toast.LENGTH_LONG).show();
                finish();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
