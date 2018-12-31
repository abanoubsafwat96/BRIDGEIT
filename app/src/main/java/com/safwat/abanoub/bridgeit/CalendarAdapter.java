package com.safwat.abanoub.bridgeit;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

public class CalendarAdapter extends BaseAdapter{
   
    Context context;
    ArrayList<CalendarEvent> list;

    public CalendarAdapter(Context context, ArrayList<CalendarEvent> list) {
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
            view = inflater.inflate(R.layout.calendar_list_item, null);
        }

        ((TextView) view.findViewById(R.id.message)).setText(list.get(i).message);

        return view;
    }
}

