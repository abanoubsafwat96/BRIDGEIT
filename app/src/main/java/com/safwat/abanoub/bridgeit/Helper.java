package com.safwat.abanoub.bridgeit;

import android.content.Context;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.widget.Toast;

import java.time.Year;

/**
 * Created by Bob on 2016-11-04.
 */

public class Helper extends SQLiteOpenHelper {
    public static final String DATABASE_NAME = "NewsDataBase";
    public static final String YEARS_TABLE_NAME = "YearsTable";
    public static final String CLASSES_TABLE_NAME = "ClassesTable";
    public static final int DATABASE_VERSION = 1;

    //Columns of Year table
    public static final String year_UID = "_id";
    public static final String year_name = "year_name";

    //Columns of Classes table
    public static final String class_UID = "_id";
    public static final String class_name = "class_name";

    public static final String CREATE_YEARS_TABLE = "CREATE TABLE " + YEARS_TABLE_NAME + " (" + year_UID
            + " INTEGER PRIMARY KEY AUTOINCREMENT, " + year_name + " VARCHAR(225) );";

    public static final String CREATE_CLASSES_TABLE = "CREATE TABLE " + CLASSES_TABLE_NAME + " ("
            + class_UID + " INTEGER PRIMARY KEY AUTOINCREMENT, " + class_name + " VARCHAR(225), " + year_name + " VARCHAR(225) );";

    public static final String DROP_YEARS_TABLE = "DROP TABLE IF EXIST " + YEARS_TABLE_NAME;
    public static final String DROP_CLASSES_TABLE = "DROP TABLE IF EXIST " + CLASSES_TABLE_NAME;

    public Context context;

    public Helper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        try {
            sqLiteDatabase.execSQL(CREATE_YEARS_TABLE);
            sqLiteDatabase.execSQL(CREATE_CLASSES_TABLE);
        } catch (SQLException e) {
            Toast.makeText(context, "" + e, Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        try {
            sqLiteDatabase.execSQL(DROP_YEARS_TABLE);
            sqLiteDatabase.execSQL(DROP_CLASSES_TABLE);

            onCreate(sqLiteDatabase);
        } catch (SQLException e) {
            Toast.makeText(context, "" + e, Toast.LENGTH_LONG).show();
        }
    }
}
