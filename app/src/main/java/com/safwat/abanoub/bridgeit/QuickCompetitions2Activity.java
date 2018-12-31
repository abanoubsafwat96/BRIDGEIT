package com.safwat.abanoub.bridgeit;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class QuickCompetitions2Activity extends AppCompatActivity implements Communicator.Deadline {

    String mixCompetitionTitle = "";
    TextView deadline, mixCompetitions;
    EditText title, description;
    private String deadlineDate;
    ListView questions_listView, bonusQuestions_listView;
    TextView noQuestions, noBonusQuestions;
    Button finish_btn;
    QuestionAdapter questionAdapter, bonusQuestionsAdapter;

    private Classroom classroom;
    private String subject;

    ArrayList<Competition> competitions_list;
    ArrayList<Question> questions_list;
    ArrayList<Question> bonusQuestions_list;

    FirebaseDatabase firebaseDatabase;
    DatabaseReference competitionReference, calendarReference, competitionGradesReference;
    ValueEventListener competitionGradesValueEventListener;
    private SimpleDateFormat dateFormatForDay;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quick_competitions2);

        classroom = getIntent().getParcelableExtra("classroom");
        subject = getIntent().getStringExtra("subject");
        competitions_list = getIntent().getParcelableArrayListExtra("competitions");

        for (int i = 0; i < competitions_list.size(); i++) {
            competitions_list.get(i).questions_list = getIntent().getParcelableArrayListExtra("questions_list" + i);
            competitions_list.get(i).bonusQuestions_list = getIntent().getParcelableArrayListExtra("bonusQuestions_list" + i);
        }

        mixCompetitions = findViewById(R.id.mixCompetitions);
        deadline = findViewById(R.id.deadline2);
        title = findViewById(R.id.title2);
        description = findViewById(R.id.description2);
        questions_listView = findViewById(R.id.questions_listView);
        bonusQuestions_listView = findViewById(R.id.bonusQuestions_listView);
        noQuestions = findViewById(R.id.noQuestions);
        noBonusQuestions = findViewById(R.id.noBonusQuestions);
        finish_btn = findViewById(R.id.finish_btn);

        dateFormatForDay = new SimpleDateFormat("EEEE ',' dd MMMM yyyy", Locale.getDefault());

        questions_list = new ArrayList<>();
        bonusQuestions_list = new ArrayList<>();

        for (int i = 0; i < competitions_list.size(); i++) {
            Competition competition = competitions_list.get(i);
            if (i == competitions_list.size() - 1)
                mixCompetitionTitle += competition.title;
            else
                mixCompetitionTitle += competition.title + " and ";

            ArrayList<Question> questions = competition.questions_list;
            ArrayList<Question> bonusQuestions = competition.bonusQuestions_list;

            for (int j = 0; j < questions.size(); j++)
                questions_list.add(questions.get(j));

            if (bonusQuestions != null) {
                for (int j = 0; j < bonusQuestions.size(); j++)
                    bonusQuestions_list.add(bonusQuestions.get(j));
            }
        }

        mixCompetitions.setText("Mix between " + mixCompetitionTitle);

        fillListView(questions_list, bonusQuestions_list);

        deadline.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DialogFragment dialog = CalendarDialogFragment.newInstance(QuickCompetitions2Activity.this);
                dialog.show(getSupportFragmentManager(), "tag");
            }
        });

        firebaseDatabase = FirebaseDatabase.getInstance();
        competitionReference = firebaseDatabase.getReference().child("competitions").child("Years").child(classroom.yearName)
                .child(classroom.className).child(subject);


        finish_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (deadlineDate != null) {

                    ArrayList<Question> final_questions_list = new ArrayList<>();
                    ArrayList<Question> final_bonusQuestions_list = new ArrayList<>();

                    boolean[] choosed_questions_arr = questionAdapter.getChoosed_questions_arr();
                    boolean[] choosed_bonusQuestions_arr = bonusQuestionsAdapter.getChoosed_questions_arr();

                    for (int i = 0; i < choosed_questions_arr.length; i++) {
                        if (choosed_questions_arr[i])
                            final_questions_list.add((Question) questionAdapter.getItem(i));
                    }

                    for (int i = 0; i < choosed_bonusQuestions_arr.length; i++) {
                        if (choosed_bonusQuestions_arr[i])
                            final_bonusQuestions_list.add((Question) bonusQuestionsAdapter.getItem(i));
                    }

                    if (final_questions_list.size() > 0) {
                        if (!TextUtils.isEmpty(title.getText()) && !TextUtils.isEmpty(description.getText())) {

                            final String pushID = competitionReference.push().getKey();
                            final Competition competition;

                            if (final_bonusQuestions_list.size() > 0) {
                                competition = new Competition(title.getText().toString()
                                        , description.getText().toString(), final_questions_list, final_bonusQuestions_list
                                        , deadlineDate);
                            } else {
                                competition = new Competition(title.getText().toString(), description.getText().toString()
                                        , final_questions_list, deadlineDate);
                            }

                            competitionReference.child(pushID).setValue(competition);

                            //Adding competition to grades branch
                            competitionGradesReference = firebaseDatabase.getReference().child("grades").child("Years")
                                    .child(classroom.yearName).child(classroom.className).child(subject);
                            competitionGradesValueEventListener = new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                    boolean competitionExist = Utilities.checkIfCompetitionExistInGrades(dataSnapshot);

                                    if (!competitionExist) {
                                        Map<String, Object> titleHashMap = new HashMap<>();
                                        titleHashMap.put("title", competition.title);
                                        competitionGradesReference.child(pushID).setValue(titleHashMap);
                                        stopCompetitionGradesReferenceListening();
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {

                                }
                            };
                            competitionGradesReference.addValueEventListener(competitionGradesValueEventListener);

                            //Adding competition to calendar branch
                            calendarReference = firebaseDatabase.getReference().child("calendar").child("Years").child(classroom.yearName)
                                    .child(classroom.className);
                            String pushID2 = calendarReference.push().getKey();
                            CalendarEvent calendarEvent = new CalendarEvent(deadlineDate
                                    , "Deadline of: " + competition.title);
                            calendarReference.child(pushID2).setValue(calendarEvent);

                            deadline.setText("Select a date...");
                            title.setText("");
                            description.setText("");
                            questionAdapter.questions_list.clear();
                            bonusQuestionsAdapter.questions_list.clear();

                            refreshListView(questionAdapter, questions_listView);
                            refreshListView(bonusQuestionsAdapter, bonusQuestions_listView);

                            finish();
                            Toast.makeText(QuickCompetitions2Activity.this, "Competition Added Successfully", Toast.LENGTH_LONG).show();
                        } else
                            Toast.makeText(QuickCompetitions2Activity.this, "Enter title and description", Toast.LENGTH_LONG).show();
                    } else
                        Toast.makeText(QuickCompetitions2Activity.this, "Choose one main question at least"
                                , Toast.LENGTH_LONG).show();
                } else
                    Toast.makeText(QuickCompetitions2Activity.this, "Choose a deadline", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void stopCompetitionGradesReferenceListening() {
        if (competitionGradesReference != null && competitionGradesValueEventListener != null) {
            competitionGradesReference.removeEventListener(competitionGradesValueEventListener);
        }
    }

    private void fillListView(ArrayList<Question> questions_list, ArrayList<Question> bonusQuestions_list) {
        if (questions_list.size() == 0)
            noQuestions.setVisibility(View.VISIBLE);
        else {
            if (noQuestions.getVisibility() == View.VISIBLE)
                noQuestions.setVisibility(View.GONE);
        }
        questionAdapter = new QuestionAdapter(this, questions_list, "questions_list"
                , true);
        questions_listView.setAdapter(questionAdapter);
        Utilities.getTotalHeightofListView2(questions_listView);


        if (bonusQuestions_list.size() == 0)
            noBonusQuestions.setVisibility(View.VISIBLE);
        else {
            if (noBonusQuestions.getVisibility() == View.VISIBLE)
                noBonusQuestions.setVisibility(View.GONE);
        }
        bonusQuestionsAdapter = new QuestionAdapter(this, bonusQuestions_list, "bonusQuestions_list"
                , true);
        bonusQuestions_listView.setAdapter(bonusQuestionsAdapter);
        Utilities.getTotalHeightofListView2(bonusQuestions_listView);
    }

    private void refreshListView(QuestionAdapter adapter1, ListView listView1) {
        adapter1.notifyDataSetChanged();
        Utilities.getTotalHeightofListView2(listView1);
    }

    @Override
    public void onDeadlineChoosed(Date dateClicked) {
        this.deadlineDate = dateClicked.getTime() + "";
        Log.e("onDeadlineChoosed: ", deadlineDate);
        deadline.setText(dateFormatForDay.format(dateClicked));
    }
}
