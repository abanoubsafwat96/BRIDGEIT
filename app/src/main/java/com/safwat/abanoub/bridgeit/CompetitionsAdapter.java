package com.safwat.abanoub.bridgeit;

import android.content.Context;
import android.support.constraint.ConstraintLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class CompetitionsAdapter extends BaseAdapter {

    Context context;
    ArrayList<Competition> competitions_list;
    String activityType;

    private SimpleDateFormat dateFormatForDay;
    boolean[] choosed_competitions_arr;

    public CompetitionsAdapter(Context context, ArrayList<Competition> competitions_list, String activityType) {
        this.context = context;
        this.competitions_list = competitions_list;
        this.activityType = activityType;

        dateFormatForDay = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        choosed_competitions_arr = new boolean[competitions_list.size()];
    }

    @Override
    public int getCount() {
        return competitions_list.size();
    }

    @Override
    public Object getItem(int i) {
        return competitions_list.get(i);
    }

    public boolean[] getChoosed_competitions_arr() {
        return choosed_competitions_arr;
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(final int i, View view, ViewGroup viewGroup) {
        if (view == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.competition_list_item, null);
        }

        Competition competition = (Competition) getItem(i);

        TextView dealine = (TextView) view.findViewById(R.id.deadline);
        Date deadline_date = new Date(Long.parseLong(competition.deadline));
        dealine.setText(dateFormatForDay.format(deadline_date));

        if (activityType.equals("StudentChooseCompetition")) {
            if (deadline_date.before(Calendar.getInstance().getTime()))
                ((ConstraintLayout) view.findViewById(R.id.layout)).setBackgroundColor(
                        context.getResources().getColor(R.color.disabled_button));
        }

        ((TextView) view.findViewById(R.id.title)).setText(competition.title);
        ((TextView) view.findViewById(R.id.description)).setText(competition.description);

        if (competition.bonusQuestions_list == null)
            ((TextView) view.findViewById(R.id.questions_count))
                    .setText(competition.questions_list.size() + " Questions and No Bonus Questions");
        else
            ((TextView) view.findViewById(R.id.questions_count))
                    .setText(competition.questions_list.size() + " Questions and "
                            + competition.bonusQuestions_list.size() + " Bonus Questions");


        CheckBox checkBox = (CheckBox) view.findViewById(R.id.checkbox);
        if (activityType.equals("QuickCompetitions"))
            checkBox.setVisibility(View.VISIBLE);

        checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                choosed_competitions_arr[i] = isChecked;
            }
        });

        return view;
    }
}
