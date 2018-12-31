package com.safwat.abanoub.bridgeit;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

public class DoctorReportsDetailedActivity extends AppCompatActivity {

    TextView fullname_textView, title, suggestedMedicine, diseaseDuration, date;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_doctor_reports_detailed);

        DoctorReport doctorReport = getIntent().getExtras().getParcelable("doctorReport");
        String fullname = getIntent().getExtras().getString("fullname");

        fullname_textView = findViewById(R.id.fullname);
        title = findViewById(R.id.title);
        suggestedMedicine = findViewById(R.id.suggestedMedicine);
        diseaseDuration = findViewById(R.id.diseaseDuration);
        date = findViewById(R.id.date);

        fullname_textView.setText(fullname);
        title.setText(doctorReport.title);
        suggestedMedicine.setText(doctorReport.suggestedMedicine);
        diseaseDuration.setText(doctorReport.diseaseDuration);
        date.setText(doctorReport.date);
    }
}