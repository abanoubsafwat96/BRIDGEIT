package com.safwat.abanoub.bridgeit;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class ParentsAdapter extends BaseAdapter {

    Context context;
    ArrayList<Parent> list;

    public ParentsAdapter(ChooseUserActivity context, ArrayList<Parent> list) {
        this.context = context;
        this.list = list;
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
            view = inflater.inflate(R.layout.choose_user_list_item, null);
        }

        ((TextView) view.findViewById(R.id.name)).setText(list.get(i).fullname);
        String profileURL = list.get(i).profileURL;
        if (profileURL != null && !profileURL.isEmpty())
            Glide.with(context)
                    .load(profileURL)
                    .into((CircleImageView) view.findViewById(R.id.profile));

        return view;

    }
}
