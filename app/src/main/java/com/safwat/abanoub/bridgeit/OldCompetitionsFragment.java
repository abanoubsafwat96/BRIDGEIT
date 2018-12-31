package com.safwat.abanoub.bridgeit;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class OldCompetitionsFragment extends Fragment {

    private Classroom classroom;
    private String subject;

    ListView listView;
    TextView noCompetitions;

    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;
    private ArrayList<Competition> competitions_list;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_old_competitions, container, false);

        classroom = getArguments().getParcelable("classroom");
        subject = getArguments().getString("subject");

        listView = view.findViewById(R.id.listView);
        noCompetitions = view.findViewById(R.id.noCompetitions);

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
                Intent intent = new Intent(getActivity(), OldCompetitions2Activity.class);
                intent.putExtra("competition", competitions_list.get(i));
                intent.putParcelableArrayListExtra("questions_list", competitions_list.get(i).questions_list);
                intent.putParcelableArrayListExtra("bonusQuestions_list", competitions_list.get(i).bonusQuestions_list);
                startActivity(intent);
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

            CompetitionsAdapter adapter = new CompetitionsAdapter(getContext(), competitions_list, "OldCompetitions");
            listView.setAdapter(adapter);
        }
    }
}
