package com.safwat.abanoub.bridgeit;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class LogInActivity extends AppCompatActivity {

    LinearLayout main_linear;
    EditText username, password;
    Button login_btn;
    Spinner spinner;
    FirebaseAuth firebaseAuth;
    private boolean isFound;
    DatabaseReference databaseReference = null;
    private String parentChildUID;
    SharedPreferences.Editor editor;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log_in);

        main_linear = findViewById(R.id.main_linear);
        username = findViewById(R.id.username);
        password = findViewById(R.id.password);
        login_btn = findViewById(R.id.login_btn);
        spinner = (Spinner) findViewById(R.id.spinner);

        firebaseAuth = FirebaseAuth.getInstance();

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(
                this, android.R.layout.simple_spinner_item, getResources().getStringArray(R.array.users));
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);

        username.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (charSequence.toString().trim().length() > 0 && TextUtils.isEmpty(password.getText()) == false)
                    login_btn.setEnabled(true);
                else
                    login_btn.setEnabled(false);
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });

        password.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (charSequence.toString().trim().length() > 0 && TextUtils.isEmpty(username.getText()) == false)
                    login_btn.setEnabled(true);
                else
                    login_btn.setEnabled(false);
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });

        login_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (Utilities.isNetworkAvailable(LogInActivity.this)) {
                    if (TextUtils.isEmpty(username.getText()) || TextUtils.isEmpty(password.getText()))
                        Toast.makeText(LogInActivity.this, R.string.wrong_email_password, Toast.LENGTH_SHORT).show();
                    else {
                        firebaseAuth.signInWithEmailAndPassword(username.getText().toString() + "@bridgeit.com", password.getText().toString())
                                .addOnCompleteListener(LogInActivity.this, new OnCompleteListener<AuthResult>() {
                                    @Override
                                    public void onComplete(@NonNull Task<AuthResult> task) {
                                        if (task.isSuccessful()) {

                                            final String currentUID = firebaseAuth.getUid();
                                            FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();

                                            String userType = spinner.getSelectedItem().toString();
                                            if (userType.equals("Student"))
                                                databaseReference = firebaseDatabase.getReference().child("students");
                                            else if (userType.equals("Teacher"))
                                                databaseReference = firebaseDatabase.getReference().child("teachers");
                                            else if (userType.equals("Parent"))
                                                databaseReference = firebaseDatabase.getReference().child("parents");
                                            else if (userType.equals("Doctor"))
                                                databaseReference = firebaseDatabase.getReference().child("doctors");
                                            else if (userType.equals("PE"))
                                                databaseReference = firebaseDatabase.getReference().child("PEs");
                                            else if (userType.equals("Bus Admin"))
                                                databaseReference = firebaseDatabase.getReference().child("busAdmins");
                                            else if (userType.equals("Manager"))
                                                databaseReference = firebaseDatabase.getReference().child("managers");

                                            databaseReference.addValueEventListener(new ValueEventListener() {
                                                @Override
                                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                    isFound = Utilities.checkIfUserExist(dataSnapshot, currentUID);
                                                    if (isFound)
                                                        setupSharedPreferences();
                                                    else {
                                                        firebaseAuth.signOut();
                                                        Toast.makeText(LogInActivity.this, "Check your spinner", Toast.LENGTH_SHORT).show();
                                                    }
                                                }

                                                @Override
                                                public void onCancelled(@NonNull DatabaseError databaseError) {

                                                }
                                            });

                                        } else {
                                            // If sign in fails, display a message to the user.
                                            Toast.makeText(LogInActivity.this, R.string.authentication_failed, Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });
                    }
                } else
                    Toast.makeText(LogInActivity.this, R.string.check_internet_connection, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setupSharedPreferences() {

        String userType = spinner.getSelectedItem().toString();

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        //editing into shared preference
        editor = sharedPreferences.edit();
        editor.putString("userType", userType);

//        if (userType.equals("Student"))
        openMainActivity();
//        else { // parent
//            databaseReference.child(Utilities.getCurrentUID()).child("sons").addValueEventListener(new ValueEventListener() {
//                @Override
//                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                    if (dataSnapshot.getValue() != null) {
//                        parentChildUID = (String) dataSnapshot.getValue().toString();
//                        editor.putString("parentChildUID", parentChildUID);
//                    } else
//                        editor.putString("parentChildUID", "not found");
//
//                    openMainActivity();
//                }
//
//                @Override
//                public void onCancelled(@NonNull DatabaseError databaseError) {
//
//                }
//            });
//        }
    }

    private void openMainActivity() {

        editor.apply();
        Intent intent = new Intent(LogInActivity.this, MainMenuActivity.class);
        startActivity(intent);
    }

    @Override
    protected void onStart() {
        super.onStart();

        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            startActivity(new Intent(LogInActivity.this, MainMenuActivity.class));
            LogInActivity.this.finish();
        }
    }

    @Override
    public void onBackPressed() {
        moveTaskToBack(true); //exit app
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        // Unregister VisualizerActivity as an OnPreferenceChangedListener to avoid any memory leaks.
        PreferenceManager.getDefaultSharedPreferences(this);
    }
}
