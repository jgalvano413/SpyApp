package com.example.grabadorvoz.Service.BroadcastReceiver;

import static androidx.core.content.ContextCompat.getSystemService;

import android.app.ActivityManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import com.example.grabadorvoz.Service.GrabacionService;
import com.example.grabadorvoz.Service.photoService;
import com.example.grabadorvoz.Service.videoRecording;

public class StopServiceReceiver extends BroadcastReceiver {
    private Context context;
    @Override
    public void onReceive(Context context, Intent intent) {
        this.context = context;
        if (isServiceRunning(GrabacionService.class)) {
            Intent serviceIntent = new Intent(context, GrabacionService.class);
            context.stopService(serviceIntent);
        } else if (isServiceRunning(photoService.class)){
            Intent serviceIntent = new Intent(context, photoService.class);
            context.stopService(serviceIntent);
        }else {
            Intent serviceIntent = new Intent(context, videoRecording.class);
            context.stopService(serviceIntent);
        }
    }

    private boolean isServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }
}
