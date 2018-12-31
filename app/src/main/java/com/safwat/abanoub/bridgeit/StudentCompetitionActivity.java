package com.safwat.abanoub.bridgeit;

import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;

public class StudentCompetitionActivity extends AppCompatActivity {

    LinearLayout question_linear, prize_linear;
    TextView prize_textView;
    ImageView prize_imageView;
    RelativeLayout next_finish_relative;
    TextView question_number, question_textView, choice1, choice2, choice3, choice4;
    Button finish_btn, next_btn;

    Classroom classroom;
    String subject, clicked_student_uid, clicked_student_name;
    Competition competition;

    int question_index = -1, bonusQuestion_index = -1;
    float questionsResult = 0, bonusQuestionsResult = 0;
    boolean nextQuestion_isAvailable = true, nextBonusQuestion_isAvailable = false, thisQuestion_isAvailable = true, checkIfGotStar = false, checkIfGotCrown = false;

    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference portfolioReference, addGradeReference;
    private Portfolio portfolio;
    private ValueEventListener valueEventListener;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_competition);

        classroom = getIntent().getParcelableExtra("classroom");
        subject = getIntent().getStringExtra("subject");
        clicked_student_uid = getIntent().getStringExtra("clicked_student_uid");
        clicked_student_name = getIntent().getStringExtra("clicked_student_name");
        competition = getIntent().getParcelableExtra("competition");
        competition.questions_list = getIntent().getParcelableArrayListExtra("questions_list");
        competition.bonusQuestions_list = getIntent().getParcelableArrayListExtra("bonusQuestions_list");

        question_linear = findViewById(R.id.question_linear);
        prize_linear = findViewById(R.id.prize_linear);
        prize_textView = findViewById(R.id.prize_textView);
        prize_imageView = findViewById(R.id.prize_imageView);
        next_finish_relative = findViewById(R.id.next_finish_relative);
        question_number = findViewById(R.id.question_number);
        question_textView = findViewById(R.id.question);
        choice1 = findViewById(R.id.choice1);
        choice2 = findViewById(R.id.choice2);
        choice3 = findViewById(R.id.choice3);
        choice4 = findViewById(R.id.choice4);
        next_btn = findViewById(R.id.next_btn);
        finish_btn = findViewById(R.id.finish_btn);

        // student portfolio
        firebaseDatabase = FirebaseDatabase.getInstance();
        portfolioReference = firebaseDatabase.getReference().child("portfolio").child(clicked_student_uid).child(subject);
        portfolioReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                portfolio = Utilities.getPortfolioForSubject(dataSnapshot);

                setNewQuestion();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        choice1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                choice1.setBackgroundColor(getResources().getColor(R.color.caldroid_light_red));
                choice2.setBackgroundColor(getResources().getColor(R.color.trquaz));
                choice3.setBackgroundColor(getResources().getColor(R.color.trquaz));
                choice4.setBackgroundColor(getResources().getColor(R.color.trquaz));
            }
        });

        choice2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                choice1.setBackgroundColor(getResources().getColor(R.color.trquaz));
                choice2.setBackgroundColor(getResources().getColor(R.color.caldroid_light_red));
                choice3.setBackgroundColor(getResources().getColor(R.color.trquaz));
                choice4.setBackgroundColor(getResources().getColor(R.color.trquaz));
            }
        });

        choice3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                choice1.setBackgroundColor(getResources().getColor(R.color.trquaz));
                choice2.setBackgroundColor(getResources().getColor(R.color.trquaz));
                choice3.setBackgroundColor(getResources().getColor(R.color.caldroid_light_red));
                choice4.setBackgroundColor(getResources().getColor(R.color.trquaz));
            }
        });

        choice4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                choice1.setBackgroundColor(getResources().getColor(R.color.trquaz));
                choice2.setBackgroundColor(getResources().getColor(R.color.trquaz));
                choice3.setBackgroundColor(getResources().getColor(R.color.trquaz));
                choice4.setBackgroundColor(getResources().getColor(R.color.caldroid_light_red));
            }
        });

        next_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (question_linear.getVisibility() == View.VISIBLE) {
                    int color1 = ((ColorDrawable) choice1.getBackground()).getColor();
                    int color2 = ((ColorDrawable) choice2.getBackground()).getColor();
                    int color3 = ((ColorDrawable) choice3.getBackground()).getColor();
                    int color4 = ((ColorDrawable) choice4.getBackground()).getColor();

                    String studentAnswer = null;

                    if (color1 == getResources().getColor(R.color.caldroid_light_red)) {
                        studentAnswer = choice1.getText().toString();
                    } else if (color2 == getResources().getColor(R.color.caldroid_light_red)) {
                        studentAnswer = choice2.getText().toString();
                    } else if (color3 == getResources().getColor(R.color.caldroid_light_red)) {
                        studentAnswer = choice3.getText().toString();
                    } else if (color4 == getResources().getColor(R.color.caldroid_light_red)) {
                        studentAnswer = choice4.getText().toString();
                    }

                    if (studentAnswer != null) {

                        int questionsCount = competition.questions_list.size();

                        if (thisQuestion_isAvailable && question_index < questionsCount) {
                            Question question = competition.questions_list.get(question_index);

                            if (studentAnswer.equals(question.answer)) {
                                questionsResult++;
                                Toast.makeText(StudentCompetitionActivity.this, "True", Toast.LENGTH_SHORT).show();
                            } else
                                Toast.makeText(StudentCompetitionActivity.this, "False", Toast.LENGTH_SHORT).show();

                            if (question_index + 1 == questionsCount) //last question
                                thisQuestion_isAvailable = false; //stop executing this condition to start execute bonus questions condition

                        } else if (competition.bonusQuestions_list != null) {

                            int bonusQuestionsCount = competition.bonusQuestions_list.size();

                            if (bonusQuestion_index < bonusQuestionsCount) {
                                Question question = competition.bonusQuestions_list.get(bonusQuestion_index);

                                if (studentAnswer.equals(question.answer)) {
                                    bonusQuestionsResult++;
                                    Toast.makeText(StudentCompetitionActivity.this, "True", Toast.LENGTH_SHORT).show();
                                } else
                                    Toast.makeText(StudentCompetitionActivity.this, "False", Toast.LENGTH_SHORT).show();
                            }
                        }

                        if (nextQuestion_isAvailable)
                            setNewQuestion();

                        else if (checkIfGotStar) {
                            checkIfGotStar_func();

                        } else if (nextBonusQuestion_isAvailable) //if executed or any other down condition executed, it mean that bonusQuestion_list != null and user got 85% and will solve bonus questions.
                            setNewBonusQuestion();

                        else if (checkIfGotCrown)
                            checkIfGotCrown_func();

                        unMarkChoices();

                    } else
                        Toast.makeText(StudentCompetitionActivity.this, "Choose answer", Toast.LENGTH_SHORT).show();

                } else if (prize_linear.getVisibility() == View.VISIBLE) { //if executed, it mean that bonusQuestion_list != null and user got 85% and will solve bonus questions. so hide prize_linear and show question_linear
                    finish_btn.setVisibility(View.INVISIBLE);
                    prize_linear.setVisibility(View.GONE);
                    question_linear.setVisibility(View.VISIBLE);

                    setNewBonusQuestion();
                }
            }
        });

        finish_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                addGradeReference = firebaseDatabase.getReference().child("grades").child("Years")
                        .child(classroom.yearName).child(classroom.className).child(subject).child(competition.uid);

                valueEventListener=new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        boolean competitionExist = Utilities.checkIfCompetitionExistInGrades(dataSnapshot);

                        if (!competitionExist) {
                            Map<String, Object> titleHashMap = new HashMap<>();
                            titleHashMap.put("title", competition.title);
                            addGradeReference.setValue(titleHashMap);
                        }

                        int final_grade;
                        if (competition.bonusQuestions_list == null)
                            final_grade = (int) ((questionsResult / competition.questions_list.size()) * 100);
                        else
                            final_grade = (int) (((questionsResult + bonusQuestionsResult)
                                                                / (competition.questions_list.size() + competition.bonusQuestions_list.size())) * 100);

                        Map<String, Object> gradeHashMap = new HashMap<>();
                        gradeHashMap.put("grade", final_grade);
                        addGradeReference.child(clicked_student_uid).setValue(gradeHashMap);

                        stopAddGradesReferenceListening();

                        Toast.makeText(StudentCompetitionActivity.this, "You have got " + final_grade, Toast.LENGTH_LONG).show();
                        finish();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                };
                addGradeReference.addValueEventListener(valueEventListener);
            }
        });
    }

    private void checkIfGotStar_func() {

        int questionsCount = competition.questions_list.size();

        if ((questionsResult / questionsCount) >= 0.85) { //got star

            if (portfolio != null) {

                int stars = Integer.parseInt(portfolio.star);
                stars++;
                portfolioReference.child("star").setValue(stars);

                prize_textView.setText("Congratulations");
                prize_imageView.setImageResource(R.drawable.star2);
            }

            if (competition.bonusQuestions_list == null)
                next_btn.setVisibility(View.INVISIBLE);
        } else {
            prize_textView.setText("Not got a star");
            next_btn.setVisibility(View.INVISIBLE);
        }

        finish_btn.setVisibility(View.VISIBLE);
        question_linear.setVisibility(View.GONE);
        prize_linear.setVisibility(View.VISIBLE);
        checkIfGotStar = false;
    }

    private void checkIfGotCrown_func() {

        prize_imageView.setImageResource(0);

        if ((bonusQuestionsResult / competition.bonusQuestions_list.size()) >= 0.85) {
            prize_textView.setText("Wohoooo Congratulations");
            checkIfGotTrophy_func();
        } else {
            prize_textView.setText("Not got a crown nor a trophy");
        }

        next_btn.setVisibility(View.INVISIBLE);
        finish_btn.setVisibility(View.VISIBLE);
        question_linear.setVisibility(View.GONE);
        prize_linear.setVisibility(View.VISIBLE);
        checkIfGotCrown = false;
    }

    private void checkIfGotTrophy_func() {

        if (portfolio != null) {

            int crowns = Integer.parseInt(portfolio.crown);
            if (crowns == 0 || crowns == 1) { //got crown
                crowns++;
                portfolioReference.child("crown").setValue(crowns);

                prize_imageView.setImageResource(R.drawable.crown);

            } else if (crowns == 2) { //got trophy
                crowns = 0;
                portfolioReference.child("crown").setValue(crowns);

                int trophies = Integer.parseInt(portfolio.trophy);
                trophies++;
                portfolioReference.child("trophy").setValue(trophies);

                prize_imageView.setImageResource(R.drawable.trophy);
            }
        }
    }

    private void setNewQuestion() {

        int questionsCount = competition.questions_list.size();
        if (question_index + 1 < questionsCount) {

            question_index++;

            question_number.setText("Question " + (question_index + 1));

            Question question = competition.questions_list.get(question_index);
            fillQuestionFields(question);

            if (question_index + 1 == questionsCount) { //last question

                nextQuestion_isAvailable = false;
                checkIfGotStar = true;

                if (competition.bonusQuestions_list != null)
                    nextBonusQuestion_isAvailable = true;
            }
        }
    }

    private void setNewBonusQuestion() {

        int bonusQuestionsCount = competition.bonusQuestions_list.size();
        if (bonusQuestion_index + 1 < bonusQuestionsCount) {

            bonusQuestion_index++;

            question_number.setText("Bonus Question " + (bonusQuestion_index + 1));

            Question question = competition.bonusQuestions_list.get(bonusQuestion_index);
            fillQuestionFields(question);

            if (bonusQuestion_index + 1 == bonusQuestionsCount) { //last bonus question

                nextBonusQuestion_isAvailable = false;
                checkIfGotCrown = true;
            }
        }
    }

    private void fillQuestionFields(Question question) {
        question_textView.setText(question.question);
        choice1.setText(question.choice1);
        choice2.setText(question.choice2);
        choice3.setText(question.choice3);
        choice4.setText(question.choice4);
    }

    private void unMarkChoices() {
        choice1.setBackgroundColor(getResources().getColor(R.color.trquaz));
        choice2.setBackgroundColor(getResources().getColor(R.color.trquaz));
        choice3.setBackgroundColor(getResources().getColor(R.color.trquaz));
        choice4.setBackgroundColor(getResources().getColor(R.color.trquaz));
    }

    @Override
    public void onBackPressed() {
        Toast.makeText(this, "You must finish competition first", Toast.LENGTH_SHORT).show();
    }

    private void stopAddGradesReferenceListening() {
        if (addGradeReference != null && valueEventListener != null) {
            addGradeReference.removeEventListener(valueEventListener);
        }
    }
}
