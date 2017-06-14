package com.jemput.rangga.jemputan.services;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * Created by asus on 5/22/2017.
 */

public class OngoingNotificationServiceStarter extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        Intent i = new Intent(context, OngoingNotificationService.class);
        context.startService(i);
        System.out.println("notifications");
    }
}
