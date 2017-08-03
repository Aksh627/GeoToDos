package com.myapps.svatts.geotodos;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * Created by svatts on 01-Aug-17.
 */

public class BootBroadcastReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        Intent startServiceIntent = new Intent(context, AddingGeofencesService.class);
        context.startService(startServiceIntent);
    }
}
