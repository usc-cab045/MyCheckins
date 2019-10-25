package android.bignerdranch.com;

import android.location.Location;
import android.telecom.Call;

import java.util.Date;
import java.util.UUID;

public class Record {
    public UUID getId() {
        return mId;
    }

    private UUID mId;

    public String getTitle() {
        return mTitle;
    }

    public void setTitle(String title) {
        mTitle = title;
    }

    private String mTitle;

    public Date getDate() {
        return mDate;
    }

    public void setDate(Date date) {
        mDate = date;
    }

    private Date mDate;

    private String mPlace;
    public String getPlace() {
        return mPlace;
    }

    public void setPlace(String place) {
        mPlace = place;
    }

    private String mDetails;

    public String getDetails() { return mDetails; }

    public void setDetails(String details){ mDetails = details; }


    public Record() {
        this(UUID.randomUUID());
    }

    public Record(UUID id) {
        mId = id;
        mDate = new Date();
    }
    public String getPhotoFilename() {
        return "IMG_" + getId().toString() + ".jpg";
    }
    private double mLat;

    public void setLat(double lat) {
        mLat = lat;
    }
    public double getLat(){
        return mLat;
    }

    private double mLon;

    public void setLong(double aLon) {
        mLon = aLon;
    }

    public double getLon() {
        return mLon;
    }
}
