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
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.james.memba.addberry.AddEntryFragment;
import com.james.memba.addberry.CreateBerryFragment;
import com.james.memba.home.HomeFragment;
import com.james.memba.map.ViewBerryDialogFragment;
import com.james.memba.map.ViewMapFragment;
import com.james.memba.model.Account;
import com.james.memba.model.Berry;
import com.james.memba.model.BerryLocation;
import com.james.memba.model.Entry;
import com.james.memba.services.ImgurClient;
import com.james.memba.services.MembaClient;
import com.james.memba.utils.LocationUtil;
import com.james.memba.utils.PermissionUtils;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

import static com.james.memba.utils.KeyUtil.GoogleClientId;

public class MainActivity extends AppCompatActivity implements HomeFragment.HomeListener,
        AddEntryFragment.AddEntryListener,
        CreateBerryFragment.CreateBerryListener,
        ViewMapFragment.ViewMapListener,
        ViewBerryDialogFragment.ViewBerryListener,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        ActivityCompat.OnRequestPermissionsResultCallback,
        PermissionUtils.PermissionResultCallback {

    private final static int REQUEST_CHECK_SETTINGS = 2000;

    // Fragments
    private HomeFragment mHomeFragment;
    private CreateBerryFragment mCreateBerryFragment;
    private AddEntryFragment mAddEntryFragment;
    private ViewMapFragment mViewMapFragment;

    // Navbar
    private ImageButton mHomeButton;
    private ImageButton mAddButton;
    private ImageButton mMapButton;
    private enum Navbar {HOME, ADD, CREATE, MAP}
    private Navbar mCurrentPage = Navbar.HOME;
    private Navbar mLastPage = Navbar.HOME;

    // Clients
    private GoogleApiClient mGoogleApiClient;
    private MembaClient mMembaClient;
    private ImgurClient mImgurClient;

    // Data
    private ArrayList<String> permissions = new ArrayList<>();
    private PermissionUtils permissionUtils;
    private boolean mIsPermissionGranted;
    private Account mAccount;
    private Location mLastLocation;
    private LocationRequest mLocationRequest;

    // Callbacks
    // When account information has been retrieved (or not), update the interface
    private Callback mGetAccountCB = new Callback() {
        @Override
        public void onFailure(@NonNull final Call call, @NonNull IOException e) {
            // Error
        }

        @Override
        public void onResponse(@NonNull Call call, @NonNull final Response response) throws java.io.IOException {
            String data = response.body().string();

            try {
                Gson gson = new Gson();
                mAccount = gson.fromJson(data, Account.class);
                if (response.isSuccessful()) {
                    // Retrieve account information
                    mMembaClient.getAccountBerries(mAccount, mGetAccountBerriesCB);
                    String username = getIntent().getStringExtra("SIGNIN_DISPLAYNAME");
                    if (!mAccount.getUsername().equals(username)) {
                        // update user account
                        mAccount.setUsername(username);
                        mMembaClient.updateAccountUsername(mAccount);
                    }
                } else {
                    // Create new account
                    mMembaClient.createAccount(mAccount);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    };
    // When account berries have been retrieved, update the home page
    private Callback mGetAccountBerriesCB = new Callback() {
        @Override
        public void onFailure(@NonNull Call call, @NonNull IOException e) {

        }

        @Override
        public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
            final String data = response.body().string();

            try {
                if (response.isSuccessful()) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Type listType = new TypeToken<List<Berry>>() {}.getType();
                            try {
                                Gson gson = new GsonBuilder().create();
                                List<Berry> berries = gson.fromJson(data, listType);
                                mHomeFragment.showBerries(new ArrayList<>(berries));
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
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
    // When a berry has been retrieved, show the user in a dialogfragment
    private Callback getBerryCB = new Callback() {
        @Override
        public void onFailure(@NonNull Call call, @NonNull IOException e) {

        }

        @Override
        public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
            final String data = response.body().string();

            try {
                if (response.isSuccessful()) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Type type = new TypeToken<Berry>() {}.getType();
                            try {
                                Gson gson = new GsonBuilder().create();
                                Berry berry = gson.fromJson(data, type);

                                FragmentManager fm = getFragmentManager();
                                ViewBerryDialogFragment viewBerry = ViewBerryDialogFragment.newInstance(berry);
                                viewBerry.show(fm, viewBerry.getTag());
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    });
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    };
    // When all berries have been retrieved, show them on the map
    private Callback getBerriesCB = new Callback() {
        @Override
        public void onFailure(@NonNull Call call, @NonNull IOException e) {

        }

        @Override
        public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
            final String data = response.body().string();

            try {
                if (response.isSuccessful()) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Type listType = new TypeToken<List<BerryLocation>>() {}.getType();
                            try {
                                Gson gson = new GsonBuilder().create();
                                List<BerryLocation> locations = gson.fromJson(data, listType);
                                mViewMapFragment.showBerries(new ArrayList<>(locations));
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    });
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    };
    // When a berry has been created, retrieve the account berries to show them on home page
    private Callback createBerryCB = new Callback() {
        @Override
        public void onFailure(@NonNull Call call, @NonNull IOException e) {

        }

        @Override
        public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
            mMembaClient.getAccountBerries(mAccount, mGetAccountBerriesCB);
        }
    };
    // When a berry has been updated, retrieve the account berries to show them on home page
    private Callback updateBerryCB = new Callback() {
        @Override
        public void onFailure(@NonNull Call call, @NonNull IOException e) {

        }

        @Override
        public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
            mMembaClient.getAccountBerries(mAccount, mGetAccountBerriesCB);
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
        mCreateBerryFragment = CreateBerryFragment.newInstance();
        mViewMapFragment = ViewMapFragment.newInstance();
        switchPage(Navbar.HOME);

        // Permissions
        permissionUtils = new PermissionUtils(this);
        permissions.add(Manifest.permission.ACCESS_FINE_LOCATION);
        permissions.add(Manifest.permission.ACCESS_COARSE_LOCATION);
        permissionUtils.checkPermission(permissions, "Need GPS permission for getting your location", 1);

        mMembaClient = new MembaClient(GoogleClientId);
        mImgurClient = new ImgurClient();

        // Synchronized
        googleAPISetup();

        // Get account information from Google+
        //String email = getIntent().getStringExtra("SIGNIN_EMAIL");
        String userId = getIntent().getStringExtra("SIGNIN_ACCOUNTID");
        //String token = getIntent().getStringExtra("SIGNIN_IDTOKEN");

        // Get account details
        mMembaClient.getAccount(userId, mGetAccountCB);
    }

    private synchronized void googleAPISetup() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();

        mGoogleApiClient.connect();

        // Get current location, needs to have high accuracy or it won't work
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(10000);
        mLocationRequest.setFastestInterval(5000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        // Ask user for permissions
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

    // Retrieves last known location as long as user has allowed locations
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
                if (mCurrentPage != Navbar.CREATE) {
                    switchPage(Navbar.CREATE);
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

        // Destroy the AddEntry fragment when user is not using it anymore (pressed back or switched
        // tabs)
        if (n != Navbar.ADD && mAddEntryFragment != null) {
            getFragmentManager().beginTransaction().detach(mAddEntryFragment).commit();
            mAddEntryFragment = null;
        }

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
            case CREATE:
                mHomeButton.setImageDrawable(getResources().getDrawable(R.drawable.home_unselected, null));
                mAddButton.setImageDrawable(getResources().getDrawable(R.drawable.add_selected, null));
                mMapButton.setImageDrawable(getResources().getDrawable(R.drawable.map_unselected, null));
                ft.replace(R.id.contentFrame, mCreateBerryFragment)
                        .addToBackStack(mCurrentPage.name())
                        .commit();

                mCurrentPage = Navbar.CREATE;
                break;
            case ADD:
                mHomeButton.setImageDrawable(getResources().getDrawable(R.drawable.home_unselected, null));
                mAddButton.setImageDrawable(getResources().getDrawable(R.drawable.add_selected, null));
                mMapButton.setImageDrawable(getResources().getDrawable(R.drawable.map_unselected, null));
                ft.replace(R.id.contentFrame, mAddEntryFragment)
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
        if (!mGoogleApiClient.isConnected()) {
            mGoogleApiClient.connect();
        }

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

    // Callback for when home fragment has loaded
    @Override
    public void onHomeLoaded() {

    }

    // Callback for when "ADD" was pressed in AddEntry fragment
    // Switches page to AddEntry, shows "ADD" as the menu option
    @Override
    public void onAddEntryTo(Berry berry) {
        mAddEntryFragment = AddEntryFragment.newInstance(berry);
        switchPage(Navbar.ADD);
    }

    // Callback for when add entry fragment has loaded
    @Override
    public void onAddEntryLoaded() {

    }

    // Callback for when create berry fragment has loaded
    @Override
    public void onCreateBerryLoaded() {
        getLocation();
        String location = LocationUtil.getAddress(this, mLastLocation);
        mCreateBerryFragment.showBerryHeader(mAccount.getUsername(), location);
    }

    // Callback for when "CREATE" was pressed in Create fragment
    @Override
    public void onCreateBerry(final Berry berry) {
        berry.setUserId(mAccount.getUserId());
        berry.setUsername(mAccount.getUsername());
        berry.setLocation(mLastLocation);
        final Entry entry = berry.getEntries().get(0);
        if (entry.getImage() != null) {
            final File image = new File(entry.getImage());
            // update entry with imgur url
            new Thread(new Runnable() {
                @Override
                public void run() {
                    entry.setImage(mImgurClient.postImage(image));
                    berry.getEntries().set(0, entry);
                    mMembaClient.createBerry(berry, createBerryCB);
                }
            }).start();
        } else {
            mMembaClient.createBerry(berry, createBerryCB);
        }
        switchPage(Navbar.HOME);
    }

    // Callback for when "ADD" was pressed in the AddEntry fragment
    // Attempts to upload an image if there is one, and updates the database
    @Override
    public void onUpdateBerry(final String berryId, final Entry entry) {
        if (entry.getImage() != null) {
            final File image = new File(entry.getImage());
            // update entry with imgur url
            new Thread(new Runnable() {
                @Override
                public void run() {
                    entry.setImage(mImgurClient.postImage(image));
                    mMembaClient.updateBerry(berryId, entry, updateBerryCB);
                }
            }).start();
        } else {
            mMembaClient.updateBerry(berryId, entry, updateBerryCB);
        }
        switchPage(Navbar.HOME);
    }

    // Callback for when the map fragment has loaded
    // Gets the berry locations and updates the fragment
    @Override
    public void onViewMapLoaded() {
        mViewMapFragment.updateLocation(mLastLocation);
        mMembaClient.getBerries(getBerriesCB);
    }

    // Callback for when a berry on the map was clicked, shows the berry in a dialog fragment
    @Override
    public void onMarkerClicked(String berryId) {
        mMembaClient.getBerry(berryId, getBerryCB);
    }

    // Callback for when "My Location" was clicked, updates the current location and moves camera
    @Override
    public void onMyLocationClicked() {
        getLocation();
        mViewMapFragment.updateLocation(mLastLocation);
    }
}
