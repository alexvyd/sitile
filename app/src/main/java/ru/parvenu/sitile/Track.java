package ru.parvenu.sitile;

import java.util.Date;
import java.util.UUID;

public class Track {
    private UUID mId;
    private String mTitle;
    private Date mDate;
    private boolean mSolved;
    public int bindpos;


    public Track() {
        this(UUID.randomUUID());
        //mId = UUID.randomUUID();
        //mDate = new Date();
    }

    public Track(UUID id) {
        mId =id;
        mDate = new Date();
    }

    public UUID getId() {
        return mId;
    }

    public String getTitle() {
        return mTitle;
    }

    public void setTitle(String title) {
        mTitle = title;
    }

    public Date getDate() {
        return mDate;
    }

    public void setDate(Date date) {
        mDate = date;
    }

    public boolean isSolved() {
        return mSolved;
    }

    public void setSolved(boolean solved) {
        mSolved = solved;
    }



}
