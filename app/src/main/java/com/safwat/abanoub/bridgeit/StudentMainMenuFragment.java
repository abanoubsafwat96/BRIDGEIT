package com.safwat.abanoub.bridgeit;

import android.content.Intent;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
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

import de.hdodenhof.circleimageview.CircleImageView;

public class StudentMainMenuFragment extends android.support.v4.app.Fragment {

    int pageNum = 1;
    TextView busTracker_btn, todoList_btn, competition_btn, announcement_btn, gallery_btn, portfolio_btn, previous_btn, next_btn, logout_btn;
    CircleImageView profile_img;
    FirebaseAuth firebaseAuth;

    ProfileFragment profileFragment;

    FirebaseDatabase firebaseDatabase;
    DatabaseReference personalDataReference;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_student_main_menu, container, false);

        gallery_btn = view.findViewById(R.id.gallery_btn);
//        portfolio_btn = view.findViewById(R.id.portfolio_btn);
        competition_btn = view.findViewById(R.id.competition_btn);
        busTracker_btn = view.findViewById(R.id.bus_btn);
//        todoList_btn = view.findViewById(R.id.todo_btn);
        announcement_btn = view.findViewById(R.id.announcements_btn);
//        previous_btn = view.findViewById(R.id.previous_btn);
//        next_btn = view.findViewById(R.id.next_btn);
//        logout_btn = view.findViewById(R.id.logout_btn);
        profile_img = view.findViewById(R.id.image);

        firebaseDatabase = FirebaseDatabase.getInstance();
        personalDataReference = firebaseDatabase.getReference().child("students").child(Utilities.getCurrentUID());
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

//        gallery_btn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                startActivity(new Intent(getActivity(), GalleryActivity.class));
//            }
//        });
//
//        portfolio_btn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                startActivity(new Intent(getActivity(), PortfolioActivity.class));
//            }
//        });

        gallery_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getActivity(), GalleryActivity.class));
            }
        });

//        previous_btn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//               changeButtons();
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

    private void changeButtons() {
        if (pageNum == 1) {
            pageNum = 2;
            busTracker_btn.setVisibility(View.GONE);
            todoList_btn.setVisibility(View.GONE);
            competition_btn.setVisibility(View.GONE);
            announcement_btn.setVisibility(View.GONE);
            gallery_btn.setVisibility(View.GONE);
            portfolio_btn.setVisibility(View.VISIBLE);
        } else if (pageNum == 2) {
            pageNum = 1;
            busTracker_btn.setVisibility(View.VISIBLE);
            todoList_btn.setVisibility(View.VISIBLE);
            competition_btn.setVisibility(View.VISIBLE);
            announcement_btn.setVisibility(View.VISIBLE);
            gallery_btn.setVisibility(View.VISIBLE);
            portfolio_btn.setVisibility(View.GONE);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        // Unregister VisualizerActivity as an OnPreferenceChangedListener to avoid any memory leaks.
        PreferenceManager.getDefaultSharedPreferences(getActivity());
    }
}
