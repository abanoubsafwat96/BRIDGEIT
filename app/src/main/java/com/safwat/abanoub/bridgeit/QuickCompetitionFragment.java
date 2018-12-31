package com.safwat.abanoub.bridgeit;


import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class QuickCompetitionFragment extends Fragment {

    private Classroom classroom;
    private String subject;

    ListView listView;
    TextView noCompetitions;
    Button start_btn;

    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;
    private ArrayList<Competition> competitions_list;
    private CompetitionsAdapter adapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_quick_competition, container, false);

        classroom = getArguments().getParcelable("classroom");
        subject = getArguments().getString("subject");

        listView = view.findViewById(R.id.listView);
        noCompetitions = view.findViewById(R.id.noCompetitions);
        start_btn = view.findViewById(R.id.start_btn);

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
            }
        });

        start_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(getActivity(), QuickCompetitions2Activity.class);
                ArrayList<Competition> list = new ArrayList<>();

                boolean[] choosed_competitions_arr = adapter.getChoosed_competitions_arr();
                for (int i = 0; i < choosed_competitions_arr.length; i++) {
                    if (choosed_competitions_arr[i] == true)
                        list.add((Competition) adapter.getItem(i));
                }
                if (list.size() > 0) {
                    intent.putExtra("classroom", classroom);
                    intent.putExtra("subject", subject);
                    intent.putParcelableArrayListExtra("competitions", list);

                    for (int i = 0; i < list.size(); i++) {
                        intent.putParcelableArrayListExtra("questions_list" + i, list.get(i).questions_list);
                        intent.putParcelableArrayListExtra("bonusQuestions_list" + i, list.get(i).bonusQuestions_list);
                    }
                    startActivity(intent);
                } else
                    Toast.makeText(getContext(), "Choose some competitions", Toast.LENGTH_SHORT).show();
            }
        });

        return view;
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

            adapter = new CompetitionsAdapter(getContext(), competitions_list, "QuickCompetitions");
            listView.setAdapter(adapter);
        }
    }
}
