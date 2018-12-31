package com.safwat.abanoub.bridgeit;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
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
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
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

public class ReplysActivity extends AppCompatActivity {

    ListView listView;
    TextView noReplies;
    ImageButton photoPicker, close, send;
    EditText editText;
    LinearLayout photoChosenLinear;
    TextView sender,msg;
    ImageView imageView;
    Msg clicked_msg;
    String roomNo;

    FirebaseDatabase firebaseDatabase;
    DatabaseReference repliesReference;
    FirebaseStorage firebaseStorage;
    StorageReference storageReference;

    Uri selectedImageUri;
    private static final int RC_PHOTO_PICKER = 2;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_replys);

        roomNo = getIntent().getExtras().getString("roomNo");
        clicked_msg = getIntent().getParcelableExtra("clicked_msg");

        TextView sender=(TextView) findViewById(R.id.sender);
        TextView message=(TextView) findViewById(R.id.msg);
        ImageView imageView=(ImageView) findViewById(R.id.image);
        photoPicker = (ImageButton) findViewById(R.id.photoPickerButton);
        listView = (ListView) findViewById(R.id.list);
        noReplies= findViewById(R.id.noReplies);
        editText = (EditText) findViewById(R.id.editText);
        send = (ImageButton) findViewById(R.id.send_btn);
        close = (ImageButton) findViewById(R.id.close);
        photoChosenLinear = (LinearLayout) findViewById(R.id.photoChosenLinear);

        sender.setText(clicked_msg.sender);
        if (message.getText().toString().isEmpty())
            message.setVisibility(View.GONE);
        else
            message.setText(clicked_msg.message);
        if (clicked_msg.photoUrl!=null){
            imageView.setVisibility(View.VISIBLE);
            Glide.with(this)
                        .load(clicked_msg.photoUrl)
                        .into(imageView);
            }

        firebaseStorage = FirebaseStorage.getInstance();

        firebaseDatabase = FirebaseDatabase.getInstance();
        repliesReference = firebaseDatabase.getReference().child("messages").child(roomNo).child(clicked_msg.uid).child("replies");

        repliesReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                ArrayList<Msg> msgs_list = Utilities.getAllMsgs(dataSnapshot);
                fillListView(msgs_list);
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

                    if (!hasPermissions(ReplysActivity.this, PERMISSIONS)) {
                        ActivityCompat.requestPermissions(ReplysActivity.this, PERMISSIONS, PERMISSION_ALL);
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

                        Toast.makeText(ReplysActivity.this, "Uploading finished", Toast.LENGTH_SHORT).show();
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
                    Toast.makeText(ReplysActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
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
        String pushId = repliesReference.push().getKey();
        repliesReference.child(pushId).setValue(msg);
    }

    private void fillListView(ArrayList<Msg> msgs_list) {
        if (msgs_list.size() == 0)
            noReplies.setVisibility(View.VISIBLE);
        else {
            if (noReplies.getVisibility() == View.VISIBLE)
                noReplies.setVisibility(View.GONE);

            MessagesAdapter adapter = new MessagesAdapter(this, msgs_list);
            listView.setAdapter(adapter);
            Utilities.getTotalHeightofListView2(listView);
        }
    }
    private void unSelectPhoto() {
        selectedImageUri = null;
        photoChosenLinear.setVisibility(View.GONE);
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
