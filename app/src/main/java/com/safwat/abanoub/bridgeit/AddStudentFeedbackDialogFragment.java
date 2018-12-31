package com.safwat.abanoub.bridgeit;

import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by Abanoub on 2018-04-27.
 */

public class AddStudentFeedbackDialogFragment extends DialogFragment {

    TextView username;
    AutoCompleteTextView note;
    Button add_btn;
    String feedbackType,studentUID,studentFullname,userType,current_userName;

    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference personalDataReference,schoolarReference,medicalReference,activityReference;

    /**
     * Create a new instance of MyDialogFragment, to pass "newUser" as an argument to this dialog.
     * <p>
     * 1. newInstance to make instance of your DialogFragment
     * 2. setter to initialize your object
     * 3. and add setRetainInstance(true); in onCreate
     */
    static AddStudentFeedbackDialogFragment newInstance(String feedbackType, String studentUID, String studentFullname
            ,String userType) {

        AddStudentFeedbackDialogFragment f = new AddStudentFeedbackDialogFragment();
        f.setFeedbackType(feedbackType,studentUID,studentFullname,userType);
        return f;
    }

    private void setFeedbackType(String feedbackType, String studentUID, String studentFullname,String userType) {
        this.feedbackType=feedbackType;
        this.studentUID=studentUID;
        this.studentFullname=studentFullname;
        this.userType=userType;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        //Create your dialog here

        setRetainInstance(true);

        Dialog dialog = new Dialog(getContext(), R.style.DialogTheme);
        dialog.setContentView(R.layout.add_student_feedback_dialog);

        return dialog;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.add_student_feedback_dialog, container, false);

        username=view.findViewById(R.id.username);
        note = view.findViewById(R.id.note);
        add_btn = (Button) view.findViewById(R.id.add_btn);

        username.setText(studentFullname);

        firebaseDatabase = FirebaseDatabase.getInstance();

        String current_user_uid=Utilities.getCurrentUID();
        if (userType.equals("Student"))
            personalDataReference = firebaseDatabase.getReference().child("students").child(current_user_uid);
        else if (userType.equals("Teacher"))
            personalDataReference = firebaseDatabase.getReference().child("teachers").child(current_user_uid);
        else if (userType.equals("Parent"))
            personalDataReference = firebaseDatabase.getReference().child("parents").child(current_user_uid);
        else if (userType.equals("Doctor"))
            personalDataReference = firebaseDatabase.getReference().child("doctors").child(current_user_uid);
        else if (userType.equals("PE"))
            personalDataReference = firebaseDatabase.getReference().child("PEs").child(current_user_uid);
        else if (userType.equals("Bus Admin"))
            personalDataReference = firebaseDatabase.getReference().child("bus").child(current_user_uid);
        else if (userType.equals("Manager"))
            personalDataReference = firebaseDatabase.getReference().child("managers").child(current_user_uid);

        personalDataReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                current_userName = Utilities.getUserFullName(dataSnapshot);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        if (feedbackType.equals("schoolar"))
            schoolarReference=firebaseDatabase.getReference().child("feedback").child("schoolar").child(studentUID);
        else if (feedbackType.equals("medical"))
            medicalReference=firebaseDatabase.getReference().child("feedback").child("medical").child(studentUID);
        else if (feedbackType.equals("activity"))
            activityReference=firebaseDatabase.getReference().child("feedback").child("activity").child(studentUID);

        note.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (charSequence.toString().trim().length() > 0 )
                    add_btn.setEnabled(true);
                else
                    add_btn.setEnabled(false);
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });

        add_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Utilities.isNetworkAvailable(getContext())) {
                    if (TextUtils.isEmpty(note.getText()))
                        Toast.makeText(getContext(), R.string.fields_cannot_be_empty, Toast.LENGTH_SHORT).show();
                    else {
                        Date date = Calendar.getInstance().getTime();
                        SimpleDateFormat df = new SimpleDateFormat("dd-MMM-yyyy");
                        String formattedDate = df.format(date);

                        StudentFeedback studentFeedback=new StudentFeedback(note.getText().toString(),current_userName,formattedDate);
                        String pushID;

                        if (feedbackType.equals("schoolar")){
                            pushID = schoolarReference.push().getKey();
                            schoolarReference.child(pushID).setValue(studentFeedback);

                        } else if (feedbackType.equals("medical")){
                            pushID = medicalReference.push().getKey();
                            medicalReference.child(pushID).setValue(studentFeedback);

                        } else if (feedbackType.equals("activity")){
                            pushID = activityReference.push().getKey();
                            activityReference.child(pushID).setValue(studentFeedback);
                        }

                        Toast.makeText(getContext(), "Feedback added successfully", Toast.LENGTH_SHORT).show();
                        dismiss();
                    }
                } else
                    Toast.makeText(getContext(), R.string.check_internet_connection, Toast.LENGTH_SHORT).show();
            }
        });

        return view;
    }
}
