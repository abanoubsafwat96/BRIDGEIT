package com.safwat.abanoub.bridgeit;

import android.app.Activity;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import com.github.sundeepk.compactcalendarview.CompactCalendarView;
import com.github.sundeepk.compactcalendarview.domain.Event;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class CalendarActivity extends Activity {

    TextView monthYear_textView, date_textView, noEventsToday;
    CompactCalendarView compactCalendarView;
    ListView listView;

    private SimpleDateFormat dateFormatForMonth, dateFormatForDay;
    private String userType;

    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;
    String user_uid;
    private Classroom classroom;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calendar);

        String usedAsParent = getIntent().getStringExtra("usedAsParent");
        if (usedAsParent != null) //parent
            user_uid = getIntent().getStringExtra("clicked_user_uid");

        else //student or teacher
            user_uid = Utilities.getCurrentUID();

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(CalendarActivity.this);
        //reading from shared preference
        userType = sharedPreferences.getString("userType", "not found");

        monthYear_textView = findViewById(R.id.monthyear);
        compactCalendarView = findViewById(R.id.compactcalendar_view);
        date_textView = findViewById(R.id.date);
        listView = findViewById(R.id.listView);
        noEventsToday = findViewById(R.id.noEventsToday);

        dateFormatForMonth = new SimpleDateFormat("MMMM yyyy", Locale.getDefault());
        dateFormatForDay = new SimpleDateFormat("EEEE ',' dd MMMM yyyy", Locale.getDefault());
        monthYear_textView.setText(dateFormatForMonth.format(compactCalendarView.getFirstDayOfCurrentMonth()));

        // Set first day of week to Monday, defaults to Monday so calling setFirstDayOfWeek is not necessary
        // Use constants provided by Java Calendar class
        compactCalendarView.setFirstDayOfWeek(Calendar.MONDAY);
        compactCalendarView.setUseThreeLetterAbbreviation(true);

        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference().child("students").child(user_uid)
                .child("class").child("id");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                String studentClassID = Utilities.getStudentClassID(dataSnapshot);

                firebaseDatabase.getReference().child("Classes").child(studentClassID)
                        .addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                classroom = Utilities.getClassroom(dataSnapshot);

                                firebaseDatabase.getReference().child("calendar").child("Years").child(classroom.yearName)
                                        .child(classroom.className).addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                                        ArrayList<CalendarEvent> list = Utilities.getAllCalendarEvents(dataSnapshot);
                                        addEventsToCalendar(list);
                                        showDateEvents(Calendar.getInstance().getTime());
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

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        // define a listener to receive callbacks when certain events happen.
        compactCalendarView.setListener(new CompactCalendarView.CompactCalendarViewListener() {
            @Override
            public void onDayClick(Date dateClicked) {
                showDateEvents(dateClicked);
            }

            @Override
            public void onMonthScroll(Date firstDayOfNewMonth) {
                monthYear_textView.setText(dateFormatForMonth.format(firstDayOfNewMonth));
            }
        });

        if (userType.equals("Student") || userType.equals("Parent")) {
        } else if (userType.equals("Teacher")) {
        }
    }

    private void showDateEvents(Date dateClicked) {

        List<Event> events = compactCalendarView.getEvents(dateClicked);
        Log.d("tag", "Day was clicked: " + dateClicked + " with events " + events);

        date_textView.setText(dateFormatForDay.format(dateClicked));

        ArrayList<CalendarEvent> events_list = new ArrayList<>();

        if (events.size() == 0) {
            noEventsToday.setVisibility(View.VISIBLE);
        } else if (events.size() > 0) {
            if (noEventsToday.getVisibility() == View.VISIBLE)
                noEventsToday.setVisibility(View.GONE);
        }

        for (int i = 0; i < events.size(); i++) {
            Event event = events.get(i);
            events_list.add(new CalendarEvent(event.getTimeInMillis() + "", event.getData().toString()));
        }
        fillListView(events_list);

    }

    private void addEventsToCalendar(ArrayList<CalendarEvent> list) {

        for (int i = 0; i < list.size(); i++) {

            CalendarEvent event = list.get(i);
            // Add events to calendar
            Event ev1 = new Event(Color.GREEN, Long.parseLong(event.timeInMillis), event.message);
            compactCalendarView.addEvent(ev1);
        }
    }

    public void fillListView(ArrayList<CalendarEvent> list) {

        CalendarAdapter adapter = new CalendarAdapter(this, list);
        listView.setAdapter(adapter);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        // Unregister VisualizerActivity as an OnPreferenceChangedListener to avoid any memory leaks.
        PreferenceManager.getDefaultSharedPreferences(this);
    }
}
