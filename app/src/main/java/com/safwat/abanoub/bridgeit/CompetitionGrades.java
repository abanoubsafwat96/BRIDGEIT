package com.safwat.abanoub.bridgeit;

import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;
import java.util.Map;

class CompetitionGrades implements Serializable{
    public String title;
    public Map<String,String> grades_map;

    public CompetitionGrades() {
    }
}
