package com.safwat.abanoub.bridgeit;

import android.os.Parcel;
import android.os.Parcelable;

class Classroom implements Parcelable{

    public String id;
    public String yearName;
    public String className;

    public Classroom() {
    }

    public Classroom(String yearName, String className) {
        this.yearName = yearName;
        this.className = className;
    }

    protected Classroom(Parcel in) {
        id = in.readString();
        yearName = in.readString();
        className = in.readString();
    }

    public static final Creator<Classroom> CREATOR = new Creator<Classroom>() {
        @Override
        public Classroom createFromParcel(Parcel in) {
            return new Classroom(in);
        }

        @Override
        public Classroom[] newArray(int size) {
            return new Classroom[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(id);
        parcel.writeString(yearName);
        parcel.writeString(className);
    }
}
