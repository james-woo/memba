package com.james.memba;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.GoogleApiClient;
import com.james.memba.addberry.AddBerryFragment;
import com.james.memba.home.HomeFragment;
import com.james.memba.map.ViewMapFragment;
import com.james.memba.model.Account;
import com.james.memba.model.Berry;
import com.james.memba.services.MembaClient;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity implements HomeFragment.OnHomeLoadedListener,
AddBerryFragment.OnAddBerryLoadedListener, ViewMapFragment.OnViewMapLoadedListener {

    private String mClientId;
    private GoogleApiClient mGoogleApiClient;

    // Fragments
    private HomeFragment mHomeFragment;
    private AddBerryFragment mAddBerryFragment;
    private ViewMapFragment mViewMapFragment;

    // Navbar
    private ImageButton mHomeButton;
    private ImageButton mAddButton;
    private ImageButton mMapButton;
    private enum Navbar {HOME, ADD, MAP}
    private Navbar mCurrentPage = Navbar.HOME;
    private Navbar mLastPage = Navbar.HOME;

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

        // Navbar
        mHomeButton = (ImageButton) findViewById(R.id.home_button);
        mAddButton = (ImageButton) findViewById(R.id.add_button);
        mMapButton = (ImageButton) findViewById(R.id.map_button);
        setNavbarListeners();

        // Load all fragments
        mHomeFragment = HomeFragment.newInstance();
        mAddBerryFragment = AddBerryFragment.newInstance();
        mViewMapFragment = ViewMapFragment.newInstance();
        switchPage(Navbar.HOME);
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

    private void setNavbarListeners() {
        mHomeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mCurrentPage != Navbar.HOME) {
                    switchPage(Navbar.HOME);
                }
            }
        });

        mAddButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mCurrentPage != Navbar.ADD) {
                    switchPage(Navbar.ADD);
                }
            }
        });

        mMapButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mCurrentPage != Navbar.MAP) {
                    switchPage(Navbar.MAP);
                }
            }
        });
    }

    private void switchPage(Navbar n) {
        FragmentManager fm = getFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        mLastPage = mCurrentPage;

        switch(n) {
            case HOME:
                mHomeButton.setImageDrawable(getResources().getDrawable(R.drawable.home_selected, null));
                mAddButton.setImageDrawable(getResources().getDrawable(R.drawable.add_unselected, null));
                mMapButton.setImageDrawable(getResources().getDrawable(R.drawable.map_unselected, null));
                ft.replace(R.id.contentFrame, mHomeFragment)
                        .addToBackStack(mCurrentPage.name())
                        .commit();
                mCurrentPage = Navbar.HOME;
                break;
            case ADD:
                mHomeButton.setImageDrawable(getResources().getDrawable(R.drawable.home_unselected, null));
                mAddButton.setImageDrawable(getResources().getDrawable(R.drawable.add_selected, null));
                mMapButton.setImageDrawable(getResources().getDrawable(R.drawable.map_unselected, null));
                ft.replace(R.id.contentFrame, mAddBerryFragment)
                        .addToBackStack(mCurrentPage.name())
                        .commit();
                mCurrentPage = Navbar.ADD;
                break;
            case MAP:
                mHomeButton.setImageDrawable(getResources().getDrawable(R.drawable.home_unselected, null));
                mAddButton.setImageDrawable(getResources().getDrawable(R.drawable.add_unselected, null));
                mMapButton.setImageDrawable(getResources().getDrawable(R.drawable.map_selected, null));
                ft.replace(R.id.contentFrame, mViewMapFragment)
                        .addToBackStack(mCurrentPage.name())
                        .commit();
                mCurrentPage = Navbar.MAP;
                break;
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
    public void onBackPressed() {
        if (getFragmentManager().getBackStackEntryCount() > 0) {
            switchPage(mLastPage);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public void onHomeLoaded() {

    }

    @Override
    public void onAddBerryLoaded() {

    }

    @Override
    public void onViewMapLoaded() {

    }
}
