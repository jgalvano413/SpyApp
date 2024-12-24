package com.example.grabadorvoz.Service.BroadcastReceiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import com.example.grabadorvoz.Service.GrabacionService;

public class StopServiceReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        Intent serviceIntent = new Intent(context, GrabacionService.class);
        context.stopService(serviceIntent);
    }
}
