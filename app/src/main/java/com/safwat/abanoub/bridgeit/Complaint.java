package com.safwat.abanoub.bridgeit;

import android.os.Parcel;
import android.os.Parcelable;

public class Complaint implements Parcelable {
    public String uid;
    public String senderUID;
    public String title;
    public String message;
    public String status;
    public String date;
    public String senderNode;

    public Complaint() {
    }

    public Complaint(String senderUID,String title, String message, String status,String date,String senderNode) {
        this.senderUID=senderUID;
        this.title=title;
        this.message = message;
        this.status = status;
        this.date=date;
        this.senderNode=senderNode;
    }

    protected Complaint(Parcel in) {
        uid = in.readString();
        senderUID= in.readString();
        title = in.readString();
        message = in.readString();
        status = in.readString();
        date = in.readString();
        senderNode=in.readString();
    }

    public static final Creator<Complaint> CREATOR = new Creator<Complaint>() {
        @Override
        public Complaint createFromParcel(Parcel in) {
            return new Complaint(in);
        }

        @Override
        public Complaint[] newArray(int size) {
            return new Complaint[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(uid);
        parcel.writeString(senderUID);
        parcel.writeString(title);
        parcel.writeString(message);
        parcel.writeString(status);
        parcel.writeString(date);
        parcel.writeString(senderNode);
    }
}
