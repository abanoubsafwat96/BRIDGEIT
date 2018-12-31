package com.safwat.abanoub.bridgeit;

import android.os.Parcel;
import android.os.Parcelable;

class DoctorReport implements Parcelable{
    public String studentUID;
    public String title;
    public String suggestedMedicine;
    public String diseaseDuration;
    public String date;

    public DoctorReport() {
    }

    public DoctorReport(String studentUID, String title, String suggestedMedicine, String diseaseDuration, String date) {
        this.studentUID = studentUID;
        this.title = title;
        this.suggestedMedicine = suggestedMedicine;
        this.diseaseDuration = diseaseDuration;
        this.date = date;
    }

    protected DoctorReport(Parcel in) {
        studentUID = in.readString();
        title = in.readString();
        suggestedMedicine = in.readString();
        diseaseDuration = in.readString();
        date = in.readString();
    }

    public static final Creator<DoctorReport> CREATOR = new Creator<DoctorReport>() {
        @Override
        public DoctorReport createFromParcel(Parcel in) {
            return new DoctorReport(in);
        }

        @Override
        public DoctorReport[] newArray(int size) {
            return new DoctorReport[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(studentUID);
        parcel.writeString(title);
        parcel.writeString(suggestedMedicine);
        parcel.writeString(diseaseDuration);
        parcel.writeString(date);
    }
}
