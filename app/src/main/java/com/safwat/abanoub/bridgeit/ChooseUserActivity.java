package com.safwat.abanoub.bridgeit;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class ChooseUserActivity extends AppCompatActivity {

    Spinner typeSpinner, yearSpinner, classSpinner;
    ListView listView;
    TextView noUsers;
    TextView toolbarTitle;
    LinearLayout typeLinear, yearLinear, classLinear;
    String activityType;
    ArrayList<String> studentsUID_list;
    String[] users;
    String typeSelected = "Student";
    ArrayList<Student> students_list;

    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;
    private ArrayList<String> yearsList, classesList;
    private String userType;
    private ProfileFragment profileFragment;
    ArrayAdapter<String> classesAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_user);

        activityType = getIntent().getExtras().getString("activityType");

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(ChooseUserActivity.this);
        //reading from shared preference
        userType = sharedPreferences.getString("userType", "not found");

        toolbarTitle = findViewById(R.id.toolbarTitle);
        typeSpinner = findViewById(R.id.typeSpinner);
        yearSpinner = findViewById(R.id.yearSpinner);
        classSpinner = findViewById(R.id.classSpinner);
        typeLinear = findViewById(R.id.typeLinear);
        yearLinear = findViewById(R.id.yearLinear);
        classLinear = findViewById(R.id.classLinear);
        listView = findViewById(R.id.list);
        noUsers = findViewById(R.id.noUsers);

        if (activityType.equals("Messages")) {
            toolbarTitle.setText("Messages");

            if (userType.equals("Student")) {
                users = new String[]{"Student", "Teacher", "PE", "Doctor", "Bus Admin"};
                yearLinear.setVisibility(View.GONE);
                classLinear.setVisibility(View.GONE);

            } else if (userType.equals("Teacher")) {
                users = new String[]{"Student", "Parent", "Teacher", "PE", "Doctor", "Bus Admin"};
                yearLinear.setVisibility(View.VISIBLE);
                classLinear.setVisibility(View.VISIBLE);

            } else if (userType.equals("Parent")) {
                users = new String[]{"Teacher", "PE", "Doctor", "Bus Admin"};
                yearLinear.setVisibility(View.GONE);
                classLinear.setVisibility(View.GONE);

            } else if (userType.equals("Doctor")) {
                users = new String[]{"Student", "Parent", "Teacher", "PE", "Doctor", "Bus Admin"};
                yearLinear.setVisibility(View.VISIBLE);
                classLinear.setVisibility(View.VISIBLE);

            } else if (userType.equals("PE")) {
                users = new String[]{"Student", "Parent", "Teacher", "PE", "Doctor", "Bus Admin"};
                yearLinear.setVisibility(View.VISIBLE);
                classLinear.setVisibility(View.VISIBLE);

            } else if (userType.equals("Bus Admin")) {
                users = new String[]{"Student", "Parent", "Teacher", "PE", "Doctor", "Bus Admin"};
                yearLinear.setVisibility(View.VISIBLE);
                classLinear.setVisibility(View.VISIBLE);
            }

            if (users != null) {
                ArrayAdapter<String> adapter1 = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item
                        , users);
                adapter1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                typeSpinner.setAdapter(adapter1);
            }
        } else {
            typeLinear.setVisibility(View.GONE);
        }

        firebaseDatabase = FirebaseDatabase.getInstance();

        typeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                typeSelected = (String) adapterView.getAdapter().getItem(i);

                if (typeSelected.equals("Student")) {
                    yearLinear.setVisibility(View.VISIBLE);
                    classLinear.setVisibility(View.VISIBLE);

                    if (classSpinner != null && classesAdapter != null)
                        classSpinner.setAdapter(classesAdapter);

                } else {
                    yearLinear.setVisibility(View.GONE);
                    classLinear.setVisibility(View.GONE);

                    if (typeLinear.getVisibility() == View.VISIBLE) {

                        if (typeSelected.equals("Parent")) {

                            databaseReference = firebaseDatabase.getReference().child("parents");
                            databaseReference.addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                    final ArrayList<Parent> parents_list = Utilities.getAllParents(dataSnapshot);
                                    final ArrayList<Parent> modified_parents_list = new ArrayList<>();

                                    for (int i = 0; i < parents_list.size(); i++) {
                                        final int position=i;

                                        Parent parent = parents_list.get(position);
                                        if (parent.fullname != null) {
                                            modified_parents_list.add(parent);

                                            if (position == parents_list.size()-1)
                                                fillParentListView(modified_parents_list);

                                        } else if (parent.node != null) {
                                            firebaseDatabase.getReference().child(parent.node).child(parent.uid)
                                                    .addValueEventListener(new ValueEventListener() {
                                                        @Override
                                                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                                                            Parent parent = Utilities.getParent(dataSnapshot);
                                                            if (parent != null)
                                                                modified_parents_list.add(parent);

                                                            if (position == parents_list.size()-1)
                                                                fillParentListView(modified_parents_list);
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

                        } else {
                            if (typeSelected.equals("Teacher"))
                                databaseReference = firebaseDatabase.getReference().child("teachers");

                            else if (typeSelected.equals("PE"))
                                databaseReference = firebaseDatabase.getReference().child("PEs");

                            else if (typeSelected.equals("Doctor"))
                                databaseReference = firebaseDatabase.getReference().child("doctors");

                            else if (typeSelected.equals("Bus Admin"))
                                databaseReference = firebaseDatabase.getReference().child("busAdmins");

                            if (databaseReference != null) {
                                databaseReference.addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                        ArrayList<Teacher> list = Utilities.getAllTeachers(dataSnapshot);
                                        TeachersAdapter adapter = new TeachersAdapter(ChooseUserActivity.this, list);
                                        listView.setAdapter(adapter);
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError databaseError) {

                                    }
                                });
                            }
                        }
                    }
                }
//                else if (userType.equals("Doctor")) {
//                    yearLinear.setVisibility(View.VISIBLE);
//                    classLinear.setVisibility(View.VISIBLE);
//                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        final DataBaseAdapter helper = new DataBaseAdapter(this);

        yearsList = helper.getAllYears();

        final ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, yearsList);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        yearSpinner.setAdapter(adapter);

        yearSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {

                String selectedYear = (String) parentView.getAdapter().getItem(position);
                classesList = helper.getAllClassesFromYear(selectedYear);

                classesAdapter = new ArrayAdapter<>(ChooseUserActivity.this
                        , android.R.layout.simple_spinner_item, classesList);
                classesAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                classSpinner.setAdapter(classesAdapter);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {

            }

        });

        classSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

                studentsUID_list = new ArrayList<>();
                students_list = new ArrayList<>();

                if (yearLinear.getVisibility() == View.VISIBLE) { //get students
                    databaseReference = firebaseDatabase.getReference().child("Years").child(yearSpinner.getSelectedItem().toString())
                            .child(classSpinner.getSelectedItem().toString()).child("students");
                    databaseReference.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                            studentsUID_list = Utilities.getUIDs(dataSnapshot);

                            if (studentsUID_list.size() == 0)
                                listView.setAdapter(new StudentsAdapter(ChooseUserActivity.this, new ArrayList<Student>()));
                            else {
                                for (int i = 0; i< studentsUID_list.size(); i++) {
                                    final int position=i;

                                    String user_uid = studentsUID_list.get(position);
                                    //user personal data
                                    firebaseDatabase.getReference().child("students").child(user_uid)
                                            .addValueEventListener(new ValueEventListener() {
                                                @Override
                                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                                                    Student student = Utilities.getStudent(dataSnapshot);
                                                    students_list.add(student);

                                                    if (position == studentsUID_list.size()-1)
                                                        fillListView(students_list);
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

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                Intent intent = null;

                if (activityType.equals("Complaints")) {
                    intent = new Intent(ChooseUserActivity.this, ComplaintsActivity.class);
                    intent.putExtra("clickedStudentUID", studentsUID_list.get(i));

                } else if (activityType.equals("Messages")) {

                    intent = new Intent(ChooseUserActivity.this, MessagesActivity.class);
                    if (typeSelected.equals("Student")) {
                        intent.putExtra("clickedUserType", "students");
                        intent.putExtra("clickedUserUID", ((Student) adapterView.getAdapter().getItem(i)).uid);

                    } else if (typeSelected.equals("Parent")) {
                        intent.putExtra("clickedUserType", "parents");
                        intent.putExtra("clickedUserUID", ((Parent) adapterView.getAdapter().getItem(i)).uid);

                    } else if (typeSelected.equals("Teacher")) {
                        intent.putExtra("clickedUserType", "teachers");
                        intent.putExtra("clickedUserUID", ((Teacher) adapterView.getAdapter().getItem(i)).uid);

                    } else if (typeSelected.equals("PE")) {
                        intent.putExtra("clickedUserType", "PEs");
                        intent.putExtra("clickedUserUID", ((Teacher) adapterView.getAdapter().getItem(i)).uid);

                    } else if (typeSelected.equals("Doctor")) {
                        intent.putExtra("clickedUserType", "doctors");
                        intent.putExtra("clickedUserUID", ((Teacher) adapterView.getAdapter().getItem(i)).uid);

                    } else if (typeSelected.equals("Bus Admin")) {
                        intent.putExtra("clickedUserType", "busAdmins");
                        intent.putExtra("clickedUserUID", ((Teacher) adapterView.getAdapter().getItem(i)).uid);
                    }

                } else if (activityType.equals("Doctor Reports")) {

                    Intent intent2 = new Intent();
                    intent2.putExtra("clickedUserUID", ((Student) adapterView.getAdapter().getItem(i)).uid);
                    intent2.putExtra("clickedUserFullname", ((Student) adapterView.getAdapter().getItem(i)).fullname);
                    setResult(RESULT_OK, intent2);
                    finish();

                } else if (activityType.equals("Student Feedbacks")) {
                    profileFragment = new ProfileFragment();
                    Bundle bundle = new Bundle();
                    bundle.putString("uid", ((Student) adapterView.getAdapter().getItem(i)).uid);
                    bundle.putString("fragmentUsedAs", "Feedback");
                    profileFragment.setArguments(bundle);

                    getSupportFragmentManager().beginTransaction().replace(R.id.frameLayout, profileFragment).commit();

//                    intent = new Intent(ChooseUserActivity.this,StudentFeedbacksActivity.class);
//                    intent.putExtra("clickedUserType", "student");
//                    intent.putExtra("clickedUserUID", ((Student) adapterView.getAdapter().getItem(i)).uid);
                }
//        else if (activityType.equals("Complaints"))
//            intent=new Intent(ChooseUserActivity.this,.class);
//            intent.putExtra("",);
//        else if (activityType.equals("Complaints"))
//            intent=new Intent(ChooseUserActivity.this,.class);
//            intent.putExtra("",);

                if (intent != null)
                    startActivity(intent);
            }
        });
    }

    private void fillParentListView(ArrayList<Parent> modified_parents_list) {
        if (modified_parents_list.size() == 0)
            noUsers.setVisibility(View.VISIBLE);
        else {
            if (noUsers.getVisibility() == View.VISIBLE)
                noUsers.setVisibility(View.GONE);

            ParentsAdapter adapter = new ParentsAdapter(ChooseUserActivity.this, modified_parents_list);
            listView.setAdapter(adapter);
        }
    }


    private void fillListView(ArrayList<Student> students_list) {
        if (students_list.size() == 0)
            noUsers.setVisibility(View.VISIBLE);
        else {
            if (noUsers.getVisibility() == View.VISIBLE)
                noUsers.setVisibility(View.GONE);

            StudentsAdapter adapter = new StudentsAdapter(ChooseUserActivity.this, students_list);
            listView.setAdapter(adapter);
        }
    }

    @Override
    public void onBackPressed() {

        if (profileFragment != null) {
            getSupportFragmentManager().beginTransaction().detach(profileFragment).commit();
            profileFragment = null;

        } else
            super.onBackPressed();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        // Unregister VisualizerActivity as an OnPreferenceChangedListener to avoid any memory leaks.
        PreferenceManager.getDefaultSharedPreferences(this);
    }
}
