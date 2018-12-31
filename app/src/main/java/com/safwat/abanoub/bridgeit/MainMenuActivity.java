package com.safwat.abanoub.bridgeit;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class MainMenuActivity extends AppCompatActivity {

    ProfileFragment profileFragment;
    private DrawerLayout myDrawerlayout;
    private ActionBarDrawerToggle myToggle;
    NavigationView navigationView;
    TextView nav_fullName, nav_email;
    CircleImageView nav_profile_image;
    private String userType;

    TextView logout_btn;
    private FirebaseAuth firebaseAuth;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference yearsDatabaseReference, classesDatabaseReference2,personalDataReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(MainMenuActivity.this);
        //reading from shared preference
        userType = sharedPreferences.getString("userType", "not found");

        Toast.makeText(MainMenuActivity.this, "Hello " + userType, Toast.LENGTH_SHORT).show();
        firebaseDatabase = FirebaseDatabase.getInstance();
        loadUserLayout();

        Toolbar myToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(myToolbar);

        ActionBar actionbar = getSupportActionBar();
        if (actionbar != null) {
            actionbar.setDisplayOptions(actionbar.DISPLAY_SHOW_CUSTOM);
            actionbar.setTitle("");
        }

        myDrawerlayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        myToggle = new ActionBarDrawerToggle(MainMenuActivity.this, myDrawerlayout, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        myDrawerlayout.addDrawerListener(myToggle);
        myToggle.syncState();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        navigationView = (NavigationView) findViewById(R.id.navDrawer);
        View nav_header = navigationView.getHeaderView(0);
        nav_fullName = (TextView) nav_header.findViewById(R.id.username);
        nav_email = (TextView) nav_header.findViewById(R.id.email);
        nav_profile_image = (CircleImageView) nav_header.findViewById(R.id.profile_image);

        personalDataReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (userType.equals("Student")) {
                   Student student=Utilities.getStudent(dataSnapshot);
                   fillNavHeaderData(student.profileURL,student.fullname,student.username);

                } else if (userType.equals("Teacher")) {
                    Teacher teacher=Utilities.getTeacher(dataSnapshot);
                    fillNavHeaderData(teacher.profileURL,teacher.fullname,teacher.username);

                } else if (userType.equals("Parent")) {
                    Parent parent=Utilities.getParent(dataSnapshot);
                    fillNavHeaderData(parent.profileURL,parent.fullname,parent.username);

                } else if (userType.equals("Bus Admin")) {
                    Teacher teacher=Utilities.getTeacher(dataSnapshot);
                    fillNavHeaderData(teacher.profileURL,teacher.fullname,teacher.username);

                } else if (userType.equals("Manager")) {
                    Teacher teacher=Utilities.getTeacher(dataSnapshot);
                    fillNavHeaderData(teacher.profileURL,teacher.fullname,teacher.username);

                } else if (userType.equals("Doctor")) {
                    Teacher teacher=Utilities.getTeacher(dataSnapshot);
                    fillNavHeaderData(teacher.profileURL,teacher.fullname,teacher.username);

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                String title = item.getTitle().toString();
                myDrawerlayout.closeDrawers();

                switch (title) {
                    case "Calendar":
                        startActivity(new Intent(MainMenuActivity.this, CalendarActivity.class));
                        break;
                    case "Gallery":
                        startActivity(new Intent(MainMenuActivity.this, GalleryActivity.class));
                        break;
                    case "Messages":
                        Intent intent = new Intent(MainMenuActivity.this, ChooseUserActivity.class);
                        intent.putExtra("activityType", "Messages");
                        startActivity(intent);
                        break;
                    case "Student Feedbacks":
                        Intent i=new Intent(MainMenuActivity.this,ChooseUserActivity.class);
                        i.putExtra("activityType","Student Feedbacks");
                        startActivity(i);
                        break;
                    case "Complaints":
                        startActivity(new Intent(MainMenuActivity.this, ComplaintsActivity.class));
                        break;
                    case "Doctor Reports":
                        startActivity(new Intent(MainMenuActivity.this, DoctorReportsActivity.class));
                        break;
                    case "Profile":
                        profileFragment = new ProfileFragment();
                        Bundle bundle = new Bundle();
                        bundle.putString("uid",  Utilities.getCurrentUID());
                        bundle.putString("fragmentUsedAs","Profile");
                        profileFragment.setArguments(bundle);

                        getSupportFragmentManager().beginTransaction().replace(R.id.frameLayout, profileFragment).commit();
                        break;
                    case "Grades":
                        startActivity(new Intent(MainMenuActivity.this, Grades1Activity.class));
                        break;
                    case "Competition":
                        Intent i2=new Intent(MainMenuActivity.this, ChooseSubjectActivity.class);
                        startActivity(i2);
                        break;
                    case "Log out":
                        logout();
                        break;
                    default:
                        break;
                }
                return true;
            }
        });

        logout_btn = findViewById(R.id.logout_btn);
        logout_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                logout();
            }
        });

        yearsDatabaseReference = firebaseDatabase.getReference().child("Years");
        final DataBaseAdapter helper = new DataBaseAdapter(MainMenuActivity.this);

        helper.deleteOldRecordsFromYearsTable();

        yearsDatabaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                ArrayList<String> list = Utilities.getAllYears(dataSnapshot);
                for (int i = 0; i < list.size(); i++)
                    helper.insertYear(list.get(i));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        classesDatabaseReference2 = firebaseDatabase.getReference().child("Classes");

        helper.deleteOldRecordsFromClassesTable();

        classesDatabaseReference2.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                ArrayList<SchoolClass> list = Utilities.getAllClasses(dataSnapshot);
                for (int i = 0; i < list.size(); i++)
                    helper.insertClass(list.get(i));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void loadUserLayout() {
        if (userType.equals("Student")) {
            setContentView(R.layout.activity_student_main_menu);
            getSupportFragmentManager().beginTransaction().replace(R.id.frame_layout_home
                    , new StudentMainMenuFragment(), "mainFragment").commit();
            personalDataReference=firebaseDatabase.getReference().child("students").child(Utilities.getCurrentUID());

        } else if (userType.equals("Teacher")) {
            setContentView(R.layout.activity_teacher_main_menu);
            getSupportFragmentManager().beginTransaction().replace(R.id.frame_layout_home
                    , new TeacherMainMenuFragment(), "mainFragment").commit();
            personalDataReference=firebaseDatabase.getReference().child("teachers").child(Utilities.getCurrentUID());

        } else if (userType.equals("Parent")) {
            setContentView(R.layout.activity_parent_main_menu);
            getSupportFragmentManager().beginTransaction().replace(R.id.frame_layout_home
                    , new ParentMainMenuFragment(), "mainFragment").commit();
            personalDataReference=firebaseDatabase.getReference().child("parents").child(Utilities.getCurrentUID());

        } else if (userType.equals("Bus Admin")) {
            setContentView(R.layout.activity_bus_admin_main_menu);
            getSupportFragmentManager().beginTransaction().replace(R.id.frame_layout_home
                    , new BusAdminMainMenuFragment(), "mainFragment").commit();
            personalDataReference=firebaseDatabase.getReference().child("busAdmins").child(Utilities.getCurrentUID());

        } else if (userType.equals("Manager")) {
            setContentView(R.layout.activity_manager_main_menu);
            getSupportFragmentManager().beginTransaction().replace(R.id.frame_layout_home
                    , new ManagerMainMenuFragment(), "mainFragment").commit();
            personalDataReference=firebaseDatabase.getReference().child("managers").child(Utilities.getCurrentUID());

        } else if (userType.equals("Doctor")) {
            setContentView(R.layout.activity_doctor_main_menu);
            getSupportFragmentManager().beginTransaction().replace(R.id.frame_layout_home
                    , new DoctorMainMenuFragment(), "mainFragment").commit();
            personalDataReference=firebaseDatabase.getReference().child("doctors").child(Utilities.getCurrentUID());

        }  else if (userType.equals("PE")) {
            setContentView(R.layout.activity_teacher_main_menu);
            getSupportFragmentManager().beginTransaction().replace(R.id.frame_layout_home
                    , new TeacherMainMenuFragment(), "mainFragment").commit();
            personalDataReference=firebaseDatabase.getReference().child("PEs").child(Utilities.getCurrentUID());
        }
    }

    private void fillNavHeaderData(String profileURL, String fullname, String username) {
        if (profileURL!=null && !TextUtils.isEmpty(profileURL))
            Glide.with(MainMenuActivity.this).load(profileURL).into(nav_profile_image);
        if (fullname!=null)
            nav_fullName.setText(fullname);
        if (username!=null)
            nav_email.setText(username);
    }

    private void logout() {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(MainMenuActivity.this);
        //editing into shared preference
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("userType", "no users");
//        editor.putString("parentChildUID", "not found");
        editor.apply();

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseAuth.signOut();
        startActivity(new Intent(MainMenuActivity.this, LogInActivity.class));
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (myToggle.onOptionsItemSelected(item))
            return true;
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {

        if (profileFragment != null) {
            getSupportFragmentManager().beginTransaction().detach(profileFragment).commit();
            profileFragment = null;

        } else if (userType.equals("Student")) {
            ProfileFragment profileFragment=((StudentMainMenuFragment) getSupportFragmentManager()
                    .findFragmentByTag("mainFragment")).profileFragment;
            if (profileFragment != null) {
                getSupportFragmentManager().beginTransaction().detach(profileFragment).commit();
                ((StudentMainMenuFragment) getSupportFragmentManager().findFragmentByTag("mainFragment")).profileFragment = null;
            } else
                exitApp();

        } else if (userType.equals("Teacher")) {
            ProfileFragment profileFragment = ((TeacherMainMenuFragment) getSupportFragmentManager()
                    .findFragmentByTag("mainFragment")).profileFragment;
            if (profileFragment != null) {
                getSupportFragmentManager().beginTransaction().detach(profileFragment).commit();
                ((TeacherMainMenuFragment) getSupportFragmentManager().findFragmentByTag("mainFragment")).profileFragment = null;
            } else
                exitApp();

        } else if (userType.equals("Parent")) {
            ProfileFragment profileFragment = ((ParentMainMenuFragment) getSupportFragmentManager()
                    .findFragmentByTag("mainFragment")).profileFragment;
            if (profileFragment != null) {
                getSupportFragmentManager().beginTransaction().detach(profileFragment).commit();
                ((ParentMainMenuFragment) getSupportFragmentManager().findFragmentByTag("mainFragment")).profileFragment = null;
            } else
                exitApp();

        } else if (userType.equals("Bus Admin")) {
            ProfileFragment profileFragment = ((BusAdminMainMenuFragment) getSupportFragmentManager()
                    .findFragmentByTag("mainFragment")).profileFragment;
            if (profileFragment != null) {
                getSupportFragmentManager().beginTransaction().detach(profileFragment).commit();
                ((BusAdminMainMenuFragment) getSupportFragmentManager().findFragmentByTag("mainFragment")).profileFragment = null;
            } else
                exitApp();

        } else if (userType.equals("Manager")) {
            ProfileFragment profileFragment = ((ManagerMainMenuFragment) getSupportFragmentManager()
                    .findFragmentByTag("mainFragment")).profileFragment;
            if (profileFragment != null) {
                getSupportFragmentManager().beginTransaction().detach(profileFragment).commit();
                ((ManagerMainMenuFragment) getSupportFragmentManager().findFragmentByTag("mainFragment")).profileFragment = null;
            } else
                exitApp();

        } else if (userType.equals("Doctor")) {
            ProfileFragment profileFragment = ((DoctorMainMenuFragment) getSupportFragmentManager()
                    .findFragmentByTag("mainFragment")).profileFragment;
            if (profileFragment != null) {
                getSupportFragmentManager().beginTransaction().detach(profileFragment).commit();
                ((DoctorMainMenuFragment) getSupportFragmentManager().findFragmentByTag("mainFragment")).profileFragment = null;
            } else
                exitApp();
        }
    }

    private void exitApp() {
        moveTaskToBack(true); //exit app
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        // Unregister VisualizerActivity as an OnPreferenceChangedListener to avoid any memory leaks.
        PreferenceManager.getDefaultSharedPreferences(MainMenuActivity.this);
    }
}
