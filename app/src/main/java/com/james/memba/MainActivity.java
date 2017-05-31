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
import com.james.memba.addberry.AddBerryFragment;
import com.james.memba.home.HomeFragment;
import com.james.memba.model.Account;
import com.james.memba.model.Berry;
import com.james.memba.services.MembaClient;
import com.james.memba.utils.ActivityUtils;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity implements HomeFragment.OnHomeLoadedListener{

    private String mClientId;
    private GoogleApiClient mGoogleApiClient;

    // Fragments
    private HomeFragment mHomeFragment;
    private AddBerryFragment mAddBerryFragment;

    // Navbar
    private ImageButton mHomeButton;
    private ImageButton mAddButton;
    private ImageButton mMapButton;
    private enum Navbar {HOME, ADD, MAP}

    private MembaClient mMembaClient;

    private Account mAccount;

    private ArrayList<Berry> mHomeBerries;

    private Callback mGetAccountCB = new Callback() {
        @Override
        public void onFailure(final Call call, IOException e) {
            // Error
        }

        @Override
        public void onResponse(Call call, final Response response) throws java.io.IOException {
            String data = response.body().string();

            try {
                if (response.isSuccessful()) {
                    // Retrieve account information
                    JSONObject object = new JSONObject(data);
                    mAccount = new Account(object.getString("userId"));
                    mMembaClient.getBerries(mAccount, mGetBerriesCB);
                } else {
                    // Create new account
                    JSONObject object = new JSONObject(data);
                    mAccount = new Account(object.getString("userId"));
                    mMembaClient.createAccount(mAccount.getUserId());
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    };

    private Callback mGetBerriesCB = new Callback() {
        @Override
        public void onFailure(Call call, IOException e) {

        }

        @Override
        public void onResponse(Call call, Response response) throws IOException {
            ArrayList<Berry> berries = new ArrayList<>();
            String data = response.body().string();
            try {
                if (response.isSuccessful()) {
                    JSONArray jArray = new JSONArray(data);
                    for (int i = 0; i < jArray.length(); i++) {
                        berries.add(mMembaClient.JSONToBerry(jArray.getJSONObject(i)));
                    }
                    mHomeBerries = berries;
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            mHomeFragment.showBerries(mHomeBerries);
                        }
                    });
                } else {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            mHomeFragment.showNoBerries();
                        }
                    });
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mClientId = getIntent().getStringExtra("SIGNIN_CLIENTID");
        mMembaClient = new MembaClient(mClientId);

        googleSignIn();

        String email = getIntent().getStringExtra("SIGNIN_EMAIL");
        String userId = getIntent().getStringExtra("SIGNIN_ACCOUNTID");
        String token = getIntent().getStringExtra("SIGNIN_IDTOKEN");

        // Asynchronous callbacks
        mMembaClient.getAccount(userId, mGetAccountCB);

        // Load all fragments
        mHomeFragment = (HomeFragment) getSupportFragmentManager().findFragmentById(R.id.contentFrame);
        if (mHomeFragment == null) {
            mHomeFragment = HomeFragment.newInstance();
            ActivityUtils.addFragmentToActivity(getSupportFragmentManager(), mHomeFragment, R.id.contentFrame);
        }
    }

    private void googleSignIn() {
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestProfile()
                .requestEmail()
                .requestIdToken(mClientId)
                .build();

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();

        mGoogleApiClient.connect();
    }

    private void setNavbar(Navbar n) {
        switch(n) {
            case HOME:
                mHomeButton = (ImageButton) findViewById(R.id.home_button);
                mHomeButton.setImageDrawable(getResources().getDrawable(R.drawable.home_selected, null));
                mAddButton = (ImageButton) findViewById(R.id.add_button);
                mAddButton.setImageDrawable(getResources().getDrawable(R.drawable.add_unselected, null));
                mMapButton = (ImageButton) findViewById(R.id.map_button);
                mMapButton.setImageDrawable(getResources().getDrawable(R.drawable.map_unselected, null));
                break;
            case ADD:
                mHomeButton = (ImageButton) findViewById(R.id.home_button);
                mHomeButton.setImageDrawable(getResources().getDrawable(R.drawable.home_unselected, null));
                mAddButton = (ImageButton) findViewById(R.id.add_button);
                mAddButton.setImageDrawable(getResources().getDrawable(R.drawable.add_selected, null));
                mMapButton = (ImageButton) findViewById(R.id.map_button);
                mMapButton.setImageDrawable(getResources().getDrawable(R.drawable.map_unselected, null));
                break;
            case MAP:
                mHomeButton = (ImageButton) findViewById(R.id.home_button);
                mHomeButton.setImageDrawable(getResources().getDrawable(R.drawable.home_unselected, null));
                mAddButton = (ImageButton) findViewById(R.id.add_button);
                mAddButton.setImageDrawable(getResources().getDrawable(R.drawable.add_unselected, null));
                mMapButton = (ImageButton) findViewById(R.id.map_button);
                mMapButton.setImageDrawable(getResources().getDrawable(R.drawable.map_selected, null));
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

    @Override
    public void onHomeLoaded() {
        setNavbar(Navbar.HOME);
    }
}
