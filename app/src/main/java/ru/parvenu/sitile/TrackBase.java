package ru.parvenu.sitile;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import ru.parvenu.sitile.database.TrackBaseHelper;
import ru.parvenu.sitile.database.TrackCursorWrapper;
import ru.parvenu.sitile.database.TrackDbSchema;

public class TrackBase {
    private static TrackBase sTrackBase;
    private Context mContext;
    private SQLiteDatabase mDatabase;

    public static TrackBase get(Context context) {
        if (sTrackBase == null) {
            sTrackBase = new TrackBase(context);
        }
        return sTrackBase;
    }

    private TrackBase(Context context) {
        mContext = context.getApplicationContext();
        mDatabase = new TrackBaseHelper(mContext)
                .getWritableDatabase();
        }


        public void addtrack(Track c) {
            ContentValues values = getContentValues(c);
            mDatabase.insert(TrackDbSchema.trackTable.NAME, null, values);
        }

        public List<Track> gettracks() {
            List<Track> tracks = new ArrayList<>();
            TrackCursorWrapper cursor = querytracks(null, null);
            try {
                cursor.moveToFirst();
                while (!cursor.isAfterLast()) {
                    tracks.add(cursor.gettrack());
                    cursor.moveToNext();
                }
            } finally {
                cursor.close();
            }
            return tracks;
            //return mTracks;
        }

        public Track gettrack(UUID id){
            TrackCursorWrapper cursor = querytracks(
                    TrackDbSchema.trackTable.Cols.UUID + " = ?",
                    new String[] { id.toString() }
            );
            try {
                if (cursor.getCount() == 0) {
                    return null;
                }
                cursor.moveToFirst();
                return cursor.gettrack();
            } finally {
                cursor.close();
            }
            //return null;
        }

    public void updatetrack(Track track) {
        String uuidString = track.getId().toString();
        ContentValues values = getContentValues(track);
        mDatabase.update(TrackDbSchema.trackTable.NAME, values,
                TrackDbSchema.trackTable.Cols.UUID + " = ?",
                new String[] { uuidString });
    }

   // private Cursor querytracks(String whereClause, String[] whereArgs) {
   private TrackCursorWrapper querytracks (String whereClause, String[] whereArgs)
   {
        Cursor cursor = mDatabase.query(
                TrackDbSchema.trackTable.NAME,
                null, // columns - с null выбираются все столбцы
                whereClause,
                whereArgs,
                null, // groupBy
                null, // having
                null // orderBy
        );
        //return cursor;
        return new TrackCursorWrapper(cursor);
    }

    private static ContentValues getContentValues(Track track) {
        ContentValues values = new ContentValues();
        values.put(TrackDbSchema.trackTable.Cols.UUID, track.getId().toString());
        values.put(TrackDbSchema.trackTable.Cols.TITLE, track.getTitle());
        values.put(TrackDbSchema.trackTable.Cols.DATE, track.getDate().getTime());
        values.put(TrackDbSchema.trackTable.Cols.BESTED, track.isBest() ? 1 : 0);
        return values;
    }


        public void deltrack(UUID id,int pos) {
            String uuidString = id.toString();
            mDatabase.delete(TrackDbSchema.trackTable.NAME, TrackDbSchema.trackTable.Cols.UUID + " = ?",
                    new String[] { uuidString });
        }
}
