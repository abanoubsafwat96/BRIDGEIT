package com.safwat.abanoub.bridgeit;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;

public class GradesGraphActivity extends AppCompatActivity {

    BarChart barChart;
    private ArrayList<String> xValues;
    private ArrayList<BarEntry> yValues;

    //GradesByTestFragment
    CompetitionGrades clicked_competitionGrades;
    int i;

    //GradesByStudentFragment
    private Classroom classroom;
    private String subject, clicked_student_name, clicked_student_uid;
    private ArrayList<String> uids_list;
    private ArrayList<CompetitionGrades> competitionGrades_list;

    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_grades_graph);

        //GradesByTestFragment
        clicked_competitionGrades = (CompetitionGrades) getIntent().getSerializableExtra("quiz");
        if (clicked_competitionGrades == null) {

            //GradesByStudentFragment or Student grades
            classroom = getIntent().getParcelableExtra("classroom");
            subject = getIntent().getExtras().getString("subject");
            uids_list = getIntent().getExtras().getStringArrayList("uids_list");
            clicked_student_uid = getIntent().getExtras().getString("clicked_student_uid");
            clicked_student_name = getIntent().getExtras().getString("clicked_student_name");
        }

        barChart = findViewById(R.id.barChart);
        Description description = new Description();

        if (clicked_competitionGrades != null)
            description.setText(clicked_competitionGrades.title);
        else if (clicked_student_name != null)
            description.setText(clicked_student_name + " grades");

        barChart.setDescription(description);

        Legend l = barChart.getLegend();
        l.setWordWrapEnabled(true);
        l.setVerticalAlignment(Legend.LegendVerticalAlignment.BOTTOM);
        l.setHorizontalAlignment(Legend.LegendHorizontalAlignment.CENTER);
        l.setOrientation(Legend.LegendOrientation.HORIZONTAL);
        l.setDrawInside(false);

        YAxis rightAxis = barChart.getAxisRight();
        rightAxis.setDrawGridLines(true);
        rightAxis.setAxisMinimum(0f); // this replaces setStartAtZero(true)

        YAxis leftAxis = barChart.getAxisLeft();
        leftAxis.setDrawGridLines(true);
        leftAxis.setAxisMinimum(0f); // this replaces setStartAtZero(true)


        firebaseDatabase = FirebaseDatabase.getInstance();
        yValues = new ArrayList<>();
        xValues = new ArrayList<>();

        //GradesByTestFragment
        if (clicked_competitionGrades != null) {
            final Map<String, String> grades_map = clicked_competitionGrades.grades_map;

            Iterator it = grades_map.entrySet().iterator();
            for (i = 0; i < grades_map.size(); i++) {

                Map.Entry pair = (Map.Entry) it.next();
                String key = pair.getKey() + "";
                yValues.add(new BarEntry(i, Integer.parseInt(pair.getValue() + "")));

                firebaseDatabase.getReference().child("students").child(key)
                        .addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                xValues.add(Utilities.getStudentName(dataSnapshot));

                                if (i == grades_map.size())
                                    setXAxis(xValues);
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });
            }
        }
        //GradesByStudentFragment or Student grades
        else if (clicked_student_uid != null) {

            firebaseDatabase.getReference().child("grades").child("Years").child(classroom.yearName)
                    .child(classroom.className).child(subject).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    competitionGrades_list = Utilities.getAllGrades(dataSnapshot, uids_list);

                    for (int j = 0; j < competitionGrades_list.size(); j++) {

                        xValues.add(competitionGrades_list.get(j).title);

                        String student_grade = competitionGrades_list.get(j).grades_map.get(clicked_student_uid);
                        if (student_grade == null)
                            yValues.add(new BarEntry(j, 0));
                        else
                            yValues.add(new BarEntry(j, Integer.parseInt(student_grade)));

                        if (j == competitionGrades_list.size() - 1)
                            setXAxis(xValues);
                    }

                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }
    }

    private void setXAxis(final ArrayList<String> xValues) {

        XAxis xAxis = barChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTH_SIDED);
        xAxis.setGranularity(1f); //prevent duplicating xAxis values
        xAxis.setValueFormatter(new IAxisValueFormatter() {
            @Override
            public String getFormattedValue(float value, AxisBase axis) {
                if (value<xValues.size())
                    return xValues.get((int) value); // xVal is a string array
                else
                    return "loading";
            }
        });

        setYAxis();
        barChart.setFitBars(true); //center barchart with mounth line
    }

    private void setYAxis() {

        BarDataSet set = new BarDataSet(yValues, "Grades");
        set.setColor(Color.rgb(60, 220, 78));
        set.setDrawValues(true);

        BarData data = new BarData(set);

        barChart.setData(data);
        barChart.invalidate();
        barChart.animateY(500);
    }
}
