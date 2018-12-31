package com.safwat.abanoub.bridgeit;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

public class Grades2Activity extends AppCompatActivity {

    private Grades2PageAdapter adapter;
    private ViewPager mViewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_grades2);

        Classroom classroom= getIntent().getParcelableExtra("classroom");
        String subject= getIntent().getExtras().getString("subject");

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.container);

        Bundle bundle = new Bundle();
        bundle.putParcelable("classroom", classroom);
        bundle.putString("subject", subject);
        // set Fragment Arguments
        GradesByTestFragment gradesByTestFragment = new GradesByTestFragment();
        gradesByTestFragment.setArguments(bundle);
        GradesByStudentFragment gradesByStudentFragment=new GradesByStudentFragment();
        gradesByStudentFragment.setArguments(bundle);

        adapter = new Grades2PageAdapter(getSupportFragmentManager());
        adapter.addFragment(gradesByTestFragment, "Grades by Tests");
        adapter.addFragment(gradesByStudentFragment, "Grades by Students");
        mViewPager.setAdapter(adapter);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(mViewPager);
    }
}
