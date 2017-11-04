package com.example.lmachillot.ths;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

/**
 * Created by Leonard on 23/07/2017.
 */
public class RestartAlarmsReceiver  extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        if ("android.intent.action.BOOT_COMPLETED".equals(intent.getAction())) {

            // It is better to reset alarms using Background IntentService
            Intent i = new Intent(context, BootService.class);

            ComponentName service = context.startService(i);

            if (null == service) {
                // something really wrong here
                Log.e("PB-----", "Could not start service ");
            }
            else {
                Log.e("OK+++++", "Successfully started service ");
            }

        } else {
            Log.e("PB+++++", "Received unexpected intent " + intent.toString());
        }
    }

}
