package com.safwat.abanoub.bridgeit;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

class StudentFeedbacksAdapter extends BaseAdapter{

    Context context;
    ArrayList<StudentFeedback> list;

    public StudentFeedbacksAdapter(Context context, ArrayList<StudentFeedback> list) {
        this.context=context;
        this.list=list;
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int i) {
        return list.get(i);
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        if (view == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.student_feedbacks_list_item, null);
        }

        ((TextView) view.findViewById(R.id.note)).setText(list.get(i).note);
        ((TextView) view.findViewById(R.id.name)).setText(list.get(i).fullname);
        ((TextView) view.findViewById(R.id.date)).setText(list.get(i).date);

        return view;
    }
}
