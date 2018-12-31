package com.safwat.abanoub.bridgeit;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class Grades1Activity extends AppCompatActivity {

    Spinner yearSpinner, classSpinner;
    Button addGrades_btn, showGrades_btn;
    LinearLayout spinnersLinear, buttonsLinear;
    View line;
    ListView listView;
    TextView noSubjects;
    private ArrayList<String> yearsList, classesList;
    private String userType;
    Classroom classroom;

    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;
    private ArrayList<String> sameClassStudentUIDs_list;
    String user_uid, user_fullname;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_grades1);

        final String usedAsParent = getIntent().getStringExtra("usedAsParent");
        if (usedAsParent != null) //parent
            user_uid = getIntent().getStringExtra("clicked_user_uid");

        else //student or teacher
            user_uid = Utilities.getCurrentUID();

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(Grades1Activity.this);
        //reading from shared preference
        userType = sharedPreferences.getString("userType", "not found");

        yearSpinner = findViewById(R.id.yearSpinner);
        classSpinner = findViewById(R.id.classSpinner);
        addGrades_btn = findViewById(R.id.addGrades_btn);
        showGrades_btn = findViewById(R.id.showGrades_btn);
        spinnersLinear = findViewById(R.id.spinnersLinear);
        buttonsLinear = findViewById(R.id.buttonsLinear);
        line = findViewById(R.id.line);
        listView = findViewById(R.id.list);
        noSubjects = findViewById(R.id.noSubjects);

        firebaseDatabase = FirebaseDatabase.getInstance();

        if (userType.equals("Student") || usedAsParent != null || userType.equals("Parent")) {
            spinnersLinear.setVisibility(View.GONE);
            buttonsLinear.setVisibility(View.GONE);
            line.setVisibility(View.GONE);

            databaseReference = firebaseDatabase.getReference().child("students").child(user_uid)
                    .child("class").child("id");
            databaseReference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                    String studentClassID = Utilities.getStudentClassID(dataSnapshot);

                    firebaseDatabase.getReference().child("Classes").child(studentClassID)
                            .addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                    classroom = Utilities.getClassroom(dataSnapshot);

                                    firebaseDatabase.getReference().child("Years").child(classroom.yearName)
                                            .child(classroom.className).child("subjects").addValueEventListener(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                                            ArrayList<String> list = Utilities.getAllSubjects(dataSnapshot);
                                            fillListView(list);
                                        }

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError databaseError) {

                                        }
                                    });

                                    firebaseDatabase.getReference().child("Years").child(classroom.yearName).child(classroom.className)
                                            .child("students").addValueEventListener(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                                            sameClassStudentUIDs_list = Utilities.getUIDs(dataSnapshot);
                                        }

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError databaseError) {

                                        }
                                    });
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {

                                }
                            });
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });

            firebaseDatabase.getReference().child("students").child(user_uid).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    user_fullname = Utilities.getUserFullName(dataSnapshot);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });

        } else {
            final DataBaseAdapter helper = new DataBaseAdapter(this);

            yearsList = helper.getAllYears();

            final ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, yearsList);
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            yearSpinner.setAdapter(adapter);

            yearSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {

                    String selectedYear = (String) parentView.getAdapter().getItem(position);
                    classesList = helper.getAllClassesFromYear(selectedYear);

                    ArrayAdapter<String> adapter2 = new ArrayAdapter<>(Grades1Activity.this
                            , android.R.layout.simple_spinner_item, classesList);
                    adapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    classSpinner.setAdapter(adapter2);
                }

                @Override
                public void onNothingSelected(AdapterView<?> parentView) {

                }

            });

            classSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                    databaseReference = firebaseDatabase.getReference().child("Years").child(yearSpinner.getSelectedItem().toString())
                            .child(classSpinner.getSelectedItem().toString()).child("subjects");
                    databaseReference.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            ArrayList<String> list = Utilities.getAllSubjects(dataSnapshot);
                            fillListView(list);
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });

                }

                @Override
                public void onNothingSelected(AdapterView<?> adapterView) {

                }
            });
        }

        addGrades_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addGrades_btn.setEnabled(false);
                addGrades_btn.setBackgroundColor(getResources().getColor(R.color.grey_buttons));
                showGrades_btn.setEnabled(true);
                showGrades_btn.setBackgroundColor(getResources().getColor(R.color.grey_toolbar));
            }
        });

        showGrades_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addGrades_btn.setEnabled(true);
                addGrades_btn.setBackgroundColor(getResources().getColor(R.color.grey_toolbar));
                showGrades_btn.setEnabled(false);
                showGrades_btn.setBackgroundColor(getResources().getColor(R.color.grey_buttons));
            }
        });

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                Intent intent = null;
                if (userType.equals("Student") || usedAsParent != null || userType.equals("Parent")) {
                    intent = new Intent(Grades1Activity.this, GradesGraphActivity.class);
                    intent.putExtra("classroom", classroom);
                    intent.putExtra("subject", (String) adapterView.getAdapter().getItem(i));
                    intent.putExtra("uids_list", sameClassStudentUIDs_list);
                    intent.putExtra("clicked_student_uid", user_uid);
                    intent.putExtra("clicked_student_name", user_fullname);

                } else {
                    if (addGrades_btn.isEnabled() && showGrades_btn.isEnabled())
                        Toast.makeText(Grades1Activity.this, "You must select Add grades or show grades first", Toast.LENGTH_LONG).show();
                    else {
                        if (!addGrades_btn.isEnabled())
                            intent = new Intent(Grades1Activity.this, AddGradesActivity.class);
                        else if (!showGrades_btn.isEnabled())
                            intent = new Intent(Grades1Activity.this, Grades2Activity.class);

                        intent.putExtra("classroom"
                                , new Classroom(yearSpinner.getSelectedItem().toString(), classSpinner.getSelectedItem().toString()));
                        intent.putExtra("subject", (String) adapterView.getAdapter().getItem(i));
                    }
                }
                if (intent != null)
                    startActivity(intent);
            }
        });
    }

    private void fillListView(ArrayList<String> list) {
        if (list.size() == 0)
            noSubjects.setVisibility(View.VISIBLE);
        else {
            if (noSubjects.getVisibility() == View.VISIBLE)
                noSubjects.setVisibility(View.GONE);

            ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                    android.R.layout.simple_list_item_1, android.R.id.text1, list);
            listView.setAdapter(adapter);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        // Unregister VisualizerActivity as an OnPreferenceChangedListener to avoid any memory leaks.
        PreferenceManager.getDefaultSharedPreferences(this);
    }
}
