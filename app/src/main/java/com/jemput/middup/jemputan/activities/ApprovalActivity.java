package com.jemput.middup.jemputan.activities;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.jemput.middup.jemputan.R;
import com.jemput.middup.jemputan.models.FirebaseReferences;
import com.jemput.middup.jemputan.models.IntentExtras;
import com.jemput.middup.jemputan.models.Student;

import butterknife.BindView;

public class ApprovalActivity extends AppCompatActivity {

    private Button approvalButton;
    private Button callButton;
    private TextView schoolNameView;
    private TextView studentNameView;
    private TextView parentNameView;
    private TextView parentPhoneView;

    private DatabaseReference mRef;
    private DatabaseReference studentRef;
    private DatabaseReference parentRef;

    private Student student;

    private final int callId = 123;
    private final String TAG = "Approval Activity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_approval);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        approvalButton = (Button) findViewById(R.id.ApproveButton);
        callButton = (Button) findViewById(R.id.CallButton);
        schoolNameView = (TextView) findViewById(R.id.SchoolNameView);
        studentNameView = (TextView) findViewById(R.id.StudentNameView);
        parentNameView = (TextView) findViewById(R.id.ParentNameView);
        parentPhoneView = (TextView) findViewById(R.id.ParentPhoneView);

        Bundle b = getIntent().getExtras();
        String parentId = new String();
        if (b != null) {
            parentId = b.getString(IntentExtras.PARENT_ID);
        }


        mRef = FirebaseDatabase.getInstance().getReference();
        studentRef = mRef.child(FirebaseReferences.STUDENTS);
        parentRef = studentRef.child(parentId);

        parentRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                student = dataSnapshot.getValue(Student.class);
                studentNameView.setText(student.getName());
                schoolNameView.setText(student.getSchool());
                parentNameView.setText(student.getParentName());
                parentPhoneView.setText(student.getParentNumber());
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        approvalButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                student.setApproved(true);
                parentRef.setValue(student, new DatabaseReference.CompletionListener() {
                    @Override
                    public void onComplete(DatabaseError error, DatabaseReference reference) {
                        Context context = getApplicationContext();
                        if (error != null) {
                            Toast toast = Toast.makeText(context,
                                    "Failed to approve student, please try again later",
                                    Toast.LENGTH_LONG);
                            toast.show();
                            Log.e("TAG", "Failed to write register student", error.toException());
                        } else {
                            Toast toast = Toast.makeText(context,
                                    getString(R.string.register_approved), Toast.LENGTH_LONG);
                            toast.show();
                            approvalButton.setEnabled(false);
                        }
                    }
                });

            }
        });

        callButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                call(parentPhoneView.getText().toString());
            }
        });

    }

    private void call(String number) {
        Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + number));
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                    this,
                    new String[]{Manifest.permission.CALL_PHONE},
                    callId);
            return;
        }
        startActivity(intent);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {

            case callId:
                if ((grantResults.length > 0) && (grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                    call(parentPhoneView.getText().toString());
                } else {
                    Log.d(TAG, "Call Permission Not Granted");
                }
                break;

            default:
                break;
        }
    }

}
