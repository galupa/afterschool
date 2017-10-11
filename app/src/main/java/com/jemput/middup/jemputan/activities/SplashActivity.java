package com.jemput.middup.jemputan.activities;

import android.app.ActivityManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.firebase.ui.auth.IdpResponse;
import com.google.android.gms.auth.api.Auth;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.jemput.middup.jemputan.R;
import com.jemput.middup.jemputan.auth.AuthUiActivity;
import com.jemput.middup.jemputan.models.Student;
import com.jemput.middup.jemputan.services.OngoingNotificationService;

public class SplashActivity extends AppCompatActivity {

    private DatabaseReference mRef;
    private DatabaseReference teacherRef;
    private DatabaseReference adminRef;
    private DatabaseReference parentRef;

    private boolean isMyServiceRunning() {
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (OngoingNotificationService.class.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        checkDataAndAction();
    }

    public void checkDataAndAction(){
        FirebaseAuth auth = FirebaseAuth.getInstance();
        if (auth.getCurrentUser() != null) {
            Intent intent = new Intent(this, OngoingNotificationService.class);
            startService(intent);
            Log.i("s", "start");
            //startActivity(SignedInActivity.createIntent(this, null));
            mRef = FirebaseDatabase.getInstance().getReference();
            teacherRef = mRef.child("teachers");
            adminRef = teacherRef.child(auth.getCurrentUser().getUid());
            parentRef = mRef.child("students").child(auth.getCurrentUser().getUid());
            Log.i("saaaaaaaaa", auth.getCurrentUser().getUid());
            adminRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        startActivity(TeacherDashboardActivity.createIntent(SplashActivity.this, null));
                    } else {
                        parentRef.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                if(dataSnapshot.exists()) {
                                    Student student = dataSnapshot.getValue(Student.class);
                                    if(student.isApproved()) {
                                        startActivity(StudentStatusActivity.createIntent(SplashActivity.this, null));
                                        finish();
                                        return;
                                    }
                                }
                                startActivity(RegisterStudentActivity.createIntent(SplashActivity.this, null));
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });

                    }
                    finish();
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        } else {
            startActivity(AuthUiActivity.createIntent(SplashActivity.this));
        }

    }

    public static Intent createIntent(Context context, IdpResponse idpResponse) {
        Intent in = IdpResponse.getIntent(idpResponse);
        in.setClass(context, SplashActivity.class);
        return in;
    }
    private BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            /*System.out.println("sana");
            Toast toast = Toast.makeText(context,
                    "Failed to update, please try again later",
                    Toast.LENGTH_LONG);
            toast.show();*/
        }
    };

    @Override
    public void onResume() {
        super.onResume();
        IntentFilter mIntentFilter = new IntentFilter();
        mIntentFilter.addAction("test");
        registerReceiver(mReceiver, mIntentFilter);
    }
}
