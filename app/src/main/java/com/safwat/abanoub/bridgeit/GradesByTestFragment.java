package com.safwat.abanoub.bridgeit;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class GradesByTestFragment extends Fragment {

    ListView listView;
    TextView noTests;

    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;
    private Classroom classroom;
    private String subject;
    private ArrayList<CompetitionGrades> competitionGrades_list;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_grades_by_test, container, false);

        classroom = getArguments().getParcelable("classroom");
        subject = getArguments().getString("subject");

        listView=view.findViewById(R.id.listView);
        noTests=view.findViewById(R.id.noTests);

        firebaseDatabase=FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference().child("Years").child(classroom.yearName)
                .child(classroom.className).child("students");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                final ArrayList<String> uids_list = Utilities.getUIDs(dataSnapshot);

                firebaseDatabase.getReference().child("grades").child("Years").child(classroom.yearName)
                        .child(classroom.className).child(subject).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        ArrayList<CompetitionGrades> reversed_competitionGrades_list=Utilities.getAllGrades(dataSnapshot,uids_list);
                        competitionGrades_list=reverseList(reversed_competitionGrades_list);
                        fillListView(competitionGrades_list);
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

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent intent=new Intent(getContext(),GradesGraphActivity.class);
                intent.putExtra("quiz",competitionGrades_list.get(i));
                startActivity(intent);
            }
        });

        return view;
    }

    private ArrayList<CompetitionGrades> reverseList(ArrayList<CompetitionGrades> reversed_competitions_list) {
        ArrayList<CompetitionGrades> reversed_list = new ArrayList<>();

        for (int i = reversed_competitions_list.size() - 1; i >= 0; i--) {
            reversed_list.add(reversed_competitions_list.get(i));
        }

        return reversed_list;
    }

    private void fillListView(ArrayList<CompetitionGrades> grades_list) {

        ArrayList<String> quizTitles_list=new ArrayList<>();
        for (int i=0;i<grades_list.size();i++)
            quizTitles_list.add(grades_list.get(i).title);

        if (quizTitles_list.size() == 0)
            noTests.setVisibility(View.VISIBLE);
        else {
            if (noTests.getVisibility() == View.VISIBLE)
                noTests.setVisibility(View.GONE);
        }
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getContext(),
                android.R.layout.simple_list_item_1, quizTitles_list);
        listView.setAdapter(adapter);
    }
}