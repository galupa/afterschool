package com.jemput.rangga.jemputan.activities;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.auth.IdpResponse;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.jemput.rangga.jemputan.R;
import com.jemput.rangga.jemputan.models.FirebaseReferences;
import com.jemput.rangga.jemputan.models.PickUpStatus;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class StudentStatusActivity extends AppCompatActivity {

    private Button actionButton;
    private Button rejectButton;
    private Button approveButton;
    private TextView studentView;
    private TextView classView;
    private TextView statusView;
    private TextView notesText;
    private ImageView kidImage;

    private int status;

    private DatabaseReference mRef;
    private DatabaseReference studentRef;
    private DatabaseReference parentRef;

    private DatabaseReference pickUpRef;

    private PickUpStatus pick = new PickUpStatus();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_status);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(StudentStatusActivity.this, NotifyTeacherActivity.class);
                startActivity(intent);
            }
        });

        final FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();

        actionButton = (Button) findViewById(R.id.actionButton);
        rejectButton = (Button) findViewById(R.id.rejectButton);
        approveButton = (Button) findViewById(R.id.approveButton);
        studentView = (TextView) findViewById(R.id.StudentView);
        classView = (TextView) findViewById(R.id.ClassView);
        statusView = (TextView) findViewById(R.id.statusView);
        kidImage = (ImageView) findViewById(R.id.pickerImage);
        notesText = (TextView) findViewById(R.id.parentNotesView);

        mRef = FirebaseDatabase.getInstance().getReference();
        studentRef = mRef.child(FirebaseReferences.STUDENTS);
        parentRef = studentRef.child(currentUser.getUid());

        Calendar c = Calendar.getInstance();

        SimpleDateFormat df = new SimpleDateFormat("dd-MMM-yyyy");
        String formattedDate = df.format(c.getTime());

        pick.setDate(formattedDate);

        parentRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    // dataSnapshot is the "issue" node with all children with id 0
                    for (DataSnapshot issue : dataSnapshot.getChildren()) {
                        if(issue.getKey().equals("name"))
                            studentView.setText(issue.getValue().toString());
                        if(issue.getKey().equals("group"))
                            classView.setText(issue.getValue().toString());
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        pickUpRef = mRef.child(FirebaseReferences.PICKS).child(currentUser.getUid() + formattedDate);

        pickUpRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    for (DataSnapshot issue : dataSnapshot.getChildren()) {
                        checkDataSnap(issue);
                    }
                } else {
                    pick.setPickUpStatus(PickUpStatus.AT_HOME);
                    updatePickUp();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        pickUpRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String prevChildKey) {
                checkDataSnap(dataSnapshot);
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String prevChildKey) {
                checkDataSnap(dataSnapshot);
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {}

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String prevChildKey) {}

            @Override
            public void onCancelled(DatabaseError databaseError) {}
        });


        actionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(status == PickUpStatus.AT_HOME){
                    pick.setPickUpStatus(PickUpStatus.AT_SCHOOL);
                } else if(status == PickUpStatus.AT_SCHOOL) {
                    pick.setPickUpStatus(PickUpStatus.PICKED_UP);
                } else if (status == PickUpStatus.READY_TO_PICK) {
                    pick.setPickUpStatus(PickUpStatus.PICKED_UP);
                } else if (status == PickUpStatus.PICKED_UP) {

                }
                updatePickUp();
            }
        });

        rejectButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pick.setPickerApproved(PickUpStatus.PICKER_REJECTED);
                updatePickUp();
                disableApproveReject();
            }
        });
        approveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pick.setPickerApproved(PickUpStatus.PICKER_APPROVED);
                updatePickUp();
                disableApproveReject();
            }
        });
    }

    private void disableApproveReject(){
        approveButton.setVisibility(View.INVISIBLE);
        rejectButton.setVisibility(View.INVISIBLE);
    }

    private void updatePickUp(){
        pickUpRef.updateChildren(pick.updateParam(), new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(DatabaseError error, DatabaseReference reference) {
                Context context = getApplicationContext();
                if (error != null) {
                    Toast toast = Toast.makeText(context,
                            "Failed to write register student, please try again later",
                            Toast.LENGTH_LONG);
                    toast.show();
                }
            }
        });
    }

    private void updateStatus(int val){
        status = val;
        pick.setPickUpStatus(val);
        actionButton.setEnabled(true);
        if(status == PickUpStatus.AT_HOME){
            statusView.setText("At home");
            actionButton.setText("Drop student at school");
        } else if(status == PickUpStatus.AT_SCHOOL) {
            statusView.setText("At school");
            actionButton.setText("Pick up student");
        } else if (status == PickUpStatus.READY_TO_PICK) {
            statusView.setText("Ready to be picked up");
            actionButton.setText("Pick up student");
        } else if (status == PickUpStatus.PICKED_UP) {
            statusView.setText("Picked up");
            actionButton.setEnabled(false);
        }
    }

    public static Intent createIntent(Context context, IdpResponse idpResponse) {
        Intent in = IdpResponse.getIntent(idpResponse);
        in.setClass(context, StudentStatusActivity.class);
        return in;
    }

    public static Bitmap decodeFromFirebaseBase64(String image) throws IOException {
        byte[] decodedByteArray = android.util.Base64.decode(image, Base64.DEFAULT);
        return BitmapFactory.decodeByteArray(decodedByteArray, 0, decodedByteArray.length);
    }
    private void checkDataSnap(DataSnapshot dataSnapshot){
            if (dataSnapshot.getKey().equals("pickUpStatus")) {
                updateStatus(dataSnapshot.getValue(Integer.class));
            }
            if (dataSnapshot.getKey().equals("notes")) {
                notesText.setVisibility(View.VISIBLE);
                notesText.setText(dataSnapshot.getValue().toString());
                rejectButton.setVisibility(View.VISIBLE);
                approveButton.setVisibility(View.VISIBLE);
            }
            if (dataSnapshot.getKey().equals("picId")) {
                kidImage.setVisibility(View.VISIBLE);
                rejectButton.setVisibility(View.VISIBLE);
                approveButton.setVisibility(View.VISIBLE);
                if (!dataSnapshot.getValue().toString().contains("http")) {
                    try {
                        Bitmap image = decodeFromFirebaseBase64(dataSnapshot.getValue().toString());
                        kidImage.setImageBitmap(image);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                } else {
                    // This block of code should already exist, we're just moving it to the 'else' statement:
                    Picasso.with(getBaseContext())
                            .load(dataSnapshot.getValue().toString())
                            .resize(100, 100)
                            .centerCrop()
                            .into(kidImage);
                }
            }
    }

}
