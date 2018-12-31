package com.safwat.abanoub.bridgeit;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Date;

public class AnnouncementsActivity extends AppCompatActivity {

    ListView listView;
    TextView noAnnouncements;
    ImageButton add_btn;
    AnnouncementsAdapter announcementsAdapter;
    private String userType;

    FirebaseDatabase firebaseDatabase;
    DatabaseReference announcementsReference;
    private String user_uid;
    private ArrayList<Announcement> announcements_list;
    private int addBtnClickedRequestCode = 88;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_announcements);

        String usedAsParent = getIntent().getStringExtra("usedAsParent");
        if (usedAsParent != null) //parent
            user_uid = getIntent().getStringExtra("clicked_user_uid");

        else //student or teacher
            user_uid = Utilities.getCurrentUID();

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(AnnouncementsActivity.this);
        //reading from shared preference
        userType = sharedPreferences.getString("userType", "not found");

        listView = findViewById(R.id.list);
        noAnnouncements = findViewById(R.id.noAnnouncements);
        add_btn = findViewById(R.id.add);

        firebaseDatabase = FirebaseDatabase.getInstance();

        if (userType.equals("Student") || usedAsParent != null) {
            add_btn.setVisibility(View.GONE);

            firebaseDatabase.getReference().child("students").child(user_uid)
                    .child("class").child("id").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                    String studentClassID = Utilities.getStudentClassID(dataSnapshot);

                    firebaseDatabase.getReference().child("Classes").child(studentClassID)
                            .addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                    final Classroom classroom = Utilities.getClassroom(dataSnapshot);

                                    announcementsReference = firebaseDatabase.getReference().child("announcements").child("Student")
                                            .child("Years").child(classroom.yearName).child(classroom.className);
                                    announcementsReference.addValueEventListener(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                            ArrayList<Announcement> reversed_announcements_list =
                                                    Utilities.getAllAnnouncements(dataSnapshot);
                                            announcements_list = reverseList(reversed_announcements_list);

                                            final ArrayList<String> userNames_list = new ArrayList<>();

                                            for (int i = 0; i < announcements_list.size(); i++) {
                                                final int position = i;
                                                Announcement announcement = announcements_list.get(position);

                                                firebaseDatabase.getReference().child(announcement.senderNode)
                                                        .child(announcement.postedBy).addValueEventListener(new ValueEventListener() {
                                                    @Override
                                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                        userNames_list.add(Utilities.getUserFullName(dataSnapshot));

                                                        if (position == announcements_list.size() - 1)
                                                            fillListView(announcements_list, userNames_list, null);
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

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {

                                }
                            });
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });

        } else if (userType.equals("Doctor")||userType.equals("Parent") ) {
            add_btn.setVisibility(View.GONE);
            announcementsReference = firebaseDatabase.getReference().child("announcements").child(userType);

        } else if (userType.equals("Teacher") || userType.equals("PE") || userType.equals("Bus Admin")) {
            add_btn.setVisibility(View.VISIBLE);
            announcementsReference = firebaseDatabase.getReference().child("announcements").child(userType);
        }

        if (userType.equals("Manager") && usedAsParent == null) { //load all user's announcements
            add_btn.setVisibility(View.VISIBLE);
            ((TextView) findViewById(R.id.title)).setText("All Announcements");

            final DataBaseAdapter helper = new DataBaseAdapter(this);

            final ArrayList<String> yearsList = helper.getAllYears();

            final ArrayList<String> postedTo_list = new ArrayList<>();
            final ArrayList<String> sorted_postedTo_list = new ArrayList<>();

            firebaseDatabase.getReference().child("announcements").child("Parent")
                    .addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            final ArrayList<Announcement> parents_announcements_list = Utilities.getAllAnnouncements(dataSnapshot);

                            firebaseDatabase.getReference().child("announcements").child("Doctor")
                                    .addValueEventListener(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                            final ArrayList<Announcement> doctors_announcements_list = Utilities
                                                    .getAllAnnouncements(dataSnapshot);

                                            firebaseDatabase.getReference().child("announcements").child("Teacher")
                                                    .addValueEventListener(new ValueEventListener() {
                                                        @Override
                                                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                            final ArrayList<Announcement> teachers_announcements_list =
                                                                    Utilities.getAllAnnouncements(dataSnapshot);

                                                            firebaseDatabase.getReference().child("announcements").child("PE")
                                                                    .addValueEventListener(new ValueEventListener() {
                                                                        @Override
                                                                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                                            final ArrayList<Announcement> PEs_announcements_list
                                                                                    = Utilities.getAllAnnouncements(dataSnapshot);

                                                                            firebaseDatabase.getReference().child("announcements")
                                                                                    .child("Bus Admin")
                                                                                    .addValueEventListener(new ValueEventListener() {
                                                                                        @Override
                                                                                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                                                            final ArrayList<Announcement> busAdmins_announcements_list = Utilities.getAllAnnouncements(dataSnapshot);

                                                                                            //get all student's announcements
                                                                                            final ArrayList<Announcement> all_students_announcements_list = new ArrayList<>();

                                                                                            for (int i = 0; i < yearsList.size(); i++) {
                                                                                                final int position = i;

                                                                                                final String year = yearsList.get(position);
                                                                                                final ArrayList<String> classesOfThisYear = helper.getAllClassesFromYear(year);

                                                                                                for (int j = 0; j < classesOfThisYear.size(); j++) {
                                                                                                    final int position2 = j;

                                                                                                    firebaseDatabase.getReference().child("announcements").child("Student")
                                                                                                            .child("Years").child(year).child(classesOfThisYear.get(position2))
                                                                                                            .addValueEventListener(new ValueEventListener() {
                                                                                                                @Override
                                                                                                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                                                                                    final ArrayList<Announcement> students_announcements_list = Utilities.getAllAnnouncements(dataSnapshot);

                                                                                                                    all_students_announcements_list.addAll(students_announcements_list);

                                                                                                                    for (int i = 0; i < students_announcements_list.size(); i++)
                                                                                                                        postedTo_list.add("Students:" + year + ":" + classesOfThisYear.get(position2));

                                                                                                                    if (position == yearsList.size() - 1 && position2 == classesOfThisYear.size() - 1) {

                                                                                                                        all_students_announcements_list.addAll(parents_announcements_list);
                                                                                                                        for (int i = 0; i < parents_announcements_list.size(); i++)
                                                                                                                            postedTo_list.add("Parents");

                                                                                                                        all_students_announcements_list.addAll(doctors_announcements_list);
                                                                                                                        for (int i = 0; i < doctors_announcements_list.size(); i++)
                                                                                                                            postedTo_list.add("Doctors");

                                                                                                                        all_students_announcements_list.addAll(teachers_announcements_list);
                                                                                                                        for (int i = 0; i < teachers_announcements_list.size(); i++)
                                                                                                                            postedTo_list.add("Teachers");

                                                                                                                        all_students_announcements_list.addAll(PEs_announcements_list);
                                                                                                                        for (int i = 0; i < PEs_announcements_list.size(); i++)
                                                                                                                            postedTo_list.add("PEs");

                                                                                                                        all_students_announcements_list.addAll(busAdmins_announcements_list);
                                                                                                                        for (int i = 0; i < busAdmins_announcements_list.size(); i++)
                                                                                                                            postedTo_list.add("Bus Admins");

                                                                                                                        final ArrayList<Announcement> sorted_announcements_list = new ArrayList<>();

                                                                                                                        if (all_students_announcements_list.size() > 0) {
                                                                                                                            sorted_announcements_list.add(all_students_announcements_list.get(0));
                                                                                                                            sorted_postedTo_list.add(postedTo_list.get(0));
                                                                                                                        }

                                                                                                                        for (int i = 1; i < all_students_announcements_list.size(); i++) {

                                                                                                                            Date date = new Date(Long.parseLong(all_students_announcements_list.get(i).date));

                                                                                                                            for (int j = sorted_announcements_list.size() - 1; j >= 0; j--) {

                                                                                                                                Date specific_date = new Date(Long.parseLong(sorted_announcements_list.get(j).date));

                                                                                                                                if (date.before(specific_date)) {
                                                                                                                                    sorted_announcements_list.add(j + 1, all_students_announcements_list.get(i));
                                                                                                                                    sorted_postedTo_list.add(j + 1, postedTo_list.get(i));
                                                                                                                                    break;
                                                                                                                                } else if (j == 0) {
                                                                                                                                    sorted_announcements_list.add(j, all_students_announcements_list.get(i));
                                                                                                                                    sorted_postedTo_list.add(j, postedTo_list.get(i));
                                                                                                                                    break;
                                                                                                                                }
                                                                                                                            }
                                                                                                                        }

                                                                                                                        final ArrayList<String> userNames_list = new ArrayList<>();

                                                                                                                        for (int i = 0; i < sorted_announcements_list.size(); i++) {

                                                                                                                            final int position3 = i;
                                                                                                                            Announcement announcement = sorted_announcements_list.get(position3);
                                                                                                                            firebaseDatabase.getReference().child(announcement.senderNode)
                                                                                                                                    .child(announcement.postedBy).addValueEventListener(new ValueEventListener() {
                                                                                                                                @Override
                                                                                                                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                                                                                                    userNames_list.add(Utilities.getUserFullName(dataSnapshot));

                                                                                                                                    if (position3 == sorted_announcements_list.size() - 1)
                                                                                                                                        fillListView(sorted_announcements_list, userNames_list, sorted_postedTo_list);
                                                                                                                                }

                                                                                                                                @Override
                                                                                                                                public void onCancelled(@NonNull DatabaseError databaseError) {

                                                                                                                                }
                                                                                                                            });
                                                                                                                        }
                                                                                                                    }
                                                                                                                }

                                                                                                                @Override
                                                                                                                public void onCancelled(@NonNull DatabaseError databaseError) {

                                                                                                                }
                                                                                                            });
                                                                                                }
                                                                                            }
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
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });

        } else if (!userType.equals("Student") && usedAsParent == null) {

            announcementsReference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    ArrayList<Announcement> reversed_announcements_list = Utilities.getAllAnnouncements(dataSnapshot);
                    announcements_list = reverseList(reversed_announcements_list);

                    final ArrayList<String> userNames_list = new ArrayList<>();

                    if (announcements_list.size() == 0)
                        fillListView(announcements_list, userNames_list, null);
                    else {
                        for (int i = 0; i < announcements_list.size(); i++) {
                            final int position = i;
                            Announcement announcement = announcements_list.get(position);

                            firebaseDatabase.getReference().child(announcement.senderNode)
                                    .child(announcement.postedBy).addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                    userNames_list.add(Utilities.getUserFullName(dataSnapshot));

                                    if (position == announcements_list.size() - 1)
                                        fillListView(announcements_list, userNames_list, null);
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {

                                }
                            });
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }

        add_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivityForResult(new Intent(AnnouncementsActivity.this, AddAnnouncementsActivity.class), addBtnClickedRequestCode);
            }
        });
    }

    private ArrayList<Announcement> reverseList(ArrayList<Announcement> reversed_competitions_list) {
        ArrayList<Announcement> reversed_list = new ArrayList<>();

        for (int i = reversed_competitions_list.size() - 1; i >= 0; i--) {
            reversed_list.add(reversed_competitions_list.get(i));
        }

        return reversed_list;
    }

    private void fillListView(ArrayList<Announcement> announcements_list, ArrayList<String> userNames_list, ArrayList<String> postedTo_list) {
        if (announcements_list.size() == 0)
            noAnnouncements.setVisibility(View.VISIBLE);
        else {
            if (noAnnouncements.getVisibility() == View.VISIBLE)
                noAnnouncements.setVisibility(View.GONE);

            announcementsAdapter = new AnnouncementsAdapter(this, announcements_list, userNames_list, postedTo_list);
            listView.setAdapter(announcementsAdapter);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (userType.equals("Manager") && requestCode == addBtnClickedRequestCode && resultCode == RESULT_OK) {
            finish();
            startActivity(getIntent());
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        // Unregister VisualizerActivity as an OnPreferenceChangedListener to avoid any memory leaks.
        PreferenceManager.getDefaultSharedPreferences(this);
    }
}
