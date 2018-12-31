package com.safwat.abanoub.bridgeit;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class AddAnnouncementsActivity extends AppCompatActivity {

    Spinner typeSpinner, yearSpinner, classSpinner;
    ListView listView;
    TextView noReceivers;
    LinearLayout typeLinear, yearLinear, classLinear;
    EditText title, announcement;
    Button select_btn, send_btn;
    String[] users;
    ArrayAdapter<String> list_adapter;
    private String typeSelected;
    ArrayList<String> receivers_list;

    private ArrayList<String> yearsList, classesList;
    private String userType;

    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_announcements);

        typeSpinner = findViewById(R.id.typeSpinner);
        yearSpinner = findViewById(R.id.yearSpinner);
        classSpinner = findViewById(R.id.classSpinner);
        typeLinear = findViewById(R.id.typeLinear);
        yearLinear = findViewById(R.id.yearLinear);
        classLinear = findViewById(R.id.classLinear);
        listView = findViewById(R.id.listView);
        noReceivers = findViewById(R.id.noReceivers);
        select_btn = (Button) findViewById(R.id.select_btn);
        title = findViewById(R.id.title);
        announcement = findViewById(R.id.announcement);
        send_btn = (Button) findViewById(R.id.send_btn);

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(AddAnnouncementsActivity.this);
        //reading from shared preference
        userType = sharedPreferences.getString("userType", "not found");

        firebaseDatabase = FirebaseDatabase.getInstance();

        receivers_list = new ArrayList<>();
        list_adapter = new ArrayAdapter<String>(AddAnnouncementsActivity.this,
                android.R.layout.simple_list_item_1, android.R.id.text1, receivers_list);
        listView.setAdapter(list_adapter);

        if (userType.equals("Teacher")) {
            users = new String[]{"Students", "Parents"};

        } else if (userType.equals("Bus Admin")) {
            users = new String[]{"Students", "Parents"};

        } else if (userType.equals("PE")) {
            users = new String[]{"Students", "Parents"};

        } else if (userType.equals("Manager")) {
            users = new String[]{"Students", "Teachers","Parents", "Doctors","PEs", "Bus Admins"};
        }
        if (users != null) {
            ArrayAdapter<String> adapter1 = new ArrayAdapter<>(AddAnnouncementsActivity.this, android.R.layout.simple_spinner_item
                    , users);
            adapter1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            typeSpinner.setAdapter(adapter1);
        }

        typeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                typeSelected = (String) adapterView.getAdapter().getItem(i);
                if (typeSelected.equals("Students")) {
                    yearLinear.setVisibility(View.VISIBLE);
                    classLinear.setVisibility(View.VISIBLE);

                } else {
                    yearLinear.setVisibility(View.GONE);
                    classLinear.setVisibility(View.GONE);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        final DataBaseAdapter helper = new DataBaseAdapter(AddAnnouncementsActivity.this);

        yearsList = helper.getAllYears();

        final ArrayAdapter<String> adapter1 = new ArrayAdapter<>(AddAnnouncementsActivity.this, android.R.layout.simple_spinner_item, yearsList);
        adapter1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        yearSpinner.setAdapter(adapter1);

        yearSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {

                String selectedYear = (String) parentView.getAdapter().getItem(position);
                classesList = helper.getAllClassesFromYear(selectedYear);

                ArrayAdapter<String> adapter2 = new ArrayAdapter<>(AddAnnouncementsActivity.this
                        , android.R.layout.simple_spinner_item, classesList);
                adapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                classSpinner.setAdapter(adapter2);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {

            }

        });

        select_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String receiver = null;

                if (typeSelected.equals("Students")) {
                    receiver = "Students:" + yearSpinner.getSelectedItem().toString()
                            + ":" + classSpinner.getSelectedItem().toString();
                }else
                    receiver = typeSelected;

                if (receiver!=null){

                    for (int i = 0; i < receivers_list.size(); i++) {
                        if (receivers_list.get(i).equals(receiver)) {
                            Toast.makeText(AddAnnouncementsActivity.this, "You selected it before", Toast.LENGTH_SHORT).show();
                            return;
                        }
                    }
                    receivers_list.add(receiver);

                }

                if (receivers_list.size() == 0)
                    noReceivers.setVisibility(View.VISIBLE);
                else {
                    if (noReceivers.getVisibility() == View.VISIBLE)
                        noReceivers.setVisibility(View.GONE);

                    refreshListView();
                }
            }
        });

        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, final int i, long l) {
                AlertDialog.Builder alertDialogBuilder2 = new AlertDialog.Builder(AddAnnouncementsActivity.this);
                alertDialogBuilder2.setMessage("Are you sure that you want to delete this receiver ?");
                alertDialogBuilder2.setPositiveButton("Yes",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface arg0, int arg1) {

                                receivers_list.remove(i);
                                refreshListView();
                            }
                        });
                alertDialogBuilder2.setNegativeButton("No",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                            }
                        });
                alertDialogBuilder2.create().show();

                return true;
            }
        });

        title.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (charSequence.toString().trim().length() > 0 && TextUtils.isEmpty(announcement.getText()) == false)
                    send_btn.setEnabled(true);
                else
                    send_btn.setEnabled(false);
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });

        announcement.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (charSequence.toString().trim().length() > 0 && TextUtils.isEmpty(title.getText()) == false)
                    send_btn.setEnabled(true);
                else
                    send_btn.setEnabled(false);
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });

        send_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Utilities.isNetworkAvailable(AddAnnouncementsActivity.this)) {
                    if (list_adapter.getCount() > 0) {
                        if (TextUtils.isEmpty(title.getText()) || TextUtils.isEmpty(announcement.getText()))
                            Toast.makeText(AddAnnouncementsActivity.this, R.string.fields_cannot_be_empty, Toast.LENGTH_SHORT).show();
                        else {
                            Date date = Calendar.getInstance().getTime();

                            String senderNode = null;
                            if (userType.equals("Teacher"))
                                senderNode = "teachers";
                            else if (userType.equals("Doctor"))
                                senderNode = "doctors";
                            else if (userType.equals("PE"))
                                senderNode = "PEs";
                            else if (userType.equals("Bus Admin"))
                                senderNode = "bus";
                            else if (userType.equals("Manager"))
                                senderNode = "managers";

                            Announcement announcement_obj = new Announcement(title.getText().toString(), date.getTime() + "",
                                    announcement.getText().toString(), senderNode, Utilities.getCurrentUID());

                            for (int i = 0; i < receivers_list.size(); i++) {
                                String user = receivers_list.get(i);
                                if (user.equals("Teachers")) {
                                    databaseReference = firebaseDatabase.getReference().child("announcements").child("Teacher");
                                    String pushID = databaseReference.push().getKey();
                                    databaseReference.child(pushID).setValue(announcement_obj);

                                } else if (user.equals("PEs")) {
                                    databaseReference = firebaseDatabase.getReference().child("announcements").child("PE");
                                    String pushID = databaseReference.push().getKey();
                                    databaseReference.child(pushID).setValue(announcement_obj);

                                } else if (user.equals("Doctors")) {
                                    databaseReference = firebaseDatabase.getReference().child("announcements").child("Doctor");
                                    String pushID = databaseReference.push().getKey();
                                    databaseReference.child(pushID).setValue(announcement_obj);

                                } else if (user.equals("Bus Admins")) {
                                    databaseReference = firebaseDatabase.getReference().child("announcements").child("Bus Admin");
                                    String pushID = databaseReference.push().getKey();
                                    databaseReference.child(pushID).setValue(announcement_obj);

                                } else if (user.equals("Parents")) {
                                    databaseReference = firebaseDatabase.getReference().child("announcements").child("Parent");
                                    String pushID = databaseReference.push().getKey();
                                    databaseReference.child(pushID).setValue(announcement_obj);

                                }  else if (user.contains("Students")){
                                    String[] userData = user.split(":");
                                    String yearName = userData[1];
                                    String className = userData[2];

                                    databaseReference = firebaseDatabase.getReference().child("announcements").child("Student")
                                            .child("Years").child(yearName).child(className);
                                    String pushID = databaseReference.push().getKey();
                                    databaseReference.child(pushID).setValue(announcement_obj);
                                }
                            }

                            Toast.makeText(AddAnnouncementsActivity.this, "Announcement sent successfully", Toast.LENGTH_SHORT).show();
                            setResult(RESULT_OK);
                            finish();
                        }
                    } else
                        Toast.makeText(AddAnnouncementsActivity.this, "Select receivers", Toast.LENGTH_SHORT).show();
                } else
                    Toast.makeText(AddAnnouncementsActivity.this, R.string.check_internet_connection, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void refreshListView() {
        if (list_adapter.getCount() == 0)
            noReceivers.setVisibility(View.VISIBLE);
        else {
            if (noReceivers.getVisibility() == View.VISIBLE)
                noReceivers.setVisibility(View.GONE);
        }
        list_adapter.notifyDataSetChanged();
        Utilities.getTotalHeightofListView(listView);
    }
}
