package com.safwat.abanoub.bridgeit;

import android.content.Context;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

public class TeacherCompetitionActivity extends AppCompatActivity {

    private Grades2PageAdapter adapter;
    private ViewPager mViewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_teacher_competition);

        Classroom classroom=getIntent().getParcelableExtra("classroom");
        String subject=getIntent().getStringExtra("subject");

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.container);

        Bundle bundle = new Bundle();
        bundle.putParcelable("classroom", classroom);
        bundle.putString("subject", subject);
        //set Fragment Arguments

        AddCompetitionFragment addCompetitionFragment=new AddCompetitionFragment();
        addCompetitionFragment.setArguments(bundle);
        OldCompetitionsFragment oldCompetitionsFragment=new OldCompetitionsFragment();
        oldCompetitionsFragment.setArguments(bundle);
        QuickCompetitionFragment quickCompetitionFragment=new QuickCompetitionFragment();
        quickCompetitionFragment.setArguments(bundle);


        adapter = new Grades2PageAdapter(getSupportFragmentManager());
        adapter.addFragment(addCompetitionFragment, "Add Competition");
        adapter.addFragment(oldCompetitionsFragment, "Old Competitions");
        adapter.addFragment(quickCompetitionFragment, "Quick Competition");
        mViewPager.setAdapter(adapter);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(mViewPager);
   }
}
