package com.sfcoding.flare.Data;

import android.graphics.Bitmap;
import android.location.Location;

import com.google.android.gms.maps.model.LatLng;

/**
 * Created by Andrea on 06/07/2014.
 */
public class Person {
    public int numParam;
    public String id;
    public String name;
    public Double lastLat = Double.valueOf(91);
    public Double lastLng = Double.valueOf(181);
    public Bitmap photo;

    public void setId(String id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setLastLat(Double lastLat) {
        this.lastLat = lastLat;
    }

    public void setLastLng(Double lastLng) {
        this.lastLng = lastLng;
    }

    public void setPhoto(Bitmap photo) {
        this.photo = photo;
    }


    public Person() {

    }

    public Person(String id, String name, Double lastLat, Double lastLng, Bitmap photo) {
        this.id = id;
        this.name = name;
        this.photo = photo;
        this.lastLat = lastLat;
        this.lastLng = lastLng;
        numParam = 5;
    }

    public void setLastLatLng(Double lat, Double lastLng) {
        this.lastLat = lat;
        this.lastLng = lat;
    }

    public String getName() {
        return this.name;
    }

    public Double getLastLat() {
        return this.lastLat;
    }

    public Double getLastLng() {
        return this.lastLng;
    }

    public String getId() {
        return this.id;
    }

    public Bitmap getPhoto() {
        return this.photo;
    }

    public void updatePos(double newLat, double newLng){
        setLastLat(newLat);
        setLastLng(newLng);
    }
}
