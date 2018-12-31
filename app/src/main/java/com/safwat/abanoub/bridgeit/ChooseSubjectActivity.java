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
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class ChooseSubjectActivity extends AppCompatActivity {

    Spinner yearSpinner, classSpinner;
    LinearLayout spinnersLinear;
    View line;
    ListView listView;
    TextView noSubjects;

    private ArrayList<String> yearsList, classesList;
    private String userType;
    Classroom classroom;

    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_subject);

        yearSpinner = findViewById(R.id.yearSpinner);
        classSpinner = findViewById(R.id.classSpinner);
        spinnersLinear = findViewById(R.id.spinnersLinear);
        line = findViewById(R.id.line);
        listView = findViewById(R.id.list);
        noSubjects=findViewById(R.id.noSubjects);

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(ChooseSubjectActivity.this);
        //reading from shared preference
        userType = sharedPreferences.getString("userType", "not found");

        firebaseDatabase = FirebaseDatabase.getInstance();

        if (userType.equals("Student")) {
            spinnersLinear.setVisibility(View.GONE);
            line.setVisibility(View.GONE);

            databaseReference = firebaseDatabase.getReference().child("students").child(Utilities.getCurrentUID())
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
                                            .child(classroom.className)
                                            .child("subjects").addValueEventListener(new ValueEventListener() {
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
                                public void onCancelled(@NonNull DatabaseError databaseError) {

                                }
                            });
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

                    ArrayAdapter<String> adapter2 = new ArrayAdapter<>(ChooseSubjectActivity.this
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

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                Intent intent = null;
                if (userType.equals("Student")) {
                    intent = new Intent(ChooseSubjectActivity.this, ChooseCompetitionActivity.class);
                    intent.putExtra("classroom", classroom);
                    intent.putExtra("subject", (String) adapterView.getAdapter().getItem(i));
                    intent.putExtra("clicked_student_uid", Utilities.getCurrentUID());
                    intent.putExtra("clicked_student_name", Utilities.getCurrentUsername());

                } else {

                    intent = new Intent(ChooseSubjectActivity.this, TeacherCompetitionActivity.class);

                    intent.putExtra("classroom"
                            , new Classroom(yearSpinner.getSelectedItem().toString(), classSpinner.getSelectedItem().toString()));
                    intent.putExtra("subject", (String) adapterView.getAdapter().getItem(i));
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
