package com.safwat.abanoub.bridgeit;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

public class PortfolioAdapter extends BaseAdapter {

    Context context;
    ArrayList<Portfolio> list;

    public PortfolioAdapter(Context context, ArrayList<Portfolio> list) {
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
            view = inflater.inflate(R.layout.portfolio_list_item, null);
        }

        ((TextView) view.findViewById(R.id.subjectsName)).setText(list.get(i).subjectName);

        ((TextView) view.findViewById(R.id.starCount)).setText(list.get(i).star);
        ((TextView) view.findViewById(R.id.crownCount)).setText(list.get(i).crown);
        ((TextView) view.findViewById(R.id.trophyCount)).setText(list.get(i).trophy);

        return view;
    }
}
