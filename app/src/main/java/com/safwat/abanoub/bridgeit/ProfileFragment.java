package com.safwat.abanoub.bridgeit;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class ProfileFragment extends Fragment {

    CircleImageView profile_img;
    TextView fullname, username,changePassword,grades_btn,calendar_btn,announcements_btn,doctorReports_btn;
    FloatingActionButton cameraFab;

    FirebaseDatabase firebaseDatabase;
    DatabaseReference personalDataReference;
    private String userType, fragmentUsedAs;
    String clicked_user_uid;

    ListView portfolioListView;
    DatabaseReference portfolioReference;

    TextView noSchoolarFeedbacks, noMedicalFeedbacks, noActivityFeedbacks;
    ListView schoolarFeedbacksListView, medicalFeedbacksListView, activityFeedbacksListView;
    ImageButton addSchoolarFeedback, addMedicalFeedback, addActivityFeedback;
    StudentFeedbacksAdapter adapter;

    DatabaseReference studentReference, parentReference, schoolarReference, medicalReference, activityReference;
    Student student;

    LinearLayout portfolioFeedbacksLinear,childButtons_linear;
    private String password;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        profile_img = view.findViewById(R.id.profile_image);
        fullname = view.findViewById(R.id.fullname);
        username= view.findViewById(R.id.username);
        cameraFab = view.findViewById(R.id.cameraFab);
        schoolarFeedbacksListView = view.findViewById(R.id.schoolarFeedbacksList);
        medicalFeedbacksListView = view.findViewById(R.id.medicalFeedbacksList);
        activityFeedbacksListView = view.findViewById(R.id.activityFeedbacksList);
        noSchoolarFeedbacks = view.findViewById(R.id.noSchoolarFeedbacks);
        noMedicalFeedbacks = view.findViewById(R.id.noMedicalFeedbacks);
        noActivityFeedbacks = view.findViewById(R.id.noActivityFeedbacks);
        addSchoolarFeedback = view.findViewById(R.id.addSchoolarFeedback);
        addMedicalFeedback = view.findViewById(R.id.addMedicalFeedback);
        addActivityFeedback = view.findViewById(R.id.addActivityFeedback);
        portfolioListView = view.findViewById(R.id.list1);
        portfolioFeedbacksLinear = view.findViewById(R.id.portfolioFeedbacksLinear);
        changePassword = (TextView) view.findViewById(R.id.changePassword);
        childButtons_linear=view.findViewById(R.id.childButtons_linear);
        grades_btn=view.findViewById(R.id.grades_btn);
        calendar_btn=view.findViewById(R.id.calendar_btn);
        announcements_btn=view.findViewById(R.id.announcements_btn);
        doctorReports_btn=view.findViewById(R.id.doctorReports_btn);

        clicked_user_uid = getArguments().getString("uid");
        fragmentUsedAs = getArguments().getString("fragmentUsedAs");

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getContext());
        //reading from shared preference
        userType = sharedPreferences.getString("userType", "not found");

        firebaseDatabase = FirebaseDatabase.getInstance();

        if (fragmentUsedAs.equals("Profile"))
            changePassword.setVisibility(View.VISIBLE);

        else if (fragmentUsedAs.equals("ParentViewingChildData")) {
            childButtons_linear.setVisibility(View.VISIBLE);
            addSchoolarFeedback.setVisibility(View.GONE);
            addMedicalFeedback.setVisibility(View.GONE);
            addActivityFeedback.setVisibility(View.GONE);
            portfolioFeedbacksLinear.setVisibility(View.VISIBLE);

            personalDataReference = firebaseDatabase.getReference().child("students").child(clicked_user_uid);
            loadStudentPortfolio(clicked_user_uid);
        }

        if ((fragmentUsedAs.equals("Profile") && userType.equals("Student"))
                || (fragmentUsedAs.equals("Feedback") && !userType.equals("Student"))) {
            portfolioFeedbacksLinear.setVisibility(View.VISIBLE);

            if ((fragmentUsedAs.equals("Profile")) && userType.equals("Student")) {
                addSchoolarFeedback.setVisibility(View.GONE);
                addMedicalFeedback.setVisibility(View.GONE);
                addActivityFeedback.setVisibility(View.GONE);

            }else if ((fragmentUsedAs.equals("Feedback")) && userType.equals("Teacher")){
                addSchoolarFeedback.setVisibility(View.VISIBLE);
                addMedicalFeedback.setVisibility(View.GONE);
                addActivityFeedback.setVisibility(View.GONE);

            }else if ((fragmentUsedAs.equals("Feedback")) && userType.equals("Doctor")){
                addSchoolarFeedback.setVisibility(View.GONE);
                addMedicalFeedback.setVisibility(View.VISIBLE);
                addActivityFeedback.setVisibility(View.GONE);

            }else if ((fragmentUsedAs.equals("Feedback")) && userType.equals("PE")){
                addSchoolarFeedback.setVisibility(View.GONE);
                addMedicalFeedback.setVisibility(View.GONE);
                addActivityFeedback.setVisibility(View.VISIBLE);

            } else{
                addSchoolarFeedback.setVisibility(View.GONE);
                addMedicalFeedback.setVisibility(View.GONE);
                addActivityFeedback.setVisibility(View.GONE);
            }

            personalDataReference = firebaseDatabase.getReference().child("students").child(clicked_user_uid);

            loadStudentPortfolio(clicked_user_uid);

        } else if (fragmentUsedAs.equals("Profile") && userType.equals("Teacher")) {
            personalDataReference = firebaseDatabase.getReference().child("teachers").child(clicked_user_uid);

        } else if (fragmentUsedAs.equals("Profile") && userType.equals("Parent")) {
            personalDataReference = firebaseDatabase.getReference().child("parents").child(clicked_user_uid);

        } else if (fragmentUsedAs.equals("Profile") && userType.equals("Bus Admin")) {
            personalDataReference = firebaseDatabase.getReference().child("busAdmins").child(clicked_user_uid);

        } else if (fragmentUsedAs.equals("Profile") && userType.equals("Manager")) {
            personalDataReference = firebaseDatabase.getReference().child("managers").child(clicked_user_uid);

        } else if (fragmentUsedAs.equals("Profile") && userType.equals("Doctor")) {
            personalDataReference = firebaseDatabase.getReference().child("doctors").child(clicked_user_uid);
        }

        cameraFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Create and show the dialog and pass the user as an argument with static method.
                DialogFragment dialog = ChangeProfilePicDialogFragment.newInstance(userType);
                dialog.show(getFragmentManager(), "tag");
            }
        });

        // student Feedbacks and user's personal data
        personalDataReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if ((fragmentUsedAs.equals("Profile") && userType.equals("Student"))
                        || (fragmentUsedAs.equals("Feedback") && !userType.equals("Student"))
                        ||fragmentUsedAs.equals("ParentViewingChildData")) {
                    student = Utilities.getStudent(dataSnapshot);
                    fillPersonalData(student.profileURL, student.fullname, student.username,student.password);

                    parentReference = firebaseDatabase.getReference().child("parents").child(student.parent);
                    parentReference.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            Parent parent = Utilities.getParent(dataSnapshot);

                            schoolarReference = firebaseDatabase.getReference().child("feedback").child("schoolar").child(student.uid);
                            schoolarReference.addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                    ArrayList<StudentFeedback> reversed_studentFeedback = Utilities.getAllFeedbacks(dataSnapshot);
                                    ArrayList<StudentFeedback> studentFeedback = reverseList(reversed_studentFeedback);

                                    fillSchoolarFeedbackListView(studentFeedback);

                                    medicalReference = firebaseDatabase.getReference().child("feedback")
                                            .child("medical").child(student.uid);
                                    medicalReference.addValueEventListener(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                            ArrayList<StudentFeedback> reversed_studentFeedback =
                                                    Utilities.getAllFeedbacks(dataSnapshot);
                                            ArrayList<StudentFeedback> studentFeedback = reverseList(reversed_studentFeedback);

                                            fillMedicalFeedbackListView(studentFeedback);

                                            activityReference = firebaseDatabase.getReference().child("feedback")
                                                    .child("activity").child(student.uid);
                                            activityReference.addValueEventListener(new ValueEventListener() {
                                                @Override
                                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                    ArrayList<StudentFeedback> reversed_studentFeedback =
                                                            Utilities.getAllFeedbacks(dataSnapshot);
                                                    ArrayList<StudentFeedback> studentFeedback = reverseList(reversed_studentFeedback);

                                                    fillActivityFeedbackListView(studentFeedback);

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

                } else if (fragmentUsedAs.equals("Profile") && userType.equals("Teacher")) {
                    Teacher teacher = Utilities.getTeacher(dataSnapshot);
                    fillPersonalData(teacher.profileURL, teacher.fullname, teacher.username,teacher.password);

                } else if (fragmentUsedAs.equals("Profile") && userType.equals("Parent")) {
                    Parent parent = Utilities.getParent(dataSnapshot);
                    fillPersonalData(parent.profileURL, parent.fullname, parent.username,parent.password);

                } else if (userType.equals("Bus Admin")) {
                    Teacher teacher = Utilities.getTeacher(dataSnapshot);
                    fillPersonalData(teacher.profileURL, teacher.fullname, teacher.username,teacher.password);

                } else if (fragmentUsedAs.equals("Profile") && userType.equals("Manager")) {
                    Teacher teacher = Utilities.getTeacher(dataSnapshot);
                    fillPersonalData(teacher.profileURL, teacher.fullname, teacher.username,teacher.password);

                } else if (fragmentUsedAs.equals("Profile") && userType.equals("Doctor")) {
                    Teacher teacher = Utilities.getTeacher(dataSnapshot);
                    fillPersonalData(teacher.profileURL, teacher.fullname, teacher.username,teacher.password);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        addSchoolarFeedback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (student != null) {
                    // Create and show the dialog and pass the user as an argument with static method.
                    DialogFragment dialog = AddStudentFeedbackDialogFragment.newInstance("schoolar", student.uid
                            , student.fullname,userType);
                    dialog.show(getFragmentManager(), "tag");
                }
            }
        });

        addMedicalFeedback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (student != null) {
                    // Create and show the dialog and pass the user as an argument with static method.
                    DialogFragment dialog = AddStudentFeedbackDialogFragment.newInstance("medical", student.uid
                            , student.fullname,userType);
                    dialog.show(getFragmentManager(), "tag");
                }
            }
        });

        addActivityFeedback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (student != null) {
                    // Create and show the dialog and pass the user as an argument with static method.
                    DialogFragment dialog = AddStudentFeedbackDialogFragment.newInstance("activity", student.uid
                            , student.fullname,userType);
                    dialog.show(getFragmentManager(), "tag");
                }
            }
        });

        changePassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Create and show the dialog and pass the user as an argument with static method.
                DialogFragment dialog = ChangePasswordDialogFragment.newInstance(clicked_user_uid,userType,password);
                dialog.show(getFragmentManager(), "tag");
            }
        });

        grades_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(getActivity(), Grades1Activity.class);
                intent.putExtra("usedAsParent","yes");
                intent.putExtra("clicked_user_uid",clicked_user_uid);
                startActivity(intent);
            }
        });

        calendar_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(getActivity(), CalendarActivity.class);
                intent.putExtra("usedAsParent","yes");
                intent.putExtra("clicked_user_uid",clicked_user_uid);
                startActivity(intent);
            }
        });

        announcements_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(getActivity(), AnnouncementsActivity.class);
                intent.putExtra("usedAsParent","yes");
                intent.putExtra("clicked_user_uid",clicked_user_uid);
                startActivity(intent);
            }
        });

        doctorReports_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(getActivity(), DoctorReportsActivity.class);
                intent.putExtra("usedAsParent","yes");
                intent.putExtra("clicked_user_uid",clicked_user_uid);
                startActivity(intent);
            }
        });

        return view;
    }

    private void loadStudentPortfolio(final String clicked_user_uid) {
        // student portfolio
        firebaseDatabase.getReference().child("students").child(clicked_user_uid)
                .child("class").child("id").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                String studentClassID = Utilities.getStudentClassID(dataSnapshot);

                firebaseDatabase.getReference().child("Classes").child(studentClassID)
                        .addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                final Classroom classroom = Utilities.getClassroom(dataSnapshot);

                                firebaseDatabase.getReference().child("Years").child(classroom.yearName)
                                        .child(classroom.className).child("subjects").addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                                        final ArrayList<String> subjects_list = Utilities.getAllSubjects(dataSnapshot);
                                        final ArrayList<Portfolio> portfolio_list = new ArrayList<>();

                                        for (int i = 0; i < subjects_list.size(); i++) {
                                            final int position=i;

                                            portfolioReference = firebaseDatabase.getReference().child("portfolio")
                                                    .child(clicked_user_uid).child(subjects_list.get(position));
                                            portfolioReference.addValueEventListener(new ValueEventListener() {
                                                @Override
                                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                    Portfolio portfolio = Utilities.getPortfolioForSubject(dataSnapshot);
                                                    if (portfolio != null)
                                                        portfolio_list.add(portfolio);

                                                    if (position == subjects_list.size()-1)
                                                        fillPortfolioListView(portfolio_list);
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
    }

    private void fillSchoolarFeedbackListView(ArrayList<StudentFeedback> studentFeedbacks) {
        if (studentFeedbacks.size() == 0)
            noSchoolarFeedbacks.setVisibility(View.VISIBLE);
        else {
            if (noSchoolarFeedbacks.getVisibility() == View.VISIBLE)
                noSchoolarFeedbacks.setVisibility(View.GONE);
            adapter = new StudentFeedbacksAdapter(getContext(), studentFeedbacks);
            schoolarFeedbacksListView.setAdapter(adapter);
            Utilities.getTotalHeightofListView(schoolarFeedbacksListView);
        }
    }

    private void fillMedicalFeedbackListView(ArrayList<StudentFeedback> studentFeedbacks) {
        if (studentFeedbacks.size() == 0)
            noMedicalFeedbacks.setVisibility(View.VISIBLE);
        else {
            if (noMedicalFeedbacks.getVisibility() == View.VISIBLE)
                noMedicalFeedbacks.setVisibility(View.GONE);
            adapter = new StudentFeedbacksAdapter(getContext(), studentFeedbacks);
            medicalFeedbacksListView.setAdapter(adapter);
            Utilities.getTotalHeightofListView(medicalFeedbacksListView);
        }
    }

    private void fillActivityFeedbackListView(ArrayList<StudentFeedback> studentFeedbacks) {
        if (studentFeedbacks.size() == 0)
            noActivityFeedbacks.setVisibility(View.VISIBLE);
        else {
            if (noActivityFeedbacks.getVisibility() == View.VISIBLE)
                noActivityFeedbacks.setVisibility(View.GONE);
            adapter = new StudentFeedbacksAdapter(getContext(), studentFeedbacks);
            activityFeedbacksListView.setAdapter(adapter);
            Utilities.getTotalHeightofListView(activityFeedbacksListView);
        }
    }

    private void fillPortfolioListView(ArrayList<Portfolio> portfolio_list) {

        PortfolioAdapter adapter = new PortfolioAdapter(getContext(), portfolio_list);
        portfolioListView.setAdapter(adapter);
        Utilities.getTotalHeightofListView(portfolioListView);
    }

    private void fillPersonalData(String profileURL, String fullname, String username,String password) {
        if (profileURL != null && !TextUtils.isEmpty(profileURL))
            Glide.with(getContext()).load(profileURL).into(profile_img);
        if (fullname != null)
            this.fullname.setText(fullname);
        if (username != null)
            this.username.setText(username);

        if (password != null)
            this.password=password;
    }

    private ArrayList<StudentFeedback> reverseList(ArrayList<StudentFeedback> reversed_competitions_list) {
        ArrayList<StudentFeedback> reversed_list = new ArrayList<>();

        for (int i = reversed_competitions_list.size() - 1; i >= 0; i--) {
            reversed_list.add(reversed_competitions_list.get(i));
        }

        return reversed_list;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        // Unregister VisualizerActivity as an OnPreferenceChangedListener to avoid any memory leaks.
        PreferenceManager.getDefaultSharedPreferences(getContext());
    }
}