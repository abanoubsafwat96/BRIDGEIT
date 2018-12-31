package com.safwat.abanoub.bridgeit;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;

/**
 * Created by Bob on 2016-11-04.
 */

public class DataBaseAdapter {
    Helper helper;

    public DataBaseAdapter(Context context) {
        helper = new Helper(context);
    }

    public long insertYear(String yearName) {
        SQLiteDatabase db = helper.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(Helper.year_name, yearName);
        long insertvalue = db.insert(Helper.YEARS_TABLE_NAME, null, contentValues);
        return insertvalue;
    }

    public ArrayList<String> getAllYears() {
        ArrayList<String> list = new ArrayList<>();
        SQLiteDatabase db = helper.getWritableDatabase();
        String[] columns = {Helper.year_UID, Helper.year_name};
        Cursor cursor = db.query(Helper.YEARS_TABLE_NAME, columns, null, null, null, null, null);
        while (cursor.moveToNext()) {

//            int cid = cursor.getInt(0);
            list.add( cursor.getString(1));
        }
        return list;
    }

    public boolean findYearByName(String yearName) {
        ArrayList<String> list = getAllYears();

        for (int i = 0; i < list.size(); i++) {
            if (yearName.equals(list.get(i)))
                return true;
        }
        return false;
    }

    public long insertClass(SchoolClass schoolClass) {
        SQLiteDatabase db = helper.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(Helper.class_name, schoolClass.className);
        contentValues.put(Helper.year_name, schoolClass.yearName);
        long insertvalue = db.insert(Helper.CLASSES_TABLE_NAME, null, contentValues);
        return insertvalue;
    }

    public ArrayList<SchoolClass> getAllClasses() {
        ArrayList<SchoolClass> list = new ArrayList<>();
        SQLiteDatabase db = helper.getWritableDatabase();
        String[] columns = {Helper.class_UID, Helper.class_name, Helper.year_name};
        Cursor cursor = db.query(Helper.CLASSES_TABLE_NAME, columns, null, null, null, null, null);
        while (cursor.moveToNext()) {
            SchoolClass schoolClass = new SchoolClass();

//            int cid = cursor.getInt(0);
            schoolClass.className = cursor.getString(1);
            schoolClass.yearName = cursor.getString(2);
            list.add(schoolClass);
        }
        return list;
    }

    public boolean findClassByName(String className) {
        ArrayList<SchoolClass> list = getAllClasses();

        for (int i = 0; i < list.size(); i++) {
            if (className.equals(list.get(i).className))
                return true;
        }
        return false;
    }

    public void deleteOldRecordsFromYearsTable() {
        SQLiteDatabase db = helper.getWritableDatabase();
        db.execSQL("delete from " + Helper.YEARS_TABLE_NAME);
    }

    public void deleteOldRecordsFromClassesTable() {
        SQLiteDatabase db = helper.getWritableDatabase();
        db.execSQL("delete from " + Helper.CLASSES_TABLE_NAME);
    }

    public ArrayList<String> getAllClassesFromYear(String yearName) {
        ArrayList<String> list =new ArrayList<>();

        SQLiteDatabase db = helper.getWritableDatabase();
        String query = "select * from " + Helper.CLASSES_TABLE_NAME + " where "+Helper.year_name+"='"+yearName+"'";
        Cursor cursor= db.rawQuery(query, null);
        while (cursor.moveToNext()){
            list.add(cursor.getString(1));
        }
        return list;
    }
}
