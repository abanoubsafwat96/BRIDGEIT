package com.safwat.abanoub.bridgeit;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.ArrayList;

import static android.app.Activity.RESULT_OK;

/**
 * Created by Abanoub on 2018-04-26.
 */

public class ChangeProfilePicDialogFragment extends DialogFragment {

    ImageButton photoPicker, close;
    Button update;
    TextView chooseImageLink;
    Uri selectedImageUri;
    LinearLayout photoChosenLinear;
    private static final int RC_PHOTO_PICKER = 2;
    ProgressDialog progressDialog;

    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference personalDataReference;
    private FirebaseStorage firebaseStorage;
    private StorageReference mStorageRef;
    private String userType;

    /**
     * Create a new instance of MyDialogFragment, to pass "newUser" as an argument to this dialog.
     * <p>
     * 1. newInstance to make instance of your DialogFragment
     * 2. setter to initialize your object
     * 3. and add setRetainInstance(true); in onCreate
     * @param userType
     */
    static ChangeProfilePicDialogFragment newInstance(String userType) {

        ChangeProfilePicDialogFragment f = new ChangeProfilePicDialogFragment();

        // Supply input as an argument.
        Bundle args = new Bundle();
        f.setArguments(args);
        f.setUserType(userType);

        return f;
    }

    private void setUserType(String userType) {
        this.userType=userType;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        //Create your dialog here

        setRetainInstance(true);

        Dialog dialog = new Dialog(getContext(), R.style.DialogTheme);
        dialog.setContentView(R.layout.change_profile_pic_dialog);

        return dialog;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.change_profile_pic_dialog, container, false);
        // Do something else

        photoPicker = (ImageButton) view.findViewById(R.id.photoPicker);
        close = (ImageButton) view.findViewById(R.id.close);
        update = (Button) view.findViewById(R.id.update);
        chooseImageLink = (TextView) view.findViewById(R.id.chooseImageLink);
        photoChosenLinear = (LinearLayout) view.findViewById(R.id.photoChosenLinear);

        firebaseDatabase = FirebaseDatabase.getInstance();
        firebaseStorage = FirebaseStorage.getInstance();

        String current_uid=Utilities.getCurrentUID();

        if (userType.equals("Student")) {
            personalDataReference=firebaseDatabase.getReference().child("students").child(current_uid);

        } else if (userType.equals("Teacher")) {
            personalDataReference=firebaseDatabase.getReference().child("teachers").child(current_uid);

        } else if (userType.equals("Parent")) {
            personalDataReference=firebaseDatabase.getReference().child("parents").child(current_uid);

        } else if (userType.equals("Bus Admin")) {
            personalDataReference=firebaseDatabase.getReference().child("busAdmins").child(current_uid);

        } else if (userType.equals("Manager")) {
            personalDataReference=firebaseDatabase.getReference().child("managers").child(current_uid);

        } else if (userType.equals("Doctor")) {
            personalDataReference=firebaseDatabase.getReference().child("doctors").child(current_uid);

        } else if (userType.equals("PE")) {
            personalDataReference=firebaseDatabase.getReference().child("PEs").child(current_uid);

        }

        photoPicker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("image/jpeg");
                intent.putExtra(Intent.EXTRA_LOCAL_ONLY, true);
                startActivityForResult(Intent.createChooser(intent, "Complete action using"), RC_PHOTO_PICKER);
            }
        });

        update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (selectedImageUri != null) {
                    progressDialog = new ProgressDialog(getContext());
                    progressDialog.setTitle("Uploading image");
                    progressDialog.show();

                    //Add file to reference
                    UploadTask uploudTask = mStorageRef.putFile(selectedImageUri);
                    uploudTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                        @Override
                        public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                            if (!task.isSuccessful()) {
                                throw task.getException();
                            }
                            // Continue with the task to get the download URL
                            return mStorageRef.getDownloadUrl();
                        }
                    }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                        @Override
                        public void onComplete(@NonNull Task<Uri> task) {
                            if (task.isSuccessful()) {

                                //Display success toast msg
                                Toast.makeText(getContext(), "Profile picture changed successfully", Toast.LENGTH_SHORT).show();

                                String profilePicture = task.getResult().toString();

                                //Save image info in to firebase database
                                if (userType.equals("Student")) {
                                    personalDataReference.child("profileURL").setValue(profilePicture);

                                } else if (userType.equals("Teacher")) {
                                    personalDataReference.child("profileURL").setValue(profilePicture);

                                } else if (userType.equals("Parent")) {
                                    personalDataReference.child("profileURL").setValue(profilePicture);

                                } else if (userType.equals("Bus Admin")) {
                                    personalDataReference.child("profileURL").setValue(profilePicture);

                                } else if (userType.equals("Manager")) {
                                    personalDataReference.child("profileURL").setValue(profilePicture);

                                } else if (userType.equals("Doctor")) {
                                    personalDataReference.child("profileURL").setValue(profilePicture);

                                } else if (userType.equals("PE")) {
                                    personalDataReference.child("profileURL").setValue(profilePicture);
                                }

                                progressDialog.dismiss(); //Dimiss progressDialog when success
                                dismiss(); //Dismiss ChangeProfilePicDialog
                            }
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {

                            //Dimiss progressDialog when error
                            progressDialog.dismiss();
                            //Display err toast msg
                            Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
                    uploudTask.addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {

                        @Override
                        public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {

                            //Show upload progress
                            double progress = (100 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount();
                            progressDialog.setMessage("Uploaded " + (int) progress + "%");
                        }
                    });

                } else {
                    Toast.makeText(getContext(), "Please select image", Toast.LENGTH_SHORT).show();
                }
            }
        });

        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectedImageUri = null;
                chooseImageLink.setText("Choose an image");
                photoChosenLinear.setVisibility(View.GONE);
            }
        });
        return view;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_PHOTO_PICKER && resultCode == RESULT_OK) {

            mStorageRef = firebaseStorage.getReference().child("ProfilePictures").child(data.getData().getPathSegments().toString());
            chooseImageLink.setText(data.getData().getLastPathSegment());
            selectedImageUri = data.getData();
            photoChosenLinear.setVisibility(View.VISIBLE);
        }
    }
}
