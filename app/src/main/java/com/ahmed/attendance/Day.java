package com.ahmed.attendance;


import java.util.Date;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;


public class Day extends RealmObject {
    @PrimaryKey
    private String ID;

    private Date date_started;
    private Date date_ended;
    private int num_of_houres;
    private int extra_houres;
    private int color_index;

    public Day() {
    }

    public Day(String ID, Date date_started, Date date_ended, int num_of_houres, int extra_houres, int color_index) {
        this.ID = ID;
        this.date_started = date_started;
        this.date_ended = date_ended;
        this.num_of_houres = num_of_houres;
        this.extra_houres = extra_houres;
        this.color_index = color_index;
    }

    public String getID() {
        return ID;
    }

    public void setID(String ID) {
        this.ID = ID;
    }

    public Date getDate_started() {
        return date_started;
    }

    public void setDate_started(Date date_started) {
        this.date_started = date_started;
    }

    public Date getDate_ended() {
        return date_ended;
    }

    public void setDate_ended(Date date_ended) {
        this.date_ended = date_ended;
    }

    public int getNum_of_houres() {
        return num_of_houres;
    }

    public void setNum_of_houres(int num_of_houres) {
        this.num_of_houres = num_of_houres;
    }

    public int getExtra_houres() {
        return extra_houres;
    }

    public void setExtra_houres(int extra_houres) {
        this.extra_houres = extra_houres;
    }

    public int getColor_index() {
        return color_index;
    }

    public void setColor_index(int color_index) {
        this.color_index = color_index;
    }
}
