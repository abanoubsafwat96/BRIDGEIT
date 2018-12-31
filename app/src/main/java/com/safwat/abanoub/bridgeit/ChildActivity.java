package com.safwat.abanoub.bridgeit;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class ChildActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_child);

        Child child=getIntent().getParcelableExtra("child");

        ProfileFragment profileFragment = new ProfileFragment();
        Bundle bundle = new Bundle();
        bundle.putString("uid",  child.uid);
        bundle.putString("fragmentUsedAs","ParentViewingChildData");
        profileFragment.setArguments(bundle);

        getSupportFragmentManager().beginTransaction().replace(R.id.frameLayout, profileFragment).commit();
    }
}
