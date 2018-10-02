package com.mtw.rkj.articlemanager;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;

public class PermissionsRequest {

    private Activity activity;
    private String[] permissions;
    private int requestCode;
    private String reRequestText;
    boolean always;

    public PermissionsRequest(Activity activity, int requestCode, String permission){
        this(activity, requestCode, new String[]{permission});
    }

    public PermissionsRequest(Activity activity, int requestCode, String[] permissions){
        this.activity = activity;
        this.permissions = permissions;
        this.requestCode = requestCode;
        this.reRequestText = "";
        this.always = true;
    }

    private boolean askPermissions(){
        for (String permission : permissions){
            if (ContextCompat.checkSelfPermission(activity, permission) != PackageManager.PERMISSION_GRANTED) {
                return false;
            }
        }
        return true;
    }

    private boolean shouldRequestAgain(){
        for (String permission : permissions){
            if (ActivityCompat.shouldShowRequestPermissionRationale(activity, permission)) {
                return true;
            }
        }
        return false;
    }

    public PermissionsRequest onlyOnce(){
        always = true;
        return this;
    }

    public PermissionsRequest always(){
        always = false;
        return this;
    }

    public PermissionsRequest addReAskText(String text){
        this.reRequestText = text;
        return this;
    }

    /**
     * Ask for permission
     * @return true if the permission or all permissions were already granted
     */
    public boolean ask(){
        if (!askPermissions()){
            if (shouldRequestAgain()){
                if (!always) {
                    new AlertDialog.Builder(activity)
                        .setTitle(R.string.permissions)
                        .setMessage(reRequestText)
                            .setNeutralButton(R.string.action_accept, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    ActivityCompat.requestPermissions(activity, permissions, requestCode);
                                }
                            })
                        .show();
                }
            }
            else {
                ActivityCompat.requestPermissions(activity, permissions, requestCode);
            }
            return false;
        }
        return true;
    }

}
