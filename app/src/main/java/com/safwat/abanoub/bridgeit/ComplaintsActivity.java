package com.safwat.abanoub.bridgeit;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class ComplaintsActivity extends AppCompatActivity {

    LinearLayout searchLinear;
    EditText search_edittext;
    ImageButton add, search_btn;
    ListView listView;
    TextView noComplaints;
    String userType;
    ComplaintsAdapter adapter;
    ArrayList<ArrayList<Msg>> replies_list;

    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;

    SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_complaints);

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(ComplaintsActivity.this);
        //reading from shared preference
        userType = sharedPreferences.getString("userType", "not found");

        searchLinear = findViewById(R.id.searchLinear);
        search_edittext = findViewById(R.id.seach_edittext);
        search_btn = findViewById(R.id.search_btn);
        add = findViewById(R.id.add);
        listView = findViewById(R.id.list);
        noComplaints = findViewById(R.id.noComplaints);

        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference().child("complaints");

        if (userType.equals("Manager")) {
            add.setVisibility(View.GONE);
        } else {
            add.setVisibility(View.VISIBLE);
        }

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                ArrayList<Complaint> reversed_allComplaints_list = Utilities.getAllComplaints(dataSnapshot);
                final ArrayList<Complaint> allComplaints_list = reverseList(reversed_allComplaints_list);

                replies_list = new ArrayList<>();
                final ArrayList<Parent> parents_list = new ArrayList<>();

                for (int i = 0; i < allComplaints_list.size(); i++) {
                    final int position = i;

                    Complaint complaint = allComplaints_list.get(position);

                    databaseReference.child(complaint.uid).child("replies")
                            .addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                    ArrayList<Msg> list = Utilities.getAllMsgs(dataSnapshot);
                                    replies_list.add(list);

                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {

                                }
                            });

                    firebaseDatabase.getReference().child(complaint.senderNode).child(complaint.senderUID)
                            .addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                                    Parent parent = Utilities.getParent(dataSnapshot);

                                    if (parent != null && parent.fullname != null) {
                                        parents_list.add(parent);

                                        if (position == allComplaints_list.size() - 1)
                                            fillListView(allComplaints_list, parents_list, replies_list);

                                    } else if (parent != null && parent.node != null) {
                                        firebaseDatabase.getReference().child(parent.node).child(parent.uid).addValueEventListener(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                                                Parent parent = Utilities.getParent(dataSnapshot);
                                                if (parent != null)
                                                    parents_list.add(parent);

                                                if (position == allComplaints_list.size() - 1)
                                                    fillListView(allComplaints_list, parents_list, replies_list);
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
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        search_edittext.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (charSequence.toString().trim().length() == 0)
                    listView.setAdapter(adapter);
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        search_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String date = search_edittext.getText().toString();
                if (TextUtils.isEmpty(date)) {
                    listView.setAdapter(adapter);
                } else {
                    ArrayList<Complaint> list1 = adapter.complaints_list;
                    ArrayList<Parent> list2 = adapter.parents_list;
                    ArrayList<Complaint> newList1 = new ArrayList<>();
                    ArrayList<Parent> newList2 = new ArrayList<>();

                    for (int i = 0; i < list1.size(); i++) {
                        if (list1.get(i).date.equalsIgnoreCase(date)) {
                            newList1.add(list1.get(i));
                            newList2.add(list2.get(i));
                        }
                    }
                    ComplaintsAdapter searchAdapter = new ComplaintsAdapter(ComplaintsActivity.this, newList1, newList2);
                    listView.setAdapter(searchAdapter);
                }
            }
        });

        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                DialogFragment dialog = AddComplaintDialogFragment.newInstance(userType);
                dialog.show(getSupportFragmentManager(), "tag");
            }
        });

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                Complaint complaint = (Complaint) adapterView.getAdapter().getItem(i);
                Parent parent = (Parent) adapter.getItem2(i);

                Intent intent = new Intent(ComplaintsActivity.this, ComplaintsDetailedActivity.class);
                intent.putExtra("complaint", complaint);
                intent.putExtra("fullname", parent.fullname);
                intent.putExtra("userType", userType);
                startActivity(intent);
            }
        });
    }

    private void fillListView(ArrayList<Complaint> allComplaints_list, ArrayList<Parent> parents_list
            , ArrayList<ArrayList<Msg>> replies_list) {

        if (userType.equals("Manager"))
            adapter = new ComplaintsAdapter(this, allComplaints_list, parents_list, replies_list);

        else if (!userType.equals("Student")) {

            ArrayList<Complaint> specificUserComplaints_list = new ArrayList<>();
            ArrayList<Parent> specificUserParents_list = new ArrayList<>();
            ArrayList<ArrayList<Msg>> specificUserReplies_list = new ArrayList<>();

            for (int i = 0; i < parents_list.size(); i++) {
                if (allComplaints_list.get(i).senderUID.equals(Utilities.getCurrentUID())) {
                    specificUserComplaints_list.add(allComplaints_list.get(i));
                    specificUserParents_list.add(parents_list.get(i));
                    specificUserReplies_list.add(replies_list.get(i));
                }
                adapter = new ComplaintsAdapter(this, specificUserComplaints_list, specificUserParents_list
                        , specificUserReplies_list);
            }
        }
        if (adapter != null) {
            if (adapter.getCount() == 0)
                noComplaints.setVisibility(View.VISIBLE);
            else {
                if (noComplaints.getVisibility() == View.VISIBLE)
                    noComplaints.setVisibility(View.GONE);

                listView.setAdapter(adapter);
            }
        }
    }

    private ArrayList<Complaint> reverseList
            (ArrayList<Complaint> reversed_competitions_list) {
        ArrayList<Complaint> reversed_list = new ArrayList<>();

        for (int i = reversed_competitions_list.size() - 1; i >= 0; i--) {
            reversed_list.add(reversed_competitions_list.get(i));
        }

        return reversed_list;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        // Unregister VisualizerActivity as an OnPreferenceChangedListener to avoid any memory leaks.
        PreferenceManager.getDefaultSharedPreferences(this);
    }
}
