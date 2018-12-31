package com.safwat.abanoub.bridgeit;

import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;

class AddGradesAdapter extends BaseAdapter{

    Context context;
    ArrayList<Student> students_list;
    String[] grades_arr;

    public AddGradesAdapter(Context context, ArrayList<Student> students_list) {
        this.context=context;
        this.students_list=students_list;
        grades_arr=new String[students_list.size()];
    }

    @Override
    public int getCount() {
        return students_list.size();
    }

    @Override
    public Object getItem(int i) {
        return students_list.get(i);
    }

    public String[] getGrades_arr() {
        return grades_arr;
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(final int position, View view, ViewGroup viewGroup) {
        if (view == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.add_grades_list_item, null);
        }

        ((TextView) view.findViewById(R.id.name)).setText(students_list.get(position).fullname);
        String profileURL = students_list.get(position).profileURL;
        if (profileURL != null && !profileURL.isEmpty())
            Glide.with(context)
                    .load(profileURL)
                    .into((ImageView) view.findViewById(R.id.profile));

        final EditText editText=view.findViewById(R.id.editText);
        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                if (charSequence.toString().equals(""))
                    grades_arr[position]=null;
                else
                    grades_arr[position]=charSequence.toString();
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        return view;
    }
}
