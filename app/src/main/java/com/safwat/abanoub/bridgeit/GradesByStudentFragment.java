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

public class GradesByStudentFragment extends Fragment {

    ListView listView;
    TextView noStudents;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;
    private Classroom classroom;
    private String subject;
    private ArrayList<String> uids_list, studentsNames_list;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_grades_by_student, container, false);

        classroom = getArguments().getParcelable("classroom");
        subject = getArguments().getString("subject");

        listView = view.findViewById(R.id.listView);
        noStudents=view.findViewById(R.id.noStudents);

        studentsNames_list = new ArrayList<>();

        firebaseDatabase=FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference().child("Years").child(classroom.yearName)
                .child(classroom.className).child("students");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                uids_list = Utilities.getUIDs(dataSnapshot);

                for (int i=0;i<uids_list.size();i++) {
                    firebaseDatabase.getReference().child("students").child(uids_list.get(i))
                            .addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                    studentsNames_list.add(Utilities.getStudentName(dataSnapshot));
                                    fillListView(studentsNames_list);

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

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent intent = new Intent(getContext(), GradesGraphActivity.class);
                intent.putExtra("classroom",classroom);
                intent.putExtra("subject",subject);
                intent.putExtra("uids_list", uids_list);
                intent.putExtra("clicked_student_uid", uids_list.get(i));
                intent.putExtra("clicked_student_name", studentsNames_list.get(i));
                startActivity(intent);
            }
        });

        return view;
    }

    private void fillListView(ArrayList<String> studentsNames_list) {
        if (studentsNames_list.size() == 0)
            noStudents.setVisibility(View.VISIBLE);
        else {
            if (noStudents.getVisibility() == View.VISIBLE)
                noStudents.setVisibility(View.GONE);
        }
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getContext(),
                android.R.layout.simple_list_item_1, studentsNames_list);
        listView.setAdapter(adapter);
    }
}


