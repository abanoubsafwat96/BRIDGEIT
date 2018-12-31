package com.safwat.abanoub.bridgeit;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

class GalleryAdapter extends RecyclerView.Adapter<GalleryAdapter.ViewHolder> {

    Context context;
    Communicator.Gallery communicator;
    ArrayList<GalleryPicture> pictures_list;
    ArrayList<Teacher> senders_list;
    ArrayList<ArrayList<String>> likers_uids_list;

    public GalleryAdapter(Context context, ArrayList<GalleryPicture> pictures_list, ArrayList<Teacher> senders_list) {
        this.context = context;
        this.pictures_list = pictures_list;
        this.senders_list = senders_list;
    }

    public GalleryAdapter(Context context, Communicator.Gallery communicator, ArrayList<GalleryPicture> pictures_list, ArrayList<Teacher> senders_list, ArrayList<ArrayList<String>> likers_uids_list) {
        this.context = context;
        this.communicator = communicator;
        this.pictures_list = pictures_list;
        this.senders_list = senders_list;
        this.likers_uids_list = likers_uids_list;
    }

    @NonNull
    @Override
    public GalleryAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.gallery_list_item, null);

        ViewHolder viewHolder = new ViewHolder(view);

        return viewHolder;
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        CircleImageView profilePic;
        ImageView like_btn;
        TextView username, likes;
        ImageView picture;

        public ViewHolder(View itemView) {
            super(itemView);
            profilePic = itemView.findViewById(R.id.profile);
            like_btn = itemView.findViewById(R.id.like_btn);
            username = itemView.findViewById(R.id.username);
            likes = itemView.findViewById(R.id.likes);
            picture = itemView.findViewById(R.id.picture);

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {

        }
    }

//    @Override
//    public void onBindViewHolder(@NonNull final GalleryAdapter.ViewHolder holder, final int i) {
//
//        if (i < senders_list.size()) {
//            Teacher sender = senders_list.get(i);
//            if (sender.profileURL != null && !sender.profileURL.isEmpty())
//                Glide.with(context)
//                        .load(sender.profileURL)
//                        .into(holder.profilePic);
//
//            holder.username.setText(sender.username);
//
//            String picture = pictures_list.get(i).picture;
//            if (picture != null && !picture.isEmpty())
//                Glide.with(context)
//                        .load(picture)
//                        .into(holder.picture);
//        }
//    }

    @Override
    public void onBindViewHolder(@NonNull final GalleryAdapter.ViewHolder holder, final int i) {

        if (i < senders_list.size() && i < likers_uids_list.size()) {
            Teacher sender = senders_list.get(i);
            if (sender.profileURL != null && !sender.profileURL.isEmpty())
                Glide.with(context)
                        .load(sender.profileURL)
                        .into(holder.profilePic);

            holder.username.setText(sender.username);

            String picture = pictures_list.get(i).picture;
            if (picture != null && !picture.isEmpty())
                Glide.with(context)
                        .load(picture)
                        .into(holder.picture);

            final boolean[] likedByMe = {false};
            String current_uid = Utilities.getCurrentUID();
            ArrayList<String> likers_list = likers_uids_list.get(i);

            if (likers_list != null) {
                for (int j = 0; j < likers_list.size(); j++) {
                    if (likers_list.get(j).equals(current_uid)) {
                        likedByMe[0] = true;
                        break;
                    }
                }

                if (likedByMe[0])
                    holder.like_btn.setImageResource(R.drawable.ic_heart_red_24dp);

                holder.likes.setText(likers_list.size() + " Likes");
            }

            holder.like_btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    communicator.likeBtnClicked();

                    if (likedByMe[0]) { //clear like
                        communicator.clearLike(i);
                        holder.like_btn.setImageResource(R.drawable.ic_heart_white_24dp);
                        likedByMe[0] = false;

                    } else { //add like
                        communicator.addLike(i);
                        holder.like_btn.setImageResource(R.drawable.ic_heart_red_24dp);
                        likedByMe[0] = true;
                    }
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return pictures_list.size();
    }
}
