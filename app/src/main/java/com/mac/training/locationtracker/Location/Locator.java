package com.mac.training.locationtracker.Location;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.support.v4.app.ActivityCompat;
import android.util.Log;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;

/**
 * Created by User on 9/12/2016.
 */
public class Locator {

    private static final String LOG = Locator.class.getName();
    // getting the user's location for initializing the map camera
    private static GoogleApiClient mGoogleApiClient;

    // variable to hold context
    private static Context context;

    //save the context recievied via constructor in a local variable
    public Locator(Context context) {
        this.context = context;
        mGoogleApiClient = new GoogleApiClient.Builder(context)
                .addApi(LocationServices.API).build();
    }

    public static final Location getCurrentLocation() {
        if (ActivityCompat.checkSelfPermission(context,
                android.Manifest.permission.ACCESS_FINE_LOCATION) !=
                PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(context,
                        android.Manifest.permission.ACCESS_COARSE_LOCATION) !=
                        PackageManager.PERMISSION_GRANTED) {
            return null;
        }
        Location l = LocationServices
                .FusedLocationApi
                .getLastLocation(mGoogleApiClient);
        Log.d(LOG, "" + l.getAltitude() + l.getLatitude() + l.getTime());
        return l;
    }

}
