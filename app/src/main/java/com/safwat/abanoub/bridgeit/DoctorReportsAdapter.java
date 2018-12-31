package com.safwat.abanoub.bridgeit;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

class DoctorReportsAdapter extends BaseAdapter {

    Context context;
    ArrayList<DoctorReport> doctorReports_list;
    ArrayList<Student> students_list;

    public DoctorReportsAdapter(Context context, ArrayList<DoctorReport> doctorReports_list, ArrayList<Student> students_list) {
        this.context = context;
        this.doctorReports_list = doctorReports_list;
        this.students_list = students_list;
    }

    @Override
    public int getCount() {
        return doctorReports_list.size();
    }

    @Override
    public Object getItem(int i) {
        return doctorReports_list.get(i);
    }

    public Object getItem2(int i) {
        return students_list.get(i);
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        if (view == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.complaints_and_doctor_reports_list_item, null);
        }

        ((TextView) view.findViewById(R.id.title)).setText(doctorReports_list.get(i).title);
        if (students_list != null && !(i == students_list.size()))
            ((TextView) view.findViewById(R.id.studentName)).setText(students_list.get(i).fullname);
        ((TextView) view.findViewById(R.id.date)).setText(doctorReports_list.get(i).date);

        return view;
    }
}
