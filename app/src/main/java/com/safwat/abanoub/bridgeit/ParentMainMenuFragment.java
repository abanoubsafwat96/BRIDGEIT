package com.safwat.abanoub.bridgeit;

import android.content.Intent;
import android.os.Bundle;
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
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class ParentMainMenuFragment extends Fragment implements Communicator.ChildrensRecyclerView {

    RecyclerView childerns_recycler;
    TextView bus_btn, announcements_btn;
    CircleImageView profile_img;

    ProfileFragment profileFragment;

    FirebaseDatabase firebaseDatabase;
    DatabaseReference personalDataReference, childrensReference;

    ArrayList<Child> children_list;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_parent_main_menu, container, false);

        bus_btn = view.findViewById(R.id.bus_btn);
        announcements_btn = view.findViewById(R.id.announcements_btn);
        profile_img = view.findViewById(R.id.image);
        childerns_recycler = view.findViewById(R.id.childrens_recycler);

        firebaseDatabase = FirebaseDatabase.getInstance();

        personalDataReference = firebaseDatabase.getReference().child("parents").child(Utilities.getCurrentUID());
        personalDataReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String profileURL = Utilities.getProfileURL(dataSnapshot);
                if (profileURL != null && !TextUtils.isEmpty(profileURL) && getContext() != null)
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
                bundle.putString("uid", Utilities.getCurrentUID());
                bundle.putString("fragmentUsedAs", "Profile");
                profileFragment.setArguments(bundle);

                getFragmentManager().beginTransaction().replace(R.id.frameLayout, profileFragment).commit();
            }
        });

        bus_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getActivity(), BusActivity.class));
            }
        });

        announcements_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getActivity(), AnnouncementsActivity.class));
            }
        });

        return view;
    }

    private void fillRecyclerView(ArrayList<Child> children_list) {
        if (children_list.size() != 0) {

            RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
            childerns_recycler.setLayoutManager(layoutManager);

            ChildrensAdapter adapter = new ChildrensAdapter(this, getContext(), children_list);
            childerns_recycler.setAdapter(adapter);
            childerns_recycler.setHasFixedSize(true);
        }
    }

    @Override
    public void onChildChoosed(int position) {
        Intent intent = new Intent(getContext(), ChildActivity.class);
        intent.putExtra("child", children_list.get(position));
        startActivity(intent);
    }
}
