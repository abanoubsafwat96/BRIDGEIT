package com.safwat.abanoub.bridgeit;

import android.app.Dialog;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by Abanoub on 2018-04-27.
 */

public class AddComplaintDialogFragment extends DialogFragment {

    AutoCompleteTextView title, message;
    Button send_btn;

    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference;

    String userType;

    /**
     * Create a new instance of MyDialogFragment, to pass "newUser" as an argument to this dialog.
     * <p>
     * 1. newInstance to make instance of your DialogFragment
     * 2. setter to initialize your object
     * 3. and add setRetainInstance(true); in onCreate
     * @param userType
     */
    static AddComplaintDialogFragment newInstance(String userType) {

        AddComplaintDialogFragment f = new AddComplaintDialogFragment();
        f.setUserType(userType);
        return f;
    }

    private void setUserType(String userType) {
        this.userType=userType;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        //Create your dialog here

        setRetainInstance(true);

        Dialog dialog = new Dialog(getContext(), R.style.DialogTheme);
        dialog.setContentView(R.layout.add_complaint_dialog);

        return dialog;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.add_complaint_dialog, container, false);

        title = view.findViewById(R.id.title);
        message = view.findViewById(R.id.message);
        send_btn = (Button) view.findViewById(R.id.send_btn);

        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference().child("complaints");

        title.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (charSequence.toString().trim().length() > 0 && TextUtils.isEmpty(message.getText()) == false)
                    send_btn.setEnabled(true);
                else
                    send_btn.setEnabled(false);
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });

        message.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (charSequence.toString().trim().length() > 0 && TextUtils.isEmpty(title.getText()) == false)
                    send_btn.setEnabled(true);
                else
                    send_btn.setEnabled(false);
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });

        send_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Utilities.isNetworkAvailable(getContext())) {
                    if (TextUtils.isEmpty(title.getText()) || TextUtils.isEmpty(message.getText()))
                        Toast.makeText(getContext(), R.string.fields_cannot_be_empty, Toast.LENGTH_SHORT).show();
                    else {
                        Date date = Calendar.getInstance().getTime();
                        SimpleDateFormat df = new SimpleDateFormat("dd-MMM-yyyy");
                        String formattedDate = df.format(date);


                        Complaint complaint = null;
                        
                        if (userType.equals("Teacher"))
                            complaint= new Complaint(Utilities.getCurrentUID(), title.getText().toString()
                                , message.getText().toString(), "not seen", formattedDate,"teachers");
                        
                        else if (userType.equals("Parent"))
                            complaint= new Complaint(Utilities.getCurrentUID(), title.getText().toString()
                                    , message.getText().toString(), "not seen", formattedDate,"parents");
                        
                        else if (userType.equals("Doctor"))
                            complaint= new Complaint(Utilities.getCurrentUID(), title.getText().toString()
                                    , message.getText().toString(), "not seen", formattedDate,"doctors");
                        
                        else if (userType.equals("PE"))
                            complaint= new Complaint(Utilities.getCurrentUID(), title.getText().toString()
                                    , message.getText().toString(), "not seen", formattedDate,"PEs");
                        
                        else if (userType.equals("Bus Admin"))
                            complaint= new Complaint(Utilities.getCurrentUID(), title.getText().toString()
                                    , message.getText().toString(), "not seen", formattedDate,"busAdmins");
                        
                        String pushID = databaseReference.push().getKey();
                        databaseReference.child(pushID).setValue(complaint);

                        Toast.makeText(getContext(), "Complaint sent successfully", Toast.LENGTH_SHORT).show();
                        dismiss();
                    }
                } else
                    Toast.makeText(getContext(), R.string.check_internet_connection, Toast.LENGTH_SHORT).show();
            }
        });

        return view;
    }
}
