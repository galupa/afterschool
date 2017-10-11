package com.jemput.middup.jemputan.activities;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.MainThread;
import android.support.annotation.StringRes;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
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
import com.jemput.middup.jemputan.R;
import com.jemput.middup.jemputan.auth.AuthUiActivity;
import com.jemput.middup.jemputan.auth.SignedInActivity;
import com.jemput.middup.jemputan.models.Student;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;

public class RegisterStudentActivity extends AppCompatActivity {

    private Button registerStudentButton;
    private EditText studentNameText;
    private Spinner schoolSpinner;
    private Spinner classSpinner;
    private EditText phoneText;
    private EditText parentNameText;

    private DatabaseReference mRef;
    private DatabaseReference studentRef;
    private DatabaseReference parentRef;
    private String SCHOOL_CHOOSE = "---Please Choose School----";
    private String CLASS_CHOOSE = "---Please Choose Class----";

    private static final String TAG = "RecyclerViewDemo";
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_student);

        registerStudentButton = (Button) findViewById(R.id.registerStudentButton);
        schoolSpinner = (Spinner) findViewById(R.id.schoolSpinner);
        classSpinner = (Spinner) findViewById(R.id.classSpinner);
        phoneText = (EditText) findViewById(R.id.phoneText);
        studentNameText = (EditText) findViewById(R.id.studentNameText);
        parentNameText = (EditText) findViewById(R.id.parentNameText);

        final FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser == null) {
            startActivity(AuthUiActivity.createIntent(this));
            finish();
            return;
        }

        List<String> list = new ArrayList<String>();
        list.add(SCHOOL_CHOOSE);
        list.add("TK BPI");
        list.add("SD BPI");
        final ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, list);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        List<String> list2 = new ArrayList<String>();
        list2.add(CLASS_CHOOSE);
        list2.add("0 kecil");
        list2.add("0 besar");
        list2.add("1 A");
        final ArrayAdapter<String> dataAdapter2 = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, list2);
        dataAdapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        schoolSpinner.setAdapter(dataAdapter);
        classSpinner.setAdapter(dataAdapter2);

        mRef = FirebaseDatabase.getInstance().getReference();
        studentRef = mRef.child("students");
        parentRef = studentRef.child(currentUser.getUid());
        parentRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    // dataSnapshot is the "issue" node with all children with id 0
                    for (DataSnapshot issue : dataSnapshot.getChildren()) {
                        if(issue.getKey().equals("name"))
                            studentNameText.setText(issue.getValue().toString());
                        if(issue.getKey().equals("parentNumber"))
                            phoneText.setText(issue.getValue().toString());
                        if(issue.getKey().equals("parentName"))
                            parentNameText.setText(issue.getValue().toString());
                        if(issue.getKey().equals("group"))
                            classSpinner.setSelection(dataAdapter2.getPosition(issue.getValue().toString()));
                        if(issue.getKey().equals("school"))
                            schoolSpinner.setSelection(dataAdapter.getPosition(issue.getValue().toString()));
                        if(issue.getKey().equals("approved") && issue.getValue().equals(true)) {
                            Intent intent = new Intent(RegisterStudentActivity.this, SplashActivity.class);
                            startActivity(intent);
                        }
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        parentRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String prevChildKey) {
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String prevChildKey) {
                if(dataSnapshot.getKey().equals("approved") && dataSnapshot.getValue().equals(true)) {
                    Intent intent = new Intent(RegisterStudentActivity.this, SplashActivity.class);
                    startActivity(intent);
                }
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {}

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String prevChildKey) {}

            @Override
            public void onCancelled(DatabaseError databaseError) {}
        });
        registerStudentButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String parentId = currentUser.getUid();
                final Context context = getApplicationContext();
                if(classSpinner.getSelectedItem().toString().equals(CLASS_CHOOSE)) {
                    Toast toast = Toast.makeText(context,
                            "Please choose class",
                            Toast.LENGTH_LONG);
                    toast.show();
                    return;
                }
                if(schoolSpinner.getSelectedItem().toString().equals(SCHOOL_CHOOSE)) {
                    Toast toast = Toast.makeText(context,
                            "Please choose school",
                            Toast.LENGTH_LONG);
                    toast.show();
                    return;
                }
                Student student = new Student(studentNameText.getText().toString(),
                        classSpinner.getSelectedItem().toString(),
                        schoolSpinner.getSelectedItem().toString(), parentId,
                        parentId, phoneText.getText().toString(), parentNameText.getText().toString());
                parentRef.setValue(student, new DatabaseReference.CompletionListener() {
                    @Override
                    public void onComplete(DatabaseError error, DatabaseReference reference) {

                        if (error != null) {
                            Toast toast = Toast.makeText(context,
                                    "Failed to write register student, please try again later",
                                    Toast.LENGTH_LONG);
                            toast.show();
                            Log.e(TAG, "Failed to write register student", error.toException());
                        } else {
                            Toast toast = Toast.makeText(context,
                                    getString(R.string.register_submited), Toast.LENGTH_LONG);
                            toast.show();
                        }
                    }
                });

            }
        });

    }
    public static Intent createIntent(Context context, IdpResponse idpResponse) {
        Intent in = IdpResponse.getIntent(idpResponse);
        in.setClass(context, RegisterStudentActivity.class);
        return in;
    }

}
