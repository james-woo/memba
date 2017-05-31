package com.james.memba;

import android.os.StrictMode;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageButton;
import android.widget.Toast;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.GoogleApiClient;
import com.james.memba.home.HomeFragment;
import com.james.memba.model.Account;
import com.james.memba.model.Berry;
import com.james.memba.services.MembaClient;
import com.james.memba.utils.ActivityUtils;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private String mClientId;
    private GoogleApiClient mGoogleApiClient;

    // Navbar
    private ImageButton mHomeButton;
    private ImageButton mAddButton;
    private ImageButton mMapButton;

    private MembaClient mMembaClient;

    private Account mAccount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

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

        String email = getIntent().getStringExtra("SIGNIN_EMAIL");
        String userId = getIntent().getStringExtra("SIGNIN_ACCOUNTID");
        String token = getIntent().getStringExtra("SIGNIN_IDTOKEN");

        mAccount = new Account(email, token, userId);

        mMembaClient = new MembaClient(mClientId);

        HomeFragment homeFragment = (HomeFragment) getSupportFragmentManager().findFragmentById(R.id.contentFrame);
        if (homeFragment == null) {
            homeFragment = HomeFragment.newInstance(mMembaClient.getBerries(mAccount));
            ActivityUtils.addFragmentToActivity(
                    getSupportFragmentManager(), homeFragment, R.id.contentFrame);
        }

        mHomeButton = (ImageButton) findViewById(R.id.home_button);
        mHomeButton.setImageDrawable(getResources().getDrawable(R.drawable.home_selected, null));
        mAddButton = (ImageButton) findViewById(R.id.add_button);
        mAddButton.setImageDrawable(getResources().getDrawable(R.drawable.add_unselected, null));
        mMapButton = (ImageButton) findViewById(R.id.map_button);
        mMapButton.setImageDrawable(getResources().getDrawable(R.drawable.map_unselected, null));
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
