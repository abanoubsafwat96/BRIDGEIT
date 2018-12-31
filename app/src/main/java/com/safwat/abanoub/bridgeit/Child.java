package com.safwat.abanoub.bridgeit;

import android.os.Parcel;
import android.os.Parcelable;

class Child implements Parcelable{
    public String uid_key;
    public String uid;
    public String fullname;
    public String profileURL;

    public Child() {
    }

    protected Child(Parcel in) {
        uid_key = in.readString();
        uid = in.readString();
        fullname = in.readString();
        profileURL = in.readString();
    }

    public static final Creator<Child> CREATOR = new Creator<Child>() {
        @Override
        public Child createFromParcel(Parcel in) {
            return new Child(in);
        }

        @Override
        public Child[] newArray(int size) {
            return new Child[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(uid_key);
        parcel.writeString(uid);
        parcel.writeString(fullname);
        parcel.writeString(profileURL);
    }
}
