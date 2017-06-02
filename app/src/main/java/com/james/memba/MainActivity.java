package com.james.memba;

import android.Manifest;
import android.app.Activity;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.content.IntentSender;
import android.location.Location;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStates;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.james.memba.addberry.AddBerryFragment;
import com.james.memba.home.HomeFragment;
import com.james.memba.map.ViewMapFragment;
import com.james.memba.model.Account;
import com.james.memba.model.Berry;
import com.james.memba.services.MembaClient;
import com.james.memba.utils.PermissionUtils;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity implements HomeFragment.OnHomeLoadedListener,
        AddBerryFragment.OnAddBerryLoadedListener,
        ViewMapFragment.OnViewMapLoadedListener,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        ActivityCompat.OnRequestPermissionsResultCallback,
        PermissionUtils.PermissionResultCallback {

    private final static String TAG = "MAIN_ACTIVITY";
    private final static int PLAY_SERVICES_REQUEST = 1000;
    private final static int REQUEST_CHECK_SETTINGS = 2000;

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

    private ArrayList<String> permissions = new ArrayList<>();
    private PermissionUtils permissionUtils;
    private boolean mIsPermissionGranted;

    private Account mAccount;

    private ArrayList<Berry> mHomeBerries;

    private Location mLastLocation;
    private LocationRequest mLocationRequest;

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

        permissionUtils=new PermissionUtils(this);
        permissions.add(Manifest.permission.ACCESS_FINE_LOCATION);
        permissions.add(Manifest.permission.ACCESS_COARSE_LOCATION);
        permissionUtils.checkPermission(permissions, "Need GPS permission for getting your location",1);

        mClientId = getIntent().getStringExtra("SIGNIN_CLIENTID");
        mMembaClient = new MembaClient(mClientId);

        // Synchronized
        googleAPISetup();

        String email = getIntent().getStringExtra("SIGNIN_EMAIL");
        String userId = getIntent().getStringExtra("SIGNIN_ACCOUNTID");
        String token = getIntent().getStringExtra("SIGNIN_IDTOKEN");

        // Asynchronous callbacks
        mMembaClient.getAccount(userId, mGetAccountCB);
    }

    private synchronized void googleAPISetup() {
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestProfile()
                .requestEmail()
                .requestIdToken(mClientId)
                .build();

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();

        mGoogleApiClient.connect();

        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(10000);
        mLocationRequest.setFastestInterval(5000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder().addLocationRequest(mLocationRequest);

        PendingResult<LocationSettingsResult> result = LocationServices.SettingsApi.checkLocationSettings(mGoogleApiClient, builder.build());

        result.setResultCallback(new ResultCallback<LocationSettingsResult>() {
            @Override
            public void onResult(@NonNull LocationSettingsResult locationSettingsResult) {

                final Status status = locationSettingsResult.getStatus();

                switch (status.getStatusCode()) {
                    case LocationSettingsStatusCodes.SUCCESS:
                        // All location settings are satisfied. The client can initialize location requests here
                        getLocation();
                        break;
                    case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                        try {
                            // Show the dialog by calling startResolutionForResult(),
                            // and check the result in onActivityResult().
                            status.startResolutionForResult(MainActivity.this, REQUEST_CHECK_SETTINGS);
                        } catch (IntentSender.SendIntentException e) {
                            // Ignore the error.
                        }
                        break;
                    case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                        break;
                }
            }
        });
    }

    private void getLocation() {
        if (mIsPermissionGranted) {
            try {
                LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, new LocationListener() {
                    @Override
                    public void onLocationChanged(Location location) {
                        mLastLocation = location;
                        mViewMapFragment.updateLocation(mLastLocation);
                    }
                });
                mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
            } catch (SecurityException e) {
                e.printStackTrace();
            }
        }
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

        switch (n) {
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
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onStop() {
        if (mGoogleApiClient.isConnected()) {
            mGoogleApiClient.disconnect();
        }

        super.onStop();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case REQUEST_CHECK_SETTINGS:
                switch (resultCode) {
                    case Activity.RESULT_OK:
                        // All required changes were successfully made
                        getLocation();
                        break;
                    case Activity.RESULT_CANCELED:
                        // The user was asked to change settings, but chose not to
                        break;
                    default:
                        break;
                }
                break;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        // redirects to utils
        permissionUtils.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    public void onConnected(Bundle connectionHint) {
        getLocation();
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void PermissionGranted(int requestCode) {
        Log.i("PERMISSION","GRANTED");
        mIsPermissionGranted = true;
    }

    @Override
    public void PartialPermissionGranted(int requestCode, ArrayList<String> grantedPermissions) {
        Log.i("PERMISSION PARTIALLY","GRANTED");
    }

    @Override
    public void PermissionDenied(int requestCode) {
        Log.i("PERMISSION","DENIED");
    }

    @Override
    public void NeverAskAgain(int requestCode) {
        Log.i("PERMISSION","NEVER ASK AGAIN");
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
        mViewMapFragment.updateLocation(mLastLocation);
    }
}
