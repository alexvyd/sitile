package ru.parvenu.sitile.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import ru.parvenu.sitile.database.TrackDbSchema.trackTable;

public class TrackBaseHelper extends SQLiteOpenHelper {
    private static final int VERSION = 1;
    private static final String DATABASE_NAME = "trackBase.db";
    public TrackBaseHelper(Context context) {
        super(context, DATABASE_NAME, null, VERSION);
    }
    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table " + trackTable.NAME + "(" +
                " _id integer primary key autoincrement, " +
                trackTable.Cols.UUID + ", " +
                trackTable.Cols.TITLE + ", " +
                trackTable.Cols.DATE + ", " +
                trackTable.Cols.SOLVED +
                ")"
        );
    }
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    }
}
