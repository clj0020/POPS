package com.madmensoftware.www.pops.Models;

import com.google.android.gms.maps.model.LatLng;

import org.parceler.Parcel;

/**
 * Created by carsonjones on 9/8/16.
 */
@Parcel
public class Job {
    String title;
    String description;
    String category;
    double budget;
    int duration;
    String status;
    String posterUid;
    String posterName;
    String popperUid;
    String popperName;
    String popperNameCache;
    String Uid;
    String parentUid;
    long startTime;
    String notification;
    String popperCache;
    double longitude;
    double latitude;
    long clockInTime;

    public Job() {

    }

    public Job(String title, String description, String posterName, double budget) {
        this.title = title;
        this.description = description;
        this.posterName = posterName;
        this.budget = budget;
    }

    public Job(String title, String description, String category, double budget, int duration, String status, String posterUid, String posterName, String popperUid, double latitude, double longitude, String Uid) {
        this.title = title;
        this.description = description;
        this.category = category;
        this.budget = budget;
        this.duration = duration;
        this.status = status;
        this.posterUid = posterUid;
        this.posterName = posterName;
        this.popperUid = popperUid;
        this.latitude = latitude;
        this.longitude = longitude;
        this.Uid = Uid;
    }

    public String getParentUid() {
        return parentUid;
    }

    public void setParentUid(String parentUid) {
        this.parentUid = parentUid;
    }

    public void setUid(String uidTemp){
        this.Uid = uidTemp;
    }

    public  String getUid()
    {
        return Uid;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getPopperName() {
        return popperName;
    }

    public void setPopperName(String popperName) {
        this.popperName = popperName;
    }

    public String getPopperNameCache() {
        return popperNameCache;
    }

    public void setPopperNameCache(String popperNameCache) {
        this.popperNameCache = popperNameCache;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public double getBudget() {
        return budget;
    }

    public void setBudget(double budget) {
        this.budget = budget;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getPosterUid() {
        return posterUid;
    }

    public void setPosterUid(String posterUid) {
        this.posterUid = posterUid;
    }

    public String getPosterName() {
        return posterName;
    }

    public void setPosterName(String posterName) {
        this.posterName = posterName;
    }

    public String getPopperUid() {
        return popperUid;
    }

    public void setPopperUid(String popperUid) {
        this.popperUid = popperUid;
    }

    public long getStartTime() {
        return startTime;
    }

    public void setStartTime(long startTime) {
        this.startTime = startTime;
    }

    public String getNotification() {
        return notification;
    }

    public void setNotification(String notification) {
        this.notification = notification;
    }

    public String getPopperCache() {
        return popperCache;
    }

    public void setPopperCache(String popperCache) {
        this.popperCache = popperCache;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public long getClockInTime() {
        return clockInTime;
    }

    public void setClockInTime(long clockInTime) {
        this.clockInTime = clockInTime;
    }

}
