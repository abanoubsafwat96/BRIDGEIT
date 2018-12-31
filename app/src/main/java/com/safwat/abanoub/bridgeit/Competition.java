package com.safwat.abanoub.bridgeit;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;

class Competition implements Parcelable {
    public String uid;
    public String title;
    public String description;
    public ArrayList<Question> questions_list;
    public ArrayList<Question> bonusQuestions_list;
    public String deadline;

    public Competition() {
    }

    public Competition(String title, String description, ArrayList<Question> questions_list, String deadline) {
        this.title = title;
        this.description = description;
        this.questions_list = questions_list;
        this.deadline = deadline;
    }

    public Competition(String title, String description, ArrayList<Question> questions_list,
                       ArrayList<Question> bonusQuestions_list, String deadline) {
        this.title = title;
        this.description = description;
        this.questions_list = questions_list;
        this.bonusQuestions_list = bonusQuestions_list;
        this.deadline = deadline;
    }

    protected Competition(Parcel in) {
        uid = in.readString();
        title = in.readString();
        description = in.readString();
        deadline = in.readString();
    }

    public static final Creator<Competition> CREATOR = new Creator<Competition>() {
        @Override
        public Competition createFromParcel(Parcel in) {
            return new Competition(in);
        }

        @Override
        public Competition[] newArray(int size) {
            return new Competition[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(uid);
        parcel.writeString(title);
        parcel.writeString(description);
        parcel.writeString(deadline);
    }
}
