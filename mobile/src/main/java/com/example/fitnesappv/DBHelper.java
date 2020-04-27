package com.example.fitnesappv;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

public class DBHelper extends SQLiteOpenHelper {

    public enum GroupENUM{
        Ноги,
        Спина,
        Грудь,
        Руки,
        Плечи
    }
    public enum  ComplexENUM{
        День1,
        День2,
        День3
    }

    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "exercisesDB";
    public static final String TABLE_EXERCISE = "exerciseTable";

    public static final String KEY_ID = "_id";
    public static final String KEY_NAME = "name";
    public static final String KEY_APPROACH = "approach";
    public static final String KEY_GROUP = "group_key";
    public static final String KEY_COMPLEX = "complex_key";
    public  static final  String KEY_CONTENT = "content_key";

    public DBHelper(@Nullable Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table " + TABLE_EXERCISE + "(" + KEY_ID
                + " integer primary key," + KEY_NAME + " text," + KEY_APPROACH + " integer,"
                + KEY_GROUP + " integer,"+ KEY_COMPLEX + " integer," + KEY_CONTENT + " text" + ")");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("drop table if exists " + TABLE_EXERCISE);

        onCreate(db);
    }
}
