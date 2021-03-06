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
    private List<Track> mTracks;
    private List<Track> mBestTracks;

    public static TrackBase get(Context context) {
        if (sTrackBase == null) {
            sTrackBase = new TrackBase(context);
        }

        return sTrackBase;
    }

    private TrackBase(Context context) {
        mTracks = new ArrayList<>();
    }

    public int getSize(boolean isbest) {
        return !isbest?mTracks.size():mBestTracks.size();

    }

    public void setTracks(List<Track> tracks) {

        mTracks = tracks;
    }

    public List<Track> getTracks(boolean isbest) {
        if (isbest){
            mBestTracks = new ArrayList<>();
            for (Track Track : mTracks) {
                if (Track.isBest()) {
                    mBestTracks.add(Track);
                }
            }
            return mBestTracks;
        }
        return mTracks;
    }

    public void addtrack(Track track) {

    }

    public Track getTrack(UUID id) {
        for (Track Track : mTracks) {
            if (Track.getId().equals(id)) {
                return Track;
            }
        }

        return null;
    }
}


