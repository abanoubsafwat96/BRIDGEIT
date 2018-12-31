package com.safwat.abanoub.bridgeit;

import android.content.Intent;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class TeacherMainMenuFragment extends Fragment implements Communicator.ChildrensRecyclerView {

    //    int pageNum = 1;
    RecyclerView childerns_recycler;
    TextView busTracker_btn, competition_btn, announcement_btn, grades_btn, logout_btn, todoList_btn;
    CircleImageView profile_img;

    ProfileFragment profileFragment;

    //    TextView achievements_btn, portfolio_btn, previous_btn, next_btn;
    FirebaseAuth firebaseAuth;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference personalDataReference,childrensReference;

    ArrayList<Child> children_list;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_teacher_main_menu, container, false);

//        achievements_btn = view.findViewById(R.id.achievements_btn);
//        portfolio_btn = view.findViewById(R.id.portfolio_btn);
//        previous_btn = view.findViewById(R.id.previous_btn);
//        next_btn = view.findViewById(R.id.next_btn);
//        todoList_btn = view.findViewById(R.id.todo_btn);
//        logout_btn = view.findViewById(R.id.logout_btn);
        grades_btn = view.findViewById(R.id.grades_btn);
        competition_btn = view.findViewById(R.id.competition_btn);
        busTracker_btn = view.findViewById(R.id.bus_btn);
        announcement_btn = view.findViewById(R.id.announcements_btn);
        profile_img = view.findViewById(R.id.image);
        childerns_recycler = view.findViewById(R.id.childrens_recycler);

        firebaseDatabase = FirebaseDatabase.getInstance();
        personalDataReference = firebaseDatabase.getReference().child("teachers").child(Utilities.getCurrentUID());
        personalDataReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String profileURL = Utilities.getProfileURL(dataSnapshot);
                if (profileURL != null&& !TextUtils.isEmpty(profileURL)&&getContext()!=null)
                    Glide.with(getContext()).load(profileURL).into(profile_img);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        childrensReference = personalDataReference.child("sons");
        childrensReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                children_list = Utilities.getAllChildrens(dataSnapshot);

                for (int i = 0; i < children_list.size(); i++) {
                    final int position = i;
                    firebaseDatabase.getReference().child("students").child(children_list.get(position).uid)
                            .addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                    children_list.get(position).fullname = Utilities.getUserFullName(dataSnapshot);
                                    children_list.get(position).profileURL = Utilities.getProfileURL(dataSnapshot);

                                    if (position == children_list.size() - 1)
                                        fillRecyclerView(children_list);
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

        profile_img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                profileFragment = new ProfileFragment();
                Bundle bundle = new Bundle();
                bundle.putString("uid",  Utilities.getCurrentUID());
                bundle.putString("fragmentUsedAs","Profile");
                profileFragment.setArguments(bundle);

                getFragmentManager().beginTransaction().replace(R.id.frameLayout, profileFragment).commit();
            }
        });

        firebaseAuth = FirebaseAuth.getInstance();

        busTracker_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getActivity(), BusActivity.class));
            }
        });

//        todoList_btn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                startActivity(new Intent(getActivity(), CalendarActivity.class));
//            }
//        });

        competition_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getActivity(), ChooseSubjectActivity.class));
            }
        });

        announcement_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getActivity(), AnnouncementsActivity.class));
            }
        });

        grades_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getActivity(), Grades1Activity.class));
            }
        });

//        portfolio_btn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                startActivity(new Intent(getActivity(), PortfolioActivity.class));
//            }
//        });
//
//        achievements_btn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                startActivity(new Intent(getActivity(), AchievementsActivity.class));
//            }
//        });
//
//        previous_btn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                changeButtons();
//            }
//        });
//
//        next_btn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                changeButtons();
//            }
//        });

//        logout_btn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
//                //editing into shared preference
//                SharedPreferences.Editor editor = sharedPreferences.edit();
//                editor.putString("userType", "no users");
//                editor.apply();
//
//                firebaseAuth.signOut();
//                startActivity(new Intent(getActivity(), LogInActivity.class));
//            }
//        });
        return view;
    }

//    private void changeButtons() {
//        if (pageNum == 1) {
//            pageNum = 2;
//            busTracker_btn.setVisibility(View.GONE);
//            todoList_btn.setVisibility(View.GONE);
//            competition_btn.setVisibility(View.GONE);
//            announcement_btn.setVisibility(View.GONE);
//            gallery_btn.setVisibility(View.GONE);
//            achievements_btn.setVisibility(View.GONE);
//            portfolio_btn.setVisibility(View.VISIBLE);
//        }else if (pageNum==2){
//            pageNum = 1;
//            busTracker_btn.setVisibility(View.VISIBLE);
//            todoList_btn.setVisibility(View.VISIBLE);
//            competition_btn.setVisibility(View.VISIBLE);
//            announcement_btn.setVisibility(View.VISIBLE);
//            gallery_btn.setVisibility(View.VISIBLE);
//            achievements_btn.setVisibility(View.VISIBLE);
//            portfolio_btn.setVisibility(View.GONE);
//        }
//    }

    private void fillRecyclerView(ArrayList<Child> children_list) {
        if (children_list.size() != 0) {

            RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext(),LinearLayoutManager.HORIZONTAL,false);
            childerns_recycler.setLayoutManager(layoutManager);

            ChildrensAdapter adapter = new ChildrensAdapter(this ,getContext(), children_list);
            childerns_recycler.setAdapter(adapter);
            childerns_recycler.setHasFixedSize(true);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        // Unregister VisualizerActivity as an OnPreferenceChangedListener to avoid any memory leaks.
        PreferenceManager.getDefaultSharedPreferences(getContext());
    }

    @Override
    public void onChildChoosed(int position) {
        Intent intent=new Intent(getContext(),ChildActivity.class);
        intent.putExtra("child",children_list.get(position));
        startActivity(intent);
    }
}
