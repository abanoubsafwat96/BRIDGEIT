package com.safwat.abanoub.bridgeit;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
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
import java.util.HashMap;
import java.util.Map;

public class GalleryActivity extends AppCompatActivity implements Communicator.Gallery {

    private static final int RC_PHOTO_PICKER = 5;
    RecyclerView recyclerView;
    TextView noPhotos;
    ImageButton add;
    private String userType;
    String current_uid;

    FirebaseDatabase firebaseDatabase;
    DatabaseReference galleryPicturesReference, likersReference;
    FirebaseStorage firebaseStorage;
    StorageReference storageReference;
    private ProgressDialog progressDialog, progressDialog1;
    private ArrayList<GalleryPicture> pictures_list;
    private GalleryAdapter galleryAdapter;
    boolean isLikeBtnClicked = false, isFirstTimeLoading = true, isNewPictureAdded = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gallery);

        progressDialog1 = new ProgressDialog(this);
        progressDialog1.setTitle("Gallery");
        progressDialog1.setMessage("Please wait...");
        progressDialog1.show();

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(GalleryActivity.this);
        //reading from shared preference
        userType = sharedPreferences.getString("userType", "not found");

        recyclerView = findViewById(R.id.recycler);
        noPhotos = findViewById(R.id.noPhotos);
        add = findViewById(R.id.add);

        current_uid = Utilities.getCurrentUID();

        if (userType.equals("Student") || userType.equals("Parent"))
            add.setVisibility(View.GONE);
        else
            add.setVisibility(View.VISIBLE);

        firebaseDatabase = FirebaseDatabase.getInstance();
        galleryPicturesReference = firebaseDatabase.getReference().child("galleryPictures");

        galleryPicturesReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                ArrayList<GalleryPicture> reversed_pictures_list = Utilities.getAllGalleryPictures(dataSnapshot);
                pictures_list = reverseList(reversed_pictures_list);

                final ArrayList<Teacher> senders_list = new ArrayList<>();
                final ArrayList<ArrayList<String>> likers_uids_list = new ArrayList<>();

                for (int i = 0; i < pictures_list.size(); i++) {
                    final int position = i;
                    final GalleryPicture galleryPicture = pictures_list.get(position);

                    firebaseDatabase.getReference().child(galleryPicture.node).child(galleryPicture.uid)
                            .addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                    senders_list.add(Utilities.getTeacher(dataSnapshot));

//                                    if (position == pictures_list.size() - 1) {
//                                        fillRecyclerView(pictures_list, senders_list);
//
//                                        if (progressDialog1.isShowing())
//                                            progressDialog1.dismiss();
//                                    }

                                    //likes
//                                    firebaseDatabase.getReference().child("galleryPicturesLikers").child(galleryPicture.pushID)
                                    galleryPicturesReference.child(galleryPicture.pushID).child("likers")
                                            .addValueEventListener(new ValueEventListener() {
                                                @Override
                                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

//                                                    if (isFirstTimeLoading || isLikeBtnClicked || isNewPictureAdded) {
                                                    ArrayList<String> list = Utilities.getUIDs(dataSnapshot);
                                                    likers_uids_list.add(list);

                                                    if (position == pictures_list.size() - 1) {
                                                        fillRecyclerView(pictures_list, senders_list, likers_uids_list);

                                                        try {
                                                            Thread.sleep(1000);
                                                        } catch (InterruptedException e) {
                                                            e.printStackTrace();
                                                        }

//                                                            isFirstTimeLoading = false;
//                                                            isLikeBtnClicked = false;
//                                                            isNewPictureAdded = false;

                                                        if (progressDialog1.isShowing())
                                                            progressDialog1.dismiss();
                                                    }
//                                                    }
                                                }

                                                @Override
                                                public void onCancelled(@NonNull DatabaseError databaseError) {

                                                }
                                            });
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

        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("image/jpeg");
                intent.putExtra(Intent.EXTRA_LOCAL_ONLY, true);
                startActivityForResult(Intent.createChooser(intent, "Complete action using"), RC_PHOTO_PICKER);
            }
        });
    }

    private ArrayList<GalleryPicture> reverseList(ArrayList<GalleryPicture> reversed_competitions_list) {
        ArrayList<GalleryPicture> reversed_list = new ArrayList<>();

        for (int i = reversed_competitions_list.size() - 1; i >= 0; i--) {
            reversed_list.add(reversed_competitions_list.get(i));
        }

        return reversed_list;
    }

    private void fillRecyclerView(ArrayList<GalleryPicture> pictures_list, ArrayList<Teacher> senders_list) {
        if (pictures_list.size() == 0)
            noPhotos.setVisibility(View.VISIBLE);
        else {
            if (noPhotos.getVisibility() == View.VISIBLE)
                noPhotos.setVisibility(View.GONE);

            if (galleryAdapter == null) {
                RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
                recyclerView.setLayoutManager(layoutManager);

                galleryAdapter = new GalleryAdapter(this, pictures_list, senders_list);
                recyclerView.setAdapter(galleryAdapter);
                recyclerView.setHasFixedSize(true);

            } else
                galleryAdapter.notifyDataSetChanged();
        }
    }

    private void fillRecyclerView(ArrayList<GalleryPicture> pictures_list, ArrayList<Teacher> senders_list, ArrayList<ArrayList<String>> likers_uids_list) {
        if (pictures_list.size() == 0)
            noPhotos.setVisibility(View.VISIBLE);
        else {
            if (noPhotos.getVisibility() == View.VISIBLE)
                noPhotos.setVisibility(View.GONE);

            if (galleryAdapter == null || isNewPictureAdded) {
                RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
                recyclerView.setLayoutManager(layoutManager);

                galleryAdapter = new GalleryAdapter(this, this, pictures_list, senders_list, likers_uids_list);
                recyclerView.setAdapter(galleryAdapter);
                recyclerView.setHasFixedSize(true);

                isNewPictureAdded=false;
            } else
                galleryAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_PHOTO_PICKER && resultCode == RESULT_OK) {

            Uri selectedImageUri = data.getData();

            if (selectedImageUri != null) {
                firebaseStorage = FirebaseStorage.getInstance();
                storageReference = firebaseStorage.getReference().child("GalleryPictures").child(selectedImageUri.getPathSegments().toString());

                progressDialog = new ProgressDialog(this);
                progressDialog.setTitle("Uploading image");
                progressDialog.show();

                //Add file to reference
                UploadTask uploudTask = storageReference.putFile(selectedImageUri);
                uploudTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                    @Override
                    public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                        if (!task.isSuccessful()) {
                            throw task.getException();
                        }
                        // Continue with the task to get the download URL
                        return storageReference.getDownloadUrl();
                    }
                }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                    @Override
                    public void onComplete(@NonNull Task<Uri> task) {
                        if (task.isSuccessful()) {

                            newPictureAdded();

                            //Display success toast msg
                            Toast.makeText(GalleryActivity.this, "Photo uploaded successfully", Toast.LENGTH_SHORT).show();

                            GalleryPicture galleryPicture = null;

                            if (userType.equals("Teacher"))
                                galleryPicture = new GalleryPicture(current_uid
                                        , task.getResult().toString(), "teachers");

                            else if (userType.equals("Doctor"))
                                galleryPicture = new GalleryPicture(current_uid
                                        , task.getResult().toString(), "doctors");

                            else if (userType.equals("PE"))
                                galleryPicture = new GalleryPicture(current_uid
                                        , task.getResult().toString(), "PEs");

                            else if (userType.equals("Bus Admin"))
                                galleryPicture = new GalleryPicture(current_uid
                                        , task.getResult().toString(), "busAdmins");

                            else if (userType.equals("Manager"))
                                galleryPicture = new GalleryPicture(current_uid
                                        , task.getResult().toString(), "managers");

                            //Save image info in to firebase database
                            String pushID = galleryPicturesReference.push().getKey();
                            galleryPicturesReference.child(pushID).setValue(galleryPicture);
                            progressDialog.dismiss(); //Dimiss progressDialog when success
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                        //Dimiss progressDialog when error
                        progressDialog.dismiss();
                        //Display err toast msg
                        Toast.makeText(GalleryActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
                        Log.e("onFailure: ", e.getMessage());
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
                Toast.makeText(GalleryActivity.this, "Please select image", Toast.LENGTH_LONG).show();
            }
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        // Unregister VisualizerActivity as an OnPreferenceChangedListener to avoid any memory leaks.
        PreferenceManager.getDefaultSharedPreferences(this);
    }

    public void newPictureAdded() {
        this.isNewPictureAdded = true;
    }

    @Override
    public void likeBtnClicked() {
        this.isLikeBtnClicked = true;
    }

    @Override
    public void addLike(int position) {

        galleryAdapter.likers_uids_list.get(position).add(current_uid);

        Map<String, String> uid_map = new HashMap<>();
        uid_map.put("uid", current_uid);

//        likersReference = firebaseDatabase.getReference().child("galleryPicturesLikers").child(pictures_list.get(position).pushID);
        likersReference = galleryPicturesReference.child(pictures_list.get(position).pushID).child("likers");
        likersReference.child(current_uid).setValue(uid_map);
    }

    @Override
    public void clearLike(int position) {

        galleryAdapter.likers_uids_list.get(position).remove(current_uid);

//        likersReference = firebaseDatabase.getReference().child("galleryPicturesLikers").child(pictures_list.get(position).pushID);
        likersReference = galleryPicturesReference.child(pictures_list.get(position).pushID).child("likers");
        likersReference.child(current_uid).setValue(null);
    }
}
