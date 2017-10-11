package com.jemput.middup.jemputan.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.jemput.middup.jemputan.R;
import com.jemput.middup.jemputan.models.PickUpStatus;
import com.jemput.middup.jemputan.models.Student;
import com.jemput.middup.jemputan.models.Students;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

/**
 * A fragment representing a list of Items.
 * <p/>
 * Activities containing this fragment MUST implement the {@link OnListFragmentInteractionListener}
 * interface.
 */
public class StudentsFragment extends Fragment {

    // TODO: Customize parameter argument names
    private static final String ARG_COLUMN_COUNT = "column-count";
    // TODO: Customize parameters
    private int mColumnCount = 1;
    private OnListFragmentInteractionListener mListener;
    private Students students = new Students();

    private DatabaseReference mRef;
    private DatabaseReference studentRef;
    private DatabaseReference statusRef;

    private MyStudentsRecyclerViewAdapter mAdapter;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public StudentsFragment() {
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        // This makes sure that the container activity has implemented
        // the callback interface. If not, it throws an exception
        try {
            mListener = (OnListFragmentInteractionListener) getActivity();
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString()
                    + " must implement OnListFragmentInteractionListener");
        }
    }

    // TODO: Customize parameter initialization
    @SuppressWarnings("unused")
    public static StudentsFragment newInstance(int columnCount) {
        StudentsFragment fragment = new StudentsFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_COLUMN_COUNT, columnCount);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            mColumnCount = getArguments().getInt(ARG_COLUMN_COUNT);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_students_list, container, false);
        initList();

        // Set the adapter
        if (view instanceof RecyclerView) {
            mAdapter = new MyStudentsRecyclerViewAdapter(students.ITEMS, mListener);
            Context context = view.getContext();
            RecyclerView recyclerView = (RecyclerView) view;
            if (mColumnCount <= 1) {
                recyclerView.setLayoutManager(new LinearLayoutManager(context));
            } else {
                recyclerView.setLayoutManager(new GridLayoutManager(context, mColumnCount));
            }
            recyclerView.setAdapter(mAdapter);
        }
        return view;
    }



    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnListFragmentInteractionListener {
        // TODO: Update argument type and name
        void onListFragmentInteraction(Student item);
    }

    private void initList(){
        mRef = FirebaseDatabase.getInstance().getReference();
        studentRef = mRef.child("students");
        statusRef = mRef.child("picks");

        studentRef.orderByChild("approved").equalTo(true).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String prevChildKey) {
                Student student = dataSnapshot.getValue(Student.class);
                students.addItem(student);
                mAdapter.notifyDataSetChanged();
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String prevChildKey) {
                Student student = dataSnapshot.getValue(Student.class);
                students.addItem(student);
                mAdapter.notifyDataSetChanged();
                System.out.println("Previous Post ID: " + student.getGroup());
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                Log.d("DEBUG", "removed");
                Student student = dataSnapshot.getValue(Student.class);
                students.removeItem(student);
                mAdapter.notifyDataSetChanged();
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String prevChildKey) {}

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.d("DEBUG", databaseError.getDetails());
            }
        });

        Calendar c = Calendar.getInstance();

        SimpleDateFormat df = new SimpleDateFormat("dd-MMM-yyyy");
        String formattedDate = df.format(c.getTime());

        statusRef.orderByChild("date").equalTo(formattedDate).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String prevChildKey) {
                editStatus(dataSnapshot);
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String prevChildKey) {
                editStatus(dataSnapshot);
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String prevChildKey) {}

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.d("DEBUG", databaseError.getDetails());
            }
        });
    }
    private void editStatus(DataSnapshot dataSnapshot){
        PickUpStatus status = dataSnapshot.getValue(PickUpStatus.class);
        Calendar c = Calendar.getInstance();

        SimpleDateFormat df = new SimpleDateFormat("dd-MMM-yyyy");
        String formattedDate = df.format(c.getTime());

        String key = dataSnapshot.getKey().replace(formattedDate, "");

        Student student = new Student();
        student.setCurrentStatus(status.getPickUpStatus());
        student.setParentId(key);
        students.editItem(student);
        mAdapter.notifyDataSetChanged();
    }
    public void SchoolOver(){
        System.out.println("test");
        Calendar c = Calendar.getInstance();
        SimpleDateFormat df = new SimpleDateFormat("dd-MMM-yyyy");
        String formattedDate = df.format(c.getTime());
        for(int i = 0; i < students.ITEMS.size(); i++) {
            Student s = students.ITEMS.get(i);
            System.out.println(s.getName());
            System.out.println(s.getCurrentStatus());
            if (s.getCurrentStatus() == PickUpStatus.AT_SCHOOL) {
                System.out.println(s.getParentId()+ formattedDate);
                Map newPickUpStatus = new HashMap();
                newPickUpStatus.put("pickUpStatus", PickUpStatus.READY_TO_PICK);
                statusRef.child(s.getParentId()+ formattedDate)
                        .updateChildren(newPickUpStatus, new DatabaseReference.CompletionListener() {
                    @Override
                    public void onComplete(DatabaseError error, DatabaseReference reference) {
                        System.out.println(error);
                    }
                });
            }
        }
    }
}
