package ru.parvenu.sitile;

import java.util.Date;
import java.util.UUID;

//Класс треков - центральных объектов приложения

public class Track {
    private UUID mId; //уникальный идентификатор трека
    private String sId; //внешний идентификатор трека
    private String mTitle; //Название трека
    private String mUrl; //Адрес превью

    private Date mDate;
    private boolean mBest; //Статус лучшего
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

    public Track(String id) {
        sId = id;
        mId = UUID.randomUUID();
        mDate = new Date();
    }

    public void setUrl(String url) {
        mUrl=url;
    }

    public String getUrl() {
        return mUrl;
    }

    public void setId(UUID id) {
        mId=id;
    }

    public UUID getId() {
        return mId;
    }

    public String getsId() {
        return sId;
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

    public boolean isBest() {
        return mBest;
    }

    public void setBest(boolean best) {
        mBest = best;
    }



}
