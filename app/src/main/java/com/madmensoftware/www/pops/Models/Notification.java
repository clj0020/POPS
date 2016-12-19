package com.madmensoftware.www.pops.Models;

import org.parceler.Parcel;

/**
 * Created by carson on 9/14/2016.
 */
@Parcel

public class Notification {

    public Notification(){
        //required
    }

    String title;
    String popperUid;
    String parentUid;
    String neighborUid;
    String jobUid;
    String Uid;
    String description;
    String type;
    String recieverUid;
    Job job;
    boolean hasViewed;

    public String getUid(){
        return Uid;
    }
    public void setUid(String uid){
        Uid = uid;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }


    public String getRecieverUid() {
        return recieverUid;
    }

    public void setRecieverUid(String recieverUid) {
        this.recieverUid = recieverUid;
    }

    public Job getJob() {
        return job;
    }

    public void setJob(Job job) {
        this.job = job;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }


    public String getPopperUid() {
        return popperUid;
    }

    public void setPopperUid(String popperUid) {
        this.popperUid = popperUid;
    }

    public String getParentUid() {
        return parentUid;
    }

    public void setParentUid(String parentUid) {
        this.parentUid = parentUid;
    }

    public String getNeighborUid() {
        return neighborUid;
    }

    public void setNeighborUid(String neighborUid) {
        this.neighborUid = neighborUid;
    }

    public String getJobUid() {
        return jobUid;
    }

    public void setJobUid(String jobUid) {
        this.jobUid = jobUid;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public boolean getHasViewed() {
        return hasViewed;
    }

    public void setHasViewed(boolean hasViewed) {
        this.hasViewed = hasViewed;
    }



}
