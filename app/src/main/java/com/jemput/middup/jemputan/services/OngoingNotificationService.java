package com.jemput.middup.jemputan.services;

import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.jemput.middup.jemputan.R;
import com.jemput.middup.jemputan.models.PickUpStatus;
import com.jemput.middup.jemputan.models.Student;

import java.text.SimpleDateFormat;
import java.util.Calendar;

/**
 * Created by asus on 5/22/2017.
 */

public class OngoingNotificationService extends Service {

    private static DatabaseReference mRef, pickUpRef;
    private static boolean isRunning = false;
    private static int status;
    private static PickUpStatus pick = new PickUpStatus();
    private static String pickId;
    private static String parentId;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);
        return Service.START_STICKY;

    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        // Check if notification should be shown and do so if needed
        FirebaseAuth auth = FirebaseAuth.getInstance();
        mRef = FirebaseDatabase.getInstance().getReference();
        parentId = auth.getCurrentUser().getUid();
        if(!isRunning){
            new MyThread().start();
            isRunning = true;
        }

    }

    private void task() {
        Calendar c = Calendar.getInstance();
        SimpleDateFormat df = new SimpleDateFormat("dd-MMM-yyyy");
        String formattedDate = df.format(c.getTime());

        pick.setDate(formattedDate);
        pickId = parentId + formattedDate;
        pickUpRef = mRef.child("picks").child(pickId);
        pickUpRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    // dataSnapshot is the "issue" node with all children with id 0
                    PickUpStatus s = dataSnapshot.getValue(PickUpStatus.class);
                    if(status != s.getPickUpStatus()) {
                        status = s.getPickUpStatus();
                        if (status != PickUpStatus.AT_HOME) {
                            Student student = new Student();
                            student.setCurrentStatus(status);
                            publishResults(student.getStatusString());
                        }
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
    private void publishResults(String message) {
        Intent intent = new Intent();
        intent.setAction("test");
        intent.putExtra("MESSAGE", message);
        sendBroadcast(intent);
        int mNotificationId = 002;
        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(this)
                        .setSmallIcon(R.drawable.logo_middup)
                        .setContentTitle("Middup Notification")
                        .setContentText("Your kid is now " + message);
        NotificationManager mNotifyMgr =
                (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        mNotifyMgr.notify(mNotificationId, mBuilder.build());
    }

    class MyThread extends Thread{
        @Override
        public void run(){
            while(isRunning){
                long DELAY = 30000;
                try {
                    task();
                    int hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY);
                    if (hour > 18 || hour < 10) {
                        DELAY = 3600000;
                    }
                    Thread.sleep(DELAY);
                } catch (InterruptedException e) {
                    isRunning = false;
                    e.printStackTrace();
                }
            }
        }

    }
}
