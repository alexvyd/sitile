package ru.parvenu.sitile.database;

import android.database.Cursor;
import android.database.CursorWrapper;

import java.util.Date;
import java.util.UUID;

import ru.parvenu.sitile.Track;
import ru.parvenu.sitile.database.TrackDbSchema.trackTable;

public class TrackCursorWrapper extends CursorWrapper {
    public TrackCursorWrapper(Cursor cursor) {
        super(cursor);
    }

    public Track gettrack() {
        String uuidString = getString(getColumnIndex(trackTable.Cols.UUID));
        String title = getString(getColumnIndex(trackTable.Cols.TITLE));
        long date = getLong(getColumnIndex(trackTable.Cols.DATE));
        int isBested = getInt(getColumnIndex(trackTable.Cols.BESTED));
        Track track = new Track(UUID.fromString(uuidString));
        track.setTitle(title);
        track.setDate(new Date(date));
        track.setBest(isBested != 0);
        return track;
    }
}
