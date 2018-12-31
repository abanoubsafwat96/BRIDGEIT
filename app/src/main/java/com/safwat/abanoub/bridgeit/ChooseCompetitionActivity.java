package com.safwat.abanoub.bridgeit;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class ChooseCompetitionActivity extends AppCompatActivity {

    ListView listView;
    TextView noCompetitions;

    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference, competitionGradesReference, studentUIDCompetitionGradeRef;
    private ValueEventListener competitionGradesValueEventListener, studentUIDCompetitionGradeValueEventListener;

    Classroom classroom;
    String subject, clicked_student_uid, clicked_student_name;
    private ArrayList<Competition> competitions_list;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_competition);

        classroom = getIntent().getParcelableExtra("classroom");
        subject = getIntent().getStringExtra("subject");
        clicked_student_uid = getIntent().getStringExtra("clicked_student_uid");
        clicked_student_name = getIntent().getStringExtra("clicked_student_name");

        listView = findViewById(R.id.list);
        noCompetitions=findViewById(R.id.noCompetitions);

        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference().child("competitions").child("Years").child(classroom.yearName)
                .child(classroom.className).child(subject);
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                ArrayList<Competition> reversed_competitions_list = Utilities.gettAllCompetitions(dataSnapshot);
                competitions_list = reverseList(reversed_competitions_list);
                fillListView(competitions_list);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                final Competition competition = competitions_list.get(i);

                Date deadline_date = new Date(Long.parseLong(competition.deadline));
                if (deadline_date.before(Calendar.getInstance().getTime()))
                    Toast.makeText(ChooseCompetitionActivity.this, "Deadline Passed", Toast.LENGTH_SHORT).show();

                else {
                    competitionGradesReference = firebaseDatabase.getReference().child("grades").child("Years")
                            .child(classroom.yearName).child(classroom.className).child(subject).child(competition.uid);

                    competitionGradesValueEventListener = new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            boolean competitionExist = Utilities.checkIfCompetitionExistInGrades(dataSnapshot);

                            if (competitionExist) {
                                studentUIDCompetitionGradeRef = competitionGradesReference.child(clicked_student_uid);
                                studentUIDCompetitionGradeValueEventListener = new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                        boolean studentGradeExist = Utilities.checkIfStudentGradeExistInCompetition(dataSnapshot);
                                        if (studentGradeExist)
                                            Toast.makeText(ChooseCompetitionActivity.this,
                                                    "You entered this competition before", Toast.LENGTH_SHORT).show();
                                        else {
                                            startStudentCompetitionActivity(competition);
                                        }
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError databaseError) {

                                    }
                                };
                                studentUIDCompetitionGradeRef.addValueEventListener(studentUIDCompetitionGradeValueEventListener);
                            } else {
                                startStudentCompetitionActivity(competition);
                            }
                        }


                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    };
                    competitionGradesReference.addValueEventListener(competitionGradesValueEventListener);
                }
            }
        });
    }

    private void startStudentCompetitionActivity(Competition competition) {
        Intent intent = new Intent(ChooseCompetitionActivity.this, StudentCompetitionActivity.class);
        intent.putExtra("classroom", classroom);
        intent.putExtra("subject", subject);
        intent.putExtra("clicked_student_uid", Utilities.getCurrentUID());
        intent.putExtra("clicked_student_name", Utilities.getCurrentUsername());
        intent.putExtra("competition", competition);
        intent.putParcelableArrayListExtra("questions_list", competition.questions_list);
        intent.putParcelableArrayListExtra("bonusQuestions_list", competition.bonusQuestions_list);

        startActivity(intent);
    }

    private ArrayList<Competition> reverseList(ArrayList<Competition> reversed_competitions_list) {
        ArrayList<Competition> reversed_list = new ArrayList<>();

        for (int i = reversed_competitions_list.size() - 1; i >= 0; i--) {
            reversed_list.add(reversed_competitions_list.get(i));
        }

        return reversed_list;
    }

    private void fillListView(ArrayList<Competition> competitions_list) {
        if (competitions_list.size() == 0)
            noCompetitions.setVisibility(View.VISIBLE);
        else {
            if (noCompetitions.getVisibility() == View.VISIBLE)
                noCompetitions.setVisibility(View.GONE);

            CompetitionsAdapter adapter = new CompetitionsAdapter(this, competitions_list
                    , "StudentChooseCompetition");
            listView.setAdapter(adapter);
        }
    }

    private void stopCompetitionGradesReferenceListening() {
        if (competitionGradesReference != null && competitionGradesValueEventListener != null) {
            competitionGradesReference.removeEventListener(competitionGradesValueEventListener);
        }
        if (studentUIDCompetitionGradeRef != null && studentUIDCompetitionGradeValueEventListener != null) {
            studentUIDCompetitionGradeRef.removeEventListener(studentUIDCompetitionGradeValueEventListener);
        }
    }

    @Override
    protected void onStop() {
        super.onStop();

        stopCompetitionGradesReferenceListening();
    }
}
