package com.safwat.abanoub.bridgeit;

import android.widget.ImageView;
import android.widget.TextView;

import de.hdodenhof.circleimageview.CircleImageView;

public class GalleryPicture {
    public String pushID;
    public String uid;
    public String picture;
    public String node;

    public GalleryPicture() {
    }

    public GalleryPicture(String uid, String picture,String node) {
        this.uid = uid;
        this.picture = picture;
        this.node= node;
    }
}
