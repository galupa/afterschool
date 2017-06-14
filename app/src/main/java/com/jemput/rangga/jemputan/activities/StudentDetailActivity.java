package com.jemput.rangga.jemputan.activities;

import android.Manifest;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.NotificationCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.jemput.rangga.jemputan.R;
import com.jemput.rangga.jemputan.models.PickUpStatus;
import com.jemput.rangga.jemputan.models.Student;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class StudentDetailActivity extends AppCompatActivity {

    private TextView statusView;
    private TextView nameView;
    private TextView phoneView;
    private TextView parentView;
    private Button actionButton;
    private Button picButton;
    private Button callButon;
    private Button notesButon;
    private EditText notesText;
    private TextView parentNotesView;
    private ImageView pickerImage;
    private ImageView parentpickerImage;

    private DatabaseReference mRef;
    private DatabaseReference studentRef;
    private DatabaseReference parentRef;

    private DatabaseReference pickUpRef;

    private PickUpStatus pick = new PickUpStatus();

    private int status;

    private String pickId;

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_home:
                    home();
                    return true;
                case R.id.navigation_dashboard:
                    toParent();
                    return true;
                case R.id.navigation_notifications:
                    fromParent();
                    return true;
            }
            return false;
        }

    };

    private void home(){
        statusView.setVisibility(View.VISIBLE);
        nameView.setVisibility(View.VISIBLE);
        phoneView.setVisibility(View.VISIBLE);
        parentView.setVisibility(View.VISIBLE);
        actionButton.setVisibility(View.VISIBLE);
        picButton.setVisibility(View.INVISIBLE);
        callButon.setVisibility(View.VISIBLE);
        pickerImage.setVisibility(View.INVISIBLE);
        notesButon.setVisibility(View.INVISIBLE);
        notesText.setVisibility(View.INVISIBLE);
        parentNotesView.setVisibility(View.INVISIBLE);
        parentpickerImage.setVisibility(View.INVISIBLE);
    }

    private void toParent(){
        statusView.setVisibility(View.INVISIBLE);
        nameView.setVisibility(View.INVISIBLE);
        phoneView.setVisibility(View.INVISIBLE);
        parentView.setVisibility(View.INVISIBLE);
        actionButton.setVisibility(View.INVISIBLE);
        picButton.setVisibility(View.VISIBLE);
        callButon.setVisibility(View.INVISIBLE);
        pickerImage.setVisibility(View.VISIBLE);
        notesButon.setVisibility(View.VISIBLE);
        notesText.setVisibility(View.VISIBLE);
        parentNotesView.setVisibility(View.INVISIBLE);
        parentpickerImage.setVisibility(View.INVISIBLE);
    }
    private void fromParent(){
        statusView.setVisibility(View.INVISIBLE);
        nameView.setVisibility(View.INVISIBLE);
        phoneView.setVisibility(View.INVISIBLE);
        parentView.setVisibility(View.INVISIBLE);
        actionButton.setVisibility(View.INVISIBLE);
        picButton.setVisibility(View.INVISIBLE);
        callButon.setVisibility(View.INVISIBLE);
        pickerImage.setVisibility(View.INVISIBLE);
        parentNotesView.setVisibility(View.VISIBLE);
        notesButon.setVisibility(View.INVISIBLE);
        notesText.setVisibility(View.INVISIBLE);
        parentpickerImage.setVisibility(View.VISIBLE);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_detail);

        statusView = (TextView) findViewById(R.id.statusView);
        nameView = (TextView) findViewById(R.id.nameView);
        parentView = (TextView) findViewById(R.id.parentView);
        phoneView = (TextView) findViewById(R.id.phoneView);
        parentNotesView = (TextView) findViewById(R.id.parentNotesView);
        actionButton = (Button) findViewById(R.id.actionButton);
        picButton = (Button) findViewById(R.id.picButton);
        callButon = (Button) findViewById(R.id.callButton);
        notesButon = (Button) findViewById(R.id.notesButton);
        pickerImage = (ImageView) findViewById(R.id.pickerImage);
        parentpickerImage = (ImageView) findViewById(R.id.parentPickerImage);
        notesText = (EditText) findViewById(R.id.notesText);

        final BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        Bundle b = getIntent().getExtras();
        String parentId = new String();
        if (b != null) {
            parentId = b.getString("parentId");
        }

        Calendar c = Calendar.getInstance();

        SimpleDateFormat df = new SimpleDateFormat("dd-MMM-yyyy");
        String formattedDate = df.format(c.getTime());

        pick.setDate(formattedDate);
        pickId = parentId + formattedDate;
        mRef = FirebaseDatabase.getInstance().getReference();
        studentRef = mRef.child("students");
        parentRef = studentRef.child(parentId);

        pickUpRef = mRef.child("picks").child(pickId);

        parentRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    // dataSnapshot is the "issue" node with all children with id 0
                    Student student = dataSnapshot.getValue(Student.class);
                    updateStatus(student.getCurrentStatus());
                    nameView.setText(student.getName());
                    parentView.setText(student.getParentName());
                    phoneView.setText(student.getParentNumber());
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        pickUpRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String prevChildKey) {
                updateDate(dataSnapshot);
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String prevChildKey) {
                updateDate(dataSnapshot);
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
        notesButon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pick.setNotes(notesText.getText().toString());
                updatePickUp();
            }
        });
        picButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onLaunchCamera();
            }
        });

        callButon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                call(phoneView.getText().toString());
            }
        });
    }

    private void updateDate(DataSnapshot dataSnapshot){
        if(dataSnapshot.getKey().equals("pickUpStatus")) {
            updateStatus(dataSnapshot.getValue(Integer.class));
        }
        if (dataSnapshot.getKey().equals("notesFromParent")) {
            parentNotesView.setText(dataSnapshot.getValue().toString());
        }
        if (dataSnapshot.getKey().equals("fromParentPicId")) {
            if (!dataSnapshot.getValue().toString().contains("http")) {
                try {
                    Bitmap image = StudentStatusActivity.
                            decodeFromFirebaseBase64(dataSnapshot.getValue().toString());
                    parentpickerImage.setImageBitmap(image);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else {
                // This block of code should already exist, we're just moving it to the 'else' statement:
                Picasso.with(getBaseContext())
                        .load(dataSnapshot.getValue().toString())
                        .resize(100, 100)
                        .centerCrop()
                        .into(parentpickerImage);
            }
        }
    }

    private void updatePickUp(){
        pickUpRef.updateChildren(pick.updateParam(), new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(DatabaseError error, DatabaseReference reference) {
                Context context = getApplicationContext();
                if (error != null) {
                    Toast toast = Toast.makeText(context,
                            "Failed to update, please try again later",
                            Toast.LENGTH_LONG);
                    toast.show();
                }
            }
        });
    }

    public void onLaunchCamera() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(this.getPackageManager()) != null) {
            startActivityForResult(takePictureIntent, 111);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 111 && resultCode == this.RESULT_OK) {
            Bundle extras = data.getExtras();
            Bitmap imageBitmap = (Bitmap) extras.get("data");
            pickerImage.setImageBitmap(imageBitmap);
            encodeBitmapAndSaveToFirebase(imageBitmap);
        }
    }

    public void encodeBitmapAndSaveToFirebase(Bitmap bitmap) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
        String imageEncoded = Base64.encodeToString(baos.toByteArray(), Base64.DEFAULT);
        DatabaseReference ref = FirebaseDatabase.getInstance()
                .getReference("picks")
                .child(pickId)
                .child("picId");
        ref.setValue(imageEncoded);
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
        int mNotificationId = 001;
        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(this)
                        .setSmallIcon(R.drawable.com_facebook_button_icon)
                        .setContentTitle("My notification")
                        .setContentText("Hello World!");
        NotificationManager mNotifyMgr =
                (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        mNotifyMgr.notify(mNotificationId, mBuilder.build());

    }

    private void call(String number) {
        Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + number));
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            ActivityCompat.requestPermissions(
                    this,
                    new String[]{Manifest.permission.CALL_PHONE},
                    123);
            return;
        }
        startActivity(intent);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {

            case 123:
                if ((grantResults.length > 0) && (grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                    call(phoneView.getText().toString());
                } else {
                    Log.d("TAG", "Call Permission Not Granted");
                }
                break;

            default:
                break;
        }
    }

}
