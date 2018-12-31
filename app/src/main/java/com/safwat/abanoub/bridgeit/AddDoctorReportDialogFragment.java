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
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by Abanoub on 2018-04-27.
 */

public class AddDoctorReportDialogFragment extends DialogFragment {

    TextView username_textView;
    AutoCompleteTextView title, suggestedMedicine;
    Spinner diseaseDuration;
    Button send_btn;

    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference;
    private String clickedUserUID, clickedUserFullname;

    /**
     * Create a new instance of MyDialogFragment, to pass "newUser" as an argument to this dialog.
     * <p>
     * 1. newInstance to make instance of your DialogFragment
     * 2. setter to initialize your object
     * 3. and add setRetainInstance(true); in onCreate
     */
    static AddDoctorReportDialogFragment newInstance(String clickedUserUID, String clickedUserFullname) {

        AddDoctorReportDialogFragment f = new AddDoctorReportDialogFragment();

        // Supply input as an argument.
//        Bundle args = new Bundle();
//        args.putSerializable("clickedUserUID", clickedUserUID);
//        f.setArguments(args);
        f.setClickedUserUID(clickedUserUID, clickedUserFullname);

        return f;
    }

    private void setClickedUserUID(String clickedUserUID, String clickedUserFullname) {
        this.clickedUserUID = clickedUserUID;
        this.clickedUserFullname = clickedUserFullname;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        //Create your dialog here

        setRetainInstance(true);

        Dialog dialog = new Dialog(getContext(), R.style.DialogTheme);
        dialog.setContentView(R.layout.add_doctor_report_dialog);

        return dialog;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.add_doctor_report_dialog, container, false);

        username_textView = view.findViewById(R.id.username);
        title = view.findViewById(R.id.title);
        suggestedMedicine = view.findViewById(R.id.suggestedMedicine);
        diseaseDuration = view.findViewById(R.id.diseaseDuration);
        send_btn = (Button) view.findViewById(R.id.send_btn);

        username_textView.setText(clickedUserFullname);

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getContext()
                , android.R.layout.simple_spinner_item, getResources().getStringArray(R.array.diseaseDuration));
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        diseaseDuration.setAdapter(adapter);

        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference().child("doctorReports");

        title.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (charSequence.toString().trim().length() > 0 && TextUtils.isEmpty(suggestedMedicine.getText()) == false)
                    send_btn.setEnabled(true);
                else
                    send_btn.setEnabled(false);
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });

        suggestedMedicine.addTextChangedListener(new TextWatcher() {
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
                    if (TextUtils.isEmpty(title.getText()) || TextUtils.isEmpty(suggestedMedicine.getText()))
                        Toast.makeText(getContext(), R.string.fields_cannot_be_empty, Toast.LENGTH_SHORT).show();
                    else {
                        Date date = Calendar.getInstance().getTime();
                        SimpleDateFormat df = new SimpleDateFormat("dd-MMM-yyyy");
                        String formattedDate = df.format(date);

                        DoctorReport doctorReport = new DoctorReport(clickedUserUID, title.getText().toString()
                                , suggestedMedicine.getText().toString(), diseaseDuration.getSelectedItem().toString(), formattedDate);
                        String pushID = databaseReference.push().getKey();
                        databaseReference.child(pushID).setValue(doctorReport);

                        Toast.makeText(getContext(), "Report sent successfully", Toast.LENGTH_SHORT).show();
                        dismiss();
                    }
                } else
                    Toast.makeText(getContext(), R.string.check_internet_connection, Toast.LENGTH_SHORT).show();
            }
        });

        return view;
    }
}
