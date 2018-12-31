package com.safwat.abanoub.bridgeit;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class DoctorReportsActivity extends AppCompatActivity {

    LinearLayout searchLinear;
    EditText search_edittext;
    ImageButton add, search_btn;
    ListView listView;
    TextView noReports;
    String userType, clickedUserUID, clickedUserFullname, parentChildUID;
    DoctorReportsAdapter adapter;

    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;

    SharedPreferences sharedPreferences;

    private String user_uid, usedAsParent;
    private static final int GET_USER_UID_REQUEST_CODE = 5;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_doctor_reports);

        usedAsParent = getIntent().getStringExtra("usedAsParent");
        if (usedAsParent != null) //parent
            user_uid = getIntent().getStringExtra("clicked_user_uid");

        else //student or teacher
            user_uid = Utilities.getCurrentUID();

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(DoctorReportsActivity.this);
        //reading from shared preference
        userType = sharedPreferences.getString("userType", "not found");

        searchLinear = findViewById(R.id.searchLinear);
        search_edittext = findViewById(R.id.seach_edittext);
        search_btn = findViewById(R.id.search_btn);
        add = findViewById(R.id.add);
        listView = findViewById(R.id.list);
        noReports = findViewById(R.id.noReports);

        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference().child("doctorReports");

        if (userType.equals("Doctor")) {
            add.setVisibility(View.VISIBLE);
            searchLinear.setVisibility(View.VISIBLE);
        } else {
            add.setVisibility(View.GONE);
            searchLinear.setVisibility(View.GONE);
        }

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                ArrayList<DoctorReport> reversed_allDoctorReports_list = Utilities.getAllDoctorReports(dataSnapshot);
                final ArrayList<DoctorReport> allDoctorReports_list = reverseList(reversed_allDoctorReports_list);

                final ArrayList<Student> students_list = new ArrayList<>();

                for (int i = 0; i < allDoctorReports_list.size(); i++) {
                    final int position=i;

                    FirebaseDatabase.getInstance().getReference().child("students")
                            .child(allDoctorReports_list.get(position).studentUID)
                            .addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                                    students_list.add(Utilities.getStudent(dataSnapshot));

                                    if (position == allDoctorReports_list.size()-1)
                                        fillListView(allDoctorReports_list, students_list);
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {

                                }
                            });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        search_edittext.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (charSequence.toString().trim().length() == 0)
                    listView.setAdapter(adapter);
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        search_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String date = search_edittext.getText().toString();
                if (TextUtils.isEmpty(date)) {
                    listView.setAdapter(adapter);
                } else {
                    ArrayList<DoctorReport> list1 = adapter.doctorReports_list;
                    ArrayList<Student> list2 = adapter.students_list;
                    ArrayList<DoctorReport> newList1 = new ArrayList<>();
                    ArrayList<Student> newList2 = new ArrayList<>();

                    for (int i = 0; i < list1.size(); i++) {
                        if (list1.get(i).date.equalsIgnoreCase(date)) {
                            newList1.add(list1.get(i));
                            newList2.add(list2.get(i));
                        }
                    }
                    DoctorReportsAdapter searchAdapter = new DoctorReportsAdapter(DoctorReportsActivity.this, newList1, newList2);
                    listView.setAdapter(searchAdapter);
                }
            }
        });

        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(DoctorReportsActivity.this, ChooseUserActivity.class);
                intent.putExtra("activityType", "Doctor Reports");
                startActivityForResult(intent, GET_USER_UID_REQUEST_CODE);
            }
        });

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                DoctorReport doctorReport = (DoctorReport) adapterView.getAdapter().getItem(i);
                Student student = (Student) adapter.getItem2(i);

                Intent intent = new Intent(DoctorReportsActivity.this, DoctorReportsDetailedActivity.class);
                intent.putExtra("doctorReport", doctorReport);
                intent.putExtra("fullname", student.fullname);
                startActivity(intent);
            }
        });
    }

    private void fillListView(ArrayList<DoctorReport> allDoctorReports_list, ArrayList<Student> students_list) {

        if (userType.equals("Doctor") && usedAsParent == null)
            adapter = new DoctorReportsAdapter(this, allDoctorReports_list, students_list);

        else {
            ArrayList<DoctorReport> specificUserDoctorReports_list = new ArrayList<>();
            ArrayList<Student> specificUserStudents_list = new ArrayList<>();

            if (userType.equals("Student") || userType.equals("Parent") || usedAsParent != null) {

                for (int i = 0; i < students_list.size(); i++) {

                    if (allDoctorReports_list.get(i).studentUID.equals(user_uid)) {
                        specificUserDoctorReports_list.add(allDoctorReports_list.get(i));
                        specificUserStudents_list.add(students_list.get(i));
                    }
                }
                adapter = new DoctorReportsAdapter(this, specificUserDoctorReports_list, specificUserStudents_list);
            }
//            else { //parent
//                parentChildUID = sharedPreferences.getString("parentChildUID", "not found");
//
//                if (!parentChildUID.equals("not found")) {
//
//                    for (int i = 0; i < allDoctorReports_list.size(); i++) {
//                        if (allDoctorReports_list.get(i).studentUID.equals(parentChildUID)) {
//                            specificUserDoctorReports_list.add(allDoctorReports_list.get(i));
//                            specificUserStudents_list.add(students_list.get(i));
//                        }
//                    }
//                    adapter = new DoctorReportsAdapter(this, specificUserDoctorReports_list, specificUserStudents_list);
//                }
//            }
        }
        if (adapter != null) {
            if (adapter.getCount() == 0)
                noReports.setVisibility(View.VISIBLE);
            else {
                if (noReports.getVisibility() == View.VISIBLE)
                    noReports.setVisibility(View.GONE);

                listView.setAdapter(adapter);
            }
        }
    }

    private ArrayList<DoctorReport> reverseList(ArrayList<DoctorReport> reversed_competitions_list) {
        ArrayList<DoctorReport> reversed_list = new ArrayList<>();

        for (int i = reversed_competitions_list.size() - 1; i >= 0; i--) {
            reversed_list.add(reversed_competitions_list.get(i));
        }

        return reversed_list;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == GET_USER_UID_REQUEST_CODE && resultCode == RESULT_OK) {

            if (data != null) {
                clickedUserUID = data.getExtras().getString("clickedUserUID");
                clickedUserFullname = data.getExtras().getString("clickedUserFullname");
            }
            if (clickedUserUID != null) {
                // Create and show the dialog and pass the user as an argument with static method.
                DialogFragment dialog = AddDoctorReportDialogFragment.newInstance(clickedUserUID, clickedUserFullname);
                dialog.show(getSupportFragmentManager(), "tag");
            }
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        // Unregister VisualizerActivity as an OnPreferenceChangedListener to avoid any memory leaks.
        PreferenceManager.getDefaultSharedPreferences(this);
    }
}
