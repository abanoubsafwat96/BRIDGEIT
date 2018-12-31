package com.safwat.abanoub.bridgeit;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

class ComplaintsAdapter extends BaseAdapter {

    Context context;
    ArrayList<Complaint> complaints_list;
    ArrayList<Parent> parents_list;
    ArrayList<ArrayList<Msg>> replies_list;

    public ComplaintsAdapter(Context context, ArrayList<Complaint> complaints_list, ArrayList<Parent> parents_list) {
        this.context=context;
        this.complaints_list=complaints_list;
        this.parents_list=parents_list;
    }

    public ComplaintsAdapter(Context context, ArrayList<Complaint> complaints_list, ArrayList<Parent> parents_list,ArrayList<ArrayList<Msg>> replies_list) {
        this.context=context;
        this.complaints_list=complaints_list;
        this.parents_list=parents_list;
        this.replies_list=replies_list;
    }

    @Override
    public int getCount() {
        return complaints_list.size();
    }

    @Override
    public Object getItem(int i) {
        return complaints_list.get(i);
    }

    public Object getItem2(int i) {
     return parents_list.get(i);
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

        ((TextView) view.findViewById(R.id.title)).setText(complaints_list.get(i).title);
        if (parents_list != null && i < parents_list.size())
            ((TextView) view.findViewById(R.id.studentName)).setText(parents_list.get(i).fullname);

        ((TextView) view.findViewById(R.id.status)).setText(complaints_list.get(i).status);
        if (complaints_list.get(i).status.equals("not seen"))
            view.setBackgroundColor(context.getResources().getColor(R.color.smawy));
        else
            view.setBackgroundColor(context.getResources().getColor(R.color.white));

        ((TextView) view.findViewById(R.id.date)).setText(complaints_list.get(i).date);

        TextView replies=view.findViewById(R.id.replies);
        if (replies_list!=null) {
            if (i < replies_list.size()) {
                int replies_count = replies_list.get(i).size();
                if (replies_count > 0)
                    replies.setText(replies_count + " replies on this complaint");
            }
        }
        replies.setVisibility(View.VISIBLE);

        return view;
    }
}
