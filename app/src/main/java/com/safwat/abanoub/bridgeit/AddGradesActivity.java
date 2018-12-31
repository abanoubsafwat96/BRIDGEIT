package com.safwat.abanoub.bridgeit;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class AddGradesActivity extends AppCompatActivity {

    TextView toolbarTitle, quizTitle;
    ListView listView;
    TextView noStudents;
    FloatingActionButton done_btn;

    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;
    ArrayList<Student> students_list;
    AddGradesAdapter adapter;
    private Classroom classroom;
    private String subject;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_grades);

        classroom = getIntent().getParcelableExtra("classroom");
        subject = getIntent().getExtras().getString("subject");

        toolbarTitle = findViewById(R.id.toolbarTitle);
        quizTitle = findViewById(R.id.quizTitle);
        listView = findViewById(R.id.list);
        noStudents = findViewById(R.id.noStudents);
        done_btn = findViewById(R.id.fab);

        toolbarTitle.setText("Add Grades to " + classroom.yearName + ", " + classroom.className + " on " + subject);

        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference().child("Years").child(classroom.yearName)
                .child(classroom.className).child("students");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                final ArrayList<String> list = Utilities.getUIDs(dataSnapshot);
                students_list = new ArrayList<>();
                for (int i = 0; i < list.size(); i++) {
                    final int position=i;

                    firebaseDatabase.getReference().child("students").child(list.get(position))
                            .addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                    students_list.add(Utilities.getStudent(dataSnapshot));

                                    if (position == list.size()-1)
                                        fillListView(students_list);
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

        done_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                boolean allGradesEntered = true;
                String[] grades_arr = adapter.getGrades_arr();
                for (int i = 0; i < grades_arr.length; i++) {
                    if (grades_arr[i] == null)
                        allGradesEntered = false;
                }
                if (TextUtils.isEmpty(quizTitle.getText().toString()))
                    Toast.makeText(AddGradesActivity.this, "Error || Please enter quiz title", Toast.LENGTH_SHORT).show();
                else if (grades_arr.length == students_list.size() && allGradesEntered
                        && !TextUtils.isEmpty(quizTitle.getText().toString())) {

                    DatabaseReference addGradeRef = firebaseDatabase.getReference().child("grades").child("Years")
                            .child(classroom.yearName).child(classroom.className).child(subject);
                    String pushID = addGradeRef.push().getKey();

                    Map<String, Object> titleHashMap = new HashMap<>();
                    titleHashMap.put("title", quizTitle.getText().toString());
                    addGradeRef.child(pushID).setValue(titleHashMap);

                    for (int i = 0; i < grades_arr.length; i++) {
                        Map<String, Object> gradeHashMap = new HashMap<>();
                        gradeHashMap.put("grade", grades_arr[i]);
                        addGradeRef.child(pushID).child(students_list.get(i).uid).setValue(gradeHashMap);
                    }

                    Toast.makeText(AddGradesActivity.this, "Grades Added Successfully", Toast.LENGTH_SHORT).show();
                    finish();
                } else
                    Toast.makeText(AddGradesActivity.this, "Error || Please enter all students grades", Toast.LENGTH_LONG).show();
            }
        });
    }

    private void fillListView(ArrayList<Student> students_list) {
        if (students_list.size() == 0)
            noStudents.setVisibility(View.VISIBLE);
        else {
            if (noStudents.getVisibility() == View.VISIBLE)
                noStudents.setVisibility(View.GONE);

            adapter = new AddGradesAdapter(this, students_list);
            listView.setAdapter(adapter);
        }
    }
}
