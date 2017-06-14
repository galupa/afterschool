package com.jemput.rangga.jemputan.activities;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.jemput.rangga.jemputan.R;
import com.jemput.rangga.jemputan.models.PickUpStatus;

import java.io.ByteArrayOutputStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class NotifyTeacherActivity extends AppCompatActivity {

    private Button picButton;
    private Button notesButon;
    private EditText notesText;
    private ImageView pickerImage;

    private DatabaseReference mRef;
    private DatabaseReference pickUpRef;

    private PickUpStatus pick = new PickUpStatus();
    private String pickId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notify_teacher);

        picButton = (Button) findViewById(R.id.picButton);
        notesButon = (Button) findViewById(R.id.notesButton);
        pickerImage = (ImageView) findViewById(R.id.pickerImage);
        notesText = (EditText) findViewById(R.id.parentNotesView);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        final FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        String parentId = currentUser.getUid();
        Calendar c = Calendar.getInstance();

        SimpleDateFormat df = new SimpleDateFormat("dd-MMM-yyyy");
        String formattedDate = df.format(c.getTime());

        pick.setDate(formattedDate);
        pickId = parentId + formattedDate;
        mRef = FirebaseDatabase.getInstance().getReference();
        pickUpRef = mRef.child("picks").child(pickId);


        notesButon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pick.setNotesFromParent(notesText.getText().toString());
                updatePickUp();
            }
        });
        picButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onLaunchCamera();
            }
        });

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
                .child("fromParentPicId");
        ref.setValue(imageEncoded);
    }

}
