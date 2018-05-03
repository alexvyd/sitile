package ru.parvenu.sitile;

import android.net.Uri;

import java.util.Date;
import java.util.UUID;

//Класс треков - центральных объектов приложения

public class Track {
    private UUID mId; //уникальный идентификатор трека
    private String sId; //внешний идентификатор трека Flickr
    private String mTitle; //Название трека
    private String mUrl; //Адрес превью
    private String mOwner;
    private String mServer;
    private String mFarm;
    private String mSecret;
    private Date mDate;
    private boolean mBest; //Статус лучшего
    public int bindpos; //позиция в списке элементов 0..n
    //private static final String SECRET_KEY = "62dcac6ae557af91";


    public Track() {
        this(UUID.randomUUID());
        //mId = UUID.randomUUID();
        //mDate = new Date();
    }

    public Track(UUID id) {
        mId = id;
        mDate = new Date();
    }

    public Track(String id) {
        sId = id;
        mId = UUID.randomUUID();
        mDate = new Date();
    }

    public Uri getPhotoPageUri() {
        return Uri.parse("https://www.flickr.com/photos/")
                .buildUpon()
                .appendPath(mOwner)
                .appendPath(sId)
                .build();
    }

    public Uri getPhotoUri() {
        return Uri.parse("https://farm" + mFarm + ".staticflickr.com/" + mServer + "/"+sId+"_" + mSecret + ".jpg");
    }

    public String getOwner () {
        return mOwner;
    }
    public String getSecret() {
        return mSecret;
    }

    public void setSecret(String secret) {
        mSecret = secret;
    }

    public void setOwner (String owner){
        mOwner = owner;
    }

    public void setUrl (String url){
        mUrl = url;
    }

    public String getUrl () {
        return mUrl;
    }

    public void setId (UUID id){
        mId = id;
    }

    public UUID getId () {
        return mId;
    }

    public String getsId () {
        return sId;
    }

    public String getTitle () {
        return mTitle;
    }

    public void setTitle (String title){
        mTitle = title;
    }

    public Date getDate () {
        return mDate;
    }

    public void setDate (Date date){
        mDate = date;
    }

    public boolean isBest () {
        return mBest;
    }

    public void setBest ( boolean best){
        mBest = best;
    }



    public String getServer() {
        return mServer;
    }

    public void setServer(String server) {
        mServer = server;
    }

    public String getFarm() {
        return mFarm;
    }

    public void setFarm(String farm) {
        mFarm = farm;
    }
}