package com.safwat.abanoub.bridgeit;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;

/**
 * Created by Abanoub on 2018-03-03.
 */

public class Msg implements Parcelable{
    public String uid;
    public String sender;
    public String message;
    public String photoUrl;

    public Msg(String sender, String message, String photoUrl) {
        this.sender = sender;
        this.message = message;
        this.photoUrl=photoUrl;
    }

    public Msg() {
    }

    protected Msg(Parcel in) {
        uid = in.readString();
        sender = in.readString();
        message = in.readString();
        photoUrl = in.readString();
    }

    public static final Creator<Msg> CREATOR = new Creator<Msg>() {
        @Override
        public Msg createFromParcel(Parcel in) {
            return new Msg(in);
        }

        @Override
        public Msg[] newArray(int size) {
            return new Msg[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(uid);
        parcel.writeString(sender);
        parcel.writeString(message);
        parcel.writeString(photoUrl);
    }
}
