package com.myapps.svatts.geotodos;

/*
* Copyright (C) 2017 The Android Open Source Project
*
* Licensed under the Apache License, Version 2.0 (the "License");
* you may not use this file except in compliance with the License.
* You may obtain a copy of the License at
*
*  	http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
*/


import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.PlaceBuffer;

public class PlaceListAdapter extends RecyclerView.Adapter<PlaceListAdapter.PlaceViewHolder> implements android.location.LocationListener {

    private static final long LOCATION_REFRESH_TIME = 2000;
    private static final float LOCATION_REFRESH_DISTANCE = 100;
    private static final int MY_PERMISSION_ACCESS_COURSE_LOCATION = 1001;
    private Context mContext;
    private PlaceBuffer mPlaces;
    PlaceViewHolder myHolder;

    String dist;

    private FusedLocationProviderClient mFusedLocationClient;
    private LocationRequest mFusedLocationRquest;
    private LocationCallback mLocationCallback;
    public Location updatedLoc;

    /**
     * Constructor using the context and the db cursor
     *
     * @param context the calling context/activity
     */
    public PlaceListAdapter(Context context, PlaceBuffer places) {
        this.mContext = context;
        this.mPlaces = places;

    }


    /**
     * Called when RecyclerView needs a new ViewHolder of the given type to represent an item
     *
     * @param parent   The ViewGroup into which the new View will be added
     * @param viewType The view type of the new View
     * @return A new PlaceViewHolder that holds a View with the item_place_card layout
     */
    @Override
    public PlaceViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // Get the RecyclerView item layout
        LayoutInflater inflater = LayoutInflater.from(mContext);
        View view = inflater.inflate(R.layout.item_place_card, parent, false);

        return new PlaceViewHolder(view);
    }

    /**
     * Binds the data from a particular position in the cursor to the corresponding view holder
     *
     * @param holder   The PlaceViewHolder instance corresponding to the required position
     * @param position The current position that needs to be loaded with data
     */
    @Override
    public void onBindViewHolder(PlaceViewHolder holder, int position) {

        myHolder = holder;
        String placeName = mPlaces.get(position).getName().toString();
        String placeAddress = mPlaces.get(position).getAddress().toString();

        double lat = mPlaces.get(position).getLatLng().latitude;
        double lng = mPlaces.get(position).getLatLng().longitude;


        if (ActivityCompat.checkSelfPermission(mContext, android.Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions((Activity) mContext,
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                    101);
        }

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(mContext);


        createLocationCallback();
        createLocationRequest();

        mFusedLocationClient.requestLocationUpdates(mFusedLocationRquest, mLocationCallback, null);


        Location des = new Location("destination");

        des.setLatitude(lat);
        des.setLongitude(lng);


        if (updatedLoc != null) {
            float distance = updatedLoc.distanceTo(des);
            distance = distance / 1000;
            dist = "";

            if (distance < 1) {
                dist = "less than km away";
            } else {
                dist = String.format("%.2f", distance) + " km";
            }
        } else {
            dist = "Waiting for location...";
        }

        //Location currentLocation = getCurrentLocation();

        //Location currLoc = new GPSTracker(mContext).getLocation();

        //Toast.makeText(mContext, "distance is" + dist + "km", Toast.LENGTH_SHORT).show();
        //  updatedLoc = mySimpleCallback.getLocation();

        holder.nameTextView.setText(placeName);
        holder.addressTextView.setText(dist);

        holder.taskTextView.setText(MainActivity.preference.getString(mPlaces.get(position).getId(), "chv"));
    }

    public void swapPlaces(PlaceBuffer newPlaces) {
        mPlaces = newPlaces;

        if(mPlaces==null)
        {
            Log.d("mPlaceNull","true");
            this.notifyDataSetChanged();
            return;
        }
        if(mPlaces.getCount()==0)
        {
            Log.d("MplaceCountZeroBro","true");
            this.notifyDataSetChanged();
        }
        if (mPlaces != null) {
            // Force the RecyclerView to refresh
            Log.d("notNullBro","true");
            this.notifyDataSetChanged();

        }
    }

    /**
     * Returns the number of items in the cursor
     *
     * @return Number of items in the cursor, or 0 if null
     */
    @Override
    public int getItemCount() {
        if (mPlaces == null) {
            Log.d("countcheck", "zero bro");
            return 0;
        }
        return mPlaces.getCount();

    }


    @Override
    public void onLocationChanged(Location location) {
        updatedLoc = location;
    }

    @Override
    public void onStatusChanged(String s, int i, Bundle bundle) {

    }

    @Override
    public void onProviderEnabled(String s) {

    }

    @Override
    public void onProviderDisabled(String s) {

    }


    /**
     * PlaceViewHolder class for the recycler view item
     */
    class PlaceViewHolder extends RecyclerView.ViewHolder {

        TextView nameTextView;
        TextView addressTextView;
        TextView taskTextView;

        public PlaceViewHolder(View itemView) {
            super(itemView);
            nameTextView = (TextView) itemView.findViewById(R.id.name_text_view);
            addressTextView = (TextView) itemView.findViewById(R.id.address_text_view);
            taskTextView = (TextView) itemView.findViewById(R.id.task_text_view);
        }

    }

    private void createLocationRequest() {
        mFusedLocationRquest = new LocationRequest();

        // Sets the desired interval for active location updates. This interval is
        // inexact. You may not receive updates at all if no location sources are available, or
        // you may receive them slower than requested. You may also receive updates faster than
        // requested if other applications are requesting location at a faster interval.
        mFusedLocationRquest.setInterval(100);

        // Sets the fastest rate for active location updates. This interval is exact, and your
        // application will never receive updates faster than this value.
        mFusedLocationRquest.setFastestInterval(100);

        mFusedLocationRquest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

    }

    /**
     * Creates a callback for receiving location events.
     */
    private void createLocationCallback() {
        mLocationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                super.onLocationResult(locationResult);

                updatedLoc = locationResult.getLastLocation();

                Log.d("testingvalue", String.valueOf(updatedLoc == null));
                //mLastUpdateTime = DateFormat.getTimeInstance().format(new Date());
                //  updateLocationUI();

                notifyDataSetChanged();

            }
        };
    }

}
