package com.james.memba.utils;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class PermissionUtils {
    private Activity mActivity;
    private PermissionResultCallback mPermissionResultCallback;
    private ArrayList<String> mPermissionList = new ArrayList<>();
    private ArrayList<String> mListPermissionsNeeded = new ArrayList<>();
    private String mDialogContent ="";
    private int mReqCode;

    public PermissionUtils(Activity activity) {
        this.mActivity = activity;
        mPermissionResultCallback = (PermissionResultCallback) activity;
    }

    /**
     * Check the API Level & Permission
     *
     * @param permissions
     * @param dialog_content
     * @param request_code
     */
    public void checkPermission(ArrayList<String> permissions, String dialog_content, int request_code) {
        this.mPermissionList = permissions;
        this.mDialogContent = dialog_content;
        this.mReqCode = request_code;

        if(Build.VERSION.SDK_INT >= 23) {
            if (checkAndRequestPermissions(permissions, request_code)) {
                mPermissionResultCallback.PermissionGranted(request_code);
                Log.i("all permissions", "granted");
                Log.i("proceed", "to callback");
            }
        } else {
            mPermissionResultCallback.PermissionGranted(request_code);
            Log.i("all permissions", "granted");
            Log.i("proceed", "to callback");
        }
    }

    /**
     * Check and request the Permissions
     *
     * @param permissions
     * @param request_code
     * @return
     */
    private boolean checkAndRequestPermissions(ArrayList<String> permissions,int request_code) {
        if(permissions.size()>0) {
            mListPermissionsNeeded = new ArrayList<>();
            for(int i = 0; i < permissions.size(); i++) {
                int hasPermission = ContextCompat.checkSelfPermission(mActivity,permissions.get(i));
                if (hasPermission != PackageManager.PERMISSION_GRANTED) {
                    mListPermissionsNeeded.add(permissions.get(i));
                }
            }

            if (!mListPermissionsNeeded.isEmpty()) {
                ActivityCompat.requestPermissions(mActivity, mListPermissionsNeeded.toArray(new String[mListPermissionsNeeded.size()]),request_code);
                return false;
            }
        }

        return true;
    }

    /**
     * @param requestCode
     * @param permissions
     * @param grantResults
     */
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case 1:
                if(grantResults.length>0) {
                    Map<String, Integer> perms = new HashMap<>();
                    for (int i = 0; i < permissions.length; i++) {
                        perms.put(permissions[i], grantResults[i]);
                    }

                    final ArrayList<String> pendingPermissions=new ArrayList<>();
                    for (int i = 0; i < mListPermissionsNeeded.size(); i++) {
                        if (perms.get(mListPermissionsNeeded.get(i)) != PackageManager.PERMISSION_GRANTED) {
                            if(ActivityCompat.shouldShowRequestPermissionRationale(mActivity, mListPermissionsNeeded.get(i))) {
                                pendingPermissions.add(mListPermissionsNeeded.get(i));
                            } else {
                                Log.i("Go to settings","and enable permissions");
                                mPermissionResultCallback.NeverAskAgain(mReqCode);
                                Toast.makeText(mActivity, "Go to settings and enable permissions", Toast.LENGTH_LONG).show();
                                return;
                            }
                        }
                    }

                    if(pendingPermissions.size()>0) {
                        showMessageOKCancel(mDialogContent, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                switch (which) {
                                    case DialogInterface.BUTTON_POSITIVE:
                                        checkPermission(mPermissionList, mDialogContent, mReqCode);
                                        break;
                                    case DialogInterface.BUTTON_NEGATIVE:
                                        Log.i("permisson","not fully given");
                                        if(mPermissionList.size() == pendingPermissions.size()) {
                                            mPermissionResultCallback.PermissionDenied(mReqCode);
                                        } else {
                                            mPermissionResultCallback.PartialPermissionGranted(mReqCode, pendingPermissions);
                                        }
                                        break;
                                }
                            }
                        });
                    } else {
                        Log.i("all","permissions granted");
                        Log.i("proceed","to next step");
                        mPermissionResultCallback.PermissionGranted(mReqCode);
                    }
                }
                break;
        }
    }

    /**
     * Explain why the app needs permissions
     *
     * @param message
     * @param okListener
     */
    private void showMessageOKCancel(String message, DialogInterface.OnClickListener okListener) {
        new AlertDialog.Builder(mActivity)
                .setMessage(message)
                .setPositiveButton("Ok", okListener)
                .setNegativeButton("Cancel", okListener)
                .create()
                .show();
    }

    public interface PermissionResultCallback {
        void PermissionGranted(int request_code);
        void PartialPermissionGranted(int request_code, ArrayList<String> granted_permissions);
        void PermissionDenied(int request_code);
        void NeverAskAgain(int request_code);
    }
}
