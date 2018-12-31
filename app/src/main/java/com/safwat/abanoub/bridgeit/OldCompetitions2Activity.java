package com.safwat.abanoub.bridgeit;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Locale;

public class OldCompetitions2Activity extends AppCompatActivity {

    TextView title, deadline, description,noBonusQuestions;
    ListView listView, bonusListView;
    private SimpleDateFormat dateFormatForDay;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_old_competitions2);

        Competition competition = getIntent().getParcelableExtra("competition");
        competition.questions_list = getIntent().getParcelableArrayListExtra("questions_list");
        competition.bonusQuestions_list = getIntent().getParcelableArrayListExtra("bonusQuestions_list");

        title = findViewById(R.id.title2);
        deadline = findViewById(R.id.deadline2);
        description = findViewById(R.id.description2);
        listView = findViewById(R.id.listView);
        bonusListView = findViewById(R.id.bonusListView);
        noBonusQuestions=findViewById(R.id.noBonusQuestions);

        dateFormatForDay = new SimpleDateFormat("EEEE ',' dd MMMM yyyy", Locale.getDefault());

        title.setText(competition.title);
        deadline.setText(dateFormatForDay.format(Long.parseLong(competition.deadline)));
        description.setText(competition.description);

        if (competition.questions_list != null) {
            QuestionAdapter adapter = new QuestionAdapter(this, competition.questions_list
                    , "questions_list",false);
            listView.setAdapter(adapter);
            Utilities.getTotalHeightofListView2(listView);
        }
        if (competition.bonusQuestions_list != null) {
            if (competition.bonusQuestions_list.size() > 0) {
                QuestionAdapter adapter2 = new QuestionAdapter(this, competition.bonusQuestions_list
                        , "bonusQuestions_list",false);
                bonusListView.setAdapter(adapter2);
                Utilities.getTotalHeightofListView2(bonusListView);
                noBonusQuestions.setVisibility(View.GONE);
            }else
                noBonusQuestions.setVisibility(View.VISIBLE);
        }else
            noBonusQuestions.setVisibility(View.VISIBLE);
    }
}
