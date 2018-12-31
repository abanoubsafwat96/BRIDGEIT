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

/**
 * Created by Abanoub on 2018-03-03.
 */

public class MessagesAdapter extends BaseAdapter {

    Context context;
    ArrayList<Msg> msgs_list;
    ArrayList<ArrayList<Msg>> replies_list;

    public MessagesAdapter(Context context, ArrayList<Msg> msgs_list) {
        this.context = context;
        this.msgs_list = msgs_list;
    }

    public MessagesAdapter(Context context, ArrayList<Msg> msgs_list, ArrayList<ArrayList<Msg>> replies_list) {
        this.context = context;
        this.msgs_list = msgs_list;
        this.replies_list=replies_list;
    }

    @Override
    public int getCount() {
        return msgs_list.size();
    }

    @Override
    public Object getItem(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.msg_single_item, null);
        }

        Msg msg = msgs_list.get(position);

        if (msg.sender.equals(Utilities.getCurrentEmail()))
            convertView.setBackgroundColor(context.getResources().getColor(R.color.white));
        else
            convertView.setBackgroundColor(context.getResources().getColor(R.color.smawy));

        TextView sender=(TextView) convertView.findViewById(R.id.sender);
        TextView message=(TextView) convertView.findViewById(R.id.msg);
        ImageView imageView=(ImageView) convertView.findViewById(R.id.image);
        TextView replies=(TextView) convertView.findViewById(R.id.replies);

        sender.setText(msg.sender);

        if (message.getText().toString().isEmpty())
            message.setVisibility(View.GONE);
        else
            message.setText(msg.message);

        if (msg.photoUrl != null){
            imageView.setVisibility(View.VISIBLE);
            Glide.with(context.getApplicationContext())
                    .load(msg.photoUrl)
                    .into(imageView);
        }

        if (replies_list!=null) {
            if (position < replies_list.size()) {
                int replies_count = replies_list.get(position).size();
                if (replies_count > 0)
                    replies.setText(replies_count + " replies on this message");
            }
        }else
            replies.setVisibility(View.GONE);

        return convertView;
    }
}
