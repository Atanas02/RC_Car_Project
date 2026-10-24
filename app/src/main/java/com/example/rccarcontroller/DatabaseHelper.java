package com.example.rccarcontroller;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "car_controller.db";
    private static final int DATABASE_VERSION = 1;

    public static final String TABLE_NAME = "command_history";

    public static final String COL_ID = "id";
    public static final String COL_COMMAND = "command";
    public static final String COL_TIME = "date_time";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        String createTable =
                "CREATE TABLE " + TABLE_NAME + "(" +
                        COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                        COL_COMMAND + " TEXT," +
                        COL_TIME + " TEXT)";

        db.execSQL(createTable);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }

    public void insertCommand(String command, String dateTime) {

        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(COL_COMMAND, command);
        values.put(COL_TIME, dateTime);

        db.insert(TABLE_NAME, null, values);
        db.close();
    }
}
