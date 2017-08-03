package com.myapps.svatts.geotodos;

import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.Places;

import static com.myapps.svatts.geotodos.MainActivity.mClient;

/**
 * Created by svatts on 01-Aug-17.
 */

public class AddingGeofencesService extends Service implements GoogleApiClient.ConnectionCallbacks,GoogleApiClient.OnConnectionFailedListener {
    private PendingIntent mGeofencePendingIntent;

    public Geofencing mGeofencing;
   // public static GoogleApiClient mClient;

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        mClient = new GoogleApiClient.Builder(this)

                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .addApi(Places.GEO_DATA_API)
                .addApi(Places.PLACE_DETECTION_API)
                .build();

        mClient.connect();

        return null;
    }


    public void onConnected(Bundle bundle) {
        //Add geofences
        mGeofencing = new Geofencing(this, mClient);
        mGeofencing.registerAllGeofences();

        Intent intent = new Intent(this, GeofenceBroadcastReceiver.class);
        //Intent intent = new Intent("com.aol.android.geofence.ACTION_RECEIVE_GEOFENCE");
        mGeofencePendingIntent = PendingIntent.getBroadcast(this, 0, intent, PendingIntent.
                FLAG_UPDATE_CURRENT);


    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }
}
