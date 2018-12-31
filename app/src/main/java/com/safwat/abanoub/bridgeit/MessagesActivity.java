package com.safwat.abanoub.bridgeit;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
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

public class MessagesActivity extends AppCompatActivity {

    ListView listView;
    TextView noMessages;
    ImageButton add,photoPicker, close, send;
    EditText editText;
    LinearLayout sendLinear,photoChosenLinear;

    FirebaseDatabase firebaseDatabase;
    DatabaseReference roomNoReference, chatDatabaseReference;
    FirebaseStorage firebaseStorage;
    StorageReference storageReference;
    ArrayList<Msg> msgs_list;
    ArrayList<ArrayList<Msg>> replies_list;

    String userType;
    boolean roomNumFound;
    String uid1, uid2, roomNo;
    Uri selectedImageUri;

    private static final int RC_PHOTO_PICKER = 2;
    private String clickedUserType;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_messages);

        photoPicker = (ImageButton) findViewById(R.id.photoPickerButton);
        listView = (ListView) findViewById(R.id.list);
        noMessages=findViewById(R.id.noMessages);
        editText = (EditText) findViewById(R.id.editText);
        add = (ImageButton) findViewById(R.id.add);
        send = (ImageButton) findViewById(R.id.send);
        close = (ImageButton) findViewById(R.id.close);
        sendLinear = (LinearLayout) findViewById(R.id.sendLinear);
        photoChosenLinear = (LinearLayout) findViewById(R.id.photoChosenLinear);

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        //reading from shared preference
        userType = sharedPreferences.getString("userType", "not found");

        uid1 = Utilities.getCurrentUID();
        uid2 = getIntent().getExtras().getString("clickedUserUID");
        clickedUserType = getIntent().getExtras().getString("clickedUserType");

        firebaseStorage = FirebaseStorage.getInstance();

        firebaseDatabase = FirebaseDatabase.getInstance();
        roomNoReference = firebaseDatabase.getReference().child(clickedUserType).child(uid2).child("messages").child(uid1);

        roomNoReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                if (dataSnapshot.getValue() != null) {
                    roomNumFound = true;
                    roomNo = dataSnapshot.getValue(Room.class).roomNo;
                    chatDatabaseReference = firebaseDatabase.getReference().child("messages").child(roomNo);
                    chatDatabaseReference.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {

                            ArrayList<Msg> reversed_msgs_list=Utilities.getAllMsgs(dataSnapshot);
                            msgs_list = reverseList(reversed_msgs_list);

                            replies_list = new ArrayList<>();

                            if (msgs_list.size()==0)
                                fillListView(msgs_list, replies_list);
                            else {
                                    for (int i = 0; i < msgs_list.size(); i++) {
                                        final int position=i;

                                        chatDatabaseReference.child(msgs_list.get(position).uid).child("replies")
                                                .addValueEventListener(new ValueEventListener() {
                                                    @Override
                                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                        ArrayList<Msg> list = Utilities.getAllMsgs(dataSnapshot);

                                                        replies_list.add(list);

                                                        if (position==msgs_list.size()-1)
                                                            fillListView(msgs_list,replies_list);
                                                    }

                                                    @Override
                                                    public void onCancelled(@NonNull DatabaseError databaseError) {

                                                    }
                                                });
                                    }
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
                } else
                    roomNumFound = false;
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                unSelectPhoto();
            }
        });

        photoPicker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
                    // Define Needed Permissions for android Marshmallow and higher
                    // The request code used in ActivityCompat.requestPermissions()
                    // and returned in the Activity's onRequestPermissionsResult()
                    int PERMISSION_ALL = 1;
                    String[] PERMISSIONS = {
                            Manifest.permission.READ_EXTERNAL_STORAGE,
                    };

                    if (!hasPermissions(MessagesActivity.this, PERMISSIONS)) {
                        ActivityCompat.requestPermissions(MessagesActivity.this, PERMISSIONS, PERMISSION_ALL);
                    }
                }

                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("image/jpeg");
                intent.putExtra(Intent.EXTRA_LOCAL_ONLY, true);
                startActivityForResult(Intent.createChooser(intent, "Complete action using"), RC_PHOTO_PICKER);
            }
        });

        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String editTextMsg = editText.getText().toString();

                if (TextUtils.isEmpty(editTextMsg) && selectedImageUri == null) {
                } else {
                    uploudImage();
                }
                editText.setText("");
            }
        });

        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                add.setVisibility(View.GONE);
                sendLinear.setVisibility(View.VISIBLE);
            }
        });

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent intent=new Intent(MessagesActivity.this,ReplysActivity.class);
                intent.putExtra("roomNo",roomNo);
                intent.putExtra("clicked_msg",msgs_list.get(i));
                startActivity(intent);
            }
        });
    }

    private void uploudImage() {

        if (selectedImageUri != null) {
            storageReference = firebaseStorage.getReference().child("MessagesPictures")
                    .child(selectedImageUri.getLastPathSegment().toString());

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

                        String downloadUrl = task.getResult().toString();

                        Msg msg = new Msg(Utilities.getCurrentUsername(), editText.getText().toString(), downloadUrl);
                        uploadMsg(msg);

                        Toast.makeText(MessagesActivity.this, "Uploading finished", Toast.LENGTH_SHORT).show();
                        unSelectPhoto();
                        progressDialog.dismiss(); //Dimiss progressDialog when success
                    }
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {

                    //Dimiss progressDialog when error
                    progressDialog.dismiss();
                    //Display err toast msg
                    Toast.makeText(MessagesActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
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
            Msg msg = new Msg(Utilities.getCurrentUsername(), editText.getText().toString(), null);
            uploadMsg(msg);
        }
    }

    private void uploadMsg(Msg msg) {

        if (roomNumFound) {
            String pushId = chatDatabaseReference.push().getKey();
            chatDatabaseReference.child(pushId).setValue(msg);
        } else {
            String roomNo2 = uid2 + uid1;
            Room room = new Room(roomNo2);
            roomNoReference.setValue(room);

            DatabaseReference roomNoReference2 = null;
            if (userType.equals("Student"))
                roomNoReference2 = firebaseDatabase.getReference().child("students").child(uid1).child("messages").child(uid2);
            else if (userType.equals("Teacher"))
                roomNoReference2 = firebaseDatabase.getReference().child("teachers").child(uid1).child("messages").child(uid2);
            else if (userType.equals("Parent"))
                roomNoReference2 = firebaseDatabase.getReference().child("parents").child(uid1).child("messages").child(uid2);
            else if (userType.equals("Doctor"))
                roomNoReference2 = firebaseDatabase.getReference().child("doctors").child(uid1).child("messages").child(uid2);
            else if (userType.equals("PE"))
                roomNoReference2 = firebaseDatabase.getReference().child("PEs").child(uid1).child("messages").child(uid2);
            else if (userType.equals("Bus Admin"))
                roomNoReference2 = firebaseDatabase.getReference().child("bus").child(uid1).child("messages").child(uid2);
            else if (userType.equals("Manager"))
                roomNoReference2 = firebaseDatabase.getReference().child("managers").child(uid1).child("messages").child(uid2);

            if (roomNoReference2 != null)
                roomNoReference2.setValue(room);

            chatDatabaseReference = firebaseDatabase.getReference().child("messages").child(roomNo2);
            String pushId = chatDatabaseReference.push().getKey();
            chatDatabaseReference.child(pushId).setValue(msg);
        }
    }

    private void fillListView(ArrayList<Msg> msgs_list, ArrayList<ArrayList<Msg>> replies_list) {
        if (msgs_list.size() == 0)
            noMessages.setVisibility(View.VISIBLE);
        else {
            if (noMessages.getVisibility() == View.VISIBLE)
                noMessages.setVisibility(View.GONE);

            MessagesAdapter adapter = new MessagesAdapter(this, msgs_list, replies_list);
            listView.setAdapter(adapter);
        }
    }

    private void unSelectPhoto() {
        selectedImageUri = null;
        photoChosenLinear.setVisibility(View.GONE);
    }

    private ArrayList<Msg> reverseList(ArrayList<Msg> reversed_competitions_list) {
        ArrayList<Msg> reversed_list = new ArrayList<>();

        for (int i = reversed_competitions_list.size() - 1; i >= 0; i--) {
            reversed_list.add(reversed_competitions_list.get(i));
        }

        return reversed_list;
    }

    public static boolean hasPermissions(Context context, String... permissions) {
        if (context != null && permissions != null) {
            for (String permission : permissions) {
                if (ActivityCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                    return false;
                }
            }
        }
        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_PHOTO_PICKER && resultCode == RESULT_OK) {

            selectedImageUri = data.getData();
            photoChosenLinear.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        // Unregister VisualizerActivity as an OnPreferenceChangedListener to avoid any memory leaks.
        PreferenceManager.getDefaultSharedPreferences(this);
    }
}
