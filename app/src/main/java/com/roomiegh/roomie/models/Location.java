package com.roomiegh.roomie.models;

import java.io.Serializable;

/**
 * Created by anonymous on 11/4/16.
 */

public class Location implements Serializable{
    int id;
    String name, region;
    boolean isHighlighted = false;

    public Location() {
    }

    public Location(int id, String name, String region, boolean isHighlighted) {
        this.id = id;
        this.name = name;
        this.region = region;
        this.isHighlighted = isHighlighted;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getRegion() {
        return region;
    }

    public void setRegion(String region) {
        this.region = region;
    }

    public boolean isHighlighted() {
        return isHighlighted;
    }

    public void setHighlighted(boolean highlighted) {
        isHighlighted = highlighted;
    }
}