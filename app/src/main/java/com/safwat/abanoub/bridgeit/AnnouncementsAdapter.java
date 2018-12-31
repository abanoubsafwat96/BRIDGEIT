package com.safwat.abanoub.bridgeit;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Locale;

public class AnnouncementsAdapter extends BaseAdapter {

    Context context;
    ArrayList<Announcement> announcements_list;
    ArrayList<String> userNames_list;
    ArrayList<String> postedTo_list;
    SimpleDateFormat dateFormat;

    public AnnouncementsAdapter(Context context, ArrayList<Announcement> announcements_list, ArrayList<String> userNames_list
        ,ArrayList<String> postedTo_list) {
        this.context = context;
        this.announcements_list=announcements_list;
        this.userNames_list=userNames_list;
        this.postedTo_list=postedTo_list;
        dateFormat = new SimpleDateFormat("EEEE ',' dd MMMM yyyy hh:mm:ss a", Locale.getDefault());
    }

    @Override
    public int getCount() {
        return announcements_list.size();
    }

    @Override
    public Object getItem(int i) {
        return announcements_list.get(i);
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        if (view == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.announcement_list_item, null);
        }

        Announcement announcement = (Announcement) getItem(i);

        dateFormat = new SimpleDateFormat("EEEE ',' dd MMMM yyyy hh:mm:ss a", Locale.getDefault());
        String formattedDate = dateFormat.format(Long.parseLong(announcement.date));

        ((TextView) view.findViewById(R.id.title)).setText(announcement.title);
        ((TextView) view.findViewById(R.id.date)).setText("Posted on: " + formattedDate);
        ((TextView) view.findViewById(R.id.announcement)).setText(announcement.announcement);
        ((TextView) view.findViewById(R.id.postedBy)).setText(userNames_list.get(i));

        if (postedTo_list!=null){
            ((TextView) view.findViewById(R.id.postedTo)).setText(postedTo_list.get(i));
        }
        
        return view;
    }
}
