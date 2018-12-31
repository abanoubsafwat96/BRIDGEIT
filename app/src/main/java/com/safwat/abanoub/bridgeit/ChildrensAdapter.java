package com.safwat.abanoub.bridgeit;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

class ChildrensAdapter extends RecyclerView.Adapter<ChildrensAdapter.ViewHolder> {

    Communicator.ChildrensRecyclerView communicator;
    Context context;
    ArrayList<Child> children_list;

    public ChildrensAdapter(Communicator.ChildrensRecyclerView communicator, Context context, ArrayList<Child> children_list) {
        this.communicator=communicator;
        this.context = context;
        this.children_list = children_list;
    }

    @NonNull
    @Override
    public ChildrensAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.childrens_list_item, null);

        ViewHolder viewHolder = new ViewHolder(view);

        return viewHolder;
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        CircleImageView profilePic;
        TextView username;

        public ViewHolder(View itemView) {
            super(itemView);
            profilePic = itemView.findViewById(R.id.image);
            username = itemView.findViewById(R.id.username);

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            int position = getAdapterPosition();

            communicator.onChildChoosed(position);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull ChildrensAdapter.ViewHolder holder, final int i) {

        Child child=children_list.get(i);
        String profile_pic = child.profileURL;
        if (profile_pic != null)
            Glide.with(context)
                    .load(profile_pic)
                    .into(holder.profilePic);

        holder.username.setText(child.fullname);
    }

    @Override
    public int getItemCount() {
        return children_list.size();
    }
}
