package com.safwat.abanoub.bridgeit;

import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

/**
 * Created by Abanoub on 2018-04-27.
 */

public class ChangePasswordDialogFragment extends DialogFragment {

    String click_user_uid, userType,old_Password;
    EditText oldPassword, newPassword, confirmPassword;
    Button update_btn;

    FirebaseUser firebaseUser;
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference;

    /**
     * Create a new instance of MyDialogFragment, to pass "newUser" as an argument to this dialog.
     * <p>
     * 1. newInstance to make instance of your DialogFragment
     * 2. setter to initialize your object
     * 3. and add setRetainInstance(true); in onCreate
     */
    static ChangePasswordDialogFragment newInstance(String clicked_user_uid, String userType, String old_Password) {

        ChangePasswordDialogFragment f = new ChangePasswordDialogFragment();

        f.setGlobalVariables(clicked_user_uid,userType,old_Password);

        return f;
    }

    public void setGlobalVariables(String clicked_user_uid, String userType,String old_Password) {
        this.click_user_uid = clicked_user_uid;
        this.userType=userType;
        this.old_Password=old_Password;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        //Create your dialog here

        setRetainInstance(true);

        Dialog dialog = new Dialog(getContext(), R.style.DialogTheme);
        dialog.setContentView(R.layout.change_password_dialog);

        return dialog;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.change_password_dialog, container, false);
        // Do something else

        oldPassword = (EditText) view.findViewById(R.id.oldPassword);
        newPassword = (EditText) view.findViewById(R.id.newPassword);
        confirmPassword = (EditText) view.findViewById(R.id.confirmPassword);
        update_btn = (Button) view.findViewById(R.id.update_btn);

        firebaseUser = Utilities.getCurrentUser();
        firebaseDatabase = FirebaseDatabase.getInstance();

        if (userType.equals("Student"))
            databaseReference = firebaseDatabase.getReference().child("students").child(click_user_uid);
        else if (userType.equals("Teacher"))
            databaseReference = firebaseDatabase.getReference().child("teachers").child(click_user_uid);
        else if (userType.equals("Parent"))
            databaseReference = firebaseDatabase.getReference().child("parents").child(click_user_uid);
        else if (userType.equals("Doctor"))
            databaseReference = firebaseDatabase.getReference().child("doctors").child(click_user_uid);
        else if (userType.equals("PE"))
            databaseReference = firebaseDatabase.getReference().child("PEs").child(click_user_uid);
        else if (userType.equals("Bus Admin"))
            databaseReference = firebaseDatabase.getReference().child("bus").child(click_user_uid);
        else if (userType.equals("Manager"))
            databaseReference = firebaseDatabase.getReference().child("managers").child(click_user_uid);

        oldPassword.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (charSequence.toString().trim().length() > 0 && TextUtils.isEmpty(newPassword.getText()) == false
                        && TextUtils.isEmpty(confirmPassword.getText()) == false)
                    update_btn.setEnabled(true);
                else
                    update_btn.setEnabled(false);
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });

        newPassword.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (charSequence.toString().trim().length() > 0 && TextUtils.isEmpty(oldPassword.getText()) == false
                        && TextUtils.isEmpty(confirmPassword.getText()) == false)
                    update_btn.setEnabled(true);
                else
                    update_btn.setEnabled(false);
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });

        confirmPassword.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (charSequence.toString().trim().length() > 0 && TextUtils.isEmpty(newPassword.getText()) == false
                        && TextUtils.isEmpty(oldPassword.getText()) == false)
                    update_btn.setEnabled(true);
                else
                    update_btn.setEnabled(false);
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });


        update_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Utilities.isNetworkAvailable(getContext())) {
                    if (TextUtils.isEmpty(oldPassword.getText()) || TextUtils.isEmpty(newPassword.getText())
                            || TextUtils.isEmpty(confirmPassword.getText()))
                        Toast.makeText(getContext(), R.string.fields_cannot_be_empty, Toast.LENGTH_SHORT).show();
                    else {
                        if (old_Password != null) {
                            if (oldPassword.getText().toString().equals(old_Password)) {
                                if (newPassword.getText().toString().equals(confirmPassword.getText().toString())) {
                                    firebaseUser.updatePassword(newPassword.getText().toString())
                                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    if (task.isSuccessful()) {
                                                        // updating password success
                                                        databaseReference.child("password").setValue(newPassword.getText().toString());
                                                        Toast.makeText(getContext(), "Password updated successfully", Toast.LENGTH_SHORT).show();
                                                        dismiss();
                                                    } else {
                                                        // If updating fails, display a message to the user.
                                                        Toast.makeText(getContext(), "Password updating failed", Toast.LENGTH_SHORT).show();
                                                    }
                                                }
                                            });

                                } else
                                    Toast.makeText(getContext(), R.string.passwords_donot_match, Toast.LENGTH_SHORT).show();
                            } else
                                Toast.makeText(getContext(), "Old password is wrong", Toast.LENGTH_SHORT).show();
                        }
                    }
                } else
                    Toast.makeText(getContext(), R.string.check_internet_connection, Toast.LENGTH_SHORT).show();
            }
        });

        return view;
    }
}
